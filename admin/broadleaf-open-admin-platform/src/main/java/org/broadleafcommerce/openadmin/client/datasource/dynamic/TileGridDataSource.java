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

import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.widgets.tile.TileGrid;
import com.smartgwt.client.widgets.viewer.DetailViewerField;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author krosenberg
 *
 */
public class TileGridDataSource extends PresentationLayerAssociatedDataSource {

    /**
     * @param name
     * @param persistencePerspective
     * @param service
     * @param modules
     */
    public TileGridDataSource(String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules) {
        super(name, persistencePerspective, service, modules);
    }

    public void setupGridFields() {
        setupGridFields(new String[]{});
    }

    public void setupGridFields(final String[] fieldNames) {
        if (fieldNames.length > 0) {
            resetProminenceOnly(fieldNames);
        }

        String[] sortedFieldNames = new String[fieldNames.length];
        for (int j=0;j<fieldNames.length;j++) {
            sortedFieldNames[j] = fieldNames[j];
        }
        Arrays.sort(sortedFieldNames);

        DataSourceField[] fields = getFields();
        int j = 0;
        List<DataSourceField> prominentFields = new ArrayList<DataSourceField>();
        for (DataSourceField field : fields) {
            if (field.getAttributeAsBoolean("prominent")) {
                prominentFields.add(field);
            }
        }
        int availableSlots = fieldNames.length==0?4:fieldNames.length;
        DetailViewerField[] gridFields = new DetailViewerField[availableSlots];
        for (DataSourceField field : prominentFields) {
            gridFields[j] = new DetailViewerField(field.getName(), field.getTitle());
            if (field.getType().equals(FieldType.IMAGE)) {
                gridFields[j].setImageURLPrefix("");
            }
            j++;
            availableSlots--;
        }
        for (DataSourceField field : fields) {
            if (!prominentFields.contains(field) && availableSlots > 0) {
                gridFields[j] = new DetailViewerField(field.getName(), field.getTitle());
                if (field.getType().equals(FieldType.IMAGE)) {
                    gridFields[j].setImageURLPrefix("");
                }
                availableSlots--;
            }
        }
        ((TileGrid) getAssociatedGrid()).setFields(gridFields);
    }
}
