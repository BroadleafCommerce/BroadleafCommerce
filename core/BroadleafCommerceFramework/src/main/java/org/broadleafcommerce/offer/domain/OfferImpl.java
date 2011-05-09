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
package org.broadleafcommerce.offer.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.broadleafcommerce.money.Money;
import org.broadleafcommerce.offer.service.type.OfferDeliveryType;
import org.broadleafcommerce.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.offer.service.type.OfferType;
import org.broadleafcommerce.presentation.AdminPresentation;
import org.broadleafcommerce.presentation.SupportedFieldType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

@Entity
@Table(name = "BLC_OFFER")
@Inheritance(strategy=InheritanceType.JOINED)
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
public class OfferImpl implements Offer {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "OfferId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "OfferId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "OfferImpl", allocationSize = 50)
    @Column(name = "OFFER_ID")
    protected Long id;

    @Column(name = "OFFER_NAME", nullable=false)
    @Index(name="OFFER_NAME_INDEX", columnNames={"OFFER_NAME"})
    @AdminPresentation(friendlyName="Offer Name", order=1, group="Description", prominent=true)
    protected String name;

    @Column(name = "OFFER_DESCRIPTION")
    @AdminPresentation(friendlyName="Offer Description", order=2, group="Description", largeEntry=true, prominent=true)
    protected String description;

    @Column(name = "OFFER_TYPE", nullable=false)
    @Index(name="OFFER_TYPE_INDEX", columnNames={"OFFER_TYPE"})
    @AdminPresentation(friendlyName="Offer Type", order=3, group="Description", prominent=true, fieldType=SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.offer.service.type.OfferType")
    protected String type;

    @Column(name = "OFFER_DISCOUNT_TYPE")
    @Index(name="OFFER_DISCOUNT_INDEX", columnNames={"OFFER_DISCOUNT_TYPE"})
    @AdminPresentation(friendlyName="Offer Discount Type", order=4, group="Amount", fieldType=SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.offer.service.type.OfferDiscountType")
    protected String discountType;

    @Column(name = "OFFER_VALUE", nullable=false)
    @AdminPresentation(friendlyName="Offer Value", order=5, group="Amount", prominent=true)
    protected BigDecimal value;

    @Column(name = "OFFER_PRIORITY")
    @AdminPresentation(friendlyName="Offer Priority", group="Description")
    protected int priority;

    @Column(name = "START_DATE")
    @AdminPresentation(friendlyName="Offer Start Date", group="Activity Range", order=1)
    protected Date startDate;

    @Column(name = "END_DATE")
    @AdminPresentation(friendlyName="Offer End Date", group="Activity Range", order=2)
    protected Date endDate;

    @Column(name = "STACKABLE")
    @AdminPresentation(friendlyName="Offer Stackable", group="Application")
    protected boolean stackable;

    @Column(name = "TARGET_SYSTEM")
    @AdminPresentation(friendlyName="Offer Target System", group="Description")
    protected String targetSystem;

    @Column(name = "APPLY_TO_SALE_PRICE")
    @AdminPresentation(friendlyName="Apply To Sale Price", group="Application")
    protected boolean applyToSalePrice;

    @Column(name = "APPLIES_TO_RULES")
    @AdminPresentation(friendlyName="Offer Order Rules", group="Application", largeEntry=true)
    protected String appliesToOrderRules;

    @Column(name = "APPLIES_WHEN_RULES")
    @AdminPresentation(friendlyName="Offer Customer Rules", group="Application", largeEntry=true)
    protected String appliesToCustomerRules;

    @Column(name = "APPLY_OFFER_TO_MARKED_ITEMS")
    @AdminPresentation(friendlyName="Apply To Marked Items", group="Application")
    protected boolean applyDiscountToMarkedItems;

    @Column(name = "COMBINABLE_WITH_OTHER_OFFERS")
    @AdminPresentation(friendlyName="Offer Combinable", group="Application")
    protected boolean combinableWithOtherOffers;  // no offers can be applied on top of this offer; if false, stackable has to be false also

    @Column(name = "OFFER_DELIVERY_TYPE", nullable=false)
    @AdminPresentation(friendlyName="Offer Delivery Type", group="Description", fieldType=SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.offer.service.type.OfferDeliveryType")
    @Index(name="OFFER_DELIVERY_INDEX", columnNames={"OFFER_DELIVERY_TYPE"})
    protected String deliveryType;

    @Column(name = "MAX_USES")
    @AdminPresentation(friendlyName="Offer Max Uses", order=7, group="Description")
    protected int maxUses;

    @Column(name = "USES")
    @AdminPresentation(friendlyName="Offer Current Uses", order=6, group="Description")
    protected int uses;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OfferType getType() {
        return OfferType.getInstance(type);
    }

    public void setType(OfferType type) {
        this.type = type.getType();
    }

    public OfferDiscountType getDiscountType() {
        return OfferDiscountType.getInstance(discountType);
    }

    public void setDiscountType(OfferDiscountType discountType) {
        this.discountType = discountType.getType();
    }

    public Money getValue() {
        return value == null ? null : new Money(value);
    }

    public void setValue(Money value) {
        this.value = Money.toAmount(value);
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Returns true if this offer can be stacked on top of another offer.  Stackable is evaluated
     * against offers with the same offer type.
     *
     * @return true if stackable, otherwise false
     */
    public boolean isStackable() {
        return stackable;
    }

    /**
     * Sets the stackable value for this offer.
     *
     * @param stackable
     */
    public void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    public boolean getStackable(){
    	return stackable;
    }
    
    public String getTargetSystem() {
        return targetSystem;
    }

    public void setTargetSystem(String targetSystem) {
        this.targetSystem = targetSystem;
    }

    public boolean getApplyDiscountToSalePrice() {
        return applyToSalePrice;
    }

    public void setApplyDiscountToSalePrice(boolean applyToSalePrice) {
        this.applyToSalePrice=applyToSalePrice;
    }

    public String getAppliesToOrderRules() {
        return appliesToOrderRules;
    }

    public void setAppliesToOrderRules(String appliesToOrderRules) {
        this.appliesToOrderRules = appliesToOrderRules;
    }

    public String getAppliesToCustomerRules() {
        return appliesToCustomerRules;
    }

    public void setAppliesToCustomerRules(String appliesToCustomerRules) {
        this.appliesToCustomerRules = appliesToCustomerRules;
    }

    public boolean isApplyDiscountToMarkedItems() {
        return applyDiscountToMarkedItems;
    }

    public boolean getApplyDiscountToMarkedItems() {
    	return applyDiscountToMarkedItems;
    }
    
    public void setApplyDiscountToMarkedItems(boolean applyDiscountToMarkedItems) {
        this.applyDiscountToMarkedItems = applyDiscountToMarkedItems;
    }

    /**
     * Returns true if this offer can be combined with other offers in the order.
     *
     * @return true if combinableWithOtherOffers, otherwise false
     */
    public boolean isCombinableWithOtherOffers() {
        return combinableWithOtherOffers;
    }

    /**
     * Sets the combinableWithOtherOffers value for this offer.
     *
     * @param combinableWithOtherOffers
     */
    public void setCombinableWithOtherOffers(boolean combinableWithOtherOffers) {
        this.combinableWithOtherOffers = combinableWithOtherOffers;
    }

    public boolean getCombinableWithOtherOffers() {
    	return combinableWithOtherOffers;
    }
    
    public OfferDeliveryType getDeliveryType() {
        return OfferDeliveryType.getInstance(deliveryType);
    }

    public void setDeliveryType(OfferDeliveryType deliveryType) {
        this.deliveryType = deliveryType.getType();
    }

    public int getMaxUses() {
        return maxUses;
    }

    public void setMaxUses(int maxUses) {
        this.maxUses = maxUses;
    }

    public int getUses() {
        return uses;
    }

    public void setUses(int uses) {
        this.uses = uses;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((appliesToCustomerRules == null) ? 0 : appliesToCustomerRules.hashCode());
        result = prime * result + ((appliesToOrderRules == null) ? 0 : appliesToOrderRules.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        OfferImpl other = (OfferImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (appliesToCustomerRules == null) {
            if (other.appliesToCustomerRules != null)
                return false;
        } else if (!appliesToCustomerRules.equals(other.appliesToCustomerRules))
            return false;
        if (appliesToOrderRules == null) {
            if (other.appliesToOrderRules != null)
                return false;
        } else if (!appliesToOrderRules.equals(other.appliesToOrderRules))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }



}
