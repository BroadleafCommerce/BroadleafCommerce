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

import org.broadleafcommerce.openadmin.client.dto.ForeignKeyRestrictionType;

public interface ForeignKey extends PersistencePerspectiveItem {

    public abstract String getManyToField();

    public abstract void setManyToField(String manyToField);

    public abstract String getForeignKeyClass();

    public abstract void setForeignKeyClass(String foreignKeyClass);

    public abstract String getCurrentValue();

    public abstract void setCurrentValue(String currentValue);

    public abstract String getDataSourceName();

    public abstract void setDataSourceName(String dataSourceName);

    public abstract ForeignKeyRestrictionType getRestrictionType();

    public abstract void setRestrictionType(ForeignKeyRestrictionType restrictionType);

    public abstract String getDisplayValueProperty();

    public abstract void setDisplayValueProperty(String displayValueProperty);

    public Long getId();

    public void setId(Long id);

}