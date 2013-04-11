/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client.datasource.dynamic;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceTextField;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;

import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * @author jfischer
 *
 */
public class FieldDataSourceWrapper extends DataSource {

    public FieldDataSourceWrapper(DataSource delegate) {
        setClientOnly(true);
        DataSourceField[] legacyFields = delegate.getFields();
        DataSourceField[] fields = new DataSourceField[legacyFields.length];
        Record[] records = new Record[fields.length];
        for (int j=0;j<legacyFields.length;j++){
            if (legacyFields[j].getAttribute("fieldType") != null && SupportedFieldType.ID == SupportedFieldType.valueOf(legacyFields[j].getAttribute("fieldType"))) {
                DataSourceTextField idField = new DataSourceTextField();
                idField.setName(legacyFields[j].getName());
                idField.setTitle(legacyFields[j].getTitle());
                idField.setAttribute("fieldType", legacyFields[j].getAttribute("fieldType"));
                idField.setAttribute("secondaryFieldType", legacyFields[j].getAttribute("secondaryFieldType"));
                idField.setAttribute("inheritedFromType", legacyFields[j].getAttribute("inheritedFromType"));
                idField.setAttribute("owningClassFriendlyName", legacyFields[j].getAttribute("owningClassFriendlyName"));
                idField.setHidden(legacyFields[j].getHidden());

                fields[j] = idField;
            } else {
                DataSourceField field = new DataSourceField();
                field.setName(legacyFields[j].getName());
                field.setTitle(legacyFields[j].getTitle());
                field.setAttribute("fieldType", legacyFields[j].getAttribute("fieldType"));
                field.setAttribute("secondaryFieldType", legacyFields[j].getAttribute("secondaryFieldType"));
                field.setAttribute("type", legacyFields[j].getAttribute("type"));
                field.setAttribute("inheritedFromType", legacyFields[j].getAttribute("inheritedFromType"));
                field.setAttribute("owningClassFriendlyName", legacyFields[j].getAttribute("owningClassFriendlyName"));
                field.setHidden(legacyFields[j].getHidden());
                field.setValueMap(legacyFields[j].getValueMap());
                fields[j] = field;
            }

            //make field names in the rule builder more specific to their owning entity
            String friendlyName = fields[j].getAttribute("owningClassFriendlyName");
            if (friendlyName == null || friendlyName.equals("")) {
                String fqcn = fields[j].getAttribute("inheritedFromType");
                if (fqcn != null) {
                    friendlyName = ((DynamicEntityDataSource) delegate).getPolymorphicEntities().get(fqcn);
                }
                if (friendlyName == null) {
                    fqcn = ((DynamicEntityDataSource) delegate).getDefaultNewEntityFullyQualifiedClassname();
                    friendlyName = ((DynamicEntityDataSource) delegate).getPolymorphicEntities().get(fqcn);
                }
            }
            if (!fields[j].getTitle().startsWith(friendlyName) && !friendlyName.contains("DTO")) {
                fields[j].setTitle(friendlyName + " - " + fields[j].getTitle());
            }

            Record record = new Record();
            for (String attribute : fields[j].getAttributes()) {
                record.setAttribute(attribute, fields[j].getAttribute(attribute));
            }
            records[j] = record;
        }
        Arrays.sort(fields, new Comparator<DataSourceField>() {
            @Override
            public int compare(DataSourceField o1, DataSourceField o2) {
                if (o1.getTitle() != null &&  o2.getTitle() != null) {
                    return o1.getTitle().compareTo(o2.getTitle());
                } else {
                    return o1.getName().compareTo(o2.getTitle());
                }
            }
        });
        setFields(fields);
        setCacheData(records);
    }

}
