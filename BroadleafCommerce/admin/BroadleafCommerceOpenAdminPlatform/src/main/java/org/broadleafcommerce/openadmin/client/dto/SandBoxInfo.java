package org.broadleafcommerce.openadmin.client.dto;

import java.io.Serializable;

public class SandBoxInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected String sandBox;
	protected boolean commitImmediately = true;
	
	public String getSandBox() {
		return sandBox;
	}
	
	public void setSandBox(String sandBox) {
		this.sandBox = sandBox;
	}
	
	public boolean isCommitImmediately() {
		return commitImmediately;
	}
	
	public void setCommitImmediately(boolean commitImmediately) {
		this.commitImmediately = commitImmediately;
	}
	
}
