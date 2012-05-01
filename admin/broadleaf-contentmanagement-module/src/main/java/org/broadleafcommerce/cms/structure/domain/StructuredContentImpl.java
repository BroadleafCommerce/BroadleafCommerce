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

package org.broadleafcommerce.cms.structure.domain;

import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.domain.LocaleImpl;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationOverride;
import org.broadleafcommerce.common.presentation.AdminPresentationOverrides;
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
@Table(name = "BLC_SC")
@EntityListeners(value = { AdminAuditableListener.class })
@AdminPresentationOverrides(
        {
            @AdminPresentationOverride(name="auditable.createdBy.id", value=@AdminPresentation(readOnly = true, visibility = VisibilityEnum.HIDDEN_ALL)),
            @AdminPresentationOverride(name="auditable.updatedBy.id", value=@AdminPresentation(readOnly = true, visibility = VisibilityEnum.HIDDEN_ALL)),
            @AdminPresentationOverride(name="auditable.createdBy.name", value=@AdminPresentation(readOnly = true, visibility = VisibilityEnum.HIDDEN_ALL)),
            @AdminPresentationOverride(name="auditable.updatedBy.name", value=@AdminPresentation(readOnly = true, visibility = VisibilityEnum.HIDDEN_ALL)),
            @AdminPresentationOverride(name="auditable.dateCreated", value=@AdminPresentation(readOnly = true, visibility = VisibilityEnum.HIDDEN_ALL)),
            @AdminPresentationOverride(name="auditable.dateUpdated", value=@AdminPresentation(readOnly = true, visibility = VisibilityEnum.HIDDEN_ALL)),
            @AdminPresentationOverride(name="locale.id", value=@AdminPresentation(excluded = true)),
            @AdminPresentationOverride(name="locale.localeCode", value=@AdminPresentation(excluded = true)),
            @AdminPresentationOverride(name="locale.friendlyName", value=@AdminPresentation(excluded = true)),
            @AdminPresentationOverride(name="locale.defaultFlag", value=@AdminPresentation(excluded = true))
        }
)
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "baseStructuredContent")
public class StructuredContentImpl implements StructuredContent {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "StructuredContentId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "StructuredContentId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "StructuredContentImpl", allocationSize = 10)
    @Column(name = "SC_ID")
    protected Long id;

    @Embedded
    @AdminPresentation(excluded = true)
    protected AdminAuditable auditable = new AdminAuditable();

    @AdminPresentation(friendlyName="Content Name", order=1, groupOrder = 1, group="Description", prominent=true)
    @Column(name = "CONTENT_NAME", nullable = false)
    @Index(name="CONTENT_NAME_INDEX", columnNames={"CONTENT_NAME", "ARCHIVED_FLAG", "SC_TYPE_ID"})
    protected String contentName;

    @ManyToOne(targetEntity = LocaleImpl.class, optional = false)
    @JoinColumn(name = "LOCALE_CODE")
    @AdminPresentation(visibility = VisibilityEnum.HIDDEN_ALL)
    protected Locale locale;

    @AdminPresentation(friendlyName="Priority", order=3, group="Description")
    @Column(name = "PRIORITY", nullable = false)
    protected Integer priority;

    @ManyToMany(targetEntity = StructuredContentRuleImpl.class, cascade = {CascadeType.ALL})
    @JoinTable(name = "BLC_SC_RULE_MAP", inverseJoinColumns = @JoinColumn(name = "SC_RULE_ID", referencedColumnName = "SC_RULE_ID"))
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @MapKeyColumn(name = "MAP_KEY", nullable = false)
    Map<String, StructuredContentRule> structuredContentMatchRules = new HashMap<String, StructuredContentRule>();

    @OneToMany(fetch = FetchType.LAZY, targetEntity = StructuredContentItemCriteriaImpl.class, cascade={CascadeType.ALL})
    @JoinTable(name = "BLC_QUAL_CRIT_SC_XREF", joinColumns = @JoinColumn(name = "SC_ID"), inverseJoinColumns = @JoinColumn(name = "SC_ITEM_CRITERIA_ID"))
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    protected Set<StructuredContentItemCriteria> qualifyingItemCriteria = new HashSet<StructuredContentItemCriteria>();

    @AdminPresentation(friendlyName="Original Item Id", order=1, group="Internal", visibility = VisibilityEnum.HIDDEN_ALL)
    @Column(name = "ORIG_ITEM_ID")
    @Index(name="SC_ORIG_ITEM_ID_INDEX", columnNames={"ORIG_ITEM_ID"})
    protected Long originalItemId;

    @ManyToOne (targetEntity = SandBoxImpl.class)
    @JoinColumn(name="SANDBOX_ID")
    @AdminPresentation(friendlyName="Content SandBox", order=1, group="Stuctured Content", excluded = true)
    protected SandBox sandbox;

    @ManyToOne(targetEntity = SandBoxImpl.class)
    @JoinColumn(name = "ORIG_SANDBOX_ID")
    @AdminPresentation(excluded = true)
	protected SandBox originalSandBox;

    @ManyToOne(targetEntity = StructuredContentTypeImpl.class)
    @JoinColumn(name="SC_TYPE_ID")
    @AdminPresentation(friendlyName="Content Type", order=2, group="Description", excluded=true, visibility = VisibilityEnum.GRID_HIDDEN, requiredOverride = RequiredOverride.REQUIRED)
    protected StructuredContentType structuredContentType;

    @ManyToMany(targetEntity = StructuredContentFieldImpl.class, cascade = CascadeType.ALL)
    @JoinTable(name = "BLC_SC_FLD_MAP", joinColumns = @JoinColumn(name = "SC_ID", referencedColumnName = "SC_ID"), inverseJoinColumns = @JoinColumn(name = "SC_FLD_ID", referencedColumnName = "SC_FLD_ID"))
    @org.hibernate.annotations.MapKey(columns = {@Column(name = "MAP_KEY", nullable = false)})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @BatchSize(size = 20)
    protected Map<String,StructuredContentField> structuredContentFields = new HashMap<String,StructuredContentField>();

    @AdminPresentation(friendlyName="Deleted", order=2, group="Internal", visibility = VisibilityEnum.HIDDEN_ALL)
    @Column(name = "DELETED_FLAG")
    @Index(name="SC_DLTD_FLG_INDX", columnNames={"DELETED_FLAG"})
    protected Boolean deletedFlag;

    @AdminPresentation(friendlyName="Archived", order=3, group="Internal", visibility = VisibilityEnum.HIDDEN_ALL)
    @Column(name = "ARCHIVED_FLAG")
    @Index(name="SC_ARCHVD_FLG_INDX", columnNames={"ARCHIVED_FLAG"})
    protected Boolean archivedFlag;

    @AdminPresentation(friendlyName="Offline", order=4, group="Description")
    @Column(name = "OFFLINE_FLAG")
    @Index(name="SC_OFFLN_FLG_INDX", columnNames={"OFFLINE_FLAG"})
    protected Boolean offlineFlag = false;

    @Column (name = "LOCKED_FLAG")
    @AdminPresentation(friendlyName="Is Locked", visibility = VisibilityEnum.HIDDEN_ALL)
    @Index(name="SC_LCKD_FLG_INDX", columnNames={"LOCKED_FLAG"})
    protected Boolean lockedFlag = false;

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
    public SandBox getSandbox() {
        return sandbox;
    }

    @Override
    public void setSandbox(SandBox sandbox) {
        this.sandbox = sandbox;
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
    public Map<String, StructuredContentField> getStructuredContentFields() {
        return structuredContentFields;
    }

    @Override
    public void setStructuredContentFields(Map<String, StructuredContentField> structuredContentFields) {
        this.structuredContentFields = structuredContentFields;
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
    public Long getOriginalItemId() {
        return originalItemId;
    }

    @Override
    public void setOriginalItemId(Long originalItemId) {
        this.originalItemId = originalItemId;
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

    public SandBox getOriginalSandBox() {
        return originalSandBox;
    }

    public void setOriginalSandBox(SandBox originalSandBox) {
        this.originalSandBox = originalSandBox;
    }

    public Map<String, StructuredContentRule> getStructuredContentMatchRules() {
        return structuredContentMatchRules;
    }

    public void setStructuredContentMatchRules(Map<String, StructuredContentRule> structuredContentMatchRules) {
        this.structuredContentMatchRules = structuredContentMatchRules;
    }

    public Set<StructuredContentItemCriteria> getQualifyingItemCriteria() {
        return qualifyingItemCriteria;
    }

    public void setQualifyingItemCriteria(Set<StructuredContentItemCriteria> qualifyingItemCriteria) {
        this.qualifyingItemCriteria = qualifyingItemCriteria;
    }

    @Override
    public StructuredContent cloneEntity() {
        StructuredContentImpl newContent = new StructuredContentImpl();
        newContent.archivedFlag = archivedFlag;
        newContent.contentName = contentName;
        newContent.deletedFlag = deletedFlag;
        newContent.locale = locale;
        newContent.offlineFlag = offlineFlag;
        newContent.originalItemId = originalItemId;
        newContent.priority = priority;
        newContent.structuredContentType = structuredContentType;

        Map<String, StructuredContentRule> ruleMap = newContent.getStructuredContentMatchRules();
        for (String key : structuredContentMatchRules.keySet()) {
            StructuredContentRule newField = structuredContentMatchRules.get(key).cloneEntity();
            ruleMap.put(key, newField);
        }

        Set<StructuredContentItemCriteria> criteriaList = newContent.getQualifyingItemCriteria();
        for (StructuredContentItemCriteria structuredContentItemCriteria : qualifyingItemCriteria) {
            StructuredContentItemCriteria newField = structuredContentItemCriteria.cloneEntity();
            criteriaList.add(newField);
        }

        Map fieldMap = newContent.getStructuredContentFields();
        for (StructuredContentField field : structuredContentFields.values()) {
            StructuredContentField newField = field.cloneEntity();
            fieldMap.put(newField.getFieldKey(), newField);
        }
        return newContent;
    }

}
