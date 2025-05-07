![image (3) (1)](https://github.com/user-attachments/assets/2c4da9d6-d35e-4597-bb34-8cd9c1ce832d)

---

## 🔹 팀소개
<table> <tr> <td align="center" width="200px"> <img src="https://avatars.githubusercontent.com/Kimg-1111" width="100"><br><br> <b>김기홍</b><br>🌟 리더<br> <ul align="left"> <li>회원 인증/인가</li> <li>올해의 책</li> <li>알림, 메시징 처리</li> </ul> <a href="https://github.com/Kimg-1111">GitHub</a> | <a href="https://velog.io/@rl...">Blog</a> </td> <td align="center" width="200px"> <img src="https://avatars.githubusercontent.com/Shin-i-Hwan" width="100"><br><br> <b>신기환</b><br>🛠 부리더<br> <ul align="left"> <li>AWS 인프라 구성</li> <li>Jenkins를 활용한 CI/CD 구축</li> </ul> <a href="https://github.com/Shin-i-Hwan">GitHub</a> | <a href="https://dev-sunflower.tistory.com/">Blog</a> </td> </tr> <tr> <td align="center" width="200px"> <img src="https://github.com/user-attachments/assets/6b887200-d224-4d48-88ef-484cdf9ff344" width="100" height="100"><br><br> <b>이지은</b><br>👤 팀원<br> <ul align="left"> <li>책 추천 기능 구현</li> <li>토론방 실시간 채팅 기능 구현</li> </ul>  Kibana, Redis, ElasticSearch<br> <a href="https://github.com/jjieun0123">GitHub</a> | <a href="https://velog.io/@co...">Blog</a> </td> <td align="center" width="200px"> <img src="https://avatars.githubusercontent.com/SuhyeonB" width="100"><br><br> <b>봉수현</b><br>👤 팀원<br> <ul align="left"> <li>검색 기능</li> <li>읽은 책 통계</li> <li>프론트엔드 작업</li> </ul> <a href="https://github.com/SuhyeonB">GitHub</a> | <a href="https://velog.io/@siha_014/posts">Blog</a> </td> <td align="center" width="200px"> <img src="https://avatars.githubusercontent.com/you-wan314" width="100"><br><br> <b>전영환</b><br>👤 팀원<br> <ul align="left"> <li>리뷰 기능 구현</li> <li>독서 토론방 구현</li> <li>동시성 제어</li> </ul> <a href="https://github.com/you-wan314">GitHub</a> | <a href="https://velog.io/@yo...">Blog</a> </td> </tr> </table>

-----

## 🔹 개요

**기록하고 소통하자 우리가 리드하는 HIP-Reader!**

한강작가가 노벨상을 받은 이후로 우리나라에서도 **TextHip**현상이 더욱 많이 생겼습니다.

저희 Hip-Reader는 그 수요를 이용해서 읽고 싶은 책을 **검색**하고 **기록**하며, 서로 **소통** 할 수 있도록 하였고,

하나의 주제를 가지고 **토론**할 수 있도록 만든 서비스입니다.

<details>
<summary>**TextHip이란?**</summary>
<div markdown="1">

'텍스트힙(Text Hip)'은 '글자'를 뜻하는 '텍스트(Text)'와 '힙하다(Hip, 멋있다', '개성 있다)'를 합성한 신조어로, ‘독서 행위가 멋지고 세련된 활동으로 인식되는 현상’을 의미한다. 이는 특히 디지털 기기에 익숙한 MZ세대)에서 두드러지게 나타나는 동향으로, 독서를 단순한 취미 활동을 넘어 자기표현과 소통의 수단으로 활용하는 것을 포함한다 
    
    (출처: 국립중앙도서관 https://librarian.nl.go.kr/LI/contents/L30103000000.do?schM=view&page=1&viewCount=10&id=50181&schBdcode=&schGroupCode=)
    
</div>
</details>
- 
    
---

## 🗣️ 주요 기능

### 🔍 책 검색

- 읽고 싶은 책에 대한 정보를 검색할 수 있습니다. (알라딘API)
- 검색 이미지
    
![image](https://github.com/user-attachments/assets/d0b2534b-5922-4c17-a562-b60006680437)

    
- 그 외에 없는 도서는 직접 등록도 가능합니다
  
<details>
<summary>등록 이미지</summary>
<div markdown="1">
![image](https://github.com/user-attachments/assets/66e5b623-e763-4c9f-b4ba-57470be8b773)

    
![image](https://github.com/user-attachments/assets/e809af65-a4ed-416a-98a5-15076e1ecea1)

    
![image](https://github.com/user-attachments/assets/b492b86b-f15e-49f5-855c-b09d791d182d)

</div>
</details>  

    

### 📝 책 읽기 기록하기

- 읽고싶은 책을 찜해두고, 읽기 시작하면 진행도를 저장할 수 있습니다.
- 다 읽은 책은 따로 목록이 저장되며, 추후에 토론도 가능합니다.

### 💓 책 추천

- 위시리스트 데이터를 기반으로 연령, 성별, 카테고리별로 책을 추천해줍니다.
- 이미지
    
![image](https://github.com/user-attachments/assets/b9f995eb-8b8e-43fa-a883-1c115253d287)

    

### 🏅 올해의 책

- 찜, 읽는중, 다읽음 상태에 각각 가산점을 부여하여 점수가 가장 높은 책을 보여줍니다.
- 이미지
    
![image](https://github.com/user-attachments/assets/acfd9a47-f27e-4ca4-946a-6aee46bc263e)

    

### 🔊 토론방

- 다 읽은 책을 가지고 주제를 선정하여 여러사람과 실시간 채팅이 가능합니다.
- 이미지
    
![image](https://github.com/user-attachments/assets/ac773042-070e-4554-983d-818267548528)

    

---

## 🖍️  ERD 다이어그램

![image](https://github.com/user-attachments/assets/bab3a114-904a-4d8d-ab54-96f79d906522)


---

## 🏗️  아키텍처

![image](https://github.com/user-attachments/assets/b42e2340-7f55-450d-bf9a-102d88abf601)

---

## 📃 [API 명세서](https://www.notion.so/6-1ce2dc3ef514814e9f6cf33e0b804f13?pvs=21)

---

## 🔧 [기술적 의사 결정 및 개선](https://www.notion.so/1e62dc3ef51480efbfbde83f4ec948de?pvs=21)

---

## 💣  [트러블 슈팅](https://www.notion.so/1e72dc3ef5148054b297d776c79f4f04?pvs=21)

---

## 📚 기술 스택
| 백엔드                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | 프론트엔드                                                                                                                                                                                                       | 인프라                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     | Other Tools                                                                                                                                                                                                                                                                                                                                                                                                                       |
| --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| ![Java](https://img.shields.io/badge/Java-007396?style=flat\&logo=java\&logoColor=white) <br> ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat\&logo=spring-boot\&logoColor=white) <br> ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat\&logo=mysql\&logoColor=white) <br> ![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=flat\&logo=rabbitmq\&logoColor=white) <br> ![Gradle](https://img.shields.io/badge/Gradle-02303A?style=flat\&logo=gradle\&logoColor=white) <br> ![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat\&logo=redis\&logoColor=white) | ![React](https://img.shields.io/badge/React-61DAFB?style=flat\&logo=react\&logoColor=black) <br> ![TypeScript](https://img.shields.io/badge/TypeScript-3178C6?style=flat\&logo=typescript\&logoColor=white) | ![EC2](https://img.shields.io/badge/AWS_EC2-FF9900?style=flat\&logo=amazon-aws\&logoColor=white) <br> ![Route 53](https://img.shields.io/badge/AWS_Route_53-FF9900?style=flat\&logo=amazon-aws\&logoColor=white) <br> ![ALB](https://img.shields.io/badge/AWS_ALB-FF9900?style=flat\&logo=amazon-aws\&logoColor=white) <br> ![Elasticsearch](https://img.shields.io/badge/Elasticsearch-005571?style=flat\&logo=elasticsearch\&logoColor=white) <br> ![Kibana](https://img.shields.io/badge/Kibana-005571?style=flat\&logo=kibana\&logoColor=white) <br> ![RDS](https://img.shields.io/badge/AWS_RDS-527FFF?style=flat\&logo=amazon-aws\&logoColor=white) <br> 기타 | ![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat\&logo=github\&logoColor=white) <br> ![Slack](https://img.shields.io/badge/Slack-4A154B?style=flat\&logo=slack\&logoColor=white) <br> ![Postman](https://img.shields.io/badge/Postman-FF6C37?style=flat\&logo=postman\&logoColor=white) <br> ![JMeter](https://img.shields.io/badge/JMeter-D22128?style=flat\&logo=apache-jmeter\&logoColor=white) <br> 기타|

---

## 🔍  프로젝트 더 보기

📎 [GitHub 링크](https://github.com/HIPReader/HIPReader)

📎 [프로젝트를 하면서 기록한 자료 모음](https://www.notion.so/5-1ce2dc3ef5148158861be342ae97e7ac?pvs=21)

---
