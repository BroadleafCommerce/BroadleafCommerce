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

package org.broadleafcommerce.openadmin.client.view.dynamic.dialog;

import java.util.LinkedHashMap;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.form.DynamicForm;
import org.broadleafcommerce.openadmin.client.dto.MapStructure;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.FormBuilder;

/**
 * 
 * @author jfischer
 *
 */
public class MapStructureEntityEditDialog extends EntityEditDialog {
    
    protected MapStructure mapStructure;
    protected LinkedHashMap<String, String> mapKeys;
    protected DataSource optionDataSource;
    protected String displayField;
    protected String valueField;
    
    public MapStructureEntityEditDialog() {
        super();
        this.setHeight("300");
    }

    public MapStructureEntityEditDialog(MapStructure mapStructure, LinkedHashMap<String, String> mapKeys) {
        super();
        this.mapStructure = mapStructure;
        this.mapKeys = mapKeys;
        this.setHeight("300");
    }

    public MapStructureEntityEditDialog(MapStructure mapStructure, DataSource optionDataSource, String displayField, String valueField) {
        super();
        this.mapStructure = mapStructure;
        this.optionDataSource = optionDataSource;
        this.displayField = displayField;
        this.valueField = valueField;
        this.setHeight("300");
    }

    @Override
    protected void buildFields(DataSource dataSource, DynamicForm form, Record record) {
        if (mapKeys != null) {
            FormBuilder.buildMapForm(dataSource, form, mapStructure, mapKeys, false, record);
        } else {
            FormBuilder.buildMapForm(dataSource, form, mapStructure, optionDataSource, displayField, valueField, false, record);
        }
    }
    
}
