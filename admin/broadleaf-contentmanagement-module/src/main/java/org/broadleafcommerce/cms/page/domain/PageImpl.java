/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.page.domain;

import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.clone.IgnoreEnterpriseBehavior;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.extensibility.jpa.copy.ProfileEntity;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverrides;
import org.broadleafcommerce.common.web.Locatable;
import org.broadleafcommerce.openadmin.audit.AdminAuditableListener;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PAGE")
@EntityListeners(value = { AdminAuditableListener.class })
@AdminPresentationOverrides(
    {
        @AdminPresentationOverride(name="auditable.createdBy.id", value=@AdminPresentation(readOnly = true, visibility = VisibilityEnum.HIDDEN_ALL)),
        @AdminPresentationOverride(name="auditable.updatedBy.id", value=@AdminPresentation(readOnly = true, visibility = VisibilityEnum.HIDDEN_ALL)),
        @AdminPresentationOverride(name="auditable.createdBy.name", value=@AdminPresentation(readOnly = true, visibility = VisibilityEnum.HIDDEN_ALL)),
        @AdminPresentationOverride(name="auditable.updatedBy.name", value=@AdminPresentation(readOnly = true, visibility = VisibilityEnum.HIDDEN_ALL)),
        @AdminPresentationOverride(name="auditable.dateCreated", value=@AdminPresentation(readOnly = true, visibility = VisibilityEnum.HIDDEN_ALL)),
        @AdminPresentationOverride(name="auditable.dateUpdated", value=@AdminPresentation(readOnly = true, visibility = VisibilityEnum.HIDDEN_ALL)),
        @AdminPresentationOverride(name="pageTemplate.templateDescription", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="pageTemplate.templateName", value=@AdminPresentation(excluded = true)),
        @AdminPresentationOverride(name="pageTemplate.locale", value=@AdminPresentation(excluded = true))
    }
)
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.AUDITABLE_ONLY)
})
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blCMSElements")
public class PageImpl implements Page, AdminMainEntity, Locatable, ProfileEntity, PageAdminPresentation {

    private static final long serialVersionUID = 1L;

    private static final Integer ZERO = new Integer(0);

    @Id
    @GeneratedValue(generator = "PageId")
    @GenericGenerator(
            name="PageId",
            strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
            parameters = {
                @Parameter(name="segment_value", value="PageImpl"),
                @Parameter(name="entity_name", value="org.broadleafcommerce.cms.page.domain.PageImpl")
            }
        )
    @Column(name = "PAGE_ID")
    protected Long id;

    @ManyToOne(targetEntity = PageTemplateImpl.class)
    @JoinColumn(name = "PAGE_TMPLT_ID")
    @AdminPresentation(friendlyName = "PageImpl_Page_Template", order = 4000,
        group = PageAdminPresentation.GroupName.Misc, groupOrder = PageAdminPresentation.GroupOrder.Misc, prominent = true,
        requiredOverride = RequiredOverride.REQUIRED)
    @AdminPresentationToOneLookup(lookupDisplayProperty = "templateName")
    protected PageTemplate pageTemplate;

    @Column (name = "DESCRIPTION")
    @AdminPresentation(friendlyName = "PageImpl_Description", order = 1000,
        group = PageAdminPresentation.GroupName.Basic, groupOrder = PageAdminPresentation.GroupOrder.Basic,
            requiredOverride = RequiredOverride.REQUIRED,
        prominent = true, gridOrder = 1)
    protected String description;

    @Column (name = "FULL_URL")
    @Index(name="PAGE_FULL_URL_INDEX", columnNames={"FULL_URL"})
    @AdminPresentation(friendlyName = "PageImpl_Full_Url", order = 3000,
        group = PageAdminPresentation.GroupName.Basic, groupOrder = PageAdminPresentation.GroupOrder.Basic,
        prominent = true, gridOrder = 2,
        validationConfigurations = { @ValidationConfiguration(validationImplementation = "blUriPropertyValidator") })
    protected String fullUrl;

    @OneToMany(mappedBy = "page", targetEntity = PageFieldImpl.class, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @MapKey(name = "fieldKey")
    @BatchSize(size = 20)
    @AdminPresentationMap(forceFreeFormKeys = true, friendlyName = "pageFields")
    protected Map<String,PageField> pageFields = new HashMap<String,PageField>();

    @Column(name = "PRIORITY")
    @Deprecated
    protected Integer priority;

    @Column(name = "OFFLINE_FLAG")
    @AdminPresentation(friendlyName = "PageImpl_Offline", order = 3500,
        group = GroupName.Misc, defaultValue = "false")
    protected Boolean offlineFlag = false;

    /*
     * This will not work with Enterprise workflows.  Do not use.
     */
    @ManyToMany(targetEntity = PageRuleImpl.class, cascade = {CascadeType.ALL})
    @JoinTable(name = "BLC_PAGE_RULE_MAP",
        inverseJoinColumns = @JoinColumn(name = "PAGE_RULE_ID", referencedColumnName = "PAGE_RULE_ID"))
    @Cascade(value = { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @MapKeyColumn(name = "MAP_KEY", nullable = false)
    @Deprecated
    protected Map<String, PageRule> pageMatchRules = new HashMap<String, PageRule>();

    /*
     * This will not work with Enterprise workflows. Do not use.
     */
    @OneToMany(fetch = FetchType.LAZY, targetEntity = PageItemCriteriaImpl.class, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @JoinTable(name = "BLC_QUAL_CRIT_PAGE_XREF",
        joinColumns = @JoinColumn(name = "PAGE_ID"),
        inverseJoinColumns = @JoinColumn(name = "PAGE_ITEM_CRITERIA_ID"))
    @Deprecated
    @IgnoreEnterpriseBehavior
    protected Set<PageItemCriteria> qualifyingItemCriteria = new HashSet<PageItemCriteria>();

    @Column(name = "EXCLUDE_FROM_SITE_MAP")
    @AdminPresentation(friendlyName = "PageImpl_Exclude_From_Site_Map", order = 1800,
        tab = TabName.Seo, group = GroupName.Sitemap, defaultValue = "false")
    protected Boolean excludeFromSiteMap = false;

    @OneToMany(mappedBy = "page", targetEntity = PageAttributeImpl.class, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @MapKey(name = "name")
    @BatchSize(size = 50)
    @AdminPresentationMap(friendlyName = "PageImpl_Page_Attributes_Title",
        deleteEntityUponRemove = true, forceFreeFormKeys = true, keyPropertyFriendlyName = "PageAttributeImpl_Name"
    )
    protected Map<String, PageAttribute> additionalAttributes = new HashMap<String, PageAttribute>();

    @Column(name = "ACTIVE_START_DATE")
    @AdminPresentation(friendlyName = "PageImpl_activeStartDate", order = 5000,
        group = PageAdminPresentation.GroupName.Basic, groupOrder = PageAdminPresentation.GroupOrder.Basic,
        excluded = true)
    protected Date activeStartDate;

    @Column(name = "ACTIVE_END_DATE")
    @AdminPresentation(friendlyName = "PageImpl_activeEndDate", order = 6000,
        group = PageAdminPresentation.GroupName.Basic, groupOrder = PageAdminPresentation.GroupOrder.Basic,
        excluded = true)
    protected Date activeEndDate;

    @Column (name = "META_TITLE")
    @AdminPresentation(friendlyName = "PageImpl_metaTitle", order = 2000,
        tab = TabName.Seo, group = GroupName.Tags, largeEntry = true)
    protected String metaTitle;

    @Column (name = "META_DESCRIPTION")
    @AdminPresentation(friendlyName = "PageImpl_metaDescription", order = 3000,
        tab = TabName.Seo, group = GroupName.Tags, largeEntry = true)
    protected String metaDescription;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public PageTemplate getPageTemplate() {
        return pageTemplate;
    }

    @Override
    public void setPageTemplate(PageTemplate pageTemplate) {
        this.pageTemplate = pageTemplate;
    }

    @Override
    public Map<String, PageField> getPageFields() {
        return pageFields;
    }

    @Override
    public void setPageFields(Map<String, PageField> pageFields) {
        this.pageFields = pageFields;
    }

    @Override
    public String getFullUrl() {
        return fullUrl;
    }

    @Override
    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
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
    public Boolean getOfflineFlag() {
        return offlineFlag == null ? false : offlineFlag;
    }

    @Override
    public void setOfflineFlag(Boolean offlineFlag) {
        this.offlineFlag = offlineFlag == null ? false : offlineFlag;
    }

    @Override
    public Integer getPriority() {
        if (priority == null) {
            return ZERO;
        }
        return priority;
    }

    @Override
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @Override
    public Map<String, PageRule> getPageMatchRules() {
        return pageMatchRules;
    }

    @Override
    public void setPageMatchRules(Map<String, PageRule> pageMatchRules) {
        this.pageMatchRules = pageMatchRules;
    }

    @Override
    public Set<PageItemCriteria> getQualifyingItemCriteria() {
        return qualifyingItemCriteria;
    }

    @Override
    public void setQualifyingItemCriteria(Set<PageItemCriteria> qualifyingItemCriteria) {
        this.qualifyingItemCriteria = qualifyingItemCriteria;
    }

    @Override
    public boolean getExcludeFromSiteMap() {
        if (this.excludeFromSiteMap == null) {
            return false;
        }
        return excludeFromSiteMap;
    }

    @Override
    public void setExcludeFromSiteMap(boolean excludeFromSiteMap) {
        this.excludeFromSiteMap = excludeFromSiteMap;
    }

    @Override
    public <G extends Page> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }

        Page cloned = createResponse.getClone();
        cloned.setPriority(priority);
        cloned.setActiveEndDate(activeEndDate);
        cloned.setActiveStartDate(activeStartDate);
        cloned.setDescription(description);
        cloned.setExcludeFromSiteMap(getExcludeFromSiteMap());
        cloned.setFullUrl(fullUrl);
        cloned.setMetaDescription(metaDescription);
        cloned.setOfflineFlag(offlineFlag);
        cloned.setMetaTitle(metaTitle);
        for(Map.Entry<String, PageField> entry : pageFields.entrySet()){
            CreateResponse<PageField> clonedPageField = entry.getValue().createOrRetrieveCopyInstance(context);
            PageField pageField = clonedPageField.getClone();
            cloned.getPageFields().put(entry.getKey(),pageField);
        }
        for(Map.Entry<String,PageRule> entry : pageMatchRules.entrySet()){
            CreateResponse<PageRule> clonedRsp = entry.getValue().createOrRetrieveCopyInstance(context);
            PageRule clonedRule = clonedRsp.getClone();
            cloned.getPageMatchRules().put(entry.getKey(),clonedRule);
        }
        if (pageTemplate != null){
            CreateResponse<PageTemplate> clonedTemplateRsp = pageTemplate.createOrRetrieveCopyInstance(context);
            cloned.setPageTemplate(clonedTemplateRsp.getClone());
        }
        return createResponse;
    }

    @Override
    public String getMainEntityName() {
        return getDescription();
    }

    @Override
    public String getLocation() {
        return getFullUrl();
    }

    @Override
    public Map<String, PageAttribute> getAdditionalAttributes() {
        return additionalAttributes;
    }

    @Override
    public void setAdditionalAttributes(Map<String, PageAttribute> additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
    }

    @Override
    public Date getActiveStartDate() {
        return activeStartDate;
    }

    @Override
    public void setActiveStartDate(Date activeStartDate) {
        this.activeStartDate = activeStartDate;
    }

    @Override
    public Date getActiveEndDate() {
        return activeEndDate;
    }

    @Override
    public void setActiveEndDate(Date activeEndDate) {
        this.activeEndDate = activeEndDate;
    }

    @Override
    public String getMetaTitle() {
        return metaTitle;
    }

    @Override
    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    @Override
    public String getMetaDescription() {
        return metaDescription;
    }

    @Override
    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

}

