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
@Table(name = "BLC_ADMIN_MODULE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@AdminPresentationClass(friendlyName = "AdminModuleImpl_baseAdminModule")
public class AdminModuleImpl implements AdminModule {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "AdminModuleId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "AdminModuleId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "AdminModuleImpl", allocationSize = 50)
    @Column(name = "ADMIN_MODULE_ID")
    @AdminPresentation(friendlyName = "AdminModuleImpl_Admin_Module_ID", group = "AdminModuleImpl_Primary_Key", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @Column(name = "NAME", nullable=false)
    @Index(name="ADMINMODULE_NAME_INDEX", columnNames={"NAME"})
    @AdminPresentation(friendlyName = "AdminModuleImpl_Name", order=1, group = "AdminModuleImpl_Module", prominent=true)
    protected String name;

    @Column(name = "MODULE_KEY", nullable=false)
    @AdminPresentation(friendlyName = "AdminModuleImpl_Module_Key", order=2, group = "AdminModuleImpl_Module", prominent=true)
    protected String moduleKey;

    @Column(name = "ICON", nullable=true)
    @AdminPresentation(friendlyName = "AdminModuleImpl_Icon", order=3, group = "AdminModuleImpl_Module", prominent=true)
    protected String icon;

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = AdminSectionImpl.class)
    @JoinTable(name = "BLC_ADMIN_MODULE_SECTION_XREF", joinColumns = @JoinColumn(name = "ADMIN_MODULE_ID", referencedColumnName = "ADMIN_MODULE_ID"), inverseJoinColumns = @JoinColumn(name = "ADMIN_SECTION_ID", referencedColumnName = "ADMIN_SECTION_ID"))
    @BatchSize(size = 50)
    protected List<AdminSection> sections = new ArrayList<AdminSection>();

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
    public String getModuleKey() {
        return moduleKey;
    }

    @Override
    public void setModuleKey(String moduleKey) {
        this.moduleKey = moduleKey;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public List<AdminSection> getSections() {
        return sections;
    }

    @Override
    public void setSections(List<AdminSection> sections) {
        this.sections = sections;
    }
}
