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

package org.broadleafcommerce.core.offer.domain;

import org.hibernate.annotations.Index;

import javax.persistence.Column;
import javax.persistence.Embeddable;


/**
 * This entity is ONLY to be used as a fix for https://github.com/broadleafcommerce/broadleafcommerce/pull/195 in the 3.0
 * line starting with 3.0.6-GA. This class should be removed in 3.1 and the properties in this class added to
 * {@link OfferAuditImpl}.
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
@Embeddable
public class OfferAuditWeaveImpl {

    @Column(name = "OFFER_CODE_ID")
    @Index(name="OFFERAUDIT_OFFER_CODE_INDEX", columnNames={"OFFER_CODE_ID"})
    protected Long offerCodeId;
    
    public Long getOfferCodeId() {
        return offerCodeId;
    }

    public void setOfferCodeId(Long offerCodeId) {
        this.offerCodeId = offerCodeId;
    }

}
