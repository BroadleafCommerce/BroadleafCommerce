/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.security.domain;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import java.util.ArrayList;
import java.util.List;

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
import javax.persistence.Table;

/**
 *
 * @author elbertbautista
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ADMIN_SECTION")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@AdminPresentationClass(friendlyName = "AdminSectionImpl_baseAdminSection")
public class AdminSectionImpl implements AdminSection {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "AdminSectionId")
    @GenericGenerator(
        name="AdminSectionId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="AdminSectionImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.openadmin.server.security.domain.AdminSectionImpl")
        }
    )
    @Column(name = "ADMIN_SECTION_ID")
    @AdminPresentation(friendlyName = "AdminSectionImpl_Admin_Section_ID", group = "AdminSectionImpl_Primary_Key", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @Column(name = "NAME", nullable=false)
    @Index(name="ADMINSECTION_NAME_INDEX", columnNames={"NAME"})
    @AdminPresentation(friendlyName = "AdminSectionImpl_Name", order=1, group = "AdminSectionImpl_Section", prominent=true)
    protected String name;

    @Column(name = "SECTION_KEY", nullable=false, unique = true)
    @AdminPresentation(friendlyName = "AdminSectionImpl_Section_Key", order=2, group = "AdminSectionImpl_Section", prominent=true)
    protected String sectionKey;

    @Column(name = "URL", nullable=true)
    @AdminPresentation(friendlyName = "AdminSectionImpl_Url", order=3, group = "AdminSectionImpl_Section", prominent=true)
    protected String url;

    @ManyToOne(optional=false, targetEntity = AdminModuleImpl.class)
    @JoinColumn(name = "ADMIN_MODULE_ID")
    @Index(name="ADMINSECTION_MODULE_INDEX", columnNames={"ADMIN_MODULE_ID"})
    protected AdminModule module;

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = AdminPermissionImpl.class)
    @JoinTable(name = "BLC_ADMIN_SEC_PERM_XREF", joinColumns = @JoinColumn(name = "ADMIN_SECTION_ID", referencedColumnName = "ADMIN_SECTION_ID"), inverseJoinColumns = @JoinColumn(name = "ADMIN_PERMISSION_ID", referencedColumnName = "ADMIN_PERMISSION_ID"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected List<AdminPermission> permissions = new ArrayList<AdminPermission>();

    /**
     * No longer needed after GWT removal
     * @param displayController
     */
    @Deprecated
    @Column(name = "DISPLAY_CONTROLLER", nullable=true)
    @AdminPresentation(friendlyName = "AdminSectionImpl_Display_Controller", order=4, group = "AdminSectionImpl_Section")
    protected String displayController;

    /**
     * No longer needed after GWT removal
     * @param displayController
     */
    @Deprecated
    @Column(name = "USE_DEFAULT_HANDLER", nullable = true)
    @AdminPresentation(friendlyName = "AdminSectionImpl_Use_Default_Handler", order=5, group = "AdminSectionImpl_Section")
    protected Boolean useDefaultHandler = Boolean.TRUE;

    @Column(name = "CEILING_ENTITY", nullable = true)
    @AdminPresentation(friendlyName = "AdminSectionImpl_Ceiling_Entity", order = 6, group = "AdminSectionImpl_Section")
    protected String ceilingEntity;

    @Column(name = "DISPLAY_ORDER", nullable = true)
    @AdminPresentation(friendlyName = "AdminSectionImpl_Display_Order", order = 7, group = "AdminSectionImpl_Section",
            prominent = true)
    protected Integer displayOrder;

    @Override
    public Long getId() {
        return id;
    }

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
    public String getSectionKey() {
        return sectionKey;
    }

    @Override
    public void setSectionKey(String sectionKey) {
        this.sectionKey = sectionKey;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public AdminModule getModule() {
        return module;
    }

    @Override
    public void setModule(AdminModule module) {
        this.module = module;
    }

    @Override
    public List<AdminPermission> getPermissions() {
        return permissions;
    }

    @Override
    public void setPermissions(List<AdminPermission> permissions) {
        this.permissions = permissions;
    }

    @Deprecated
    @Override
    public String getDisplayController() {
        return displayController;
    }

    @Deprecated
    @Override
    public void setDisplayController(String displayController) {
        this.displayController = displayController;
    }

    @Deprecated
    @Override
    public Boolean getUseDefaultHandler() {
        return useDefaultHandler;
    }

    @Deprecated
    @Override
    public void setUseDefaultHandler(Boolean useDefaultHandler) {
        this.useDefaultHandler = useDefaultHandler;
    }

    @Override
    public String getCeilingEntity() {
        return ceilingEntity;
    }

    @Override
    public void setCeilingEntity(String ceilingEntity) {
        this.ceilingEntity = ceilingEntity;
    }

    @Override
    public Integer getDisplayOrder() {
        return displayOrder;
    }

    @Override
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
