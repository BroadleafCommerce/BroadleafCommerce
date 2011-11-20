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
}
