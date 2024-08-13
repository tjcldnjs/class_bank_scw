package com.tenco.bank.handler;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.tenco.bank.handler.exception.UnAuthorizedException;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.utils.Define;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component // 1개의 Class 단위로 등록 할 때 사용
public class AuthInterceptor implements HandlerInterceptor {

	// 컨트롤러 들어오기전에 동작함.
	// true 일때 컨트롤러 작동 false일때 컨트롤러 작동X
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession();
		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		if (principal == null) {
			throw new UnAuthorizedException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
		}
		// throw 걸리면 밑으로 코드 안내려감.
		return true;
	}

	// postHandle 동작 흐름
	// 뷰가 렌더링 되기 바로 전에 콜백되는 메서드 // 유효성 검사할때.. ?
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}

	// afterCompletion 동작 흐름
	// 요청 처리가 완료된 후, 즉 뷰가 완전 렌더링이 된 후에 호출 된다. // 요청 처리 시간 측정 할때.. ?
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}
}
