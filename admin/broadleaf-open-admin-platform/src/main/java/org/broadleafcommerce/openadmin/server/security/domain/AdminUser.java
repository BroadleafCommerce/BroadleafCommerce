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

import org.broadleafcommerce.common.sandbox.domain.SandBox;

import java.util.Map;
import java.util.Set;

/**
 * 
 * @author jfischer
 *
 */
public interface AdminUser extends AdminSecurityContext {
    public Long getId();
    public void setId(Long id);
    public String getName();
    public void setName(String name);
    public String getLogin();
    public void setLogin(String login);
    public String getPassword();
    public void setPassword(String password);
    public String getEmail();
    public void setEmail(String email);
    public Set<AdminRole> getAllRoles();
    public void setAllRoles(Set<AdminRole> allRoles);
    public String getUnencodedPassword();
    public void setUnencodedPassword(String unencodedPassword);

    /**
     * Stores the user's phone number.
     * @param phone
     */
    public void setPhoneNumber(String phone);

    /**
     * Returns the users phone number.
     * @return
     */
    public String getPhoneNumber();

    /**
     * Sets the users active status.   A user whose active status is set to false
     * will not be able to login.
     *
     * @param activeStatus
     */
    public void setActiveStatusFlag(Boolean activeStatus);

    /**
     * Returns the users active status.    A user whose active status is set to
     * false will not be able to login.
     *
     * @return
     */
    public Boolean getActiveStatusFlag();

    /**
     * The current sandbox associated with this user.
     * This is primarily intended to be used by the BLC-CMS workflow
     * processes.
     *
     * If null, the user is using their own SandBox.
     *
     * @return
     */
    public SandBox getOverrideSandBox();

    /**
     * Overrides the user's sandbox.    This could be used
     * to setup shared sandboxes.  Setting to null will
     * mean that the user is setup to use the sandbox associated
     * with their user.
     *
     * @param sandbox
     */
    public void setOverrideSandBox(SandBox sandbox);

    public Set<AdminPermission> getAllPermissions();
    public void setAllPermissions(Set<AdminPermission> allPermissions);
    //public AdminUser clone();

    /**
     * Returns a map representing just the key-value pairs inside the {@link #getAdditionalFields()} map.
     * 
     * @return the collapsed map
     */
    public Map<String, String> getFlatAdditionalFields();

    public Map<String, AdminUserAttribute> getAdditionalFields();

    public void setAdditionalFields(Map<String, AdminUserAttribute> additionalFields);
    
    /**
     * @return the id of the last sandbox this admin user used
     */
    public Long getLastUsedSandBoxId();

    /**
     * Sets the last used sandbox for this admin user
     * 
     * @param sandBoxId
     */
    public void setLastUsedSandBoxId(Long sandBoxId);

}
