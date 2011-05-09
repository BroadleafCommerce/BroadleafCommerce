package org.broadleafcommerce.gwt.client.view.dynamic.dialog;

import java.util.LinkedHashMap;

import org.broadleafcommerce.gwt.client.datasource.relations.MapStructure;
import org.broadleafcommerce.gwt.client.view.dynamic.form.FormBuilder;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.form.DynamicForm;

public class MapStructureEntityEditDialog extends EntityEditDialog {
	
	protected MapStructure mapStructure;
	protected LinkedHashMap<String, String> mapKeys;
	
	public MapStructureEntityEditDialog() {
		this(null, null);
	}

	public MapStructureEntityEditDialog(MapStructure mapStructure, LinkedHashMap<String, String> mapKeys) {
		super();
		this.mapStructure = mapStructure;
		this.mapKeys = mapKeys;
		this.setHeight("300");
	}

	@Override
	protected void buildFields(DataSource dataSource, DynamicForm form) {
		FormBuilder.buildMapForm(dataSource, form, mapStructure, mapKeys, false);
	}
	
}
