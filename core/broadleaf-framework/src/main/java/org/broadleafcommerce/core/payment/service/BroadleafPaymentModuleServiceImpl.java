package org.broadleafcommerce.core.payment.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.payment.service.workflow.PaymentSeed;
import org.springframework.stereotype.Service;

/**
 * @author Jerry Ocanas (jocanas)
 */
@Service("blPaymentModuleService")
public class BroadleafPaymentModuleServiceImpl implements BroadleafPaymentModuleService {

    private static final Log LOG = LogFactory.getLog(BroadleafPaymentModuleServiceImpl.class);

    @Override
    public void validateResponse(PaymentSeed paymentSeed) throws Exception {
        LOG.warn("Validate response has not been implemented.");
    }

    @Override
    public void manualPayment(String transactionID) {
        LOG.warn("Manual payment has not been implemented.");
    }

}
