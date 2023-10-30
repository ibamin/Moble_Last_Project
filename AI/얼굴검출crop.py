import dlib
import cv2
import os

# Dlib 얼굴 감지기 초기화
detector = dlib.get_frontal_face_detector()

# 최상위 폴더 경로
top_folder = "C:/Users/82104/Desktop/High_Resolution_crop/train"

# 원하는 크기
desired_size = (256, 256)  # 원하는 크기로 수정하세요

# 모든 하위 폴더를 순회
for root, dirs, files in os.walk(top_folder):
    for img_file in files:
        if img_file.endswith(".jpg"):
            img_path = os.path.join(root, img_file)
            img = cv2.imread(img_path)
            gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

            # 얼굴 감지
            faces = detector(gray)

            if len(faces) > 0:
                for face in faces:
                    x, y, w, h = face.left(), face.top(), face.width(), face.height()
                    cropped = img[y : y + h, x : x + w]

                    # 감지된 얼굴이 비어 있지 않을 때만 이미지를 저장하고 크기를 조정
                    if not cropped is None and not cropped.size == 0:
                        cropped_resized = cv2.resize(cropped, desired_size)
                        # 원본 이미지를 덮어씌우기
                        cv2.imwrite(img_path, cropped_resized)
            else:
                print("No face detected in:", img_path)
