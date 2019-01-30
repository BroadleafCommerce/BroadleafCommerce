/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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
@Component("blRecordOfferUsageActivity")
public class RecordOfferUsageActivity extends BaseActivity<ProcessContext<CheckoutSeed>> {
    
    public static final int ORDER = 4000;
    
    /**
     * Key to retrieve the audits that were persisted
     */
    public static final String SAVED_AUDITS = "savedAudits";
    
    protected static final Log LOG = LogFactory.getLog(RecordOfferUsageActivity.class);

    @Resource(name="blOfferAuditService")
    protected OfferAuditService offerAuditService;
    
    @Resource(name = "blOfferService")
    protected OfferService offerService;
    
    @Autowired
    public RecordOfferUsageActivity(@Qualifier("blRecordOfferUsageRollbackHandler") RecordOfferUsageRollbackHandler rollbackHandler) {
        setOrder(ORDER);
        setRollbackHandler(rollbackHandler);
    }

    @Override
    public ProcessContext<CheckoutSeed> execute(ProcessContext<CheckoutSeed> context) throws Exception {
        Order order = context.getSeedData().getOrder();
        Set<Offer> appliedOffers = offerService.getUniqueOffersFromOrder(order);
        Map<Offer, OfferCode> offerToCodeMapping = offerService.getOffersRetrievedFromCodes(order.getAddedOfferCodes(), appliedOffers);
        
        List<OfferAudit> audits = saveOfferIds(appliedOffers, offerToCodeMapping, order);
        
        Map<String, Object> state = new HashMap<>();
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
        List<OfferAudit> audits = new ArrayList<>(offers.size());
        for (Offer offer : offers) {
            OfferAudit audit = offerAuditService.create();
            audit.setCustomerId(order.getCustomer().getId());
            audit.setOfferId(offer.getId());
            audit.setOrderId(order.getId());
            
            //add the code that was used to obtain the offer to the audit context
            OfferCode codeUsedToRetrieveOffer = offerToCodeMapping.get(offer);
            if (codeUsedToRetrieveOffer != null) {
                audit.setOfferCodeId(codeUsedToRetrieveOffer.getId());
            }
            
            audit.setRedeemedDate(SystemTime.asDate());
            audit = offerAuditService.save(audit);
            audits.add(audit);
        }
        
        return audits;
    }
        
}
