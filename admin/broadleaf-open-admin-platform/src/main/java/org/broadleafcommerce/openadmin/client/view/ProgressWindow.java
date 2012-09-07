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

package org.broadleafcommerce.openadmin.client.view;

import com.google.gwt.user.client.Timer;
import com.smartgwt.client.widgets.Window;
import org.broadleafcommerce.openadmin.client.BLCMain;

/**
 * 
 * @author jfischer
 *
 */
public class ProgressWindow extends Window implements Stoppable {
	
	private SimpleProgress simpleProgress;
	
	public ProgressWindow() {
    	setWidth(360);  
    	setHeight(52);  
    	setShowMinimizeButton(false);  
    	setIsModal(true);   
    	centerInPage();
    	setTitle(BLCMain.getMessageManager().getString("contactingServerTitle"));
    	setShowCloseButton(false);
    	simpleProgress = new SimpleProgress(24);    
    	addItem(simpleProgress);
	}

	public void startProgress(Timer timer) {
		//show();
		simpleProgress.startProgress();
		timer.schedule(300);
	}
	
	public void stopProgress() {
		simpleProgress.stopProgress(this);
	}
	
	public void finalizeProgress() {
		//hide();
	}
	
	public Boolean isActive() {
		return simpleProgress.isActive();
	}
}
