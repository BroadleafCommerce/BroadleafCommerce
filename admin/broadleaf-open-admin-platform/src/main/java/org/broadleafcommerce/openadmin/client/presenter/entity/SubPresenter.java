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

package org.broadleafcommerce.openadmin.client.presenter.entity;

import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.openadmin.client.dto.ClassTree;
import org.broadleafcommerce.openadmin.client.view.dynamic.SubItemDisplay;

import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;

import java.util.Arrays;

/**
 * 
 * @author jfischer
 *
 */
public class SubPresenter extends DynamicFormPresenter implements SubPresentable {

	protected SubItemDisplay display;
	protected HandlerRegistration selectionChangedHandlerRegistration;
	protected HandlerRegistration removeButtonHandlerRegistration;
	
	protected Record associatedRecord;
	protected AbstractDynamicDataSource abstractDynamicDataSource;
	
	protected Boolean disabled = false;

    protected Boolean showDisabledState = false;
    protected Boolean canEdit = false;
    protected Boolean showId = false;
    protected String[] availableToTypes;

    public SubPresenter(SubItemDisplay display) {
        this(display, null, false, false, false);
    }

    public SubPresenter(SubItemDisplay display, String[] availableToTypes) {
		this(display, availableToTypes, false, false, false);
	}

    public SubPresenter(SubItemDisplay display, Boolean showDisabledState, Boolean canEdit, Boolean showId) {
		this(display, null, showDisabledState, canEdit, showId);
	}

	public SubPresenter(SubItemDisplay display, String[] availableToTypes, Boolean showDisabledState, Boolean canEdit, Boolean showId) {
		super(display);
        this.showDisabledState = showDisabledState;
        this.canEdit = canEdit;
        this.showId = showId;
		this.display = display;
        this.availableToTypes = availableToTypes;
	}

    public void setDataSource(ListGridDataSource dataSource, String[] gridFields, Boolean[] editable) {
		display.getGrid().setDataSource(dataSource);
		dataSource.setAssociatedGrid(display.getGrid());
		dataSource.setupGridFields(gridFields, editable);
		display.getFormOnlyDisplay().buildFields(dataSource, true, false, false, null);
	}
	
	@Override
	public void setStartState() {
		if (!disabled) {
			super.setStartState();
			display.getAddButton().enable();
			display.getGrid().enable();
			display.getRemoveButton().disable();
		}
	}
	
	@Override
	public void enable() {
		disabled = false;
		super.enable();
		display.getAddButton().enable();
		display.getGrid().enable();
		display.getRemoveButton().enable();
		display.getToolbar().enable();
	}
	
	@Override
	public void disable() {
		disabled = true;
		super.disable();
		display.getAddButton().disable();
		display.getGrid().disable();
		display.getRemoveButton().disable();
		display.getToolbar().disable();
	}
	
	public void setReadOnly(Boolean readOnly) {
		if (readOnly) {
			disable();
			display.getGrid().enable();
		} else {
			enable();
		}
	}

    @Override
    public boolean load(Record associatedRecord, AbstractDynamicDataSource associatedDataSource) {
        return load(associatedRecord, associatedDataSource, null);
    }

    @Override
    public boolean load(Record associatedRecord, AbstractDynamicDataSource abstractDynamicDataSource, final DSCallback cb) {
		this.associatedRecord = associatedRecord;
		this.abstractDynamicDataSource = abstractDynamicDataSource;
        ClassTree classTree = abstractDynamicDataSource.getPolymorphicEntityTree();
        String[] types = associatedRecord.getAttributeAsStringArray("_type");
        boolean shouldLoad = availableToTypes == null || types == null;
        if (types != null && types.length > 0) {
            if (availableToTypes != null) {
                if (Arrays.binarySearch(availableToTypes, types[0]) >= 0) {
                    shouldLoad = true;
                } else {
                    ClassTree myTypeResult = classTree.find(types[0]);
                    if (myTypeResult != null) {
                        for (String availableType : availableToTypes) {
                            ClassTree availableTypeResult = classTree.find(availableType);
                            if (availableTypeResult.getLeft() < myTypeResult.getLeft() && availableTypeResult.getRight() > myTypeResult.getRight()) {
                                shouldLoad = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        //display.setVisible(shouldLoad);

        if (shouldLoad) {
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

        return shouldLoad;
	}
	
	@Override
	public void bind() {
		super.bind();
		selectionChangedHandlerRegistration = display.getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
			public void onSelectionChanged(SelectionEvent event) {
				if (event.getState()) {
					display.getRemoveButton().enable();
					((DynamicEntityDataSource) display.getGrid().getDataSource()).resetPermanentFieldVisibilityBasedOnType(event.getSelectedRecord().getAttributeAsStringArray("_type"));
					display.getFormOnlyDisplay().buildFields(display.getGrid().getDataSource(),showDisabledState, canEdit, showId, event.getRecord());
					display.getFormOnlyDisplay().getForm().editRecord(event.getRecord());
					display.getFormOnlyDisplay().getForm().enable();
				} else {
					display.getRemoveButton().disable();
				}
			}
		});
		removeButtonHandlerRegistration = display.getRemoveButton().addClickHandler(new ClickHandler() {
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
	
	public HandlerRegistration getSelectionChangedHandlerRegistration() {
	    return selectionChangedHandlerRegistration;
	}
	
	public HandlerRegistration getRemoveButtonHandlerRegistration() {
	    return removeButtonHandlerRegistration;
	}
	
}
