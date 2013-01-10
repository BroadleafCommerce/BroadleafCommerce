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

package org.broadleafcommerce.openadmin.client.presenter.entity;

import com.smartgwt.client.data.DataSourceField;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
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
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.FetchDataEvent;
import com.smartgwt.client.widgets.events.FetchDataHandler;
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
    protected Boolean readOnly = false;
    protected String prefix;

    protected Boolean showDisabledState = false;
    protected Boolean canEdit = false;
    protected Boolean showId = false;
    protected String[] availableToTypes;

    /**
     * Create a new instance.
     *
     * @deprecated use the constructor that specifies the prefix value
     * @param display The display component that visually represents this SubPresenter. Usually an instance of SubItemView.
     */
    @Deprecated
    public SubPresenter(SubItemDisplay display) {
        this(null, display, null, false, false, false);
    }

    /**
     * Create a new instance.
     *
     * @deprecated use the constructor that specifies the prefix value
     * @param display The display component that visually represents this SubPresenter. Usually an instance of SubItemView.
     * @param availableToTypes Comma delimited list of polymorphic types that have access to this property.
     */
    @Deprecated
    public SubPresenter(SubItemDisplay display, String[] availableToTypes) {
        this(null, display, availableToTypes, false, false, false);
    }

    /**
     * Create a new instance.
     *
     * @deprecated use the constructor that specifies the prefix value
     * @param display The display component that visually represents this SubPresenter. Usually an instance of SubItemView.
     * @param showDisabledState Whether or not to dim the item when it is disabled.
     * @param canEdit Whether or not the SubPresenter instance can be edited.
     * @param showId Whether or not to show the id for a SubPresenter selected records in its form
     */
    @Deprecated
    public SubPresenter(SubItemDisplay display, Boolean showDisabledState, Boolean canEdit, Boolean showId) {
        this(null, display, null, showDisabledState, canEdit, showId);
    }

    /**
     * Create a new instance.
     *
     * @deprecated use the constructor that specifies the prefix value
     * @param display The display component that visually represents this SubPresenter. Usually an instance of SubItemView.
     * @param availableToTypes Comma delimited list of polymorphic types that have access to this property.
     * @param showDisabledState Whether or not to dim the item when it is disabled.
     * @param canEdit Whether or not the SubPresenter instance can be edited.
     * @param showId Whether or not to show the id for a SubPresenter selected records in its form
     */
    @Deprecated
    public SubPresenter(SubItemDisplay display, String[] availableToTypes, Boolean showDisabledState, Boolean canEdit, Boolean showId) {
        this(null, display, availableToTypes, showDisabledState, canEdit, showId);
    }

    /**
     * Create a new instance.
     *
     * @param prefix The list of "." delimited properties from the parent record that lead to this property. For example, if this SubPresenter referenced a property on Sku and the owning record was a Product, the prefix would likely be defaultSku. Can be null or an empty String for properties directly on the parent record.
     * @param display The display component that visually represents this SubPresenter. Usually an instance of SubItemView.
     */
    public SubPresenter(String prefix, SubItemDisplay display) {
        this(prefix, display, null, false, false, false);
    }

    /**
     * Create a new instance.
     *
     * @param prefix The list of "." delimited properties from the parent record that lead to this property. For example, if this SubPresenter referenced a property on Sku and the owning record was a Product, the prefix would likely be defaultSku. Can be null or an empty String for properties directly on the parent record.
     * @param display The display component that visually represents this SubPresenter. Usually an instance of SubItemView.
     * @param availableToTypes Comma delimited list of polymorphic types that have access to this property.
     */
    public SubPresenter(String prefix, SubItemDisplay display, String[] availableToTypes) {
        this(prefix, display, availableToTypes, false, false, false);
    }

    /**
     * Create a new instance.
     *
     * @param prefix The list of "." delimited properties from the parent record that lead to this property. For example, if this SubPresenter referenced a property on Sku and the owning record was a Product, the prefix would likely be defaultSku. Can be null or an empty String for properties directly on the parent record.
     * @param display The display component that visually represents this SubPresenter. Usually an instance of SubItemView.
     * @param showDisabledState Whether or not to dim the item when it is disabled.
     * @param canEdit Whether or not the SubPresenter instance can be edited.
     * @param showId Whether or not to show the id for a SubPresenter selected records in its form
     */
    public SubPresenter(String prefix, SubItemDisplay display, Boolean showDisabledState, Boolean canEdit, Boolean showId) {
        this(prefix, display, null, showDisabledState, canEdit, showId);
    }

    /**
     * Create a new instance.
     *
     * @param prefix The list of "." delimited properties from the parent record that lead to this property. For example, if this SubPresenter referenced a property on Sku and the owning record was a Product, the prefix would likely be defaultSku. Can be null or an empty String for properties directly on the parent record.
     * @param display The display component that visually represents this SubPresenter. Usually an instance of SubItemView.
     * @param availableToTypes Comma delimited list of polymorphic types that have access to this property.
     * @param showDisabledState Whether or not to dim the item when it is disabled.
     * @param canEdit Whether or not the SubPresenter instance can be edited.
     * @param showId Whether or not to show the id for a SubPresenter selected records in its form
     */
    public SubPresenter(String prefix, SubItemDisplay display, String[] availableToTypes, Boolean showDisabledState, Boolean canEdit, Boolean showId) {
        super(display);
        this.showDisabledState = showDisabledState;
        this.canEdit = canEdit;
        this.showId = showId;
        this.display = display;
        this.availableToTypes = availableToTypes;
        this.prefix = prefix==null?"":prefix;
    }

    @Override
    public Canvas getDisplay() {
        return (Canvas) display;
    }

    @Override
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
              display.getFormOnlyDisplay().getForm().clearValues();
            display.getFormOnlyDisplay().getForm().disable(); 
            display.getRemoveButton().disable();
        }
    }
    
    @Override
    public void enable() {
        disabled = false;
        super.enable();
        display.getAddButton().enable();
        display.getGrid().enable();
        
        display.getToolbar().enable();
        
    }
    
    @Override
    public void disable() {
        disabled = true;
        super.disable();
        display.getAddButton().disable();
        display.getGrid().disable();

        display.getToolbar().disable();
        
    }
    
    @Override
    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
        updatePresenterReadOnlyStatus();
    }
    
    protected void updatePresenterReadOnlyStatus() {
        if (readOnly) {
            display.getAddButton().disable();
            
            display.getToolbar().disable();
        } else {
            display.getAddButton().enable();
            
            display.getToolbar().enable();
        }
    }

    @Override
    public boolean load(Record associatedRecord, AbstractDynamicDataSource associatedDataSource) {
        return load(associatedRecord, associatedDataSource, null);
    }

    @Override
    public boolean load(final Record associatedRecord, AbstractDynamicDataSource abstractDynamicDataSource, final DSCallback cb) {
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
            String id = getRelationshipValue(associatedRecord, abstractDynamicDataSource);
            ((PresentationLayerAssociatedDataSource) display.getGrid().getDataSource()).loadAssociatedGridBasedOnRelationship(id, new DSCallback() {
                @Override
                public void execute(DSResponse response, Object rawData, DSRequest request) {
                    String locked = associatedRecord.getAttribute("__locked");
                    if (!(locked != null && locked.equals("true"))) {
                        setStartState();
                    }
                    
                    updatePresenterReadOnlyStatus();
                    
                    if (cb != null) {
                        cb.execute(response, rawData, request);
                    }
                }
            });
        }

        return shouldLoad;
    }

    public String getRelationshipValue(final Record associatedRecord, AbstractDynamicDataSource abstractDynamicDataSource) {
        if (prefix.equals("")) {
            return abstractDynamicDataSource.getPrimaryKeyValue(associatedRecord);
        } else {
            //we need to check all the parts of the prefix. For example, the prefix could include an @Embedded class like
            //defaultSku.dimension. In this case, we want the id from the defaultSku property, since the @Embedded does
            //not have an id property - nor should it.
            String[] prefixParts = prefix.split("\\.");
            for (int j=0;j<prefixParts.length;j++) {
                StringBuilder sb = new StringBuilder();
                for (int x=0;x<prefixParts.length-j;x++) {
                    sb.append(prefixParts[x]);
                    if (x < prefixParts.length-j-1) {
                        sb.append(".");
                    }
                }
                String tempPrefix = sb.toString();
                for (String fieldName : abstractDynamicDataSource.getFieldNames()) {
                    if (fieldName.startsWith(tempPrefix)) {
                        DataSourceField field = abstractDynamicDataSource.getField(fieldName);
                        if (SupportedFieldType.ID == SupportedFieldType.valueOf(field.getAttribute("fieldType"))) {
                            return abstractDynamicDataSource.stripDuplicateAllowSpecialCharacters(associatedRecord.getAttribute(fieldName));
                        }
                    }
                }
            }
        }
        throw new RuntimeException("Unable to establish a relationship value for the datasource: " + abstractDynamicDataSource.getDataURL());
    }
    
    @Override
    public void bind() {
        super.bind();
                display.getGrid().addFetchDataHandler(new FetchDataHandler() {
                    @Override
                    public void onFilterData(FetchDataEvent event) {
                        display.getFormOnlyDisplay().getForm().clearValues();  
                        display.getFormOnlyDisplay().getForm().disable();  
                        display.getRemoveButton().disable();
                    }
                });

        selectionChangedHandlerRegistration = display.getGrid().addSelectionChangedHandler(new SelectionChangedHandler() {
            @Override
            public void onSelectionChanged(SelectionEvent event) {
                if (event.getState()) {
                    display.getRemoveButton().enable();
                    ((DynamicEntityDataSource) display.getGrid().getDataSource()).resetPermanentFieldVisibilityBasedOnType(event.getSelectedRecord().getAttributeAsStringArray("_type"));
                    display.getFormOnlyDisplay().buildFields(display.getGrid().getDataSource(),showDisabledState, canEdit, showId, event.getRecord());
                    display.getFormOnlyDisplay().getForm().editRecord(event.getRecord());
                    display.getFormOnlyDisplay().getForm().enable();
                     
                      
                } else {
                    display.getFormOnlyDisplay().getForm().clearValues();  
                    display.getFormOnlyDisplay().getForm().disable();  
                    display.getRemoveButton().disable();
                }
                
                updatePresenterReadOnlyStatus();
            }
        });
        removeButtonHandlerRegistration = display.getRemoveButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    display.getGrid().removeData(display.getGrid().getSelectedRecord(), new DSCallback() {
                        @Override
                        public void execute(DSResponse response, Object rawData, DSRequest request) {
                            setStartState(); 
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
