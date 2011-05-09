package org.broadleafcommerce.gwt.client.view;

import com.google.gwt.core.client.JavaScriptObject;

public class TabSet extends com.smartgwt.client.widgets.tab.TabSet {

	/**
	 * 
	 */
	public TabSet() {
		super();
	}

	/**
	 * @param jsObj
	 */
	public TabSet(JavaScriptObject jsObj) {
		super(jsObj);
	}

	public void setPaneMargin(int margin) {
		setAttribute("paneMargin", String.valueOf(margin), false);
	}
	
}
