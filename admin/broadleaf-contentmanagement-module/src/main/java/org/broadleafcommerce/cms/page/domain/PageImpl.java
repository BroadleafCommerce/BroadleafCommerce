/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.page.domain;

import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.extensibility.jpa.clone.ClonePolicyCollection;
import org.broadleafcommerce.common.extensibility.jpa.clone.ClonePolicyMap;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationMapField;
import org.broadleafcommerce.common.presentation.AdminPresentationMapFields;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.RuleIdentifier;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverrides;
import org.broadleafcommerce.common.web.Locatable;
import org.broadleafcommerce.openadmin.audit.AdminAuditable;
import org.broadleafcommerce.openadmin.audit.AdminAuditableListener;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "PageImpl_basePage")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_CATALOG)
})
public class PageImpl implements Page, AdminMainEntity, Locatable {

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
    @AdminPresentation(friendlyName = "PageImpl_Page_Template", order = 2,
        group = Presentation.Group.Name.Basic, groupOrder = Presentation.Group.Order.Basic, prominent = true,
        requiredOverride = RequiredOverride.REQUIRED)
    @AdminPresentationToOneLookup(lookupDisplayProperty = "templateName")
    protected PageTemplate pageTemplate;

    @Column (name = "DESCRIPTION")
    @AdminPresentation(friendlyName = "PageImpl_Description", order = 3, 
        group = Presentation.Group.Name.Basic, groupOrder = Presentation.Group.Order.Basic,
        prominent = true, gridOrder = 1)
    protected String description;

    @Column (name = "FULL_URL")
    @Index(name="PAGE_FULL_URL_INDEX", columnNames={"FULL_URL"})
    @AdminPresentation(friendlyName = "PageImpl_Full_Url", order = 1, 
        group = Presentation.Group.Name.Basic, groupOrder = Presentation.Group.Order.Basic,
            prominent = true, gridOrder = 2,
            validationConfigurations = { @ValidationConfiguration(validationImplementation = "blUriPropertyValidator") })
    protected String fullUrl;

    @ManyToMany(targetEntity = PageFieldImpl.class, cascade = CascadeType.ALL)
    @JoinTable(name = "BLC_PAGE_FLD_MAP", 
        joinColumns = @JoinColumn(name = "PAGE_ID", referencedColumnName = "PAGE_ID"), 
        inverseJoinColumns = @JoinColumn(name = "PAGE_FLD_ID", referencedColumnName = "PAGE_FLD_ID"))
    @MapKeyColumn(name = "MAP_KEY")
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @BatchSize(size = 20)
    @ClonePolicyMap
    protected Map<String,PageField> pageFields = new HashMap<String,PageField>();
    
    @Column(name = "PRIORITY")
    @AdminPresentation(friendlyName = "PageImpl_Priority", order = 3, 
        group = Presentation.Group.Name.Basic, groupOrder = Presentation.Group.Order.Basic)
    protected Integer priority;
    
    @Column(name = "OFFLINE_FLAG")
    @AdminPresentation(friendlyName = "PageImpl_Offline", order = 4, 
        group = Presentation.Group.Name.Basic, groupOrder = Presentation.Group.Order.Basic)
    protected Boolean offlineFlag = false;     

    @ManyToMany(targetEntity = PageRuleImpl.class, cascade = {CascadeType.ALL})
    @JoinTable(name = "BLC_PAGE_RULE_MAP", 
        inverseJoinColumns = @JoinColumn(name = "PAGE_RULE_ID", referencedColumnName = "PAGE_RULE_ID"))
    @Cascade(value = { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @MapKeyColumn(name = "MAP_KEY", nullable = false)
    @AdminPresentationMapFields(
        mapDisplayFields = {
            @AdminPresentationMapField(
                fieldName = RuleIdentifier.CUSTOMER_FIELD_KEY,
                fieldPresentation = @AdminPresentation(fieldType = SupportedFieldType.RULE_SIMPLE, order = 1,
                    tab = Presentation.Tab.Name.Rules, tabOrder = Presentation.Tab.Order.Rules,
                    group = Presentation.Group.Name.Rules, groupOrder = Presentation.Group.Order.Rules,
                    ruleIdentifier = RuleIdentifier.CUSTOMER, friendlyName = "Generic_Customer_Rule")
            ),
            @AdminPresentationMapField(
                fieldName = RuleIdentifier.TIME_FIELD_KEY,
                fieldPresentation = @AdminPresentation(fieldType = SupportedFieldType.RULE_SIMPLE, order = 2,
                    tab = Presentation.Tab.Name.Rules, tabOrder = Presentation.Tab.Order.Rules,
                    group = Presentation.Group.Name.Rules, groupOrder = Presentation.Group.Order.Rules,
                    ruleIdentifier = RuleIdentifier.TIME, friendlyName = "Generic_Time_Rule")
            ),
            @AdminPresentationMapField(
                fieldName = RuleIdentifier.REQUEST_FIELD_KEY,
                fieldPresentation = @AdminPresentation(fieldType = SupportedFieldType.RULE_SIMPLE, order = 3,
                    tab = Presentation.Tab.Name.Rules, tabOrder = Presentation.Tab.Order.Rules,
                    group = Presentation.Group.Name.Rules, groupOrder = Presentation.Group.Order.Rules,
                    ruleIdentifier = RuleIdentifier.REQUEST, friendlyName = "Generic_Request_Rule")
            ),
            @AdminPresentationMapField(
                fieldName = RuleIdentifier.PRODUCT_FIELD_KEY,
                fieldPresentation = @AdminPresentation(fieldType = SupportedFieldType.RULE_SIMPLE, order = 4,
                    tab = Presentation.Tab.Name.Rules, tabOrder = Presentation.Tab.Order.Rules,
                    group = Presentation.Group.Name.Rules, groupOrder = Presentation.Group.Order.Rules,
                    ruleIdentifier = RuleIdentifier.PRODUCT, friendlyName = "Generic_Product_Rule")
            )
        }
    )
    @ClonePolicyMap
    Map<String, PageRule> pageMatchRules = new HashMap<String, PageRule>();

    @OneToMany(fetch = FetchType.LAZY, targetEntity = PageItemCriteriaImpl.class, cascade={CascadeType.ALL})
    @JoinTable(name = "BLC_QUAL_CRIT_PAGE_XREF", 
        joinColumns = @JoinColumn(name = "PAGE_ID"), 
        inverseJoinColumns = @JoinColumn(name = "PAGE_ITEM_CRITERIA_ID"))
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @AdminPresentation(friendlyName = "Generic_Item_Rule", order = 5,
        tab = Presentation.Tab.Name.Rules, tabOrder = Presentation.Tab.Order.Rules,
        group = Presentation.Group.Name.Rules, groupOrder = Presentation.Group.Order.Rules,
        fieldType = SupportedFieldType.RULE_WITH_QUANTITY, 
        ruleIdentifier = RuleIdentifier.ORDERITEM)
    @ClonePolicyCollection
    protected Set<PageItemCriteria> qualifyingItemCriteria = new HashSet<PageItemCriteria>();

    @Column(name = "EXCLUDE_FROM_SITE_MAP")
    @AdminPresentation(friendlyName = "PageImpl_Exclude_From_Site_Map", order = 6,
            group = Presentation.Group.Name.Basic, groupOrder = Presentation.Group.Order.Basic)
    protected Boolean excludeFromSiteMap;

    @Embedded
    @AdminPresentation(excluded = true)
    protected AdminAuditable auditable = new AdminAuditable();

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
    public AdminAuditable getAuditable() {
        return auditable;
    }

    @Override
    public void setAuditable(AdminAuditable auditable) {
        this.auditable = auditable;
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

    public static class Presentation {
        public static class Tab {
            public static class Name {
                public static final String Rules = "PageImpl_Rules_Tab";
            }
            
            public static class Order {
                public static final int Rules = 1000;
            }
        }
            
        public static class Group {
            public static class Name {
                public static final String Basic = "PageImpl_Basic";
                public static final String Page = "PageImpl_Page";
                public static final String Rules = "PageImpl_Rules";
            }
            
            public static class Order {
                public static final int Basic = 1000;
                public static final int Page = 2000;
                public static final int Rules = 1000;
            }
        }
    }

    @Override
    public String getMainEntityName() {
        return getDescription();
    }

    @Override
    public String getLocation() {
        return getFullUrl();
    }
}

