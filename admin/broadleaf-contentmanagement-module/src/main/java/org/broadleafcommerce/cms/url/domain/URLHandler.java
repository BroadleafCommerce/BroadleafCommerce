package org.broadleafcommerce.cms.url.domain;

import java.io.Serializable;

import org.broadleafcommerce.cms.url.type.URLRedirectType;

public interface URLHandler extends  Serializable{

	public abstract Long getId();

	public abstract void setId(Long id);

	public abstract String getIncomingURL();

	public abstract void setIncomingURL(String incomingURL);

	public abstract String getNewURL();

	public abstract void setNewURL(String newURL);

	 public abstract URLRedirectType getUrlRedirectType();
	 public void setUrlRedirectType(URLRedirectType redirectType);
}