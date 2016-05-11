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
package org.broadleafcommerce.openadmin.security;

import org.broadleafcommerce.common.web.SandBoxContext;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.broadleafcommerce.openadmin.server.service.SandBoxMode;

/**
 * @author Jeff Fischer
 */
public class AdminSandBoxContext extends SandBoxContext {

    protected AdminUser adminUser;
    protected SandBoxMode sandBoxMode;
    protected String sandBoxName;
    protected boolean resetData = false;
    protected boolean isReplay = false;
    protected boolean rebuildSandBox = false;

    public AdminUser getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(AdminUser adminUser) {
        this.adminUser = adminUser;
    }

    public SandBoxMode getSandBoxMode() {
        return sandBoxMode;
    }

    public void setSandBoxMode(SandBoxMode sandBoxMode) {
        this.sandBoxMode = sandBoxMode;
    }

    public String getSandBoxName() {
        return sandBoxName;
    }

    public void setSandBoxName(String sandBoxName) {
        this.sandBoxName = sandBoxName;
    }

    public boolean isReplay() {
        return isReplay;
    }

    public void setReplay(boolean replay) {
        isReplay = replay;
    }

    public boolean isRebuildSandBox() {
        return rebuildSandBox;
    }

    public void setRebuildSandBox(boolean rebuildSandBox) {
        this.rebuildSandBox = rebuildSandBox;
    }

    public boolean isResetData() {
        return resetData;
    }

    public void setResetData(boolean resetData) {
        this.resetData = resetData;
    }

    public SandBoxContext clone() {
        AdminSandBoxContext myContext = new AdminSandBoxContext();
        myContext.setResetData(isResetData());
        myContext.setAdminUser(getAdminUser());
        myContext.setSandBoxId(getSandBoxId());
        myContext.setPreviewMode(getPreviewMode());
        myContext.setSandBoxMode(getSandBoxMode());
        myContext.setSandBoxName(getSandBoxName());
        myContext.setReplay(isReplay());
        myContext.setRebuildSandBox(isRebuildSandBox());


        return myContext;
    }
}
