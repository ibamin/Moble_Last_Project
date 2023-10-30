from PIL import Image

# 원본 이미지 열기
input_image_path = "1.jpg"
output_image_path = "output.jpg"
image = Image.open(input_image_path)

# 원하는 크기로 이미지 리사이즈
new_width = 640  # 새로운 너비
new_height = 480  # 새로운 높이
resized_image = image.resize((new_width, new_height), Image.ANTIALIAS)

# 이미지 저장 (압축 수준을 조정하려면 quality 매개변수 사용)
resized_image.save(output_image_path, optimize=True, quality=10)

# 이미지 닫기
image.close()
resized_image.close()
