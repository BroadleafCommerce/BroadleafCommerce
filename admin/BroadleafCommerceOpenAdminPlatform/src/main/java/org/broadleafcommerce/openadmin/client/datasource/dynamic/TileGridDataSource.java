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
package org.broadleafcommerce.openadmin.client.datasource.dynamic;

import java.util.Arrays;

import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.widgets.tile.TileGrid;
import com.smartgwt.client.widgets.viewer.DetailViewerField;

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
	
	public void setupGridFields(final String[] fieldNames, final Boolean[] canEdit) {
		if (fieldNames.length > 0) {
			resetProminenceOnly(fieldNames);
		}
		
		String[] sortedFieldNames = new String[fieldNames.length];
		for (int j=0;j<fieldNames.length;j++) {
			sortedFieldNames[j] = fieldNames[j];
		}
		Arrays.sort(sortedFieldNames);
		
		DataSourceField[] fields = getFields();
		DetailViewerField[] gridFields = new DetailViewerField[fields.length];
		
        int j = 0;
        for (DataSourceField field : fields) {
    		gridFields[j] = new DetailViewerField(field.getName(), field.getTitle());
    		j++;
        }

        ((TileGrid) getAssociatedGrid()).setFields(gridFields);
        getAssociatedGrid().setHilites(hilites);
	}
	
}
