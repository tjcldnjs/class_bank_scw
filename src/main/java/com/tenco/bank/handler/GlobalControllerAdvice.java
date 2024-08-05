package com.tenco.bank.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice // Ioc 대상 (싱글톤 패턴)
// 예외 처리를 전역적으로 관리할 수 있는 강력한 기능을 제공
public class GlobalControllerAdvice {
	
	@ExceptionHandler(value = Exception.class) // 예외처리를 간편하게 처리해주는 어노테이션
	
	@ResponseBody // 메소드가 반환하는 값을 HTTP 응답 본문으로 직접 반환
	// @RestController를 사용하면 @ResponseBody 어노테이션이 메소드에 필요없음
	// @RestController는 @Controller + @ResponseBody
	
	public ResponseEntity<Object> handleResourceNotFoundException(Exception e){
		System.out.println("GlobalControllerAdvice : 오류 확인 : ");
		return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
	}
}
