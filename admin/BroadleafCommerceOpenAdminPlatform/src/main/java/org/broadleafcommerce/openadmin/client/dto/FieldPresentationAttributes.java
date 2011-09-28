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
package org.broadleafcommerce.openadmin.client.dto;

import org.broadleafcommerce.openadmin.client.presentation.SupportedFieldType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class FieldPresentationAttributes implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String friendlyName;
	private String securityLevel;
	private Integer order;
	private Boolean hidden;
    private FormHiddenEnum formHidden;
	private String group;
	private Integer groupOrder;
    private Boolean groupCollapsed;
	private SupportedFieldType explicitFieldType;
	private Boolean largeEntry;
	private Boolean prominent;
	private String columnWidth;
	private String broadleafEnumeration;
	private Boolean readOnly;
	private Map<String, Map<String, String>> validationConfigurations = new HashMap<String, Map<String, String>>();
    private Boolean requiredOverride;
    private Boolean excluded;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getFriendlyName() {
		return friendlyName;
	}
	
	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}
	
	public String getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(String securityLevel) {
		this.securityLevel = securityLevel;
	}

	public Integer getOrder() {
		return order;
	}
	
	public void setOrder(Integer order) {
		this.order = order;
	}
	
	public Boolean isHidden() {
		return hidden;
	}
	
	public void setHidden(Boolean hidden) {
		this.hidden = hidden;
	}

	public SupportedFieldType getExplicitFieldType() {
		return explicitFieldType;
	}

	public void setExplicitFieldType(SupportedFieldType fieldType) {
		this.explicitFieldType = fieldType;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public Boolean isLargeEntry() {
		return largeEntry;
	}

	public void setLargeEntry(Boolean largeEntry) {
		this.largeEntry = largeEntry;
	}

	public Boolean isProminent() {
		return prominent;
	}

	public void setProminent(Boolean prominent) {
		this.prominent = prominent;
	}

	public String getColumnWidth() {
		return columnWidth;
	}

	public void setColumnWidth(String columnWidth) {
		this.columnWidth = columnWidth;
	}

	public String getBroadleafEnumeration() {
		return broadleafEnumeration;
	}

	public void setBroadleafEnumeration(String broadleafEnumeration) {
		this.broadleafEnumeration = broadleafEnumeration;
	}

	public Boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

	public Integer getGroupOrder() {
		return groupOrder;
	}

	public void setGroupOrder(Integer groupOrder) {
		this.groupOrder = groupOrder;
	}

	public Map<String, Map<String, String>> getValidationConfigurations() {
		return validationConfigurations;
	}

	public void setValidationConfigurations(Map<String, Map<String, String>> validationConfigurations) {
		this.validationConfigurations = validationConfigurations;
	}

    public Boolean getRequiredOverride() {
        return requiredOverride;
    }

    public void setRequiredOverride(Boolean requiredOverride) {
        this.requiredOverride = requiredOverride;
    }

    public Boolean getGroupCollapsed() {
        return groupCollapsed;
    }

    public void setGroupCollapsed(Boolean groupCollapsed) {
        this.groupCollapsed = groupCollapsed;
    }

    public Boolean getExcluded() {
        return excluded;
    }

    public void setExcluded(Boolean excluded) {
        this.excluded = excluded;
    }

    public FormHiddenEnum getFormHidden() {
        return formHidden;
    }

    public void setFormHidden(FormHiddenEnum formHidden) {
        this.formHidden = formHidden;
    }
}
