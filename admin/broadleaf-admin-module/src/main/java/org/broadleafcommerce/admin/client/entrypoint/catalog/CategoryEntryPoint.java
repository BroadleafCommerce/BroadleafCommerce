/*
 * Copyright 2008-2009 the original author or authors.
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

package org.broadleafcommerce.admin.client.entrypoint.catalog;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import org.broadleafcommerce.admin.client.presenter.catalog.category.CategoryPresenter;
import org.broadleafcommerce.admin.client.view.catalog.category.CategoryView;
import org.broadleafcommerce.openadmin.client.reflection.ModuleFactory;
import org.broadleafcommerce.openadmin.client.setup.BroadleafAbstractEntryPoint;

import java.util.ArrayList;
import java.util.List;

public class CategoryEntryPoint extends BroadleafAbstractEntryPoint {

    @Override
    public void onModuleLoad() {
        HLayout container = new HLayout();


        IButton button = new IButton("&quot;Hello World TEST &quot;");
        button.addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent event)
            {
                SC.say("&quot;Hello World from SmartGWT&quot;");
            }
        });
        container.addChild(button);

        RootPanel.get("gwt-component").add(container);

        initializeEntryPoint(container, new CategoryPresenter(), new CategoryView());

        List<String> categoryPermissions = new ArrayList<String>();
        categoryPermissions.add("PERMISSION_CREATE_CATEGORY");
        categoryPermissions.add("PERMISSION_UPDATE_CATEGORY");
        categoryPermissions.add("PERMISSION_DELETE_CATEGORY");
        categoryPermissions.add("PERMISSION_READ_CATEGORY");

        ModuleFactory moduleFactory = ModuleFactory.getInstance();
        moduleFactory.put("category", "org.broadleafcommerce.admin.client.view.catalog.category.CategoryView");
        moduleFactory.put("categoryPresenter", "org.broadleafcommerce.admin.client.presenter.catalog.category.CategoryPresenter");
        org.broadleafcommerce.openadmin.client.security.SecurityManager.getInstance().registerSection("BLCMerchandising", "category", categoryPermissions);

    }


}
