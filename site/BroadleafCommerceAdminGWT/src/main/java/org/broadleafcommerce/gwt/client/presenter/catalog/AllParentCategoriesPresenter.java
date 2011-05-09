package org.broadleafcommerce.gwt.client.presenter.catalog;

import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.gwt.client.event.SearchItemSelectedEvent;
import org.broadleafcommerce.gwt.client.event.SearchItemSelectedEventHandler;
import org.broadleafcommerce.gwt.client.presenter.dynamic.entity.SubPresenter;
import org.broadleafcommerce.gwt.client.view.dynamic.dialog.EntitySearchDialog;
import org.broadleafcommerce.gwt.client.view.dynamic.grid.GridStructureDisplay;

import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;

public class AllParentCategoriesPresenter implements SubPresenter {

	protected GridStructureDisplay display;
	protected EntitySearchDialog searchDialog;
	protected String searchDialogTitle;
	protected DynamicEntityDataSource productDataSource;
	
	protected Record associatedRecord;
	
	public AllParentCategoriesPresenter(GridStructureDisplay display) {
		this.display = display;
	}
	
	public void setDataSource(ListGridDataSource dataSource, String[] gridFields, Boolean[] editable) {
		display.getGrid().setDataSource(dataSource);
		dataSource.setAssociatedGrid(display.getGrid());
		dataSource.setupGridFields(gridFields, editable);
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
		display.getAddButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					searchDialog.search(searchDialogTitle, new SearchItemSelectedEventHandler() {
						public void onSearchItemSelected(SearchItemSelectedEvent event) {
							display.getGrid().addData(event.getRecord()); 
						}
					});
				}
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
	}
}
