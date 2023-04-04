// package com.flw.moka;

// @RunWith(MockitoJUnitRunner.class)
// public class RefundsUtilTest {

// @Mock
// private RefundsService refundsService;

// @Mock
// private LogsUtil logsUtil;

// @InjectMocks
// private RefundsUtil refundsUtil;

// @Test
// public void testSaveRefundToDataBase() {
// // create test data
// ProxyResponse proxyResponse = new ProxyResponse();
// proxyResponse.setCode("RR");
// proxyResponse.setMessage("Refund request successful");
// proxyResponse.setProvider("MOKA");

// Refunds refund = new Refunds();
// Transaction transaction = new Transaction();
// transaction.setAmount(5000L);
// transaction.setCurrency("TRY");
// transaction.setMask("123456******7890");
// transaction.setTransactionReference("12345");
// transaction.setExternalReference("67890");

// // call the method to be tested
// refundsUtil.saveRefundToDataBase(proxyResponse, refund, transaction);

// // verify that the saveRefund method of the RefundsService was called with
// the
// // correct argument
// verify(refundsService).saveRefund(refund);
// }

// @Test
// public void testCheckIfRefundExistInDB() {
// // create test data
// ProductRequest productRequest = new ProductRequest();
// productRequest.setTransactionReference("12345");
// productRequest.setAmount(2000L);

// Refunds refund = new Refunds();
// refund.setRefundedAmount(3000L);
// refund.setBalance(1000L);
// refund.setResponseCode("01");

// Optional<Refunds> optionalRefund = Optional.of(refund);

// // mock the behavior of the refundsService.getRefund method
// when(refundsService.getRefund("12345")).thenReturn(optionalRefund);

// // call the method to be tested
// Refunds result = refundsUtil.checkIfRefundExistInDB(productRequest, "POST");

// // verify that the getRefund method of the RefundsService was called with the
// // correct argument
// verify(refundsService).getRefund("12345");

// // verify that the refund object was updated correctly
// assertEquals(5000L, result.getRefundedAmount().longValue());
// assertNull(result.getBalance());
// }

// @Test
// public void testPrepareResponseIfTransactionDoesNotExist() {
// // call the static method to be tested
// ProxyResponse result =
// RefundsUtil.prepareResponseIfTransactionDoesNotExist("12345", "GET");

// // verify that the proxyResponse object was created correctly
// assertEquals("RR", result.getCode());
// assertEquals("MOKA", result.getProvider());
// }

// }

// "transactionReference": "TURQA66GRRJD2J23129J5771N7575E47",
//    "amount": 100,
//    "currency": "TL"
