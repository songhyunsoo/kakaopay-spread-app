package com.kakaopay.spread.util;

public enum SpreadErrorCode {
	INVALID_TOKEN(1000,"유효하지 않은 토큰입니다."),
	NOT_ALLOWED_DUPLICATION_RECEIVING(1001,"뿌리기 당 한 사용자는 한번만 받을 수 있습니다."),
	NOT_ALLOWED_SELF_RECEIVING(1002,"자신이 뿌리기한 건은 자신이 받을 수 없습니다."), 
	NOT_ALLOWED_ROOM_ID_DIFFERENCE(1003, "뿌리기가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수 있습니다."),
	EXPIRE_SPREAD_TIME(1004, "뿌린 건은 10분간만 유효합니다."),
	INVALID_SEARCH_USER_ID(1005,"뿌린 사람 자신만 조회를 할 수 있습니다."),
	EXPIRE_SEARCH_TIME(1006, "뿌린 건에 대한 조회는 7일 동안 할 수 있습니다."),
	EXCEED_TOKEN_CAPACITY(1007,"더 이상 유효한 토큰을 생성할 수 없습니다."),
	NOT_FOUND_VALID_RECEIVING(1008, "더 이상 받을 유효한 분배금액이 없습니다.");
	
	
	private int code; 
	private String description; 
	private SpreadErrorCode(int code, String description) 
	{ this.code = code; this.description = description; } 
	public int getCode() { return code; } 
	public String getDescription() { return description; }

}
