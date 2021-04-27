package com.assignment.accountservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.assignment.accountservice.model.AccountResponseModel;
import com.assignment.accountservice.model.TransactionResponseModel;
import com.assignment.accountservice.util.Constants;
import com.assignment.accountservice.util.ErrorCode;
import com.assignment.accountservice.util.TestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AccountServiceApplicationTest {
	
	@LocalServerPort
	private int serverPort;
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Test
	public void createAccountTest() throws JsonProcessingException {
		String url = TestUtil.getHttpUrl(serverPort,Constants.VERSION,Constants.CREATE_ACCOUNT_URL);
		UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();
		String createAccountRequest = TestUtil.getValidAccountRequest();
		HttpHeaders headers = TestUtil.getPostRequestHeaders();
		HttpEntity<String> requestEntity = new HttpEntity<String>(createAccountRequest, headers);
		ResponseEntity<AccountResponseModel> response = testRestTemplate.exchange(uri.toString(), HttpMethod.POST, requestEntity,AccountResponseModel.class);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertNotNull(response.getBody().getAccountId());
	}
	
	@Test
	public void fetchByAccountIdTest() {
		
		String url = TestUtil.getHttpUrl(serverPort,Constants.VERSION,Constants.GET_ACCOUNT_URL.replace("{accountId}","20"));
		UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();
		HttpEntity<String> requestEntity = new HttpEntity<String>(null, null);
		ResponseEntity<AccountResponseModel> response = testRestTemplate.exchange(uri.toString(), HttpMethod.GET, requestEntity,AccountResponseModel.class);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(20, response.getBody().getAccountId());
	}
	
	@Test
	public void createINTransactionTest() throws JsonProcessingException {
		String url = TestUtil.getHttpUrl(serverPort,Constants.VERSION,Constants.CREATE_TRANSACTION_URL);
		UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();
		String createAccountRequest = TestUtil.getValidTransactionRequest();
		HttpHeaders headers = TestUtil.getPostRequestHeaders();
		HttpEntity<String> requestEntity = new HttpEntity<String>(createAccountRequest, headers);
		ResponseEntity<TransactionResponseModel> response = testRestTemplate.exchange(uri.toString(), HttpMethod.POST, requestEntity,TransactionResponseModel.class);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertNotNull(response.getBody().getTransactionId());
	}
	
	@Test
	public void createOUTTransactionTest() throws JsonProcessingException {
		String url = TestUtil.getHttpUrl(serverPort,Constants.VERSION,Constants.CREATE_TRANSACTION_URL);
		UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();
		String createAccountRequest = TestUtil.getValidOUTTransactionRequest();
		HttpHeaders headers = TestUtil.getPostRequestHeaders();
		HttpEntity<String> requestEntity = new HttpEntity<String>(createAccountRequest, headers);
		ResponseEntity<TransactionResponseModel> response = testRestTemplate.exchange(uri.toString(), HttpMethod.POST, requestEntity,TransactionResponseModel.class);
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertNotNull(response.getBody().getTransactionId());
	}
	
	@Test
	public void fetchTransactionByAccountIdTest() {
		
		String url = TestUtil.getHttpUrl(serverPort,Constants.VERSION,Constants.GET_TRANSACTION_URL.replace("{accountId}","19"));
		UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();
		HttpEntity<String> requestEntity = new HttpEntity<String>(null, null);
		ResponseEntity<List<TransactionResponseModel>> response = testRestTemplate.exchange(uri.toString(), HttpMethod.GET, requestEntity,new ParameterizedTypeReference<List<TransactionResponseModel>>() {
		});
		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertNotNull(response.getBody());
		assertEquals(19,response.getBody().stream().findAny().orElseThrow(null).getAccountId());
		
	}
	
	@Test
	public void createAccountFailure() throws JsonProcessingException {
		String url = TestUtil.getHttpUrl(serverPort,Constants.VERSION,Constants.CREATE_ACCOUNT_URL);
		UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();
		String createInvalidAccountRequest = TestUtil.getInvalidAccountRequest();
		HttpHeaders headers = TestUtil.getPostRequestHeaders();
		HttpEntity<String> requestEntity = new HttpEntity<String>(createInvalidAccountRequest, headers);
		ResponseEntity<String> response = testRestTemplate.exchange(uri.toString(), HttpMethod.POST, requestEntity,String.class);
		assertNotNull(response);
		assertEquals(ErrorCode.INVALID_CURRENCY.getErrorStatus(), response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(Constants.INVALID_CURRENCY.equalsIgnoreCase(response.getBody()));
	
	}
	
	@Test
	public void fetchByAccountIdAccountNotExistTest() {
		
		String url = TestUtil.getHttpUrl(serverPort,Constants.VERSION,Constants.GET_ACCOUNT_URL.replace("{accountId}","0"));
		UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();
		HttpEntity<String> requestEntity = new HttpEntity<String>(null, null);
		ResponseEntity<String> response = testRestTemplate.exchange(uri.toString(), HttpMethod.GET, requestEntity,String.class);
		assertNotNull(response);
		assertEquals(ErrorCode.ACCOUNT_NOT_FOUND.getErrorStatus(), response.getStatusCode());
	}
	
	@Test
	public void createTransactionInsufficientFundTest() throws JsonProcessingException {
		String url = TestUtil.getHttpUrl(serverPort,Constants.VERSION,Constants.CREATE_TRANSACTION_URL);
		UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();
		String createAccountRequest = TestUtil.getInvalidTransactionRequestInsufficientFund();
		HttpHeaders headers = TestUtil.getPostRequestHeaders();
		HttpEntity<String> requestEntity = new HttpEntity<String>(createAccountRequest, headers);
		ResponseEntity<String> response = testRestTemplate.exchange(uri.toString(), HttpMethod.POST, requestEntity,String.class);
		assertNotNull(response);
		assertEquals(ErrorCode.INSUFFICIENT_FUNDS.getErrorStatus(), response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(Constants.INSUFFICIENT_FUNDS.equalsIgnoreCase(response.getBody()));
	}
	
	@Test
	public void createTransactionInvalidCurrencyTest() throws JsonProcessingException {
		String url = TestUtil.getHttpUrl(serverPort,Constants.VERSION,Constants.CREATE_TRANSACTION_URL);
		UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();
		String createAccountRequest = TestUtil.getInvalidTransactionRequestInvalidCurrency();
		HttpHeaders headers = TestUtil.getPostRequestHeaders();
		HttpEntity<String> requestEntity = new HttpEntity<String>(createAccountRequest, headers);
		ResponseEntity<String> response = testRestTemplate.exchange(uri.toString(), HttpMethod.POST, requestEntity,String.class);
		assertNotNull(response);
		assertEquals(ErrorCode.INVALID_CURRENCY.getErrorStatus(), response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(Constants.INVALID_CURRENCY.equalsIgnoreCase(response.getBody()));
	}
	
	@Test
	public void createTransactionInvalidAccountTest() throws JsonProcessingException {
		String url = TestUtil.getHttpUrl(serverPort,Constants.VERSION,Constants.CREATE_TRANSACTION_URL);
		UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();
		String createAccountRequest = TestUtil.getInvalidTransactionRequestInvalidAccount();
		HttpHeaders headers = TestUtil.getPostRequestHeaders();
		HttpEntity<String> requestEntity = new HttpEntity<String>(createAccountRequest, headers);
		ResponseEntity<String> response = testRestTemplate.exchange(uri.toString(), HttpMethod.POST, requestEntity,String.class);
		assertNotNull(response);
		assertEquals(ErrorCode.ACCOUNT_NOT_FOUND.getErrorStatus(), response.getStatusCode());
		assertEquals(Constants.ACCOUNT_MISSING,response.getBody());
	}
	
	@Test
	public void createTransactionInvalidAmountTest() throws JsonProcessingException {
		String url = TestUtil.getHttpUrl(serverPort,Constants.VERSION,Constants.CREATE_TRANSACTION_URL);
		UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();
		String createAccountRequest = TestUtil.getInvalidTransactionRequestInvalidAmount();
		HttpHeaders headers = TestUtil.getPostRequestHeaders();
		HttpEntity<String> requestEntity = new HttpEntity<String>(createAccountRequest, headers);
		ResponseEntity<String> response = testRestTemplate.exchange(uri.toString(), HttpMethod.POST, requestEntity,String.class);
		assertNotNull(response);
		assertEquals(ErrorCode.INVALID_AMOUNT.getErrorStatus(), response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(Constants.INVALID_AMOUNT.equalsIgnoreCase(response.getBody()));
	}
	
	@Test
	public void createTransactionInvalidDescriptionTest() throws JsonProcessingException {
		String url = TestUtil.getHttpUrl(serverPort,Constants.VERSION,Constants.CREATE_TRANSACTION_URL);
		UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();
		String createAccountRequest = TestUtil.getInvalidTransactionRequestInvalidDescription();
		HttpHeaders headers = TestUtil.getPostRequestHeaders();
		HttpEntity<String> requestEntity = new HttpEntity<String>(createAccountRequest, headers);
		ResponseEntity<String> response = testRestTemplate.exchange(uri.toString(), HttpMethod.POST, requestEntity,String.class);
		assertNotNull(response);
		assertEquals(ErrorCode.DESCRIPTION_MISSING.getErrorStatus(), response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(Constants.DESCRIPTION_MISSING.equalsIgnoreCase(response.getBody()));
	}
	
	@Test
	public void createTransactionInvalidDirectionTest() throws JsonProcessingException {
		String url = TestUtil.getHttpUrl(serverPort,Constants.VERSION,Constants.CREATE_TRANSACTION_URL);
		UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();
		String createAccountRequest = TestUtil.getInvalidTransactionRequestInvalidDirection();
		HttpHeaders headers = TestUtil.getPostRequestHeaders();
		HttpEntity<String> requestEntity = new HttpEntity<String>(createAccountRequest, headers);
		ResponseEntity<String> response = testRestTemplate.exchange(uri.toString(), HttpMethod.POST, requestEntity,String.class);
		assertNotNull(response);
		assertEquals(ErrorCode.INVALID_TRANSACTION_DIRECTION.getErrorStatus(), response.getStatusCode());
		assertNotNull(response.getBody());
		assertTrue(Constants.INVALID_TRANSACTION_DIRECTION.equalsIgnoreCase(response.getBody()));
	}
	
	/*
	 * Create account load test
	 */
	@Disabled
	public void createAccountLoadTest() throws JsonProcessingException {
		String url = TestUtil.getHttpUrl(serverPort,Constants.VERSION,Constants.CREATE_ACCOUNT_URL);
		UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();
		List<String> createAccountRequests = TestUtil.getValidAccountRequestList();
		System.out.println(LocalDateTime.now());
		for(String createAccountRequest: createAccountRequests) {
			HttpHeaders headers = TestUtil.getPostRequestHeaders();
			HttpEntity<String> requestEntity = new HttpEntity<String>(createAccountRequest, headers);
			ResponseEntity<AccountResponseModel> response = testRestTemplate.exchange(uri.toString(), HttpMethod.POST, requestEntity,AccountResponseModel.class);
			assertEquals(HttpStatus.OK, response.getStatusCode());
		}
		System.out.println(LocalDateTime.now());
	}
	
	@Disabled
	public void createTransactionLoadTest() throws JsonProcessingException {
		String url = TestUtil.getHttpUrl(serverPort,Constants.VERSION,Constants.CREATE_TRANSACTION_URL);
		UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).build();
		List<String> createAccountRequests = TestUtil.getValidTransactionRequestLoadTest();
		System.out.println(LocalDateTime.now());
		for(String createAccountRequest: createAccountRequests) {
			HttpHeaders headers = TestUtil.getPostRequestHeaders();
			HttpEntity<String> requestEntity = new HttpEntity<String>(createAccountRequest, headers);
			ResponseEntity<TransactionResponseModel> response = testRestTemplate.exchange(uri.toString(), HttpMethod.POST, requestEntity,TransactionResponseModel.class);
			assertNotNull(response);
			assertEquals(HttpStatus.OK, response.getStatusCode());
		}
		System.out.println(LocalDateTime.now());
	}

}
