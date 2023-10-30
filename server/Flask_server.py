from flask import Flask,request,send_file,jsonify,render_template
import os
import uuid
import cv2
import json
from flask import Flask
from flask_mysqldb import MySQL
from twilio.rest import Client
import random #휴대폰 인증번호 만들기위한 모듈
import numpy as np #얼굴 인식을위한 모듈
from datetime import datetime
import pytesseract
from PIL import Image
import sys
#AI import 
from recognition.test import active
import dlib
import hashlib
from dotenv import load_dotenv


app = Flask(__name__)

load_dotenv("ffn.env")

app.config['MYSQL_HOST'] = os.getenv("Server_ip")
app.config['MYSQL_USER'] = os.getenv("Access_user_id")
app.config['MYSQL_PASSWORD'] = os.getenv("Access_user_pw")
app.config['MYSQL_DB'] = os.getenv("DB_name")

mysql = MySQL(app)

#face selector
face_cascade = cv2.CascadeClassifier(os.getenv("face_cascader"))

#face classfication
detector = dlib.get_frontal_face_detector()
predictor = dlib.shape_predictor(os.getenv("dlib_cascader"))
face_recog = dlib.face_recognition_model_v1(os.getenv("dlib_face_recog"))
def encode_face(image):
    faces = detector(image, 1)
    if len(faces) == 0:
        return None
    land = predictor(image, faces[0])
    face_descriptor = face_recog.compute_face_descriptor(image, land)
    return face_descriptor

#img to raspberry camera
upload_dir = os.getenv("raspberry_camera_img_reg")
if not os.path.exists(upload_dir):
    os.makedirs(upload_dir)

#sign_up img 
sign_dir = os.getenv("sign_up_img_reg")
if not os.path.exists(sign_dir):
    os.makedirs(sign_dir)

#user_face_file's init (test)
image_dir = sign_dir
image_files = os.listdir(image_dir)
users_face = [os.path.join(image_dir, file) for file in image_files if file.endswith('.jpg')]

#security
md5_hash = hashlib.md5()

print(users_face)

# ResNet 아키텍처를 기반으로 입력이미지에서 감지된 얼굴에 대한 얼굴 설명자를 계산
# 얼굴 설명자는 각 개인에게 고유한 얼굴 특징을 숫자로 표현한 것
def encode_face(image):
    faces = detector(image, 1)
    if len(faces) == 0:
        return None
    land = predictor(image, faces[0])
    face_descriptor = face_recog.compute_face_descriptor(image, land)
    return face_descriptor

@app.route('/')
def index():
    return render_template("index.html")

#데이터베이스 테스트
@app.route('/dbtest')
def db():
    try:
        db = mysql.connection.cursor()
        db.execute('SHOW TABLES')
        data = db.fetchall()
        db.close()
        return f"Database connection successful. Tables: {data}"
    except Exception as e:
        return f"Database connection failed."

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
                # 이미지에 얼굴 사각형 그리기
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
                    break
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

#로그인
@app.route('/login', methods=['POST', 'GET'])
def login():
    try:
        response_data = {}
        md5_hash = hashlib.md5()
        db = mysql.connection.cursor()
        id = request.args.get("id")
        pw = request.args.get("pw")
        #이하 md5 hash함수로 암호화
        pw = pw.encode('UTF-8')
        md5_hash.update(pw)
        pw = md5_hash.hexdigest()
        print(pw)
        db.execute(f"select user_id, user_pass, user_group, user_name, phone_num from user_list where user_id = '{id}' and user_pass = '{pw}'")
        result = db.fetchall()
        print(f"select user_id, user_pass, user_group, user_name, phone_num from user_list where user_id = '{id}' and user_pass = '{pw}'")
        print(result)
        if len(result) > 0:
            user_id, user_pass, user_group, user_name, phone_num = result[0]  # Retrieve user_name and phone_num
            response_data['status'] = "success"
            response_data['user_id'] = user_id
            response_data['user_pass'] = user_pass
            response_data['user_group'] = user_group
            response_data['user_name'] = user_name  # Add user_name to the response_data
            response_data['phone_num'] = phone_num  # Add phone_num to the response_data
        else:
            response_data['status'] = "Fail"
        db.close()
        return jsonify(response_data)
    except Exception as e:
        return str(e)

#신분증 인식 ocr
@app.route('/ocr_test',methods=["POST","GET"])
def ocr():
    response_data={}
    image=""
    if 'image' in request.files: #이미지 체크
        image=Image.open(request.files['image'])
        image=np.array(image)
        gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY) #이미지 정확도를 위한 grayscale
        image = cv2.rotate(image,cv2.ROTATE_90_COUNTERCLOCKWISE) #이미지 정확도를 위한 90' 로테이션을 활용한 이미지 확대
        cv2.imwrite("ncard.jpg",image)
        print("ocr_image in")
    else:
        response_data['status']="fail"
        print("ocr_image load fail")
        return response_data
    response_data['result']=pytesseract.image_to_string(image,lang='kor') #pytesseract를 통한 한국어 ocr작업
    response_data['status']="success"
    print(response_data['status'])
    print(response_data['result'])
    return response_data

#회원가입
@app.route('/Sign_up',methods=["POST"])
def Sign_in():
    try:
        response_data={}
        db=mysql.connection.cursor()
        md5_hash = hashlib.md5()
        id = request.form.get('id')
        pw = request.form.get('pw')
        #이하 md5 해시화
        pw = pw.encode('UTF-8')
        md5_hash.update(pw)
        pw=md5_hash.hexdigest()
        u_name = request.form.get('name')
        jumin = request.form.get('jumin')
        gender=0
        if request.form.get('gender')=="남자":
            gender=1
        phone = request.form.get('phone_number')
        sign_time = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        face_img_path = os.getenv("sign_up_img_reg")+'/'+id+".jpg"
        
        print(face_img_path)
        if 'image' in request.files:
            print("이미지 체크")
            image = request.files['image']
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
                print("얼굴 감지 및 저장")
                for i, (x, y, w, h) in enumerate(faces):
                    # 얼굴 영역을 잘라냅니다.
                    face_roi = image[y : y + h+10, x : x + w]
                    # 얼굴을 저장합니다.
                    cv2.imwrite(face_img_path, face_roi)
        db = mysql.connection.cursor()
        try:
            print(f"INSERT INTO user_list (user_id, user_name, user_jumin, sex, user_pass, phone_num, sign_up_date, face_img_path) VALUES ('{id}', '{u_name}', '{jumin}', '{gender}', '{pw}', '{phone}', '{sign_time}', '{face_img_path}')")
            db.execute(f"INSERT INTO user_list (user_id, user_name, user_jumin, sex, user_pass, phone_num, sign_up_date, face_img_path) VALUES ('{id}', '{u_name}', '{jumin}', '{gender}', '{pw}', '{phone}', '{sign_time}', '{face_img_path}')")
            result=mysql.connection.commit()
            print(result)
            response_data['status']='success'
        except Exception as e:
            mysql.connection.rollback()  # 롤백
            response_data['status']='error'
        finally:
            db.close()
            return response_data
    except Exception as e:
        return str(e)

#사용자 개인 출입기록 확인
@app.route("/log",methods=["GET","POST"])
def send_log():
    
    id = request.form.get("id")
    date = request.form.get("date")
    db = mysql.connection.cursor()
    db.execute(f"SELECT inner_time, outter_time, state FROM inout_check_log WHERE user_id = '{id}' AND DATE_FORMAT(inner_time, '%Y-%m-%d') = '{date}'")
    result = db.fetchall()
    print(id)
    print(date)
    print(result)
    response_data = {}
    if len(result) >0:
        logs = []
        for row in result:
            inner_time, outter_time, state = row
            log_entry = {
                'inner_time': inner_time,
                'outter_time': outter_time,
                'state': state,
            }
            logs.append(log_entry)
        response_data['status'] = 'success'
        response_data['logs'] = logs
    else:
        response_data['status']='error'
        
    print(response_data)
    return response_data

#ID 중복 테스트
@app.route("/check", methods=["GET","POST"])
def same_id_check():
    response_data = {}
    
    id = request.form.get("id")
    db = mysql.connection.cursor()
    print(id)
    try:
        db.execute(f"SELECT * FROM user_list WHERE user_id = '{id}'")
        result = db.fetchall()
        if len(result) > 0:
            response_data['status'] = 'already in'
        else:
            response_data['status'] = 'can_use'
    except Exception as e:
        response_data['status'] = 'error'
    finally:
        return jsonify(response_data)

#개인정보 수정
@app.route("/alter", methods=["GET","POST"])
def alter_user():
    response_data = {}
    
    old_id = request.form.get('old_id')
    new_id = request.form.get('id')
    pw = request.form.get('pw')
    pw = pw.encode('UTF-8')
    md5_hash.update(pw)
    pw=md5_hash.hexdigest()
    name = request.form.get('name')
    group = request.form.get('group')
    phone = request.form.get('pnum')
    
    try:
        db = mysql.connection.cursor()
        db.execute(f"UPDATE user_list SET user_id = '{new_id}', user_pass = '{pw}', user_name = '{name}', user_group = '{group}', phone_num = '{phone}' WHERE user_id = '{old_id}'")
        mysql.connection.commit()
        response_data['status'] = 'success'
    except Exception as e:
        response_data['status'] = 'error'
    finally:
        return jsonify(response_data)

#관리자가 모든 로그를 체크
@app.route("/all_log", methods=["POST"])
def all_log_send():
    response_data = {}
    date_str = request.form.get("date")
    date = datetime.strptime(date_str, "%Y-%m-%d")
    year = date.year
    month = date.month
    day = date.day
    db = mysql.connection.cursor()
    print(date)
    print(year)
    print(month)
    print(day)
    try:
        db.execute(f"SELECT inner_time, outter_time, state, confidence, user_id FROM inout_check_log WHERE YEAR(inner_time)={year} AND MONTH(inner_time)={month} AND DAY(inner_time)={day} AND user_id != 'new_face'")
        result = db.fetchall()
        print(result)
        if len(result) > 0:
            logs = []
            for row in result:
                inner_time, outter_time, state, confidence, user_id = row
                log_entry = {
                    'inner_time': inner_time,
                    'outter_time': outter_time,
                    'state': state,
                    'confidence': confidence,
                    'user_id': user_id
                }
                logs.append(log_entry)
            response_data['status'] = 'success'
            response_data['logs'] = logs
        else:
            response_data['status'] = 'no_logs'
    except Exception as e:
        response_data['status'] = 'error'
    print(response_data)
    return jsonify(response_data)
        
if __name__ == "__main__":
    app.run(host="0.0.0.0",port=5000)
