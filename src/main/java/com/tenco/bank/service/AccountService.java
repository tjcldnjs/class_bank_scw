package com.tenco.bank.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.DepositDTO;
import com.tenco.bank.dto.SaveDTO;
import com.tenco.bank.dto.TransferDTO;
import com.tenco.bank.dto.WithdrawalDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.repository.interfaces.AccountRepository;
import com.tenco.bank.repository.interfaces.HistoryRepository;
import com.tenco.bank.repository.model.Account;
import com.tenco.bank.repository.model.History;
import com.tenco.bank.repository.model.HistoryAccount;
import com.tenco.bank.utils.Define;

@Service
public class AccountService {

	// final - 초기화 한번 해주기
	private final AccountRepository accountRepository;
	private final HistoryRepository historyRepository;
	
	public AccountService(AccountRepository accountRepository, HistoryRepository historyRepository) {
		this.accountRepository = accountRepository;
		this.historyRepository = historyRepository;
	}
	
	// 계좌 생성기능
	// insert 라서 트랜잭션 처리 필요
	@Transactional
	public void createAccount(SaveDTO dto, Integer principalId) {
		
		int result = 0;
		
		try {
			result = accountRepository.insert(dto.toAccount(principalId));
		} catch (DataAccessException e) {
			throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException(Define.UNKNOWN, HttpStatus.SERVICE_UNAVAILABLE);
		}
		
		// insert 동작 안할때
		if (result == 0) {
			throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * 계좌 목록 기능
	 * @param userId
	 * @return
	 */
	public List<Account> readAccountListByUserId(Integer userId) {
		List<Account> accountListEntity = null;
		
		try {
			accountListEntity = accountRepository.findByUserId(userId);
		} catch (DataAccessException e) {
			throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException(Define.UNKNOWN, HttpStatus.SERVICE_UNAVAILABLE);
		}
		return accountListEntity;
	}
	
	/**
	 * 출금 기능
	 * @param dto
	 * @param principalId
	 */
	@Transactional
	public void updateAccountWithdraw(WithdrawalDTO dto, Integer principalId) {
		Account accountEntity = accountRepository.findByNumber(dto.getWAccountNumber());
		// 1. 계좌 존재 여부 확인
		if (accountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.BAD_REQUEST);
		}
		// 2. 본인 계좌 여부 확인
		accountEntity.checkOwner(principalId);
		
		// 3. 계좌 비밀번호 확인
		accountEntity.checkPassword(dto.getWAccountPassword());
		
		// 4. 잔액 여부 확인
		accountEntity.checkBalance(dto.getAmount());
		
		// 5. 출금 처리 -- update 처리 필요
		accountEntity.withdraw(dto.getAmount());
		// update 처리
		accountRepository.updateById(accountEntity);
		
		// 6. 거래 내역 등록
		History history = new History();
		history.setAmount(dto.getAmount());
		history.setWBalance(accountEntity.getBalance());
		history.setDBalance(null);
		history.setWAccountId(accountEntity.getId());
		history.setDAccountId(null);
		
		int rowResultCount = historyRepository.insert(history);
		if (rowResultCount != 1) {
			throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 입금 기능
	 * @param dto
	 * @param principalId
	 */
	@Transactional
	public void updateAccountDeposit(DepositDTO dto, Integer principalId) {
		// 입금 기능 만들기
		Account accountEntity = accountRepository.findByNumber(dto.getDAccountNumber());
		// 계좌 존재 여부 확인
		if (accountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.BAD_REQUEST);
		}
		// 본인 계좌 여부 확인
		accountEntity.checkOwner(principalId);
		
		// 입금 처리 -- update 처리
		accountEntity.deposit(dto.getAmount());
		accountRepository.updateById(accountEntity);
		
		// 거래 내역 등록
		int rowResultCount = historyRepository.insert(History.builder()
                   .amount(dto.getAmount())
                   .dBalance(accountEntity.getBalance())
                   .dAccountId(accountEntity.getId())
                   .build());
		
		if(rowResultCount != 1) {
			throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		   
	}
	
	/**
	 * 이체 기능 
	 * @param dto
	 * @param principalId
	 */
	@Transactional
	public void updateAccountTransfer(TransferDTO dto, Integer principalId) {
		// 이체 기능 만들기
		Account wAccountEntity = accountRepository.findByNumber(dto.getWAccountNumber());
		Account dAccountEntity = accountRepository.findByNumber(dto.getDAccountNumber());
		// 출금 계좌 존재 여부 확인
		if (wAccountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.BAD_REQUEST);
		}
		// 입금 계좌 존재 여부 확인
		if (dAccountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.BAD_REQUEST);
		}
		// 출금 계좌 본인 소유 확인
		wAccountEntity.checkOwner(principalId);
		// 출금 계좌 비밀 번호 확인
		wAccountEntity.checkPassword(dto.getPassword());
		// 출금 계좌 잔액 여부 확인
		wAccountEntity.checkBalance(dto.getAmount());
		// 입금 계좌 객체 상태값 변경 처리
		dAccountEntity.deposit(dto.getAmount());
		// 입금 계좌 update 처리
		accountRepository.updateById(dAccountEntity);
		// 출금 계좌 객체 상태값 변경 처리
		wAccountEntity.withdraw(dto.getAmount());
		// 출금 계좌 update 처리
		accountRepository.updateById(wAccountEntity);
		// 거래 내역 등록 처리
		int rowResultCount = historyRepository.insert(History.builder()
															.amount(dto.getAmount())
															.wBalance(wAccountEntity.getBalance())
															.dBalance(dAccountEntity.getBalance())
															.wAccountId(wAccountEntity.getId())
															.dAccountId(dAccountEntity.getId())
															.build());
		if (rowResultCount != 1) {
			throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * 단일 계좌 조회 기능
	 * @param accountId
	 * @return
	 */
	public Account readAccountById(Integer accountId) {
		Account accountEntity = accountRepository.finndByAccountId(accountId);
		if(accountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return accountEntity;
	}
	
	/**
	 * 단일 계좌 거래 내역 조회
	 * @param type 
	 * @param accountId
	 * @return 전체, 입금, 출금 거래 내역 반환
	 */
	public List<HistoryAccount> readHistoryByAccountId(String type, Integer accountId, int page, int size) {
		List<HistoryAccount> list = new ArrayList<>();
		int limit = size;
		int offset = (page - 1) * size;
		list = historyRepository.findByAccountIdAndTypeOfHistory(type, accountId, limit, offset);
		return list;
	}
	
	// type 별 전체, 입금, 출금 내역 개수 구하기
	public int countHistoryByAccountIdAndType(String type, Integer accountId) {
		return historyRepository.countByAccountIdAndType(type, accountId);
	}
}
