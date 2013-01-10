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

package org.broadleafcommerce.openadmin.client.datasource.dynamic;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.CriteriaPolicy;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.GwtRpcDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.ClassTree;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.presenter.entity.FormItemCallbackHandlerManager;
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
    //TODO change this flag to come from an annotation on the entity that defines the commit status from the server side
    //protected boolean commitImmediately = true;
    protected Record addedRecord;
    
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
}
