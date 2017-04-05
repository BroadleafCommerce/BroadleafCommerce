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
import org.broadleafcommerce.common.extensibility.jpa.clone.ClonePolicy;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.domain.LocaleImpl;
import org.broadleafcommerce.common.media.domain.Media;
import org.broadleafcommerce.common.media.domain.MediaImpl;
import org.broadleafcommerce.common.persistence.ArchiveStatus;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.presentation.ConfigurationItem;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.broadleafcommerce.common.util.DateUtil;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.SQLDelete;

import java.util.Date;

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
import javax.persistence.Table;

@Entity
@Table(name = "BLC_PROMOTION_MESSAGE")
@Inheritance(strategy=InheritanceType.JOINED)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@SQLDelete(sql="UPDATE BLC_PROMOTION_MESSAGE SET ARCHIVED = 'Y' WHERE PROMOTION_MESSAGE_ID = ?")
@AdminPresentationMergeOverrides({
    @AdminPresentationMergeOverride(name = "media.url", mergeEntries = {
        @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.EXCLUDED, booleanOverrideValue = false),
        @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.TAB, overrideValue = PromotionMessageAdminPresentation.TabName.Media),
        @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.TABORDER, intOverrideValue = PromotionMessageAdminPresentation.TabOrder.Media),
        @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.GROUP, overrideValue = PromotionMessageAdminPresentation.GroupName.Media),
        @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.FRIENDLYNAME, overrideValue = "PromotionMessageImpl_Media"),
        @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.REQUIREDOVERRIDE, overrideValue = "NOT_REQUIRED"),
        @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.PROMINENT, booleanOverrideValue = false)
    }),
    @AdminPresentationMergeOverride(name = "media.title", mergeEntries = {
        @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.TAB, overrideValue = PromotionMessageAdminPresentation.TabName.Media),
        @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.TABORDER, intOverrideValue = PromotionMessageAdminPresentation.TabOrder.Media),
        @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.GROUP, overrideValue = PromotionMessageAdminPresentation.GroupName.Media),
        @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.PROMINENT, booleanOverrideValue = false)
    }),
    @AdminPresentationMergeOverride(name = "media.altText", mergeEntries = {
        @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.TAB, overrideValue = PromotionMessageAdminPresentation.TabName.Media),
        @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.TABORDER, intOverrideValue = PromotionMessageAdminPresentation.TabOrder.Media),
        @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.GROUP, overrideValue = PromotionMessageAdminPresentation.GroupName.Media),
        @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.PROMINENT, booleanOverrideValue = false)
    }),
    @AdminPresentationMergeOverride(name = "media.tags", mergeEntries = {
        @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.TAB, overrideValue = PromotionMessageAdminPresentation.TabName.Media),
        @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.TABORDER, intOverrideValue = PromotionMessageAdminPresentation.TabOrder.Media),
        @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.GROUP, overrideValue = PromotionMessageAdminPresentation.GroupName.Media),
        @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.PROMINENT, booleanOverrideValue = false)
    }),
    @AdminPresentationMergeOverride(name = "media.auditable.createdBy", mergeEntries = {
        @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.VISIBILITY, overrideValue = "HIDDEN_ALL")
    }),
    @AdminPresentationMergeOverride(name = "media.auditable.updatedBy", mergeEntries = {
        @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.VISIBILITY, overrideValue = "HIDDEN_ALL")
    }),
    @AdminPresentationMergeOverride(name = "media.auditable.dateCreated", mergeEntries = {
        @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.VISIBILITY, overrideValue = "HIDDEN_ALL")
    }),
    @AdminPresentationMergeOverride(name = "media.auditable.dateUpdated", mergeEntries = {
        @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.VISIBILITY, overrideValue = "HIDDEN_ALL")
    })
})
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
    @Index(name="PROMOTION_MESSAGE_NAME_INDEX", columnNames={"NAME"})
    @AdminPresentation(friendlyName = "PromotionMessageImpl_Name",
        tab = TabName.General, tabOrder = TabOrder.General,
        group = GroupName.General, groupOrder = GroupOrder.General, order = FieldOrder.Name,
        prominent = true, gridOrder = FieldOrder.Name,
        requiredOverride = RequiredOverride.REQUIRED)
    protected String name;

    @Column(name = "PROMOTION_MESSASGE")
    @AdminPresentation(friendlyName = "PromotionMessageImpl_message",
        tab = TabName.General, tabOrder = TabOrder.General,
        group = GroupName.General, groupOrder = GroupOrder.General, order = FieldOrder.Message,
        prominent = true, gridOrder = FieldOrder.Message)
    protected String message;

    @ManyToOne(targetEntity = MediaImpl.class, cascade = {CascadeType.ALL})
    @JoinColumn(name = "MEDIA_ID")
    @ClonePolicy
    protected Media media;

    @Column(name = "PROMOTION_MESSAGE_PRIORITY")
    @AdminPresentation(friendlyName = "PromotionMessageImpl_Priority",
        tab = TabName.General, tabOrder = TabOrder.General,
        group = GroupName.Placement, groupOrder = GroupOrder.Placement, order = FieldOrder.Priority,
        tooltip = "PromotionMessageImpl_Priority_Tooltip")
    protected Integer priority;

    @Column(name = "START_DATE")
    @AdminPresentation(friendlyName = "PromotionMessageImpl_StartDate",
        tab = TabName.General, tabOrder = TabOrder.General,
        group = GroupName.ActiveRange, groupOrder = GroupOrder.ActiveRange, order = FieldOrder.StartDate,
        requiredOverride = RequiredOverride.REQUIRED,
        defaultValue = "today")
    protected Date startDate;

    @Column(name = "END_DATE")
    @AdminPresentation(friendlyName = "PromotionMessageImpl_EndDate",
        tab = TabName.General, tabOrder = TabOrder.General,
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
        tab = TabName.General, tabOrder = TabOrder.General,
        group = GroupName.Placement, groupOrder = GroupOrder.Placement, order = FieldOrder.MessagePlacement,
        fieldType= SupportedFieldType.BROADLEAF_ENUMERATION,
        broadleafEnumeration="org.broadleafcommerce.core.promotionMessage.domain.type.PromotionMessagePlacementType",
        defaultValue = "EVERYWHERE",
        requiredOverride = RequiredOverride.REQUIRED)
    protected String messagePlacement;

    @ManyToOne(targetEntity = LocaleImpl.class)
    @JoinColumn(name = "LOCALE_CODE")
    @AdminPresentation(friendlyName = "PromotionMessageImpl_Locale",
        tab = TabName.General, tabOrder = TabOrder.General,
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

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
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
        cloned.setMedia(getMedia());

        return  createResponse;
    }
}
