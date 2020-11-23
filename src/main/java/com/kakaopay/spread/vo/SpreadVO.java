package com.kakaopay.spread.vo;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
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
