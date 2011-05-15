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
package org.broadleafcommerce.gwt.client.view;

import com.google.gwt.user.client.Timer;
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
	
	public SimpleProgress(Integer height) {
		this(null, height);
	}

	public SimpleProgress(Integer width, Integer height) {
		setHeight(height);  
		if (width != null) setWidth(height);
        setVertical(false); 
        timer = new Timer() {  
            public void run() {  
                barValue += 1 + (int) (10 * Math.random());  
                if (barValue > 100) {  
                    barValue = 0;  
                }  
                setPercentDone(barValue);
                if(isActive)  
                    schedule(5 + (int) (50 * Math.random())); 
            }  
        };
        setOpacity(50);
	}
	
	public void startProgress() {
		isActive = true;
		barValue = 50;  
		setOpacity(100);
        setPercentDone(barValue);
        timer.schedule(50);
	}
	
	public void stopProgress() {
		isActive = false;
		timer.cancel();
		setPercentDone(0);
		setOpacity(50);
	}
	
	public Boolean isActive() {
		return isActive;
	}
}
