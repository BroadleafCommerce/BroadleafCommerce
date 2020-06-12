/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.security.domain;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.AdminPresentationMap;
import org.broadleafcommerce.common.presentation.AdminPresentationOperationTypes;
import org.broadleafcommerce.common.presentation.ConfigurationItem;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxImpl;
import org.broadleafcommerce.openadmin.server.service.type.ContextType;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.CascadeType;
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
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 
 * @author jfischer
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ADMIN_USER")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blAdminSecurityVolatile")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_ADMINUSER),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.AUDITABLE_ONLY),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTI_PHASE_ADD),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.ARCHIVE_ONLY)
})
public class AdminUserImpl implements AdminUser, AdminMainEntity, AdminUserAdminPresentation {

    private static final Log LOG = LogFactory.getLog(AdminUserImpl.class);
    private static final long serialVersionUID = 1L;
    protected static final String LAST_USED_SANDBOX = "LAST_USED_SANDBOX";

    @Id
    @GeneratedValue(generator = "AdminUserId")
    @GenericGenerator(
        name="AdminUserId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="AdminUserImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.openadmin.server.security.domain.AdminUserImpl")
        }
    )
    @Column(name = "ADMIN_USER_ID")
    @AdminPresentation(friendlyName = "AdminUserImpl_Admin_User_ID", group = "AdminUserImpl_Primary_Key", visibility = VisibilityEnum.HIDDEN_ALL)
    private Long id;

    @Column(name = "NAME", nullable=false)
    @Index(name="ADMINUSER_NAME_INDEX", columnNames={"NAME"})
    @AdminPresentation(friendlyName = "AdminUserImpl_Admin_Name",
            group = GroupName.User, order = FieldOrder.NAME,
            prominent = true, gridOrder = 1000)
    protected String name;

    @Column(name = "LOGIN", nullable=false)
    @AdminPresentation(friendlyName = "AdminUserImpl_Admin_Login",
            group = GroupName.User, order = FieldOrder.LOGIN,
            prominent = true, gridOrder = 2000)
    protected String login;

    @Column(name = "PASSWORD")
    @AdminPresentation(
            friendlyName = "AdminUserImpl_Admin_Password",
            group = GroupName.User, order = FieldOrder.PASSWORD,
            fieldType = SupportedFieldType.PASSWORD,
            validationConfigurations = { @ValidationConfiguration(
                    validationImplementation = "org.broadleafcommerce.openadmin.server.service.persistence.validation.MatchesFieldValidator",
                    configurationItems = {
                            @ConfigurationItem(itemName = ConfigurationItem.ERROR_MESSAGE, itemValue = "passwordNotMatchError"),
                            @ConfigurationItem(itemName = "otherField", itemValue = "passwordConfirm")
                    }
                    )
            })
    protected String password;

    @Column(name = "EMAIL", nullable=false)
    @Index(name="ADMINPERM_EMAIL_INDEX", columnNames={"EMAIL"})
    @AdminPresentation(friendlyName = "AdminUserImpl_Admin_Email_Address",
            group = GroupName.User, order = FieldOrder.EMAIL)
    protected String email;

    @Column(name = "PHONE_NUMBER")
    @AdminPresentation(friendlyName = "AdminUserImpl_Phone_Number",
            group = GroupName.User, order = FieldOrder.PHONE_NUMBER)
    protected String phoneNumber;

    @Column(name = "ACTIVE_STATUS_FLAG")
    @AdminPresentation(friendlyName = "AdminUserImpl_Active_Status",
            group = GroupName.Miscellaneous, order = FieldOrder.ACTIVE_STATUS_FLAG,
            defaultValue = "true")
    protected Boolean activeStatusFlag;

    /** All roles that this user has */
    @ManyToMany(fetch = FetchType.LAZY, targetEntity = AdminRoleImpl.class)
    @JoinTable(name = "BLC_ADMIN_USER_ROLE_XREF", joinColumns = @JoinColumn(name = "ADMIN_USER_ID", referencedColumnName = "ADMIN_USER_ID"), inverseJoinColumns = @JoinColumn(name = "ADMIN_ROLE_ID", referencedColumnName = "ADMIN_ROLE_ID"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blAdminSecurityVolatile")
    @BatchSize(size = 50)
    @AdminPresentationCollection(friendlyName = "roleListTitle",
        group = GroupName.RolesAndPermissions, order = FieldOrder.ROLES,
        addType = AddMethodType.LOOKUP,
        manyToField = "allUsers",
        operationTypes = @AdminPresentationOperationTypes(removeType = OperationType.NONDESTRUCTIVEREMOVE))
    protected Set<AdminRole> allRoles = new HashSet<AdminRole>();

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = AdminPermissionImpl.class)
    @JoinTable(name = "BLC_ADMIN_USER_PERMISSION_XREF", joinColumns = @JoinColumn(name = "ADMIN_USER_ID", referencedColumnName = "ADMIN_USER_ID"), inverseJoinColumns = @JoinColumn(name = "ADMIN_PERMISSION_ID", referencedColumnName = "ADMIN_PERMISSION_ID"))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blAdminSecurityVolatile")
    @BatchSize(size = 50)
    @AdminPresentationCollection(friendlyName = "permissionListTitle",
        group = GroupName.RolesAndPermissions, order = FieldOrder.PERMISSIONS,
        addType = AddMethodType.LOOKUP,
        customCriteria = "includeFriendlyOnly",
        manyToField = "allUsers",
        operationTypes = @AdminPresentationOperationTypes(removeType = OperationType.NONDESTRUCTIVEREMOVE))
    protected Set<AdminPermission> allPermissions = new HashSet<AdminPermission>();

    @Transient
    protected String unencodedPassword;
    
    @Override
    public String getUnencodedPassword() {
        return unencodedPassword;
    }

    @ManyToOne(targetEntity = SandBoxImpl.class, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "BLC_ADMIN_USER_SANDBOX", joinColumns = @JoinColumn(name = "ADMIN_USER_ID", referencedColumnName = "ADMIN_USER_ID"), inverseJoinColumns = @JoinColumn(name = "SANDBOX_ID", referencedColumnName = "SANDBOX_ID"))
    @AdminPresentation(excluded = true)
    protected SandBox overrideSandBox;

    @OneToMany(mappedBy = "adminUser", targetEntity = AdminUserAttributeImpl.class, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="blAdminSecurityVolatile")
    @MapKey(name = "name")
    @BatchSize(size = 50)
    @AdminPresentationMap(friendlyName = "AdminUserImpl_additional_fields",
            group = GroupName.AdditionalFields,
            deleteEntityUponRemove = true, forceFreeFormKeys = true, keyPropertyFriendlyName = "AdminUserAttributeImpl_Key")
    protected Map<String, AdminUserAttribute> additionalFields = new HashMap<String, AdminUserAttribute>();

    @Override
    public void setUnencodedPassword(String unencodedPassword) {
        this.unencodedPassword = unencodedPassword;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
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
    public String getLogin() {
        return login;
    }

    @Override
    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public Boolean getActiveStatusFlag() {
        return activeStatusFlag;
    }

    @Override
    public void setActiveStatusFlag(Boolean activeStatusFlag) {
        this.activeStatusFlag = activeStatusFlag;
    }

    @Override
    public Set<AdminRole> getAllRoles() {
        return allRoles;
    }

    @Override
    public void setAllRoles(Set<AdminRole> allRoles) {
        this.allRoles = allRoles;
    }

    @Override
    public SandBox getOverrideSandBox() {
        return overrideSandBox;
    }

    @Override
    public void setOverrideSandBox(SandBox overrideSandBox) {
        this.overrideSandBox = overrideSandBox;
    }

    @Override
    public Set<AdminPermission> getAllPermissions() {
        return allPermissions;
    }

    @Override
    public void setAllPermissions(Set<AdminPermission> allPermissions) {
        this.allPermissions = allPermissions;
    }

    @Override
    public ContextType getContextType() {
        return ContextType.GLOBAL;
    }

    @Override
    public void setContextType(ContextType contextType) {
        //do nothing
    }

    @Override
    public String getContextKey() {
        return null;
    }

    @Override
    public void setContextKey(String contextKey) {
        //do nothing
    }
    
    @Override
    public Map<String, String> getFlatAdditionalFields() {
        Map<String, String> map = new HashMap<String, String>();
        for (Entry<String, AdminUserAttribute> entry : getAdditionalFields().entrySet()) {
            map.put(entry.getKey(), entry.getValue().getValue());
        }
        return map;
    }
    
    @Override
    public Map<String, AdminUserAttribute> getAdditionalFields() {
        return additionalFields;
    }

    @Override
    public void setAdditionalFields(Map<String, AdminUserAttribute> additionalFields) {
        this.additionalFields = additionalFields;
    }
    
    @Override
    public Long getLastUsedSandBoxId() {
        AdminUserAttribute attr = getAdditionalFields().get(LAST_USED_SANDBOX);
        if (attr != null && StringUtils.isNotBlank(attr.getValue())) {
            return Long.parseLong(attr.getValue());
        }
        return null;
    }
    
    @Override
    public void setLastUsedSandBoxId(Long sandBoxId) {
        AdminUserAttribute attr = getAdditionalFields().get(LAST_USED_SANDBOX);
        if (attr == null) {
            attr = new AdminUserAttributeImpl();
            attr.setName(LAST_USED_SANDBOX);
            attr.setAdminUser(this);
            getAdditionalFields().put(LAST_USED_SANDBOX, attr);
        }
        attr.setValue(String.valueOf(sandBoxId));
    }

    @Override
    public String getMainEntityName() {
        return getName();
    }

}
