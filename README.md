본 레파지토리는 넘블 딥 다이브 프로젝트인 "방문자 수 트래킹 서비스 구축하기"의 결과물입니다.

## 프로젝트 소개
본 프로젝트는 url의 조회수를 관리하는 기능을 제공합니다.
1. url 정보를 등록하고 조회수를 증가시킬 수 있습니다.
2. url의 일일 조회수와 총 조회수를 조회할 수 있습니다.
3. url의 7일간의 조회수를 조회할 수 있습니다.

## 구현 기능
다음 4 가지의 기능을 제공합니다.   
|기능|전송|반환|
|---|:---:|---|
|url 조회수 증가|PUT http://{domain}/hit?url={url}|![](image\addHits_result.PNG)|
|url 조회수 조회|GET http://{domain}/hit?url={url}|![](image\getHits_result.PNG)|
|url 히스토리 조회|GET http://{domain}/history?url={url}|![](image\getHistory_result.PNG)|
|오늘 조회수 업데이트| 자정마다 실행| 자정마다 현재 오늘의 조회수를 히스토리에 저장하고 오늘의 조회수를 0으로 변경|

## 개발기간
2023/10/05 ~ 2023/10/18 (2주)

## 기술 스택
- Back-end   
: Java, Spring, JPA, Mysql, Swagger
- 버전 관리   
: Git, Github actions(CI)

## Branch 관리
Github Actions를 이용하여 main branch에 pull request가 들어오면 프로그램을 자동으로 테스트와 빌드합니다.
- main branch: 구현이 완료된 사항을 합하는 용도
- develop branch: 구현 중인 사항을 저장하는 용도

## 프로젝트 구성도
| ERD |
| :---: |
|![](image\erd.PNG)|


## 고려사항 및 회고록
프로젝트 기간
1. [API 프로토타입 만들기](https://lateral-stag-283.notion.site/1-API-98ae9eedfc6a43b8a3929ab3f9a617c4?pvs=4)
    - ERD 그리기 및 요구사항 정의
    - 프로토타입 작성 및 테스트
2. [Swagger 사용하기 & CI 적용하기](https://lateral-stag-283.notion.site/2-Swagger-CI-b8e141ab27864fc09427e253b1cd7eee?pvs=4)
    - Swagger 적용하기
    - 통합 테스트 코드 작성, Github Actions를 이용한 CI 적용
3. [대용량 데이터 처리와 동시성 문제 처리](https://lateral-stag-283.notion.site/3-47b84c86eafb4299a2a357a209d9308c?pvs=4)
    - 대용량 처리 성능 향상(paging + jdbctemplete)
    - 조회수 증가 동시성 문제 해결(Redis redisson)


프로젝트 이후 수정 사항
1. [대용량 트래픽에서 성능 개선하기](https://lateral-stag-283.notion.site/4-4740025d3f0d4731a490a42d4e59635f?pvs=4)
    - 조회수 증가 성능 개선(Mysql update/upsert native query)
2. 캐싱을 이용한 전체 성능 개선(예정)