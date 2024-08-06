package com.tenco.bank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.handler.exception.UnAuthorizedException;

@Controller  // Ioc 대상 (싱글톤 패턴 관리가 된다) -- 제어의 역전
// 웹 요청을 처리하고 비즈니스 로직을 호출하고 적절한 뷰를 반환하는 역할
public class MainController {
	
	// REST API 기반으로 주소설계 가능
	// 주소설계 - http://localhost:8080/main-page
	@GetMapping({"/main-page", "/index"})
	@ResponseBody // 메소드가 반환하는 값을 HTTP 응답 본문으로 직접 반환
	
	public String mainPage() {
		System.out.println("mainPage() 호출 확인");
		return "/main";
	}
	
	@GetMapping("/error-test1")
	public String errorPage() {
		if(true) {
			throw new RedirectException("잘못된 요청입니다.", HttpStatus.NOT_FOUND);
		}
		return "main";
	}
	
	@GetMapping("/error-test2")
	public String errorData2() {
		if(true) {
			throw new DataDeliveryException("잘못된 데이터 입니다", HttpStatus.BAD_REQUEST);
		}
		return "main";
	}
	
	@GetMapping("error-test3")
	public String errorData3() {
		if(true) {
			throw new UnAuthorizedException("인증 안된 사용자 입니다.", HttpStatus.UNAUTHORIZED);
		}
		return "main";
	}
	
}
