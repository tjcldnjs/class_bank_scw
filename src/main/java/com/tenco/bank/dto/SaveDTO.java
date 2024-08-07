package com.tenco.bank.dto;

import com.tenco.bank.repository.model.Account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SaveDTO {
	private String number;
	private String password;
	private Long balance;
	
	// Account Object로 변환
	// 재사용성: 여러 곳에서 SaveDTO를 사용하여 Account Object로 변환할 수 있다.
	public Account toAccount(Integer userId) {
		return Account.builder()
				.number(this.number)
				.password(this.password)
				.balance(this.balance)
				.userId(userId)
				.build();
	}
}
