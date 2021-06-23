package com.ezfarm.ezfarmback.common.exception.dto;

import lombok.Getter;

@Getter
public enum ErrorCode {

    /*
    기본적인 에러
    */
    INTERNAL_SERVER_ERROR(500, "S_001", "서버에 문제가 생겼습니다."),

    INVALID_INPUT_VALUE(400, "C_001", "적절하지 않은 요청 값입니다."),
    INVALID_TYPE_VALUE(400, "C_002", "요청 값의 타입이 잘못되었습니다."),
    METHOD_NOT_ALLOWED(405, "C_003", "적절하지 않은 HTTP 메소드입니다."),
    DUPLICATED_EMAIL(400, "C_004", "이미 존재하는 이메일입니다."),
    BAD_LOGIN(400, "C_005", "잘못된 아이디 또는 패스워드입니다."),
    NON_EXISTENT_USER(404, "C_006", "존재하지 않는 사용자 입니다."),
    INVALID_FARM_START_DATE(400, "C_007", "잘못된 농장 생성일을 입력했습니다."),
    NOT_FARM_OWNER(500, "C_008", "농장에 접근 권한이 없습니다."),
    INVALID_FARM_ID(500, "C_008", "잘못된 농장 ID 입니다.");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
