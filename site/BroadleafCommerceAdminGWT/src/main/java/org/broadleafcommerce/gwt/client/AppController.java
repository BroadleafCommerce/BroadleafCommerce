package org.broadleafcommerce.gwt.client;

import org.broadleafcommerce.gwt.client.presenter.CatalogPresenter;
import org.broadleafcommerce.gwt.client.presenter.Presenter;
import org.broadleafcommerce.gwt.client.view.catalog.CatalogView;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.smartgwt.client.widgets.Canvas;

public class AppController implements Presenter, ValueChangeHandler<String> {
	
	private static AppController controller = null;

	public static AppController getInstance() {
		if (controller == null) {
			AppController.controller = new AppController();
		}
		return AppController.controller;
	}
	
	private final HandlerManager eventBus = new HandlerManager(null);
	private Canvas container;

	private AppController() {
		bind();
	}

	private void bind() {
		History.addValueChangeHandler(this);

		// eventBus.addHandler(AddContactEvent.TYPE,
		// new AddContactEventHandler() {
		// public void onAddContact(AddContactEvent event) {
		// doAddNewContact();
		// }
		// });
	}

	private void doAddNewContact() {
		History.newItem("add");
	}

	public void go(final Canvas container) {
		this.container = container;

		if ("".equals(History.getToken())) {
			History.newItem("catalog");
		} else {
			History.fireCurrentHistoryState();
		}
	}

	public void onValueChange(ValueChangeEvent<String> event) {
		String token = event.getValue();

		if (token != null) {
			Presenter presenter = null;

			if (token.equals("catalog")) {
				presenter = new CatalogPresenter(eventBus, new CatalogView());
			}

			if (presenter != null) {
				presenter.go(container);
			}
		}
	}
}
