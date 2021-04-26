package com.assignment.accountservice.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.assignment.accountservice.model.AccountRequestModel;
import com.assignment.accountservice.model.TransactionRequestModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class TestUtil {

	private TestUtil() {
		//Private Constructor 
	}
	
	private static final String SERVER_HOST = "http://localhost:";
	private static final String SLASH = "/";
	private static final String ESTONIA = "ESTONIA";
	private static final Double Double_100 = 100.0;
	private static final String TEST_DEPOSIT = "Test Deposit";
	private static final String USD = "USD";
	private static final String INR = "INR";
	private static final Double DOUBLE_0_1 = 0.1;
	private static final Double DOUBLE_MINUS_10 = -10.0;
	private static final String EMPTY_STRING = "";
	private static final String CREDIT = "Credit";
	private static final Long LONG_19 = 19L;
	private static final Integer INT_0 = 0;
	private static final Integer INT_10 = 10;
	private static final Integer INT_20 = 20;
	

	public static String getHttpUrl(int serverPort, String version, String getAccountUrl) {		
		return SERVER_HOST+serverPort+SLASH+version+getAccountUrl;
	}

	public static String getValidAccountRequest() throws JsonProcessingException {
		return getAccountRequestModel(Constants.ACCEPTED_CURRENCIES);
	}
    
	public static String getInvalidAccountRequest() throws JsonProcessingException {
		return getAccountRequestModel(Arrays.asList(INR));
	}
	
	private static String getAccountRequestModel(List<String> currencies) throws JsonProcessingException {
		AccountRequestModel accountRequestModel = new AccountRequestModel();
		accountRequestModel.setCountry(ESTONIA);
		accountRequestModel.setCurrencies(currencies);
		accountRequestModel.setCustomerId(UUID.randomUUID().toString());
		return converObjToString(accountRequestModel);
	}

	public static HttpHeaders getPostRequestHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
		headers.setContentType(MediaType.APPLICATION_JSON);
		return headers;
	}

	public static String getValidTransactionRequest() throws JsonProcessingException {
		return converObjToString(getTransactionRequestObject());
	}

	private static TransactionRequestModel getTransactionRequestObject() {
		TransactionRequestModel transactionRequestModel = new TransactionRequestModel();
		transactionRequestModel.setAccountId(LONG_19);
		transactionRequestModel.setAmount(Double_100);
		transactionRequestModel.setCurrency(USD);
		transactionRequestModel.setDescription(TEST_DEPOSIT);
		transactionRequestModel.setDirectionOfTransaction(Constants.IN);
		return transactionRequestModel;
	}

	public static String getInvalidTransactionRequestInsufficientFund() throws JsonProcessingException {
		TransactionRequestModel transactionRequestModel = getTransactionRequestObject();
		transactionRequestModel.setAmount(Double.MAX_VALUE);
		transactionRequestModel.setDirectionOfTransaction(Constants.OUT);
		return converObjToString(transactionRequestModel);
	}
	
	public static String getInvalidTransactionRequestInvalidCurrency() throws JsonProcessingException {
		TransactionRequestModel transactionRequestModel = getTransactionRequestObject();
		transactionRequestModel.setCurrency(INR);
		return converObjToString(transactionRequestModel);
	}
	
	public static String getValidOUTTransactionRequest() throws JsonProcessingException {
		TransactionRequestModel transactionRequestModel = getTransactionRequestObject();
		transactionRequestModel.setDirectionOfTransaction(Constants.OUT);
		transactionRequestModel.setAmount(DOUBLE_0_1);
		return converObjToString(transactionRequestModel);
	}
	
	public static String getInvalidTransactionRequestInvalidAccount() throws JsonProcessingException {
		TransactionRequestModel transactionRequestModel = getTransactionRequestObject();
		transactionRequestModel.setAccountId(Long.MAX_VALUE);
		return converObjToString(transactionRequestModel);
	}
	
	public static String getInvalidTransactionRequestInvalidAmount() throws JsonProcessingException {
		TransactionRequestModel transactionRequestModel = getTransactionRequestObject();
		transactionRequestModel.setAmount(DOUBLE_MINUS_10);
		return converObjToString(transactionRequestModel);
	}
	
	public static String getInvalidTransactionRequestInvalidDescription() throws JsonProcessingException {
		TransactionRequestModel transactionRequestModel = getTransactionRequestObject();
		transactionRequestModel.setDescription(EMPTY_STRING);
		return converObjToString(transactionRequestModel);
	}
	
	public static String getInvalidTransactionRequestInvalidDirection() throws JsonProcessingException {
		TransactionRequestModel transactionRequestModel = getTransactionRequestObject();
		transactionRequestModel.setDirectionOfTransaction(CREDIT);
		return converObjToString(transactionRequestModel);
	}
	
	private static String converObjToString(Object obj) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(obj);
	}
    
	/*
	 * Returns create account load test requests
	 */
	public static List<String> getValidAccountRequestList() throws JsonProcessingException {
		List<String> validAccounts = new ArrayList<>();
		for(int i = INT_0; i<INT_20; i++) {
			validAccounts.add(getValidAccountRequest());
		}
		return validAccounts;
	}

	public static List<String> getValidTransactionRequestLoadTest() throws JsonProcessingException {
		List<String> validAccounts = new ArrayList<>();
		for(int i = INT_0; i<INT_20 ; i++) {
			if(i<INT_10) {
				validAccounts.add(getValidTransactionRequest());
			} else {
				validAccounts.add(getValidOUTTransactionRequest());
			}
		}
		return validAccounts;
	}
}