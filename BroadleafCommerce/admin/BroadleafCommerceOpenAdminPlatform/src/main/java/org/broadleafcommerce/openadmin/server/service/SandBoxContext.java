package org.broadleafcommerce.openadmin.server.service;




public class SandBoxContext {
	
	private static final ThreadLocal<SandBoxContext> SANDBOXCONTEXT = new ThreadLocal<SandBoxContext>();
	
	public static SandBoxContext getSandBoxContext() {
		return SANDBOXCONTEXT.get();
	}
	
	public static void setSandBoxContext(SandBoxContext sandBoxContext) {
		SANDBOXCONTEXT.set(sandBoxContext);
	}
	
	protected String userName;
	protected String sandBoxName;
	protected SandBoxMode sandBoxMode;
	
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * @return the sandBoxName
	 */
	public String getSandBoxName() {
		return sandBoxName;
	}
	
	/**
	 * @param sandBoxName the sandBoxName to set
	 */
	public void setSandBoxName(String sandBoxName) {
		this.sandBoxName = sandBoxName;
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

}
