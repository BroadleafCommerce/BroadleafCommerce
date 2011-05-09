package org.broadleafcommerce.gwt.client;

import java.util.LinkedHashMap;
import java.util.List;

import org.broadleafcommerce.gwt.client.reflection.ModuleFactory;
import org.broadleafcommerce.gwt.client.security.SecurityManager;

import com.google.gwt.core.client.EntryPoint;

public abstract class AbstractModule implements EntryPoint, Module {

	protected LinkedHashMap<String, String[]> pages = new LinkedHashMap<String, String[]>();
	protected String moduleTitle;
	protected String moduleKey;
	
	public void registerModule() {
		BLCMain.addModule(this);
	}
	
	public String getModuleTitle() {
		return moduleTitle;
	}
	
	public void setModuleTitle(String moduleTitle) {
		this.moduleTitle = moduleTitle;
	}
	
	public String getModuleKey() {
		return moduleKey;
	}

	public void setModuleKey(String moduleKey) {
		this.moduleKey = moduleKey;
	}

	public void setSection(
		String sectionTitle, 
		String sectionViewKey, 
		String sectionViewClass,
		String sectionPresenterKey, 
		String sectionPresenterClass,
		List<String> sectionRoles,
		List<String> sectionPermissions
	) {
		pages.put(sectionTitle, new String[]{sectionViewKey, sectionPresenterKey});
		ModuleFactory moduleFactory = ModuleFactory.getInstance();
		moduleFactory.put(sectionViewKey, sectionViewClass);
		moduleFactory.put(sectionPresenterKey, sectionPresenterClass);
		SecurityManager.getInstance().registerSection(sectionViewKey, sectionRoles, sectionPermissions);
	}
	
	public void removeSection(
		String sectionTitle
	) {
		String[] items = pages.remove(sectionTitle);
		ModuleFactory.getInstance().remove(items[0]);
		ModuleFactory.getInstance().remove(items[1]);
	}
	

	public LinkedHashMap<String, String[]> getPages() {
		return pages;
	}

	public void postDraw() {
		//do nothing
	}

	public void preDraw() {
		//do nothing
	}

}
