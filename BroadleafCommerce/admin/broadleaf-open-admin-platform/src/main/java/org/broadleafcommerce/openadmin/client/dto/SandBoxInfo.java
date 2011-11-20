package org.broadleafcommerce.openadmin.client.dto;

import java.io.Serializable;

public class SandBoxInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected Long sandBox;
	protected boolean commitImmediately = true;
    protected Long siteId;
    protected String name;

    public Long getSandBox() {
        return sandBox;
    }

    public void setSandBox(Long sandBox) {
        this.sandBox = sandBox;
    }

    public boolean isCommitImmediately() {
		return commitImmediately;
	}
	
	public void setCommitImmediately(boolean commitImmediately) {
		this.commitImmediately = commitImmediately;
	}

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
