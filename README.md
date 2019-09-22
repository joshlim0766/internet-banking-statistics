# 카카오페이 사전과제

**개발환경**
- Backend
    - Java 8
    - Spring Boot 2.1.8
    - JPA
    - gradle

**Build**
```
$ ./gradlew clean build
```

**Excute**
```
$ ./startup.sh
```

**API Document**
```
웹브라우저에서 http://${service_ip}:8080/swagger-ui.html로 접근 가능
```

## 문제 해결 전략 
#### API 설계
```
1. 조회는 GET, 서버에 새로운 자원을 생성하거나 행위는 POST, 수정은 PUT이나 PATCH 삭제는 DELETE Method를 사용하는 것을 원칙으로 한다.
1.1. CSV에서 자료를 읽어서 DB에 적재하는 API는 POST
1.2. 다른 조회 API들은 GET
1.3. 2019년 사용율 예측 API는 POST
```
#### DB 설계
```
1. 디바이스 테이블은 독립 테이블로 설계하라는 제약 사항 존재
2. 다음과 같이 두 가지 형태를 생각해볼 수 있음
2.1 {year, year_rate, device_id(FK), rate}로 자료를 적재
2.2 {stat_master_id(FK), device_id(FK), rate}로 자료를 적재하는 형태
3. 2.2의 형태를 채택
3.1 2.1의 경우 연도, 연도별 전체 인터넷 뱅킹 사용율이 계속 중복되어 저장되므로 비효율적이라 생각하였음.
```
#### 사용율 예측 API
```
1. 각종 예측 알고리즘 중 지수평활(Exponential Smoothing)을 선택
1.1 시계열 예측에 적합한 모델로 생각되며 실제 기존 값을 바탕으로 한 예측 값을 구하는데 많이 사용되고 있음
1.2 주어진 자료는 연도별 자료이기에 추세까지 반영하는 Double Exponential Smoothing을 채택하기로 함
2. 구현 전략
2.1 https://geekprompt.github.io/Java-implementation-for-Double-Exponential-Smoothing-for-time-series-with-linear-trend/의 구현을 그대로 사용
2.2 API는 다음과 같은 과정을 거쳐서 위의 참고 링크에 있는 클래스를 이용하였음
2.2.1 서울시의 자료이므로 인구수는 1000만으로 가정
2.2.2 연도별 전체 인터넷 뱅킹 이용율이 저장되어 있는 internet_banking_stat을 조회하여 얻어온 사용율을 이용하여 인터넷 뱅킹 전체 이용자 수를 계산
2.2.3 2.2.2에서 얻은 결과에 디바이스 별 이용률이 저장되어 있는 internet_banking_stat_detail을 조회하여 얻어온 사용율로 디바이스 별 이용자 수를 계산
2.2.4 2.2.2에서 얻은 값을 forecasting하고 2.2.3에서 얻은 값을 forecasting하여 얻은 두 값으로 입력으로 받은 디바이스의 2019년 인터넷 뱅킹 예상 사용율 계산
2.2.5 alpha, beta 값은 엑셀로 미리 기존 데이터를 이용하여 계산해둔 값을 사용하였음.
```
#### JWT를 이용한 API 인증
```
1. Spring Security에서 제공하는 oauth2 + jwt 구현을 이용하였음
2. 토큰 발급, 갱신은 spring의 oauth2, jwt 관련 class를 이용하여 구현
3. Refresh token 
3.1 Refresh token 발급 정책은 토큰 발급시 서버의 DB에 저장하도록 함.
3.2. refresh API가 호출되면 oauth, jwt 관련 class를 이용하여 access token을 재발급하고 새로운 refresh token을 DB에 저장
4. Access token
4.1 signin, signup을 제외한 모든 API는 authorization header에 토큰을 넣어야 접근 가능함
5. Token 서명
5.1 keytool을 이용하여 생성한 서버 인증서를 secret으로 이용하여 JWT의 signature를 서명하며 서버의 공개키를 이용하여 이를 복호화 하여 인증을 할 수 있도록 구성
```
#### 대용량 서비스를 위한 전략
```
1. EHCache 적용
1.1 과제의 API 대부분이 한번 적재되면 변할 일이 거의 없는 데이터임
1.2 이런 경우 Cache를 운영하면 매번 API 호출할 때마다 DBMS에 접근하지 않고 최초 한번만 DBMS에 접근 후 Cache가 살아 있는 한 계속 Cache를 이용하게 되므로 매우 효율적임
1.3 실제 운영 상황에서도 DB 읽기가 80% 가량을 차지하는 경향을 보이므로 Read replica를 구성하고 Redis등으로 Cache 서버를 구성하는 구조를 채택하는 것을 생각해볼 수 있음.

2. 서비스 분리
2.1 현재 구현 같은 경우 API 서비스, 인증&인가 서비스가 함께 합쳐진 구조임
2.2 시간이 좀 더 있다면 API 서비스(oauth2 입장에서는 Resource 서버), 인증&인가 서비스를 나누어서 구성하고 싶음
2.3 실제 운영 상황을 가정해본다면 인증&인가 서비스는 다양한 다른 서비스들의 인증&인가를 담당하고 API 서비스는 자신의 역할만 담당하는 형태로 나뉘어서 구성되는 것이 효율적이라고 생각함
2.4 서비스가 분리되면 서비스간의 API 호출과 같은 시나리오가 생길 수 있는데 이럴 때 Service Discovery와 같은 개념이 중요해짐
2.4.1 Spring Cloud에 이러한 역할을 해주는 다양한 제품들이 존재함. (Eureka, Zuul, Ribbon, Etc)
```
