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

package org.broadleafcommerce.cms.admin.client.view.structure;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.grid.HoverCustomizer;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.FieldDataSourceWrapper;
import org.broadleafcommerce.openadmin.client.reflection.Instantiable;
import org.broadleafcommerce.openadmin.client.view.TabSet;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityListView;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormView;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 8/22/11
 * Time: 3:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class StructuredContentView extends HLayout implements Instantiable, StructuredContentDisplay {

    protected DynamicEntityListView listDisplay;
    protected DynamicFormView dynamicFormDisplay;
    protected FilterBuilder customerFilterBuilder;

    public StructuredContentView() {
		setHeight100();
		setWidth100();
	}

    public void build(DataSource entityDataSource, DataSource... additionalDataSources) {
        DataSource customerDataSource = additionalDataSources[0];
        //DataSource orderItemDataSource = additionalDataSources[1];
        //DataSource timeDataSource = additionalDataSources[2];
        //DataSource requestDataSource = additionalDataSources[3];

		VLayout leftVerticalLayout = new VLayout();
		leftVerticalLayout.setID("structureLeftVerticalLayout");
		leftVerticalLayout.setHeight100();
		leftVerticalLayout.setWidth("40%");
		leftVerticalLayout.setShowResizeBar(true);
		listDisplay = new DynamicEntityListView(BLCMain.getMessageManager().getString("pagesTitle"), entityDataSource);
        listDisplay.getGrid().setHoverMoveWithMouse(true);
        listDisplay.getGrid().setCanHover(true);
        listDisplay.getGrid().setShowHover(true);
        listDisplay.getGrid().setHoverOpacity(75);
        listDisplay.getGrid().setHoverCustomizer(new HoverCustomizer() {
            @Override
            public String hoverHTML(Object value, ListGridRecord record, int rowNum, int colNum) {
                if (record.getAttribute("lockedFlag") != null && record.getAttributeAsBoolean("lockedFlag")) {
                    return "Last Updated By <B>" + record.getAttribute("auditable.updatedBy.name") + "</B> On <B>" + record.getAttribute("auditable.dateUpdated") + "</B>.";
                }
                return null;
            }
        });

        leftVerticalLayout.addMember(listDisplay);
        dynamicFormDisplay = new DynamicFormView(BLCMain.getMessageManager().getString("detailsTitle"), entityDataSource);

        addMember(leftVerticalLayout);

        TabSet topTabSet = new TabSet();
        topTabSet.setID("scTopTabSet");
        topTabSet.setTabBarPosition(Side.TOP);
        topTabSet.setPaneContainerOverflow(Overflow.HIDDEN);
        topTabSet.setWidth("50%");
        topTabSet.setHeight100();
        topTabSet.setPaneMargin(0);

        Tab detailsTab = new Tab(BLCMain.getMessageManager().getString("scDetailsTabTitle"));
        detailsTab.setID("scDetailsTab");
        detailsTab.setPane(dynamicFormDisplay);
        topTabSet.addTab(detailsTab);

        Tab rulesTab = new Tab(BLCMain.getMessageManager().getString("scRulesTabTitle"));
        rulesTab.setID("scRulesTab");
        VLayout rulesLayout = new VLayout();
        rulesLayout.setHeight100();
        rulesLayout.setWidth100();
        rulesLayout.setBackgroundColor("#eaeaea");
        rulesLayout.setOverflow(Overflow.AUTO);

        customerFilterBuilder = new FilterBuilder();
        customerFilterBuilder.setDataSource(customerDataSource);
        customerFilterBuilder.setFieldDataSource(new FieldDataSourceWrapper(customerDataSource));
        customerFilterBuilder.setVisible(false);
        customerFilterBuilder.setLayoutBottomMargin(10);
        customerFilterBuilder.setAllowEmpty(true);
        customerFilterBuilder.setValidateOnChange(false);
        rulesLayout.addMember(customerFilterBuilder);

        rulesTab.setPane(rulesLayout);
        topTabSet.addTab(rulesTab);

        addMember(topTabSet);
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
