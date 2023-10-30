# 학습된모델을 통해 사진들(라즈베리파이,휴대폰)을 각각 비교
# 거리가 0.6미만이면 입출입 가능
import dlib
import cv2
import numpy as np

# dlib 모델 및 얼굴 인코딩을 로드합니다.
detector = dlib.get_frontal_face_detector()
predictor = dlib.shape_predictor(
    "recognition/dlib/shape_predictor_68_face_landmarks.dat"
)
face_recog = dlib.face_recognition_model_v1(
    "recognition/dlib/dlib_face_recognition_resnet_model_v1.dat"
)


# 이미지에서 얼굴 인코딩 계산
def encode_face(image):
    faces = detector(image, 1)
    if len(faces) == 0:
        return None
    land = predictor(image, faces[0])
    face_descriptor = face_recog.compute_face_descriptor(image, land)
    return face_descriptor


# 이미지 파일 경로 설정
image1 = cv2.imread("C:/Users/82104/Desktop/team/c.jpg")

# image2 = cv2.imread("C:/Users/82104/Desktop/team/suji1.jpg")

# 이미지에서 얼굴 인코딩 계산
encoding1 = encode_face(image1)
# encoding2 = encode_face(image2)

# 저장된 얼굴 인코딩 불러오기
descs = np.load(
    "Controller-with-Face-Recognition-master/Controller-with-Face-Recognition-master/Face-Recog/test.npy",
    allow_pickle=True,
)[()]

# 각 이미지와 저장된 얼굴 인코딩을 비교하고 유사도를 확인
for name, saved_desc in descs.items():
    dist1 = np.linalg.norm([encoding1] - saved_desc, axis=1)
    # dist2 = np.linalg.norm([encoding2] - saved_desc, axis=1)
    if dist1 < 0.6:  # 거리가 일정 값 이하인 경우 같은 얼굴로 판단
        print("얼굴 인식됨 (이미지1):", name)
        print("dist1", dist1)
    else:
        print("미등록된 얼굴 (이미지1)", name)
        print("dist1", dist1)
    # if dist2 < 0.6:  # 거리가 일정 값 이하인 경우 같은 얼굴로 판단
    # print("얼굴 인식됨 (이미지2):", name)

# 얼굴 인식되지 않은 경우
if encoding1 is None:
    print("얼굴 인식 불가능 (이미지1)")
# if encoding2 is None:
# print("얼굴 인식 불가능 (이미지2)")
