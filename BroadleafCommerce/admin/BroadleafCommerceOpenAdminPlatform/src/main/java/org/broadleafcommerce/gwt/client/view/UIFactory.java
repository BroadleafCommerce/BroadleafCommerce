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

import java.util.HashMap;
import java.util.Stack;

import org.broadleafcommerce.gwt.client.presenter.entity.EntityPresenter;
import org.broadleafcommerce.gwt.client.reflection.ModuleFactory;

/**
 * 
 * @author jfischer
 *
 */
public class UIFactory extends HashMap<String, Display> {

	private static final long serialVersionUID = 1L;
	
	private Stack<Display> currentView = new Stack<Display>();
	private Stack<String> keyStack = new Stack<String>();

	public Display getView(String value) {
		return getView(value, true, true);
	}
	
	public Display getView(String value, boolean clearCurrentView, boolean storeView) {
		if (clearCurrentView) {
			clearCurrentView();
		}
		Display view;
		if (!containsKey(value)) {
			view = (Display) ModuleFactory.getInstance().createItem(value);
			if (storeView) {
				put(value, view);
			}
		} else {
			view = get(value);
		}
		currentView.push(view);
		keyStack.push(value);
		
		return view;
	}
	
	public EntityPresenter getPresenter(String value) {
		return (EntityPresenter) ModuleFactory.getInstance().createItem(value);
	}
	
	public void clearCurrentView() {
		for (Display display : currentView) {
			display.destroy();
		}
		currentView.clear();
		keyStack.clear();
	}

	public boolean equalsCurrentView(String key) {
		return !(keyStack.isEmpty() || !keyStack.peek().equals(key));
	}

}
