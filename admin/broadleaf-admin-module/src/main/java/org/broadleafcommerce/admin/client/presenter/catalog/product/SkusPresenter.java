/*
 * Copyright 2012 the original author or authors.
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

package org.broadleafcommerce.admin.client.presenter.catalog.product;

import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.client.presenter.entity.SubPresenter;
import org.broadleafcommerce.openadmin.client.view.dynamic.SubItemDisplay;

import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Phillip Verheyden
 */
public class SkusPresenter extends SubPresenter {

    protected String newEntityDialogTitle;
    protected Map<String, Object> initialValues = new HashMap<String, Object>();
    protected HandlerRegistration removedClickedHandlerRegistration;
    protected HandlerRegistration addClickedHandlerRegistration;

    public SkusPresenter(SubItemDisplay display, String newEntityDialogTitle, String[] availableToTypes, Boolean showDisabledState, Boolean canEdit, Boolean showId) {
        super(display, availableToTypes, showDisabledState, canEdit, showId);
        this.newEntityDialogTitle = newEntityDialogTitle;
    }

    @Override
    public void bind() {
        super.bind();
        addClickedHandlerRegistration = display.getAddButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    DynamicEntityDataSource ds = (DynamicEntityDataSource) display.getGrid().getDataSource();
                    ForeignKey foreignKey = (ForeignKey) ds.getPersistencePerspective().getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);
                    initialValues.put(foreignKey.getManyToField(), abstractDynamicDataSource.getPrimaryKeyValue(associatedRecord));
                    String[] type = new String[] {((DynamicEntityDataSource) display.getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname()};
                    initialValues.put("_type", type);
                    BLCMain.ENTITY_ADD.editNewRecord(newEntityDialogTitle, ds, initialValues, null, null, null);
                }
            }
        });
        removedClickedHandlerRegistration = display.getRemoveButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    display.getGrid().removeData(display.getGrid().getSelectedRecord(), new DSCallback() {
                        @Override
                        public void execute(DSResponse response, Object rawData, DSRequest request) {
                            display.getRemoveButton().disable();
                        }
                    });
                }
            }
        });
    }

}
