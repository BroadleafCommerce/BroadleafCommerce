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
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxImpl;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.ConfigurationItem;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author jfischer
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ADMIN_USER")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@AdminPresentationClass(friendlyName = "baseAdminUser")
public class AdminUserImpl implements AdminUser {
	
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(generator = "AdminUserId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "AdminUserId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "AdminUserImpl", allocationSize = 50)
    @Column(name = "ADMIN_USER_ID")
    @AdminPresentation(friendlyName="AdminUserImpl_Admin_User_ID", group="Primary Key", visibility = VisibilityEnum.HIDDEN_ALL)
    private Long id;

    @Column(name = "NAME", nullable=false)
    @Index(name="ADMINUSER_NAME_INDEX", columnNames={"NAME"})
    @AdminPresentation(friendlyName="AdminUserImpl_Admin_Name", order=1, group="User", prominent=true)
    protected String name;

    @Column(name = "LOGIN", nullable=false)
    @AdminPresentation(friendlyName="AdminUserImpl_Admin_Login", order=2, group="User", prominent=true)
    protected String login;

    @Column(name = "PASSWORD", nullable=false)
    @AdminPresentation(
		friendlyName="Admin Password",
		order=3, 
		group="User", 
		fieldType= SupportedFieldType.PASSWORD,
		validationConfigurations={
			@ValidationConfiguration(
				validationImplementation="com.smartgwt.client.widgets.form.validator.MatchesFieldValidator",
				configurationItems={
                        @ConfigurationItem(itemName="errorMessageKey", itemValue="passwordNotMatchError"),
                        @ConfigurationItem(itemName="fieldType", itemValue="password")
                }
			)
		}
    )
    protected String password;

    @Column(name = "EMAIL", nullable=false)
    @Index(name="ADMINPERM_EMAIL_INDEX", columnNames={"EMAIL"})
    @AdminPresentation(friendlyName="AdminUserImpl_Admin_Email_Address", order=4, group="User")
    protected String email;

    @Column(name = "PHONE_NUMBER")
    @AdminPresentation(friendlyName="AdminUserImpl_Phone_Number", order=5, group="User")
    protected String phoneNumber;

    @Column(name = "ACTIVE_STATUS_FLAG")
    @AdminPresentation(friendlyName="AdminUserImpl_Active_Status", order=6, group="User")
    protected Boolean activeStatusFlag = Boolean.TRUE;

    /** All roles that this user has */
    @ManyToMany(fetch = FetchType.LAZY, targetEntity = AdminRoleImpl.class)
    @JoinTable(name = "BLC_ADMIN_USER_ROLE_XREF", joinColumns = @JoinColumn(name = "ADMIN_USER_ID", referencedColumnName = "ADMIN_USER_ID"), inverseJoinColumns = @JoinColumn(name = "ADMIN_ROLE_ID", referencedColumnName = "ADMIN_ROLE_ID"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected Set<AdminRole> allRoles = new HashSet<AdminRole>();

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = AdminPermissionImpl.class)
    @JoinTable(name = "BLC_ADMIN_USER_PERMISSION_XREF", joinColumns = @JoinColumn(name = "ADMIN_USER_ID", referencedColumnName = "ADMIN_USER_ID"), inverseJoinColumns = @JoinColumn(name = "ADMIN_PERMISSION_ID", referencedColumnName = "ADMIN_PERMISSION_ID"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @BatchSize(size = 50)
    protected Set<AdminPermission> allPermissions = new HashSet<AdminPermission>();

    @Transient
    protected String unencodedPassword;
    
    public String getUnencodedPassword() {
        return unencodedPassword;
    }

    @ManyToOne(targetEntity = SandBoxImpl.class, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "BLC_ADMIN_USER_SANDBOX", joinColumns = @JoinColumn(name = "ADMIN_USER_ID", referencedColumnName = "ADMIN_USER_ID"), inverseJoinColumns = @JoinColumn(name = "SANDBOX_ID", referencedColumnName = "SANDBOX_ID"))
    @AdminPresentation(excluded = true)
    protected SandBox overrideSandBox;

    public void setUnencodedPassword(String unencodedPassword) {
        this.unencodedPassword = unencodedPassword;
    }

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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getActiveStatusFlag() {
        return activeStatusFlag;
    }

    public void setActiveStatusFlag(Boolean activeStatusFlag) {
        this.activeStatusFlag = activeStatusFlag;
    }

    public Set<AdminRole> getAllRoles() {
        return allRoles;
    }

    public void setAllRoles(Set<AdminRole> allRoles) {
        this.allRoles = allRoles;
    }

    public SandBox getOverrideSandBox() {
        return overrideSandBox;
    }

    public void setOverrideSandBox(SandBox overrideSandBox) {
        this.overrideSandBox = overrideSandBox;
    }

    public Set<AdminPermission> getAllPermissions() {
        return allPermissions;
    }

    public void setAllPermissions(Set<AdminPermission> allPermissions) {
        this.allPermissions = allPermissions;
    }
}
