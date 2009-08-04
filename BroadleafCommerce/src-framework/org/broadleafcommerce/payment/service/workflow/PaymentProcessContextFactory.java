/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.payment.service.workflow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.broadleafcommerce.order.service.CartService;
import org.broadleafcommerce.payment.domain.PaymentInfo;
import org.broadleafcommerce.payment.domain.Referenced;
import org.broadleafcommerce.payment.service.SecurePaymentInfoService;
import org.broadleafcommerce.workflow.ProcessContext;
import org.broadleafcommerce.workflow.ProcessContextFactory;
import org.broadleafcommerce.workflow.WorkflowException;

public class PaymentProcessContextFactory implements ProcessContextFactory {

    @Resource(name = "blSecurePaymentInfoService")
    private SecurePaymentInfoService securePaymentInfoService;

    @Resource(name = "blCartService")
    private CartService cartService;

    private PaymentActionType paymentActionType;

    public ProcessContext createContext(Object seedData) throws WorkflowException {
        if (!(seedData instanceof PaymentSeed)) {
            throw new WorkflowException("Seed data instance is incorrect. " + "Required class is " + PaymentSeed.class.getName() + " " + "but found class: " + seedData.getClass().getName());
        }
        PaymentSeed paymentSeed = (PaymentSeed) seedData;
        Map<PaymentInfo, Referenced> secureMap = paymentSeed.getInfos();
        if (secureMap == null) {
            secureMap = new HashMap<PaymentInfo, Referenced>();
            List<PaymentInfo> paymentInfoList = cartService.readPaymentInfosForOrder(paymentSeed.getOrder());
            if (paymentInfoList == null || paymentInfoList.size() == 0) {
                throw new WorkflowException("No payment info instances associated with the order -- id: " + paymentSeed.getOrder().getId());
            }
            Iterator<PaymentInfo> infos = paymentInfoList.iterator();
            while (infos.hasNext()) {
                PaymentInfo info = infos.next();
                secureMap.put(info, securePaymentInfoService.findSecurePaymentInfo(info.getReferenceNumber(), info.getType()));
            }
        }
        CombinedPaymentContextSeed combinedSeed = new CombinedPaymentContextSeed(secureMap, paymentActionType, paymentSeed.getOrder().getTotal(), paymentSeed.getPaymentResponse());
        WorkflowPaymentContext response = new WorkflowPaymentContext();
        response.setSeedData(combinedSeed);

        return response;
    }

    public PaymentActionType getPaymentActionType() {
        return paymentActionType;
    }

    public void setPaymentActionType(PaymentActionType paymentActionType) {
        this.paymentActionType = paymentActionType;
    }

}
