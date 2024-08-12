package com.tenco.bank.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tenco.bank.dto.DepositDTO;
import com.tenco.bank.dto.SaveDTO;
import com.tenco.bank.dto.TransferDTO;
import com.tenco.bank.dto.WithdrawalDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.UnAuthorizedException;
import com.tenco.bank.repository.model.Account;
import com.tenco.bank.repository.model.HistoryAccount;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.service.AccountService;
import com.tenco.bank.utils.Define;

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
	@GetMapping("/save")
	public String savePage() {

		// 인증검사 필요
		// UserController 에서 담은 principal
		User principal = (User) session.getAttribute("principal");
		if (principal == null) {
			throw new UnAuthorizedException("인증된 사용자가 아닙니다.", HttpStatus.UNAUTHORIZED);
		}
		return "account/save";
	}

	// 계좌 생성 기능 요청
	@PostMapping("/save")
	public String saveProc(SaveDTO dto) {
		// 1. form 데이터 추출

		// 2. 인증검사
		User principal = (User) session.getAttribute("principal");
		if (principal == null) {
			throw new UnAuthorizedException("인증된 사용자가 아닙니다.", HttpStatus.UNAUTHORIZED);
		}

		// 3. 유효성 검사
		if (dto.getNumber() == null || dto.getNumber().isEmpty()) {
			throw new DataDeliveryException("계좌번호를 입력하세요.", HttpStatus.BAD_REQUEST);
		}
		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new DataDeliveryException("계좌 비밀번호를 입력하세요.", HttpStatus.BAD_REQUEST);
		}
		if (dto.getBalance() == null || dto.getBalance() < 0) {
			throw new DataDeliveryException("계좌 잔액을 입력하세요.", HttpStatus.BAD_REQUEST);
		}

		// 4. 서비스 호출
		accountService.createAccount(dto, principal.getId());

		// 추후 계좌 목록 페이지 이동 처리
		return "redirect:/account/list";
	}

	/**
	 * 계좌 목록 화면 요청
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/list")
	// Model - jsp에 데이터를 쉽게 넘겨 줄수있다.
	public String listPage(Model model) {
		// 인증 검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		if (principal == null) {
			throw new UnAuthorizedException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.UNAUTHORIZED);
		}
		// 서비스 호출
		List<Account> accountList = accountService.readAccountListByUserId(principal.getId());

		// 계좌가 하나도 없을 때
		if (accountList.isEmpty()) {
			model.addAttribute("accountList", null);
			// 계좌가 있을 때
		} else {
			model.addAttribute("accountList", accountList);
		}
		return "account/list";
	}

	/**
	 * 출금 페이지 요청
	 * 
	 * @return
	 */
	@GetMapping("/withdrawal")
	public String withdrawalPage() {
		// 인증 검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		if (principal == null) {
			throw new UnAuthorizedException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.UNAUTHORIZED);
		}
		return "account/withdrawal";
	}

	/**
	 * 출금 처리 기능
	 * 
	 * @param dto
	 * @return
	 */
	@PostMapping("/withdrawal")
	public String withdrawalProc(WithdrawalDTO dto) {

		// 인증 검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		if (principal == null) {
			throw new UnAuthorizedException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.UNAUTHORIZED);
		}

		// 유효성 검사
		if (dto.getAmount() == null) {
			throw new DataDeliveryException(Define.ENTER_YOUR_BALANCE, HttpStatus.BAD_REQUEST);
		}
		if (dto.getAmount().longValue() <= 0) {
			throw new DataDeliveryException(Define.W_BALANCE_VALUE, HttpStatus.BAD_REQUEST);
		}
		if (dto.getWAccountNumber() == null) {
			throw new DataDeliveryException(Define.ENTER_YOUR_ACCOUNT_NUMBER, HttpStatus.BAD_REQUEST);
		}
		if (dto.getWAccountPassword() == null || dto.getWAccountPassword().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_PASSWORD, HttpStatus.BAD_REQUEST);
		}

		accountService.updateAccountWithdraw(dto, principal.getId());

		return "redirect:/account/list";
	}

	/**
	 * 입금 페이지 요청
	 */
	@GetMapping("/deposit")
	public String depositPage() {
		// 인증 검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		if (principal == null) {
			throw new UnAuthorizedException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.UNAUTHORIZED);
		}
		return "account/deposit";
	}

	/**
	 * 입금 처리 기능
	 * 
	 * @param dto
	 * @return
	 */
	@PostMapping("/deposit")
	public String depositProc(DepositDTO dto) {
		// 인증 검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		if (principal == null) {
			throw new UnAuthorizedException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.UNAUTHORIZED);
		}
		// 유효성 검사
		if (dto.getAmount() == null) {
			throw new DataDeliveryException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.BAD_REQUEST);
		}
		if (dto.getAmount().longValue() <= 0) {
			throw new DataDeliveryException(Define.D_BALANCE_VALUE, HttpStatus.BAD_REQUEST);
		}
		if (dto.getDAccountNumber() == null || dto.getDAccountNumber().trim().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_ACCOUNT_NUMBER, HttpStatus.BAD_REQUEST);
		}

		accountService.updateAccountDeposit(dto, principal.getId());

		return "redirect:/account/list";
	}

	/**
	 * 이체 페이지 요청
	 * 
	 * @return
	 */
	@GetMapping("/transfer")
	public String transferPage() {
		// 인증 검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		if (principal == null) {
			throw new UnAuthorizedException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.UNAUTHORIZED);
		}
		return "account/transfer";
	}

	/**
	 * 이체 처리 기능
	 * @param dto
	 * @return
	 */
	@PostMapping("/transfer")
	public String transferProc(TransferDTO dto) {
		// 인증 검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		if (principal == null) {
			throw new UnAuthorizedException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.UNAUTHORIZED);
		}
		// 유효성 검사
		if (dto.getAmount() == null) {
			throw new DataDeliveryException(Define.ENTER_YOUR_BALANCE, HttpStatus.BAD_REQUEST);
		}
		if (dto.getAmount().longValue() <= 0) {
			throw new DataDeliveryException(Define.D_BALANCE_VALUE, HttpStatus.BAD_REQUEST);
		}
		if (dto.getDAccountNumber() == null || dto.getDAccountNumber().trim().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_ACCOUNT_NUMBER, HttpStatus.BAD_REQUEST);
		}
		if (dto.getWAccountNumber() == null || dto.getWAccountNumber().trim().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_ACCOUNT_NUMBER, HttpStatus.BAD_REQUEST);
		}
		if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_PASSWORD, HttpStatus.BAD_REQUEST);
		}
		
		accountService.updateAccountTransfer(dto,principal.getId());
		
		return "redirect:/account/list";
	}
	name 이랑 defalutValue 아직 잘 모르겠습니다...    name은 쿼리문뒤에 이름 설정해주는거고 defalutValue는 값을 따로 안넣어 줄때? defalutValue에 넣은 기본값으로 작동 하는건가요 ?
	
	@GetMapping("/detail/{accountId}")
	public String detail(@PathVariable(name = "accountId") Integer accountId,
				@RequestParam(required = false, name = "type") String type,
				@RequestParam(name = "page", defaultValue = "1") int page,
				@RequestParam(name = "size", defaultValue = "2") int size,
				Model model) {
		// required = false 일 경우 쿼리스트링 없어도 null 로 반환된다.  true 일 경우 쿼리스트링 없으면 오류 뜬다.
		
		// 인증 검사
		User principal = (User) session.getAttribute(Define.PRINCIPAL);
		if (principal == null) {
			throw new UnAuthorizedException(Define.NOT_AN_AUTHENTICATED_USER, HttpStatus.UNAUTHORIZED);
		}
		
		// 유효성 검사
		// Arrays.asList - 배열을 리스트로 변환하는 메소드
		List<String> validTypes = Arrays.asList("all", "deposit", "withdrawal");
		
		// 리스트 validTypes 에 type이 포함 안되어있을때 
		if(!validTypes.contains(type)) {
			throw new DataDeliveryException("유효하지 않은 접근입니다", HttpStatus.BAD_REQUEST);
		}
		
		// 페이지 개수를 계산하기 위해서 총페이지 수를 계산해주어야 한다.
		int totalRecords = accountService.countHistoryByAccountIdAndType(type, accountId);
		// 소수점올림
		int totalPages = (int) Math.ceil((double)totalRecords / size);
		
		// 단일 계좌 조회
		Account account = accountService.readAccountById(accountId);
		// 단일 계좌 거래 내역 조회
		List<HistoryAccount> historyList = accountService.readHistoryByAccountId(type, accountId, page, size);
		
		// JSP로 사용할수 있게 보내주기
		model.addAttribute("account",account);
		model.addAttribute("historyList",historyList);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages",totalPages);
		model.addAttribute("type",type);
		model.addAttribute("size",size);
		
		return "account/detail";
	}
	
}
