//package com.flw.moka;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.util.Optional;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import com.flw.moka.entity.models.Transaction;
//import com.flw.moka.entity.request.ProductRequest;
//import com.flw.moka.entity.response.Meta;
//import com.flw.moka.entity.response.ProxyResponse;
//import com.flw.moka.repository.TransactionRepository;
//import com.flw.moka.service.helper_service.ProxyResponseService;
//import com.flw.moka.utilities.entity.LogsUtil;
//import com.flw.moka.utilities.entity.TransactionUtil;
//import com.flw.moka.utilities.helpers.TimeUtil;
//import com.flw.moka.validation.MethodValidator;
//
//@ExtendWith(SpringExtension.class)
//public class TransactionServiceTest {
//
//    @Mock
//    private TransactionRepository transactionRepository;
//
//    @Mock
//    private ProxyResponseService proxyResponseService;
//
//    @Mock
//    private MethodValidator methodValidator;
//
//    @Mock
//    private LogsUtil logsUtil;
//
//    @Mock
//    private RefundsEntityService refundsEntityService;
//
//    @Mock
//    private TransactionUtil transactionUtil;
//
//    @InjectMocks
//    private TransactionServiceImpl transactionService;
//
//    @Test
//    public void testSaveTransaction() {
//        // Set up test data
//        ProductRequest productRequest = new ProductRequest();
//        Transaction transaction = new Transaction();
//        String provider = "MOKA";
//        String method = "authorize";
//
//        // Mock objects that will be stubbed
//        Meta meta = Mockito.mock(Meta.class);
//        ProxyResponse proxyResponse = Mockito.mock(ProxyResponse.class);
//
//        // Mock dependencies
//        TimeUtil timeUtilMock = Mockito.mock(TimeUtil.class);
//
//        doNothing().when(transactionUtil).setAuthorizeTransactionFields(transaction,
//                                                                        productRequest,
//                                                                        proxyResponse,
//                                                                        timeUtilMock);
//        when(proxyResponse.getMeta()).thenReturn(meta);
//        when(proxyResponse.getMeta().getProvider()).thenReturn(provider);
//        when(transactionRepository.save(transaction)).thenReturn(transaction);
//
//        // Call method under test
//        transactionService.saveTransaction(productRequest, proxyResponse, transaction, method);
//
//        // Verify that the transaction was saved to the repository
//        verify(transactionRepository, Mockito.times(1)).save(transaction);
//    }
//
//    @Test
//    public void testGetTransactionIfExistInDB() {
//        // Set up test data
//        String ref = "testRef";
//        Transaction transaction = new Transaction();
//        Optional<Transaction> optionalTransaction = Optional.of(transaction);
//
//        // Mock dependencies
//        when(transactionRepository.findByTransactionReference(ref)).thenReturn(optionalTransaction);
//
//        // Call method under test
//        Optional<Transaction> result = transactionService.getTransactionIfExistInDB(ref);
//
//        // Verify that the expected transaction was returned
//        assertEquals(optionalTransaction, result);
//    }
//
//    // Add more test cases as needed
//}
//
