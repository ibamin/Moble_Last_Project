import RPi.GPIO as GPIO
import time
from flask import Flask

app = Flask(__name__)

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)

servo_pin1 = 12
servo_pin2 = 14

# 두 개의 PWM 객체 생성
GPIO.setup(servo_pin1, GPIO.OUT)  # servo_pin1을 출력으로 설정
GPIO.setup(servo_pin2, GPIO.OUT)  # servo_pin2를 출력으로 설정
p1 = GPIO.PWM(servo_pin1, 50)
p2 = GPIO.PWM(servo_pin2, 50)

# 초기 듀티 사이클 설정
p1.start(0)  # 2.5%의 듀티 사이클로 0도 회전
p2.start(0)  # 2.5%의 듀티 사이클로 0도 회전

tf = False

@app.route("/motor/on", methods=["POST"])
def motor_open():
    global tf
    tf = True
    if tf:
        p1.ChangeDutyCycle(7.5)  # 7.5%의 듀티 사이클로 90도 회전
        p2.ChangeDutyCycle(7.5)  # 7.5%의 듀티 사이클로 90도 회전
        time.sleep(3)  # 3초 대기
        p1.ChangeDutyCycle(2.5)  # 2.5%의 듀티 사이클로 0도로 회전
        p2.ChangeDutyCycle(2.5)  # 2.5%의 듀티 사이클로 0도로 회전
        time.sleep(3)  # 3초 대기q
        p1.ChangeDutyCycle(0)  # 모터 정지
        p2.ChangeDutyCycle(0)  # 모터 정지
        print("정상")
        tf = False
        return "성공"


@app.route("/motor/off", methods=["POST"])
def motor_closed():
    global tf
    if not tf:
        p1.ChangeDutyCycle(0)  # 모터 정지
        p2.ChangeDutyCycle(0)  # 모터 정지
        print("오프")
        return "아쉽다"

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=1234)
