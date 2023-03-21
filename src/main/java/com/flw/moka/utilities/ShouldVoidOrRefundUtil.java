package com.flw.moka.utilities;

import java.text.ParseException;

import org.springframework.stereotype.Component;

import com.flw.moka.exception.NoMethodNamePassedException;
import com.flw.moka.exception.TransactionNotCapturedException;
import com.flw.moka.exception.TransactionShouldBeRefundedException;
import com.flw.moka.exception.TransactionShouldBeVoidedException;

@Component
public class ShouldVoidOrRefundUtil {
    public void routeTransaction(String timeCaptured, String method)
            throws TransactionShouldBeRefundedException, TransactionShouldBeVoidedException, ParseException {
        if (timeCaptured == null || timeCaptured.isEmpty()) {
            transactionNotCapturedForRefund(method);
            return;
        }
        if (method == null || method.isEmpty()) {
            throw new NoMethodNamePassedException(
                    "Please provide a method in your void/refund service to use this utility");
        }

        TimeUtil timeUtility = new TimeUtil();
        Boolean isTransactionUpTo24Hours = timeUtility.isTransactionUpTo24Hours(timeCaptured);

        if (method.equalsIgnoreCase("void")) {

            if (isTransactionUpTo24Hours) {
                throw new TransactionShouldBeRefundedException("This transaction should be refunded");
            }
        }

        if (method.equalsIgnoreCase("refund")) {

            if (!isTransactionUpTo24Hours) {
                throw new TransactionShouldBeVoidedException("This transaction should be voided");
            }
        }

    }

    private void transactionNotCapturedForRefund(String method) throws TransactionNotCapturedException {
        if (method.equalsIgnoreCase("refund")) {
            throw new TransactionNotCapturedException("Can't refund a transaction that is not captured");
        }
    }
}
