package org.broadleafcommerce.util;

import java.io.Serializable;

import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CreateCategory implements Serializable {
    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    @Transient
    private final Log logger = LogFactory.getLog(getClass());
    private String name;
    private String parentId;
    private String urlKey;
    private String url;

	public String getUrlKey() {
		return urlKey;
	}
	public void setUrlKey(String urlKey) {
		this.urlKey = urlKey;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

}
