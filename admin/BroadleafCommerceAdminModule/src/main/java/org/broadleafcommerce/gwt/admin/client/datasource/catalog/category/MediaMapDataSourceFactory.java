package org.broadleafcommerce.gwt.admin.client.datasource.catalog.category;

import java.util.LinkedHashMap;

import org.broadleafcommerce.gwt.admin.client.datasource.CeilingEntities;
import org.broadleafcommerce.gwt.admin.client.datasource.EntityImplementations;
import org.broadleafcommerce.gwt.admin.client.presenter.catalog.category.CategoryPresenter;
import org.broadleafcommerce.gwt.admin.client.view.catalog.category.CategoryDisplay;
import org.broadleafcommerce.gwt.client.datasource.DataSourceFactory;
import org.broadleafcommerce.gwt.client.datasource.dynamic.ComplexValueMapStructureDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.gwt.client.datasource.dynamic.module.MapStructureModule;
import org.broadleafcommerce.gwt.client.datasource.relations.ForeignKey;
import org.broadleafcommerce.gwt.client.datasource.relations.MapStructure;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspectiveItemType;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationType;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationTypes;
import org.broadleafcommerce.gwt.client.service.AppServices;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;

public class MediaMapDataSourceFactory implements DataSourceFactory {
	
	public static final MapStructure MAPSTRUCTURE = new MapStructure(String.class.getName(), "key", "Key", EntityImplementations.MEDIA, "categoryMedia", true);
	public static ComplexValueMapStructureDataSource dataSource = null;
	
	protected CategoryPresenter presenter;
	
	public MediaMapDataSourceFactory(CategoryPresenter presenter) {
		this.presenter = presenter;
	}
	
	@SuppressWarnings("unchecked")
	public void createDataSource(String name, OperationTypes operationTypes, Object[] additionalItems, AsyncCallback<DataSource> cb) {
		if (dataSource == null) {
			operationTypes = new OperationTypes(OperationType.MAPSTRUCTURE, OperationType.MAPSTRUCTURE, OperationType.MAPSTRUCTURE, OperationType.MAPSTRUCTURE, OperationType.MAPSTRUCTURE);
			PersistencePerspective persistencePerspective = new PersistencePerspective(operationTypes, new String[]{}, new ForeignKey[]{});
			persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.FOREIGNKEY, new ForeignKey("id", EntityImplementations.CATEGORY, null));
			persistencePerspective.addPersistencePerspectiveItem(PersistencePerspectiveItemType.MAPSTRUCTURE, MAPSTRUCTURE);
			DataSourceModule[] modules = new DataSourceModule[]{
				new MapStructureModule(CeilingEntities.CATEGORY, persistencePerspective, AppServices.DYNAMIC_ENTITY, ((CategoryDisplay) presenter.getDisplay()).getMediaDisplay().getGrid())
			};
			dataSource = new ComplexValueMapStructureDataSource(name, persistencePerspective, AppServices.DYNAMIC_ENTITY, modules, (LinkedHashMap<String, String>) additionalItems[0]);
			dataSource.buildFields(null, false, cb);
		} else {
			if (cb != null) {
				cb.onSuccess(dataSource);
			}
		}
	}

}
