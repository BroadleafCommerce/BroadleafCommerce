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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.widgets.Progressbar;

/**
 * 
 * @author jfischer
 *
 */
public class SimpleProgress extends Progressbar {
	
	private int barValue;
	private Timer timer;
	private boolean isActive = false;
	private Double current = 100D;
	
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
            	Double factor = 1/current;
            	/*
            	 * At 50 ms intervals, it should take 20 seconds
            	 * to go through 100% of the progress using an
            	 * increment of .25
            	 */
            	current -= .25D;
                barValue += (int) (100D * factor);
                if (barValue >= 100) { 
                	current = 100D;
                    barValue = 0;  
                }  
                setPercentDone(barValue);
                if(isActive)  
                    schedule(50); 
            }  
        };
        setOpacity(50);
	}
	
	private void stop(final Stoppable progressContainer) {
		final IntContainer container = new IntContainer(100);
		Timer timer = new Timer() {  
            public void run() { 
                setPercentDone(container.getVal());
                if(container.getVal() > 0) {  
                	container.setVal(0);
                    schedule(500); 
                } else {
                	setOpacity(50);
                	if (progressContainer != null) {
                		progressContainer.stop();
                	}
                }
            }  
        };
        timer.schedule(50);
	}
	
	public void startProgress() {
		isActive = true;
		barValue = 0;  
		setOpacity(100);
        setPercentDone(barValue);
        timer.schedule(50);
	}
	
	public void stopProgress(Stoppable progressContainer) {
		isActive = false;
		timer.cancel();
		stop(progressContainer);
	}
	
	public void stopProgress() {
		isActive = false;
		timer.cancel();
		stop(null);
	}
	
	public Boolean isActive() {
		return isActive;
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
}
