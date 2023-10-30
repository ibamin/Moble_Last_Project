import cv2
import os

# 최상위 폴더 경로
top_folder = "C:\\Users\\82104\\Desktop\\CropImage\\train"

# 모든 하위 폴더를 순회
for root, dirs, files in os.walk(top_folder):
    for img_file in files:
        if img_file.endswith(".jpg"):
            img_path = os.path.join(root, img_file)
            img = cv2.imread(img_path)
            height, width, _ = img.shape

            # 특정 영역을 정의 (가로 1/4부터 3/4, 세로 1/5부터 3/3.5)
            start_x = int(width / 4)
            end_x = int(3 * width / 4)
            start_y = int(height / 5)
            end_y = int(3 * height / 3.5)

            # 특정 영역을 자르기
            cropped = img[start_y:end_y, start_x:end_x]

            # 이미지를 원래 경로에 저장
            if not cropped is None and not cropped.size == 0:
                cv2.imwrite(img_path, cropped)
            else:
                print("Failed to process or save image:", img_path)
