/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.offer.domain;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.i18n.service.DynamicTranslationProvider;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.persistence.ArchiveStatus;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.AdminPresentationMapField;
import org.broadleafcommerce.common.presentation.AdminPresentationMapFields;
import org.broadleafcommerce.common.presentation.ConfigurationItem;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.RuleIdentifier;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.util.DateUtil;
import org.broadleafcommerce.core.offer.service.type.OfferDiscountType;
import org.broadleafcommerce.core.offer.service.type.OfferItemRestrictionRuleType;
import org.broadleafcommerce.core.offer.service.type.OfferType;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.SQLDelete;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "BLC_OFFER")
@Inheritance(strategy=InheritanceType.JOINED)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blOffers")
@SQLDelete(sql="UPDATE BLC_OFFER SET ARCHIVED = 'Y' WHERE OFFER_ID = ?")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
public class OfferImpl implements Offer, AdminMainEntity, OfferAdminPresentation {

    public static final String EXCLUDE_OFFERCODE_COPY_HINT = "exclude-offerCodes";
    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "OfferId")
    @GenericGenerator(
        name="OfferId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="OfferImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.offer.domain.OfferImpl")
        }
    )
    @Column(name = "OFFER_ID")
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Id", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @OneToMany(mappedBy = "offer", targetEntity = OfferCodeImpl.class, cascade = { CascadeType.ALL })
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blOffers")
    @BatchSize(size = 50)
    @AdminPresentationCollection(friendlyName = "offerCodeTitle",
        group = GroupName.Codes, order = FieldOrder.OfferCodes,
        addType = AddMethodType.PERSIST)
    protected List<OfferCode> offerCodes = new ArrayList<OfferCode>(100);

    @Column(name = "OFFER_NAME", nullable=false)
    @Index(name="OFFER_NAME_INDEX", columnNames={"OFFER_NAME"})
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Name",
        group = GroupName.Description, order = FieldOrder.Name,
        prominent = true, gridOrder = 1, translatable = true,
        defaultValue = "New Offer")
    protected String name;

    @Column(name = "OFFER_DESCRIPTION")
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Description",
        group = GroupName.Description, order = FieldOrder.Description,
        largeEntry = true, fieldType = SupportedFieldType.DESCRIPTION, defaultValue = "")
    protected String description;

    @Column(name = "MARKETING_MESSASGE")
    @Index(name = "OFFER_MARKETING_MESSAGE_INDEX", columnNames = { "MARKETING_MESSASGE" })
    @AdminPresentation(friendlyName = "OfferImpl_marketingMessage",
        group = GroupName.Marketing, order = FieldOrder.Message,
        translatable = true, defaultValue = "")
    protected String marketingMessage;

    @Column(name = "OFFER_TYPE", nullable=false)
    @Index(name="OFFER_TYPE_INDEX", columnNames={"OFFER_TYPE"})
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Type",
        group = GroupName.Description, order = FieldOrder.OfferType,
        fieldType=SupportedFieldType.BROADLEAF_ENUMERATION, 
        broadleafEnumeration="org.broadleafcommerce.core.offer.service.type.OfferType",
        defaultValue = "ORDER")
    protected String type;

    @Column(name = "OFFER_DISCOUNT_TYPE")
    @Index(name="OFFER_DISCOUNT_INDEX", columnNames={"OFFER_DISCOUNT_TYPE"})
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Discount_Type",
        group = GroupName.Description, order = FieldOrder.DiscountType,
        requiredOverride = RequiredOverride.REQUIRED,
        fieldType=SupportedFieldType.BROADLEAF_ENUMERATION,
        broadleafEnumeration="org.broadleafcommerce.core.offer.service.type.OfferDiscountType")
    protected String discountType;

    @Column(name = "OFFER_VALUE", nullable=false, precision=19, scale=5)
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Value",
        group = GroupName.Description, order = FieldOrder.Amount,
        prominent = true, gridOrder = 4,
        defaultValue = "0.00000")
    protected BigDecimal value;

    @Column(name = "OFFER_PRIORITY")
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Priority",
        group = GroupName.Advanced, order = FieldOrder.Priority)
    protected Integer priority;

    @Column(name = "START_DATE")
    @Index(name="idx_BLOF_START_DATE", columnNames={"START_DATE"})
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Start_Date",
        group = GroupName.ActivityRange, order = FieldOrder.StartDate,
        prominent = true, gridOrder = 2,
        defaultValue = "today")
    protected Date startDate;

    @Column(name = "END_DATE")
    @AdminPresentation(friendlyName = "OfferImpl_Offer_End_Date",
        group = GroupName.ActivityRange, order = FieldOrder.EndDate,
        validationConfigurations = { 
            @ValidationConfiguration(
                validationImplementation = "blAfterStartDateValidator",
                configurationItems = {
                        @ConfigurationItem(itemName = "otherField", itemValue = "startDate")
                }) 
        })
    protected Date endDate;

    @Column(name = "TARGET_SYSTEM")
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Target_System",
            visibility = VisibilityEnum.HIDDEN_ALL)
    protected String targetSystem;

    @Column(name = "APPLY_TO_SALE_PRICE")
    @AdminPresentation(friendlyName = "OfferImpl_Apply_To_Sale_Price",
            group = GroupName.Advanced,
            defaultValue = "true")
    protected Boolean applyToSalePrice = true;

    @Column(name = "APPLY_TO_CHILD_ITEMS")
    @AdminPresentation(friendlyName = "OfferImpl_Apply_To_Child_Items",
            tooltip = "OfferImpl_Apply_To_Child_Items_tooltip",
            group = GroupName.Advanced,
            defaultValue = "false")
    protected Boolean applyToChildItems = false;

    /**
     * Determines if other offers of the same type can be combined with this offer. 
     */
    @Column(name = "COMBINABLE_WITH_OTHER_OFFERS")
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Combinable",
        tooltip = "OfferImplCombinableWithOtherOffers_tooltip",
            group = GroupName.CombineStack,
            order = FieldOrder.CombinableWithOtherOffers,
            defaultValue = "true")
    protected Boolean combinableWithOtherOffers = true;

    @Column(name = "AUTOMATICALLY_ADDED")
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Automatically_Added",
            tooltip = "OfferImpl_Offer_Automatically_Added_tooltip",
            group = GroupName.Customer, order = FieldOrder.AutomaticallyAdded,
            fieldType = SupportedFieldType.BOOLEAN, defaultValue = "false")
    protected Boolean automaticallyAdded = false;

    @Column(name = "MAX_USES")
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Max_Uses_Per_Order",
        tooltip = "OfferImplMaxUsesPerOrder_tooltip", order = FieldOrder.MaxUsesPerOrder,
        group = GroupName.Restrictions)
    protected Integer maxUsesPerOrder;

    @Column(name = "MAX_USES_PER_CUSTOMER")
    @AdminPresentation(friendlyName = "OfferImpl_Max_Uses_Per_Customer",
        group = GroupName.Restrictions, order = FieldOrder.MaxUsesPerCustomer,
        tooltip = "OfferImplMaxUsesPerCustomer_tooltip",
        defaultValue = "0")
    protected Long maxUsesPerCustomer;
    
    @Column(name = "OFFER_ITEM_QUALIFIER_RULE")
    @AdminPresentation(friendlyName = "OfferImpl_Item_Qualifier_Rule",
        group = GroupName.QualifierRuleRestriction,
        tooltip = "OfferItemRestrictionRuleType_tooltip",
        fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
        broadleafEnumeration = "org.broadleafcommerce.core.offer.service.type.OfferItemRestrictionRuleType",
        defaultValue = "NONE",
        visibility = VisibilityEnum.HIDDEN_ALL)
    protected String offerItemQualifierRuleType;

    @Column(name = "QUALIFYING_ITEM_MIN_TOTAL", precision=19, scale=5)
    @AdminPresentation(friendlyName="OfferImpl_Qualifying_Item_Subtotal",
        group = GroupName.QualifierRuleRestriction, order = FieldOrder.QualifyingItemSubTotal,
        defaultValue = "0.00000")
    protected BigDecimal qualifyingItemSubTotal;

    @Column(name = "ORDER_MIN_TOTAL", precision=19, scale=5)
    @AdminPresentation(friendlyName="OfferImpl_Order_Subtotal",
        tooltip = "OfferImplMinOrderSubtotal_tooltip",
        group = GroupName.Restrictions, order = FieldOrder.OrderMinSubTotal,
        defaultValue = "0.00000")
    protected BigDecimal orderMinSubTotal;

    @Column(name = "TARGET_MIN_TOTAL", precision=19, scale=5)
    @AdminPresentation(friendlyName="OfferImpl_Target_Subtotal",
            tooltip = "OfferImplMinTargetSubtotal_tooltip",
            group = GroupName.Restrictions, order = FieldOrder.TargetMinSubTotal,
            defaultValue = "0.00000")
    protected BigDecimal targetMinSubTotal;

    @Column(name = "OFFER_ITEM_TARGET_RULE")
    @AdminPresentation(friendlyName = "OfferImpl_Item_Target_Rule",
        group = GroupName.CombineStack,
        tooltip = "OfferItemRestrictionRuleType_tooltip",
        fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
        broadleafEnumeration = "org.broadleafcommerce.core.offer.service.type.OfferItemRestrictionRuleType",
        defaultValue = "NONE",
        visibility = VisibilityEnum.HIDDEN_ALL)
    protected String offerItemTargetRuleType;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "offer", targetEntity = OfferQualifyingCriteriaXrefImpl.class, cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blOffers")
    @AdminPresentation(friendlyName = "OfferImpl_Qualifying_Item_Rule",
        tab = TabName.Qualifiers,
        fieldType = SupportedFieldType.RULE_WITH_QUANTITY,
        ruleIdentifier = RuleIdentifier.ORDERITEM)
    protected Set<OfferQualifyingCriteriaXref> qualifyingItemCriteria = new HashSet<OfferQualifyingCriteriaXref>();

    @Transient
    protected Set<OfferItemCriteria> legacyQualifyingItemCriteria = new HashSet<OfferItemCriteria>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "offer", targetEntity = OfferTargetCriteriaXrefImpl.class, cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blOffers")
    @AdminPresentation(friendlyName = "OfferImpl_Target_Item_Rule",
        group = GroupName.RuleConfiguration,
        fieldType = SupportedFieldType.RULE_WITH_QUANTITY, 
        ruleIdentifier = RuleIdentifier.ORDERITEM,
        validationConfigurations = @ValidationConfiguration(validationImplementation = "blOfferTargetCriteriaItemValidator"))
    protected Set<OfferTargetCriteriaXref> targetItemCriteria = new HashSet<OfferTargetCriteriaXref>();

    @Transient
    protected Set<OfferItemCriteria> legacyTargetItemCriteria = new HashSet<OfferItemCriteria>();
    
    @Column(name = "TOTALITARIAN_OFFER")
    @AdminPresentation(friendlyName = "OfferImpl_Totalitarian_Offer",
        group = GroupName.Advanced,
        visibility = VisibilityEnum.HIDDEN_ALL, defaultValue = "false")
    protected Boolean totalitarianOffer = false;

    @Column(name = "REQUIRES_RELATED_TAR_QUAL")
    @AdminPresentation(friendlyName = "OfferImpl_Requires_Related_Target_And_Qualifiers",
        group = GroupName.ShouldBeRelated,
        tooltip = "OfferImplRelatedTargetQualifier_tooltip",
        visibility = VisibilityEnum.HIDDEN_ALL, defaultValue = "false")
    protected Boolean requiresRelatedTargetAndQualifiers = false;

    @OneToMany(mappedBy = "offer", targetEntity = OfferOfferRuleXrefImpl.class, cascade = { CascadeType.ALL })
    @MapKey(name = "key")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blOffers")
    @AdminPresentationMapFields(
        toOneTargetProperty = "offerRule",
        toOneParentProperty = "offer",
        mapDisplayFields = {
            @AdminPresentationMapField(
                fieldName = RuleIdentifier.CUSTOMER_FIELD_KEY,
                fieldPresentation = @AdminPresentation(fieldType = SupportedFieldType.RULE_SIMPLE,
                    group = GroupName.Customer, order = FieldOrder.CustomerRule,
                    ruleIdentifier = RuleIdentifier.CUSTOMER, friendlyName = "OfferImpl_Customer_Rule")
            ),
            @AdminPresentationMapField(
            fieldName = RuleIdentifier.TIME_FIELD_KEY,
                fieldPresentation = @AdminPresentation(fieldType = SupportedFieldType.RULE_SIMPLE_TIME,
                    group = GroupName.ActivityRange, order = FieldOrder.TimeRule,
                    ruleIdentifier = RuleIdentifier.TIME, friendlyName = "OfferImpl_Time_Rule")
            ),
            @AdminPresentationMapField(
                fieldName = RuleIdentifier.ORDER_FIELD_KEY,
                fieldPresentation = @AdminPresentation(fieldType = SupportedFieldType.RULE_SIMPLE, 
                    group = GroupName.Restrictions,
                    ruleIdentifier = RuleIdentifier.ORDER, friendlyName = "OfferImpl_Order_Rule")
            ),
            @AdminPresentationMapField(
                fieldName = RuleIdentifier.FULFILLMENT_GROUP_FIELD_KEY,
                fieldPresentation = @AdminPresentation(fieldType = SupportedFieldType.RULE_SIMPLE, 
                    group = GroupName.RuleConfiguration,
                    ruleIdentifier = RuleIdentifier.FULFILLMENTGROUP, friendlyName = "OfferImpl_FG_Rule")
            )
        }
    )
    Map<String, OfferOfferRuleXref> offerMatchRules = new HashMap<String, OfferOfferRuleXref>();

    @Embedded
    protected ArchiveStatus archiveStatus = new ArchiveStatus();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public OfferType getType() {
        return OfferType.getInstance(type);
    }

    @Override
    public void setType(OfferType type) {
        this.type = type.getType();
    }

    @Override
    public OfferDiscountType getDiscountType() {
        return OfferDiscountType.getInstance(discountType);
    }

    @Override
    public void setDiscountType(OfferDiscountType discountType) {
        this.discountType = discountType.getType();
    }
    
    @Override
    public OfferItemRestrictionRuleType getOfferItemQualifierRuleType() {
        OfferItemRestrictionRuleType returnType = OfferItemRestrictionRuleType.getInstance(offerItemQualifierRuleType);
        if (returnType == null) {
            return OfferItemRestrictionRuleType.NONE;
        } else {
            return returnType;
        }
    }

    @Override
    public void setOfferItemQualifierRuleType(OfferItemRestrictionRuleType restrictionRuleType) {
        this.offerItemQualifierRuleType = restrictionRuleType.getType();
    }
    
    @Override
    public OfferItemRestrictionRuleType getOfferItemTargetRuleType() {
        OfferItemRestrictionRuleType returnType = OfferItemRestrictionRuleType.getInstance(offerItemTargetRuleType);
        if (returnType == null) {
            return OfferItemRestrictionRuleType.NONE;
        } else {
            return returnType;
        }
    }

    @Override
    public void setOfferItemTargetRuleType(OfferItemRestrictionRuleType restrictionRuleType) {
        this.offerItemTargetRuleType = restrictionRuleType.getType();
    }

    @Override
    public BigDecimal getValue() {
        return value;
    }

    @Override
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public int getPriority() {
        // Treat null as the maximum value minus one to allow for someone to create a
        // priority that is even less than an unset priority.
        return priority == null ? Integer.MAX_VALUE - 1 : priority;
    }

    @Override
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public Date getStartDate() {
        if ('Y'==getArchived()) {
            return null;
        }
        return startDate;
    }

    @Override
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    @Override
    public String getTargetSystem() {
        return targetSystem;
    }

    @Override
    public void setTargetSystem(String targetSystem) {
        this.targetSystem = targetSystem;
    }

    @Override
    public boolean getApplyDiscountToSalePrice() {
        return applyToSalePrice == null ? false : applyToSalePrice;
    }

    @Override
    public void setApplyDiscountToSalePrice(boolean applyToSalePrice) {
        this.applyToSalePrice = applyToSalePrice;
    }

    @Override
    public Boolean getApplyToChildItems() {
        return applyToChildItems == null ? false : applyToChildItems;
    }

    @Override
    public void setApplyToChildItems(boolean applyToChildItems) {
        this.applyToChildItems = applyToChildItems;
    }

    /**
     * Returns true if this offer can be combined with other offers in the order.
     *
     * @return true if combinableWithOtherOffers, otherwise false
     */
    @Override
    public boolean isCombinableWithOtherOffers() {
        return combinableWithOtherOffers == null ? false : combinableWithOtherOffers;
    }

    /**
     * Sets the combinableWithOtherOffers value for this offer.
     *
     * @param combinableWithOtherOffers
     */
    @Override
    public void setCombinableWithOtherOffers(boolean combinableWithOtherOffers) {
        this.combinableWithOtherOffers = combinableWithOtherOffers;
    }

    @JsonIgnore
    public boolean getCombinableWithOtherOffers() {
        return combinableWithOtherOffers;
    }

    @Override
    public boolean isAutomaticallyAdded() {
        if (automaticallyAdded == null) {
            return false;
        }
        return automaticallyAdded;
    }

    
    @Override
    public void setAutomaticallyAdded(boolean automaticallyAdded) {
        this.automaticallyAdded = automaticallyAdded;
    }

    @Override
    public Long getMaxUsesPerCustomer() {
        return maxUsesPerCustomer == null ? 0 : maxUsesPerCustomer;
    }

    @Override
    public void setMaxUsesPerCustomer(Long maxUsesPerCustomer) {
        this.maxUsesPerCustomer = maxUsesPerCustomer;
    }
    
    @Override
    public boolean isUnlimitedUsePerCustomer() {
        return getMaxUsesPerCustomer() == 0;
    }
    
    @Override
    public boolean isLimitedUsePerCustomer() {
        return getMaxUsesPerCustomer() > 0;
    }

    @Override
    public int getMaxUsesPerOrder() {
        return maxUsesPerOrder == null ? 0 : maxUsesPerOrder;
    }

    @Override
    public void setMaxUsesPerOrder(int maxUsesPerOrder) {
        this.maxUsesPerOrder = maxUsesPerOrder;
    }

    @Override
    public boolean isUnlimitedUsePerOrder() {
        return getMaxUsesPerOrder() == 0;
    }
    
    @Override
    public boolean isLimitedUsePerOrder() {
        return getMaxUsesPerOrder() > 0;
    }

    @Override
    public String getMarketingMessage() {
        return DynamicTranslationProvider.getValue(this, "marketingMessage", marketingMessage);
    }

    @Override
    public void setMarketingMessage(String marketingMessage) {
        this.marketingMessage = marketingMessage;
    }

    //    @Override
    //    @Deprecated
    //    public Set<OfferItemCriteria> getQualifyingItemCriteria() {
    //        if (legacyQualifyingItemCriteria.size() == 0) {
    //            for (OfferQualifyingCriteriaXref xref : getQualifyingItemCriteriaXref()) {
    //                legacyQualifyingItemCriteria.add(xref.getOfferItemCriteria());
    //            }
    //        }
    //        return Collections.unmodifiableSet(legacyQualifyingItemCriteria);
    //    }
    //
    //    @Override
    //    @Deprecated
    //    public void setQualifyingItemCriteria(Set<OfferItemCriteria> qualifyingItemCriteria) {
    //        this.legacyQualifyingItemCriteria.clear();
    //        this.qualifyingItemCriteria.clear();
    //        for(OfferItemCriteria crit : qualifyingItemCriteria){
    //            this.qualifyingItemCriteria.add(new OfferQualifyingCriteriaXrefImpl(this, crit));
    //        }
    //    }

    @Override
    public Set<OfferQualifyingCriteriaXref> getQualifyingItemCriteriaXref() {
        return qualifyingItemCriteria;
    }

    @Override
    public void setQualifyingItemCriteriaXref(Set<OfferQualifyingCriteriaXref> qualifyingItemCriteriaXref) {
        this.qualifyingItemCriteria = qualifyingItemCriteriaXref;
    }

    @Override
    public Set<OfferTargetCriteriaXref> getTargetItemCriteriaXref() {
        if (OfferType.ORDER_ITEM.equals(getType()) && CollectionUtils.isEmpty(targetItemCriteria)) {
            OfferItemCriteria oic = new OfferItemCriteriaImpl();
            oic.setQuantity(1);
            OfferTargetCriteriaXref xref = new OfferTargetCriteriaXrefImpl(this, oic);
            return Collections.unmodifiableSet(Collections.singleton(xref));
        }
        return targetItemCriteria;
    }

    @Override
    public void setTargetItemCriteriaXref(Set<OfferTargetCriteriaXref> targetItemCriteriaXref) {
        this.targetItemCriteria = targetItemCriteriaXref;
    }

    @Override
    public Boolean isTotalitarianOffer() {
        if (totalitarianOffer == null) {
            return false;
        } else {
            return totalitarianOffer.booleanValue();
        }
    }

    @Override
    public void setTotalitarianOffer(Boolean totalitarianOffer) {
        if (totalitarianOffer == null) {
            this.totalitarianOffer = false;
        } else {
            this.totalitarianOffer = totalitarianOffer;
        }
    }

    @Override
    public Map<String, OfferOfferRuleXref> getOfferMatchRulesXref() {
       return offerMatchRules;
    }

    @Override
    public void setOfferMatchRulesXref(Map<String, OfferOfferRuleXref> offerMatchRulesXref) {
       this.offerMatchRules = offerMatchRulesXref;
   }

    @Override
    public Character getArchived() {
       ArchiveStatus temp;
       if (archiveStatus == null) {
           temp = new ArchiveStatus();
       } else {
           temp = archiveStatus;
       }
       return temp.getArchived();
    }

    @Override
    public void setArchived(Character archived) {
        if (archiveStatus == null) {
            archiveStatus = new ArchiveStatus();
        }
        archiveStatus.setArchived(archived);
    }

    @Override
    public boolean isActive() {
        return DateUtil.isActive(startDate, endDate, true) && 'Y'!=getArchived();
    }
    
    @Override
    public Money getQualifyingItemSubTotal() {
        return qualifyingItemSubTotal == null ? null : BroadleafCurrencyUtils.getMoney(qualifyingItemSubTotal, null);
    }

    @Override
    public void setQualifyingItemSubTotal(Money qualifyingItemSubTotal) {
        this.qualifyingItemSubTotal = Money.toAmount(qualifyingItemSubTotal);
    }

    @Override
    public Money getOrderMinSubTotal() {
        return orderMinSubTotal == null ? null : BroadleafCurrencyUtils.getMoney(orderMinSubTotal, null);
    }

    @Override
    public void setOrderMinSubTotal(Money orderMinSubTotal) {
        this.orderMinSubTotal = Money.toAmount(orderMinSubTotal);
    }

    @Override
    public Money getTargetMinSubTotal() {
        return targetMinSubTotal == null ? null : BroadleafCurrencyUtils.getMoney(targetMinSubTotal, null);
    }

    @Override
    public void setTargetMinSubTotal(Money targetMinSubTotal) {
        this.targetMinSubTotal = Money.toAmount(targetMinSubTotal);
    }

    @Override
    public List<OfferCode> getOfferCodes() {
        return offerCodes;
    }

    @Override
    public void setOfferCodes(List<OfferCode> offerCodes) {
        this.offerCodes = offerCodes;
    }

    @Override
    public Boolean getRequiresRelatedTargetAndQualifiers() {
        return requiresRelatedTargetAndQualifiers == null ? false : requiresRelatedTargetAndQualifiers;
    }
    
    @Override
    public void setRequiresRelatedTargetAndQualifiers(Boolean requiresRelatedTargetAndQualifiers) {
        this.requiresRelatedTargetAndQualifiers = requiresRelatedTargetAndQualifiers;
    }

    @Override
    public String getMainEntityName() {
        return getName();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(name)
            .append(startDate)
            .append(type)
            .append(value)
            .build();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o != null && getClass().isAssignableFrom(o.getClass())) {
            OfferImpl that = (OfferImpl) o;
            return new EqualsBuilder()
                .append(this.id, that.id)
                .append(this.name, that.name)
                .append(this.startDate, that.startDate)
                .append(this.type, that.type)
                .append(this.value, that.value)
                .build();
        }
        
        return false;
    }

    @Override
    public <G extends Offer> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        Offer cloned = createResponse.getClone();
        cloned.setApplyDiscountToSalePrice(applyToSalePrice);
        if (automaticallyAdded != null) {
            cloned.setAutomaticallyAdded(automaticallyAdded);
        }
        cloned.setDescription(description);
        cloned.setDiscountType(getDiscountType());
        cloned.setEndDate(endDate);
        cloned.setMaxUsesPerCustomer(maxUsesPerCustomer);
        cloned.setMarketingMessage(marketingMessage);
        cloned.setName(name);
        cloned.setValue(value);
        cloned.setPriority(getPriority());
        cloned.setMaxUsesPerOrder(getMaxUsesPerOrder());
        cloned.setArchived(getArchived());
        cloned.setOfferItemQualifierRuleType(getOfferItemQualifierRuleType());
        cloned.setOfferItemTargetRuleType(getOfferItemTargetRuleType());
        cloned.setCombinableWithOtherOffers(isCombinableWithOtherOffers());
        cloned.setQualifyingItemSubTotal(getQualifyingItemSubTotal());
        cloned.setOrderMinSubTotal(getOrderMinSubTotal());
        cloned.setStartDate(startDate);
        cloned.setTargetSystem(targetSystem);
        cloned.setRequiresRelatedTargetAndQualifiers(requiresRelatedTargetAndQualifiers);
        cloned.setTotalitarianOffer(totalitarianOffer);
        cloned.setType(getType());
        if (!BooleanUtils.toBoolean(context.getCopyHints().get(EXCLUDE_OFFERCODE_COPY_HINT))) {
            for (OfferCode entry : offerCodes) {
                OfferCode clonedEntry = entry.createOrRetrieveCopyInstance(context).getClone();
                cloned.getOfferCodes().add(clonedEntry);
            }
        }
        for(OfferQualifyingCriteriaXref entry : qualifyingItemCriteria){
            OfferQualifyingCriteriaXref clonedEntry = entry.createOrRetrieveCopyInstance(context).getClone();
            cloned.getQualifyingItemCriteriaXref().add(clonedEntry);
        }
        Set<OfferTargetCriteriaXref> offerTargetCriteriaXrefs = new HashSet<>();
        for(OfferTargetCriteriaXref entry : targetItemCriteria){
            OfferTargetCriteriaXref clonedEntry = entry.createOrRetrieveCopyInstance(context).getClone();
            offerTargetCriteriaXrefs.add(clonedEntry);
        }
        cloned.setTargetItemCriteriaXref(offerTargetCriteriaXrefs);
        for(Map.Entry<String, OfferOfferRuleXref> entry : offerMatchRules.entrySet()){
            OfferOfferRuleXref clonedEntry = entry.getValue().createOrRetrieveCopyInstance(context).getClone();
            cloned.getOfferMatchRulesXref().put(entry.getKey(),clonedEntry);
        }

        return  createResponse;
    }

}
