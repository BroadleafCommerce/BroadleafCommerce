package org.broadleafcommerce.gwt.client.presenter.dynamic.entity;


import com.google.gwt.event.shared.HandlerManager;

public abstract class AbstractEntityPresenter implements EntityPresenter {

	protected HandlerManager eventBus;
	
	public HandlerManager getEventBus() {
		return eventBus;
	}

	public void setEventBus(HandlerManager eventBus) {
		this.eventBus = eventBus;
	}
	
}
