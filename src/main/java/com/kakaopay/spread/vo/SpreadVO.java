package com.kakaopay.spread.vo;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
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
