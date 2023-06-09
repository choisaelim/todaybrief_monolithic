### 오늘의 브리핑 서비스를 모놀리식으로 개발 및 배포하기

혼자서 개발을 처음부터 끝까지 해보기 위한 사이드 프로젝트  
현재까지 기능은 유저 정보의 위치 정보를 이용,  
기상청 공개 API와 카카오 공개 API로  
출퇴근시 해당 지역 날씨와 차량으로 걸리는 소요시간을 보여주는 웹 사이트.  
차후 관심사를 추가하거나 구독서비스 정보 받아서 한눈에 확인할 수 있게 하거나, 캘린더 연동해서 일정을 보여주거나, 출퇴근 루트를 저장하면 대중교통 소요시간 알려주는 등 서비스를 추가해서 폰 위젯기능 처럼 활용 계획

원래는 MSA로 배포하려고 했으나 일단 모놀리식으로 배포하고  
차후 로컬에서 eureka client, server, spring cloud gateway/config로 되어 있는 소스를  
쿠버네티스에 맞게 변경해서 따로 배포 예정

### 개발/배포환경

깃헙에서 개발 소스를 push하면, 젠킨스와 연동된 webhook을 통해 도커허브에 도커 이미지를 빌드하고 푸쉬합니다.  
젠킨스 파이프라인 내에서 빌드 넘버로 도커 이미지 번호를 업데이트해서 자동으로 argoCD가 실행되어,  
AWS로 운영중인 웹사이트에도 반영됩니다.

<p align="center">
 <img src = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbA1cYj%2Fbtr9BYev5yh%2FbpIZ6oF0zSdo2xV03n5xS1%2Fimg.png">
</p>
   
젠킨스 파이프라인    
<p align="center">
 <img src = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FIq2hP%2Fbtr9zvEtTeu%2FSOQ1sgWQgnJOAK7yolni30%2Fimg.png">
</p>
   
argoCD 설정 화면   
<p align="center">
 <img src = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FcDWSQy%2Fbtr9A0Rb6K2%2FQlE0EwPoGdZRLCs3IwrsMk%2Fimg.png">
</p>
   
### 사용기술
> 쿠버네티스(with kops)  
> AWS  
> 스프링부트(with security, jwt, Rest API, JPA)  
> 리액트  
> Mysql  
> 젠킨스, 도커허브, argoCd 등
   
### 화면
<p align="center">
 <img src = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FcIXrkc%2Fbtr9sik7UH0%2FIOdxdXnhEefYcAXjnBsOL1%2Fimg.png">
</p>

배포된 사이트 주소
http://52.78.183.242:30000/

코드 관련 내용, 개발환경구성, 개발이슈는 [티스토리](https://hanaweb.tistory.com/category/%EC%BF%A0%EB%B2%84%EB%84%A4%ED%8B%B0%EC%8A%A4%2CAWS%2CJPA%2CCICD%2CMSA%EC%82%AC%EC%9D%B4%EB%93%9C%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8)에 업데이트
