# HIPReader - 독서 커뮤니티 플랫폼

HIPReader는 책을 사랑하는 사람들이 모여 리뷰를 작성하고, 토론에 참여하며, 자신만의 독서 여정을 관리할 수 있는 커뮤니티 기반 플랫폼입니다.
**CRUD 기능을 넘어**, 실시간 토론(WebSocket), 추천 알고리즘, 통계, 비동기 이벤트 처리 등 다양한 기능과 아키텍처 고도화를 통해 확장성과 실용성을 갖춘 프로젝트입니다.

---

## 기술 스택
|분야|기술|
|---|---|
|Backend| Java, Spring Boot, Spring Security, Spring Data JPA|
|Frontend| Node.js|
|인증/보안| JWT, Spring Security|
|데이터베이스| MySQL, Redis, Elasticsearch|
|메시징/비동기| RabbitMQ, @Async|
|모니터링| Grafana, Prometheus, Kibana|
|CI/CD| Docker, Jenkins|
|협업 및 문서화| Git, GitHub, Slack, Notion, Swagger|

---

## 주요 기능 (MVP)

### 독서 관련 기능
- **유저북(UserBook) CRUD**: TO_READ, READING, FINISHED 상태 관리
- **책(Book) CRUD** + **책 검색**
- **책 추천** 기능
- **올해의 책 선정**
  - 출판연도 기준 필터링
  - 상태별 가중치 계산을 통해 점수화 및 랭킹화
  - 예: TO_READ(1), READING(2), FINISHED(3)

### 커뮤니티 기능
- **회원 CRUD**
- **리뷰 CRUD**
- **자유게시판 CRUD**
- **토론방 CRUD + 신청/승인/거절 로직**
- **이미지 업로드(AWS S3)**

---

## 기능 고도화

### 아키텍처 및 시스템 개선
- 아키텍처 V2 리팩토링
- CI/CD 자동화 구축(Docker + Jenkins)
- Kibana 기반 모니터링 환경 설정

### 동시성 제어 및 성능
- **Redis 기반 선착순 토론방 입장 제어**
- **낙관적/비관적 락 성능 테스트**
- **RabbitMQ 기반 비동기 이벤트 처리**

### 통계 및 추천 기능 확장
- **개인별 독서 통계**
  - 연도별 독서량
  - 책당 소요시간 평균
- **올해의 책 고도화**
  - 동일 점수 책 -> 모두 표시 or 대표 책 한 권 선택
  - 과년도별 올해의 책 기능 추가 예정
- **책 추천 기능**: Elasticsearch 기반 추천 로직 도입(V3)
- **검색 기능 개선**: 기존 DB -> 외부 Open API 활용으로 확장 검토

### 실시간 기능 및 사용자 알림
- **WebSocket 기반 실시간 토론 기능**
- **SMTP 이메일 알림 시스템(승인/거절 알림 등)**

---

