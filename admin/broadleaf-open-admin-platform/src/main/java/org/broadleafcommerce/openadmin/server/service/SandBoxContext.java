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

package org.broadleafcommerce.openadmin.server.service;


import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;

public class SandBoxContext {
	
	private static final ThreadLocal<SandBoxContext> SANDBOXCONTEXT = new ThreadLocal<SandBoxContext>();
	
	public static SandBoxContext getSandBoxContext() {
		return SANDBOXCONTEXT.get();
	}
	
	public static void setSandBoxContext(SandBoxContext sandBoxContext) {
		SANDBOXCONTEXT.set(sandBoxContext);
	}
	
	protected AdminUser adminUser;
	protected Long sandBoxId;
	protected SandBoxMode sandBoxMode;
    protected String sandBoxName;
    protected boolean resetData = false;

    public AdminUser getAdminUser() {
        return adminUser;
    }

    public void setAdminUser(AdminUser adminUser) {
        this.adminUser = adminUser;
    }

    /**
	 * @return the sandBoxName
	 */
	public Long getSandBoxId() {
		return sandBoxId;
	}
	
	/**
	 * @param sandBoxId the sandBoxName to set
	 */
	public void setSandBoxId(Long sandBoxId) {
		this.sandBoxId = sandBoxId;
	}

	/**
	 * @return the sandBoxMode
	 */
	public SandBoxMode getSandBoxMode() {
		return sandBoxMode;
	}

	/**
	 * @param sandBoxMode the sandBoxMode to set
	 */
	public void setSandBoxMode(SandBoxMode sandBoxMode) {
		this.sandBoxMode = sandBoxMode;
	}

    public String getSandBoxName() {
        return sandBoxName;
    }

    public void setSandBoxName(String sandBoxName) {
        this.sandBoxName = sandBoxName;
    }

    public boolean isResetData() {
        return resetData;
    }

    public void setResetData(boolean resetData) {
        this.resetData = resetData;
    }

    public SandBoxContext clone() {
        SandBoxContext myContext = new SandBoxContext();
        myContext.setResetData(isResetData());
        myContext.setAdminUser(getAdminUser());
        myContext.setSandBoxId(getSandBoxId());
        myContext.setSandBoxMode(getSandBoxMode());
        myContext.setSandBoxName(getSandBoxName());

        return myContext;
    }
}
