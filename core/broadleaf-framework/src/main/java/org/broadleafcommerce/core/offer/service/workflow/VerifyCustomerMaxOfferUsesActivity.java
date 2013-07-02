/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.offer.service.workflow;

import org.broadleafcommerce.core.checkout.service.workflow.CheckoutSeed;
import org.broadleafcommerce.core.offer.dao.OfferAuditDao;
import org.broadleafcommerce.core.offer.domain.Adjustment;
import org.broadleafcommerce.core.offer.service.exception.OfferMaxUseExceededException;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * Checks the offers being used in the order to make sure that the customer
 * has not exceeded the max uses for the offer.
 */
public class VerifyCustomerMaxOfferUsesActivity extends BaseActivity<ProcessContext<CheckoutSeed>> {

    @Resource(name="blOfferAuditDao")
    private OfferAuditDao offerAuditDao;

    @Override
    public ProcessContext<CheckoutSeed> execute(ProcessContext<CheckoutSeed> context) throws Exception {
        Map<Long,Long> offerIdToAllowedUsesMap = new HashMap<Long,Long>();
        CheckoutSeed seed = context.getSeedData();
        Order order = seed.getOrder();
        if (order != null) {
            addOfferIds(order.getOrderAdjustments(), offerIdToAllowedUsesMap);

            if (order.getOrderItems() != null) {
                for (OrderItem item : order.getOrderItems()) {
                    addOfferIds(item.getOrderItemAdjustments(), offerIdToAllowedUsesMap);
                }
            }

            if (order.getFulfillmentGroups() != null) {
                for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
                    addOfferIds(fg.getFulfillmentGroupAdjustments(), offerIdToAllowedUsesMap);
                }
            }
            if (! checkOffers(offerIdToAllowedUsesMap, order)) {
                throw new OfferMaxUseExceededException("The customer has used this offer code more than the maximum allowed number of times.");
            }
        }

        return context;
    }
    
    private boolean checkOffers(Map<Long,Long> offerIdToAllowedUsesMap, Order order) {
        boolean orderVerified = true;    
    
        if (order.getCustomer() != null && order.getCustomer().getId() != null) {
            Long customerId = order.getCustomer().getId();
                        
            for(Long offerId : offerIdToAllowedUsesMap.keySet()) {
                Long allowedUses = offerIdToAllowedUsesMap.get(offerId);
                Long currentUses = offerAuditDao.countUsesByCustomer(customerId, offerId);
                if (currentUses != null && currentUses >= allowedUses) {
                    return false;
                }
            }
        }
        return true;                      
    }
        
    private void addOfferIds(List<? extends Adjustment> adjustments, Map<Long, Long> offerIdToAllowedUsesMap) {
        if (adjustments != null) {
            for(Adjustment adjustment : adjustments) {
                if (adjustment.getOffer() != null) {
                    Long maxUsesPerCustomer = adjustment.getOffer().getMaxUsesPerCustomer();
                    if (maxUsesPerCustomer != null && maxUsesPerCustomer > 0) {                
                        offerIdToAllowedUsesMap.put(adjustment.getOffer().getId(), maxUsesPerCustomer);
                    }
                }
            }
        }
    }

}
