import dlib
import cv2
import numpy as np
from datetime import datetime

# C:/Users/haneu/fin_pro
detector = dlib.get_frontal_face_detector()
predictor = dlib.shape_predictor(
    "recognition/dlib/shape_predictor_68_face_landmarks.dat"
)
face_recog = dlib.face_recognition_model_v1(
    "recognition/dlib/dlib_face_recognition_resnet_model_v1.dat"
)

# 인코딩이 되어있거 가져옴
# print("20개의 인코딩")
# descs = np.load('C:/Users/haneu/fin_pro/a_project/a_descs.npy', allow_pickle=True)[()]
# descs = np.load('C:/Users/haneu/fin_pro/a_project/a5_descs.npy', allow_pickle=True)[()]
# descs = np.load('C:/Users/haneu/fin_pro/a_project/a10_descs.npy', allow_pickle=True)[()]
descs = np.load(
    "Controller-with-Face-Recognition-master/Controller-with-Face-Recognition-master/Face-Recog/test.npy",
    allow_pickle=True,
)[()]


# 들어온 영상에서 이미지 찾기
def encode_faces(image):
    faces = detector(image, 1)

    if len(faces) == 0:
        return np.empty(0)

    for k, d in enumerate(faces):
        land = predictor(image, d)
        face_decriptor = face_recog.compute_face_descriptor(image, land)

        return np.array(face_decriptor)


frame_width = 640  # Adjust this value as needed
frame_height = 480  # Adjust this value as needed

stream_cap = cv2.VideoCapture(cv2.CAP_DSHOW + 0)
stream_cap.set(3, frame_width)  # Set frame width
stream_cap.set(4, frame_height)  # Set frame height
# Initialize variables for frame skipping
frame_skip = 5  # Skip every 5 frames
frame_count = 0


# show the time
def get_time():
    # tm = time.localtime()
    # tm_p = time.strftime('%Y-%m-%d-%H-%M-%S-%f', tm)
    now = datetime.now()
    tm_p = now.strftime("%Y-%m-%d-%H-%M-%S-%f")
    print("time : ", tm_p)
    return tm_p


# text_w = open('C:/Users/haneu/fin_pro/a_project/a_result.txt', 'w')
# check the score
chs = 0
ch_sum = 0


while True:
    ret, img_bgr = stream_cap.read()
    frame_count += 1

    if not ret:
        break

    if frame_count % frame_skip != 0:
        continue
    # img_bgr = cv2.resize(img_bgr, video_size)
    img_rgb = cv2.cvtColor(img_bgr, cv2.COLOR_BGR2RGB)

    faces = detector(img_rgb, 1)

    for k, d in enumerate(faces):
        # 사각형위치 찾아놓기
        rect = ((d.left(), d.top()), (d.right(), d.bottom()))

        land = predictor(img_rgb, d)
        face_descriptor = face_recog.compute_face_descriptor(img_rgb, land)

        last_found = {"name": "unknown", "dist": 0.6, "color": (0, 0, 255)}  # 0.6? 0.5?

        for name, saved_desc in descs.items():
            dist = np.linalg.norm([face_descriptor] - saved_desc, axis=1)

            if dist < last_found["dist"]:
                last_found = {"name": name, "dist": dist, "color": (255, 255, 255)}
                tm_p = get_time()  # 인식될때 시간을 찍음
                print("오차 : ", dist)
                # print('name :  ' + last_found['name']+' time : ' + tm_p+' dist : ' + dist)

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

        tm_p = get_time()
        print("name :  ", last_found["name"])
        print("오차 : ", dist)

        # text_w.write(last_found['name'] +" " + tm_p + '/n')

        if chs == 0:
            first_name = last_found["name"]

        if ch_sum < 3:
            if first_name == last_found["name"]:
                ch_sum += 1
                chs += 1
            else:
                chs = 0
                ch_sum = 0

        else:
            if first_name == last_found["name"]:
                print("find check user")
                print("name :  ", last_found["name"])
                tm_p = get_time()

            else:  # unkown
                print("find unuser/n")

            chs = 0
            ch_sum = 0

    # writer.write(img_bgr)

    cv2.imshow("img", img_bgr)
    if cv2.waitKey(1) == ord("q"):
        break

# text_w.close()
stream_cap.release()
cv2.destroyAllWindows()
# writer.release()
