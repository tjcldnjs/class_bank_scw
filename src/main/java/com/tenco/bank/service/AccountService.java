package com.tenco.bank.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.SaveDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.repository.interfaces.AccountRepository;

@Service
public class AccountService {

	// final - 초기화 한번 해주기
	private final AccountRepository accountRepository;
	
	public AccountService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}
	
	
	// 계좌 생성기능
	// insert 라서 트랜잭션 처리 필요
	@Transactional
	public void createAccount(SaveDTO dto, Integer principalId) {
		
		int result = 0;
		
		try {
			result = accountRepository.insert(dto.toAccount(principalId));
		} catch (DataDeliveryException e) {
			throw new DataDeliveryException("잘못된 요청입니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException("알 수 없는 오류입니다.", HttpStatus.SERVICE_UNAVAILABLE);
		}
		
		// insert 작동안할때
		if(result == 0) {
			throw new DataDeliveryException("정상 처리 되지 않았습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
