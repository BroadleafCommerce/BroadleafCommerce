package org.broadleafcommerce.gwt.client.presenter.dynamic.structure;

import java.util.Map;

import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.gwt.client.view.dynamic.dialog.ComplexValueMapStructureEntityEditDialog;
import org.broadleafcommerce.gwt.client.view.dynamic.grid.GridStructureDisplay;

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

public class MapStructurePresenter {
	
	protected GridStructureDisplay display;
	protected ComplexValueMapStructureEntityEditDialog entityEditDialog;
	protected String entityEditDialogTitle;
	protected Map<String, Object> initialValues;
	protected String[] gridFields;
	
	protected Record associatedRecord;
	
	public MapStructurePresenter(GridStructureDisplay display, ComplexValueMapStructureEntityEditDialog entityEditDialog, String entityEditDialogTitle, Map<String, Object> initialValues) {
		this.display = display;
		this.entityEditDialog = entityEditDialog;
		this.entityEditDialogTitle = entityEditDialogTitle;
		this.initialValues = initialValues;
	}
	
	public void setDataSource(ListGridDataSource dataSource, String[] gridFields, Boolean[] editable) {
		display.getGrid().setDataSource(dataSource);
		dataSource.setAssociatedGrid(display.getGrid());
		dataSource.setupGridFields(gridFields, editable);
		this.gridFields = gridFields;
	}
	
	public void setStartState() {
		display.getAddButton().enable();
		display.getGrid().enable();
		display.getRemoveButton().disable();
	}
	
	public void enable() {
		display.getAddButton().enable();
		display.getGrid().enable();
		display.getRemoveButton().enable();
	}
	
	public void disable() {
		display.getAddButton().disable();
		display.getGrid().disable();
		display.getRemoveButton().disable();
	}
	
	public void load(Record associatedRecord, final DSCallback cb) {
		this.associatedRecord = associatedRecord;
		String id = associatedRecord.getAttribute("id");
		((PresentationLayerAssociatedDataSource) display.getGrid().getDataSource()).loadAssociatedGridBasedOnRelationship(id, new DSCallback() {
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				if (response.getErrors().isEmpty()) {
					setStartState();
					if (cb != null) {
						cb.execute(response, rawData, request);
					}
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
							if (response.getErrors().isEmpty()) {
								display.getRemoveButton().disable();
							}
						}
					});
				}
			}
		});
		display.getAddButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					DynamicEntityDataSource dataSource = (DynamicEntityDataSource) display.getGrid().getDataSource();
					initialValues.put("symbolicId", dataSource.getCompatibleModule(dataSource.getPersistencePerspective().getOperationTypes().getAddType()).getLinkedValue());
					String[] type = associatedRecord.getAttributeAsStringArray("type");
					if (type == null) {
						type = new String[] {((DynamicEntityDataSource) display.getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname()};
					}
					initialValues.put("type", type);
					entityEditDialog.editNewRecord(entityEditDialogTitle, (DynamicEntityDataSource) display.getGrid().getDataSource(), initialValues, null, null, gridFields);
				}
			}
		});
	}

}
