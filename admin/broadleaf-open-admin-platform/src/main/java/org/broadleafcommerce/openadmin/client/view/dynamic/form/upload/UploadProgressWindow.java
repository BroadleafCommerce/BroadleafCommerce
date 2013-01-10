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

package org.broadleafcommerce.openadmin.client.view.dynamic.form.upload;

import com.smartgwt.client.widgets.Window;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.view.Stoppable;

/**
 * 
 * @author jfischer
 *
 */
public class UploadProgressWindow extends Window implements Stoppable {

    private UploadStatusProgress progressBar;

    public UploadProgressWindow() {
        setWidth(360);  
        setHeight(52);  
        setShowMinimizeButton(false);  
        setIsModal(true);   
        centerInPage();
        setTitle(BLCMain.getMessageManager().getString("contactingServerTitle"));
        setShowCloseButton(false);
        progressBar = new UploadStatusProgress(24);
        addItem(progressBar);
    }

    public void startProgress() {
        show();
        progressBar.startProgress();
    }
    
    public void stopProgress() {
        progressBar.stopProgress();
    }
    
    public void finalizeProgress() {
        hide();
    }
    
    public Boolean isActive() {
        return progressBar.isActive();
    }

    public UploadStatusProgress getProgressBar() {
        return progressBar;
    }
}
