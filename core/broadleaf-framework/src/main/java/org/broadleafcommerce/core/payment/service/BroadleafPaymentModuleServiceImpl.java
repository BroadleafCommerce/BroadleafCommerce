package org.broadleafcommerce.core.payment.service;

import org.broadleafcommerce.core.payment.service.workflow.PaymentSeed;
import org.springframework.stereotype.Service;

/**
 * @author Jerry Ocanas (jocanas)
 */
@Service("blPaymentModuleService")
public class BroadleafPaymentModuleServiceImpl implements BroadleafPaymentModuleService {

    @Override
    public void validateResponse(PaymentSeed paymentSeed) throws Exception {
    }

    @Override
    public void manualPayment(String transactionID) {
    }

}
