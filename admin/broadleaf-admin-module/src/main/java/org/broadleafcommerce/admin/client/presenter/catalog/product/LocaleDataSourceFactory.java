package org.broadleafcommerce.admin.client.presenter.catalog.product;

import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.openadmin.client.datasource.SimpleDataSourceFactory;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.CustomCriteriaListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.service.AppServices;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;

public class LocaleDataSourceFactory extends SimpleDataSourceFactory{

    public LocaleDataSourceFactory(String entityName)  {
        super(entityName);
    }

    @Override
    public PersistencePerspective setupPersistencePerspective(PersistencePerspective persistencePerspective) {
        persistencePerspective.setOperationTypes(new OperationTypes(OperationType.BASIC, OperationType.BASIC, OperationType.BASIC, OperationType.BASIC, OperationType.BASIC));
        return persistencePerspective;
    }
    
    @Override
    public void createDataSource(String name, OperationTypes operationTypes, Object[] additionalItems, AsyncCallback<DataSource> cb) {
        PersistencePerspective persistencePerspective = createPersistencePerspective();
        DataSourceModule[] modules = createDataSourceModules().toArray(new DataSourceModule[0]);
        CustomCriteriaListGridDataSource dataSource = new CustomCriteriaListGridDataSource(name, persistencePerspective, AppServices.DYNAMIC_ENTITY, modules, true, true, true, true, true);
      //  dataSource.setCustomCriteria(new String[]{"createNewPermission"});
        dataSource.buildFields(null, false, cb);
    }
}
