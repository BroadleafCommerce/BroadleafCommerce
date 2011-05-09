package org.broadleafcommerce.gwt.client.view;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.HasClickHandlers;

public interface AbstractDisplay {

	public abstract Canvas asCanvas();
	public abstract void draw();
	public abstract void show();
	public abstract void hide();
	public abstract void clear();
	HasClickHandlers getAddButton();
}
