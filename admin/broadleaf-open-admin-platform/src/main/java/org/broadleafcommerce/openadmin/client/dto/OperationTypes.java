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

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author jfischer
 *
 */
public class OperationTypes implements IsSerializable, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private OperationType fetchType = OperationType.ENTITY;
    private OperationType removeType = OperationType.ENTITY;
    private OperationType addType = OperationType.ENTITY;
    private OperationType updateType = OperationType.ENTITY;
    private OperationType inspectType = OperationType.ENTITY;
    
    public OperationTypes() {
        //do nothing
    }
    
    public OperationTypes(OperationType fetchType, OperationType removeType, OperationType addType, OperationType updateType, OperationType inspectType) {
        this.removeType = removeType;
        this.addType = addType;
        this.updateType = updateType;
        this.fetchType = fetchType;
        this.inspectType = inspectType;
    }
    
    public OperationType getRemoveType() {
        return removeType;
    }
    
    public void setRemoveType(OperationType removeType) {
        this.removeType = removeType;
    }
    
    public OperationType getAddType() {
        return addType;
    }
    
    public void setAddType(OperationType addType) {
        this.addType = addType;
    }
    
    public OperationType getUpdateType() {
        return updateType;
    }
    
    public void setUpdateType(OperationType updateType) {
        this.updateType = updateType;
    }

    public OperationType getFetchType() {
        return fetchType;
    }

    public void setFetchType(OperationType fetchTyper) {
        this.fetchType = fetchTyper;
    }

    public OperationType getInspectType() {
        return inspectType;
    }

    public void setInspectType(OperationType inspectType) {
        this.inspectType = inspectType;
    }
    
}
