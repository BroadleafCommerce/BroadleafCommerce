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
package org.broadleafcommerce.core.order.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.currency.util.CurrencyCodeIdentifiable;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.broadleafcommerce.core.order.service.type.FulfillmentGroupStatusType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@DiscriminatorColumn(name = "TYPE")
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FULFILLMENT_GROUP_ITEM")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
@AdminPresentationMergeOverrides(
    {
        @AdminPresentationMergeOverride(name = "", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.READONLY,
                                            booleanOverrideValue = true))
    }
)
public class FulfillmentGroupItemImpl implements FulfillmentGroupItem, Cloneable, CurrencyCodeIdentifiable {

    private static final Log LOG = LogFactory.getLog(FulfillmentGroupItemImpl.class);
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "FulfillmentGroupItemId")
    @GenericGenerator(
        name="FulfillmentGroupItemId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="FulfillmentGroupItemImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.order.domain.FulfillmentGroupItemImpl")
        }
    )
    @Column(name = "FULFILLMENT_GROUP_ITEM_ID")
    protected Long id;

    @ManyToOne(targetEntity = FulfillmentGroupImpl.class, optional=false)
    @JoinColumn(name = "FULFILLMENT_GROUP_ID")
    @Index(name="FGITEM_FG_INDEX", columnNames={"FULFILLMENT_GROUP_ID"})
    protected FulfillmentGroup fulfillmentGroup;

    //this needs to stay OrderItem in order to provide backwards compatibility for those implementations that place a BundleOrderItem
    @ManyToOne(targetEntity = OrderItemImpl.class, optional=false, cascade = CascadeType.REFRESH)
    @JoinColumn(name = "ORDER_ITEM_ID")
    @AdminPresentation(friendlyName = "FulfillmentGroupItemImpl_Order_Item", prominent = true, order = 1000, gridOrder = 1000)
    @AdminPresentationToOneLookup()
    protected OrderItem orderItem;

    @Column(name = "QUANTITY", nullable=false)
    @AdminPresentation(friendlyName = "FulfillmentGroupItemImpl_Quantity", prominent = true, order = 2000, gridOrder = 2000)
    protected int quantity;

    @Column(name = "STATUS")
    @Index(name="FGITEM_STATUS_INDEX", columnNames={"STATUS"})
    @AdminPresentation(friendlyName = "FulfillmentGroupItemImpl_Status", prominent = true, order = 3000, gridOrder = 3000)
    private String status;
    
    @OneToMany(fetch = FetchType.LAZY, targetEntity = TaxDetailImpl.class, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @JoinTable(name = "BLC_FG_ITEM_TAX_XREF", joinColumns = @JoinColumn(name = "FULFILLMENT_GROUP_ITEM_ID"), inverseJoinColumns = @JoinColumn(name = "TAX_DETAIL_ID"))
    @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
    protected List<TaxDetail> taxes = new ArrayList<TaxDetail>();
    
    @Column(name = "TOTAL_ITEM_TAX", precision=19, scale=5)
    @AdminPresentation(friendlyName = "FulfillmentGroupItemImpl_Total_Item_Tax", order=4000, fieldType=SupportedFieldType.MONEY)
    protected BigDecimal totalTax;

    @Column(name = "TOTAL_ITEM_AMOUNT", precision = 19, scale = 5)
    @AdminPresentation(friendlyName = "FulfillmentGroupItemImpl_Total_Item_Amount", order = 5000, fieldType = SupportedFieldType.MONEY)
    protected BigDecimal totalItemAmount;

    @Column(name = "TOTAL_ITEM_TAXABLE_AMOUNT", precision = 19, scale = 5)
    @AdminPresentation(friendlyName = "FulfillmentGroupItemImpl_Total_Item_Taxable_Amount", order = 6000, fieldType = SupportedFieldType.MONEY)
    protected BigDecimal totalItemTaxableAmount;

    @Column(name = "PRORATED_ORDER_ADJ")
    @AdminPresentation(friendlyName = "FulfillmentGroupItemImpl_Prorated_Adjustment", order = 7000, fieldType = SupportedFieldType.MONEY)
    protected BigDecimal proratedOrderAdjustment;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public FulfillmentGroup getFulfillmentGroup() {
        return fulfillmentGroup;
    }

    @Override
    public void setFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
        this.fulfillmentGroup = fulfillmentGroup;
    }

    @Override
    public OrderItem getOrderItem() {
        return orderItem;
    }

    @Override
    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public Money getRetailPrice() {
        return orderItem.getRetailPrice();
    }

    @Override
    public Money getSalePrice() {
        return orderItem.getSalePrice();
    }

    @Override
    public Money getPrice() {
        return orderItem.getAveragePrice();
    }

    protected Money convertToMoney(BigDecimal amount) {
        return amount == null ? null : BroadleafCurrencyUtils.getMoney(amount, orderItem.getOrder().getCurrency());
    }

    @Override
    public Money getTotalItemAmount() {
        return convertToMoney(totalItemAmount);
    }

    @Override
    public void setTotalItemAmount(Money amount) {
        totalItemAmount = amount.getAmount();
    }

    @Override
    public Money getProratedOrderAdjustmentAmount() {
        return convertToMoney(proratedOrderAdjustment);
    }

    @Override
    public void setProratedOrderAdjustmentAmount(Money proratedOrderAdjustment) {
        this.proratedOrderAdjustment = proratedOrderAdjustment.getAmount();
    }

    @Override
    public Money getTotalItemTaxableAmount() {
        return convertToMoney(totalItemTaxableAmount);
    }

    @Override
    public void setTotalItemTaxableAmount(Money taxableAmount) {
        totalItemTaxableAmount = taxableAmount.getAmount();
    }


    @Override
    public FulfillmentGroupStatusType getStatus() {
        return FulfillmentGroupStatusType.getInstance(this.status);
    }

    @Override
    public void setStatus(FulfillmentGroupStatusType status) {
        this.status = status.getType();
    }
    
    @Override
    public void removeAssociations() {
        if (getFulfillmentGroup() != null) {
            getFulfillmentGroup().getFulfillmentGroupItems().remove(this);
        }
        setFulfillmentGroup(null);
        setOrderItem(null);
    }

    @Override
    public List<TaxDetail> getTaxes() {
        return taxes;
    }

    @Override
    public void setTaxes(List<TaxDetail> taxes) {
        this.taxes = taxes;
    }
    
    @Override
    public Money getTotalTax() {
        return totalTax == null ? null : BroadleafCurrencyUtils.getMoney(totalTax, getFulfillmentGroup().getOrder().getCurrency());
    }

    @Override
    public void setTotalTax(Money totalTax) {
        this.totalTax = Money.toAmount(totalTax);
    }

    @Override
    public String getCurrencyCode() {
        return ((CurrencyCodeIdentifiable) fulfillmentGroup).getCurrencyCode();
    }

    public void checkCloneable(FulfillmentGroupItem fulfillmentGroupItem) throws CloneNotSupportedException, SecurityException, NoSuchMethodException {
        Method cloneMethod = fulfillmentGroupItem.getClass().getMethod("clone", new Class[]{});
        if (cloneMethod.getDeclaringClass().getName().startsWith("org.broadleafcommerce") && !orderItem.getClass().getName().startsWith("org.broadleafcommerce")) {
            //subclass is not implementing the clone method
            throw new CloneNotSupportedException("Custom extensions and implementations should implement clone in order to guarantee split and merge operations are performed accurately");
        }
    }

    @Override
    public FulfillmentGroupItem clone() {
        //this is likely an extended class - instantiate from the fully qualified name via reflection
        FulfillmentGroupItem clonedFulfillmentGroupItem;
        try {
            clonedFulfillmentGroupItem = (FulfillmentGroupItem) Class.forName(this.getClass().getName()).newInstance();
            try {
                checkCloneable(clonedFulfillmentGroupItem);
            } catch (CloneNotSupportedException e) {
                LOG.warn("Clone implementation missing in inheritance hierarchy outside of Broadleaf: " + clonedFulfillmentGroupItem.getClass().getName(), e);
            }

            clonedFulfillmentGroupItem.setFulfillmentGroup(getFulfillmentGroup());
            clonedFulfillmentGroupItem.setOrderItem(getOrderItem());
            clonedFulfillmentGroupItem.setQuantity(getQuantity());
            clonedFulfillmentGroupItem.setTotalItemAmount(getTotalItemAmount());
            clonedFulfillmentGroupItem.setTotalItemTaxableAmount(getTotalItemTaxableAmount());
            if (getStatus() != null) {
                clonedFulfillmentGroupItem.setStatus(getStatus());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return clonedFulfillmentGroupItem;
    }

    @Override
    public boolean getHasProratedOrderAdjustments() {
        if (proratedOrderAdjustment != null) {
            return (proratedOrderAdjustment.compareTo(BigDecimal.ZERO) != 0);
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        FulfillmentGroupItemImpl other = (FulfillmentGroupItemImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (orderItem == null) {
            if (other.orderItem != null) {
                return false;
            }
        } else if (!orderItem.equals(other.orderItem)) {
            return false;
        }
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
