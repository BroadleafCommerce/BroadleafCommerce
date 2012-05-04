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

package org.broadleafcommerce.openadmin.server.security.domain;

import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.openadmin.server.security.service.type.PermissionType;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author jfischer
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ADMIN_PERMISSION")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@AdminPresentationClass(friendlyName = "baseAdminPermission")
public class AdminPermissionImpl implements AdminPermission {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "AdminPermissionId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "AdminPermissionId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "AdminPermissionImpl", allocationSize = 50)
    @Column(name = "ADMIN_PERMISSION_ID")
    @AdminPresentation(friendlyName="AdminPermissionImpl_Admin_Permission_ID", group="Primary Key", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @Column(name = "NAME", nullable=false)
    @Index(name="ADMINPERM_NAME_INDEX", columnNames={"NAME"})
    @AdminPresentation(friendlyName="AdminPermissionImpl_Name", order=1, group="Permission", prominent=true)
    protected String name;

    @Column(name = "PERMISSION_TYPE", nullable=false)
    @Index(name="ADMINPERM_TYPE_INDEX", columnNames={"PERMISSION_TYPE"})
    @AdminPresentation(friendlyName="AdminPermissionImpl_Permission_Type", order=3, group="Permission", fieldType= SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration="org.broadleafcommerce.openadmin.server.security.service.type.PermissionType")
    protected String type;

    @Column(name = "DESCRIPTION", nullable=false)
    @AdminPresentation(friendlyName="AdminPermissionImpl_Description", order=2, group="Permission", prominent=true)
    protected String description;
    
    @ManyToMany(fetch = FetchType.LAZY, targetEntity = AdminRoleImpl.class)
    @JoinTable(name = "BLC_ADMIN_ROLE_PERMISSION_XREF", joinColumns = @JoinColumn(name = "ADMIN_PERMISSION_ID", referencedColumnName = "ADMIN_PERMISSION_ID"), inverseJoinColumns = @JoinColumn(name = "ADMIN_ROLE_ID", referencedColumnName = "ADMIN_ROLE_ID"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected Set<AdminRole> allRoles= new HashSet<AdminRole>();

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = AdminUserImpl.class)
    @JoinTable(name = "BLC_ADMIN_USER_PERMISSION_XREF", joinColumns = @JoinColumn(name = "ADMIN_PERMISSION_ID", referencedColumnName = "ADMIN_PERMISSION_ID"), inverseJoinColumns = @JoinColumn(name = "ADMIN_USER_ID", referencedColumnName = "ADMIN_USER_ID"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected Set<AdminUser> allUsers= new HashSet<AdminUser>();

    @OneToMany(mappedBy = "adminPermission", targetEntity = AdminPermissionQualifiedEntityImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected List<AdminPermissionQualifiedEntity> qualifiedEntities;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public Set<AdminRole> getAllRoles() {
		return allRoles;
	}

	public void setAllRoles(Set<AdminRole> allRoles) {
		this.allRoles = allRoles;
	}

    public PermissionType getType() {
        return PermissionType.getInstance(type);
    }

    public void setType(PermissionType type) {
        if (type != null) {
    		this.type = type.getType();
    	}
    }

    public List<AdminPermissionQualifiedEntity> getQualifiedEntities() {
        return qualifiedEntities;
    }

    public void setQualifiedEntities(List<AdminPermissionQualifiedEntity> qualifiedEntities) {
        this.qualifiedEntities = qualifiedEntities;
    }

    public Set<AdminUser> getAllUsers() {
        return allUsers;
    }

    public void setAllUsers(Set<AdminUser> allUsers) {
        this.allUsers = allUsers;
    }
}
