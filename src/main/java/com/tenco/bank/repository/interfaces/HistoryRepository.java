package com.tenco.bank.repository.interfaces;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tenco.bank.repository.model.History;
import com.tenco.bank.repository.model.HistoryAccount;

// HistoryRepository 인터페이스와 history.xml 파일을 매칭 시킨다.
@Mapper
public interface HistoryRepository {

	public int insert(History history);
	
	public int updateById(History history);
	
	public int deleteById(Integer id);
	
	public History findById(Integer id);
	
	public List<History> findAll();

	public List<HistoryAccount> findByAccountIdAndTypeOfHistory(@Param("type") String type,
			@Param("accountId") Integer accountId,@Param("limit") int limit,@Param("offset") int offset);
	
	public int countByAccountIdAndType(@Param("type") String type, @Param("accountId") Integer accountId);
}
