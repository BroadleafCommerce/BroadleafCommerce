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

package org.broadleafcommerce.cms.admin.client.view.file;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.HoverCustomizer;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormView;

/**
 * Created by jfischer
 */
public class StaticAssetsView extends HLayout implements Instantiable, StaticAssetsDisplay {

    protected DynamicEntityListView listDisplay;
    protected DynamicFormView dynamicFormDisplay;

    public StaticAssetsView() {
		setHeight100();
		setWidth100();
	}

    public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
        VLayout leftVerticalLayout = new VLayout();
		leftVerticalLayout.setID("staticFolderAssetsGridLayout");
		leftVerticalLayout.setHeight100();
		leftVerticalLayout.setWidth("40%");
		leftVerticalLayout.setShowResizeBar(true);

		listDisplay = new DynamicEntityListView(entityDataSource);
        listDisplay.getGrid().setHoverMoveWithMouse(true);
        listDisplay.getGrid().setCanHover(true);
        listDisplay.getGrid().setShowHover(true);
        listDisplay.getGrid().setHoverOpacity(75);
        listDisplay.getGrid().setHoverCustomizer(new HoverCustomizer() {
            @Override
            public String hoverHTML(Object value, ListGridRecord record, int rowNum, int colNum) {
                if (record != null && record.getAttribute("lockedFlag") != null && record.getAttributeAsBoolean("lockedFlag")) {
                    return BLCMain.getMessageManager().replaceKeys(BLCMain.getMessageManager().getString("lockedMessage"), new String[]{"userName", "date"}, new String[]{record.getAttribute("auditable.updatedBy.name"), record.getAttribute("auditable.dateUpdated")});
                }
                return null;
            }
        });

        leftVerticalLayout.addMember(listDisplay);

        dynamicFormDisplay = new DynamicFormView(entityDataSource);

        addMember(leftVerticalLayout);
        addMember(dynamicFormDisplay);
	}

    public Canvas asCanvas() {
		return this;
	}

    public DynamicEntityListDisplay getListDisplay() {
		return listDisplay;
	}

    public DynamicFormDisplay getDynamicFormDisplay() {
		return dynamicFormDisplay;
	}
}
