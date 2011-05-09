package org.broadleafcommerce.gwt.client.view;

import java.util.HashMap;
import java.util.Stack;

import org.broadleafcommerce.gwt.client.presenter.Presenter;
import org.broadleafcommerce.gwt.client.reflection.ModuleFactory;

public class UIFactory extends HashMap<String, Display> {

	private static final long serialVersionUID = 1L;
	
	private Stack<Display> currentView = new Stack<Display>();
	private Stack<String> keyStack = new Stack<String>();

	public Display getView(String value) {
		return getView(value, true);
	}
	
	public Display getView(String value, boolean clearCurrentView) {
		if (clearCurrentView) {
			clearCurrentView();
		}
		Display view;
		if (!containsKey(value)) {
			view = (Display) ModuleFactory.getInstance().createItem(value);
			put(value, view);
		} else {
			view = get(value);
		}
		currentView.push(view);
		keyStack.push(value);
		
		return view;
	}
	
	public Presenter getPresenter(String value) {
		return (Presenter) ModuleFactory.getInstance().createItem(value);
	}
	
	public void clearCurrentView() {
		for (Display display : currentView) {
			display.clear();
		}
		currentView.clear();
		keyStack.clear();
	}

	public boolean equalsCurrentView(String key) {
		return !(keyStack.isEmpty() || !keyStack.peek().equals(key));
	}

}
