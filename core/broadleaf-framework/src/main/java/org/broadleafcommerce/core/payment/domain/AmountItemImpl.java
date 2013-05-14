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

package org.broadleafcommerce.core.payment.domain;

import org.broadleafcommerce.common.currency.util.CurrencyCodeIdentifiable;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.hibernate.annotations.Index;

import javax.persistence.Column;
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
import java.math.BigDecimal;

/**
 * @author jfischer
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_AMOUNT_ITEM")
@AdminPresentationMergeOverrides(
    {
        @AdminPresentationMergeOverride(name = "", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.READONLY,
                                            booleanOverrideValue = true))
    }
)
public class AmountItemImpl implements AmountItem, CurrencyCodeIdentifiable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(generator = "AmountItemId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "AmountItemId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "AmountItemImpl", allocationSize = 50)
    @Column(name = "AMOUNT_ITEM_ID")
    protected Long id;
    
    @Column(name = "SHORT_DESCRIPTION", nullable=true)
    @Index(name="SHORT_DESCRIPTION_INDEX", columnNames={"SHORT_DESCRIPTION"})
    @AdminPresentation(friendlyName = "AmountItemImpl_Short_Description", order=1000, prominent=true, gridOrder = 1000)
    protected String shortDescription;
    
    @Column(name = "DESCRIPTION")
    @AdminPresentation(friendlyName = "AmountItemImpl_Description", order=2000)
    protected String description;
    
    @Column(name = "UNIT_PRICE", nullable=false, precision=19, scale=5)
    @AdminPresentation(friendlyName = "AmountItemImpl_Unit_Price", order=3000, gridOrder = 2000, prominent=true, fieldType=
                SupportedFieldType.MONEY)
    protected BigDecimal unitPrice;
    
    @Column(name = "QUANTITY", nullable=false)
    @AdminPresentation(friendlyName = "AmountItemImpl_Quantity", order=4000, prominent=true, gridOrder = 3000)
    protected Long quantity;
    
    @Column(name = "SYSTEM_ID")
    @AdminPresentation(friendlyName = "AmountItemImpl_SystemId", order=5000)
    protected String systemId;
    
    @ManyToOne(targetEntity = PaymentInfoImpl.class, optional = true)
    @JoinColumn(name = "PAYMENT_ID")
    @Index(name="AMOUNTITEM_PAYMENTINFO_INDEX", columnNames={"PAYMENT_ID"})
    @AdminPresentation(excluded = true)
    protected PaymentInfo paymentInfo;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.domain.AmountItem#getId()
     */
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.domain.AmountItem#setId(java.lang.Long)
     */
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.domain.AmountItem#getShortDescription()
     */
    public String getShortDescription() {
        return shortDescription;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.domain.AmountItem#setShortDescription(java.lang.String)
     */
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.domain.AmountItem#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.domain.AmountItem#setDescription(java.lang.String)
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.domain.AmountItem#getUnitPrice()
     */
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.domain.AmountItem#setUnitPrice(java.math.BigDecimal)
     */
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.domain.AmountItem#getQuantity()
     */
    public Long getQuantity() {
        return quantity;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.payment.domain.AmountItem#setQuantity(java.lang.Long)
     */
    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Override
    public String getCurrencyCode() {
        return ((CurrencyCodeIdentifiable) paymentInfo).getCurrencyCode();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((quantity == null) ? 0 : quantity.hashCode());
        result = prime * result + ((shortDescription == null) ? 0 : shortDescription.hashCode());
        result = prime * result + ((unitPrice == null) ? 0 : unitPrice.hashCode());
        result = prime * result + ((systemId == null) ? 0 : systemId.hashCode());
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
        AmountItemImpl other = (AmountItemImpl) obj;
        
        if (id != null && other.id != null) {
            return id.equals(other.id);
        }
        
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (quantity == null) {
            if (other.quantity != null)
                return false;
        } else if (!quantity.equals(other.quantity))
            return false;
        if (shortDescription == null) {
            if (other.shortDescription != null)
                return false;
        } else if (!shortDescription.equals(other.shortDescription))
            return false;
        if (unitPrice == null) {
            if (other.unitPrice != null)
                return false;
        } else if (!unitPrice.equals(other.unitPrice))
            return false;
        if (systemId == null) {
            if (other.systemId != null)
                return false;
        } else if (!systemId.equals(other.systemId))
            return false;
        return true;
    }
    
    
}
