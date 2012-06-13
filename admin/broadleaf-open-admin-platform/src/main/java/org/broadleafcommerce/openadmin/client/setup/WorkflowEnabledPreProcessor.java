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
public class WorkflowEnabledPreProcessor implements PreProcessor {

    @Override
    public void preProcess(final ServerProcessProgressWindow progressWindow, final PreProcessStatus cb) {
        SimpleProgress progress = new SimpleProgress(24);
        progressWindow.setProgressBar(progress);
        progressWindow.setTitleKey("workflowEnabledPreProcessTitle");
        progressWindow.startProgress();
        java.util.logging.Logger.getLogger(WorkflowEnabledPreProcessor.class.getName()).info("Retrieving workflow enabled status...");

        AppServices.UTILITY.getWorkflowEnabled(null, new AbstractCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                BLCMain.workflowEnabled = result;
                if (!result) {
                    BLCMain.removeModule("BLCSandBox");
                }
                progressWindow.stopProgress();
                progressWindow.finalizeProgress();
                cb.complete();
            }
        });
    }
}
