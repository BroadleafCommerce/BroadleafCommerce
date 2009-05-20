package org.broadleafcommerce.payment.service;

import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.workflow.WorkflowException;

public interface SecurePaymentInfoService {

    public Referenced findSecurePaymentInfo(String referenceNumber, String paymentInfoType) throws WorkflowException;

    public Referenced save(Referenced securePaymentInfo);

    public Referenced create(String paymentInfoType);

    public void remove(Referenced securePaymentInfo);

    public void findAndRemoveSecurePaymentInfo(String referenceNumber, String paymentInfoType) throws WorkflowException;

}
