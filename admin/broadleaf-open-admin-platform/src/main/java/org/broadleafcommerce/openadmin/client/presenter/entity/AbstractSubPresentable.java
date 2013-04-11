/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client.presenter.entity;

import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Canvas;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.PresentationLayerAssociatedDataSource;
import org.broadleafcommerce.openadmin.client.dto.ClassTree;
import org.broadleafcommerce.openadmin.client.view.dynamic.grid.GridStructureDisplay;

import java.util.Arrays;

/**
 * @author jfischer
 */
public abstract class AbstractSubPresentable implements SubPresentable {

    protected Boolean disabled = false;
    protected GridStructureDisplay display;
    protected String[] availableToTypes;

    protected Record associatedRecord;
    protected AbstractDynamicDataSource abstractDynamicDataSource;
    protected boolean readOnly = false;
    protected String prefix;

    /**
     * Create a new instance.
     *
     * @deprecated use the constructor that specifies the prefix value
     * @param display The display component that visually represents this SubPresentable.
     * @param availableToTypes The display component that visually represents this SubPresentable.
     */
    @Deprecated
    public AbstractSubPresentable(GridStructureDisplay display, String[] availableToTypes) {
        this(null,display,availableToTypes);
    }

    /**
     * Create a new instance.
     *
     * @deprecated use the constructor that specifies the prefix value
     * @param display The display component that visually represents this SubPresentable.
     */
    @Deprecated
    public AbstractSubPresentable(GridStructureDisplay display) {
        this(null, display);
    }

    /**
     * Create new instance.
     *
     * @param prefix The list of "." delimited properties from the parent record that lead to this property. For example, if this SubPresenter referenced a property on Sku and the owning record was a Product, the prefix would likely be defaultSku. Can be null or an empty String for properties directly on the parent record.
     * @param display The display component that visually represents this SubPresentable.
     * @param availableToTypes The display component that visually represents this SubPresentable.
     */
    public AbstractSubPresentable(String prefix, GridStructureDisplay display, String[] availableToTypes) {
        this.display = display;
        this.availableToTypes = availableToTypes;
        this.prefix = prefix==null?"":prefix;
    }

    /**
     * Create new instance.
     *
     * @param prefix The list of "." delimited properties from the parent record that lead to this property. For example, if this SubPresenter referenced a property on Sku and the owning record was a Product, the prefix would likely be defaultSku. Can be null or an empty String for properties directly on the parent record.
     * @param display The display component that visually represents this SubPresentable.
     */
    public AbstractSubPresentable(String prefix, GridStructureDisplay display) {
        this(prefix, display, null);
    }

    @Override
    public void setStartState() {
        if (!disabled) {
            display.getAddButton().enable();
            display.getGrid().enable();
            display.getRemoveButton().disable();
        }
    }

    @Override
    public void enable() {
        disabled = false;
        display.getAddButton().enable();
        display.getGrid().enable();
        display.getRemoveButton().enable();
        display.getToolBar().enable();
    }

    @Override
    public void disable() {
        disabled = true;
        display.getAddButton().disable();
        display.getGrid().disable();
        display.getRemoveButton().disable();
        display.getToolBar().disable();
    }
    
    @Override
    public boolean load(Record associatedRecord, AbstractDynamicDataSource abstractDynamicDataSource) {
        return load(associatedRecord, abstractDynamicDataSource, null);
    }

    @Override
    public boolean load(final Record associatedRecord, AbstractDynamicDataSource abstractDynamicDataSource, final DSCallback cb) {
        this.associatedRecord = associatedRecord;
        this.abstractDynamicDataSource = abstractDynamicDataSource;
        ClassTree classTree = abstractDynamicDataSource.getPolymorphicEntityTree();
        String[] types = associatedRecord.getAttributeAsStringArray("_type");
        boolean shouldLoad = availableToTypes == null;
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
        display.setVisible(shouldLoad);

        if (shouldLoad) {
            String id = getRelationshipValue(associatedRecord, abstractDynamicDataSource);
            ((PresentationLayerAssociatedDataSource) display.getGrid().getDataSource()).loadAssociatedGridBasedOnRelationship(id, new DSCallback() {
                @Override
                public void execute(DSResponse response, Object rawData, DSRequest request) {
                    String locked = associatedRecord.getAttribute("__locked");
                    if (!readOnly) {
                        if (!(locked != null && locked.equals("true"))) {
                            setReadOnly(false);
                            setStartState();
                        } else {
                            setReadOnly(true);
                        }
                    }
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
            //this is a root entity
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
        if (!prefix.contains(".")) {
            //this may be an embedded class directly on the root entity
            return abstractDynamicDataSource.getPrimaryKeyValue(associatedRecord);
        }
        throw new RuntimeException("Unable to establish a relationship value for the datasource: " + abstractDynamicDataSource.getDataURL());
    }

    @Override
    public void setReadOnly(Boolean readOnly) {
        if (readOnly) {
            disable();
            display.getGrid().enable();
        } else {
            enable();
        }
        this.readOnly = readOnly;
    }

    @Override
    public void setDataSource(ListGridDataSource dataSource, String[] gridFields, Boolean[] editable) {
        display.getGrid().setDataSource(dataSource);
        dataSource.setAssociatedGrid(display.getGrid());
        dataSource.permanentlyShowFields(gridFields);
        dataSource.setupGridFields(gridFields, editable);
    }

    @Override
    public Canvas getDisplay() {
        return (Canvas) display;
    }
}
