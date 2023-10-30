import requests# http 요청을 보내기 위한 라이브러리
import cv2 #비디오 캡처 및 이미지 처리 위한 라이브러리
import RPi.GPIO as GPIO#라즈베리파이 GPIO 핀 제어 라이브러리
import time#시간 및 딜레이 관리를 위한 라이브러리
import threading  # 스레딩 모듈 추가
import dlib

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)

# 카메라 캡처 객체 생성
cap = cv2.VideoCapture(0)
cap.set(cv2.CAP_PROP_FPS, 10)  # 30fps로 설정
cap.set(3, 480)  # 가로 해상도 설정
cap.set(4, 640)  # 세로 해상도 설정
cap.set(5, 30)   # 프레임 속도를 초당 30프레임으로 설정

# 클라이언트 측 정보
client_ip = '192.168.0.199'
client_port = 12345

# 라즈베리 파이 2 측 정보
raspberry_pi_2_ip = '192.168.0.224'
raspberry_pi_2_port = 1234
data = ""

# 외부 서버 정보(aws)
server_ip = '15.164.120.162'
server_port = 5000

TRIG = 23
ECHO = 24

print("Distance measurement in progress")
GPIO.setup(TRIG, GPIO.OUT)
GPIO.setup(ECHO, GPIO.IN)
GPIO.setup(18, GPIO.OUT)# 부저 

p = GPIO.PWM(18, 1)

Frq = [262, 294, 330, 349, 392, 440, 493, 523]
print("Waiting for sensor to settle")
GPIO.output(TRIG, False)
time.sleep(2)

tf = True
detector = dlib.get_frontal_face_detector() #전면얼굴 dlib 
status = None

# 이미지 전송 스레드 함수 정의
def send_image():
    global tf, status, new_face_count
    new_face_count = 0

    while True:
        ret, frame = cap.read()

        if not ret:
            print("이미지 캡처 실패")
            continue  # 또는 다른 처리를 수행하세요

        # faces = detector(frame)
        # 이미지를 바이너리로 인코딩
        _, img_encoded = cv2.imencode('.jpg', frame)#imencode 함수는 bool값과 ,이미지를 jpeg형식으로 인코딩(넘파이배열)
        img_bytes = img_encoded.tobytes()#넘파이 배열을 tobytes 함수를사용해 binary 데이터로 변환
                                         #바이너리 데이터로 이미지를 저장하면 이미지를 이진형식으로 전송 및 저장이 가능하며 다르시스템간 이미지를 교환하는데 사용할수 있습니다.   
        # 초음파 센서로 거리 측정
        GPIO.output(TRIG, True)#초음파 센서 초음파 발생 TRIG 핀 HIGH(1) 
        time.sleep(0.00001)# 초음파 발생 시킨후 잠시대기
        GPIO.output(TRIG, False)#초음파 센서 OFF 


        while GPIO.input(ECHO) == 0: #ECHO 핀이 LOW 상태인 동안,초음파가 반사되어 오고있다
            start = time.time()#ECHO 핀이 HIGH 로 전환된 시간을 기록(초음파 발사된 시간)
        while GPIO.input(ECHO) == 1:#ECHO 핀이 HIGH 상태인 동안 초음파 반사 계속해서 감시
            stop = time.time()#ECHO 핀이 LOW로 전환된 시간 기록(초음파 반사 시간)

        check_time = stop - start # 초음파 센서로 측정된 신호의 왕복 시간 계산
        distance = check_time * 34300 / 2 #거리 계산 코드
        print("Distance : %.1f cm" % distance)
        time.sleep(0.4)

        # 거리가 10cm 미만이면 이미지 전송
        if distance < 100:
            url = f'http://{server_ip}:{server_port}/upload'  #aws 서버 url
            response = requests.post(url, files={'image': ('image.jpg', img_bytes)}) # 사진을 찍어서 전송

            response_data = response.json()# 서버로부터 응답을 json 형식으로 읽어들여옴
            print(response_data)
            status = response_data.get('status', None)#입출입 확인 하려고 
            state = response_data.get('state',None)#success or new face

            if status == 'new face':
                new_face_count += 1
            else:
                new_face_count = 0

            if new_face_count >= 3: # 부저 울리는 조건 
                p.start(50)
                try:
                    for fr in Frq:
                        p.ChangeFrequency(fr)
                        time.sleep(0.5)
                    time.sleep(4)  # 5초 동안 부저 울림
                except KeyboardInterrupt:
                    pass
                p.stop()
                print("부저 울림")
                new_face_count = 0

            if (state == 'user_in' or state == 'user_out') and tf == True:
                url = f'http://{raspberry_pi_2_ip}:{raspberry_pi_2_port}/motor/on'# flask motor on 상태
                response = requests.post(url)
                status = ''
                tf = False

        if tf == False:
            url = f'http://{raspberry_pi_2_ip}:{raspberry_pi_2_port}/motor/off' #flask motor off 상태
            response = requests.post(url)
            tf = True

# 이미지 전송 스레드 시작
image_thread = threading.Thread(target=send_image)#이미지 전송 및 거리 측정을 백그라운드에서 처리
image_thread.daemon = True #스레드를 백그라운드 스레드로 설정. 메인스레드 종료될때 image.thread도 함께 종료

dlib.DLIB_USE_CUDA = True  # CUDA 지원을 사용하려면 활성화하세요 (선택 사항)
face_detector = dlib.get_frontal_face_detector()#dlib.라이브러리에서 얼굴 감지기 객체 생성
image_thread.start()#이미지 전송 스레드 시작
scaler = 0.7

# 메인 스레드에서 화면 표시
while True:
    ret, frame = cap.read()
    frame = cv2.flip(frame, 1) #좌우 반전

    if ret:
        faces = face_detector(frame)
        for face in faces:
            x, y, w, h = face.left(), face.top(), face.width(), face.height()
            cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)

            if status == 'success':
                cv2.putText(frame, "OPEN DOOR", (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 255, 0), 2)
            elif status == 'new face':
                cv2.putText(frame, "FAILED", (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2)

        cv2.imshow('Camera Feed', frame)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# 리소스 해제 및 창 닫기
cap.release()
cv2.destroyAllWindows()
