/*
 * Copyright 2008-2009 the original author or authors.
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

package org.broadleafcommerce.openadmin.server.security.domain;

import org.broadleafcommerce.common.email.domain.EmailTrackingImpl;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
    @GeneratedValue(generator = "AdminSectionId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "AdminSectionId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "AdminSectionImpl", allocationSize = 50)
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

    @Column(name = "DISPLAY_CONTROLLER", nullable=true)
    @AdminPresentation(friendlyName = "AdminSectionImpl_Display_Controller", order=4, group = "AdminSectionImpl_Section")
    protected String displayController;

    @Column(name = "USE_DEFAULT_HANDLER", nullable=false)
    @AdminPresentation(friendlyName = "AdminSectionImpl_Use_Default_Handler", order=5, group = "AdminSectionImpl_Section")
    protected Boolean useDefaultHandler = Boolean.TRUE;

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

    @Override
    public String getDisplayController() {
        return displayController;
    }

    @Override
    public void setDisplayController(String displayController) {
        this.displayController = displayController;
    }

    @Override
    public Boolean getUseDefaultHandler() {
        return useDefaultHandler;
    }

    @Override
    public void setUseDefaultHandler(Boolean useDefaultHandler) {
        this.useDefaultHandler = useDefaultHandler;
    }
}
