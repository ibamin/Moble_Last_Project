# Moble_Last_Project

# 기획 이유
[모블 라스트 프로젝트]
<img src="/readme/planning_reason_chart.jpg"></img>

프로젝트 기획 당시 해외에 Flipper Zero라는 네트워크 제어장치가 상용화되며
NFC, RFID로 통신하는 TV,LED패널등의 기기들이 멋대로 제어되는 문제가 발생하고 있었다

또한 그에 따라 물리보안 장치 및 다양한 장치에 생체 인식이 포함되는 상황에 본 프로젝트를 기획하였다.

# 구축
## AWS 

-------------------------------------------------------------


| 운영체제 | 용량 | IOPS |
| ------ | -- | -- |
| Ubuntu | 30GB | 3000 |


Free tier에서 사용가능한 최대한의 사용을 했다
하지만 torch를 설치하는 부분에서의 cache를 과다 사용하여 용량을 버티지 못하는지 지속하여 인스턴스가
꺼지는 문제가 발생했다.

<pre>
  <code>
    pip install --no-cache-dir torch torchvision
  </code>
</pre>

과

<pre>
  <code>
    #스왑 파일 생성
    sudo fallocate -l 2G /swapfile

    sudo chmode 600 /swapfile
    sudo chown root:root /swapfile

    #스왑 설정
    sudo mkswap /swapfile

    #스왑 메모리 고정
    echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab

    #스왑 활성화
    sudo swapon /swapfile

    #스왑 확인
    sudo swapon --show
  </code>
</pre>

을 통해 cache를 사용하지 않는 설치와 가상 메모리를 사용하는 방법으로 해결 할 수 있었고 
<pre>
  <code>
    pip install openssh-server
    pip install python3
    pip install Flask
    pip install dlib
  </code>
</pre>
와 같은 모듈들을 설치하였다

## Embedded

--------------------------------------------------------------------------

| 구성품 | 갯수 |
| ------------ | -- |
| Rsapberry Pi | 2개 |
| Camera Module | 1개 |
| Camera Wide-Angle Lens | 1개 |
| Motor | 2개 |
| cooler | 2개 |
| Touch Pannel | 1개 |
