package org.broadleafcommerce.gwt.client.view;

import com.smartgwt.client.widgets.Canvas;

public interface Display {

	public abstract Canvas asCanvas();

	public abstract void draw();

	public abstract void show();

	public abstract void hide();

	public abstract void destroy();
	
}
