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

package org.broadleafcommerce.openadmin.client.datasource.dynamic;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceTextField;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;

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

                fields[j] = idField;
            } else {
                fields[j] = legacyFields[j];
            }
            Record record = new Record();
            for (String attribute : fields[j].getAttributes()) {
                record.setAttribute(attribute, fields[j].getAttribute(attribute));
            }
            records[j] = record;
        }
        setFields(fields);
        setCacheData(records);
	}

}
