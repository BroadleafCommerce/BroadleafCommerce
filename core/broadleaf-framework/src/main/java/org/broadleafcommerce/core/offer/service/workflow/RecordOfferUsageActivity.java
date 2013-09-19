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

import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutContext;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutSeed;
import org.broadleafcommerce.core.offer.dao.OfferAuditDao;
import org.broadleafcommerce.core.offer.domain.Adjustment;
import org.broadleafcommerce.core.offer.domain.OfferAudit;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.core.order.domain.OrderItemPriceDetail;
import org.broadleafcommerce.core.workflow.BaseActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

/**
 * Saves an instance of OfferAudit for each offer in the passed in order.
 * Assumes that it is part of a larger transaction context.
 */
public class RecordOfferUsageActivity extends BaseActivity<CheckoutContext> {

    @Resource(name="blOfferAuditDao")
    private OfferAuditDao offerAuditDao;

    @Override
    public CheckoutContext execute(CheckoutContext context) throws Exception {
        Set<Long> appliedOfferIds = new HashSet<Long>();
        CheckoutSeed seed = context.getSeedData();
        Order order = seed.getOrder();
        if (order != null) {
            addOfferIds(order.getOrderAdjustments(), appliedOfferIds);

            if (order.getOrderItems() != null) {
                for (OrderItem item : order.getOrderItems()) {
                    addOfferIds(item.getOrderItemAdjustments(), appliedOfferIds);
                    
                    //record usage for price details on the item as well
                    if (item.getOrderItemPriceDetails() != null) {
                        for (OrderItemPriceDetail detail : item.getOrderItemPriceDetails()) {
                            addOfferIds(detail.getOrderItemPriceDetailAdjustments(), appliedOfferIds);
                        }
                    }
                }
            }

            if (order.getFulfillmentGroups() != null) {
                for (FulfillmentGroup fg : order.getFulfillmentGroups()) {
                    addOfferIds(fg.getFulfillmentGroupAdjustments(), appliedOfferIds);
                }
            }
            saveOfferIds(appliedOfferIds, order);
        }

        return context;
    }
    
    protected void saveOfferIds(Set<Long> offerIds, Order order) {
        for (Long offerId : offerIds) {
            OfferAudit audit = offerAuditDao.create();
            if (order.getCustomer() != null) {
                audit.setCustomerId(order.getCustomer().getId());
            }
            audit.setOfferId(offerId);
            audit.setOrderId(order.getId());
            audit.setRedeemedDate(SystemTime.asDate());
            offerAuditDao.save(audit);
        }
    }
        
    protected void addOfferIds(List<? extends Adjustment> adjustments, Set<Long> offerIds) {
        if (adjustments != null) {
            for (Adjustment adjustment : adjustments) {
                if (adjustment.getOffer() != null) {
                    offerIds.add(adjustment.getOffer().getId());
                }
            }
        }
    }

}
