package com.tenco.bank.handler.exception;

import org.springframework.http.HttpStatus;

public class DataDeliveryException extends RuntimeException{
	
	// HttpStatus - HTTP 응답의 상태 코드를 쉽게 설정하고 관리
	private HttpStatus status;
	
	public DataDeliveryException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}

}
