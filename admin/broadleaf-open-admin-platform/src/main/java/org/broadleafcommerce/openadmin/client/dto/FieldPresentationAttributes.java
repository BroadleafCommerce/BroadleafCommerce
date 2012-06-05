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

import com.google.gwt.user.client.rpc.IsSerializable;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class FieldPresentationAttributes implements IsSerializable, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private String friendlyName;
	private String securityLevel;
	private Integer order;
    private VisibilityEnum visibility;
	private String group;
	private Integer groupOrder;
    private Boolean groupCollapsed;
	private SupportedFieldType explicitFieldType;
	private Boolean largeEntry;
	private Boolean prominent;
	private String columnWidth;
	private String broadleafEnumeration;
	private Boolean readOnly;
	private Map<String, Map<String, String>> validationConfigurations = new HashMap<String, Map<String, String>>(5);
    private Boolean requiredOverride;
    private Boolean excluded;
    private String tooltip;
	
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
    
    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public VisibilityEnum getVisibility() {
        return visibility;
    }

    public void setVisibility(VisibilityEnum visibility) {
        this.visibility = visibility;
    }

    public FieldPresentationAttributes cloneFieldPresentationAttributes() {
        FieldPresentationAttributes attr = new FieldPresentationAttributes();
        attr.name = name;
        attr.friendlyName = friendlyName;
        attr.securityLevel = securityLevel;
        attr.order = order;
        attr.visibility = visibility;
        attr.group = group;
        attr.groupOrder = groupOrder;
        attr.groupCollapsed = groupCollapsed;
        attr.explicitFieldType = explicitFieldType;
        attr.largeEntry = largeEntry;
        attr.prominent = prominent;
        attr.columnWidth = columnWidth;
        attr.broadleafEnumeration = broadleafEnumeration;
        attr.readOnly = readOnly;
        attr.requiredOverride = requiredOverride;
        attr.excluded = excluded;
        attr.tooltip = tooltip;
        for (Map.Entry<String, Map<String, String>> entry : validationConfigurations.entrySet()) {
            Map<String, String> clone = new HashMap<String, String>(entry.getValue().size());
            for (Map.Entry<String, String> entry2 : entry.getValue().entrySet()) {
                clone.put(entry2.getKey(), entry2.getValue());
            }
            attr.validationConfigurations.put(entry.getKey(), clone);
        }

        return attr;
    }
}
