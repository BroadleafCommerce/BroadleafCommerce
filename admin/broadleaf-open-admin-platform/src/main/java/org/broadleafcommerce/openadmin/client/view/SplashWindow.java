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

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * 
 * @author jfischer
 *
 */
public class SplashWindow extends Window implements SplashView, Stoppable {
    
    private SimpleProgress simpleProgress;
    
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
        spacer.setHeight(95);
        layout.addMember(spacer);
        HLayout versionLayout = new HLayout();
        versionLayout.setAlign(Alignment.LEFT);
        versionLayout.setHeight(15);
        HLayout spacer3 = new HLayout();
        spacer3.setWidth(25);
        versionLayout.addMember(spacer3);
        Label versionLabel = new Label("&nbsp;");
        versionLabel.setWrap(false);
        versionLabel.setStyleName("versionStyle");
        versionLabel.setHeight(15);
        versionLayout.addMember(versionLabel);
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
    }

    public void startProgress() {
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
