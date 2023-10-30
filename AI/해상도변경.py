import cv2
import os

# 최상위 폴더 경로
top_folder = "C:/Users/82104/Desktop/High_Resolution_crop/train"

# 원하는 크기
desired_size = (128, 128)  # 원하는 크기로 수정하세요

# 모든 하위 폴더를 순회
for root, dirs, files in os.walk(top_folder):
    for img_file in files:
        if img_file.endswith(".jpg"):
            img_path = os.path.join(root, img_file)
            img = cv2.imread(img_path)

            # 이미지 크기를 원하는 크기로 조정
            resized_img = cv2.resize(img, desired_size)

            # 원본 이미지를 덮어씌우기
            cv2.imwrite(img_path, resized_img)
