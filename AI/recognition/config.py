from easydict import EasyDict as dict


def configuration():
    config = dict()
    config.imagePath = "C:/Users/82104/Desktop/CropImage/"  # 이미지 폴더 경로
    config.modelSavePath = ".//work//"  # 모델 저장 경로
    config.batch_size = 20  # 배치 사이즈
    config.epochs = 30  # 에폭 수
    config.save_epochs = 1  # 에폭 몇 번마다 모델 저장할 것인지
    config.validate_epochs = 1  # 검증을 위한 에폭 수
    config.validate_ratio = 0.1  # 데이터셋을 훈련 및 검증 세트로 나눌 비율
    config.display_it = 10  # 훈련 과정을 표시할 반복 횟수

    config.lr = 0.001  # 학습률
    config.momentum = 0.9
    config.weight_decay = 0.0005
    config.schedule_lr = [
        10,
        20,
        30,
    ]  # 학습률 수정을 적용할 에폭

    config.inputSize = 128
    # config.device = -1                     # CPU : -1, GPU : 디바이스 번호
    config.device = 0  # CPU : -1, GPU : 디바이스 번호

    config.embedding_size = 256  # 특성 벡터 차원

    # "test.py"의 매개변수

    config.pretrained_model = "10.26work/280.pth.tar"  # 훈련된 모델의 전체 경로
    config.refImg = r"C:\Users\82104\Desktop\High_Resolution\test\team\1.jpg"
    config.queryImg = r"C:\Users\82104\Desktop\High_Resolution\test\team\v.jpg"

    return config
