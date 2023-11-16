 Moble_Last_Project

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

### 회로도

#### Raspberry 1

<img src="/readme/RP1.png"></img>

#### Raspberry 2

<img src="/readme/RP2.png"></img>


--------------------------------------------------------------------------

## AI

사용 모델 : ResNet + Dlib

### RseNet + Dlib 성능 평가
![image](https://github.com/ibamin/Moble_Last_Project/assets/96930294/2c9d0b9c-cd49-474e-98f1-2fd525788b2d)

------------------------------------------------------------------------

이외 안드로이드와 서버는 Server , Android에 있는 코드를 사용하시면 됩니다

## 사용법

### 서버
1.AWS인스턴스를 구축한뒤 환경을 설정한다

2.Server.py를 만들어 서버코드를 넣는다

3.pyhon3 Server.py를 통해 서버를 실행시킨다

### 임베디드
1.모듈들과 프레임을 구한다

2.회로도에 맞게 회로를 구성한다

3.vnc와 같은 원격 접속을 통해 Raspberry Pi에 접속

4.Embedded폴더에 있는 코드를 사용한다

5.python3 실행파일.py를 실행시킨다

### 안드로이드
1.Andorid Studio를 설치후 Andorid 폴더에 있는 project를 연다

2.코드 내부의 AWS와의 통신을 위한 부분을 자신의 AWS IP로 변경한다

3.휴대폰의 관리자옵션을 On 한다

4.컴퓨터와 휴대폰을 연결한뒤 Android Studio을 통해 설치 시킨다

5.이후 회원가입 및 실행을 한다
