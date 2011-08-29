package org.broadleafcommerce.cms.admin.client.datasource.pages;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;

/**
 * Created by jfischer
 */
public class PageTemplateFormListDataSource extends DynamicEntityDataSource {

    protected String[] customCriteria;

    public PageTemplateFormListDataSource(String name, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, DataSourceModule[] modules) {
        super(name, persistencePerspective, service, modules);
    }

    public String[] getCustomCriteria() {
        return customCriteria;
    }

    public void setCustomCriteria(String[] customCriteria) {
        this.customCriteria = customCriteria;
    }

    @Override
    protected void executeFetch(String requestId, DSRequest request, DSResponse response, String[] customCriteria, AsyncCallback<DataSource> cb) {
        super.executeFetch(requestId, request, response, this.customCriteria, cb);
    }

    @Override
    protected void executeUpdate(String requestId, DSRequest request, DSResponse response, String[] customCriteria, AsyncCallback<DataSource> cb) {
        super.executeUpdate(requestId, request, response, this.customCriteria, cb);
    }
}
