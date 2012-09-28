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

import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.presenter.structure.MapStructurePresenter;

/**
 * 
 * @author Phillip Verheyden
 */
public class DefaultSkuMediaMapStructurePresenter extends MapStructurePresenter {

    public DefaultSkuMediaMapStructurePresenter(MapStructurePresenter template) {
        super(template);
    }
    
    @Override
    public String getRelationshipValue(final Record associatedRecord, AbstractDynamicDataSource abstractDynamicDataSource) {
        return associatedRecord.getAttributeAsString("defaultSku.id");
    }
    
    @Override
    public void bind() {
        super.bind();
        addClickedHandlerRegistration.removeHandler();
        addClickedHandlerRegistration = display.getAddButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    DynamicEntityDataSource dataSource = (DynamicEntityDataSource) display.getGrid().getDataSource();
                    initialValues.put("symbolicId", dataSource.getCompatibleModule(dataSource.getPersistencePerspective().getOperationTypes().getAddType()).getLinkedValue());
                    //The type should always be the fully-qualified name of this datasource, since that's different than the associatedRecord's dataSource
                    //(the associatedRecord's classname is Product, whereas this should be Sku)
                    String[] type = new String[] {((DynamicEntityDataSource) display.getGrid().getDataSource()).getDefaultNewEntityFullyQualifiedClassname()};
                    initialValues.put("_type", type);
                    entityEditDialog.editNewRecord(entityEditDialogTitle, (DynamicEntityDataSource) display.getGrid().getDataSource(), initialValues, null, gridFields, null);
                }
            }
        });

    }

}
