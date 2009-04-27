package org.broadleafcommerce.checkout.service.workflow;

import javax.annotation.Resource;

import org.broadleafcommerce.payment.service.PaymentService;
import org.broadleafcommerce.workflow.BaseActivity;
import org.broadleafcommerce.workflow.ProcessContext;

public class PaymentServiceActivity extends BaseActivity {

    @Resource
    private PaymentService paymentService;

    @Override
    public ProcessContext execute(ProcessContext context) throws Exception {
        CheckoutSeed seed = ((CheckoutContext) context).getSeedData();
        paymentService.executePayment(seed.getOrder(), seed.getInfos());

        return context;
    }

}
