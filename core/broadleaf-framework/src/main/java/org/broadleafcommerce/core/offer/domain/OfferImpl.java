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

import org.apache.commons.collections.CollectionUtils;
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
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.RuleIdentifier;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.util.DateUtil;
import org.broadleafcommerce.core.offer.service.type.OfferDeliveryType;
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
import org.hibernate.annotations.Type;

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
import javax.persistence.Lob;
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

    @OneToMany(mappedBy = "offer", targetEntity = OfferCodeImpl.class, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blOffers")
    @BatchSize(size = 50)
    @AdminPresentationCollection(friendlyName = "offerCodeTitle", order = 1000,
        group = OfferAdminPresentation.GroupName.Codes,
        addType = AddMethodType.PERSIST)
    protected List<OfferCode> offerCodes = new ArrayList<OfferCode>(100);

    @Column(name = "OFFER_NAME", nullable=false)
    @Index(name="OFFER_NAME_INDEX", columnNames={"OFFER_NAME"})
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Name", order = 1000, 
        group = OfferAdminPresentation.GroupName.Description,
        prominent = true, gridOrder = 1,
        defaultValue = "New Offer")
    protected String name;

    @Column(name = "OFFER_DESCRIPTION")
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Description", order = 2000, 
        group = OfferAdminPresentation.GroupName.Description,
        largeEntry = true)
    protected String description;

    @Column(name = "MARKETING_MESSASGE")
    @Index(name = "OFFER_MARKETING_MESSAGE_INDEX", columnNames = { "MARKETING_MESSASGE" })
    @AdminPresentation(friendlyName = "OfferImpl_marketingMessage", order = 3000,
        group = OfferAdminPresentation.GroupName.Description,
        translatable = true)
    protected String marketingMessage;

    @Column(name = "OFFER_TYPE", nullable=false)
    @Index(name="OFFER_TYPE_INDEX", columnNames={"OFFER_TYPE"})
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Type", order = 1000,
        group = OfferAdminPresentation.GroupName.RuleConfiguration,
        prominent =  true, gridOrder = 3,
        fieldType=SupportedFieldType.BROADLEAF_ENUMERATION, 
        broadleafEnumeration="org.broadleafcommerce.core.offer.service.type.OfferType",
        defaultValue = "ORDER")
    protected String type;

    @Column(name = "OFFER_DISCOUNT_TYPE")
    @Index(name="OFFER_DISCOUNT_INDEX", columnNames={"OFFER_DISCOUNT_TYPE"})
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Discount_Type", order = 2000,
        group = OfferAdminPresentation.GroupName.RuleConfiguration,
        requiredOverride = RequiredOverride.REQUIRED,
        fieldType=SupportedFieldType.BROADLEAF_ENUMERATION,
        broadleafEnumeration="org.broadleafcommerce.core.offer.service.type.OfferDiscountType")
    protected String discountType;

    @Column(name = "OFFER_VALUE", nullable=false, precision=19, scale=5)
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Value", order = 3000,
        group = OfferAdminPresentation.GroupName.RuleConfiguration,
        prominent = true, gridOrder = 4,
        defaultValue = "0")
    protected BigDecimal value;

    @Column(name = "OFFER_PRIORITY")
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Priority", order = 1000,
        group = OfferAdminPresentation.GroupName.Advanced)
    protected Integer priority;

    @Column(name = "START_DATE")
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Start_Date", order = 1000,
        group = OfferAdminPresentation.GroupName.ActivityRange,
        prominent = true, gridOrder = 2,
        defaultValue = "today")
    protected Date startDate;

    @Column(name = "END_DATE")
    @AdminPresentation(friendlyName = "OfferImpl_Offer_End_Date", order = 2000,
        group = OfferAdminPresentation.GroupName.ActivityRange,
        validationConfigurations = { 
            @ValidationConfiguration(
                validationImplementation = "blAfterStartDateValidator",
                configurationItems = {
                        @ConfigurationItem(itemName = "otherField", itemValue = "startDate")
                }) 
        })
    protected Date endDate;

    @Column(name = "STACKABLE")
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Stackable",
            tooltip = "OfferImplStackable_tooltip",
            group = OfferAdminPresentation.GroupName.CombineStack)
    protected Boolean stackable = true;

    @Column(name = "TARGET_SYSTEM")
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Target_System",
            visibility = VisibilityEnum.HIDDEN_ALL)
    protected String targetSystem;

    @Column(name = "APPLY_TO_SALE_PRICE")
    @AdminPresentation(friendlyName = "OfferImpl_Apply_To_Sale_Price",
            group = OfferAdminPresentation.GroupName.Advanced)
    protected Boolean applyToSalePrice = false;

    @Column(name = "APPLIES_TO_RULES", length = Integer.MAX_VALUE - 1)
    @AdminPresentation(excluded = true)
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    @Deprecated
    protected String appliesToOrderRules;

    @Column(name = "APPLIES_WHEN_RULES", length = Integer.MAX_VALUE - 1)
    @AdminPresentation(excluded = true)
    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    @Deprecated
    protected String appliesToCustomerRules;

    @Column(name = "APPLY_OFFER_TO_MARKED_ITEMS")
    @AdminPresentation(excluded = true)
    @Deprecated
    protected boolean applyDiscountToMarkedItems;
    
    /**
     * No offers can be applied on top of this offer; 
     * If false, stackable has to be false also
     */
    @Column(name = "COMBINABLE_WITH_OTHER_OFFERS")
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Combinable",
        tooltip = "OfferImplCombinableWithOtherOffers_tooltip",
        group = OfferAdminPresentation.GroupName.CombineStack)
    protected Boolean combinableWithOtherOffers = true;

    @Column(name = "OFFER_DELIVERY_TYPE")
    @AdminPresentation(excluded = true)
    protected String deliveryType;

    @Column(name = "AUTOMATICALLY_ADDED")
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Automatically_Added", order = 5000,
            group = OfferAdminPresentation.GroupName.Usage,
            fieldType = SupportedFieldType.BOOLEAN)
    protected Boolean automaticallyAdded = false;

    @Column(name = "MAX_USES")
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Max_Uses_Per_Order", order = 2000,
        tooltip = "OfferImplMaxUsesPerOrder_tooltip",
        group = OfferAdminPresentation.GroupName.Advanced)
    protected Integer maxUsesPerOrder;

    @Column(name = "MAX_USES_PER_CUSTOMER")
    @AdminPresentation(friendlyName = "OfferImpl_Max_Uses_Per_Customer", order = 3000,
        tooltip = "OfferImplMaxUsesPerCustomer_tooltip",
        group = OfferAdminPresentation.GroupName.Advanced)
    protected Long maxUsesPerCustomer;

    @Column(name = "USES")
    @AdminPresentation(friendlyName = "OfferImpl_Offer_Current_Uses",
        visibility = VisibilityEnum.HIDDEN_ALL)
    @Deprecated
    protected int uses;
    
    @Column(name = "OFFER_ITEM_QUALIFIER_RULE")
    @AdminPresentation(friendlyName = "OfferImpl_Item_Qualifier_Rule",
        group = OfferAdminPresentation.GroupName.QualifierRuleRestriction,
        order = 1000,
        tooltip = "OfferItemRestrictionRuleType_tooltip",
        fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
        broadleafEnumeration = "org.broadleafcommerce.core.offer.service.type.OfferItemRestrictionRuleType")
    protected String offerItemQualifierRuleType;

    @Column(name = "QUALIFYING_ITEM_MIN_TOTAL", precision=19, scale=5)
    @AdminPresentation(friendlyName="OfferImpl_Qualifying_Item_Subtotal",
        group = OfferAdminPresentation.GroupName.QualifierRuleRestriction,
        order = 2000)
    protected BigDecimal qualifyingItemSubTotal;
    
    @Column(name = "OFFER_ITEM_TARGET_RULE")
    @AdminPresentation(friendlyName = "OfferImpl_Item_Target_Rule",
        group = OfferAdminPresentation.GroupName.TargetRuleRestriction,
        tooltip = "OfferItemRestrictionRuleType_tooltip",
        fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
        broadleafEnumeration = "org.broadleafcommerce.core.offer.service.type.OfferItemRestrictionRuleType")
    protected String offerItemTargetRuleType;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "offer", targetEntity = OfferQualifyingCriteriaXrefImpl.class, cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blOffers")
    @AdminPresentation(friendlyName = "OfferImpl_Qualifying_Item_Rule",
        group = OfferAdminPresentation.GroupName.RuleConfiguration,
        fieldType = SupportedFieldType.RULE_WITH_QUANTITY,
        ruleIdentifier = RuleIdentifier.ORDERITEM)
    protected Set<OfferQualifyingCriteriaXref> qualifyingItemCriteria = new HashSet<OfferQualifyingCriteriaXref>();

    @Transient
    protected Set<OfferItemCriteria> legacyQualifyingItemCriteria = new HashSet<OfferItemCriteria>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "offer", targetEntity = OfferTargetCriteriaXrefImpl.class, cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blOffers")
    @AdminPresentation(friendlyName = "OfferImpl_Target_Item_Rule",
        group = OfferAdminPresentation.GroupName.RuleConfiguration,
        fieldType = SupportedFieldType.RULE_WITH_QUANTITY, 
        ruleIdentifier = RuleIdentifier.ORDERITEM)
    protected Set<OfferTargetCriteriaXref> targetItemCriteria = new HashSet<OfferTargetCriteriaXref>();

    @Transient
    protected Set<OfferItemCriteria> legacyTargetItemCriteria = new HashSet<OfferItemCriteria>();
    
    @Column(name = "TOTALITARIAN_OFFER")
    @AdminPresentation(friendlyName = "OfferImpl_Totalitarian_Offer",
        group = OfferAdminPresentation.GroupName.Advanced,
        visibility = VisibilityEnum.HIDDEN_ALL)
    protected Boolean totalitarianOffer = false;

    @Column(name = "REQUIRES_RELATED_TAR_QUAL")
    @AdminPresentation(friendlyName = "OfferImpl_Requires_Related_Target_And_Qualifiers",
        group = OfferAdminPresentation.GroupName.Advanced,
        tooltip = "OfferImplRelatedTargetQualifier_tooltip",
        visibility = VisibilityEnum.VISIBLE_ALL)
    protected Boolean requiresRelatedTargetAndQualifiers = false;

    @OneToMany(mappedBy = "offer", targetEntity = OfferOfferRuleXrefImpl.class, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @MapKey(name = "key")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blOffers")
    @AdminPresentationMapFields(
        toOneTargetProperty = "offerRule",
        toOneParentProperty = "offer",
        mapDisplayFields = {
            @AdminPresentationMapField(
                fieldName = RuleIdentifier.CUSTOMER_FIELD_KEY,
                fieldPresentation = @AdminPresentation(fieldType = SupportedFieldType.RULE_SIMPLE,
                    group = OfferAdminPresentation.GroupName.Usage,
                    ruleIdentifier = RuleIdentifier.CUSTOMER, friendlyName = "OfferImpl_Customer_Rule")
            ),
            @AdminPresentationMapField(
            fieldName = RuleIdentifier.TIME_FIELD_KEY,
                fieldPresentation = @AdminPresentation(fieldType = SupportedFieldType.RULE_SIMPLE,
                    group = OfferAdminPresentation.GroupName.ActivityRange,
                    ruleIdentifier = RuleIdentifier.TIME, friendlyName = "OfferImpl_Time_Rule")
            ),
            @AdminPresentationMapField(
                fieldName = RuleIdentifier.ORDER_FIELD_KEY,
                fieldPresentation = @AdminPresentation(fieldType = SupportedFieldType.RULE_SIMPLE, 
                    group = OfferAdminPresentation.GroupName.Usage,
                    ruleIdentifier = RuleIdentifier.ORDER, friendlyName = "OfferImpl_Order_Rule")
            ),
            @AdminPresentationMapField(
                fieldName = RuleIdentifier.FULFILLMENT_GROUP_FIELD_KEY,
                fieldPresentation = @AdminPresentation(fieldType = SupportedFieldType.RULE_SIMPLE, 
                    group = OfferAdminPresentation.GroupName.RuleConfiguration,
                    ruleIdentifier = RuleIdentifier.FULFILLMENTGROUP, friendlyName = "OfferImpl_FG_Rule")
            )
        }
    )
    Map<String, OfferOfferRuleXref> offerMatchRules = new HashMap<String, OfferOfferRuleXref>();

    @Transient
    Map<String, OfferRule> legacyOfferMatchRules = new HashMap<String, OfferRule>();
    
    @Column(name = "USE_NEW_FORMAT")
    @AdminPresentation(friendlyName = "OfferImpl_Treat_As_New_Format",
        group = OfferAdminPresentation.GroupName.Advanced,
        visibility = VisibilityEnum.HIDDEN_ALL)
    protected Boolean treatAsNewFormat = false;

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

    /**
     * Returns true if this offer can be stacked on top of another offer.  Stackable is evaluated
     * against offers with the same offer type.
     *
     * @return true if stackable, otherwise false
     */
    @Override
    public boolean isStackable() {
        return stackable == null ? false : stackable;
    }

    /**
     * Sets the stackable value for this offer.
     *
     * @param stackable
     */
    @Override
    public void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    @Deprecated
    @JsonIgnore
    public boolean getStackable(){
        return stackable;
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
        this.applyToSalePrice=applyToSalePrice;
    }

    @Override
    @Deprecated
    public String getAppliesToOrderRules() {
        return appliesToOrderRules;
    }

    @Override
    @Deprecated
    public void setAppliesToOrderRules(String appliesToOrderRules) {
        this.appliesToOrderRules = appliesToOrderRules;
    }

    @Override
    @Deprecated
    public String getAppliesToCustomerRules() {
        return appliesToCustomerRules;
    }

    @Override
    @Deprecated
    public void setAppliesToCustomerRules(String appliesToCustomerRules) {
        this.appliesToCustomerRules = appliesToCustomerRules;
    }

    @Override
    @Deprecated
    public boolean isApplyDiscountToMarkedItems() {
        return applyDiscountToMarkedItems;
    }

    @Deprecated
    @JsonIgnore
    public boolean getApplyDiscountToMarkedItems() {
        return applyDiscountToMarkedItems;
    }
    
    @Override
    @Deprecated
    public void setApplyDiscountToMarkedItems(boolean applyDiscountToMarkedItems) {
        this.applyDiscountToMarkedItems = applyDiscountToMarkedItems;
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

    @Deprecated
    @JsonIgnore
    public boolean getCombinableWithOtherOffers() {
        return combinableWithOtherOffers;
    }

    @Override
    public boolean isAutomaticallyAdded() {
        if (automaticallyAdded == null) {
            if (deliveryType != null) {
                OfferDeliveryType offerDeliveryType = OfferDeliveryType.getInstance(deliveryType);
                return OfferDeliveryType.AUTOMATIC.equals(offerDeliveryType);
            }
            return false;
        }
        return automaticallyAdded;
    }

    
    @Override
    public void setAutomaticallyAdded(boolean automaticallyAdded) {
        this.automaticallyAdded = automaticallyAdded;
    }

    @Override
    @Deprecated
    @JsonIgnore
    public OfferDeliveryType getDeliveryType() {
        if (deliveryType == null) {
            if (isAutomaticallyAdded()) {
                return OfferDeliveryType.AUTOMATIC;
            } else {
                return OfferDeliveryType.MANUAL;
            }
        }
        return OfferDeliveryType.getInstance(deliveryType);
    }

    @Override
    public void setDeliveryType(OfferDeliveryType deliveryType) {
        this.deliveryType = deliveryType.getType();
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
    @Deprecated
    public int getMaxUses() {
        return getMaxUsesPerOrder();
    }

    @Override
    public void setMaxUses(int maxUses) {
        setMaxUsesPerOrder(maxUses);
    }

    @Override
    @Deprecated
    public int getUses() {
        return uses;
    }

    @Override
    public String getMarketingMessage() {
        return DynamicTranslationProvider.getValue(this, "marketingMessage", marketingMessage);
    }

    @Override
    public void setMarketingMessage(String marketingMessage) {
        this.marketingMessage = marketingMessage;
    }

    @Override
    @Deprecated
    public void setUses(int uses) {
        this.uses = uses;
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
    public Boolean getTreatAsNewFormat() {
        return treatAsNewFormat;
    }

    @Override
    public void setTreatAsNewFormat(Boolean treatAsNewFormat) {
        this.treatAsNewFormat = treatAsNewFormat;
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
        cloned.setStackable(getStackable());
        cloned.setDeliveryType(getDeliveryType());
        cloned.setMaxUsesPerOrder(getMaxUsesPerOrder());
        cloned.setArchived(getArchived());
        cloned.setOfferItemQualifierRuleType(getOfferItemQualifierRuleType());
        cloned.setCombinableWithOtherOffers(isCombinableWithOtherOffers());
        cloned.setQualifyingItemSubTotal(getQualifyingItemSubTotal());
        cloned.setStartDate(startDate);
        cloned.setUses(uses);
        cloned.setTargetSystem(targetSystem);
        cloned.setRequiresRelatedTargetAndQualifiers(requiresRelatedTargetAndQualifiers);
        cloned.setTreatAsNewFormat(treatAsNewFormat);
        cloned.setTotalitarianOffer(totalitarianOffer);
        cloned.setType(getType());
        for(OfferCode entry : offerCodes){
            OfferCode clonedEntry = entry.createOrRetrieveCopyInstance(context).getClone();
            cloned.getOfferCodes().add(clonedEntry);
        }
        for(OfferQualifyingCriteriaXref entry : qualifyingItemCriteria){
            OfferQualifyingCriteriaXref clonedEntry = entry.createOrRetrieveCopyInstance(context).getClone();
            cloned.getQualifyingItemCriteriaXref().add(clonedEntry);
        }
        for(Map.Entry<String, OfferOfferRuleXref> entry : offerMatchRules.entrySet()){
            OfferOfferRuleXref clonedEntry = entry.getValue().createOrRetrieveCopyInstance(context).getClone();
            cloned.getOfferMatchRulesXref().put(entry.getKey(),clonedEntry);
        }

        return  createResponse;
    }

}
