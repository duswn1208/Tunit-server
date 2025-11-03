package com.tunit.domain.contract.exception;

public class ContractException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private static final String ERROR_MESSAGE_DEFAULT = "튜터-학생 매칭 처리 중 오류가 발생했습니다.";

    public ContractException() {
        super(ERROR_MESSAGE_DEFAULT);
    }

    public ContractException(String message) {
        super(message);
    }}
