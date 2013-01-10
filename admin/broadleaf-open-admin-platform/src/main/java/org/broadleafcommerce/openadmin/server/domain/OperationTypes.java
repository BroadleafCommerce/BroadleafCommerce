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

package org.broadleafcommerce.openadmin.server.domain;

import java.io.Serializable;

import org.broadleafcommerce.openadmin.client.dto.OperationType;

public interface OperationTypes extends Serializable {

    public abstract OperationType getRemoveType();

    public abstract void setRemoveType(OperationType removeType);

    public abstract OperationType getAddType();

    public abstract void setAddType(OperationType addType);

    public abstract OperationType getUpdateType();

    public abstract void setUpdateType(OperationType updateType);

    public abstract OperationType getFetchType();

    public abstract void setFetchType(OperationType fetchTyper);

    public abstract OperationType getInspectType();

    public abstract void setInspectType(OperationType inspectType);

    /**
     * @return the id
     */
    public abstract Long getId();

    /**
     * @param id the id to set
     */
    public abstract void setId(Long id);

}