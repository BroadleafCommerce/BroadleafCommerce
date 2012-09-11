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

public class CustomerModule extends AbstractModule {

    public void onModuleLoad() {
        addConstants(GWT.<ConstantsWithLookup>create(CustomerCareMessages.class));
        addConstants(GWT.<ConstantsWithLookup>create(GeneratedMessagesEntityCommon.class));
        addConstants(GWT.<ConstantsWithLookup>create(GeneratedMessagesEntityFramework.class));
        addConstants(GWT.<ConstantsWithLookup>create(GeneratedMessagesEntityProfile.class));
        addConstants(GWT.<ConstantsWithLookup>create(GeneratedMessagesEntityOpenAdmin.class));
        addConstants(GWT.<ConstantsWithLookup>create(OpenAdminMessages.class));

        setModuleTitle(BLCMain.getMessageManager().getString("customerCareModuleTitle"));
        setModuleKey("BLCCustomerModule");

        List<String> customerPermissions = new ArrayList<String>();
        customerPermissions.add("PERMISSION_CREATE_CUSTOMER");
        customerPermissions.add("PERMISSION_UPDATE_CUSTOMER");
        customerPermissions.add("PERMISSION_DELETE_CUSTOMER");
        customerPermissions.add("PERMISSION_READ_CUSTOMER");
        setSection(
                BLCMain.getMessageManager().getString("customerMainTitle"),
                "customer",
                "org.broadleafcommerce.admin.client.view.customer.CustomerView",
                "customerPresenter",
                "org.broadleafcommerce.admin.client.presenter.customer.CustomerPresenter",
                customerPermissions
        );

        setOrder(200);

        registerModule();
    }

}

