package org.broadleafcommerce.openadmin.client.datasource.dynamic;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

public class CustomCriteriaTileGridDataSource extends TileGridDataSource {

    protected String[] customCriteria;
    protected boolean useForFetch = false;
    protected boolean useForUpdate = false;
    protected boolean useForAdd = false;
    protected boolean useForRemove = false;
    protected boolean useForInspect = false;

    public CustomCriteriaTileGridDataSource(String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules) {
        super(name, persistencePerspective, service, modules);
    }

    public CustomCriteriaTileGridDataSource(String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules, boolean useForFetch, boolean useForUpdate, boolean useForAdd, boolean useForRemove, boolean useForInspect) {
        super(name, persistencePerspective, service, modules);
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
