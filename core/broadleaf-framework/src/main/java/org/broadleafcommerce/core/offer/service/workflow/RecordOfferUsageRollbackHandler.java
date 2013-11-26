/*
 * Copyright 2008-2013 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.offer.service.workflow;

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
public class RecordOfferUsageRollbackHandler implements RollbackHandler {

    @Resource(name = "blOfferAuditService")
    protected OfferAuditService offerAuditService;
    
    @Override
    public void rollbackState(Activity<? extends ProcessContext> activity, ProcessContext processContext, Map<String, Object> stateConfiguration) throws RollbackFailureException {
        List<OfferAudit> audits = (List<OfferAudit>) stateConfiguration.get(RecordOfferUsageActivity.SAVED_AUDITS);
        
        for (OfferAudit audit : audits) {
            offerAuditService.delete(audit);
        }
    }
    
}
