
package org.broadleafcommerce.core.payment.dao;

import org.broadleafcommerce.core.payment.domain.PaymentInfo;
import org.broadleafcommerce.core.payment.domain.PaymentRequest;

public interface PaymentRequestDao {

    public abstract PaymentRequest save(PaymentRequest paymentRequest);

    public abstract PaymentRequest readPaymentRequestById(Long paymentId);

    @SuppressWarnings("unchecked")
    public abstract PaymentRequest readPaymentRequestByKey(String key);

    public abstract PaymentInfo create();

    public abstract void delete(PaymentRequest paymentRequest);

}