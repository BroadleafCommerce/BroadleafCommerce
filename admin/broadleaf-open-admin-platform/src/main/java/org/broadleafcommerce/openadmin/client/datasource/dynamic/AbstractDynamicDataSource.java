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

package org.broadleafcommerce.openadmin.client.datasource.dynamic;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.CriteriaPolicy;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.GwtRpcDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.BasicClientEntityModule;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.ClassTree;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.presenter.entity.FormItemCallbackHandlerManager;
import org.broadleafcommerce.openadmin.client.service.AppServices;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.MissingResourceException;

/**
 * 
 * @author jfischer
 *
 */
public abstract class AbstractDynamicDataSource extends GwtRpcDataSource {

	protected LinkedHashMap<String, String> polymorphicEntities = new LinkedHashMap<String, String>(10);
    protected ClassTree polymorphicEntityTree;
	protected String defaultNewEntityFullyQualifiedClassname;
	protected DynamicEntityServiceAsync service;
	protected PersistencePerspective persistencePerspective;
	protected DataSourceModule[] modules;
	protected FormItemCallbackHandlerManager formItemCallbackHandlerManager = new FormItemCallbackHandlerManager();
    protected Record addedRecord;
    protected boolean showArchived = false;


    /**
     * Sets the criteria policy and max age to use for this datasource.
     */
    private void setDefaults() {
        setCriteriaPolicy(CriteriaPolicy.DROPONCHANGE);
        setCacheMaxAge(0);
    }

    /**
     * Typical constructor used to initialize a Broadleaf Entity Backed Datasource.
     *
      * Creates a datasource capable of all CRUD operations on the passed in Entity.
      * This constructor handles the simplest needs for an Entity Datasource which is
      * what is called for 70% of the time.    For more advanced needs involving
      * Foreign Keys, Lists, and Maps, the more advanced constructor is required.
      *
      * @param ceilingEntityClassName - The fully qualified name of the ceilingEntity.
      */
    public AbstractDynamicDataSource(String ceilingEntityClassName) {
        // Note that the name field while it is required for GwtRpcParent class, it is not actually used
        super("nameNotSet");
        this.persistencePerspective = new PersistencePerspective();
        this.service = AppServices.DYNAMIC_ENTITY;

        // Setup default entity module.   Good for most simple list/form based entities.
        modules = new DataSourceModule[1];
        modules[0] = new BasicClientEntityModule(ceilingEntityClassName, this.persistencePerspective, this.service);
        modules[0].setDataSource(this);
    }   
	
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

	public LinkedHashMap<String, String> getPolymorphicEntities() {
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

    public ClassTree getPolymorphicEntityTree() {
        return polymorphicEntityTree;
    }

    public void setPolymorphicEntityTree(ClassTree polymorphicEntityTree) {
        this.polymorphicEntityTree = polymorphicEntityTree;
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(polymorphicEntityTree.getRight()/2);
        buildPolymorphicEntityMap(polymorphicEntityTree, map);
        Map.Entry[] entries = new Map.Entry[map.size()];
        entries = map.entrySet().toArray(entries);
        //reverse the order
        for (int i = entries.length - 1; i >= 0; i--) {
            polymorphicEntities.put((String) entries[i].getKey(), (String) entries[i].getValue());
        }
    }

    protected void buildPolymorphicEntityMap(ClassTree entity, LinkedHashMap<String, String> map) {
        String friendlyName = entity.getFriendlyName();
        if (friendlyName != null && !friendlyName.equals("")) {
            //check if the friendly name is an i18N key
            try {
                String val = BLCMain.getMessageManager().getString(friendlyName);
                if (val != null) {
                    friendlyName = val;
                }
            } catch (MissingResourceException e) {
                //do nothing
            }
        }
        map.put(entity.getFullyQualifiedClassname(), friendlyName!=null?friendlyName:entity.getName());
        for (ClassTree child : entity.getChildren()) {
            buildPolymorphicEntityMap(child, map);
        }
    }

    public Record getAddedRecord() {
        return addedRecord;
    }

    public void setAddedRecord(Record addedRecord) {
        this.addedRecord = addedRecord;
    }

    public boolean isShowArchived() {
        return showArchived;
    }

    public void setShowArchived(boolean showArchived) {
        this.showArchived = showArchived;
    }

    public PersistencePerspective getPersistencePerspective() {
        return persistencePerspective;
    }

    public DataSourceModule[] getModules() {
        return modules;
    }

    public void setModules(DataSourceModule[] modules) {
        this.modules = modules;
    }

    public DynamicEntityServiceAsync getService() {
        return service;
    }

    public void setService(DynamicEntityServiceAsync service) {
        this.service = service;
    }
}
