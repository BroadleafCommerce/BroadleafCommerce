/*-
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.cms.structure.domain;

import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.clone.IgnoreEnterpriseBehavior;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.extensibility.jpa.copy.ProfileEntity;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.domain.LocaleImpl;
import org.broadleafcommerce.common.persistence.IdOverrideTableGenerator;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.client.LookupType;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.broadleafcommerce.openadmin.audit.AdminAuditableListener;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKey;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SC", indexes = {
        @Index(name = "CONTENT_PRIORITY_INDEX", columnList = "PRIORITY"),
        @Index(name = "SC_OFFLN_FLG_INDX", columnList = "OFFLINE_FLAG"),
        @Index(name = "CONTENT_NAME_INDEX_ARCHIVED", columnList = "CONTENT_NAME, SC_TYPE_ID")
})
@EntityListeners(value = {AdminAuditableListener.class})
@AdminPresentationMergeOverrides(value = {
        @AdminPresentationMergeOverride(name = "auditable.createdBy.id",
                mergeEntries = {
                        @AdminPresentationMergeEntry(
                                propertyType = PropertyType.AdminPresentation.READONLY,
                                booleanOverrideValue = true),
                        @AdminPresentationMergeEntry(
                                propertyType = PropertyType.AdminPresentation.VISIBILITY,
                                overrideValue = "HIDDEN_ALL")
                }
        ),
        @AdminPresentationMergeOverride(name = "auditable.updatedBy.id",
                mergeEntries = {
                        @AdminPresentationMergeEntry(
                                propertyType = PropertyType.AdminPresentation.READONLY,
                                booleanOverrideValue = true),
                        @AdminPresentationMergeEntry(
                                propertyType = PropertyType.AdminPresentation.VISIBILITY,
                                overrideValue = "HIDDEN_ALL")
                }
        ),
        @AdminPresentationMergeOverride(name = "auditable.createdBy.name",
                mergeEntries = {
                        @AdminPresentationMergeEntry(
                                propertyType = PropertyType.AdminPresentation.READONLY,
                                booleanOverrideValue = true),
                        @AdminPresentationMergeEntry(
                                propertyType = PropertyType.AdminPresentation.VISIBILITY,
                                overrideValue = "HIDDEN_ALL")
                }
        ),
        @AdminPresentationMergeOverride(name = "auditable.updatedBy.name",
                mergeEntries = {
                        @AdminPresentationMergeEntry(
                                propertyType = PropertyType.AdminPresentation.READONLY,
                                booleanOverrideValue = true),
                        @AdminPresentationMergeEntry(
                                propertyType = PropertyType.AdminPresentation.VISIBILITY,
                                overrideValue = "HIDDEN_ALL")
                }
        ),
        @AdminPresentationMergeOverride(name = "auditable.dateCreated",
                mergeEntries = {
                        @AdminPresentationMergeEntry(
                                propertyType = PropertyType.AdminPresentation.READONLY,
                                booleanOverrideValue = true),
                        @AdminPresentationMergeEntry(
                                propertyType = PropertyType.AdminPresentation.VISIBILITY,
                                overrideValue = "HIDDEN_ALL")
                }
        ),
        @AdminPresentationMergeOverride(name = "auditable.dateUpdated",
                mergeEntries = {
                        @AdminPresentationMergeEntry(
                                propertyType = PropertyType.AdminPresentation.READONLY,
                                booleanOverrideValue = true),
                        @AdminPresentationMergeEntry(
                                propertyType = PropertyType.AdminPresentation.VISIBILITY,
                                overrideValue = "HIDDEN_ALL")
                }
        ),
        @AdminPresentationMergeOverride(name = "structuredContentType.name",
                mergeEntries = {
                        @AdminPresentationMergeEntry(
                                propertyType = PropertyType.AdminPresentation.READONLY,
                                booleanOverrideValue = true),
                        @AdminPresentationMergeEntry(
                                propertyType = PropertyType.AdminPresentation.VISIBILITY,
                                overrideValue = "HIDDEN_ALL")
                }
        ),
        @AdminPresentationMergeOverride(
                name = "structuredContentType.structuredContentFieldTemplate.name",
                mergeEntries = {
                        @AdminPresentationMergeEntry(
                                propertyType = PropertyType.AdminPresentation.READONLY,
                                booleanOverrideValue = true),
                        @AdminPresentationMergeEntry(
                                propertyType = PropertyType.AdminPresentation.VISIBILITY,
                                overrideValue = "HIDDEN_ALL")
                }
        )
})
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE,
        friendlyName = "StructuredContentImpl_baseStructuredContent")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX,
                skipOverlaps = true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blCMSElements")
public class StructuredContentImpl implements StructuredContent, AdminMainEntity, ProfileEntity {

    public static final String SC_DONT_DUPLICATE_SC_TYPE_HINT = "dont-duplicate-sc-type";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "StructuredContentId")
    @GenericGenerator(
            name = "StructuredContentId",
            type = IdOverrideTableGenerator.class,
            parameters = {
                    @Parameter(name = "segment_value", value = "StructuredContentImpl"),
                    @Parameter(name = "entity_name",
                            value = "org.broadleafcommerce.cms.structure.domain.StructuredContentImpl")
            }
    )
    @Column(name = "SC_ID")
    protected Long id;

    @AdminPresentation(friendlyName = "StructuredContentImpl_Content_Name", order = 1,
            group = Presentation.Group.Name.Description,
            groupOrder = Presentation.Group.Order.Description,
            prominent = true, gridOrder = 1)
    @Column(name = "CONTENT_NAME", nullable = false)
    protected String contentName;

    @ManyToOne(targetEntity = LocaleImpl.class, optional = false)
    @JoinColumn(name = "LOCALE_CODE")
    @AdminPresentation(friendlyName = "StructuredContentImpl_Locale", order = 2,
            group = Presentation.Group.Name.Description,
            groupOrder = Presentation.Group.Order.Description,
            prominent = true, gridOrder = 2)
    @AdminPresentationToOneLookup(lookupDisplayProperty = "friendlyName",
            lookupType = LookupType.DROPDOWN)
    protected Locale locale;

    @Column(name = "PRIORITY", nullable = false)
    @AdminPresentation(friendlyName = "StructuredContentImpl_Priority", order = 3,
            group = Presentation.Group.Name.Description,
            groupOrder = Presentation.Group.Order.Description)
    protected Integer priority;
    @OneToMany(fetch = FetchType.LAZY, targetEntity = StructuredContentItemCriteriaImpl.class,
            cascade = {CascadeType.ALL})
    @JoinTable(name = "BLC_QUAL_CRIT_SC_XREF", joinColumns = @JoinColumn(name = "SC_ID"),
            inverseJoinColumns = @JoinColumn(name = "SC_ITEM_CRITERIA_ID"))
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blCMSElements")
    @IgnoreEnterpriseBehavior
    protected Set<StructuredContentItemCriteria> qualifyingItemCriteria = new HashSet<>();
    @ManyToOne(targetEntity = StructuredContentTypeImpl.class)
    @JoinColumn(name = "SC_TYPE_ID")
    @AdminPresentation(friendlyName = "StructuredContentImpl_Content_Type", order = 2,
            prominent = true,
            group = Presentation.Group.Name.Description,
            groupOrder = Presentation.Group.Order.Description,
            requiredOverride = RequiredOverride.REQUIRED)
    @AdminPresentationToOneLookup(lookupDisplayProperty = "name",
            forcePopulateChildProperties = true)
    protected StructuredContentType structuredContentType;
    @OneToMany(mappedBy = "structuredContent", targetEntity = StructuredContentFieldXrefImpl.class,
            cascade = CascadeType.ALL)
    @MapKey(name = "key")
    @BatchSize(size = 20)
    @AdminPresentationMap(forceFreeFormKeys = true, friendlyName = "structuredContentFields")
    protected Map<String, StructuredContentFieldXref> structuredContentFields = new HashMap<>();
    @AdminPresentation(friendlyName = "StructuredContentImpl_Offline", order = 4,
            group = Presentation.Group.Name.Description,
            groupOrder = Presentation.Group.Order.Description)
    @Column(name = "OFFLINE_FLAG")
    protected Boolean offlineFlag = false;
    @Transient
    protected Map<String, String> fieldValuesMap = null;
    @ManyToMany(targetEntity = StructuredContentRuleImpl.class, cascade = {CascadeType.ALL})
    @JoinTable(name = "BLC_SC_RULE_MAP",
            joinColumns = @JoinColumn(name = "BLC_SC_SC_ID", referencedColumnName = "SC_ID"),
            inverseJoinColumns = @JoinColumn(name = "SC_RULE_ID",
                    referencedColumnName = "SC_RULE_ID"))
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @MapKeyColumn(name = "MAP_KEY", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blCMSElements")
    @IgnoreEnterpriseBehavior
    Map<String, StructuredContentRule> structuredContentMatchRules =
            new HashMap<String, StructuredContentRule>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getContentName() {
        return contentName;
    }

    @Override
    public void setContentName(String contentName) {
        this.contentName = contentName;
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
    public StructuredContentType getStructuredContentType() {
        return structuredContentType;
    }

    @Override
    public void setStructuredContentType(StructuredContentType structuredContentType) {
        this.structuredContentType = structuredContentType;
    }

    @Override
    public Map<String, StructuredContentFieldXref> getStructuredContentFieldXrefs() {
        return structuredContentFields;
    }

    @Override
    public void setStructuredContentFieldXrefs(@Nullable Map<String, StructuredContentFieldXref> structuredContentFields) {
        this.structuredContentFields = structuredContentFields;
    }

    @Override
    public String getFieldValue(String fieldName) {
        if (structuredContentFields.containsKey(fieldName)) {
            return getStructuredContentFieldXrefs().get(fieldName).getStructuredContentField()
                    .getValue();
        }
        return null;
    }

    @Override
    public Map<String, String> getFieldValues() {
        if (fieldValuesMap == null) {
            fieldValuesMap = new HashMap<String, String>();
            for (Entry<String, StructuredContentFieldXref> entry : getStructuredContentFieldXrefs().entrySet()) {
                fieldValuesMap.put(entry.getKey(),
                        entry.getValue().getStructuredContentField().getValue());
            }
        }
        return fieldValuesMap;
    }

    @Override
    public void setFieldValues(Map<String, String> fieldValuesMap) {
        this.fieldValuesMap = fieldValuesMap;
    }

    @Override
    public Boolean getOfflineFlag() {
        if (offlineFlag == null) {
            return Boolean.FALSE;
        } else {
            return offlineFlag;
        }
    }

    @Override
    public void setOfflineFlag(Boolean offlineFlag) {
        this.offlineFlag = offlineFlag;
    }

    @Override
    public Integer getPriority() {
        return priority;
    }

    @Override
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public Map<String, StructuredContentRule> getStructuredContentMatchRules() {
        return structuredContentMatchRules;
    }

    @Override
    public void setStructuredContentMatchRules(Map<String, StructuredContentRule> structuredContentMatchRules) {
        this.structuredContentMatchRules = structuredContentMatchRules;
    }

    @Override
    public Set<StructuredContentItemCriteria> getQualifyingItemCriteria() {
        return qualifyingItemCriteria;
    }

    @Override
    public void setQualifyingItemCriteria(Set<StructuredContentItemCriteria> qualifyingItemCriteria) {
        this.qualifyingItemCriteria = qualifyingItemCriteria;
    }

    @Override
    public String getMainEntityName() {
        return getContentName();
    }

    @Override
    public <G extends StructuredContent> CreateResponse<G> createOrRetrieveCopyInstance(
            MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        StructuredContent cloned = createResponse.getClone();
        cloned.setContentName(contentName);
        cloned.setLocale(locale);
        cloned.setOfflineFlag(offlineFlag);
        cloned.setPriority(priority);

        if (structuredContentType != null) {
            if (Boolean.valueOf(context.getCopyHints().get(SC_DONT_DUPLICATE_SC_TYPE_HINT))) {
                cloned.setStructuredContentType(structuredContentType);
            } else {
                CreateResponse<StructuredContentType> clonedType =
                        structuredContentType.createOrRetrieveCopyInstance(context);
                cloned.setStructuredContentType(clonedType.getClone());
            }
        }

        for (StructuredContentItemCriteria itemCriteria : qualifyingItemCriteria) {
            CreateResponse<StructuredContentItemCriteria> clonedItem =
                    itemCriteria.createOrRetrieveCopyInstance(context);
            StructuredContentItemCriteria clonedCritera = clonedItem.getClone();
            cloned.getQualifyingItemCriteria().add(clonedCritera);
        }

        for (Entry<String, StructuredContentRule> entry : structuredContentMatchRules.entrySet()) {
            CreateResponse<StructuredContentRule> clonedItem =
                    entry.getValue().createOrRetrieveCopyInstance(context);
            StructuredContentRule clonedRule = clonedItem.getClone();
            cloned.getStructuredContentMatchRules().put(entry.getKey(), clonedRule);

        }

        for (Entry<String, StructuredContentFieldXref> entry : structuredContentFields.entrySet()) {
            CreateResponse<StructuredContentFieldXref> clonedItem =
                    entry.getValue().createOrRetrieveCopyInstance(context);
            StructuredContentFieldXref clonedContentFieldXref = clonedItem.getClone();
            cloned.getStructuredContentFieldXrefs().put(entry.getKey(), clonedContentFieldXref);
        }

        return createResponse;
    }

    public static class Presentation {
        public static class Tab {
            public static class Name {
                public static final String Rules = "StructuredContentImpl_Rules_Tab";
            }


            public static class Order {
                public static final int Rules = 1000;
            }
        }


        public static class Group {
            public static class Name {
                public static final String Description = "StructuredContentImpl_Description";
                public static final String Internal = "StructuredContentImpl_Internal";
                public static final String Rules = "StructuredContentImpl_Rules";
            }


            public static class Order {
                public static final int Description = 1000;
                public static final int Internal = 2000;
                public static final int Rules = 1000;
            }
        }
    }

}
