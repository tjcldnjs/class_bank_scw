package com.tenco.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tenco.bank.dto.SignUpDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.service.UserService;

@Controller
@RequestMapping("/user") // 해당 클래스에 정의된 모든 메소드의 기본 URL 경로를 설정
public class UserController {
	
	private UserService userService;
	
	// 생존자 주입 - @Autowired 로 대체가능
	@Autowired 
	public UserController(UserService service) {
		this.userService = service;
	}
	
	@GetMapping
	public String signUpPage() {
		// application.yml에서 설정
		// prefix  : /WEB-INF/view/
		// user/signUp
		// suffix : .jsp
		return "user/signUp";
	}
	
	@PostMapping("/sign-up")
	public String signUpProc(SignUpDTO dto) {
		
		if(dto.getUsername() == null || dto.getUsername().isEmpty()) {
			throw new DataDeliveryException("username을 입력 하세요", HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new DataDeliveryException("password를 입력 하세요", HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getFullname() == null || dto.getFullname().isEmpty()) {
			throw new DataDeliveryException("fullname을 입력 하세요", HttpStatus.BAD_REQUEST);
		}
		
		// 서비스 객체로 전달
		userService.createUser(dto);
		
		// TODO - 수정필요
		return null;
	}
}
