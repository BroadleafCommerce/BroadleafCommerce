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
package org.broadleafcommerce.core.offer.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name = "BLC_OFFER_AUDIT")
@Inheritance(strategy=InheritanceType.JOINED)
public class OfferAuditImpl implements OfferAudit {

    public static final long serialVersionUID = 1L;
    
    protected static final Log LOG = LogFactory.getLog(OfferAuditImpl.class);

    @Id
    @GeneratedValue(generator = "OfferAuditId")
    @GenericGenerator(
        name="OfferAuditId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="OfferAuditImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.offer.domain.OfferAuditImpl")
        }
    )
    @Column(name = "OFFER_AUDIT_ID")
    protected Long id;

    @Column(name = "OFFER_ID")
    @Index(name="OFFERAUDIT_OFFER_INDEX", columnNames={"OFFER_ID"})
    protected Long offerId;

    @Column(name = "CUSTOMER_ID")
    @Index(name="OFFERAUDIT_CUSTOMER_INDEX", columnNames={"CUSTOMER_ID"})
    protected Long customerId;

    @Column(name = "ORDER_ID")
    @Index(name="OFFERAUDIT_ORDER_INDEX", columnNames={"ORDER_ID"})
    protected Long orderId;
    
    @Column(name = "OFFER_CODE_ID")
    @Index(name="OFFERAUDIT_OFFER_CODE_INDEX", columnNames={"OFFER_CODE_ID"})
    protected Long offerCodeId;
    
    @Column(name = "REDEEMED_DATE")
    protected Date redeemedDate;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getOfferId() {
        return offerId;
    }

    @Override
    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    @Override
    public Long getOfferCodeId() {
        return offerCodeId;
    }

    @Override
    public void setOfferCodeId(Long offerCodeId) {
        this.offerCodeId = offerCodeId;
    }

    @Override
    public Long getCustomerId() {
        return customerId;
    }

    @Override
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    @Override
    public Long getOrderId() {
        return orderId;
    }

    @Override
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    @Override
    public Date getRedeemedDate() {
        return redeemedDate;
    }

    @Override
    public void setRedeemedDate(Date redeemedDate) {
        this.redeemedDate = redeemedDate;
    }

    @Override
    public int hashCode() {
            return new HashCodeBuilder()
            .append(customerId)
            .append(offerId)
            .append(offerCodeId)
            .append(redeemedDate)
            .append(orderId)
            .build();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof OfferAuditImpl) {
            OfferAuditImpl that = (OfferAuditImpl) o;
            
            return new EqualsBuilder()
                .append(this.id, that.id)
                .append(this.customerId, that.customerId)
                .append(this.offerId, that.offerId)
                .append(this.offerCodeId, that.offerCodeId)
                .append(this.redeemedDate, that.redeemedDate)
                .append(this.orderId, that.orderId)
                .build();
        }
        
        return false;
    }

}
