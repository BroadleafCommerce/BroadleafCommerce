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

package org.broadleafcommerce.admin.client.modules.customerCare;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.ConstantsWithLookup;
import org.broadleafcommerce.admin.client.CustomerCareMessages;
import org.broadleafcommerce.openadmin.client.*;

import java.util.ArrayList;
import java.util.List;

public class OrderModule extends AbstractModule {

    public void onModuleLoad() {
        addConstants(GWT.<ConstantsWithLookup>create(CustomerCareMessages.class));
        addConstants(GWT.<ConstantsWithLookup>create(GeneratedMessagesEntityCommon.class));
        addConstants(GWT.<ConstantsWithLookup>create(GeneratedMessagesEntityFramework.class));
        addConstants(GWT.<ConstantsWithLookup>create(GeneratedMessagesEntityProfile.class));
        addConstants(GWT.<ConstantsWithLookup>create(GeneratedMessagesEntityOpenAdmin.class));
        addConstants(GWT.<ConstantsWithLookup>create(OpenAdminMessages.class));

        setModuleTitle(BLCMain.getMessageManager().getString("customerCareModuleTitle"));
        setModuleKey("BLCOrderModule");

        List<String> orderPermissions = new ArrayList<String>();
        orderPermissions.add("PERMISSION_CREATE_ORDER");
        orderPermissions.add("PERMISSION_UPDATE_ORDER");
        orderPermissions.add("PERMISSION_DELETE_ORDER");
        orderPermissions.add("PERMISSION_READ_ORDER");
        setSection(
                BLCMain.getMessageManager().getString("orderMainTitle"),
                "order",
                "org.broadleafcommerce.admin.client.view.order.OrderView",
                "orderPresenter",
                "org.broadleafcommerce.admin.client.presenter.order.OrderPresenter",
                orderPermissions
        );

        setOrder(200);

        registerModule();
    }

}
