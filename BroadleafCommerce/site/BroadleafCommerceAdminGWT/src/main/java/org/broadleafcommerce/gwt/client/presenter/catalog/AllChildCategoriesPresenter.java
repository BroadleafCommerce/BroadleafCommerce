package org.broadleafcommerce.gwt.client.presenter.catalog;

import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.gwt.client.datasource.relations.JoinStructure;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspectiveItemType;
import org.broadleafcommerce.gwt.client.event.SearchItemSelectedEvent;
import org.broadleafcommerce.gwt.client.event.SearchItemSelectedEventHandler;
import org.broadleafcommerce.gwt.client.presenter.dynamic.entity.SubPresenter;
import org.broadleafcommerce.gwt.client.view.catalog.CategoryDisplay;
import org.broadleafcommerce.gwt.client.view.dynamic.dialog.EntitySearchDialog;
import org.broadleafcommerce.gwt.client.view.dynamic.grid.GridStructureDisplay;

import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordDropEvent;
import com.smartgwt.client.widgets.grid.events.RecordDropHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;

public class AllChildCategoriesPresenter implements SubPresenter {

	protected GridStructureDisplay display;
	protected EntitySearchDialog searchDialog;
	protected String searchDialogTitle;
	protected CategoryPresenter categoryPresenter;
	
	protected Record associatedRecord;
	
	public AllChildCategoriesPresenter(CategoryPresenter categoryPresenter, GridStructureDisplay display, EntitySearchDialog searchDialog, String searchDialogTitle) {
		this.display = display;
		this.searchDialog = searchDialog;
		this.searchDialogTitle = searchDialogTitle;
		this.categoryPresenter = categoryPresenter;
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
							display.getGrid().addData(event.getRecord(), new DSCallback() {
								public void execute(DSResponse response, Object rawData, DSRequest request) {
									if (response.getErrors().isEmpty()) {
										categoryPresenter.reloadParentTreeNodeRecords(true);
									}
								}
							}); 
						}
					});
				}
			}
		});
		/*
		 * TODO add code to check if the JoinStructure has a sort field defined. If not,
		 * then disable the re-order functionality
		 */
		display.getGrid().addRecordDropHandler(new RecordDropHandler() {
			public void onRecordDrop(RecordDropEvent event) {
				ListGridRecord record = event.getDropRecords()[0];
				int originalIndex = ((ListGrid) event.getSource()).getRecordIndex(record);
				int newIndex = event.getIndex();
				if (newIndex > originalIndex) {
					newIndex--;
				}
				JoinStructure joinStructure = (JoinStructure) ((DynamicEntityDataSource) display.getGrid().getDataSource()).getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.JOINSTRUCTURE);
				record.setAttribute(joinStructure.getSortField(), newIndex);
				display.getGrid().updateData(record, new DSCallback() {
					public void execute(DSResponse response, Object rawData, DSRequest request) {
						if (response.getErrors().isEmpty()) {
							categoryPresenter.reloadParentTreeNodeRecords(false);
						}
					}
				});
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
								//display.getRemoveButton().disable();
								categoryPresenter.reloadParentTreeNodeRecords(true);
								((CategoryDisplay) categoryPresenter.getDisplay()).getRemoveOrphanedButton().disable();
								((CategoryDisplay) categoryPresenter.getDisplay()).getInsertOrphanButton().disable();
							}
						}
					});
				}
			}
		});
	}
}
