import dlib
import cv2
import numpy as np
import time
import socket

import ftplib
import os

# socket_client.ver
# HOST = '172.30.1.40'
HOST = "172.29.118.157"
PORT = 8088

client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client_socket.connect((HOST, PORT))

# txt
# open_t = open('C:/Bitnami/wampstack-7.4.6-1/apache2/htdocs/unknown_img/open_name.txt', 'a')
# unknown_t = open('C:/Bitnami/wampstack-7.4.6-1/apache2/htdocs/unknown_img/unknown_name.txt', 'a')

# ftp 사진
ftp = ftplib.FTP()
ftp.connect("112.175.184.82", 21)
ftp.login("sormdi11", "tidlsl1254!")

# ftp unknown
ftpun = ftplib.FTP()
ftpun.connect("112.175.184.82", 21)
ftpun.login("sormdi11", "tidlsl1254!")

# ftp open
ftpop = ftplib.FTP()
ftpop.connect("112.175.184.82", 21)
ftpop.login("sormdi11", "tidlsl1254!")

detector = dlib.get_frontal_face_detector()
predictor = dlib.shape_predictor(
    "C:/Users/haneu/fin_pro/modle/shape_predictor_68_face_landmarks.dat"
)
face_recog = dlib.face_recognition_model_v1(
    "C:/Users/haneu/fin_pro/modle/dlib_face_recognition_resnet_model_v1.dat"
)

# 인코딩이 되어있거 가져옴
# print("20개의 인코딩")
# descs = np.load('C:/Users/haneu/fin_pro/a_project/a5_descs.npy', allow_pickle=True)[()]
# descs = np.load('C:/Users/haneu/fin_pro/a_project/a10_descs.npy', allow_pickle=True)[()]
descs = np.load("C:/Users/haneu/fin_pro/a_project/a20_descs.npy", allow_pickle=True)[()]


# 들어온 영상에서 이미지 찾기
def encode_faces(image):
    faces = detector(image, 1)

    if len(faces) == 0:
        return np.empty(0)

    for k, d in enumerate(faces):
        land = predictor(image, d)
        face_decriptor = face_recog.compute_face_descriptor(image, land)

        return np.array(face_decriptor)


# 실시간으로 해보자
stream_cap = cv2.VideoCapture("http://172.29.119.7:8090/?action=stream")
time.sleep(0.1)
# stream_cap = cv2.VideoCapture(0)


# show the time
def get_time():
    tm = time.localtime()
    tm_p = time.strftime("%Y-%m-%d-%H-%M-%S-%p", tm)
    print(tm_p)
    return tm_p


# check the image + 저장name
checks = 0
check_sum = 0
save_ph = ""


while True:
    ret, img_bgr = stream_cap.read()

    if not ret:
        break

    img_rgb = cv2.cvtColor(img_bgr, cv2.COLOR_BGR2RGB)

    faces = detector(img_rgb, 1)

    for k, d in enumerate(faces):
        # 사각형위치 찾아놓기
        rect = ((d.left(), d.top()), (d.right(), d.bottom()))

        land = predictor(img_rgb, d)
        face_descriptor = face_recog.compute_face_descriptor(img_rgb, land)

        last_found = {"name": "unknown", "dist": 0.6, "color": (0, 0, 255)}
        tm_p = get_time()

        for name, saved_desc in descs.items():
            dist = np.linalg.norm([face_descriptor] - saved_desc, axis=1)

            if dist < last_found["dist"]:
                last_found = {"name": name, "dist": dist, "color": (255, 255, 255)}
                # tm_p = get_time() #인식될때 시간을 찍음
                # print(dist)

        cv2.rectangle(
            img_bgr,
            pt1=(d.left(), d.top()),
            pt2=(d.right(), d.bottom()),
            color=last_found["color"],
            thickness=2,
        )
        cv2.putText(
            img_bgr,
            last_found["name"],
            org=(d.left(), d.top()),
            fontFace=cv2.FONT_HERSHEY_SIMPLEX,
            fontScale=1,
            color=last_found["color"],
            thickness=2,
        )
        # print('  :  ' + last_found['name'])
        tm_p = get_time()
        print("name :  ", last_found["name"])
        print("오차 : ", dist)

        # 연속으로 동작 확인
        if checks == 0:
            first_name = last_found["name"]

        if check_sum < 4:
            if first_name == last_found["name"]:
                check_sum += 1
                checks += 1
            else:
                checks = 0
                check_sum = 0

        else:
            # 연속적으로 5번 동작확인

            # 모르는 사람이면 사진저장
            if first_name == "unknown":
                tm_list = tm_p.split("-")
                tmtm = (
                    tm_list[0]
                    + "_"
                    + tm_list[1]
                    + "_"
                    + tm_list[2]
                    + "_"
                    + tm_list[3]
                    + "_"
                    + tm_list[4]
                    + "_"
                    + tm_list[5]
                )

                unknown_t = open(
                    "C:/Bitnami/wampstack-7.4.6-1/apache2/htdocs/unknown_img/unknown_name.txt",
                    "a",
                )
                unknown_info = first_name + "_" + tmtm
                unknown_t.write(unknown_info + "\n")
                unknown_t.close()

                s_path = (
                    "C:/Bitnami/wampstack-7.4.6-1/apache2/htdocs/unknown_img/"
                    + first_name
                    + "_"
                    + tmtm
                    + ".jpg"
                )
                cv2.imwrite(s_path, img_bgr)

                filename = first_name + "_" + tmtm + ".jpg"
                ftp.cwd("/html/unknown_img/")
                os.chdir(r"C:/Bitnami/wampstack-7.4.6-1/apache2/htdocs/unknown_img")
                myfile = open(filename, "rb")
                ftp.storbinary("STOR " + filename, myfile)
                myfile.close()

                textname = "unknown_name.txt"
                ftpun.cwd("/html/unknown_img/")
                os.chdir(r"C:/Bitnami/wampstack-7.4.6-1/apache2/htdocs/unknown_img")
                myfileun = open(textname, "rb")
                ftpun.storbinary("STOR unknown_name.txt", myfileun)
                myfileun.close()

                print("cap")
            # 다섯번 얼굴 인식 되면 이름_시간해서 php 보내고 제어기에 통신
            else:
                tm_list = tm_p.split("-")
                tmtm = (
                    tm_list[0]
                    + "_"
                    + tm_list[1]
                    + "_"
                    + tm_list[2]
                    + "_"
                    + tm_list[3]
                    + "_"
                    + tm_list[4]
                    + "_"
                    + tm_list[5]
                )

                open_t = open(
                    "C:/Bitnami/wampstack-7.4.6-1/apache2/htdocs/unknown_img/open_name.txt",
                    "a",
                )
                open_info = first_name + "_" + tmtm
                print(open_info)
                open_t.write(open_info + "\n")
                open_t.close()

                textname = "open_name"
                print(textname)
                ftpop.cwd("/html/unknown_img/")
                os.chdir(r"C:/Bitnami/wampstack-7.4.6-1/apache2/htdocs/unknown_img")
                myfileop = open(textname + ".txt", "rb")
                ftpop.storbinary("STOR open_name.txt", myfileop)
                myfileop.close()

                # 소켓 통신으로 메세지 보내기
                # 메시지를 전송합니다.
                s_data = first_name
                # client_socket.sendall(s_data.encode()) #개인
                client_socket.send(s_data.encode())  # 쓰레드

                # 메시지를 수신합니다.
                r_data = client_socket.recv(1024)
                print("Received", repr(r_data.decode()))

            # 연속동작되는거 reset
            checks = 0
            check_sum = 0

    cv2.imshow("face_recognation", img_bgr)
    if cv2.waitKey(1) == ord("q"):
        break


stream_cap.release()
cv2.destroyAllWindows()


ftp.close()
ftpun.close()
ftpop.close()

client_socket.close()
