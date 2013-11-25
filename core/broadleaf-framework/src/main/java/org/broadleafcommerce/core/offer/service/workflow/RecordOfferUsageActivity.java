/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.offer.service.workflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.core.checkout.service.workflow.CheckoutSeed;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferAudit;
import org.broadleafcommerce.core.offer.domain.OfferCode;
import org.broadleafcommerce.core.offer.service.OfferAuditService;
import org.broadleafcommerce.core.offer.service.OfferService;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.workflow.BaseActivity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.state.ActivityStateManagerImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

/**
 * Saves an instance of OfferAudit for each offer in the passed in order.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link RecordOfferUsageRollbackHandler}
 */
public class RecordOfferUsageActivity extends BaseActivity<ProcessContext<CheckoutSeed>> {
    
    /**
     * Key to retrieve the audits that were persisted
     */
    public static final String SAVED_AUDITS = "savedAudits";
    
    protected static final Log LOG = LogFactory.getLog(RecordOfferUsageActivity.class);

    @Resource(name="blOfferAuditService")
    protected OfferAuditService offerAuditService;
    
    @Resource(name = "blOfferService")
    protected OfferService offerService;

    @Override
    public ProcessContext<CheckoutSeed> execute(ProcessContext<CheckoutSeed> context) throws Exception {
        Order order = context.getSeedData().getOrder();
        Set<Offer> appliedOffers = offerService.getUniqueOffersFromOrder(order);
        Map<Offer, OfferCode> offerToCodeMapping = offerService.getOffersRetrievedFromCodes(order.getAddedOfferCodes(), appliedOffers);
        
        List<OfferAudit> audits = saveOfferIds(appliedOffers, offerToCodeMapping, order);
        
        Map<String, Object> state = new HashMap<String, Object>();
        state.put(SAVED_AUDITS, audits);
        
        ActivityStateManagerImpl.getStateManager().registerState(this, context, getRollbackHandler(), state);

        return context;
    }
    
    /**
     * Persists each of the offers to the database as {@link OfferAudit}s.
     * 
     * @return the {@link OfferAudit}s that were persisted
     */
    protected List<OfferAudit> saveOfferIds(Set<Offer> offers, Map<Offer, OfferCode> offerToCodeMapping, Order order) {
        List<OfferAudit> audits = new ArrayList<OfferAudit>(offers.size());
        for (Offer offer : offers) {
            OfferAudit audit = offerAuditService.create();
            audit.setCustomerId(order.getCustomer().getId());
            audit.setOfferId(offer.getId());
            audit.setOrderId(order.getId());
            
            //add the code that was used to obtain the offer to the audit context
            try {
                OfferCode codeUsedToRetrieveOffer = offerToCodeMapping.get(offer);
                if (codeUsedToRetrieveOffer != null) {
                    audit.setOfferCodeId(codeUsedToRetrieveOffer.getId());
                }
            } catch (UnsupportedOperationException e) {
                LOG.warn("Checking for offer code max usage has not been enabled in your Broadleaf installation. This warning" +
                        " will only appear in the Broadleaf 3.0 line, versions 3.0.6-GA and above. In order to fix your" +
                        " version of Broadleaf to enable this functionality, refer to the OfferAuditWeaveImpl or directly to" +
                        " https://github.com/BroadleafCommerce/BroadleafCommerce/pull/195.");
            }
            
            audit.setRedeemedDate(SystemTime.asDate());
            audit = offerAuditService.save(audit);
            audits.add(audit);
        }
        
        return audits;
    }
        
}
