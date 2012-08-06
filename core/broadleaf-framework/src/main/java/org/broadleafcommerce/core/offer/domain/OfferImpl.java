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

package org.broadleafcommerce.core.offer.domain;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationOverride;
import org.broadleafcommerce.common.presentation.AdminPresentationOverrides;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.core.offer.service.type.OfferDeliveryType;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferItemRestrictionRuleType;
import org.broadleafcommerce.core.offer.service.type.OfferType;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "BLC_OFFER")
@Inheritance(strategy=InheritanceType.JOINED)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@AdminPresentationOverrides(
        {@AdminPresentationOverride(name="targetItemCriteria.offer", value=@AdminPresentation(excluded = true))}
)
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "baseOffer")
public class OfferImpl implements Offer {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "OfferId")
    @GenericGenerator(
        name="OfferId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="table_name", value="SEQUENCE_GENERATOR"),
            @Parameter(name="segment_column_name", value="ID_NAME"),
            @Parameter(name="value_column_name", value="ID_VAL"),
            @Parameter(name="segment_value", value="OfferImpl"),
            @Parameter(name="increment_size", value="50"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.offer.domain.OfferImpl")
        }
    )
    @Column(name = "OFFER_ID")
    @AdminPresentation(friendlyName="Offer Id", order=1, group="Description", groupOrder=1, visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @Column(name = "OFFER_NAME", nullable=false)
    @Index(name="OFFER_NAME_INDEX", columnNames={"OFFER_NAME"})
    @AdminPresentation(friendlyName="Offer Name", order=1, group="Description", prominent=true, groupOrder=1)
    protected String name;

    @Column(name = "OFFER_DESCRIPTION")
    @AdminPresentation(friendlyName="Offer Description", order=2, group="Description", largeEntry=true, prominent=true, groupOrder=1)
    protected String description;

    @Column(name = "OFFER_TYPE", nullable=false)
    @Index(name="OFFER_TYPE_INDEX", columnNames={"OFFER_TYPE"})
    @AdminPresentation(friendlyName="Offer Type", order=3, group="Description", prominent=true, fieldType=SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.core.offer.service.type.OfferType", groupOrder=1)
    protected String type;

    @Column(name = "OFFER_DISCOUNT_TYPE")
    @Index(name="OFFER_DISCOUNT_INDEX", columnNames={"OFFER_DISCOUNT_TYPE"})
    @AdminPresentation(friendlyName="Offer Discount Type", order=4, group="Amount", fieldType=SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.core.offer.service.type.OfferDiscountType", groupOrder=2)
    protected String discountType;

    @Column(name = "OFFER_VALUE", nullable=false)
    @AdminPresentation(friendlyName="Offer Value", order=5, group="Amount", prominent=true, groupOrder=2)
    protected BigDecimal value;

    @Column(name = "OFFER_PRIORITY")
    @AdminPresentation(friendlyName="Offer Priority", group="Description", groupOrder=1)
    protected int priority;

    @Column(name = "START_DATE")
    @AdminPresentation(friendlyName="Offer Start Date", group="Activity Range", order=1, groupOrder=3)
    protected Date startDate;

    @Column(name = "END_DATE")
    @AdminPresentation(friendlyName="Offer End Date", group="Activity Range", order=2, groupOrder=3)
    protected Date endDate;

    @Column(name = "STACKABLE")
    @AdminPresentation(friendlyName="Offer Stackable", group="Application", groupOrder=4)
    protected boolean stackable;

    @Column(name = "TARGET_SYSTEM")
    @AdminPresentation(friendlyName="Offer Target System", group="Description", groupOrder=1)
    protected String targetSystem;

    @Column(name = "APPLY_TO_SALE_PRICE")
    @AdminPresentation(friendlyName="Apply To Sale Price", group="Application", groupOrder=4)
    protected boolean applyToSalePrice;

    @Column(name = "APPLIES_TO_RULES")
    @Deprecated
    protected String appliesToOrderRules;

    @Column(name = "APPLIES_WHEN_RULES")
    @Deprecated
    protected String appliesToCustomerRules;

    @Column(name = "APPLY_OFFER_TO_MARKED_ITEMS")
    @AdminPresentation(excluded = true)
    @Deprecated
    protected boolean applyDiscountToMarkedItems;
    
    @Column(name = "COMBINABLE_WITH_OTHER_OFFERS")
    @AdminPresentation(friendlyName="Offer Combinable", group="Application", groupOrder=4, visibility =VisibilityEnum.HIDDEN_ALL)
    protected boolean combinableWithOtherOffers;  // no offers can be applied on top of this offer; if false, stackable has to be false also

    @Column(name = "OFFER_DELIVERY_TYPE", nullable=false)
    @AdminPresentation(friendlyName="Offer Delivery Type", group="Description", fieldType=SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.core.offer.service.type.OfferDeliveryType", groupOrder=1)
    @Index(name="OFFER_DELIVERY_INDEX", columnNames={"OFFER_DELIVERY_TYPE"})
    protected String deliveryType;

    @Column(name = "MAX_USES")
    @AdminPresentation(friendlyName="Offer Max Uses Per Order", order=7, group="Description", groupOrder=2)
    protected int maxUsesPerOrder;

    @Column(name = "MAX_USES_PER_CUSTOMER")
    @AdminPresentation(friendlyName="Max Uses Per Customer", order=7, group="Description", groupOrder=1)
    protected Long maxUsesPerCustomer;

    @Column(name = "USES")
    @AdminPresentation(friendlyName="Offer Current Uses", visibility =VisibilityEnum.HIDDEN_ALL)
    @Deprecated
    protected int uses;
    
    @Column(name = "OFFER_ITEM_QUALIFIER_RULE")
    @AdminPresentation(friendlyName="Item Qualifier Rule", group="Application", groupOrder=4, fieldType=SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.core.offer.service.type.OfferItemRestrictionRuleType")
    protected String offerItemQualifierRuleType;
    
    @Column(name = "OFFER_ITEM_TARGET_RULE")
    @AdminPresentation(friendlyName="Item Target Rule", group="Application", groupOrder=4, fieldType= SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.core.offer.service.type.OfferItemRestrictionRuleType")
    protected String offerItemTargetRuleType;
    
    @OneToMany(fetch = FetchType.LAZY, targetEntity = OfferItemCriteriaImpl.class, cascade={CascadeType.ALL})
    @JoinTable(name = "BLC_QUAL_CRIT_OFFER_XREF", joinColumns = @JoinColumn(name = "OFFER_ID"), inverseJoinColumns = @JoinColumn(name = "OFFER_ITEM_CRITERIA_ID"))
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    protected Set<OfferItemCriteria> qualifyingItemCriteria = new HashSet<OfferItemCriteria>();
    
    @ManyToOne(targetEntity = OfferItemCriteriaImpl.class, cascade={CascadeType.ALL})
    @AdminPresentation(friendlyName="Target Item Criteria", group="Application", groupOrder=4, visibility =VisibilityEnum.HIDDEN_ALL)
    @JoinTable(name = "BLC_TAR_CRIT_OFFER_XREF", joinColumns = @JoinColumn(name = "OFFER_ID"), inverseJoinColumns = @JoinColumn(name = "OFFER_ITEM_CRITERIA_ID"))
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    protected OfferItemCriteria targetItemCriteria;
    
    @Column(name = "TOTALITARIAN_OFFER")
    @AdminPresentation(friendlyName="Totalitarian Offer", group="Application", groupOrder=4, visibility =VisibilityEnum.HIDDEN_ALL)
    protected Boolean totalitarianOffer;
    
    @ManyToMany(targetEntity = OfferRuleImpl.class, cascade = {CascadeType.ALL})
    @JoinTable(name = "BLC_OFFER_RULE_MAP", inverseJoinColumns = @JoinColumn(name = "OFFER_RULE_ID", referencedColumnName = "OFFER_RULE_ID"))
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @MapKeyColumn(name = "MAP_KEY", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    Map<String, OfferRule> offerMatchRules = new HashMap<String, OfferRule>();
    
    @Column(name = "USE_NEW_FORMAT")
    @AdminPresentation(friendlyName="Treat As New Format", group="Application", groupOrder=4, visibility = VisibilityEnum.HIDDEN_ALL)
    protected Boolean treatAsNewFormat;
    
    @Column(name = "QUALIFYING_ITEM_MIN_TOTAL", precision=19, scale=5)
    @AdminPresentation(friendlyName="Qualifying Item Subtotal",group="Application", groupOrder=5)    
    protected BigDecimal qualifyingItemSubTotal;

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
    
    public OfferItemRestrictionRuleType getOfferItemQualifierRuleType() {
        return OfferItemRestrictionRuleType.getInstance(offerItemQualifierRuleType);
    }

    public void setOfferItemQualifierRuleType(OfferItemRestrictionRuleType restrictionRuleType) {
        this.offerItemQualifierRuleType = restrictionRuleType.getType();
    }
    
    public OfferItemRestrictionRuleType getOfferItemTargetRuleType() {
        return OfferItemRestrictionRuleType.getInstance(offerItemTargetRuleType);
    }

    public void setOfferItemTargetRuleType(OfferItemRestrictionRuleType restrictionRuleType) {
        this.offerItemTargetRuleType = restrictionRuleType.getType();
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
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

    @Deprecated
    @JsonIgnore
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

    @Deprecated
    public String getAppliesToOrderRules() {
        return appliesToOrderRules;
    }

    @Deprecated
    public void setAppliesToOrderRules(String appliesToOrderRules) {
        this.appliesToOrderRules = appliesToOrderRules;
    }

    @Deprecated
    public String getAppliesToCustomerRules() {
        return appliesToCustomerRules;
    }

    @Deprecated
    public void setAppliesToCustomerRules(String appliesToCustomerRules) {
        this.appliesToCustomerRules = appliesToCustomerRules;
    }

	@Deprecated
    public boolean isApplyDiscountToMarkedItems() {
        return applyDiscountToMarkedItems;
    }

    @Deprecated
    @JsonIgnore
    public boolean getApplyDiscountToMarkedItems() {
    	return applyDiscountToMarkedItems;
    }
    
    @Deprecated
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

    @Deprecated
    @JsonIgnore
    public boolean getCombinableWithOtherOffers() {
    	return combinableWithOtherOffers;
    }
    
    public OfferDeliveryType getDeliveryType() {
        return OfferDeliveryType.getInstance(deliveryType);
    }

    public void setDeliveryType(OfferDeliveryType deliveryType) {
        this.deliveryType = deliveryType.getType();
    }

    public Long getMaxUsesPerCustomer() {
        return maxUsesPerCustomer;
    }

    public void setMaxUsesPerCustomer(Long maxUsesPerCustomer) {
        this.maxUsesPerCustomer = maxUsesPerCustomer;
    }

    public int getMaxUsesPerOrder() {
        return maxUsesPerOrder;
    }

    public void setMaxUsesPerOrder(int maxUsesPerOrder) {
        this.maxUsesPerOrder = maxUsesPerOrder;
    }


    public int getMaxUses() {
        return maxUsesPerOrder;
    }

    public void setMaxUses(int maxUses) {
        this.maxUsesPerOrder = maxUses;
    }

    @Deprecated
    public int getUses() {
        return uses;
    }

    @Deprecated
    public void setUses(int uses) {
        this.uses = uses;
    }

    public Set<OfferItemCriteria> getQualifyingItemCriteria() {
		return qualifyingItemCriteria;
	}

	public void setQualifyingItemCriteria(Set<OfferItemCriteria> qualifyingItemCriteria) {
		this.qualifyingItemCriteria = qualifyingItemCriteria;
	}

	public OfferItemCriteria getTargetItemCriteria() {
		return targetItemCriteria;
	}

	public void setTargetItemCriteria(OfferItemCriteria targetItemCriteria) {
		this.targetItemCriteria = targetItemCriteria;
	}

	public Boolean isTotalitarianOffer() {
		return totalitarianOffer;
	}

	public void setTotalitarianOffer(Boolean totalitarianOffer) {
		this.totalitarianOffer = totalitarianOffer;
	}

	public Map<String, OfferRule> getOfferMatchRules() {
		if (offerMatchRules == null) {
			offerMatchRules = new HashMap<String, OfferRule>();
		}
		return offerMatchRules;
	}

	public void setOfferMatchRules(Map<String, OfferRule> offerMatchRules) {
		this.offerMatchRules = offerMatchRules;
	}

	public Boolean getTreatAsNewFormat() {
		return treatAsNewFormat;
	}

	public void setTreatAsNewFormat(Boolean treatAsNewFormat) {
		this.treatAsNewFormat = treatAsNewFormat;
	}

	public Money getQualifyingItemSubTotal() {
		return qualifyingItemSubTotal == null ? null : new Money(qualifyingItemSubTotal);
	}

	public void setQualifyingItemSubTotal(Money qualifyingItemSubTotal) {
		this.qualifyingItemSubTotal = Money.toAmount(qualifyingItemSubTotal);
	}
	
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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
