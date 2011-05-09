package org.broadleafcommerce.gwt.client.datasource.dynamic;

import java.util.HashMap;

import org.broadleafcommerce.gwt.client.datasource.GwtRpcDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.presenter.entity.FormItemCallbackHandlerManager;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.CriteriaPolicy;

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
			int index = string.indexOf("_");
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
