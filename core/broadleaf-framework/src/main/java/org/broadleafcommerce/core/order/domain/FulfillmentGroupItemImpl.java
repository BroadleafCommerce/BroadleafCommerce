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

package org.broadleafcommerce.core.order.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;

@Entity
@DiscriminatorColumn(name = "TYPE")
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FULFILLMENT_GROUP_ITEM")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
public class FulfillmentGroupItemImpl implements FulfillmentGroupItem {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "FulfillmentGroupItemId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "FulfillmentGroupItemId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "FulfillmentGroupItemImpl", allocationSize = 50)
    @Column(name = "FULFILLMENT_GROUP_ITEM_ID")
    protected Long id;

    @ManyToOne(targetEntity = FulfillmentGroupImpl.class, optional=false)
    @JoinColumn(name = "FULFILLMENT_GROUP_ID")
    @Index(name="FGITEM_FG_INDEX", columnNames={"FULFILLMENT_GROUP_ID"})
    protected FulfillmentGroup fulfillmentGroup;

    @OneToOne(targetEntity = OrderItemImpl.class, optional=false)
    @JoinColumn(name = "ORDER_ITEM_ID")
    protected OrderItem orderItem;

    @Column(name = "QUANTITY", nullable=false)
    protected int quantity;

    @Column(name = "STATUS")
    @Index(name="FGITEM_STATUS_INDEX", columnNames={"STATUS"})
    private String status;
    
    @OneToMany(fetch = FetchType.LAZY, targetEntity = TaxDetailImpl.class, cascade = {CascadeType.ALL})
    @JoinTable(name = "BLC_FG_ITEM_TAX_XREF", joinColumns = @JoinColumn(name = "FULFILLMENT_GROUP_ITEM_ID"), inverseJoinColumns = @JoinColumn(name = "TAX_DETAIL_ID"))
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<TaxDetail> taxes = new ArrayList<TaxDetail>();
    
    @Column(name = "TOTAL_ITEM_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName="Total Item Tax", order=9, group="Pricing", fieldType=SupportedFieldType.MONEY)
    protected BigDecimal totalTax;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FulfillmentGroup getFulfillmentGroup() {
        return fulfillmentGroup;
    }

    public void setFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
        this.fulfillmentGroup = fulfillmentGroup;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Money getRetailPrice() {
        return orderItem.getRetailPrice();
    }

    public Money getSalePrice() {
        return orderItem.getSalePrice();
    }

    public Money getPrice() {
        return orderItem.getPrice();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public void removeAssociations() {
		if (getFulfillmentGroup() != null) getFulfillmentGroup().getFulfillmentGroupItems().remove(this);
		setFulfillmentGroup(null);
		setOrderItem(null);
	}

    @Override
    public List<TaxDetail> getTaxes() {
        return this.taxes;
    }

    @Override
    public void setTaxes(List<TaxDetail> taxes) {
        this.taxes = taxes;
    }
    
    public Money getTotalTax() {
        return totalTax == null ? null : new Money(totalTax);
    }

    public void setTotalTax(Money totalTax) {
        this.totalTax = Money.toAmount(totalTax);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FulfillmentGroupItemImpl other = (FulfillmentGroupItemImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (orderItem == null) {
            if (other.orderItem != null)
                return false;
        } else if (!orderItem.equals(other.orderItem))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((orderItem == null) ? 0 : orderItem.hashCode());
        return result;
    }
}
