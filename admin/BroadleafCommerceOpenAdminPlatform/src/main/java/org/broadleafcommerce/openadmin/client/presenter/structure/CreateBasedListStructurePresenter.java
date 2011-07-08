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
package org.broadleafcommerce.openadmin.client.presenter.structure;

import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.client.presenter.entity.SubPresentable;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureDisplay;

import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
import com.smartgwt.client.widgets.grid.events.EditCompleteEvent;
import com.smartgwt.client.widgets.grid.events.EditCompleteHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;

/**
 * 
 * @author jfischer
 *
 */
public class CreateBasedListStructurePresenter implements SubPresentable {

	protected GridStructureDisplay display;
	protected String editDialogTitle;
	protected Map<String, Object> initialValues;
	
	protected Record associatedRecord;
	protected AbstractDynamicDataSource abstractDynamicDataSource;
	protected Boolean disabled = false;
	
	public CreateBasedListStructurePresenter(GridStructureDisplay display, String editDialogTitle) {
		this(display, editDialogTitle, new HashMap<String, Object>());
	}
	
	public CreateBasedListStructurePresenter(GridStructureDisplay display, String editDialogTitle, Map<String, Object> initialValues) {
		this.display = display;
		this.editDialogTitle = editDialogTitle;
		this.initialValues = initialValues;
	}
	
	public void setDataSource(ListGridDataSource dataSource, String[] gridFields, Boolean[] editable) {
		display.getGrid().setDataSource(dataSource);
		dataSource.setAssociatedGrid(display.getGrid());
		dataSource.setupGridFields(gridFields, editable);
	}
	
	public void setStartState() {
		if (!disabled) {
			display.getAddButton().enable();
			display.getGrid().enable();
			display.getRemoveButton().disable();
		}
	}
	
	public void enable() {
		disabled = false;
		display.getAddButton().enable();
		display.getGrid().enable();
		display.getRemoveButton().enable();
		display.getToolBar().enable();
	}
	
	public void disable() {
		disabled = true;
		display.getAddButton().disable();
		display.getGrid().disable();
		display.getRemoveButton().disable();
		display.getToolBar().disable();
	}
	
	public void setReadOnly(Boolean readOnly) {
		if (readOnly) {
			disable();
			display.getGrid().enable();
		} else {
			enable();
		}
	}
	
	public void load(Record associatedRecord, AbstractDynamicDataSource abstractDynamicDataSource, final DSCallback cb) {
		this.associatedRecord = associatedRecord;
		this.abstractDynamicDataSource = abstractDynamicDataSource;
		String id = abstractDynamicDataSource.getPrimaryKeyValue(associatedRecord);
		((PresentationLayerAssociatedDataSource) display.getGrid().getDataSource()).loadAssociatedGridBasedOnRelationship(id, new DSCallback() {
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				setStartState();
				if (cb != null) {
					cb.execute(response, rawData, request);
				}
			}
		});
	}
	
	public void bind() {
		display.getGrid().addDataArrivedHandler(new DataArrivedHandler() {
			public void onDataArrived(DataArrivedEvent event) {
				display.getRemoveButton().disable();
			}
		});
		display.getGrid().addEditCompleteHandler(new EditCompleteHandler() {
			public void onEditComplete(EditCompleteEvent event) {
				display.getGrid().deselectAllRecords();
				setStartState();
			}
		});
		display.getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			public void onSelectionChanged(SelectionEvent event) {
				if (event.getState()) {
					display.getRemoveButton().enable();
				} else {
					display.getRemoveButton().disable();
				}
			}
		});
		display.getRemoveButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					display.getGrid().removeData(display.getGrid().getSelectedRecord(), new DSCallback() {
						public void execute(DSResponse response, Object rawData, DSRequest request) {
							display.getRemoveButton().disable();
						}
					});
				}
			}
		});
		
		display.getAddButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					DynamicEntityDataSource ds = (DynamicEntityDataSource) display.getGrid().getDataSource();
					ForeignKey foreignKey = (ForeignKey) ds.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);
					initialValues.put(foreignKey.getManyToField(), abstractDynamicDataSource.getPrimaryKeyValue(associatedRecord));
					String[] type = new String[] {((DynamicEntityDataSource) display.getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname()};
					initialValues.put("_type", type);
					BLCMain.ENTITY_ADD.editNewRecord(editDialogTitle, ds, initialValues, null, null, null, null);
				}
			}
		});
	}
}
