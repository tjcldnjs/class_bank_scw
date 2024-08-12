package com.tenco.bank.utils;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class ValueFormatter {

	// 시간 포맷
	public String timestampToString(Timestamp timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(timestamp);
	}
	
	// 금액 포맷
	public String formatKoreanWon(Long amount) {
		DecimalFormat df = new DecimalFormat();
		String formatNumber = df.format(amount);
		return formatNumber + "원";
	}
}
