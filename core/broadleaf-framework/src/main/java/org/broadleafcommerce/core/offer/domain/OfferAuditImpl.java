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

package org.broadleafcommerce.core.offer.domain;

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
        final int prime = 31;
        int result = 1;
        result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
        result = prime * result + ((offerId == null) ? 0 : offerId.hashCode());
        result = prime * result + ((redeemedDate == null) ? 0 : redeemedDate.hashCode());
        result = prime * result + ((orderId == null) ? 0 : orderId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OfferAuditImpl other = (OfferAuditImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (customerId == null) {
            if (other.customerId != null)
                return false;
        } else if (!customerId.equals(other.customerId))
            return false;
        if (offerId == null) {
            if (other.offerId != null)
                return false;
        } else if (!offerId.equals(other.offerId))
            return false;
        if (redeemedDate == null) {
            if (other.redeemedDate != null)
                return false;
        } else if (!redeemedDate.equals(other.redeemedDate))
            return false;
        if (orderId == null) {
            if (other.orderId != null)
                return false;
        } else if (!orderId.equals(other.orderId))
            return false;
        return true;
    }
}
