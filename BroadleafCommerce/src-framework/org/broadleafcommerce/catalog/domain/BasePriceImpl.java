/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.catalog.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.broadleafcommerce.common.domain.Auditable;
import org.broadleafcommerce.util.money.Money;

/**
 * The Class BasePriceImpl.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_BASE_PRICE")
public class BasePriceImpl implements BasePrice {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The id. */
    @Id
    @GeneratedValue(generator = "BasePriceId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "BasePriceId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "BasePriceImpl", allocationSize = 50)
    @Column(name = "BASE_PRICE_ID")
    protected Long id;

    /** The auditable. */
    @Embedded
    protected Auditable auditable = new Auditable();

    /** The sku. */
    @ManyToOne(targetEntity = SkuImpl.class)
    @JoinColumn(name = "SKU_ID", nullable = false)
    protected Sku sku;

    /** The amount. */
    @Column(name = "AMOUNT", nullable=false)
    protected BigDecimal amount;

    /** The start date. */
    @Column(name = "START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date startDate;

    /** The end date. */
    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    protected Date endDate;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.BasePrice#getId()
     */
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.BasePrice#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.BasePrice#getAmount()
     */
    public Money getAmount() {
        return amount == null ? null : new Money(amount);
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.BasePrice#setAmount(org.broadleafcommerce.util.money.Money)
     */
    public void setAmount(Money amount) {
        this.amount = Money.toAmount(amount);
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.BasePrice#getStartDate()
     */
    public Date getStartDate() {
        return startDate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.BasePrice#setStartDate(java.util.Date)
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.BasePrice#getEndDate()
     */
    public Date getEndDate() {
        return endDate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.BasePrice#setEndDate(java.util.Date)
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.BasePrice#getSku()
     */
    public Sku getSku() {
        return sku;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.BasePrice#setSku(org.broadleafcommerce.catalog.domain.Sku)
     */
    public void setSku(Sku sku) {
        this.sku = sku;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.BasePrice#getAuditable()
     */
    public Auditable getAuditable() {
        return auditable;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.catalog.domain.BasePrice#setAuditable(org.broadleafcommerce.common.domain.Auditable)
     */
    public void setAuditable(Auditable auditable) {
        this.auditable = auditable;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        result = prime * result + ((auditable == null) ? 0 : auditable.hashCode());
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result + ((sku == null) ? 0 : sku.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
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
        BasePriceImpl other = (BasePriceImpl) obj;

        if (id == null && other.id != null) {
            return id.equals(other.id);
        }

        if (amount == null) {
            if (other.amount != null)
                return false;
        } else if (!amount.equals(other.amount))
            return false;
        if (auditable == null) {
            if (other.auditable != null)
                return false;
        } else if (!auditable.equals(other.auditable))
            return false;
        if (endDate == null) {
            if (other.endDate != null)
                return false;
        } else if (!endDate.equals(other.endDate))
            return false;
        if (sku == null) {
            if (other.sku != null)
                return false;
        } else if (!sku.equals(other.sku))
            return false;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        return true;
    }
}
