package com.tenco.bank.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tenco.bank.dto.SignInDTO;
import com.tenco.bank.dto.SignUpDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.repository.interfaces.UserRepository;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.utils.Define;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	@Autowired // 안써도 되지만 가독성을 위해
	private final UserRepository userRepository;
	@Autowired
	private final PasswordEncoder passwordEncoder;
	
	
	// insert 라서 트랜잭션 처리 필요
	@Transactional
	public void createUser(SignUpDTO dto) {
		int result = 0;
		
		try {
			// Post로 던진 Password를 인코딩한다. (암호화처리)
			String hashPwd = passwordEncoder.encode(dto.getPassword());
			// 인코딩한 Password를 dto에 다시 설정
			dto.setPassword(hashPwd);
			result =  userRepository.insert(dto.toUser());
		} catch (DataAccessException e) {
			throw new DataDeliveryException(Define.FAIL_TO_CREATE_USER, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException(Define.UNKNOWN, HttpStatus.SERVICE_UNAVAILABLE);
		}

		
		if(result != 1) {
			throw new DataDeliveryException(Define.FAIL_TO_CREATE_USER, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	// 조회 작업은 데이터베이스의 상태를 변경하지 않으므로 트랜잭션 처리가 필요없다.
	public User readUser(SignInDTO dto) {
		User userEntity = null;
		try {
			// username으로만 select 처리 변경
			userEntity = userRepository.findByUsername(dto.getUsername());
		} catch (DataAccessException e) {
			throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException(Define.UNKNOWN, HttpStatus.SERVICE_UNAVAILABLE);
		}
		if(userEntity == null) {
			throw new DataDeliveryException(Define.INVALID_INPUT, HttpStatus.BAD_REQUEST);
		}
		// passwordEncoder.matches 메서드를 사용해서 판별하여 true , false로 반환한다.
		// 포스트로 보내서 값이 담긴 Password랑 DB에 저장되어있는 암호화된 Password 비교
		boolean isPwdMatched = passwordEncoder.matches(dto.getPassword(), userEntity.getPassword());
		// 비밀번호가 일치하지않을때 false를 반환
		if(isPwdMatched == false) {
			throw new DataDeliveryException("비밀번호가 잘못되었습니다.", HttpStatus.BAD_REQUEST);
		}
		return userEntity;
	}
	
	private String[] uploadFile(MultipartFile mFile) {
		// 만약 파일 사이즈가 20MB보다 크면
		if(mFile.getSize() > Define.MAX_FILE_SIZE) {
			throw new DataDeliveryException("파일 크기는 20MB 이상 클 수 없습니다.", HttpStatus.BAD_REQUEST);
		}
		
		// 저장할 경로에 폴더가 없으면 생성하는 코드 //
		// 서버 컴퓨터에 파일을 넣을 디렉토리가 있는지 검사
		// 저장할 파일 경로 설정
		String saveDirectory = Define.UPLOAD_FILE_DERECTORY;
		File directory = new File(saveDirectory);
		System.out.println(directory+"dasdsadasasdsda");
		// 디렉토리가 없으면
		if(!directory.exists()) {
			// mkdirs - 상위 디렉 토리가 없는 경우에도 전체 경로를 생성함.
			directory.mkdirs();
		}
		
		// 파일 이름 생성 (중복 이름 예방)
		// UUID를 생성후 문자열로 변환해서 새로운 파일명 + _ + 업로드된 파일의 이름
		String uploadFileName = UUID.randomUUID() + "_" + mFile.getOriginalFilename();
		
		// 파일경로 + / + 중복명을 예방한 파일명
		String uploadPath = saveDirectory + File.separator + uploadFileName;
		// 추상적인 Path
		File destination = new File(uploadPath);
		
		try {
			// 지정된 파일 경로에 mFile을 업로드해라.
			mFile.transferTo(destination);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
			throw new DataDeliveryException("파일 업로드중에 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new String[] {mFile.getOriginalFilename(), uploadFileName};
	}
}
