package com.tenco.bank.repository.interfaces;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tenco.bank.repository.model.User;

// UserRepository 인터페이스와 user.xml 파일을 매칭시킨다.
@Mapper
public interface UserRepository {
	
	public int insert(User user);
	
	public int updateById(User user);
	
	public int deleteById(Integer id);
	
	public User findbyId(Integer id);
	
	public List<User> findAll();
	
	// 매개변수 2개 이상일때 - @Param 어노테이션 사용하기
	public User findByUsernameAndPassword(@Param("username") String username,@Param("password") String password);

	public User findByUsername(@Param("username") String username);
}
