package org.springcommerce.util;

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
