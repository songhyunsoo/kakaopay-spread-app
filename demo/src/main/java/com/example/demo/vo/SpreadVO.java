package com.example.demo.vo;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class SpreadVO {

	private int createUserId;
	private String roomId;
	private Date createTime;
	private String token;
	private double money;
	private int personCount;
	private List<DistributionVO> distribution;
	
	//이건 빼도 될 거 같은데
	private List<String> receivedUserId;
		
}
