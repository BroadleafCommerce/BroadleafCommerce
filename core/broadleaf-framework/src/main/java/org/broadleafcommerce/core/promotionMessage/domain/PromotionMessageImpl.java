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
package org.broadleafcommerce.core.promotionMessage.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.domain.LocaleImpl;
import org.broadleafcommerce.common.persistence.ArchiveStatus;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.common.presentation.AdminPresentationMapField;
import org.broadleafcommerce.common.presentation.AdminPresentationMapFields;
import org.broadleafcommerce.common.presentation.AdminPresentationMapKey;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.presentation.ConfigurationItem;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.common.presentation.client.LookupType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.util.DateUtil;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductImpl;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.SQLDelete;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "BLC_PROMOTION_MESSAGE")
@Inheritance(strategy=InheritanceType.JOINED)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blOffers")
@SQLDelete(sql="UPDATE BLC_PROMOTION_MESSAGE SET ARCHIVED = 'Y' WHERE PROMOTION_MESSAGE_ID = ?")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
public class PromotionMessageImpl implements PromotionMessage, AdminMainEntity, PromotionMessageAdminPresentation {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "PromotionMessageId")
    @GenericGenerator(
        name="PromotionMessageId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="PromotionMessageImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.promotionMessage.domain.PromotionMessageImpl")
        }
    )
    @Column(name = "PROMOTION_MESSAGE_ID")
    @AdminPresentation(friendlyName = "PromotionMessageImpl_Id", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @Column(name = "NAME")
    @Index(name="PROMOTION_MESSAGE_NAME_INDEX", columnNames={"PROMOTION_MESSAGE_NAME"})
    @AdminPresentation(friendlyName = "PromotionMessageImpl_Name",
        group = GroupName.General, groupOrder = GroupOrder.General, order = FieldOrder.Name,
        prominent = true, gridOrder = FieldOrder.Name,
        requiredOverride = RequiredOverride.REQUIRED)
    protected String name;

    @Column(name = "PROMOTION_MESSASGE")
    @AdminPresentation(friendlyName = "PromotionMessageImpl_message",
        group = GroupName.General, groupOrder = GroupOrder.General, order = FieldOrder.Message,
        prominent = true, gridOrder = FieldOrder.Message)
    protected String message;

    @OneToMany(mappedBy = "promotionMessage", targetEntity = PromotionMessageMediaXrefImpl.class, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @MapKey(name = "key")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blOffers")
    @BatchSize(size = 50)
    @AdminPresentationMap(
            excluded = true,
            mediaField = "media.url",
            toOneTargetProperty = "media",
            toOneParentProperty = "promotionMessage",
            keys = {
                    @AdminPresentationMapKey(keyName = "primary", friendlyKeyName = "mediaPrimary")
            }
    )
    @AdminPresentationMapFields(
        mapDisplayFields = {
            @AdminPresentationMapField(
                fieldName = "primary",
                fieldPresentation = @AdminPresentation(friendlyName = "PromotionMessageImpl_Media",
                    group = GroupName.General, groupOrder = GroupOrder.General, order = FieldOrder.Media,
                    fieldType = SupportedFieldType.MEDIA)
            )
        })
    protected Map<String, PromotionMessageMediaXref> promotionMessageMedia = new HashMap<>();

    @Column(name = "PROMOTION_MESSAGE_PRIORITY")
    @AdminPresentation(friendlyName = "PromotionMessageImpl_Priority",
        group = GroupName.Placement, groupOrder = GroupOrder.Placement, order = FieldOrder.Priority,
        tooltip = "PromotionMessageImpl_Priority_Tooltip")
    protected Integer priority;

    @Column(name = "START_DATE")
    @AdminPresentation(friendlyName = "PromotionMessageImpl_StartDate",
        group = GroupName.ActiveRange, groupOrder = GroupOrder.ActiveRange, order = FieldOrder.StartDate,
        requiredOverride = RequiredOverride.REQUIRED,
        defaultValue = "today")
    protected Date startDate;

    @Column(name = "END_DATE")
    @AdminPresentation(friendlyName = "PromotionMessageImpl_EndDate",
        group = GroupName.ActiveRange, groupOrder = GroupOrder.ActiveRange, order = FieldOrder.EndDate,
        validationConfigurations = {
            @ValidationConfiguration(
                validationImplementation = "blAfterStartDateValidator",
                configurationItems = {
                    @ConfigurationItem(itemName = "otherField", itemValue = "startDate")
                })
        })
    protected Date endDate;

    @Column(name = "MESSAGE_PLACEMENT")
    @AdminPresentation(friendlyName = "PromotionMessageImpl_MessagePlacement",
            group = GroupName.Placement, groupOrder = GroupOrder.Placement, order = FieldOrder.MessagePlacement,
            fieldType= SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration="org.broadleafcommerce.core.promotionMessage.domain.type.PromotionMessagePlacementType",
            defaultValue = "ALL",
            requiredOverride = RequiredOverride.REQUIRED)
    protected String messagePlacement;

    @ManyToOne(targetEntity = LocaleImpl.class)
    @JoinColumn(name = "LOCALE_CODE")
    @AdminPresentation(friendlyName = "PromotionMessageImpl_Locale",
            group = GroupName.Placement, groupOrder = GroupOrder.Placement, order = FieldOrder.Locale)
    @AdminPresentationToOneLookup(lookupDisplayProperty = "friendlyName")
    protected Locale locale;

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
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String promotionMessage) {
        this.message = promotionMessage;
    }

    @Override
    public Map<String, PromotionMessageMediaXref> getPromotionMessageMedia() {
        return promotionMessageMedia;
    }

    @Override
    public void setPromotionMessageMedia(Map<String, PromotionMessageMediaXref> promotionMessageMedia) {
        this.promotionMessageMedia = promotionMessageMedia;
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

    public String getMessagePlacement() {
        return messagePlacement;
    }

    public void setMessagePlacement(String messageLocation) {
        this.messagePlacement = messageLocation;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
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
    public String getMainEntityName() {
        return getName();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(name)
            .append(message)
            .append(startDate)
            .build();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o != null && getClass().isAssignableFrom(o.getClass())) {
            PromotionMessageImpl that = (PromotionMessageImpl) o;
            return new EqualsBuilder()
                .append(this.id, that.id)
                .append(this.name, that.name)
                .append(this.message, that.message)
                .append(this.startDate, that.startDate)
                .build();
        }
        
        return false;
    }

    @Override
    public <G extends PromotionMessage> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        PromotionMessage cloned = createResponse.getClone();
        cloned.setName(name);
        cloned.setMessage(message);
        cloned.setPriority(getPriority());
        cloned.setStartDate(startDate);
        cloned.setEndDate(endDate);
        cloned.setArchived(getArchived());
        for(Map.Entry<String, PromotionMessageMediaXref> entry : promotionMessageMedia.entrySet()){
            PromotionMessageMediaXrefImpl clonedEntry = ((PromotionMessageMediaXrefImpl)entry.getValue()).createOrRetrieveCopyInstance(context).getClone();
            cloned.getPromotionMessageMedia().put(entry.getKey(),clonedEntry);
        }

        return  createResponse;
    }
}
