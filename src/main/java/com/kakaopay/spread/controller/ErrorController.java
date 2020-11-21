package com.kakaopay.spread.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kakaopay.spread.exception.SpreadException;
import com.kakaopay.spread.vo.ResponseVO;

@RestControllerAdvice
public class ErrorController {

	 @ExceptionHandler(SpreadException.class)
	 @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	  public ResponseVO SpreadErrorHandler(SpreadException e) {
		 ResponseVO responseVO = new ResponseVO();
		 responseVO.setErrorCode(e.getErrorCode());
		 responseVO.setMessage(e.getMessage());
	    return responseVO;
	  }
}
