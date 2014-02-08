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
