# HIPReader - 독서 커뮤니티 플랫폼

책을 사랑하는 사람들을 위한 독서 커뮤니티 플랫폼, **HIPReader**입니다.
리뷰 작성, 토론방 참여, 찜하기, 도서 추천, 올해의 책 선정 등 독서 경험을 풍부하게 만드는 다양한 기능을 제공합니다.

---

## 프로젝트 링크

- **홈페이지**: [HIPReader](https://HIPReader.com)
- **기획/정리 문서(Notion)**: [Notion 기획 문서](https://teamsparta.notion.site/6-1ce2dc3ef514814e9f6cf33e0b804f13)

---

## 시스템 아키텍처

### 아키텍처 다이어그램
![Architecture](./docs/images/architecture.png)

- **Spring Boot 기반 백엔드 애플리케이션**
- **MySQL**을 RDB로 사용하여 데이터 영속성 보장
- **Redis**를 활용해 토론방 자동참여 동시성 제어 구현
- **RabbitMQ**를 통한 비동기 알림 처리(토론방 승인/거절 시)
- **WebSocket** 기반 토론방 실시간 채팅 기능
- **JWT 기반 인증 시스템** (Spring Security)
- **Elasticsearch**를 통한 책 검색 및 추천
- **SMTP(Gmail)**를 이용한 이메일 인증 및 알림
- **Jenkins + Docker + AWS EC2**를 통한 CI/CD 자동화

### 주요 흐름 요약

- 사용자 요청 -> Spring Controller -> Service -> Repository
- 성능 이슈가 예상되는 참여/통계 로직은 **Redis 또는 MQ 기반 처리**
- 토론방 채팅은 WebSocket 연결을 통해 실시간 처리
- 주요 변경 이벤트 발생 시, MQ를 통해 알림 및 통계 갱신 비동기 처리
  
---

## ERD & 플로우차트

- [ERD 보기](https://erdcloud.com/...)
- [토론방 참여 흐름도](https://draw.io/...)

---

## 🔧 사용 기술

### Backend
- Java 17, Spring Boot 3, JPA, Spring Security, JWT
- Redis, MySQL, Elasticsearch, RabbitMQ, WebSocket

### Infra
- AWS EC2, RDS, S3, Route 53, Nginx
- Docker, Jenkins, GitHub Actions

### Monitoring & Test
- Spring Actuator, JUnit5, TestContainers
- Kibana

### Collaboration
- GitHub, Notion, Slack

---

## 💻 주요 기능

| 분류       | 기능 설명 |
|------------|-----------|
| 회원       | 회원가입, 로그인, 권한 검증 (JWT 기반) |
| 유저북     | TO_READ / READING / FINISHED 관리 |
| 책         | 책 등록, 조회, 검색, 추천 |
| 리뷰       | 리뷰 작성, 수정, 삭제, 페이징 조회 |
| 자유게시판 | 게시글 CRUD, 댓글 기능 예정 |
| 토론방     | 생성, 신청, 자동/수동 승인, 참여 상태 관리 |
| 올해의 책 | 유저북 상태 기반 점수 집계 → 인기 책 선정 |
| 찜 기능    | 관심 도서 찜/취소 |
| 실시간 채팅 | 토론방 WebSocket 채팅 |
| 알림       | 토론방 승인/거절 시 이메일 발송 (SMTP) |
| 비동기 처리 | RabbitMQ 기반 이벤트 처리 |
| 통계       | 연도별 독서량, 평균 독서 시간 계산 |

---

## 🧭 기술적 의사 결정 및 성능 개선

- Redis 기반 분산락: 선착순 토론방 입장 제어
- Pessimistic & Optimistic Lock: DB 경쟁 제어 비교
- Elasticsearch: 책 검색 및 추천 기능에 활용
- RabbitMQ: 비동기 알림, 이벤트 흐름 분리
- CI/CD 자동화: Jenkins, Docker 기반 배포 자동화
- JWT + Spring Security: 인증/인가 처리

---

## 😵 트러블슈팅

| 문제 상황 | 해결 방법 |
|-----------|------------|
| 동시성 충돌 | Redis Lock 도입, save vs saveAndFlush 시점 비교 |
| 이메일 미발송 | SMTP 설정 검토 및 Gmail 보안 설정 확인 |
| 참여 실패 케이스 | 분산락 성능 테스트 및 경합 조건 수정 |
| 테스트 실패 | TestContainers 도입 및 Mock 설정 |
| 연관 엔티티 삭제 오류 | Cascade 정책 정리 및 soft delete 적용 |

---

## 👥 팀원 소개

| 이름     | 역할                                | GitHub | Blog |
|----------|-------------------------------------|--------|------|
| 전영환   | 리뷰, 토론방, 동시성 제어, 성능 테스트 | [GitHub](https://github.com/younghwan314) | [Velog] |
| 팀원 A   | WebSocket 채팅, 통계 기능           | [GitHub](...) | [...] |
| 팀원 B   | 책 추천(Elasticsearch), 검색 API    | [...] | [...] |
| 팀원 C   | CI/CD, SMTP, RabbitMQ               | [...] | [...] |

---

## 🧪 로컬 실행 및 모니터링

```bash
# 1. 빌드
./gradlew build

# 2. 도커 실행
docker-compose up
