import os
import shutil

top_folder = "C:/Users/82104/Desktop/last dataset/lastdataset"

# 모든 하위 폴더를 순회
for root, dirs, files in os.walk(top_folder):
    for folder in dirs:
        if folder == "E03":
            shutil.rmtree(os.path.join(root, folder))
