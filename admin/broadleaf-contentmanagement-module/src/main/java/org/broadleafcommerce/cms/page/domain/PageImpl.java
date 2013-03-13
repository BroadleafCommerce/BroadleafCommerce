/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.cms.page.domain;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationOverrides;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxImpl;
import org.broadleafcommerce.openadmin.audit.AdminAuditable;
import org.broadleafcommerce.openadmin.audit.AdminAuditableListener;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        @AdminPresentationOverride(name="pageTemplate.locale", value=@AdminPresentation(excluded = true))
    }
)
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "PageImpl_basePage")
public class PageImpl implements Page {

    private static final long serialVersionUID = 1L;
    
    private static final Integer ZERO = new Integer(0);

    @Id
    @GeneratedValue(generator = "PageId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "PageId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "PageImpl", allocationSize = 10)
    @Column(name = "PAGE_ID")
    protected Long id;
    
    @ManyToOne (targetEntity = PageTemplateImpl.class)
    @JoinColumn(name = "PAGE_TMPLT_ID")
    @AdminPresentation(friendlyName = "PageImpl_Page_Template", group = "PageImpl_Basic", order=2, excluded=true, visibility = VisibilityEnum.GRID_HIDDEN, requiredOverride = RequiredOverride.REQUIRED)
    protected PageTemplate pageTemplate;

    @Column (name = "DESCRIPTION")
    @AdminPresentation(friendlyName = "PageImpl_Description", order=3, group = "PageImpl_Basic", prominent=true)
    protected String description;

    @Column (name = "FULL_URL")
    @Index(name="PAGE_FULL_URL_INDEX", columnNames={"FULL_URL"})
    @AdminPresentation(friendlyName = "PageImpl_Full_Url", order=1, group = "PageImpl_Basic", prominent=true)
    protected String fullUrl;

    @ManyToMany(targetEntity = PageFieldImpl.class, cascade = CascadeType.ALL)
    @JoinTable(name = "BLC_PAGE_FLD_MAP", joinColumns = @JoinColumn(name = "PAGE_ID", referencedColumnName = "PAGE_ID"), inverseJoinColumns = @JoinColumn(name = "PAGE_FLD_ID", referencedColumnName = "PAGE_FLD_ID"))
    @MapKeyColumn(name = "MAP_KEY")
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @BatchSize(size = 20)
    protected Map<String,PageField> pageFields = new HashMap<String,PageField>();

    @ManyToOne (targetEntity = SandBoxImpl.class)
    @JoinColumn(name="SANDBOX_ID")
    @AdminPresentation(excluded = true)
    protected SandBox sandbox;

    @ManyToOne(targetEntity = SandBoxImpl.class)
    @JoinColumn(name = "ORIG_SANDBOX_ID")
    @AdminPresentation(excluded = true)
    protected SandBox originalSandBox;

    @Column (name = "DELETED_FLAG")
    @AdminPresentation(friendlyName = "PageImpl_Deleted", order=2, group = "PageImpl_Description", visibility = VisibilityEnum.HIDDEN_ALL)
    @Index(name="PAGE_DLTD_FLG_INDX", columnNames={"DELETED_FLAG"})
    protected Boolean deletedFlag = false;

    @Column (name = "ARCHIVED_FLAG")
    @AdminPresentation(friendlyName = "PageImpl_Archived", order=5, group = "PageImpl_Page", visibility = VisibilityEnum.HIDDEN_ALL)
    @Index(name="PAGE_ARCHVD_FLG_INDX", columnNames={"ARCHIVED_FLAG"})
    protected Boolean archivedFlag = false;

    @Column (name = "LOCKED_FLAG")
    @AdminPresentation(friendlyName = "PageImpl_Is_Locked", visibility = VisibilityEnum.HIDDEN_ALL)
    @Index(name="PAGE_LCKD_FLG_INDX", columnNames={"LOCKED_FLAG"})
    protected Boolean lockedFlag = false;

    @Column (name = "ORIG_PAGE_ID")
    @AdminPresentation(friendlyName = "PageImpl_Original_Page_ID", order=6, group = "PageImpl_Page", visibility = VisibilityEnum.HIDDEN_ALL)
    @Index(name="ORIG_PAGE_ID_INDX", columnNames={"ORIG_PAGE_ID"})
    protected Long originalPageId;      
    
    @AdminPresentation(friendlyName = "PageImpl_Priority", order=3, group = "PageImpl_Description")
    @Column(name = "PRIORITY")
    protected Integer priority;
    
    @AdminPresentation(friendlyName = "PageImpl_Offline", order=4, group = "PageImpl_Description")
    @Column(name = "OFFLINE_FLAG")
    protected Boolean offlineFlag = false;     

    @ManyToMany(targetEntity = PageRuleImpl.class, cascade = {CascadeType.ALL})
    @JoinTable(name = "BLC_PAGE_RULE_MAP", inverseJoinColumns = @JoinColumn(name = "PAGE_RULE_ID", referencedColumnName = "PAGE_RULE_ID"))
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @MapKeyColumn(name = "MAP_KEY", nullable = false)
    Map<String, PageRule> pageMatchRules = new HashMap<String, PageRule>();

    @OneToMany(fetch = FetchType.LAZY, targetEntity = PageItemCriteriaImpl.class, cascade={CascadeType.ALL})
    @JoinTable(name = "BLC_QUAL_CRIT_PAGE_XREF", joinColumns = @JoinColumn(name = "PAGE_ID"), inverseJoinColumns = @JoinColumn(name = "PAGE_ITEM_CRITERIA_ID"))
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    protected Set<PageItemCriteria> qualifyingItemCriteria = new HashSet<PageItemCriteria>();

    

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
    public Boolean getDeletedFlag() {
        if (deletedFlag == null) {
            return Boolean.FALSE;
        } else {
            return deletedFlag;
        }
    }

    @Override
    public void setDeletedFlag(Boolean deletedFlag) {
        this.deletedFlag = deletedFlag;
    }

    @Override
    public Boolean getArchivedFlag() {
        if (archivedFlag == null) {
            return Boolean.FALSE;
        } else {
            return archivedFlag;
        }
    }

    @Override
    public void setArchivedFlag(Boolean archivedFlag) {
        this.archivedFlag = archivedFlag;
    }

    @Override
    public SandBox getSandbox() {
        return sandbox;
    }

    @Override
    public void setSandbox(SandBox sandbox) {
        this.sandbox = sandbox;
    }


    @Override
    public Long getOriginalPageId() {
        return originalPageId;
    }

    @Override
    public void setOriginalPageId(Long originalPageId) {
        this.originalPageId = originalPageId;
    }

    public String getFullUrl() {
        return fullUrl;
    }

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

    public SandBox getOriginalSandBox() {
        return originalSandBox;
    }

    public void setOriginalSandBox(SandBox originalSandBox) {
        this.originalSandBox = originalSandBox;
    }

    public AdminAuditable getAuditable() {
        return auditable;
    }

    public void setAuditable(AdminAuditable auditable) {
        this.auditable = auditable;
    }

    public Boolean getLockedFlag() {
        if (lockedFlag == null) {
            return Boolean.FALSE;
        } else {
            return lockedFlag;
        }
    }

    public void setLockedFlag(Boolean lockedFlag) {
        this.lockedFlag = lockedFlag;
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
    public Page cloneEntity() {
        PageImpl newPage = new PageImpl();

        newPage.archivedFlag = archivedFlag;
        newPage.deletedFlag = deletedFlag;
        newPage.pageTemplate = pageTemplate;
        newPage.description = description;
        newPage.sandbox = sandbox;
        newPage.originalPageId = originalPageId;
        newPage.offlineFlag = offlineFlag;        
        newPage.priority = priority;
        newPage.originalSandBox = originalSandBox;
        newPage.fullUrl = fullUrl;
        
        Map<String, PageRule> ruleMap = newPage.getPageMatchRules();
        for (String key : pageMatchRules.keySet()) {
            PageRule newField = pageMatchRules.get(key).cloneEntity();
            ruleMap.put(key, newField);
        }

        Set<PageItemCriteria> criteriaList = newPage.getQualifyingItemCriteria();
        for (PageItemCriteria pageItemCriteria : qualifyingItemCriteria) {
            PageItemCriteria newField = pageItemCriteria.cloneEntity();
            criteriaList.add(newField);
        }

        for (PageField oldPageField: pageFields.values()) {
            PageField newPageField = oldPageField.cloneEntity();
            newPageField.setPage(newPage);
            newPage.getPageFields().put(newPageField.getFieldKey(), newPageField);
        }

        return newPage;
    }
}

