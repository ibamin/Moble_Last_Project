# ResNet 아키텍처를 기반으로 입력이미지에서 감지된 얼굴에 대한 얼굴 설명자를 계산
# 얼굴 설명자는 각 개인에게 고유한 얼굴 특징을 숫자로 표현한 것
def encode_face(image):
    faces = detector(image, 1)
    if len(faces) == 0:
        return None
    land = predictor(image, faces[0])
    face_descriptor = face_recog.compute_face_descriptor(image, land)
    return face_descriptor


# 입출입 Raspberry PI 카메라와 연동되는 부분
@app.route("/upload", methods=["POST"])
def upload_image():
    response_data = {}
    width, height = 265, 285
    image_path = ""
    user = ""
    date = datetime.now()
    year = date.year
    month = date.month
    day = date.day
    try:
        if "image" in request.files:
            image = request.files["image"]
            image_data = image.read()  # 이미지 파일을 읽어옵니다.
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

            image1 = cv2.imread(image_path)  # 이미지 파일 경로 설정
            encoding1 = encode_face(image1)  # 이미지에서 얼굴 인코딩 계산

            # 저장된 얼굴 인코딩 불러오기
            descs = np.load(
                "test.npy",
                allow_pickle=True,
            )[()]
            db = mysql.connection.cursor()

            # 각 이미지와 저장된 얼굴 인코딩을 비교하고 유사도 확인
            # 이미지에서 인코딩된 설명자와 저장된 인코딩 설명자비교(유클리디안거리 사용)
            for name, saved_desc in descs.items():
                dist1 = np.linalg.norm([encoding1] - saved_desc, axis=1)  # 유클리디안 거리
                if dist1 < 0.4:  # 거리가 일정 값 미만 인 경우 같은 얼굴로 판단
                    print("얼굴 인식됨 (이미지1):", name)
                    print("dist1", dist1)
                    response_data["status"] = "success"
                    db.execute(
                        f"SELECT state,inner_time FROM inout_check_log WHERE user_id = '{name}'AND YEAR(inner_time)={year} AND MONTH(inner_time)={month} AND DAY(inner_time)={day}"
                    )
                    result = db.fetchall()
                    print(
                        f"SELECT state FROM inout_check_log WHERE user_id = '{name}'AND YEAR(inner_time)={year} AND MONTH(inner_time)={month} AND DAY(inner_time)={day}"
                    )
                    print(result)

                    last_state = ""

                    if not result:
                        last_state = ""  # result가 비어 있다면 초기화
                    else:
                        last_state = result[-1][0]

                    if last_state == "" or last_state == "퇴장":
                        db.execute(
                            f"INSERT INTO inout_check_log (inner_time, state, confidence, user_id) VALUES ('{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}', '입장', '{dist1}', '{name}')"
                        )
                        response_data["state"] = "user_in"
                    else:
                        db.execute(
                            f"UPDATE inout_check_log SET outter_time = '{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}', state = '퇴장', confidence = '{dist1}' WHERE user_id = '{name}'AND inner_time='{result[-1][1]}'"
                        )
                        response_data["state"] = "user_out"
                    mysql.connection.commit()
                    # break
                elif dist1 > 0.4:  # 거리가 일정 값 초과인 경우 미등록된 얼굴로 판단
                    print("미등록된 얼굴 (이미지1)", name)
                    print("dist1", dist1)
                    response_data["status"] = "new face"
                    db.execute(
                        f"INSERT INTO inout_check_log (inner_time, state, confidence, user_id) VALUES ('{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}', 'new_face', '{dist1}', 'new_face')"
                    )
                    mysql.connection.commit()
    # 얼굴 인식되지 않은 경우
    except Exception as e:
        response_data["status"] = "error"
        print("얼굴 인식 불가능")
    # JSON 형식으로 응답을 반환
    print(response_data)
    return jsonify(response_data)
