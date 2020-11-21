package com.kakaopay.spread.vo;

import java.util.List;

import lombok.Data;

@Data
public class ResponseSpreadVO {

	private String createTime;
	
	private int spreadMoney;
	
	private int completedReceivingMoney;
	
	private List<CompletedRecevingInfoVO> CompletedRecevingInfoList;
	 
}
