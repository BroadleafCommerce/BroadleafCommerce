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

public interface MapStructure extends PersistencePerspectiveItem {

    public abstract String getKeyClassName();

    public abstract void setKeyClassName(String keyClassName);

    public abstract String getValueClassName();

    public abstract void setValueClassName(String valueClassName);

    public abstract String getMapProperty();

    public abstract void setMapProperty(String mapProperty);

    public abstract String getKeyPropertyName();

    public abstract void setKeyPropertyName(String keyPropertyName);

    public abstract String getKeyPropertyFriendlyName();

    public abstract void setKeyPropertyFriendlyName(
            String keyPropertyFriendlyName);

    public abstract Boolean getDeleteValueEntity();

    public abstract void setDeleteValueEntity(Boolean deleteValueEntity);

}