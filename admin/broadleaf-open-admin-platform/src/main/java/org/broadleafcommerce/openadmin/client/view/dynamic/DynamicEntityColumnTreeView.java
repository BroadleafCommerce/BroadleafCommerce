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

package org.broadleafcommerce.openadmin.client.view.dynamic;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.events.FocusEvent;
import com.smartgwt.client.widgets.form.fields.events.FocusHandler;
import com.smartgwt.client.widgets.grid.ColumnTree;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class DynamicEntityColumnTreeView extends VLayout implements DynamicEntityColumnTreeDisplay {

    protected ToolStripButton addButton;
    protected ToolStripButton removeButton;
    protected ComboBoxItem entityType = new ComboBoxItem();
    protected ColumnTree grid;
    protected ToolStrip toolBar;

    public DynamicEntityColumnTreeView(String title, DataSource dataSource) {
        super();
        if ("".equals(title) || title == null) {
            title = "Item";
        }
        toolBar = new ToolStrip();
        toolBar.setHeight(30);
        toolBar.setWidth100();
        toolBar.addSpacer(6);

        addButton = new ToolStripButton();  
        addButton.setDisabled(true);
        addButton.setTitle(BLCMain.getMessageManager().getString("addTitle"));
        addButton.setIcon(GWT.getModuleBaseURL()+"sc/skins/Broadleaf/images/actions/add.png");
        toolBar.addButton(addButton);

        removeButton = new ToolStripButton();
        removeButton.setTitle(BLCMain.getMessageManager().getString("removeTitle"));
        removeButton.setIcon(GWT.getModuleBaseURL() + "sc/skins/Broadleaf/images/actions/remove.png");
        removeButton.setDisabled(true);
        toolBar.addButton(removeButton);

        toolBar.addSpacer(6);
        Label categoryLabel = new Label();
        categoryLabel.setContents(title);
        categoryLabel.setWrap(false);
        toolBar.addMember(categoryLabel);
        toolBar.addFill();
        Map<String, String> polymorphicEntities = ((DynamicEntityDataSource) dataSource).getPolymorphicEntities();
        if (polymorphicEntities.size() > 1) { 
            entityType.setShowTitle(false);  
            entityType.setWidth(120); 
            
            LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();  
            for (String name : polymorphicEntities.keySet()) {
                valueMap.put(name, polymorphicEntities.get(name)); 
            }
            entityType.setValueMap(valueMap);  
            entityType.setDefaultValue(((DynamicEntityDataSource) dataSource).getDefaultNewEntityFullyQualifiedClassname()); 
            entityType.addFocusHandler(new FocusHandler() {
                public void onFocus(FocusEvent event) {
                    ((ComboBoxItem) event.getItem()).selectValue();
                }
            });
            toolBar.addFormItem(entityType);
        }
        addMember(toolBar);
        grid = new ColumnTree();
        grid.setDataSource(dataSource);
        grid.setAutoFetchData(true);
        grid.setDataPageSize(10);
        grid.setShowOpenIcons(false);
        grid.setShowDropIcons(false);
        grid.setClosedIconSuffix("");
        grid.setShowHeaders(true);
        grid.setShowNodeCount(true);
        grid.setLoadDataOnDemand(true);
        addMember(grid);
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityTreeDisplay#getAddButton()
     */
    public ToolStripButton getAddButton() {
        return addButton;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityTreeDisplay#getRemoveButton()
     */
    public ToolStripButton getRemoveButton() {
        return removeButton;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityTreeDisplay#getEntityType()
     */
    public ComboBoxItem getEntityType() {
        return entityType;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.client.view.dynamic.DynamicEntityTreeDisplay#getGrid()
     */
    public ColumnTree getGrid() {
        return grid;
    }

    public ToolStrip getToolBar() {
        return toolBar;
    }

}
