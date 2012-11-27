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

package org.broadleafcommerce.openadmin.client.presenter.structure;

import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.EditCompleteEvent;
import com.smartgwt.client.widgets.grid.events.EditCompleteHandler;
import com.smartgwt.client.widgets.grid.events.RecordDropEvent;
import com.smartgwt.client.widgets.grid.events.RecordDropHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import org.broadleafcommerce.openadmin.client.callback.SearchItemSelected;
import org.broadleafcommerce.openadmin.client.callback.SearchItemSelectedHandler;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetList;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.client.presenter.entity.AbstractSubPresentable;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntitySearchDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureDisplay;

/**
 * A simple presenter for list grids that is capable of displaying lists of
 * AdornedTargetList associated items, as well as simple ForeignKey associated items.
 *
 * @author jfischer
 *
 */
public class SimpleSearchListPresenter extends AbstractSubPresentable {

	protected EntitySearchDialog searchDialog;
	protected String searchDialogTitle;
    protected HandlerRegistration editCompletedHandlerRegistration;
    protected HandlerRegistration addClickedHandlerRegistration;
    protected HandlerRegistration recordDroppedHandlerRegistration;
    protected HandlerRegistration selectionChangedHandlerRegistration;
    protected HandlerRegistration removeClickedHandlerRegistration;
	
	public SimpleSearchListPresenter(String prefix, GridStructureDisplay display, EntitySearchDialog searchDialog, String[] availableToTypes, String searchDialogTitle) {
		super(prefix, display, availableToTypes);
		this.searchDialog = searchDialog;
		this.searchDialogTitle = searchDialogTitle;
	}

    public SimpleSearchListPresenter(String prefix, GridStructureDisplay display, EntitySearchDialog searchDialog, String searchDialogTitle) {
		this(prefix, display, searchDialog, null, searchDialogTitle);
	}

    public SimpleSearchListPresenter(SimpleSearchListPresenter template) {
        this(template.prefix, template.display, template.searchDialog, template.availableToTypes, template.searchDialogTitle);
        this.abstractDynamicDataSource = template.abstractDynamicDataSource;
        this.readOnly = template.readOnly;
    }
	
	public void setDataSource(ListGridDataSource dataSource, String[] gridFields, Boolean[] editable) {
		display.getGrid().setDataSource(dataSource);
		dataSource.setAssociatedGrid(display.getGrid());
		dataSource.setupGridFields(gridFields, editable);
	}
	
	public void bind() {
		editCompletedHandlerRegistration = display.getGrid().addEditCompleteHandler(new EditCompleteHandler() {
			public void onEditComplete(EditCompleteEvent event) {
				display.getGrid().deselectAllRecords();
				setStartState();
			}
		});
		addClickedHandlerRegistration = display.getAddButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					searchDialog.search(searchDialogTitle, new SearchItemSelectedHandler() {
						public void onSearchItemSelected(SearchItemSelected event) {
                            DynamicEntityDataSource ds = (DynamicEntityDataSource) display.getGrid().getDataSource();
                            ForeignKey foreignKey = (ForeignKey) ds.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);
                            if (foreignKey != null) {
                                event.getRecord().setAttribute(foreignKey.getManyToField(), abstractDynamicDataSource.getPrimaryKeyValue(associatedRecord));
                            }
							display.getGrid().addData(event.getRecord());
						}
					});
				}
			}
		});
		/*
		 * TODO add code to check if the AdornedTargetList has a sort field defined. If not,
		 * then disable the re-order functionality
		 */
		recordDroppedHandlerRegistration = display.getGrid().addRecordDropHandler(new RecordDropHandler() {
			public void onRecordDrop(RecordDropEvent event) {
				ListGridRecord record = event.getDropRecords()[0];
				int originalIndex = ((ListGrid) event.getSource()).getRecordIndex(record);
				int newIndex = event.getIndex();
				if (newIndex > originalIndex) {
					newIndex--;
				}
				AdornedTargetList adornedTargetList = (AdornedTargetList) ((DynamicEntityDataSource) display.getGrid().getDataSource()).getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.ADORNEDTARGETLIST);
				record.setAttribute(adornedTargetList.getSortField(), newIndex);
				display.getGrid().updateData(record);
			}
		});
		selectionChangedHandlerRegistration = display.getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			public void onSelectionChanged(SelectionEvent event) {
				if (event.getState()) {
					display.getRemoveButton().enable();
				} else {
					display.getRemoveButton().disable();
				}
			}
		});
		removeClickedHandlerRegistration = display.getRemoveButton().addClickHandler(new ClickHandler() {
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

    public HandlerRegistration getAddClickedHandlerRegistration() {
        return addClickedHandlerRegistration;
    }

    public HandlerRegistration getEditCompletedHandlerRegistration() {
        return editCompletedHandlerRegistration;
    }

    public HandlerRegistration getRecordDroppedHandlerRegistration() {
        return recordDroppedHandlerRegistration;
    }

    public HandlerRegistration getRemoveClickedHandlerRegistration() {
        return removeClickedHandlerRegistration;
    }

    public HandlerRegistration getSelectionChangedHandlerRegistration() {
        return selectionChangedHandlerRegistration;
    }

    public EntitySearchDialog getSearchDialog() {
        return searchDialog;
    }

    public void setSearchDialog(EntitySearchDialog searchDialog) {
        this.searchDialog = searchDialog;
    }

    public String getSearchDialogTitle() {
        return searchDialogTitle;
    }

    public void setSearchDialogTitle(String searchDialogTitle) {
        this.searchDialogTitle = searchDialogTitle;
    }
}
