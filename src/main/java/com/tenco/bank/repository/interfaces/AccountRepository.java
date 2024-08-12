package com.tenco.bank.repository.interfaces;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tenco.bank.repository.model.Account;

// AccountRepository 인터페이스와 account.xml 파일을 매칭 시킨다.
@Mapper 
public interface AccountRepository {
	public int insert(Account account);
	public int updateById(Account account);
	public int deleteById(Integer id);
	
	// @Param 쓰는이유
	// 다중 파라미터를 사용할때, 파라미터의 이름 지정
	public List<Account> findByUserId(@Param("userId") Integer principalId);
	public Account findByNumber(@Param("number") String id);
	
	public Account finndByAccountId(Integer accountId);
}
