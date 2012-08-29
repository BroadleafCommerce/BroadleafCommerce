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
import org.broadleafcommerce.openadmin.client.dto.visitor.MetadataVisitor;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 
 * @author jfischer
 *
 */
public abstract class FieldMetadata implements IsSerializable, Serializable {
	
	private static final long serialVersionUID = 1L;

	private String inheritedFromType;
	private String[] availableToTypes;
    private Boolean excluded;
    private String friendlyName;
    private String securityLevel;
    private Integer order;

	public String[] getAvailableToTypes() {
		return availableToTypes;
	}

	public void setAvailableToTypes(String[] availableToTypes) {
		Arrays.sort(availableToTypes);
		this.availableToTypes = availableToTypes;
	}

	public String getInheritedFromType() {
		return inheritedFromType;
	}

	public void setInheritedFromType(String inheritedFromType) {
		this.inheritedFromType = inheritedFromType;
	}

    public Boolean getExcluded() {
        return excluded;
    }

    public void setExcluded(Boolean excluded) {
        this.excluded = excluded;
    }

    protected FieldMetadata populate(FieldMetadata metadata) {
        metadata.inheritedFromType = inheritedFromType;
        if (availableToTypes != null) {
            metadata.availableToTypes = new String[availableToTypes.length];
            System.arraycopy(availableToTypes, 0, metadata.availableToTypes, 0, availableToTypes.length);
        }
        metadata.excluded = excluded;
        metadata.friendlyName = friendlyName;
        metadata.securityLevel = securityLevel;
        metadata.order = order;

        return metadata;
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

    public abstract FieldMetadata cloneFieldMetadata();

    public abstract void accept(MetadataVisitor visitor);
}
