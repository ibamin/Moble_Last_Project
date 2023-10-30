# Controller-with-Face-Recognition
Face-Recognition with ResNet + Door Lock Controller

# 대학교 졸업 작품
 얼굴인식을 이용한 도어락 제어기
 
 
## 1. 작품 주제
 기존의 디지털 도어락에 카메라와 제어기를 부착하여, 얼굴인식 시스템을 도입합니다.
 그로써 얼굴인식이 가능한 생체인식 도어락으로 업그레이드를 시킵니다.
 얼굴인식 시스템에는 딥러닝 기술인 ResNet을 사용하여 보다 높은 인식률를 부여합니다.
 
 
 현관문 밖에 부착된 카메라 앞에 사람이 서 있을 때, 등록된 사용자인 경우 약 2초내로 인식/확인하고 문을 열어줍니다.
 하지만 등록된 사용자가 아닌 경우, 약 5초 이상 현관문 앞에 머무를 때 사용자 어플리케이션을 통해 사진과 시간에 대한 기록을 남깁니다.
 또한 부착된 카메라를 이용해 사용자는 CCTV로도 활용할 수 있습니다. 언제 어디서든 어플리케이션을 이용해서 말입니다.
 

## 2. 개발 환경
**Controller**
1. Rasberry Pi 3+ model
2. python
 
**Camera**
 1. python (OpenCV + dlib + ResNet)
 
**Application**
 1. Android studio
 2. JAVA
 
 
## 3. 개발 기간
 프로젝트 기간 : 20.03.16 ~ 20.06.12
 프로젝트 인원 : 3명
 
 
## 4. 얼굴인식 과정

1. 얼굴 검출 : dlib.get_frontal_face_detector()
2. 특징 검출 : 68_face_landmarks
3. npy 파일에 학습된 사용자의 얼굴 정보 저장 (encoding)
4. 얼굴 인식 : resnet_model 


## 5. 작품 영상(결과물)
