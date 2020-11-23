package com.kakaopay.spread.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "뿌리기 요청")
public class RequestSpreadVO {

	@ApiModelProperty(value = "뿌릴 금액", example ="1000000", required = true)
	private int money;
	@ApiModelProperty(value = "뿌릴 인원", example ="10", required = true)
	private int personCount;
	
}
