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

package org.broadleafcommerce.admin.client.presenter.catalog.category;

import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordDropEvent;
import com.smartgwt.client.widgets.grid.events.RecordDropHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import org.broadleafcommerce.admin.client.view.catalog.category.CategoryDisplay;
import org.broadleafcommerce.openadmin.client.callback.SearchItemSelected;
import org.broadleafcommerce.openadmin.client.callback.SearchItemSelectedHandler;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetList;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.client.presenter.entity.AbstractSubPresentable;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntitySearchDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureDisplay;

/**
 * 
 * @author jfischer
 *
 */
public class AllChildCategoriesPresenter extends AbstractSubPresentable {

	protected EntitySearchDialog searchDialog;
	protected String searchDialogTitle;
	protected CategoryPresenter categoryPresenter;
	
	public AllChildCategoriesPresenter(CategoryPresenter categoryPresenter, GridStructureDisplay display, EntitySearchDialog searchDialog, String[] availableToTypes, String searchDialogTitle) {
		super("", display, availableToTypes);
		this.searchDialog = searchDialog;
		this.searchDialogTitle = searchDialogTitle;
		this.categoryPresenter = categoryPresenter;
	}
	
	public void setDataSource(ListGridDataSource dataSource, String[] gridFields, Boolean[] editable) {
		display.getGrid().setDataSource(dataSource);
		dataSource.setAssociatedGrid(display.getGrid());
		dataSource.setupGridFields(gridFields, editable);
	}
	
	public void bind() {
		display.getAddButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					searchDialog.search(searchDialogTitle, new SearchItemSelectedHandler() {
						public void onSearchItemSelected(SearchItemSelected event) {
							display.getGrid().addData(event.getRecord(), new DSCallback() {
								public void execute(DSResponse response, Object rawData, DSRequest request) {
									if (response.getStatus()!= RPCResponse.STATUS_FAILURE) {
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
		 * TODO add code to check if the AdornedTargetList has a sort field defined. If not,
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
				AdornedTargetList adornedTargetList = (AdornedTargetList) ((DynamicEntityDataSource) categoryPresenter.getPresenterSequenceSetupManager().getDataSource("allChildCategoriesDS")).getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST);
				record.setAttribute(adornedTargetList.getSortField(), newIndex);
				display.getGrid().updateData(record, new DSCallback() {
					public void execute(DSResponse response, Object rawData, DSRequest request) {
						categoryPresenter.reloadParentTreeNodeRecords(false);
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
							if (response.getStatus()!=RPCResponse.STATUS_FAILURE) {
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
