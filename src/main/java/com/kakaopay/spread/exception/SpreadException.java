package com.kakaopay.spread.exception;

import lombok.Data;

@Data
@SuppressWarnings("serial")
public class SpreadException extends Exception {

	int errorCode;
	String message;
	public SpreadException(int errorCode, String message)
	{
		super(message);
		this.errorCode = errorCode;
		this.message = message;
		
	}
}
