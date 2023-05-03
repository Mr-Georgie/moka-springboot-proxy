package com.flw.moka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.flw.moka.entity.models.Refunds;
import com.flw.moka.entity.models.Transaction;
import com.flw.moka.entity.request.ProductRequest;
import com.flw.moka.entity.response.Meta;
import com.flw.moka.entity.response.ProxyResponse;
import com.flw.moka.repository.RefundsRepository;
import com.flw.moka.service.entity_service.RefundsEntityServiceImpl;
import com.flw.moka.service.helper_service.ProxyResponseService;
import com.flw.moka.utilities.entity.LogsUtil;
import com.flw.moka.utilities.entity.RefundsUtil;
import com.flw.moka.utilities.helpers.GenerateReferenceUtil;
import com.flw.moka.validation.MethodValidator;

@ExtendWith(SpringExtension.class)
public class RefundsEntityServiceTest {

    @Mock
    private RefundsRepository refundsRepository;

    @Mock
    private ProxyResponseService proxyResponseService;

    @Mock
    private MethodValidator methodValidator;

    @Mock
    private LogsUtil logsUtil;

    @Mock
    private GenerateReferenceUtil generateReferenceUtil;

    @Mock
    private RefundsUtil refundsUtil;

    @InjectMocks
    private RefundsEntityServiceImpl refundsEntityService;

    @Test
    public void testSaveRefund() {
        // Set up test data
        String transactionReference = "MRN394984839933";
        String refundReference = "REF8286850677339869";

        // Mock method parameters
        Refunds existingRefund = mock(Refunds.class);
        ProxyResponse proxyResponse = mock(ProxyResponse.class);
        Transaction transaction = mock(Transaction.class);

        // Create a mock Refunds object for the expected result
        Refunds expectedRefund = mock(Refunds.class);

        // Mock object to be stubbed
        Meta meta = mock(Meta.class);

        when(refundsUtil.updateRefund(proxyResponse, existingRefund, transaction)).thenReturn(expectedRefund);
        when(generateReferenceUtil.generateRandom("REF")).thenReturn(refundReference);
        when(proxyResponse.getMeta()).thenReturn(meta);
        when(existingRefund.getTransactionReference()).thenReturn(transactionReference);

        // Call method under test
        refundsEntityService.saveRefund(proxyResponse, existingRefund, transaction);

        // Verify that the expected methods were called with the expected arguments
        verify(refundsUtil, times(1)).updateRefund(proxyResponse, existingRefund, transaction);
        verify(expectedRefund, times(1)).setRefundReference(refundReference);
        verify(refundsRepository, times(1)).save(expectedRefund);

    }

    @Test
    public void testCreateNewRefund(){
        // Arrange
        String reference = "REF399949494949333";
        ProductRequest productRequest = new ProductRequest();
        productRequest.setAmount(500L);
        productRequest.setTransactionReference(reference);
        Optional<Refunds> optionalRefund = Optional.empty();

        Transaction transaction = mock(Transaction.class);

        when(refundsRepository.findFirstByTransactionReferenceOrderByIdDesc(reference))
                .thenReturn(optionalRefund);

        // call method under test
        Refunds refunds = refundsEntityService.getRefund(productRequest, transaction);

        assertEquals(productRequest.getAmount(), refunds.getRefundedAmount());
        assertEquals(productRequest.getTransactionReference(), refunds.getTransactionReference());
    }

    @Test
    public void testCreateNewRefund_whenRequestAmountIsNotPassed(){
        // Arrange
        String reference = "REF399949494949333";
        ProductRequest productRequest = new ProductRequest();
        productRequest.setTransactionReference(reference);
        Optional<Refunds> optionalRefund = Optional.empty();

        Transaction transaction = new Transaction();
        transaction.setAmount(500L);

        when(refundsRepository.findFirstByTransactionReferenceOrderByIdDesc(reference))
                .thenReturn(optionalRefund);

        // call method under test
        Refunds refunds = refundsEntityService.getRefund(productRequest, transaction);

        // asserts
        assertEquals(transaction.getAmount(), refunds.getRefundedAmount());
        assertEquals(productRequest.getTransactionReference(), refunds.getTransactionReference());
    }

/* 
    @Test
    public void testGetRefund_withInvalidRefundAmount(){

        String reference = "REF399949494949333";
        ProductRequest productRequest = new ProductRequest();
        productRequest.setTransactionReference(reference);
        Refunds refund = new Refunds();
        refund.setBalance(500L);
        refund.setResponseCode("RR");
        refund.setCurrency("USD");
        Optional<Refunds> optionalRefund = Optional.of(refund);

        Transaction transaction = new Transaction();
        String message = "REFUND Failed: Invalid Amount";
        ProxyResponse proxyResponse = mock(ProxyResponse.class);


        when(refundsUtil.prepareFailedResponse(reference, "refund", message)).thenReturn(proxyResponse);
        when(refundsRepository.findFirstByTransactionReferenceOrderByIdDesc(reference))
                .thenReturn(optionalRefund);


        // checks
        assertThrows(InvalidRefundRequestException.class, () -> refundsEntityService.getRefund(productRequest, transaction));
    }
*/
}
