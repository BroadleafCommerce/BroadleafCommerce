package org.broadleafcommerce.gwt.client.view;

import java.util.HashMap;
import java.util.Stack;

import org.broadleafcommerce.gwt.client.presenter.entity.EntityPresenter;
import org.broadleafcommerce.gwt.client.reflection.ModuleFactory;

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
