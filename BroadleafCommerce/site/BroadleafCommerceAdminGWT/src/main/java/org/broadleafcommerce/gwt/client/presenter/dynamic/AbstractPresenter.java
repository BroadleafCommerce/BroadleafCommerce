package org.broadleafcommerce.gwt.client.presenter.dynamic;

import org.broadleafcommerce.gwt.client.presenter.Presenter;

import com.google.gwt.event.shared.HandlerManager;

public abstract class AbstractPresenter implements Presenter {

	protected HandlerManager eventBus;
	
	public HandlerManager getEventBus() {
		return eventBus;
	}

	public void setEventBus(HandlerManager eventBus) {
		this.eventBus = eventBus;
	}
	
}
