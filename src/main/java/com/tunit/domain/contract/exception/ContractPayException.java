package com.tunit.domain.contract.exception;

public class ContractPayException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private static final String ERROR_MESSAGE_DEFAULT = "결제 처리 중 오류가 발생했습니다.";

    public ContractPayException() {
        super(ERROR_MESSAGE_DEFAULT);
    }

    public ContractPayException(String message) {
        super(message);
    }}
