package com.assignment.accountservice.util;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    
	ACCOUNT_NOT_FOUND(HttpStatus.OK),
	INVALID_CURRENCY(HttpStatus.BAD_REQUEST), 
	INVALID_TRANSACTION_DIRECTION(HttpStatus.BAD_REQUEST), 
	INVALID_AMOUNT(HttpStatus.BAD_REQUEST),
	DESCRIPTION_MISSING(HttpStatus.BAD_REQUEST),
	INSUFFICIENT_FUNDS(HttpStatus.UNPROCESSABLE_ENTITY), 
	ACCOUNT_EXIST(HttpStatus.OK);
	
	private HttpStatus errorStatus;
	
	private ErrorCode(HttpStatus errorStatus) {
		
		this.errorStatus = errorStatus;
	}
	
	public HttpStatus getErrorStatus() {
		return this.errorStatus;
	}
}
