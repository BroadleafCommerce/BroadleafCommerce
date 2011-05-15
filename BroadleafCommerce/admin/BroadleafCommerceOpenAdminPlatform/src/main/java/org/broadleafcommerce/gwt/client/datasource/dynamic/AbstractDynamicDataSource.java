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
package org.broadleafcommerce.gwt.client.datasource.dynamic;

import java.util.HashMap;

import org.broadleafcommerce.gwt.client.datasource.GwtRpcDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.presenter.entity.FormItemCallbackHandlerManager;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.CriteriaPolicy;

/**
 * 
 * @author jfischer
 *
 */
public abstract class AbstractDynamicDataSource extends GwtRpcDataSource {

	protected HashMap<String, String> polymorphicEntities = new HashMap<String, String>();
	protected String defaultNewEntityFullyQualifiedClassname;
	protected DynamicEntityServiceAsync service;
	protected PersistencePerspective persistencePerspective;
	protected DataSourceModule[] modules;
	protected FormItemCallbackHandlerManager formItemCallbackHandlerManager = new FormItemCallbackHandlerManager();
	
	/**
	 * @param name
	 */
	public AbstractDynamicDataSource(String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules) {
		super(name);
		setCriteriaPolicy(CriteriaPolicy.DROPONCHANGE);
		setCacheMaxAge(0);
		this.service = service;
		this.persistencePerspective = persistencePerspective;
		for (DataSourceModule module : modules) {
			module.setDataSource(this);
		}
		this.modules = modules;
	}

	public HashMap<String, String> getPolymorphicEntities() {
		return polymorphicEntities;
	}
	
	public String stripDuplicateAllowSpecialCharacters(String string) {
    	if (string != null) {
			int index = string.indexOf("_^_");
	        if (index >= 0) {
	        	string = string.substring(0,index);
	        }
    	}
        return string;
	}
	
	public String getDefaultNewEntityFullyQualifiedClassname() {
		return defaultNewEntityFullyQualifiedClassname;
	}

	public void setDefaultNewEntityFullyQualifiedClassname(String defaultNewEntityFullyQualifiedClassname) {
		this.defaultNewEntityFullyQualifiedClassname = defaultNewEntityFullyQualifiedClassname;
	}
	
	public FormItemCallbackHandlerManager getFormItemCallbackHandlerManager() {
		return formItemCallbackHandlerManager;
	}

	public void setFormItemCallbackHandlerManager(FormItemCallbackHandlerManager formItemCallbackHandlerManager) {
		this.formItemCallbackHandlerManager = formItemCallbackHandlerManager;
	}
	
	public String getPrimaryKeyValue(Record record) {
		String primaryKey = getPrimaryKeyFieldName();
		return record.getAttribute(primaryKey);
	}
}
