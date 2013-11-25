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

package org.broadleafcommerce.core.offer.service;

import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferAudit;
import org.broadleafcommerce.profile.core.domain.Customer;


/**
 * Service for managing {@link OfferAudit}s. An {@link OfferAudit} is used to track usage of an offer and offer code
 * for a particular {@link Order} and {@link Customer}. This provides easy and fast tracking of verifying max uses on
 * particular {@link Offer}s.
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface OfferAuditService {

    public OfferAudit readAuditById(Long offerAuditId);
    
    /**
     * Persists an audit record to the database
     */
    public OfferAudit save(OfferAudit offerAudit);
    
    public void delete(OfferAudit offerAudit);

    /**
     * Creates a new offer audit
     */
    public OfferAudit create();
    
    /**
     * Counts how many times the an offer has been used by a customer
     * 
     * @param customerId
     * @param offerId
     * @return
     */
    public Long countUsesByCustomer(Long customerId, Long offerId);

    /**
     * Counts how many times the given offer code has been used in the system
     * 
     * @param offerCodeId
     * @return
     */
    public Long countOfferCodeUses(Long offerCodeId);
    
}
