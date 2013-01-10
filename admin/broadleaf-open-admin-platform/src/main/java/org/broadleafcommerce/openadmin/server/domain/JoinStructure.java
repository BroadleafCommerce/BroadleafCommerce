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

public interface JoinStructure extends PersistencePerspectiveItem {

    public abstract String getName();

    public abstract void setName(String manyToField);

    public abstract String getLinkedObjectPath();

    public abstract void setLinkedObjectPath(String linkedPropertyPath);

    public abstract String getTargetObjectPath();

    public abstract void setTargetObjectPath(String targetObjectPath);

    public abstract String getJoinStructureEntityClassname();

    public abstract void setJoinStructureEntityClassname(
            String joinStructureEntityClassname);

    public abstract String getSortField();

    public abstract void setSortField(String sortField);

    public abstract Boolean getSortAscending();

    public abstract void setSortAscending(Boolean sortAscending);

    public abstract String getLinkedIdProperty();

    public abstract void setLinkedIdProperty(String linkedIdProperty);

    public abstract String getTargetIdProperty();

    public abstract void setTargetIdProperty(String targetIdProperty);

    public abstract Boolean getInverse();

    public abstract void setInverse(Boolean inverse);

}