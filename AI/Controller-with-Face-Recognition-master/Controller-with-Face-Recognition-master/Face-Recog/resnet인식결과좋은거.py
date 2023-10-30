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


# 거리를 비교할 기준값 (임계값) 설정
threshold = 0.4

# 이미지 파일 경로 설정
image1 = cv2.imread("C:/Users/82104/Desktop/team/5.jpg")

# 이미지에서 얼굴 인코딩 계산
encoding1 = encode_face(image1)

# 얼굴 인식된 경우
if encoding1 is not None:
    # 저장된 얼굴 인코딩 불러오기
    descs = np.load(
        "Controller-with-Face-Recognition-master/Controller-with-Face-Recognition-master/Face-Recog/test.npy",
        allow_pickle=True,
    )[()]

    # 유사도를 비교하고 인식 여부 판단
    recognized = False
    for name, saved_desc in descs.items():
        dist1 = np.linalg.norm(np.array(encoding1) - np.array(saved_desc))
        if dist1 < threshold:
            print("얼굴 인식됨 (이미지1):", name)
            print("dist1", dist1)
            recognized = True
            break  # 이미 얼굴 인식됨

    if not recognized:
        print("미등록된 얼굴 (이미지1)")
        print("dist1", dist1)

# 얼굴 인식되지 않은 경우.
if encoding1 is None:
    print("얼굴 인식 불가능 (이미지1)")
