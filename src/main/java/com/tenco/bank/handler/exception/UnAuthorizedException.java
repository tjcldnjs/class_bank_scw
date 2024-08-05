package com.tenco.bank.handler.exception;

import org.springframework.http.HttpStatus;

public class UnAuthorizedException extends RuntimeException {

	// HttpStatus - HTTP 응답의 상태 코드를 쉽게 설정하고 관리
	private HttpStatus status;
	
	// throw new UnAuthorizedException( , )
	public UnAuthorizedException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}
}
