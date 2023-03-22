package com.flw.moka.utilities;

import org.springframework.stereotype.Component;

import com.flw.moka.entity.helpers.ProductRequest;

@Component
public class MaskCardNumberInProductRequestUtil {

    public ProductRequest mask(ProductRequest productRequest) {
        String maskedCardNumber = maskHandler(productRequest.getCardNo());

        productRequest.setCardNo(maskedCardNumber);
        return productRequest;
    }

    private static String maskHandler(String cardNumber) {
        String firstSixChars = cardNumber.substring(0, 6);
        String lastFourChars = cardNumber.substring(cardNumber.length() - 4);
        String maskedCardNumber = firstSixChars + "******" + lastFourChars;

        return maskedCardNumber;
    }

}
