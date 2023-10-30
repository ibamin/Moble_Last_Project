# face classfication
detector = dlib.get_frontal_face_detector()
predictor = dlib.shape_predictor("shape_predictor_68_face_landmarks.dat")
face_recog = dlib.face_recognition_model_v1("dlib_face_recognition_resnet_model_v1.dat")


def encode_face(image):
    faces = detector(image, 1)
    if len(faces) == 0:
        return None
    land = predictor(image, faces[0])
    face_descriptor = face_recog.compute_face_descriptor(image, land)
    return face_descriptor


@app.route("/upload", methods=["POST"])
def upload_image():
    response_data = {}
    width, height = 265, 285
    image_path = ""
    user = ""
    date = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    try:
        if "image" in request.files:
            image = request.files["image"]
            # 이미지 파일을 읽어옵니다.
            image_data = image.read()
            nparr = np.fromstring(image_data, np.uint8)
            image = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

            # 그레이스케일 이미지로 변환합니다 (얼굴 감지를 위한 최적화).
            gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

            # 얼굴을 감지합니다.
            faces = face_cascade.detectMultiScale(
                gray, scaleFactor=1.1, minNeighbors=5, minSize=(30, 30)
            )

            # 얼굴이 감지된 경우
            if len(faces) > 0:
                for i, (x, y, w, h) in enumerate(faces):
                    # 얼굴 영역을 잘라냅니다.
                    face_roi = image[y : y + h + 10, x : x + w]

                    # 얼굴을 저장합니다.
                    face_roi = cv2.resize(face_roi, (width, height))
                    cv2.imwrite(f"face.jpg", face_roi)
                    image_path = "/home/ubuntu/face.jpg"
            else:
                # 이미지에 얼굴 사각형 그리기 (테스트용)
                for x, y, w, h in faces:
                    cv2.rectangle(image, (x, y), (x + w, y + h), (0, 255, 0), 3)

                image = cv2.resize(image, (width, height))
                current_time = datetime.now().strftime("%Y%m%d%H%M%S")
                filename = current_time + ".jpg"
                image_path = os.path.join(upload_dir, filename)
                cv2.imwrite(image_path, image)

            image1 = cv2.imread(image_path)
            encoding1 = encode_face(image1)
            # npy location
            descs = np.load(
                "test.npy",
                allow_pickle=True,
            )[()]
            db = mysql.connection.cursor()
            for name, saved_desc in descs.items():
                dist1 = np.linalg.norm([encoding1] - saved_desc, axis=1)  # 유클리디안 거리 값
                # dist2 = np.linalg.norm([encoding2] - saved_desc, axis=1)
                if dist1 < 0.4:  # 거리가 일정 값 이하인 경우 같은 얼굴로 판단
                    print("얼굴 인식됨 (이미지1):", name)
                    print("dist1", dist1)
                    response_data["status"] = "success"
                    db.execute(
                        f"SELECT state FROM inout_check_log WHERE user_id = '{name}'"
                    )
                    result = db.fetchall()
                    print(f"SELECT state FROM inout_check_log WHERE user_id = '{name}'")
                    print(result)
                    if result[0][0] == "입장" or result[0][0] == "":  # 튜플의 첫 번째 열 값 확인
                        db.execute(
                            f"INSERT INTO inout_check_log (inner_time, state, confidence, user_id) VALUES ('{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}', '입장', '{dist1}', '{name}')"
                        )
                    else:
                        db.execute(
                            f"UPDATE inout_check_log SET outter_time = '{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}', state = '퇴장', confidence = '{dist1}' WHERE user_id = '{name}'AND DATE_FORMAT(inner_time, '%Y-%m-%d') = '{date}'"
                        )
                    mysql.connection.commit()

                else:
                    print("미등록된 얼굴 (이미지1)", name)
                    print("dist1", dist1)
                    response_data["status"] = "new face"
                    db.execute(
                        f"INSERT INTO inout_check_log (inner_time, state, confidence, user_id) VALUES ('{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}', 'new_face', '{dist1}', 'new_face')"
                    )
                    mysql.connection.commit()
    except Exception as e:
        response_data["status"] = "error"
        print("얼굴 인식 불가능")
    # JSON 형식으로 응답을 반환
    return jsonify(response_data)
