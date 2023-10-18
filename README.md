# Numble-Project
본 레파지토리는 넘블 딥 다이브 프로젝트인 "방문자 수 트래킹 서비스 구축하기"의 결과물로,   
[https://hits.seeyoufarm.com/](https://hits.seeyoufarm.com/)를 클론코딩 하였습니다.
구현 내용은 다음과 같습니다

## 구현 기능
다음 4 가지의 기능을 제공합니다.   
1. PUT ~/hits?url={url}을 이용하여 해당 url의 일일 조회수와 총 조회수를 1 증가합니다.
2. GET ~/hits?url={url}을 이용하여 해당 url의 일일 조회수와 총 조회수를 조회합니다.
3. GET ~/history?url={url}을 이용하여 해당 url의 7일 간의 조회수를 조회합니다.
4. 날짜가 변경되면 전 날의 조회수를 히스토리로 기록하고 일일 조회수를 모두 0으로 변경합니다.

## Branch 관리 및 CI 적용
Github Actions를 이용하여 main branch에 pull request가 들어오면 프로그램을 자동으로 빌드합니다.(테스트도 같이 진행)
- main branch: 구현이 완료된 사항을 합하는 용도
- develop branch: 구현 중인 사항을 저장하는 용도

## 고려 사항
- 기본 고려 사항   
  기본적으로 코드를 깔끔하고 알아볼 수 있게 작성하려고 노력하고, 주석을 달아 알아보는 것에 도움을 주고자 하였습니다.   
- sql 쿼리 관련 고려 사항   
  기본적으로 서버와의 통신을 최소화하도록 노력하였습니다.   
  기본적으로 Lazy fetch를 사용하고 히스토리를 불러올 경우 join fetch를 이용하여 N+1 쿼리를 해결합니다.   
  대용량 조회와 저장의 경우 페이징과 jdbc templete의 batch update를 사용합니다.
- 동시성 문제 고려 사항   
  redis의 분산락인 redisson을 사용하여 분산락을 해결하였습니다.
   
자세한 고려 사항은 아래의 회고록에서 확인하실 수 있습니다.

## 회고록
1. [API 프로토타입 만들기](https://lateral-stag-283.notion.site/1-API-98ae9eedfc6a43b8a3929ab3f9a617c4?pvs=4)
2. [Swagger 사용하기 & CI 적용하기](https://lateral-stag-283.notion.site/2-Swagger-CI-b8e141ab27864fc09427e253b1cd7eee?pvs=4)
3. [대용량 데이터 처리와 동시성 문제 처리](https://lateral-stag-283.notion.site/3-47b84c86eafb4299a2a357a209d9308c?pvs=4)
