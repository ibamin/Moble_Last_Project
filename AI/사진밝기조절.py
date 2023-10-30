import cv2
import numpy as np
import os

# 이미지 폴더 경로
image_folder = "C:/Users/82104/Desktop/2/2"

# 밝기 조절 값을 설정
brightness_factor = -50  # 더 어둡게 조절하려면 값을 음수로 설정

# 이미지 폴더 내의 모든 이미지 파일에 대한 경로를 가져옵니다
image_files = [f for f in os.listdir(image_folder) if f.endswith(".jpg")]

for image_file in image_files:
    # 이미지를 불러오기
    image_path = os.path.join(image_folder, image_file)
    image = cv2.imread(image_path)

    # HSV 색상 공간으로 변환
    hsv = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)

    # 밝기 조절
    hsv[:, :, 2] = np.clip(hsv[:, :, 2] + brightness_factor, 0, 255)

    # 다시 BGR 색상 공간으로 변환
    output_image = cv2.cvtColor(hsv, cv2.COLOR_HSV2BGR)

    # 결과를 저장
    output_path = os.path.join(image_folder, "adjusted_" + image_file)
    cv2.imwrite(output_path, output_image)

print("밝기 조절이 완료되었습니다.")
