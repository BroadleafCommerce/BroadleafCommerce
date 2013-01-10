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

import com.google.gwt.user.client.Timer;
import com.smartgwt.client.widgets.Progressbar;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.Progress;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.ServerProcessProgressWindow;

/**
 * 
 * @author jfischer
 *
 */
public class SimpleProgress extends Progressbar implements Progress {
    
    private int barValue;
    private Timer timer;
    private boolean isActive;
    private Double current = 1D;
    
    public SimpleProgress(Integer height) {
        this(null, height);
    }

    public SimpleProgress(Integer width, Integer height) {
        setHeight(height);  
        if (width != null) setWidth(height);
        setVertical(false); 
        timer = new Timer() {  
            public void run() {
                //asymptote calculation
                Double factor = 1D/current;
                current += .009D;
                barValue = (int) (100 - 100D * factor);
                setPercentDone(barValue);
                if(isActive)  
                    schedule(50); 
            }  
        };
        setOpacity(50);
    }
    
    private void finalizeProgress(final Stoppable progressContainer) {
        final IntContainer container = new IntContainer(100);
        Timer timer = new Timer() {  
            public void run() { 
                if (container.getVal() > 0) {  
                    setPercentDone(container.getVal());
                    container.setVal(-1);
                    schedule(10); 
                } else if (container.getVal() == -1){
                    container.setVal(0);
                    schedule(500);
                } else {
                    setOpacity(50);
                    setPercentDone(container.getVal());
                    if (progressContainer != null) {
                        progressContainer.finalizeProgress();
                    }
                }
            }  
        };
        timer.schedule(10);
    }
    
    public void startProgress() {
        isActive = true;
        barValue = 0;  
        current = 1D;
        setOpacity(100);
        setPercentDone(barValue);
        timer.schedule(50);
    }
    
    public void stopProgress(Stoppable progressContainer) {
        isActive = false;
        timer.cancel();
        finalizeProgress(progressContainer);
    }
    
    public void stopProgress() {
        isActive = false;
        timer.cancel();
        finalizeProgress(null);
    }
    
    public Boolean isActive() {
        return isActive;
    }
    
    private static class IntContainer {
        
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
        //do nothing
    }
}
