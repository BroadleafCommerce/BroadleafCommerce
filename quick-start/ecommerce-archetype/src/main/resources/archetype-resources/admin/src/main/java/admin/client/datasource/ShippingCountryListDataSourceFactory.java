#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${artifactId}.client.datasource;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;
import org.broadleafcommerce.open${artifactId}.client.datasource.DataSourceFactory;
import org.broadleafcommerce.open${artifactId}.client.datasource.dynamic.ListGridDataSource;
import org.broadleafcommerce.open${artifactId}.client.datasource.dynamic.module.BasicClientEntityModule;
import org.broadleafcommerce.open${artifactId}.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.open${artifactId}.client.dto.*;
import org.broadleafcommerce.open${artifactId}.client.service.AppServices;

/**
 * 
 * @author jfischer
 *
 */
public class ShippingCountryListDataSourceFactory implements DataSourceFactory {

	public static final String foreignKeyName = "products";
	public static ListGridDataSource dataSource = null;
	
	public void createDataSource(String name, OperationTypes operationTypes, Object[] additionalItems, AsyncCallback<DataSource> cb) {
		if (dataSource == null) {
			operationTypes = new OperationTypes(OperationType.ENTITY, OperationType.FOREIGNKEY, OperationType.FOREIGNKEY, OperationType.ENTITY, OperationType.ENTITY);
			PersistencePerspective persistencePerspective = new PersistencePerspective(operationTypes, new String[]{}, new ForeignKey[]{});
			persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.FOREIGNKEY, new ForeignKey(foreignKeyName, EntityImplementations.PRODUCT, null));
			persistencePerspective.setPopulateToOneFields(true);

			DataSourceModule[] modules = new DataSourceModule[]{
				new BasicClientEntityModule(CeilingEntities.SHIPPING_COUNTRY, persistencePerspective, AppServices.DYNAMIC_ENTITY)
			};
			dataSource = new ListGridDataSource(name, persistencePerspective, AppServices.DYNAMIC_ENTITY, modules);
			dataSource.buildFields(null, false, cb);
		} else {
			if (cb != null) {
				cb.onSuccess(dataSource);
			}
		}
	}

}
