import dlib
import cv2
import numpy as np

# 이미지 비교를 위해서 그래프 그리기 위한 module
import matplotlib.pyplot as plt
import matplotlib.patches as patches
import matplotlib.patheffects as path_effects

detector = dlib.get_frontal_face_detector()  # 얼굴 감지 모델
predictor = dlib.shape_predictor(
    "recognition/dlib/shape_predictor_68_face_landmarks.dat"  # 얼굴 랜드마크생성을 위한 모델
)
face_recog = dlib.face_recognition_model_v1(  # ResNet 아키텍처를 기반으로 하는 얼굴 인식 모델
    "recognition/dlib/dlib_face_recognition_resnet_model_v1.dat"
)


# dect & predic(landmarks)
# 얼굴의 위치 찾아보기
# 이 함수는 입력 이미지를 받아 (dlib의 얼굴 감지기)를 사용하여 얼굴 감지를 수행합니다.
# 이미지에서 얼굴의 위치를 직사각형()으로 반환하고 얼굴의 랜드마크를 numpy 배열()로 반환합니다.
# 랜드마크는 주요 얼굴 포인트(예: 눈, 코, 입)를 나타냅니다
def find_faces(
    image,
):
    faces = detector(image, 1)

    if len(faces) == 0:
        return np.empty(0)

    # rets는 사각형 처리, lands는 68개의 landmarks
    rects, lands = [], []
    lands_np = np.zeros((len(faces), 68, 2), dtype=int)

    for k, d in enumerate(faces):
        # 사각형위치 찾아놓기
        rect = ((d.left(), d.top()), (d.right(), d.bottom()))
        rects.append(rect)

        # landmark
        land = predictor(image, d)
        lands.append(land)

        # convert dlib shape to numpy array
        for i in range(0, 68):
            lands_np[k][i] = (land.part(i).x, land.part(i).y)

    return rects, lands, lands_np


# use resnet
# 이 함수는 모델을 사용하여 입력 이미지에서 감지된 얼굴에 대한 얼굴 설명자를 계산합니다.
# 얼굴 설명자는 각 개인에게 고유한 얼굴 특징을 숫자로 표현한 것입니다.
def encode_faces(image, lands):
    face_descriptors = []
    for land in lands:
        face_descriptor = face_recog.compute_face_descriptor(image, land)
        face_descriptors.append(np.array(face_descriptor))

    return np.array(face_descriptors)


image_pathe = "C:/Users/82104/Desktop/ResNet/"
image_paths = {
    "Jo": image_pathe + "Jo/",
    # "No": image_pathe + "No/",
    "Park": image_pathe + "Park/",
    "qwer": image_pathe + "qwer/",
    "You": image_pathe + "You/",
}

img_num = 30
img_type = ".jpg"

# image result
descs = {
    "Jo": None,
    # "No": None,
    "Park": None,
    "qwer": None,
    "You": None,
}


# 이미지에서 얼굴 설명자 추출
# 스크립트는 각 디렉터리(사람)를 반복하고 처리를 위해 개별 이미지를 로드합니다.
# find_faces - > 이기능을 사용하여 이미지에서 얼굴과 랜드마크를 감지
# encode_faces -> 이 함수를 사용하여 감지된 각 얼굴에 대한 얼굴 설명자를 계산
# descs -> 얼굴 설명자를 해당 사람의 이름을 키로 사용하여 사전에 저장

# 이 부분이 Resnet모델을 사용하여 사람들 사진의 얼굴 설명자를 계산하여  descs 사전에 저장
for name, image_path in image_paths.items():
    i = 0
    print(name)
    for idx in range(img_num):
        img_p = image_path + str(idx + 1) + img_type
        # print(img_p)
        img_bgr = cv2.imread(img_p)
        img_rgb = cv2.cvtColor(img_bgr, cv2.COLOR_BGR2RGB)

        _, img_lands, _ = find_faces(img_rgb)
        descs[name] = encode_faces(img_rgb, img_lands)[i]

        print(descs[name][i])
    i += 1


# 인코딩파일 저장
# 스크립트는 각 사람에 대해 계산된 얼굴 설명자가 포함된 사전을 사용하여  이진 파일에 저장합니다.
# 이 파일은 향후 인식 작업에 사용할 수 있습니다.
np.save(
    r"Controller-with-Face-Recognition-master/Controller-with-Face-Recognition-master/Face-Recog/test.npy",
    descs,
)
print(descs)

# 이미지 삽입해서 확인하기
# 스크립트는 인식해야 하는 입력 이미지를 로드
# finde_faces,encode_faces -> 함수를 사용하여 입력 이미지에서 얼굴을 감지하고 설명자를 계산
img_bgr = cv2.imread(r"C:/Users/82104/Desktop/team/5.jpg")
img_rgb = cv2.cvtColor(img_bgr, cv2.COLOR_BGR2RGB)
rects, img_lands, _ = find_faces(img_rgb)
descriptors = encode_faces(img_rgb, img_lands)


# Visualize output
# 이미지를 표시하기 위한 matplotlib 그림과 축을 만드는데 사용함
fig, ax = plt.subplots(1, figsize=(20, 20))
ax.imshow(img_rgb)

# 그런다음 이러한 설명자를 각 사람에 대해 저장된 설명자와 비교함
# 일치하는 항목이 발견되면(유클리드 거리 기준) 사람의 이름이 이미지에 표시됨
# 일치하는 항목이 없으면 알수 없음
for i, desc in enumerate(descriptors):
    found = False
    for name, saved_desc in descs.items():
        dist = np.linalg.norm([desc] - saved_desc, axis=1)  # 유클리디안 거리

        # 그래프에서 원본과의 거리차의가 0.6미만이라면 얼굴인식확정
        if dist < 0.6:
            found = True

            # 이름을 출력해준다.
            text = ax.text(
                rects[i][0][0],
                rects[i][0][1],
                name,
                color="b",
                fontsize=40,
                fontweight="bold",
            )
            text.set_path_effects(
                [
                    path_effects.Stroke(linewidth=10, foreground="white"),
                    path_effects.Normal(),
                ]
            )
            rect = patches.Rectangle(
                rects[i][0],
                rects[i][1][1] - rects[i][0][1],
                rects[i][1][0] - rects[i][0][0],
                linewidth=2,
                edgecolor="w",
                facecolor="none",
            )
            ax.add_patch(rect)
            print(dist)
            print(name)

            break

    if not found:
        ax.text(
            rects[i][0][0],
            rects[i][0][1],
            "unknown",
            color="r",
            fontsize=20,
            fontweight="bold",
        )
        rect = patches.Rectangle(
            rects[i][0],
            rects[i][1][1] - rects[i][0][1],
            rects[i][1][0] - rects[i][0][0],
            linewidth=2,
            edgecolor="r",
            facecolor="none",
        )
        ax.add_patch(rect)
        print(dist)

plt.axis("off")
# plt.savefig('C:/Users/haneu/a_project/output_re1.jpg') #사각형과 이름이 적혀진 비교파일을 새로저장.
plt.show()
