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

package org.broadleafcommerce.openadmin.client.setup;

import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Window;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.security.AdminUser;
import org.broadleafcommerce.openadmin.client.service.AbstractCallback;
import org.broadleafcommerce.openadmin.client.service.AppServices;
import org.broadleafcommerce.openadmin.client.view.SimpleProgress;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.ServerProcessProgressWindow;

import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class UserSecurityPreProcessor implements PreProcessor {

    @Override
    public void preProcess(final ServerProcessProgressWindow progressWindow, Map<String, String> piplineSeed, final PreProcessStatus cb) {
        SimpleProgress progress = new SimpleProgress(24);
        progressWindow.setProgressBar(progress);
        progressWindow.setTitleKey("userSecurityPreProcessTitle");
        progressWindow.startProgress();
        java.util.logging.Logger.getLogger(UserSecurityPreProcessor.class.getName()).info("Retrieving user security...");

        AppServices.SECURITY.getAdminUser(new AbstractCallback<AdminUser>() {
            @Override
            public void onSuccess(AdminUser result) {
                org.broadleafcommerce.openadmin.client.security.SecurityManager.USER = result;
                if (result == null) {
                    java.util.logging.Logger.getLogger(UserSecurityPreProcessor.class.getName()).info("Admin user not found. Logging out...");
                    UrlBuilder builder = Window.Location.createUrlBuilder();
                    builder.setPath(BLCMain.webAppContext + "/admin/adminLogout.htm");
                    builder.setParameter("time", String.valueOf(System.currentTimeMillis()));
                    Window.open(builder.buildString(), "_self", null);
                } else {
                    progressWindow.stopProgress();
                    progressWindow.finalizeProgress();
                    cb.complete();
                }
            }
        });
    }
}
