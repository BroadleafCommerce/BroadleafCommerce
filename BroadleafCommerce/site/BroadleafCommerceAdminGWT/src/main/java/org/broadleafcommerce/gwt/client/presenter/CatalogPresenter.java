package org.broadleafcommerce.gwt.client.presenter;

import org.broadleafcommerce.gwt.client.view.catalog.CatalogDisplay;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.widgets.Canvas;

public class CatalogPresenter implements Presenter {

	protected HandlerManager eventBus;
	protected CatalogDisplay display;

	public CatalogPresenter(HandlerManager eventBus, CatalogDisplay view) {
		this.eventBus = eventBus;
		this.display = view;
	}
	
	public void bind() {
		
	}
	
	public void go(Canvas container) {
		bind();
		if (container.contains(display.asCanvas())) {
			display.show();
		} else {
			container.addChild(display.asCanvas());
			//display.draw();
		}
	}

}
