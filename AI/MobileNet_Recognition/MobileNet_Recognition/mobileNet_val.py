from keras.models import load_model
import os
import cv2
import numpy as np
from os import listdir
from os.path import isfile, join

try:
    # 모델 로드
    classifier = load_model("Facial_recogNet.h5", compile=False)
except UnicodeDecodeError as e:
    print("모델 파일을 올바르게 로드하지 못했습니다:", str(e))


facial_recog_dict = {
    "[0]": "AhnHyunBeom",
    "[1]": "ChoGuesung",
    "[2]": "CristianoRonaldo",
    "[3]": "ErlingHaaland",
}

facial_recog_dict_n = {
    "n0": "AhnHyunBeom",
    "n1": "ChoGuesung",
    "n2": "CristianoRonaldo",
    "n3": "ErlingHaaland",
}


def draw_Prediction(name, pred, im, className):
    facial = facial_recog_dict[str(pred)]
    BLACK = [0, 0, 0]
    expanded_image = cv2.copyMakeBorder(
        im, 80, 0, 0, 100, cv2.BORDER_CONSTANT, value=BLACK
    )
    cv2.putText(
        expanded_image,
        className,
        (20, 60),
        cv2.FONT_HERSHEY_SIMPLEX,
        1,
        (0, 0, 255),
        2,
    )
    cv2.imshow(name, expanded_image)


def getRandomImage(path):
    """function loads a random images from a random folder in our test path"""
    folders = list(
        filter(lambda x: os.path.isdir(os.path.join(path, x)), os.listdir(path))
    )
    random_directory = np.random.randint(0, len(folders))
    path_class = folders[random_directory]
    print("Class - " + facial_recog_dict_n[str(path_class)])
    file_path = path + path_class
    file_names = [f for f in listdir(file_path) if isfile(join(file_path, f))]
    random_file_index = np.random.randint(0, len(file_names))
    image_name = file_names[random_file_index]
    return (
        cv2.imread(file_path + "/" + image_name),
        facial_recog_dict_n[str(path_class)],
    )


for i in range(0, 20):
    input_im, className = getRandomImage(
        "MobileNet_Recognition/MobileNet_Recognition/val/"
    )
    input_original = input_im.copy()
    input_original = cv2.resize(
        input_original, None, fx=0.5, fy=0.5, interpolation=cv2.INTER_LINEAR
    )

    input_im = cv2.resize(input_im, (224, 224), interpolation=cv2.INTER_LINEAR)
    input_im = input_im / 255.0
    input_im = input_im.reshape(1, 224, 224, 3)

    # Get Prediction
    res = np.argmax(classifier.predict(input_im, 1, verbose=0), axis=1)

    # Show image with predicted class
    draw_Prediction("Prediction", res, input_original, className)
    cv2.waitKey(0)

cv2.destroyAllWindows()
