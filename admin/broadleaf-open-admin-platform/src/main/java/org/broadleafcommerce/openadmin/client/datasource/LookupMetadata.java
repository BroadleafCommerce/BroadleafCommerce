/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client.datasource;

import com.smartgwt.client.data.DataSource;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;

/**
 * @author Jeff Fischer
 */
public class LookupMetadata {

    private String parentDataSourceName;
    private String targetDynamicFormDisplayId;
    private ForeignKey lookupForeignKey;
    private String friendlyName;
    private SupportedFieldType fieldType;
    private DataSource defaultDataSource;
    private String[] customCriteria;
    private Boolean useServerSideInspectionCache;

    public ForeignKey getLookupForeignKey() {
        return lookupForeignKey;
    }

    public void setLookupForeignKey(ForeignKey lookupForeignKey) {
        this.lookupForeignKey = lookupForeignKey;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getParentDataSourceName() {
        return parentDataSourceName;
    }

    public void setParentDataSourceName(String parentDataSourceName) {
        this.parentDataSourceName = parentDataSourceName;
    }

    public String getTargetDynamicFormDisplayId() {
        return targetDynamicFormDisplayId;
    }

    public void setTargetDynamicFormDisplayId(String targetDynamicFormDisplayId) {
        this.targetDynamicFormDisplayId = targetDynamicFormDisplayId;
    }

    public SupportedFieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(SupportedFieldType fieldType) {
        this.fieldType = fieldType;
    }

    public DataSource getDefaultDataSource() {
        return defaultDataSource;
    }

    public void setDefaultDataSource(DataSource defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
    }

    public String[] getCustomCriteria() {
        return customCriteria;
    }

    public void setCustomCriteria(String[] customCriteria) {
        this.customCriteria = customCriteria;
    }

    public Boolean getUseServerSideInspectionCache() {
        return useServerSideInspectionCache;
    }

    public void setUseServerSideInspectionCache(Boolean useServerSideInspectionCache) {
        this.useServerSideInspectionCache = useServerSideInspectionCache;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LookupMetadata)) return false;

        LookupMetadata that = (LookupMetadata) o;

        if (fieldType != that.fieldType) return false;
        if (friendlyName != null ? !friendlyName.equals(that.friendlyName) : that.friendlyName != null) return false;
        if (lookupForeignKey != null ? !lookupForeignKey.equals(that.lookupForeignKey) : that.lookupForeignKey != null)
            return false;
        if (parentDataSourceName != null ? !parentDataSourceName.equals(that.parentDataSourceName) : that.parentDataSourceName != null)
            return false;
        if (targetDynamicFormDisplayId != null ? !targetDynamicFormDisplayId.equals(that.targetDynamicFormDisplayId) : that.targetDynamicFormDisplayId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = parentDataSourceName != null ? parentDataSourceName.hashCode() : 0;
        result = 31 * result + (targetDynamicFormDisplayId != null ? targetDynamicFormDisplayId.hashCode() : 0);
        result = 31 * result + (lookupForeignKey != null ? lookupForeignKey.hashCode() : 0);
        result = 31 * result + (friendlyName != null ? friendlyName.hashCode() : 0);
        result = 31 * result + (fieldType != null ? fieldType.hashCode() : 0);
        return result;
    }
}
