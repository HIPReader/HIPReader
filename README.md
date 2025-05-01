# HIPReader - 독서 커뮤니티 플랫폼

책을 사랑하는 사람들을 위한 독서 커뮤니티 플랫폼입니다.  
유저는 책을 리뷰하고, 토론방에 참여하며, 찜한 책을 관리하고, 올해의 책에 기여할 수 있습니다.  
기본 CRUD를 넘어 인증, 동시성 제어, 추천 알고리즘, 실시간 채팅, CI/CD, 통계 기능까지 포함한 고도화된 백엔드 시스템입니다.

---

## 프로젝트 링크

- **홈페이지**: [링크 삽입 예정]
- **Notion 문서**: [링크 삽입 예정]

---

## 시스템 아키텍처

![Architecture](./docs/images/architecture.png)

- Spring Boot 기반 모놀리식 구조
- Redis와 RabbitMQ를 통한 확장성 고려
- 실시간 채팅(WebSocket), 비동기 이벤트 처리(RabbitMQ)
- CI/CD 자동화 (Jenkins + Docker + AWS)

---

## ERD & 플로우차트

- [ERD 보기](https://erdcloud.com/...)
- [토론방 참여 흐름도](https://draw.io/...)

---

## 사용 기술

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
- Git, GitHub, Notion, Slack

---

## 주요 기능

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

## 기술적 의사 결정 및 성능 개선

- Redis 기반 분산락: 선착순 토론방 입장 제어
- Pessimistic & Optimistic Lock: DB 경쟁 제어 비교
- Elasticsearch: 책 검색 및 추천 기능에 활용
- RabbitMQ: 비동기 알림, 이벤트 흐름 분리
- CI/CD 자동화: Jenkins, Docker 기반 배포 자동화
- JWT + Spring Security: 인증/인가 처리

---

## 트러블슈팅

| 문제 상황 | 해결 방법 |
|-----------|------------|
| 동시성 충돌 | Redis Lock 도입, save vs saveAndFlush 시점 비교 |
| 이메일 미발송 | SMTP 설정 검토 및 Gmail 보안 설정 확인 |
| 참여 실패 케이스 | 분산락 성능 테스트 및 경합 조건 수정 |
| 테스트 실패 | TestContainers 도입 및 Mock 설정 |
| 연관 엔티티 삭제 오류 | Cascade 정책 정리 및 soft delete 적용 |

---

## 팀원 소개

| 이름     | 역할                                | GitHub | Blog |
|----------|-------------------------------------|--------|------|
| 전영환   | 리뷰, 토론방, 동시성 제어, 성능 테스트 | [GitHub]| [Velog]|
| 팀원 A   | WebSocket 채팅, 통계 기능           | [GitHub](...) | [...] |
| 팀원 B   | 책 추천(Elasticsearch), 검색 API    | [...] | [...] |
| 팀원 C   | CI/CD, SMTP, RabbitMQ               | [...] | [...] |

---

## 블로그 정리

| 작성자 | 주제 |
|--------|------|
| 전영환 | Redis Lock vs Pessimistic Lock 성능 비교 |
| 팀원 A | WebSocket 채팅 구축 과정 |
| 팀원 B | Elasticsearch로 책 추천 구현 |
| 팀원 C | SMTP + RabbitMQ로 알림 기능 고도화 |

---

## 로컬 실행 및 모니터링

```bash
# 1. 빌드
./gradlew build

# 2. 도커 실행
docker-compose up
