package org.broadleafcommerce.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;

public class BLCLaunch implements EntryPoint {

	public void onModuleLoad() {
		String moduleParam = Window.Location.getParameter("defaultModule");
		BLCMain.drawCurrentState(moduleParam);
	}

}
