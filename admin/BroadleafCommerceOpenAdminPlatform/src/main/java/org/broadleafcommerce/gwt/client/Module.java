package org.broadleafcommerce.gwt.client;

import java.util.LinkedHashMap;

public interface Module {

	public String getModuleTitle();
	
	public String getModuleKey();
	
	public LinkedHashMap<String, String[]> getPages();
	
	public void preDraw();
	
	public void postDraw();
	
}
