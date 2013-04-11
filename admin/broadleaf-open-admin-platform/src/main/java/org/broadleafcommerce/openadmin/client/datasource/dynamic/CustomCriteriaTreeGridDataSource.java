/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.client.datasource.dynamic;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/15/11
 * Time: 10:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class CustomCriteriaTreeGridDataSource extends TreeGridDataSource {

    protected String[] customCriteria;
    protected boolean useForFetch = false;
    protected boolean useForUpdate = false;
    protected boolean useForAdd = false;
    protected boolean useForRemove = false;
    protected boolean useForInspect = false;

    public CustomCriteriaTreeGridDataSource(String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules, String rootId, String rootName) {
        super(name, persistencePerspective, service, modules, rootId, rootName);
    }

    public CustomCriteriaTreeGridDataSource(String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules, String rootId, String rootName, boolean useForFetch, boolean useForUpdate, boolean useForAdd, boolean useForRemove, boolean useForInspect) {
        super(name, persistencePerspective, service, modules, rootId, rootName);
        this.useForAdd = useForAdd;
        this.useForFetch = useForFetch;
        this.useForInspect = useForInspect;
        this.useForRemove = useForRemove;
        this.useForUpdate = useForUpdate;
    }

    public String[] getCustomCriteria() {
        return customCriteria;
    }

    public void setCustomCriteria(String[] customCriteria) {
        this.customCriteria = customCriteria;
    }

    @Override
    protected void executeFetch(String requestId, DSRequest request, DSResponse response, String[] customCriteria, AsyncCallback<DataSource> cb) {
        if (useForFetch) {
            super.executeFetch(requestId, request, response, this.customCriteria, cb);
        } else {
            super.executeFetch(requestId, request, response, customCriteria, cb);
        }
    }

    @Override
    protected void executeUpdate(String requestId, DSRequest request, DSResponse response, String[] customCriteria, AsyncCallback<DataSource> cb) {
        if (useForUpdate) {
            super.executeUpdate(requestId, request, response, this.customCriteria, cb);
        } else {
            super.executeUpdate(requestId, request, response, customCriteria, cb);
        }
    }

    @Override
    protected void executeAdd(String requestId, DSRequest request, DSResponse response, String[] customCriteria, AsyncCallback<DataSource> cb) {
        if (useForAdd) {
            super.executeAdd(requestId, request, response, this.customCriteria, cb);
        } else {
            super.executeAdd(requestId, request, response, customCriteria, cb);
        }
    }

    @Override
    protected void executeRemove(String requestId, DSRequest request, DSResponse response, String[] customCriteria, AsyncCallback<DataSource> cb) {
        if (useForRemove) {
            super.executeRemove(requestId, request, response, this.customCriteria, cb);
        } else {
            super.executeRemove(requestId, request, response, customCriteria, cb);
        }
    }

    @Override
    public void buildFields(String[] customCriteria, Boolean overrideFieldSort, AsyncCallback<DataSource> cb) {
        if (useForInspect) {
            super.buildFields(this.customCriteria, overrideFieldSort, cb);
        } else {
            super.buildFields(customCriteria, overrideFieldSort, cb);
        }
    }
}
