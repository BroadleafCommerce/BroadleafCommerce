/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client;

import org.broadleafcommerce.openadmin.client.dto.Section;
import org.broadleafcommerce.openadmin.client.reflection.ModuleFactory;
import org.broadleafcommerce.openadmin.client.security.SecurityManager;

import com.google.gwt.core.client.EntryPoint;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 
 * @author jfischer
 *
 */
public abstract class AbstractModule implements EntryPoint, Module {

	protected LinkedHashMap<String, String[]> pages = new LinkedHashMap<String, String[]>();
	protected String moduleTitle;
	protected String moduleKey;
    protected Integer order = Integer.MAX_VALUE;
	protected List<Section> sections = new ArrayList<Section>();
	
	public void registerModule() {
		BLCMain.addModule(this);
	}
	
	@Override
    public String getModuleTitle() {
		return moduleTitle;
	}
	
	public void setModuleTitle(String moduleTitle) {
		this.moduleTitle = moduleTitle;
        if (moduleKey == null) {
            moduleKey = moduleTitle;
        }
	}
	
	@Override
    public String getModuleKey() {
		return moduleKey;
	}

	/**
	 * If 2 modules have the same key, the sections in that module are merged together. For instance, if you wanted to add
	 * a new section to the merchandising module, you would create a new AbstractModule subclass and call this method
	 * with the same key as the Merchandising module
	 * 
	 * @param moduleKey
	 */
	public void setModuleKey(String moduleKey) {
		this.moduleKey = moduleKey;
	}

    public void addConstants(i18nConstants constants) {
        BLCMain.MESSAGE_MANAGER.addConstants(constants);
    }

	public void setSection(
		String sectionTitle, 
		String sectionViewKey, 
		String sectionViewClass,
		String sectionPresenterKey, 
		String sectionPresenterClass,
		List<String> sectionPermissions
	) {
	    sections.add(new Section(sectionTitle,sectionViewKey,sectionViewClass,sectionPresenterKey,sectionPresenterClass,sectionPermissions));
		pages.put(sectionTitle, new String[]{sectionViewKey, sectionPresenterKey});
		ModuleFactory moduleFactory = ModuleFactory.getInstance();
		moduleFactory.put(sectionViewKey, sectionViewClass);
		moduleFactory.put(sectionPresenterKey, sectionPresenterClass);
		SecurityManager.getInstance().registerSection(this.moduleKey, sectionViewKey, sectionPermissions);
	}
	
	public void setSecurity(
		String sectionViewKey,
		List<String> sectionPermissions
	) {
		SecurityManager.getInstance().registerSection(this.moduleKey, sectionViewKey, sectionPermissions);
	}
	
	public void removeSection(
		String sectionTitle
	) {
		String[] items = pages.remove(sectionTitle);
		ModuleFactory.getInstance().remove(items[0]);
		ModuleFactory.getInstance().remove(items[1]);
	}
	

	@Override
    public LinkedHashMap<String, String[]> getPages() {
		return pages;
	}

	@Override
    public void postDraw() {
		//do nothing
	}

	@Override
    public void preDraw() {
		//do nothing
	}

    @Override
    public Integer getOrder() {
        return order;
    }

    @Override
    public void setOrder(Integer order) {
        this.order = order;
    }

    
    protected List<Section> getSections() {
        return sections;
    }

    
    protected void setSections(List<Section> sections) {
        this.sections = sections;
    }

    @Override
    public void onModuleLoad() {
        // TODO Auto-generated method stub
        
    }
}
