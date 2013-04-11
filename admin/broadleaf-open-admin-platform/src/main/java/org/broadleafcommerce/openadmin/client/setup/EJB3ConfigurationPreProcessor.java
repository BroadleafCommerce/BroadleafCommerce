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

import org.broadleafcommerce.openadmin.client.service.AbstractCallback;
import org.broadleafcommerce.openadmin.client.service.AppServices;
import org.broadleafcommerce.openadmin.client.view.SimpleProgress;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.ServerProcessProgressWindow;

import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class EJB3ConfigurationPreProcessor implements PreProcessor {

    @Override
    public void preProcess(final ServerProcessProgressWindow progressWindow, Map<String, String> piplineSeed, final PreProcessStatus cb) {
        SimpleProgress progress = new SimpleProgress(24);
        progressWindow.setProgressBar(progress);
        progressWindow.setTitleKey("ejb3ConfigurationPreProcessTitle");
        progressWindow.startProgress();
        java.util.logging.Logger.getLogger(EJB3ConfigurationPreProcessor.class.getName()).info("Initializing EJB3Configuration...");

        AppServices.UTILITY.initializeEJB3Configuration(new AbstractCallback<Void>() {
            @Override
            public void onSuccess(Void response) {
                progressWindow.stopProgress();
                progressWindow.finalizeProgress();
                cb.complete();
            }
        });
    }
}
