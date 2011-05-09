package org.broadleafcommerce.gwt.client.view;

import com.google.gwt.user.client.Timer;
import com.smartgwt.client.widgets.Progressbar;

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
        hide();
	}
	
	public void startProgress() {
		isActive = true;
		show();
		barValue = 0;  
        setPercentDone(barValue);
        timer.schedule(50);
	}
	
	public void stopProgress() {
		isActive = false;
		timer.cancel();
		setPercentDone(100);
		hide();
	}
	
	public Boolean isActive() {
		return isActive;
	}
}
