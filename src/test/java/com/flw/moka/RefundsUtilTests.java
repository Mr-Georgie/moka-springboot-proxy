package com.flw.moka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.flw.moka.entity.constants.Methods;
import com.flw.moka.entity.models.Refunds;
import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.utilities.entity.RefundsUtil;

public class RefundsUtilTests {

    private RefundsUtil refundsUtil;


    @BeforeEach
    public void setup() {
        refundsUtil = new RefundsUtil();
    }

    @Test
    public void testUpdateRefund_ReturnedObject(){
        // Create test data
        ProxyResponse proxyResponse = mock(ProxyResponse.class);
        Refunds refund = new Refunds();
        refund.setId(1L);

        // Call the method with null transaction
        Refunds actualRefund = refundsUtil.updateRefund(proxyResponse, refund, null);

        // Verify the result
        assertNotEquals(refund.getId(), actualRefund.getId(), "if failed, the IDs are the same which they shouldn't be");
    }

    @Test
    public void testUpdateRefund_withNullTransaction() {
        // Create test data
        ProxyResponse proxyResponse = mock(ProxyResponse.class);
        Refunds refund = new Refunds();
        refund.setRefundedAmount(50L);
        refund.setBalance(100L);

        // Call the method with null transaction
        Refunds actualRefund = refundsUtil.updateRefund(proxyResponse, refund, null);

        // Verify the result
        assertEquals(refund.getRefundedAmount(), actualRefund.getRefundedAmount());
        assertEquals(refund.getBalance(), actualRefund.getBalance());
    }

    /*
     * test update refund with failed proxy response and two scenarios:
     * 1. when refund balance is Null
     * 2. when refund balance is Non Null
     */

    @Test
    public void testUpdateRefund_withFailedProxyResponse_andRefundBalanceIsNull() {
        // Create test data
        Transaction transaction = new Transaction();
        transaction.setAmount(450L);
        ProxyResponse proxyResponse = new ProxyResponse();
        proxyResponse.setResponseCode("RR");

        // refund balance is null
        Refunds refund = new Refunds();

        // Call the method
        Refunds actualRefund = refundsUtil.updateRefund(proxyResponse, refund, transaction);

        // Verify the result
        assertEquals(transaction.getAmount(), actualRefund.getBalance());
    }

    @Test
    public void testUpdateRefund_withFailedProxyResponse_andRefundBalanceIsNonNull() {
        // Create test data
        Transaction transaction = new Transaction();
        transaction.setAmount(450L);
        ProxyResponse proxyResponse = new ProxyResponse();
        proxyResponse.setResponseCode("RR");

        // refund balance is not null
        Refunds refund = new Refunds();
        refund.setBalance(200L);

        // Call the method
        Refunds actualRefund = refundsUtil.updateRefund(proxyResponse, refund, transaction);

        // Verify the result
        assertEquals(refund.getBalance(), actualRefund.getBalance());
    }

    @Test
    public void testUpdateRefund_withFailedProxyResponse_andRefundedAmountIsNull() {
        // Create test data
        Transaction transaction = new Transaction();
        ProxyResponse proxyResponse = new ProxyResponse();
        proxyResponse.setResponseCode("RR");
        Refunds refund = new Refunds();

        // Call the method
        Refunds actualRefund = refundsUtil.updateRefund(proxyResponse, refund, transaction);

        // Verify the result
        assertEquals(actualRefund.getRefundedAmount(), (long)0);
    }

    /*
     * test update refund with successful proxy response
     */

    @Test
    public void testUpdateRefund_withSuccessfulProxyResponse_andRefundedAmountIsNull() {
        // Create test data
        Transaction transaction = new Transaction();
        transaction.setAmount(450L);
        ProxyResponse proxyResponse = new ProxyResponse();
        proxyResponse.setResponseCode("03");

        // refund amount is null
        Refunds refund = new Refunds();

        // Call the method
        Refunds actualRefund = refundsUtil.updateRefund(proxyResponse, refund, transaction);

        // Verify the result
        assertEquals(transaction.getAmount(), actualRefund.getRefundedAmount());
    }

    @Test
    public void testUpdateRefund_withSuccessfulProxyResponse_andRefundedAmountIsNonNull() {
         // Create test data
        Transaction transaction = new Transaction();
        transaction.setAmount(450L);
        ProxyResponse proxyResponse = new ProxyResponse();
        proxyResponse.setResponseCode("03");

        // refund amount is null
        Refunds refund = new Refunds();
        refund.setRefundedAmount(50L);

        // Call the method
        Refunds actualRefund = refundsUtil.updateRefund(proxyResponse, refund, transaction);

        // Verify the result
        assertEquals(refund.getRefundedAmount(), actualRefund.getRefundedAmount());
    }

    @Test
    public void testUpdateRefund_withSuccessfulProxyResponse_andRefundBalanceIsNonNullAndNotZero() {
        // Create test data
        Transaction transaction = new Transaction();
        transaction.setAmount(450L);
        ProxyResponse proxyResponse = new ProxyResponse();
        proxyResponse.setResponseCode("03");

        // refund amount is null
        Refunds refund = new Refunds();
        refund.setBalance(450L);
        refund.setRefundedAmount(50L);

        // compute expected balance
        Long balance = transaction.getAmount() - refund.getRefundedAmount();

        // Call the method
        Refunds actualRefund = refundsUtil.updateRefund(proxyResponse, refund, transaction);

        // Verify the result
        assertEquals(balance, actualRefund.getBalance());
    }

    @Test
    public void testUpdateRefund_withSuccessfulProxyResponse_andRefundBalanceIsNull() {
        // Create test data
        Transaction transaction = new Transaction();
        transaction.setAmount(450L);
        ProxyResponse proxyResponse = new ProxyResponse();
        proxyResponse.setResponseCode("03");

        // refund amount is null
        Refunds refund = new Refunds();
        refund.setRefundedAmount(50L);

        // compute expected balance
        Long balance = transaction.getAmount() - refund.getRefundedAmount();

        // Call the method
        Refunds actualRefund = refundsUtil.updateRefund(proxyResponse, refund, transaction);

        // Verify the result
        assertEquals(balance, actualRefund.getBalance());
    }

    @Test
    public void testUpdateRefund_withSuccessfulProxyResponse_andRefundBalanceIsZero() {
        // Create test data
        Transaction transaction = new Transaction();
        transaction.setAmount(450L);
        ProxyResponse proxyResponse = new ProxyResponse();
        proxyResponse.setResponseCode("03");

        // refund amount is null
        Refunds refund = new Refunds();
        refund.setBalance(0L);
        refund.setRefundedAmount(50L);

        // expected balance should be zero
        Long balance = 0L;

        // Call the method
        Refunds actualRefund = refundsUtil.updateRefund(proxyResponse, refund, transaction);

        // Verify the result
        assertEquals(balance, actualRefund.getBalance());
    }
    
    /*
     * Computing refunded amount test
     */

    @Test
    public void testComputeRefundedAmount_whenRequestAmountIsMoreThanBalance(){
        // create test data
        ProductRequest productRequest = new ProductRequest();
        productRequest.setAmount(500L);
        Refunds refund = new Refunds();
        refund.setBalance(250L);
        refund.setRefundedAmount(250L);

        // Call the method
        Refunds actualRefund = refundsUtil.computeRefundedAmount(refund, productRequest);

        // verify that the null is return when request amount is invalid
        assertEquals(null, actualRefund);
    }

    @Test
    public void testComputeRefundedAmount(){
        // create test data
        ProductRequest productRequest = new ProductRequest();
        productRequest.setAmount(250L);
        Refunds refund = new Refunds();
        refund.setBalance(250L);
        refund.setRefundedAmount(250L);

        // expected computation
        Long computedRefundedAmount = refund.getRefundedAmount() + productRequest.getAmount();

        // Call the method
        Refunds actualRefund = refundsUtil.computeRefundedAmount(refund, productRequest);

        // verify that the refund is computed correctly
        assertEquals(computedRefundedAmount, actualRefund.getRefundedAmount());
        assertNotEquals(refund.getRefundedAmount(), actualRefund.getRefundedAmount());
    }


    @Test
    public void testCheckIfFoundRefundBalanceIsZeroAndReturnResponse() {
        // Setup
        String method = Methods.REFUND;
        Refunds foundRefund = new Refunds();
        foundRefund.setBalance(0L);
        foundRefund.setResponseCode("03");
        ProductRequest productRequest = new ProductRequest();
        productRequest.setTransactionReference("REF399949494949333");
        
        // Execute
        ProxyResponse proxyResponse = refundsUtil.checkFoundRefundBalanceAndReturnResponse(foundRefund, productRequest, method);
        
        // Verify
        assertEquals("This has been refunded", proxyResponse.getResponseMessage());
        
    }

    @Test
    public void testCheckIfFoundRefundBalanceIsNotZeroAndReturnNullResponse() {
        // Setup
        String method = Methods.REFUND;
        Refunds foundRefund = new Refunds();
        foundRefund.setBalance(10L);
        foundRefund.setResponseCode("03");
        ProductRequest productRequest = new ProductRequest();
        productRequest.setTransactionReference("REF399949494949333");
        
        // Execute
        ProxyResponse proxyResponse = refundsUtil.checkFoundRefundBalanceAndReturnResponse(foundRefund, productRequest, method);
        
        // Verify
        assertEquals(null, proxyResponse);
        
    }
    
}
