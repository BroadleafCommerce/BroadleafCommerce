package org.broadleafcommerce.gwt.admin.client.presenter.user;

import org.broadleafcommerce.gwt.admin.client.AdminModule;
import org.broadleafcommerce.gwt.admin.client.view.user.UserRoleDisplay;
import org.broadleafcommerce.gwt.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.gwt.client.event.SearchItemSelectedEvent;
import org.broadleafcommerce.gwt.client.event.SearchItemSelectedEventHandler;
import org.broadleafcommerce.gwt.client.presenter.entity.SubPresentable;
import org.broadleafcommerce.gwt.client.view.dynamic.dialog.EntitySearchDialog;

import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;

public class UserRolePresenter implements SubPresentable {

	protected UserRoleDisplay display;
	
	protected Record associatedRecord;
	protected AbstractDynamicDataSource abstractDynamicDataSource;
	protected Boolean disabled = false;
	protected EntitySearchDialog searchDialog;
	
	public UserRolePresenter(UserRoleDisplay display, EntitySearchDialog searchDialog) {
		this.display = display;
		this.searchDialog = searchDialog;
	}
	
	public void setDataSource(ListGridDataSource dataSource, String[] gridFields, Boolean[] editable) {
		display.getGrid().setDataSource(dataSource);
		dataSource.setAssociatedGrid(display.getGrid());
		dataSource.setupGridFields(gridFields, editable);
	}
	
	public void setExpansionDataSource(ListGridDataSource dataSource, String[] gridFields, Boolean[] editable) {
		display.getExpansionGrid().setDataSource(dataSource);
		dataSource.setAssociatedGrid(display.getExpansionGrid());
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
		display.getAddButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					searchDialog.search(AdminModule.ADMINMESSAGES.userRolesTitle(), new SearchItemSelectedEventHandler() {
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
					((DynamicEntityDataSource) display.getGrid().getDataSource()).resetPermanentFieldVisibilityBasedOnType(event.getSelectedRecord().getAttributeAsStringArray("_type"));
				} else {
					display.getRemoveButton().disable();
				}
			}
		});
		display.getExpansionGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			public void onSelectionChanged(SelectionEvent event) {
				if (event.getState()) {
					((DynamicEntityDataSource) display.getExpansionGrid().getDataSource()).resetPermanentFieldVisibilityBasedOnType(event.getSelectedRecord().getAttributeAsStringArray("_type"));
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
	}
}
