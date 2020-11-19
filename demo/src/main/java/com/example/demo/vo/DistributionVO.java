package com.example.demo.vo;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class DistributionVO {

	private int distributionMoney;
	private boolean expired;
	private String receivedUserId;
}
