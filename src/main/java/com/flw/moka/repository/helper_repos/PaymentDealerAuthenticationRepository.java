package com.flw.moka.repository.helper_repos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import com.flw.moka.entity.helpers.PaymentDealerAuthentication;

@Repository
public class PaymentDealerAuthenticationRepository {

    @Autowired
    private Environment environment;
    private PaymentDealerAuthentication paymentDealerAuthentication = new PaymentDealerAuthentication();

    public PaymentDealerAuthentication setPaymentDealerDetails() {
        paymentDealerAuthentication.setDealerCode(environment.getProperty("payment.dealer.authentication.dealercode"));
        paymentDealerAuthentication.setPassword(environment.getProperty("payment.dealer.authentication.password"));
        paymentDealerAuthentication.setUsername(environment.getProperty("payment.dealer.authentication.username"));
        paymentDealerAuthentication.setCheckKey(environment.getProperty("payment.dealer.authentication.checkkey"));

        return paymentDealerAuthentication;
    }
}