package org.broadleafcommerce.gwt.client.datasource.dynamic.module;

import org.broadleafcommerce.gwt.client.Main;
import org.broadleafcommerce.gwt.client.datasource.dynamic.operation.EntityOperationType;
import org.broadleafcommerce.gwt.client.datasource.dynamic.operation.EntityServiceAsyncCallback;
import org.broadleafcommerce.gwt.client.datasource.relations.MapStructure;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspectiveItemType;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationType;
import org.broadleafcommerce.gwt.client.datasource.results.ClassMetadata;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.MergedPropertyType;
import org.broadleafcommerce.gwt.client.datasource.results.PolymorphicEntity;
import org.broadleafcommerce.gwt.client.datasource.results.Property;
import org.broadleafcommerce.gwt.client.service.AbstractCallback;
import org.broadleafcommerce.gwt.client.service.AppServices;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtincubator.security.exception.ApplicationSecurityException;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.tree.TreeNode;

public class ComplexValueMapStructureModule extends BasicEntityModule {

	protected ListGrid associatedGrid;
	
	public ComplexValueMapStructureModule(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service, ListGrid associatedGrid) {
		super(ceilingEntityFullyQualifiedClassname, persistencePerspective, service);
		this.associatedGrid = associatedGrid;
	}

	@Override
	public void executeFetch(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb) {
		CriteriaTransferObject criteriaTransferObject = getCto(request);
		final String parentCategoryId = criteriaTransferObject.get("id").getFilterValues()[0];
		service.fetch(ceilingEntityFullyQualifiedClassname, criteriaTransferObject, persistencePerspective, customCriteria, new EntityServiceAsyncCallback<DynamicResultSet>(EntityOperationType.FETCH, requestId, request, response, dataSource) {
			public void onSuccess(DynamicResultSet result) {
				super.onSuccess(result);
				TreeNode[] recordList = buildRecords(result, null);
				MapStructure mapStructure = (MapStructure) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.MAPSTRUCTURE);
				for (TreeNode node : recordList) {
					node.setAttribute("symbolicId", parentCategoryId);
					node.setAttribute("priorKey", node.getAttribute(mapStructure.getKeyPropertyName()));
				}
				response.setData(recordList);
				response.setTotalRows(result.getTotalRecords());
				if (cb != null) {
					cb.onSuccess(dataSource);
				}
				dataSource.processResponse(requestId, response);
			}
		});
	}	
	
	@Override
	public void executeUpdate(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb) {
		JavaScriptObject data = request.getData();
        final ListGridRecord temp = new ListGridRecord(data);
        Entity tempEntity = buildEntity(temp);
        final ListGridRecord record = associatedGrid.getRecord(associatedGrid.getRecordIndex(temp));
    	Entity entity = buildEntity(record);
    	for (Property property : tempEntity.getProperties()) {
    		entity.findProperty(property.getName()).setValue(property.getValue());
    	}
        String componentId = request.getComponentId();
        if (componentId != null) {
            if (entity.getType() == null) {
            	String[] type = ((ListGrid) Canvas.getById(componentId)).getSelectedRecord().getAttributeAsStringArray("type");
            	entity.setType(type);
            }
        }
		service.update(entity, persistencePerspective, customCriteria, new EntityServiceAsyncCallback<Entity>(EntityOperationType.UPDATE, requestId, request, response, dataSource) {
			public void onSuccess(Entity result) {
				super.onSuccess(result);
				ListGridRecord myRecord = (ListGridRecord) updateRecord(result, (Record) temp, false);
				ListGridRecord[] recordList = new ListGridRecord[]{myRecord};
				response.setData(recordList);
				response.setTotalRows(1);
				/*
				 * An update can result in the removal of a value, which would make the cache out-of-sync
				 * with the database. Refresh the cache to make sure the display values are accurate.
				 */
				response.setInvalidateCache(true);
				if (cb != null) {
					cb.onSuccess(dataSource);
				}
				dataSource.processResponse(requestId, response);
			}
			
			@Override
			protected void onSecurityException(ApplicationSecurityException exception) {
				super.onSecurityException(exception);
				if (cb != null) {
					cb.onFailure(exception);
				}
			}

			@Override
			protected void onOtherException(Throwable exception) {
				super.onOtherException(exception);
				if (cb != null) {
					cb.onFailure(exception);
				}
			}

			@Override
			protected void onError(EntityOperationType opType, String requestId, DSRequest request, DSResponse response, Throwable caught) {
				super.onError(opType, requestId, request, response, caught);
				if (cb != null) {
					cb.onFailure(caught);
				}
			}
		});
	}
	
	@Override
	public void executeRemove(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb) {
		JavaScriptObject data = request.getData();
        final ListGridRecord temp = new ListGridRecord(data);
        Entity tempEntity = buildEntity(temp);
        final ListGridRecord record = associatedGrid.getRecord(associatedGrid.getRecordIndex(temp));
    	Entity entity = buildEntity(record);
    	for (Property property : tempEntity.getProperties()) {
    		entity.findProperty(property.getName()).setValue(property.getValue());
    	}
        String componentId = request.getComponentId();
        if (componentId != null) {
            if (entity.getType() == null) {
            	String[] type = ((ListGrid) Canvas.getById(componentId)).getSelectedRecord().getAttributeAsStringArray("type");
            	entity.setType(type);
            }
        }
        service.remove(entity, persistencePerspective, customCriteria, new EntityServiceAsyncCallback<Void>(EntityOperationType.REMOVE, requestId, request, response, dataSource) {
			public void onSuccess(Void item) {
				super.onSuccess(null);
				if (cb != null) {
					cb.onSuccess(dataSource);
				}
				dataSource.processResponse(requestId, response);
			}
			
			@Override
			protected void onSecurityException(ApplicationSecurityException exception) {
				super.onSecurityException(exception);
				if (cb != null) {
					cb.onFailure(exception);
				}
			}

			@Override
			protected void onOtherException(Throwable exception) {
				super.onOtherException(exception);
				if (cb != null) {
					cb.onFailure(exception);
				}
			}

			@Override
			protected void onError(EntityOperationType opType, String requestId, DSRequest request, DSResponse response, Throwable caught) {
				super.onError(opType, requestId, request, response, caught);
				if (cb != null) {
					cb.onFailure(caught);
				}
			}
		});
	}
	
	@Override
	public void executeAdd(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb) {
		Main.NON_MODAL_PROGRESS.startProgress();
		JavaScriptObject data = request.getData();
        TreeNode record = new TreeNode(data);
        Entity entity = buildEntity(record);
        service.add(ceilingEntityFullyQualifiedClassname, entity, persistencePerspective, customCriteria, new EntityServiceAsyncCallback<Entity>(EntityOperationType.ADD, requestId, request, response, dataSource) {
			public void onSuccess(Entity result) {
				super.onSuccess(result);
				TreeNode record = (TreeNode) buildRecord(result, false);
				TreeNode[] recordList = new TreeNode[]{record};
				response.setData(recordList);
				/*
				 * If the key is a duplicate, it can result in the deletion of the old value
				 * and the creation of a new value, which can result in a new id for the retured
				 * value. Therefore, we need to invalidate the cache to make sure the displayed
				 * values are correct.
				 */
				response.setInvalidateCache(true);
				if (cb != null) {
					cb.onSuccess(dataSource);
				}
				dataSource.processResponse(requestId, response);
			}
			
			@Override
			protected void onSecurityException(ApplicationSecurityException exception) {
				super.onSecurityException(exception);
				if (cb != null) {
					cb.onFailure(exception);
				}
			}

			@Override
			protected void onOtherException(Throwable exception) {
				super.onOtherException(exception);
				if (cb != null) {
					cb.onFailure(exception);
				}
			}

			@Override
			protected void onError(EntityOperationType opType, String requestId, DSRequest request, DSResponse response, Throwable caught) {
				super.onError(opType, requestId, request, response, caught);
				if (cb != null) {
					cb.onFailure(caught);
				}
			}
		});
	}

	@Override
	public void buildFields(final String[] customCriteria, final AsyncCallback<DataSource> cb) {
		Main.NON_MODAL_PROGRESS.startProgress();
		AppServices.DYNAMIC_ENTITY.inspect(ceilingEntityFullyQualifiedClassname, persistencePerspective, customCriteria, metadataOverrides, new AbstractCallback<DynamicResultSet>() {
			
			@Override
			protected void onOtherException(Throwable exception) {
				super.onOtherException(exception);
				cb.onFailure(exception);
			}

			@Override
			protected void onSecurityException(ApplicationSecurityException exception) {
				super.onSecurityException(exception);
				cb.onFailure(exception);
			}

			public void onSuccess(DynamicResultSet result) {
				super.onSuccess(result);
				ClassMetadata metadata = result.getClassMetaData();
				filterProperties(metadata, new MergedPropertyType[]{MergedPropertyType.MAPSTRUCTUREKEY, MergedPropertyType.MAPSTRUCTUREVALUE});
				
				DataSourceField symbolicIdField = new DataSourceTextField("symbolicId");
				symbolicIdField.setCanEdit(false);
				symbolicIdField.setHidden(true);
				symbolicIdField.setAttribute("rawName", "symbolicId");
				dataSource.addField(symbolicIdField);
				
				DataSourceField priorKeyField = new DataSourceTextField("priorKey");
				priorKeyField.setCanEdit(false);
				priorKeyField.setHidden(true);
				priorKeyField.setAttribute("rawName", "priorKey");
				dataSource.addField(priorKeyField);
				
				//Add a hidden field to store the polymorphic type for this entity
				DataSourceField typeField = new DataSourceTextField("type");
				typeField.setCanEdit(false);
				typeField.setHidden(true);
				typeField.setAttribute("rawName", "type");
				dataSource.addField(typeField);
				
				for (PolymorphicEntity polymorphicEntity : metadata.getPolymorphicEntities()){
					String name = polymorphicEntity.getName();
					String type = polymorphicEntity.getType();
					dataSource.getPolymorphicEntities().put(type, name);
				}
				dataSource.setDefaultNewEntityFullyQualifiedClassname(dataSource.getPolymorphicEntities().keySet().iterator().next());
				
				cb.onSuccess(dataSource);
			}
		});
	}
	
	@Override
	public boolean isCompatible(OperationType operationType) {
    	return OperationType.MAPSTRUCTURE.equals(operationType);
    }
}
