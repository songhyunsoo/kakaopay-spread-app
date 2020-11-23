## 카카오페이 뿌리기 기능 구현하기

### 기반 기술

- 언어 : Java

- Application : Springboot

- Database: MongoDB (Embeded)

- Database는 Springboot embeded MongoDB를 사용

### 문제 해결 전략


- Springboot 프레임워크를 사용하여 REST API를 구현

- NoSQL 데이터베이스를 사용하여 Json 형태로 데이터 관리 (java내에서는 Value Object를 사용)

- Content-Type 및 Accept는 application/json를 사용하여 json 형식으로 요청/응답 

- 별도의 Exception 객체 및 ErrorController를 구현하여 제약 조건에 대해 예외 처리

- 받기 API의 경우, 여러 사람이 동시에 뿌리기 객체에 접근해 하나의 분배된 금액을 받아갈수도 있기에, @Transactional(isolation=Isolation.READ_COMMITTED)를 사용하여 한명의 사용자가 받기 수행중이라면 다른 사용자는 해당 데이터에 접근하지 못하여 받기 요청 시간에 따른 일관된 데이터를 보장

- Token은 난수함수를 사용하여 랜덤 문자열을 생성(HashSet을 사용하여 중복 허용X)

- 뿌리기 금액의 분배는 난수를 통해 분배(1원부터 총금액중에서 난수.. 1원부터 (총금액 -앞의 금액..)의 난수..) 형태로 로직 구성 (총합은 원래 금액과 일치하도록 구성)

- 제약 조건에 대한 모든 케이스가 포함된 테스트 코드 작성 (JUnit과 MockMvc 사용)

- 주요 Value Object는 아래처럼 구성하여 Token, Id, 시간, 금액, 만료여부, 받아간 유저/금액 등 필요한 모든 데이터 관리

```
public class SpreadVO {
	//토큰
	@Id	
	private String token;
	
	//뿌린 사람
	private String createUserId;
	
	//뿌린 방ID
	private String roomId;
	
	//뿌린 시간
	private Date createTime;
	
	//뿌린 금액
	private int money;
	
	//분배 인원
	private int personCount;
	
	//분배 금액, 받은 사람, 받은 여부 리스트
	private List<DistributionVO> distribution;
	
	//받은 사람 리스트
	private List<String> receivedUserId;
		
}
```

```
public class DistributionVO {

	//분배 금액
	private int distributionMoney;
	
	//받은 여부
	private boolean expired;
	
	//받은 사람
	private String receivedUserId;
}
```





## 테스트 방법


### 테스트 코드를 통한 테스트

- 아래 코드를 Junit으로 실행하여 테스트

- SpreadApplicationTests.java (기본 뿌리기/받기/조회 테스트)

- ReceiveExceptionTests.java (받기 기능에 대한 제약 조건 테스트)

- SearchExceptionTests.java (조회 기능에 대한 제약 조건 테스트)

- MultipleReceiveTests.java (분배인원보다 많은 사람의 받기 요청 테스트)

### Swagger UI를 통한 테스트

- application을 실행 후, http://localhost:8080/kakaopay/swagger-ui.html 로 접속

- 아래처럼 UI를 통해 각각의 API를 테스트

![image](https://user-images.githubusercontent.com/19392516/99887493-3a497880-2c88-11eb-804e-3962b64840dc.png)

