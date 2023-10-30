from keras.models import load_model
import cv2
import numpy as np

facial_recog_dict = {
    "[0]": "AhnHyunBeom",
    "[1]": "ChoGuesung",
    "[2]": "CristianoRonaldo",
    "[3]": "ErlingHaaland",
}


def compare_faces(image_path1, image_path2):
    # 모델 로드

    try:
        # 모델 로드 (이진 모드로 열기)
        classifier = load_model(
            "Facial_recogNet.h5",
            compile=False,
        )
    except UnicodeDecodeError as e:
        print("모델 파일을 올바르게 로드하지 못했습니다:", str(e))

    # 이미지1 로드 및 전처리
    image1 = cv2.imread(image_path1)
    image1 = cv2.resize(image1, (224, 224), interpolation=cv2.INTER_LINEAR)
    image1 = image1 / 255.0
    image1 = image1.reshape(1, 224, 224, 3)

    # 이미지2 로드 및 전처리
    image2 = cv2.imread(image_path2)
    image2 = cv2.resize(image2, (224, 224), interpolation=cv2.INTER_LINEAR)
    image2 = image2 / 255.0
    image2 = image2.reshape(1, 224, 224, 3)

    # 얼굴 인식 및 비교
    pred1 = np.argmax(classifier.predict(image1, 1, verbose=0), axis=1)
    pred2 = np.argmax(classifier.predict(image2, 1, verbose=0), axis=1)

    name1 = facial_recog_dict[str(pred1[0])]
    name2 = facial_recog_dict[str(pred2[0])]

    # 동일한 사람 여부 확인
    if name1 == name2:
        return f"두 사진은 동일한 사람 ({name1}) 입니다."
    else:
        return "두 사진은 서로 다른 사람입니다."


# 비교할 이미지 파일 경로 설정
image_path1 = "MobileNet_Recognition/MobileNet_Recognition/val/n0/AhnHyunBeom_24.jpg"
image_path2 = "MobileNet_Recognition/MobileNet_Recognition/val/n0/AhnHyunBeom_28.jpg"

result = compare_faces(image_path1, image_path2)
print(result)
