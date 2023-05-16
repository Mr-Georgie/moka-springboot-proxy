package com.flw.moka;

import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.Meta;
import com.flw.moka.entity.response.ProviderResponse;
import com.flw.moka.entity.response.ProviderResponseData;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.utilities.entity.RefundsUtil;
import com.flw.moka.utilities.helpers.TimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class RefundsUtilsTest {

    private RefundsUtil refundsUtil;
    private Transaction transaction;
    private ProductRequest productRequest;
    private ProxyResponse proxyResponse;
    private TimeUtil timeUtilMock;


    @BeforeEach
    public void setup() {

        refundsUtil = new RefundsUtil();
        transaction = new Transaction();
        productRequest = new ProductRequest();

        timeUtilMock = mock(TimeUtil.class);
        Meta meta = mock(Meta.class);
        ProviderResponse providerResponse = mock(ProviderResponse.class);
        ProviderResponseData providerResponseData = mock(ProviderResponseData.class);
        proxyResponse = mock(ProxyResponse.class);
        productRequest.setAmount(50L);

        transaction.setAmount(500L);
        transaction.setAmountRefunded(0L);
        transaction.setBalance(500L);

        //  mock method call behaviour
        when(proxyResponse.getMeta()).thenReturn(meta);
        when(proxyResponse.getProviderResponse()).thenReturn(providerResponse);
        when(proxyResponse.getProviderResponse().getData()).thenReturn(providerResponseData);
        when(proxyResponse.getMeta().getExternalReference()).thenReturn("test-384uj-33nd");
        when(proxyResponse.getProviderResponse().getData().getRefundRequestId()).thenReturn("2001");
    }

    @Test
    public void testCreateUpdateRefundRecord_returnNullID() {

        // Call method under test
        Transaction refundedtransaction = refundsUtil.createRefundRecord(
                transaction, proxyResponse, productRequest, timeUtilMock);

        assertNull(refundedtransaction.getId());
    }

    @Test
    public void testCreateUpdateRefundRecord_returnCorrectBalance() {
        // Call method under test
        Transaction refundedtransaction = refundsUtil.createRefundRecord(
                transaction, proxyResponse, productRequest, timeUtilMock);


        assertNotNull(refundedtransaction.getBalance());
        //
        assertNotEquals(0L, refundedtransaction.getBalance());
    }

    @Test
    public void testCreateUpdateRefundRecord_returnRefundID() {
        // Call method under test
        Transaction refundedtransaction = refundsUtil.createRefundRecord(
                transaction, proxyResponse, productRequest, timeUtilMock);

        assertNotNull(refundedtransaction.getRefundId());
    }

    @Test
    public void testComputeBalance() {
        // expected computation
        Long expectedBalance = transaction.getAmount() - productRequest.getAmount();

        // Call method under test
        Long balance = refundsUtil.computeBalance(transaction, productRequest);

        assertEquals(expectedBalance, balance);
    }

    @Test
    public void testComputeBalance_whenProductRequestIsNull() {

        productRequest.setAmount(null);
        // Call method under test
        Long balance = refundsUtil.computeBalance(transaction, productRequest);

        assertEquals(0, balance);
    }

    @Test
    public void testComputeAmountRefunded() {
        // expected computation
        Long expectedAmountRefunded = productRequest.getAmount() + transaction.getAmountRefunded();

        // Call method under test
        Long amountRefunded = refundsUtil.computeAmountRefunded(transaction, productRequest);

        assertEquals(expectedAmountRefunded, amountRefunded);
    }

    @Test
    public void testComputeAmountRefunded_whenProductRequestIsNull() {

        productRequest.setAmount(null);
        // Call method under test
        Long amountRefunded = refundsUtil.computeAmountRefunded(transaction, productRequest);

        assertEquals(transaction.getAmount(), amountRefunded);
    }
}
