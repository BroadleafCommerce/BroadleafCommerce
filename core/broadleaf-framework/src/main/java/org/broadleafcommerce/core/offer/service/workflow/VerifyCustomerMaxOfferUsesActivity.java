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

import org.broadleafcommerce.core.checkout.service.workflow.CheckoutContext;
import org.broadleafcommerce.core.offer.dao.OfferAuditDao;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.service.OfferService;
import org.broadleafcommerce.core.offer.service.exception.OfferMaxUseExceededException;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.workflow.BaseActivity;

import java.util.Set;

import javax.annotation.Resource;

/**
 * Checks the offers being used in the order to make sure that the customer
 * has not exceeded the max uses for the offer.
 * 
 * This will also verify that max uses for the code
 */
public class VerifyCustomerMaxOfferUsesActivity extends BaseActivity<CheckoutContext> {

    @Resource(name="blOfferAuditDao")
    protected OfferAuditDao offerAuditDao;
    
    @Resource(name = "blOfferService")
    protected OfferService offerService;

    @Override
    public CheckoutContext execute(CheckoutContext context) throws Exception {
        Order order = context.getSeedData().getOrder();
        Set<Offer> appliedOffers = offerService.getUniqueOffersFromOrder(order);
        
        for (Offer offer : appliedOffers) {
            if (offer.isLimitedUsePerCustomer()) {
                Long currentUses = offerAuditDao.countUsesByCustomer(order.getCustomer().getId(), offer.getId());
                if (currentUses >= offer.getMaxUsesPerCustomer()) {
                    throw new OfferMaxUseExceededException("The customer has used this offer more than the maximum allowed number of times.");
                }
            }
        }
        
        //TODO: allow lenient checking on offer code usage
        for (OfferCode code : order.getAddedOfferCodes()) {
            if (code.isLimitedUse()) {
                Long currentCodeUses = offerAuditDao.countOfferCodeUses(code.getId());
                if (currentCodeUses >= code.getMaxUses()) {
                    throw new OfferMaxUseExceededException("Offer code " + code.getOfferCode() + " with id " + code.getId()
                            + " has been than the maximum allowed number of times.");
                }
            }
        }
        
        return context;
    }
   
}
