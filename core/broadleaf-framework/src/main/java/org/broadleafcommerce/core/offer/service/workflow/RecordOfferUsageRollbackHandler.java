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

import org.broadleafcommerce.core.checkout.service.workflow.CheckoutSeed;
import org.broadleafcommerce.core.offer.domain.OfferAudit;
import org.broadleafcommerce.core.offer.service.OfferAuditService;
import org.broadleafcommerce.core.workflow.Activity;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.state.RollbackFailureException;
import org.broadleafcommerce.core.workflow.state.RollbackHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;


/**
 * Rolls back audits that were saved in the database from {@link RecordOfferUsageActivity}.
 *
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link RecordOfferUsageActivity}
 */
@Component("blRecordOfferUsageRollbackHandler")
public class RecordOfferUsageRollbackHandler implements RollbackHandler<ProcessContext<CheckoutSeed>> {

    @Resource(name = "blOfferAuditService")
    protected OfferAuditService offerAuditService;
    
    @Override
    public void rollbackState(Activity<ProcessContext<CheckoutSeed>> activity, ProcessContext<CheckoutSeed> processContext, Map<String, Object> stateConfiguration) throws RollbackFailureException {
        List<OfferAudit> audits = (List<OfferAudit>) stateConfiguration.get(RecordOfferUsageActivity.SAVED_AUDITS);
        
        for (OfferAudit audit : audits) {
            offerAuditService.delete(audit);
        }
    }
    
}
