import cv2

# 이미지 불러오기
image_path = "C:/Users/82104/Desktop/CropImage/train/19062842/S001/L1/E01"
image = cv2.imread(image_path)

# Haar Cascade Classifier 모델 로드
face_cascade = cv2.CascadeClassifier(
    cv2.data.haarcascades
    + "recognition\\dlib\\opencv-4.x\\data\\haarcascades\\haarcascade_frontalface_default.xml"
)

# 얼굴 감지
faces = face_cascade.detectMultiScale(
    image, scaleFactor=1.1, minNeighbors=5, minSize=(30, 30)
)

# 감지된 얼굴 주위에 사각형 그리기
for x, y, w, h in faces:
    cv2.rectangle(image, (x, y), (x + w, y + h), (0, 255, 0), 2)

    # 얼굴 부분 추출
    face = image[y : y + h, x : x + w]

    # 추출된 얼굴 이미지를 저장
    cv2.imwrite("output.jpg", face)

# 이미지에 얼굴 감지 결과 표시
cv2.imshow("Detected Faces", image)
cv2.waitKey(0)
cv2.destroyAllWindows()
