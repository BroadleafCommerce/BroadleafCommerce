package org.broadleafcommerce.gwt.client.view;

import java.util.HashMap;
import java.util.Stack;

public class ViewLibrary<ViewClass extends AbstractDisplay> extends HashMap<String, ViewClass> {

	private static final long serialVersionUID = 1L;
	
	private Stack<AbstractDisplay> currentView = new Stack<AbstractDisplay>();

	public ViewClass getView(String value, ViewClass instance) {
		return getView(value, instance, true);
	}
	
	public ViewClass getView(String value, ViewClass instance, boolean clearCurrentView) {
		if (clearCurrentView) {
			clearCurrentView();
		}
		ViewClass view;
		if (!containsKey(value)) {
			put(value, instance);
			view = instance;
		} else {
			view = get(value);
		}
		currentView.push(view);
		
		return view;
	}
	
	public void clearCurrentView() {
		for (AbstractDisplay display : currentView) {
			display.clear();
		}
		currentView.clear();
	}

	public boolean equalsCurrentViewClass(String viewName) {
		return !(currentView == null || !currentView.getClass().getName().equals(viewName));
	}

}
