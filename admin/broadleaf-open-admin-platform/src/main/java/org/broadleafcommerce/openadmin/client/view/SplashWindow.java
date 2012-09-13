/*
 * Copyright 2008-2012 the original author or authors.
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

package org.broadleafcommerce.openadmin.client.view;

import com.smartgwt.client.core.KeyIdentifier;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.KeyNames;
import com.smartgwt.client.util.KeyCallback;
import com.smartgwt.client.util.Page;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import org.broadleafcommerce.openadmin.client.BLCMain;

/**
 * 
 * @author jfischer
 *
 */
public class SplashWindow extends Window implements SplashView, Stoppable {
	
	private SimpleProgress simpleProgress;
    private Label buildDateLabel;
	
	public SplashWindow(String backgroundImage, String version) {
		setShowShadow(true);
		setBackgroundImage(backgroundImage);
		setShowEdges(false);
    	setWidth(447);  
    	setHeight(257);  
    	setShowMinimizeButton(false);
    	setShowTitle(false);
    	setShowHeader(false);
    	setIsModal(true);   
    	centerInPage();
    	setShowCloseButton(false);
    	VLayout layout = new VLayout();
    	VLayout spacer = new VLayout();
    	spacer.setHeight(90);
    	layout.addMember(spacer);
    	HLayout versionLayout = new HLayout();
    	versionLayout.setAlign(Alignment.LEFT);
    	versionLayout.setHeight(15);
    	HLayout spacer3 = new HLayout();
    	spacer3.setWidth(25);
    	versionLayout.addMember(spacer3);
        VLayout temp = new VLayout(5);
    	Label versionLabel = new Label("Core Version: " + BLCMain.getMessageManager().getString("openAdminVersion") + "/" + BLCMain.getMessageManager().getString("buildDate"));
    	versionLabel.setWrap(false);
    	versionLabel.setStyleName("versionStyle");
    	versionLabel.setHeight(15);
        temp.addMember(versionLabel);
        
        buildDateLabel = new Label("&nbsp;");
        buildDateLabel.setWrap(false);
        buildDateLabel.setStyleName("versionStyle");
        buildDateLabel.setHeight(15);
        
        temp.addMember(buildDateLabel);
        versionLayout.addMember(temp);
    	layout.addMember(versionLayout);
    	VLayout spacer2 = new VLayout();
    	spacer2.setHeight(10);
    	layout.addMember(spacer2);
    	HLayout progressLayout = new HLayout();
    	progressLayout.setAlign(Alignment.CENTER);
    	simpleProgress = new SimpleProgress(24); 
    	simpleProgress.setWidth(417);
    	progressLayout.addMember(simpleProgress);
    	layout.addMember(progressLayout);
    	setBorder("1px solid #3b4726");
    	addItem(layout);

        KeyIdentifier escapeKey = new KeyIdentifier();
        escapeKey.setKeyName(KeyNames.ESC);
        Page.registerKey(escapeKey, new KeyCallback() {
            public void execute(String keyName) {
                SplashWindow.this.hide();
            }
        });
	}
    
    protected void showClientBuildDate() {
        String clientBuildDate = BLCMain.getMessageManager().getString("clientBuildDate");
        if (!clientBuildDate.equals("${clientBuildDate}")) {
            buildDateLabel.setContents("Build Date: " + clientBuildDate);
        }
    }

    public void explicitShow() {
        showClientBuildDate();
        simpleProgress.setVisible(false);
        show();
    }

	public void startProgress() {
        showClientBuildDate();
        simpleProgress.setVisible(true);
		show();
		simpleProgress.startProgress();
	}
	
	public void stopProgress() {
		simpleProgress.stopProgress(this);
	}
	
	public void finalizeProgress() {
		hide();
	}
	
	public Boolean isActive() {
		return simpleProgress.isActive();
	}
}
