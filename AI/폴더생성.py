import os
import shutil

# 원본 디렉토리 경로
source_dir = "C:\\Users\\82104\\Desktop\\CropImage\\train"

# 대상 디렉토리 기본 경로
target_base_dir = "C:\\Users\\82104\\Desktop\\NewTargetFolder"

# 원본 디렉토리 내의 "testfolder" 디렉토리를 찾아 대상 디렉토리로 복사
for root, dirs, files in os.walk(source_dir):
    for dir in dirs:
        if dir == "test":
            source_folder = os.path.join(root, dir)
            target_folder = os.path.join(
                target_base_dir,
                f"NewTargetFolder{len(os.listdir(target_base_dir)) + 1}",
            )

            # 대상 디렉토리에 복사
            os.makedirs(target_folder)
            for item in os.listdir(source_folder):
                source_item = os.path.join(source_folder, item)
                target_item = os.path.join(target_folder, item)
                if os.path.isdir(source_item):
                    shutil.copytree(source_item, target_item)
                else:
                    shutil.copy2(source_item, target_item)
