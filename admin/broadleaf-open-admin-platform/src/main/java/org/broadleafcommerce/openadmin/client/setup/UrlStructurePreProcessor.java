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

package org.broadleafcommerce.openadmin.client.setup;

import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.service.AbstractCallback;
import org.broadleafcommerce.openadmin.client.service.AppServices;
import org.broadleafcommerce.openadmin.client.view.SimpleProgress;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.ServerProcessProgressWindow;

/**
 * @author Jeff Fischer
 */
public class UrlStructurePreProcessor implements PreProcessor {

    @Override
    public void preProcess(final ServerProcessProgressWindow progressWindow, final PreProcessStatus cb) {
        SimpleProgress progress = new SimpleProgress(24);
        progressWindow.setProgressBar(progress);
        progressWindow.setTitleKey("urlStructurePreProcessTitle");
        progressWindow.startProgress();
        java.util.logging.Logger.getLogger(UrlStructurePreProcessor.class.getName()).info("Retrieving web app context...");

        AppServices.UTILITY.getAllItems(new AbstractCallback<String[]>() {
            @Override
            public void onSuccess(String[] result) {
                BLCMain.webAppContext = result[0];
                if (result[1] != null) {
                    BLCMain.storeFrontWebAppPrefix = result[1];
                } else {
                    BLCMain.storeFrontWebAppPrefix = BLCMain.webAppContext;
                }
                BLCMain.assetServerUrlPrefix = result[2];
                BLCMain.csrfToken = result[3];
                progressWindow.stopProgress();
                progressWindow.finalizeProgress();
                cb.complete();
            }
        });
    }

}
