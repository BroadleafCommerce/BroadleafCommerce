package org.broadleafcommerce.gwt.client.presenter.entity;

import org.broadleafcommerce.gwt.client.view.Display;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.widgets.Canvas;

public interface EntityPresenter {

	public abstract void go(final Canvas container);

	public HandlerManager getEventBus();

	public void setEventBus(HandlerManager eventBus);

	public Display getDisplay();

	public void setDisplay(Display display);

}
