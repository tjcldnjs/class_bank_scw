package com.tenco.bank.dto;

import com.tenco.bank.repository.model.User;

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

public class SignInDTO {

	private String username;
	private String password;
	
	// User Object 로 반환 
	public User toUser() {
		return User.builder()
				.username(this.username)
				.password(this.password)
				.build();
	}
}
