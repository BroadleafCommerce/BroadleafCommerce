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
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
import com.smartgwt.client.widgets.grid.events.EditCompleteEvent;
import com.smartgwt.client.widgets.grid.events.EditCompleteHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.presenter.entity.AbstractSubPresentable;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureDisplay;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class SimpleMapStructurePresenter extends AbstractSubPresentable {

	protected Map<String, Object> initialValues = new LinkedHashMap<String, Object>(10);
	protected String[] gridFields;
    protected HandlerRegistration dataArrivedHandlerRegistration;
    protected HandlerRegistration editCompletedHandlerRegistration;
    protected HandlerRegistration selectionChangedHandlerRegistration;
    protected HandlerRegistration removeClickedHandlerRegistration;
    protected HandlerRegistration addClickedHandlerRegistration;
	
	public SimpleMapStructurePresenter(GridStructureDisplay display, String[] availableToTypes, Map<String, Object> initialValues) {
		super(display, availableToTypes);
        display.getGrid().setCanEdit(true);
        if (initialValues != null) {
		    this.initialValues.putAll(initialValues);
        }
	}

    public SimpleMapStructurePresenter(GridStructureDisplay display, Map<String, Object> initialValues) {
		this(display, null, initialValues);
	}

    public void setDataSource(ListGridDataSource dataSource, String[] gridFields, Boolean[] editable) {
		display.getGrid().setDataSource(dataSource);
		dataSource.setAssociatedGrid(display.getGrid());
		dataSource.setupGridFields(gridFields, editable);
		this.gridFields = gridFields;
	}
	
	public void bind() {
		dataArrivedHandlerRegistration = display.getGrid().addDataArrivedHandler(new DataArrivedHandler() {
			public void onDataArrived(DataArrivedEvent event) {
				display.getRemoveButton().disable();
			}
		});
		editCompletedHandlerRegistration = display.getGrid().addEditCompleteHandler(new EditCompleteHandler() {
			public void onEditComplete(EditCompleteEvent event) {
				display.getGrid().deselectAllRecords();
				setStartState();
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
		addClickedHandlerRegistration = display.getAddButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.isLeftButtonDown()) {
					DynamicEntityDataSource dataSource = (DynamicEntityDataSource) display.getGrid().getDataSource();
					if (initialValues == null) {
						initialValues = new LinkedHashMap<String, Object>(10);
					}
					initialValues.put("symbolicId", dataSource.getCompatibleModule(dataSource.getPersistencePerspective().getOperationTypes().getAddType()).getLinkedValue());
					String[] type = associatedRecord.getAttributeAsStringArray("_type");
					if (type == null) {
						type = new String[] {((DynamicEntityDataSource) display.getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname()};
					}
					initialValues.put("_type", type);
					if (initialValues != null) {
						display.getGrid().startEditingNew(initialValues);
					} else {
						display.getGrid().startEditingNew();
					}
				}
			}
		});
	}

    public HandlerRegistration getAddClickedHandlerRegistration() {
        return addClickedHandlerRegistration;
    }

    public HandlerRegistration getDataArrivedHandlerRegistration() {
        return dataArrivedHandlerRegistration;
    }

    public HandlerRegistration getEditCompletedHandlerRegistration() {
        return editCompletedHandlerRegistration;
    }

    public HandlerRegistration getRemoveClickedHandlerRegistration() {
        return removeClickedHandlerRegistration;
    }

    public HandlerRegistration getSelectionChangedHandlerRegistration() {
        return selectionChangedHandlerRegistration;
    }
}
