package org.broadleafcommerce.checkout.service.workflow;

import javax.annotation.Resource;

import org.broadleafcommerce.payment.service.CompositePaymentService;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class PaymentServiceActivity extends BaseActivity {

    @Resource
    private CompositePaymentService compositePaymentService;

    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        CheckoutSeed seed = ((CheckoutContext) context).getSeedData();
        compositePaymentService.executePayment(seed.getOrder(), seed.getInfos(), seed.getPaymentResponse());

        return context;
    }

}
