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

import java.util.HashMap;
import java.util.Map;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.types.OperatorId;
import com.smartgwt.client.widgets.form.FilterBuilder;

public class BLCFilterBuilder extends FilterBuilder {
    public BLCFilterBuilder() {
        super();
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        OperatorId[] operatorsForNumbers = new OperatorId[] {
                OperatorId.GREATER_THAN, OperatorId.GREATER_OR_EQUAL,
                OperatorId.GREATER_THAN_FIELD, OperatorId.BETWEEN,
                OperatorId.BETWEEN_INCLUSIVE, OperatorId.LESS_THAN,
                OperatorId.LESS_OR_EQUAL, OperatorId.LESS_THAN_FIELD,
                OperatorId.EQUALS, OperatorId.EQUALS_FIELD,
                OperatorId.NOT_EQUAL_FIELD, OperatorId.NOT_EQUAL };
        Map<FieldType, OperatorId[]> map = new HashMap<FieldType, OperatorId[]>();

        for (FieldType fieldType : new FieldType[] { FieldType.ANY,
                FieldType.BINARY, FieldType.BOOLEAN, FieldType.CREATOR,
                FieldType.CREATORTIMESTAMP, FieldType.ENUM, FieldType.IMAGE,
                FieldType.IMAGEFILE, FieldType.ANY, FieldType.MODIFIER,
                FieldType.MODIFIERTIMESTAMP, FieldType.TEXT,
                FieldType.SEQUENCE, FieldType.PASSWORD, FieldType.CUSTOM }) {
            // we have to save the values, then reapply, since the operators
            // are cleared on write.
            map.put(fieldType, dataSource.getTypeOperators(fieldType));

        }
        // removing operators that don't make sense for numbers. BLC-380
        dataSource.setTypeOperators(FieldType.FLOAT, operatorsForNumbers);
        dataSource.setTypeOperators(FieldType.INTEGER, operatorsForNumbers);
        dataSource.setTypeOperators(FieldType.TIME, operatorsForNumbers);
        dataSource.setTypeOperators(FieldType.DATE, operatorsForNumbers);
        dataSource.setTypeOperators(FieldType.DATETIME, operatorsForNumbers);

        for (FieldType fieldType : new FieldType[] { FieldType.ANY,
                FieldType.BINARY, FieldType.BOOLEAN, FieldType.CREATOR,
                FieldType.CREATORTIMESTAMP, FieldType.ENUM, FieldType.IMAGE,
                FieldType.IMAGEFILE, FieldType.ANY, FieldType.MODIFIER,
                FieldType.MODIFIERTIMESTAMP, FieldType.TEXT,
                FieldType.SEQUENCE, FieldType.PASSWORD, FieldType.CUSTOM }) {
            // we have to reapply the values, since the operators are
            // cleared on write above. mabye due to bug in smartgwt
            dataSource.setTypeOperators(fieldType, map.get(fieldType));
        }
        super.setDataSource(dataSource);
    }
}
