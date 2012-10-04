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

package org.broadleafcommerce.openadmin.client.datasource;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DataSource;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.ComplexValueMapStructureDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.CustomCriteriaListGridDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.AdornedTargetListClientModule;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.BasicClientEntityModule;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.MapStructureClientModule;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.CollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MapMetadata;
import org.broadleafcommerce.openadmin.client.dto.OperationTypes;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.visitor.MetadataVisitorAdapter;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.service.AppServices;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.MissingResourceException;

/**
 * This factory is responsible for generating a datasource from CollectionMetadata.
 * The config information is derived from @AdminPresentationCollection, @AdminPresentationAdornedTargetCollection,
 * and @AdminPresentationMap annotations that inform collections and maps in the
 * JPA domain.
 *
 * @author Jeff Fischer
 */
public class AdvancedCollectionDataSourceFactory implements DataSourceFactory {

    private CollectionMetadata collectionMetadata;
    private DynamicEntityPresenter presenter;

    public AdvancedCollectionDataSourceFactory(CollectionMetadata collectionMetadata, DynamicEntityPresenter presenter) {
        this.collectionMetadata = collectionMetadata;
        this.presenter = presenter;
    }

    @Override
    public void createDataSource(final String name, OperationTypes operationTypes, final Object[] additionalItems, final AsyncCallback<DataSource> cb) {
        final PersistencePerspective persistencePerspective = collectionMetadata.getPersistencePerspective();
        final List<DataSourceModule> dataSourceModuleList = new ArrayList<DataSourceModule>();
        collectionMetadata.accept(new MetadataVisitorAdapter() {
            @Override
            public void visit(AdornedTargetCollectionMetadata metadata) {
                dataSourceModuleList.add(new BasicClientEntityModule(metadata.getParentObjectClass(), persistencePerspective, AppServices.DYNAMIC_ENTITY));
                dataSourceModuleList.add(new AdornedTargetListClientModule(metadata.getParentObjectClass(), persistencePerspective, AppServices.DYNAMIC_ENTITY));

                DataSourceModule[] modules = new DataSourceModule[dataSourceModuleList.size()];
                modules = dataSourceModuleList.toArray(modules);
                CustomCriteriaListGridDataSource dataSource = new CustomCriteriaListGridDataSource(name, persistencePerspective, AppServices.DYNAMIC_ENTITY, modules);
                if (metadata.getCustomCriteria() != null && metadata.getCustomCriteria().length > 0) {
                    dataSource.setUseForAdd(true);
                    dataSource.setUseForFetch(true);
                    dataSource.setUseForInspect(true);
                    dataSource.setUseForRemove(true);
                    dataSource.setUseForUpdate(true);
                    dataSource.setCustomCriteria(metadata.getCustomCriteria());
                }
                dataSource.buildFields(null, false, cb);
            }

            @Override
            public void visit(BasicCollectionMetadata metadata) {
                dataSourceModuleList.add(new BasicClientEntityModule(metadata.getCollectionCeilingEntity(), persistencePerspective, AppServices.DYNAMIC_ENTITY));

                DataSourceModule[] modules = new DataSourceModule[dataSourceModuleList.size()];
                modules = dataSourceModuleList.toArray(modules);
                CustomCriteriaListGridDataSource dataSource = new CustomCriteriaListGridDataSource(name, persistencePerspective, AppServices.DYNAMIC_ENTITY, modules);
                if (metadata.getCustomCriteria() != null && metadata.getCustomCriteria().length > 0) {
                    dataSource.setUseForAdd(true);
                    dataSource.setUseForFetch(true);
                    dataSource.setUseForInspect(true);
                    dataSource.setUseForRemove(true);
                    dataSource.setUseForUpdate(true);
                    dataSource.setCustomCriteria(metadata.getCustomCriteria());
                }
                dataSource.buildFields(null, false, cb);
            }

            @Override
            public void visit(MapMetadata metadata) {
                String parentObjectClass = ((ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY)).getForeignKeyClass();
                dataSourceModuleList.add(new MapStructureClientModule(parentObjectClass, persistencePerspective, AppServices.DYNAMIC_ENTITY));

                DataSourceModule[] modules = new DataSourceModule[dataSourceModuleList.size()];
                modules = dataSourceModuleList.toArray(modules);

                CustomCriteriaListGridDataSource dataSource;
                if (metadata.isSimpleValue()) {
                    dataSource = new CustomCriteriaListGridDataSource(name, persistencePerspective, AppServices.DYNAMIC_ENTITY, modules);
                } else {
                    if (metadata.getMapKeyOptionEntityClass() == null || metadata.getMapKeyOptionEntityClass().length() == 0) {
                        LinkedHashMap<String, String> keys = new LinkedHashMap<String, String>();
                        for (String[] key : metadata.getKeys()) {
                            String temp;
                            try {
                                temp = BLCMain.getMessageManager().getString(key[1]);
                            } catch (MissingResourceException e) {
                                temp = key[1];
                            }
                            keys.put(key[0], temp);
                        }
                        dataSource = new ComplexValueMapStructureDataSource(name, persistencePerspective, AppServices.DYNAMIC_ENTITY, modules, keys);
                    } else {
                        dataSource = new ComplexValueMapStructureDataSource(name, persistencePerspective, AppServices.DYNAMIC_ENTITY, modules, presenter.getPresenterSequenceSetupManager(), name + "Lookup", metadata.getMapKeyOptionEntityDisplayField(), metadata.getMapKeyOptionEntityValueField());
                    }
                }
                if (metadata.getCustomCriteria() != null && metadata.getCustomCriteria().length > 0) {
                    dataSource.setUseForAdd(true);
                    dataSource.setUseForFetch(true);
                    dataSource.setUseForInspect(true);
                    dataSource.setUseForRemove(true);
                    dataSource.setUseForUpdate(true);
                    dataSource.setCustomCriteria(metadata.getCustomCriteria());
                }
                dataSource.buildFields(null, false, cb);
            }
        });
    }
}
