package com.flw.moka.validation;
// package com.flw.moka.utilities;

// import java.text.ParseException;

// import org.springframework.stereotype.Component;

// import com.flw.moka.entity.helpers.Methods;
// import com.flw.moka.exception.NoMethodNamePassedException;
// import com.flw.moka.exception.TransactionShouldBeRefundedException;
// import com.flw.moka.exception.TransactionShouldBeVoidedException;

// import lombok.AllArgsConstructor;

// @AllArgsConstructor
// @Component
// public class ShouldVoidOrRefundUtil {

// MethodValidator methodValidator;

// public void routeTransaction(String timeCaptured, String method)
// throws TransactionShouldBeRefundedException,
// TransactionShouldBeVoidedException, ParseException {
// if (timeCaptured == null || timeCaptured.isEmpty()) {
// methodValidator.preventVoidOrRefundIfNotCaptured(method);
// return;
// }
// if (method == null || method.isEmpty()) {
// throw new NoMethodNamePassedException(
// "Please provide a method in your void/refund service to use this utility");
// }

// TimeUtil timeUtility = new TimeUtil();
// Boolean isTransactionUpTo24Hours =
// timeUtility.isTransactionUpTo24Hours(timeCaptured);

// if (method.equalsIgnoreCase(Methods.VOID)) {

// if (isTransactionUpTo24Hours) {
// throw new TransactionShouldBeRefundedException("This transaction should be
// refunded");
// }
// }

// if (method.equalsIgnoreCase(Methods.REFUND)) {

// if (!isTransactionUpTo24Hours) {
// throw new TransactionShouldBeVoidedException("This transaction should be
// voided");
// }
// }

// }
// }
