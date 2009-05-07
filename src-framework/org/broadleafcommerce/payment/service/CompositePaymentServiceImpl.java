package org.broadleafcommerce.payment.service;

import java.util.Map;

import javax.annotation.Resource;

import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.payment.service.exception.PaymentException;
import org.broadleafcommerce.payment.service.workflow.PaymentSeed;
import org.broadleafcommerce.workflow.SequenceProcessor;
import org.broadleafcommerce.workflow.WorkflowException;
import org.springframework.stereotype.Service;

/**
 * Execute the payment workflow independently of the checkout workflow
 * 
 * @author jfischer
 *
 */
@Service("compositePaymentService")
public class CompositePaymentServiceImpl implements CompositePaymentService {

    @Resource(name="paymentWorkflow")
    SequenceProcessor paymentWorkflow;

    @Override
    public void executePayment(Order order, Map<PaymentInfo, Referenced> payments) throws PaymentException {
        /*
         * TODO add validation that checks the order and payment information for validity.
         */
        try {
            PaymentSeed seed = new PaymentSeed(order, payments);
            paymentWorkflow.doActivities(seed);
        } catch (WorkflowException e) {
            throw new PaymentException("Unable to execute payment for order -- id: " + order.getId(), e);
        }
    }

    @Override
    public void executePayment(Order order) throws PaymentException {
        executePayment(order, null);
    }

}
