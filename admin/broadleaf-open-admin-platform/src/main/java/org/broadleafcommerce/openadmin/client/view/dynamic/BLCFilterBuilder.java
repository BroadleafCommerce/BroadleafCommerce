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

package org.broadleafcommerce.openadmin.client.view.dynamic;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.types.OperatorId;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.form.fields.DateItem;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.FieldDataSourceWrapper;

import java.util.HashMap;
import java.util.Map;

public class BLCFilterBuilder extends FilterBuilder {
    public BLCFilterBuilder() {
        super();
    }

    @Override
    public String getEditorType(DataSourceField fieldName, OperatorId operatorId) {
        if (FieldType.DATE == fieldName.getType() || FieldType.DATETIME == fieldName.getType()) {
            return DateItem.class.getName();
        }
        return super.getEditorType(fieldName, operatorId);
    }

    @Override
    public void setDataSource(DataSource legacy) {
        DataSource dataSource = new FieldDataSourceWrapper(legacy);
        Map<FieldType, OperatorId[]> map = new HashMap<FieldType, OperatorId[]>();

        for (FieldType fieldType : new FieldType[] { FieldType.TEXT, FieldType.ANY,
                FieldType.BINARY, FieldType.IMAGE,
                FieldType.IMAGEFILE, FieldType.MODIFIER,
                FieldType.MODIFIERTIMESTAMP, FieldType.SEQUENCE, FieldType.CUSTOM }) {
            // we have to save the values, then reapply, since the operators
            // are cleared on write.
            map.put(fieldType, dataSource.getTypeOperators(fieldType));
        }

        dataSource.setTypeOperators(FieldType.BOOLEAN, getBasicBooleanOperators());
        dataSource.setTypeOperators(FieldType.DATE, getBasicDateOperators());
        dataSource.setTypeOperators(FieldType.DATETIME, getBasicDateOperators());
        dataSource.setTypeOperators(FieldType.ENUM, getBasicEnumerationOperators());
        dataSource.setTypeOperators(FieldType.FLOAT, getBasicNumericOperators());
        dataSource.setTypeOperators(FieldType.INTEGER, getBasicNumericOperators());
        dataSource.setTypeOperators(FieldType.PASSWORD, getBasicTextOperators());
        dataSource.setTypeOperators(FieldType.TIME, getBasicDateOperators());

        for (FieldType fieldType : new FieldType[] { FieldType.TEXT, FieldType.ANY,
                FieldType.BINARY, FieldType.IMAGE,
                FieldType.IMAGEFILE, FieldType.MODIFIER,
                FieldType.MODIFIERTIMESTAMP, FieldType.SEQUENCE, FieldType.CUSTOM }) {
            // we have to reapply the values, since the operators are
            // cleared on write above. mabye due to bug in smartgwt
            dataSource.setTypeOperators(fieldType, map.get(fieldType));
        }

        super.setDataSource(dataSource);
        //super.setFieldDataSource(dataSource);
    }

    protected OperatorId[] getBasicBooleanOperators() {
        return new OperatorId[]{OperatorId.EQUALS, OperatorId.NOT_EQUAL, OperatorId.NOT_NULL, OperatorId.EQUALS_FIELD, OperatorId.NOT_EQUAL_FIELD};
    }

    protected OperatorId[] getBasicDateOperators() {
        return new OperatorId[]{OperatorId.EQUALS, OperatorId.GREATER_OR_EQUAL, OperatorId.GREATER_THAN, OperatorId.NOT_EQUAL, OperatorId.LESS_OR_EQUAL, OperatorId.LESS_THAN, OperatorId.NOT_NULL, OperatorId.EQUALS_FIELD, OperatorId.GREATER_OR_EQUAL_FIELD, OperatorId.GREATER_THAN_FIELD, OperatorId.LESS_OR_EQUAL_FIELD, OperatorId.LESS_THAN_FIELD, OperatorId.NOT_EQUAL_FIELD, OperatorId.BETWEEN, OperatorId.BETWEEN_INCLUSIVE};
    }

    protected OperatorId[] getBasicNumericOperators() {
        return new OperatorId[]{OperatorId.EQUALS, OperatorId.GREATER_OR_EQUAL, OperatorId.GREATER_THAN, OperatorId.NOT_EQUAL, OperatorId.LESS_OR_EQUAL, OperatorId.LESS_THAN, OperatorId.NOT_NULL, OperatorId.EQUALS_FIELD, OperatorId.GREATER_OR_EQUAL_FIELD, OperatorId.GREATER_THAN_FIELD, OperatorId.LESS_OR_EQUAL_FIELD, OperatorId.LESS_THAN_FIELD, OperatorId.NOT_EQUAL_FIELD, OperatorId.IN_SET, OperatorId.NOT_IN_SET, OperatorId.BETWEEN, OperatorId.BETWEEN_INCLUSIVE};
    }

    protected OperatorId[] getBasicTextOperators() {
        return new OperatorId[]{OperatorId.CONTAINS, OperatorId.NOT_CONTAINS, OperatorId.STARTS_WITH, OperatorId.ENDS_WITH, OperatorId.NOT_STARTS_WITH, OperatorId.NOT_ENDS_WITH, OperatorId.EQUALS, OperatorId.NOT_EQUAL, OperatorId.NOT_NULL, OperatorId.EQUALS_FIELD, OperatorId.NOT_EQUAL_FIELD};
    }

    protected OperatorId[] getBasicEnumerationOperators() {
        return new OperatorId[]{OperatorId.EQUALS, OperatorId.NOT_EQUAL, OperatorId.NOT_NULL, OperatorId.EQUALS_FIELD, OperatorId.NOT_EQUAL_FIELD};
    }
}
