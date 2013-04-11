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

package org.broadleafcommerce.openadmin.client.view.dynamic.form;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.widgets.Progressbar;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.service.AppServices;

/**
 * 
 * @author jfischer
 *
 */
public class UploadStatusProgress extends Progressbar implements Progress {

    private int barValue;
    private Timer timer;
    private boolean isActive = false;
    private Double current = 1D;
    private String callbackName;
    private ServerProcessProgressWindow window;

    public UploadStatusProgress(Integer height) {
        this(null, height);
    }

    public UploadStatusProgress(Integer width, Integer height) {
        this.callbackName = callbackName;
        setHeight(height);  
        if (width != null) setWidth(width);
        setVertical(false);
        setTitle("test");
        timer = new Timer() {
            public void run() {
                AppServices.UPLOAD.getPercentUploadComplete(callbackName, BLCMain.csrfToken, new AsyncCallback<Double>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        throw new RuntimeException(caught);
                    }

                    @Override
                    public void onSuccess(Double result) {
                        barValue = result.intValue();
                        setPercentDone(barValue);
                        if(isActive)
                            schedule(3000);
                    }
                });
            }
        };
        setOpacity(50);
    }

    public void startProgress() {
        isActive = true;
        barValue = 0;  
        current = 1D;
        setOpacity(100);
        setPercentDone(barValue);
        timer.schedule(50);
    }
    
    public void stopProgress() {
        isActive = false;
        timer.cancel();
        setPercentDone(100);
    }
    
    public Boolean isActive() {
        return isActive;
    }

    public String getCallbackName() {
        return callbackName;
    }

    public void setCallbackName(String callbackName) {
        this.callbackName = callbackName;
    }

    private class IntContainer {
        
        public IntContainer(int val) {
            this.val = val;
        }
        
        int val;

        /**
         * @return the val
         */
        public int getVal() {
            return val;
        }

        /**
         * @param val the val to set
         */
        public void setVal(int val) {
            this.val = val;
        }
        
    }

    @Override
    public void setDisplay(ServerProcessProgressWindow window) {
        this.window = window;
    }
}
