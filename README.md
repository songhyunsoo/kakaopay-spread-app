## 카카오페이 뿌리기 기능 구현하기

### 기반 기술

- 언어 : Java
- Application : Springboot
- Database: MongoDB (Embeded)
- Database는 Springboot embeded MongoDB를 사용하였습니다.

### 문제 해결 전략


- Springboot 프레임워크를 사용하여 REST API를 구현
- NoSQL 데이터베이스를 사용하여 Json 형태로 데이터 관리 (java내에서는 value object를 사용)
- Content-Type 및 Accept는 application/json를 사용하여 json 형식으로 요청/응답 
- 별도의 Exception 객체 및 ErrorController를 구현하여 제약 조건에 대해 예외 처리
- Token은 난수함수를 사용하여 랜덤 문자열을 생성(HashSet을 사용하여 중복 허용X)
- 뿌리기 금액의 분배는 난수를 통해 분배(1~총금액의 난수 -> 1~(총금액 -앞의 금액..) -> ..) 형태로 로직 구성 (총합은 원래 금액과 일치하도록 구성)
- 제약 조건에 대한 모든 케이스가 포함된 테스트 코드 작성 (JUnit과 MockMvc 사용)
-
```
public class SpreadVO {
	@Id
	private String token;
	
	private String createUserId;
	
	private String roomId;
	
	private Date createTime;
	
	private double money;
	
	private int personCount;
	
	private List<DistributionVO> distribution;
	
	private List<String> receivedUserId;
		
}
```


### 테스트 방법

## 테스트 코드를 통한 테스트


## Swagger UI를 통한 테스트 


