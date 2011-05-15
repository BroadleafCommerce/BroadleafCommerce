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
package org.broadleafcommerce.gwt.client.setup;

import java.util.HashMap;

import org.broadleafcommerce.gwt.client.presenter.entity.EntityPresenter;
import org.broadleafcommerce.gwt.client.security.SecurityManager;
import org.broadleafcommerce.gwt.client.view.Display;
import org.broadleafcommerce.gwt.client.view.UIFactory;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.smartgwt.client.widgets.Canvas;

/**
 * 
 * @author jfischer
 *
 */
public class AppController implements ValueChangeHandler<String> {

	private static AppController controller = null;

	public static AppController getInstance() {
		if (controller == null) {
			AppController.controller = new AppController();
		}
		return AppController.controller;
	}

	private final HandlerManager eventBus = new HandlerManager(null);
	private Canvas container;
	private UIFactory uiFactory = new UIFactory();
	private HashMap<String, String[]> pages;

	private AppController() {
		bind();
	}

	private void bind() {
		History.addValueChangeHandler(this);
	}

	public void go(final Canvas container, HashMap<String, String[]> pages) {
		this.pages = pages;
		this.container = container;

		if ("".equals(History.getToken())) {
			for (String sectionTitle : pages.keySet()){
				if (SecurityManager.getInstance().isUserAuthorizedToViewSection(pages.get(sectionTitle)[0])){
					History.newItem(sectionTitle);
					break;
				}
			}
		} else {
			History.fireCurrentHistoryState();
		}
	}

	public HandlerManager getEventBus() {
		return eventBus;
	}

	public void onValueChange(ValueChangeEvent<String> event) {
		String token = event.getValue();

		if (token != null) {
			if (!uiFactory.equalsCurrentView(token)) {
				String[] vals = pages.get(token);
				showView(vals[0], vals[1]);
			}
		}
	}
	
	protected void showView(String viewKey, String presenterKey) {
		if (SecurityManager.getInstance().isUserAuthorizedToViewSection(viewKey)){
			uiFactory.clearCurrentView();
			Display view = uiFactory.getView(viewKey, false, false);
			EntityPresenter presenter = uiFactory.getPresenter(presenterKey);
			presenter.setDisplay(view);
			presenter.setEventBus(eventBus);
			presenter.getPresenterSequenceSetupManager().setCanvas(container);
			presenter.setup();
			presenter.getPresenterSequenceSetupManager().launch();
		}
	}
}
