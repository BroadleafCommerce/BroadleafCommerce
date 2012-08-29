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

import com.smartgwt.client.widgets.Canvas;
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
        Canvas canvas = new HLayout();
        canvas.setWidth100();
        canvas.setHeight100();

        initializeEntryPoint(canvas, new CategoryPresenter(), new CategoryView());

        List<String> categoryPermissions = new ArrayList<String>();
        categoryPermissions.add("PERMISSION_CREATE_CATEGORY");
        categoryPermissions.add("PERMISSION_UPDATE_CATEGORY");
        categoryPermissions.add("PERMISSION_DELETE_CATEGORY");
        categoryPermissions.add("PERMISSION_READ_CATEGORY");

        ModuleFactory moduleFactory = ModuleFactory.getInstance();
        moduleFactory.put("category", "org.broadleafcommerce.admin.client.view.catalog.category.CategoryView");
        moduleFactory.put("categoryPresenter", "org.broadleafcommerce.admin.client.presenter.catalog.category.CategoryPresenter");
        org.broadleafcommerce.openadmin.client.security.SecurityManager.getInstance().registerSection("BLCMerchandising", "category", categoryPermissions);

        canvas.draw();
    }

}
