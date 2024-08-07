package com.tenco.bank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tenco.bank.dto.SaveDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.UnAuthorizedException;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.service.AccountService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/account") // 해당 클래스에 정의된 모든 메소드의 기본 URL 경로를 설정
public class AccountController { 
	
	// 계좌 생성 화면 요청 - DI 처리
	// final - 동작속도가 더 빠름, 한번 초기화 해줘야함.
	private final HttpSession session;
	private final AccountService accountService;
	
	// @Autowired - 생략가능
	public AccountController(AccountService accountService, HttpSession session) {
		this.session = session;
		this.accountService = accountService;
	}
	
	// 계좌 생성 페이지 요청
	public String savePage() {
		
		// 인증검사 필요
		// UserController 에서 담은 principal
		User principal = (User) session.getAttribute("principal");
		if(principal == null) {
			throw new UnAuthorizedException("인증된 사용자가 아닙니다.", HttpStatus.UNAUTHORIZED);
		}
		return "account/save";
	}
	
	// 계좌 생성 기능 요청
	public String saveProc(SaveDTO dto) {
		// 1. form 데이터 추출
		
		// 2. 인증검사
		User principal = (User) session.getAttribute("principal");
		if(principal == null) {
			throw new UnAuthorizedException("인증된 사용자가 아닙니다.", HttpStatus.UNAUTHORIZED);
		}
		
		// 3. 유효성 검사
		if(dto.getNumber() == null || dto.getNumber().isEmpty()) {
			throw new DataDeliveryException("계좌번호를 입력하세요.", HttpStatus.BAD_REQUEST);
		}
		if(dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new DataDeliveryException("계좌 비밀번호를 입력하세요.", HttpStatus.BAD_REQUEST);
		}
		if(dto.getBalance() == null || dto.getBalance() < 0) {
			throw new DataDeliveryException("계좌 잔액을 입력하세요.", HttpStatus.BAD_REQUEST);
		}
		
		// 4. 서비스 호출
		accountService.createAccount(dto, principal.getId());
		
		// 추후 계좌 목록 페이지 이동 처리
		return "redirect:/index";
	}

}
