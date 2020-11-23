package com.kakaopay.spread.vo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class DistributionVO {

	//분배 금액
	private int distributionMoney;
	
	//받은 여부
	private boolean expired;
	
	//받은 사람
	private String receivedUserId;
}
