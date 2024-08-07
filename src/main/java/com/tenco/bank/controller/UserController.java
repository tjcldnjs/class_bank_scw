package com.tenco.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tenco.bank.dto.SignInDTO;
import com.tenco.bank.dto.SignUpDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user") // 해당 클래스에 정의된 모든 메소드의 기본 URL 경로를 설정
public class UserController {

	private UserService userService;
	// final 은 한번 초기화 해줘야함.
	private final HttpSession session;

	// 생존자 주입 - @Autowired 로 대체가능
	@Autowired
	public UserController(UserService service, HttpSession session) {
		this.userService = service;
		this.session = session;
	}

	@GetMapping
	public String signUpPage() {
		// application.yml에서 설정
		// prefix : /WEB-INF/view/
		// user/signUp
		// suffix : .jsp
		return "user/signUp";
	}

	@PostMapping("/sign-up")
	public String signUpProc(SignUpDTO dto) {

		if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
			throw new DataDeliveryException("username을 입력 하세요", HttpStatus.BAD_REQUEST);
		}

		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new DataDeliveryException("password를 입력 하세요", HttpStatus.BAD_REQUEST);
		}

		if (dto.getFullname() == null || dto.getFullname().isEmpty()) {
			throw new DataDeliveryException("fullname을 입력 하세요", HttpStatus.BAD_REQUEST);
		}

		// 서비스 객체로 전달
		userService.createUser(dto);

		return "redirect:/user/sign-in";
	}

	/**
	 * 로그인 화면 요청
	 * 
	 * @return
	 */
	@GetMapping("/sign-in")
	public String signInpage() {
		// application.yml에서 설정
		// prefix : /WEB-INF/view/
		// user/signUp
		// suffix : .jsp
		return "user/singIn";
	}
	
	/**
	 * 회원가입 요청처리
	 * @return
	 */
	@PostMapping("/sign-in")
	public String signProc(SignInDTO dto) {
		// 유효성 검사
		if(dto.getUsername() == null || dto.getUsername().isEmpty()) {
			throw new DataDeliveryException("username을 입력하세요", HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new DataDeliveryException("password를 입력하세요", HttpStatus.BAD_REQUEST);
		}
		
		// 서비스 호출
		User principal = userService.readUser(dto);
		
		// 세션 메모리에 등록
		session.setAttribute("principal", principal);
		
		// TODO - 수정예정 (계좌 목록 이동페이지로)
		return "redirect:/index";
	}
	
	@GetMapping("/logout")
	public String logout() {
		// invalidate - HTTP 세션을 무효화 (세션의 모든 데이터가 제거되고 세션이 종료)
		session.invalidate();
		return "redirect:/user/sign-in";
	}
}
