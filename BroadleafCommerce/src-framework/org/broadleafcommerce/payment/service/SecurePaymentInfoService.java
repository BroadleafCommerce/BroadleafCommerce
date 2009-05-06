package org.broadleafcommerce.payment.service;

import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.workflow.WorkflowException;

public interface SecurePaymentInfoService {

    Referenced findSecurePaymentInfo(String referenceNumber, String paymentInfoType) throws WorkflowException;

}
