package org.broadleafcommerce.gwt.cms.client;

import org.broadleafcommerce.gwt.client.BLCMain;
import org.broadleafcommerce.gwt.client.reflection.ModuleFactory;

public class CMSMain extends BLCMain {
	
	public CMSMain() {
		super();
		pages.put("CMS", new String[]{"cms", "cmsPresenter"});
		
		ModuleFactory moduleFactory = ModuleFactory.getInstance();
		moduleFactory.put("cms", "org.broadleafcommerce.gwt.cms.client.view.cms.CmsView");
		moduleFactory.put("cmsPresenter", "org.broadleafcommerce.gwt.cms.client.presenter.cms.CmsPresenter");
	}

}
