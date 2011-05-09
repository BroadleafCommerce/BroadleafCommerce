package org.broadleafcommerce.gwt.client.datasource.dynamic.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.broadleafcommerce.gwt.client.Main;
import org.broadleafcommerce.gwt.client.datasource.Validators;
import org.broadleafcommerce.gwt.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.gwt.client.datasource.dynamic.EntityOperationType;
import org.broadleafcommerce.gwt.client.datasource.dynamic.EntityServiceAsyncCallback;
import org.broadleafcommerce.gwt.client.datasource.relations.ForeignKey;
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
import org.broadleafcommerce.presentation.SupportedFieldType;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.anasoft.os.daofusion.cto.client.FilterAndSortCriteria;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtincubator.security.exception.ApplicationSecurityException;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.SortSpecifier;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceFloatField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.util.JSON;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

public class BasicEntityModule implements DataSourceModule {

	protected final DateTimeFormat formatter = DateTimeFormat.getFormat("yyyy.MM.dd HH:mm:ss");
	
	protected ForeignKey currentForeignKey;
	protected DynamicEntityDataSource dataSource;
	protected String linkedValue;
	protected DynamicEntityServiceAsync service;
	protected final String ceilingEntityFullyQualifiedClassname;
	protected PersistencePerspective persistencePerspective;
	protected Long loadLevelCount = 0L;
	
	public BasicEntityModule(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service) {
		this.service = service;
		this.ceilingEntityFullyQualifiedClassname = ceilingEntityFullyQualifiedClassname;
		this.persistencePerspective = persistencePerspective;
	}
	
	/**
     * Transforms the given <tt>request</tt> into
     * {@link CriteriaTransferObject} instance.
     * <p>
     * We are doing this because we can apply seamless
     * CTO-to-criteria conversions back on the server.
     */
    @SuppressWarnings("unchecked")
    public CriteriaTransferObject getCto(DSRequest request) {
        CriteriaTransferObject cto = new CriteriaTransferObject();
        
        // paging
        if (request.getStartRow() != null) {
        	cto.setFirstResult(request.getStartRow());
        	if (request.getEndRow() != null) {
        		cto.setMaxResults(request.getEndRow() - request.getStartRow());
        	}
        }
        
        try {
			// sort
			SortSpecifier[] sortBy = request.getSortBy();
			if (sortBy != null && sortBy.length > 0) {
				String sortPropertyId = sortBy[0].getField();
			    boolean sortAscending = sortBy[0].getSortDirection().equals(SortDirection.ASCENDING);            
			    FilterAndSortCriteria sortCriteria = cto.get(sortPropertyId);
			    sortCriteria.setSortAscending(sortAscending);
			}
		} catch (Exception e) {
			//do nothing
			GWT.log("WARN: Unable to set sort criteria because of an exception.", e);
		}
        
        Criteria criteria = request.getCriteria();
        String jsObj = JSON.encode(criteria.getJsObj());
        // filter
        @SuppressWarnings("rawtypes")
		Map filterData = criteria.getValues();
        Set<String> filterFieldNames = filterData.keySet();
        for (String fieldName : filterFieldNames) {
        	if (!fieldName.equals("_constructor") && !fieldName.equals("operator")) {
        		if (!fieldName.equals("criteria")) {
        			FilterAndSortCriteria filterCriteria = cto.get(fieldName);
        			filterCriteria.setFilterValue(dataSource.stripDuplicateAllowSpecialCharacters((String) filterData.get(fieldName)));
        		} else {
        			JSONValue value = JSONParser.parse(jsObj);
        			JSONObject criteriaObj = value.isObject();
        			JSONArray criteriaArray = criteriaObj.get("criteria").isArray();
        			buildCriteria(criteriaArray, cto);
        		}
        	}
        }
        if (getCurrentForeignKey() != null) {
        	FilterAndSortCriteria filterCriteria = cto.get(getCurrentForeignKey().getManyToField());
			filterCriteria.setFilterValue(getCurrentForeignKey().getCurrentValue());
        }
        
        return cto;
    }
    
    public ForeignKey getCurrentForeignKey() {
		return currentForeignKey;
	}

	public void setCurrentForeignKey(ForeignKey currentForeignKey) {
		this.currentForeignKey = currentForeignKey;
	}
	
	public String getLinkedValue() {
		return linkedValue;
	}

	public void setLinkedValue(String linkedValue) {
		this.linkedValue = linkedValue;
	}
    
    protected void buildCriteria(JSONArray criteriaArray, CriteriaTransferObject cto) {
    	if (criteriaArray != null) {
			for (int i=0; i<=criteriaArray.size()-1; i++) {
				JSONObject itemObj = criteriaArray.get(i).isObject();
				if (itemObj != null) {
					JSONValue val = itemObj.get("fieldName");
					if (val == null) {
						JSONArray array = itemObj.get("criteria").isArray();
						buildCriteria(array, cto);
					} else {
						FilterAndSortCriteria filterCriteria = cto.get(val.isString().stringValue());
						String[] items = filterCriteria.getFilterValues();
						String[] newItems = new String[items.length + 1];
						int j = 0;
						for (String item : items) {
							newItems[j] = item;
							j++;
						}
						JSONValue value = itemObj.get("value");
						JSONString strVal = value.isString();
						if (strVal != null) {
							newItems[j] = strVal.stringValue();
						} else {
							newItems[j] = value.isObject().get("value").isString().stringValue();
							/*
							 * TODO need to add special parsing for relative dates. Convert this relative
							 * value to an actual date string.
							 */
						}
						
						filterCriteria.setFilterValues(newItems);
					}
				}
			}
		}
    }
    
    public boolean isCompatible(OperationType operationType) {
    	return OperationType.ENTITY.equals(operationType) || OperationType.FOREIGNKEY.equals(operationType);
    }
    
    public void executeFetch(final String requestId, final DSRequest request, final DSResponse response) {
		Main.NON_MODAL_PROGRESS.startProgress();
		CriteriaTransferObject cto = getCto(request);
		service.fetch(ceilingEntityFullyQualifiedClassname, cto, persistencePerspective, null, new EntityServiceAsyncCallback<DynamicResultSet>(EntityOperationType.FETCH, requestId, request, response, dataSource) {
			public void onSuccess(DynamicResultSet result) {
				super.onSuccess(result);
				TreeNode[] recordList = buildRecords(result, null);
				response.setData(recordList);
				response.setTotalRows(result.getTotalRecords());
				dataSource.processResponse(requestId, response);
			}
		});
	}
    
    public void executeAdd(final String requestId, final DSRequest request, final DSResponse response) {
		Main.NON_MODAL_PROGRESS.startProgress();
		JavaScriptObject data = request.getData();
        TreeNode record = new TreeNode(data);
        Entity entity = buildEntity(record);
        service.add(ceilingEntityFullyQualifiedClassname, entity, persistencePerspective, null, new EntityServiceAsyncCallback<Entity>(EntityOperationType.ADD, requestId, request, response, dataSource) {
			public void onSuccess(Entity result) {
				super.onSuccess(result);
				TreeNode record = (TreeNode) buildRecord(result);
				TreeNode[] recordList = new TreeNode[]{record};
				response.setData(recordList);
				dataSource.processResponse(requestId, response);
			}
		});
	}
    
    public void executeUpdate(final String requestId, final DSRequest request, final DSResponse response) {
		Main.NON_MODAL_PROGRESS.startProgress();
		JavaScriptObject data = request.getData();
        final TreeNode record = new TreeNode(data);
        Entity entity = buildEntity(record);
		String componentId = request.getComponentId();
        if (componentId != null) {
            if (entity.getType() == null) {
            	String type = ((ListGrid) Canvas.getById(componentId)).getSelectedRecord().getAttribute("type");
            	entity.setType(type);
            }
        }
        service.update(entity, persistencePerspective, null, new EntityServiceAsyncCallback<Entity>(EntityOperationType.UPDATE, requestId, request, response, dataSource) {
			public void onSuccess(Entity result) {
				super.onSuccess(null);
				dataSource.processResponse(requestId, response);
			}
		});
	}
    
    public void executeRemove(final String requestId, final DSRequest request, final DSResponse response) {
		Main.NON_MODAL_PROGRESS.startProgress();
		JavaScriptObject data = request.getData();
        TreeNode record = new TreeNode(data);
        Entity entity = buildEntity(record);
		String componentId = request.getComponentId();
        if (componentId != null) {
            if (entity.getType() == null) {
            	String type = ((ListGrid) Canvas.getById(componentId)).getSelectedRecord().getAttribute("type");
            	entity.setType(type);
            }
        }
        service.remove(entity, persistencePerspective, null, new EntityServiceAsyncCallback<Void>(EntityOperationType.REMOVE, requestId, request, response, dataSource) {
			public void onSuccess(Void item) {
				super.onSuccess(null);
				dataSource.processResponse(requestId, response);
			}
		});
	}
    
    public Record buildRecord(Entity entity) {
		TreeNode record = new TreeNode();
		return updateRecord(entity, record, true);
	}

	public Record updateRecord(Entity entity, Record record, Boolean updateId) {
		for (Property property : entity.getProperties()){
			String attributeName = property.getName();
			if (property.getValue() != null && dataSource.getField(attributeName).getType().equals(FieldType.DATETIME)) {
				record.setAttribute(attributeName, formatter.parse(property.getValue()));
			} else if (dataSource.getField(attributeName).getType().equals(FieldType.BOOLEAN) && property.getValue() == null && dataSource.getField(attributeName).getRequired()) {
				record.setAttribute(attributeName, false);
			} else if (property.getType() != null && SupportedFieldType.valueOf(property.getType()).equals(SupportedFieldType.FOREIGN_KEY)) {
				record.setAttribute(attributeName, linkedValue);
			} else {
				String propertyValue;
				if (property.getName().equals("id")) {
					if (updateId) {
						propertyValue = property.getValue() + "_" + loadLevelCount;
						loadLevelCount++;
						record.setAttribute("id", propertyValue);
					}
				} else {
					propertyValue = property.getValue();
					record.setAttribute(attributeName, propertyValue);
				}
			}
		}
		String entityType = entity.getType();
		record.setAttribute("type", entityType);
		return record;
	}
    
    public TreeNode[] buildRecords(DynamicResultSet result, String[] filterOutIds) {
		List<TreeNode> recordList = new ArrayList<TreeNode>();
		int decrement = 0;
		for (Entity entity : result.getRecords()){
			if (filterOutIds == null || (filterOutIds != null && Arrays.binarySearch(filterOutIds, entity.findProperty("id").getValue()) < 0)) {
				TreeNode record = (TreeNode) buildRecord(entity);
				recordList.add(record);
			} else {
				decrement++;
			}
		}
		result.setTotalRecords(result.getTotalRecords() - decrement);
		TreeNode[] response = new TreeNode[recordList.size()];
		response = recordList.toArray(response);
		return response;
	}
    
    public Entity buildEntity(Record record) {
		Entity entity = new Entity();
		entity.setType(record.getAttribute("type"));
		List<Property> properties = new ArrayList<Property>();
		String[] attributes = record.getAttributes();
		for (String attribute : attributes) {
			if (!attribute.equals("type") && !attribute.startsWith("__") && dataSource.getField(attribute) != null) {
				Property property = new Property();
				if (record.getAttribute(attribute) != null && dataSource.getField(attribute) != null && dataSource.getField(attribute).getType().equals(FieldType.DATETIME)) {
					property.setValue(formatter.format(record.getAttributeAsDate(attribute)));
				} else if (linkedValue != null && dataSource.getField(attribute).getAttribute("fieldType") != null && SupportedFieldType.valueOf(dataSource.getField(attribute).getAttribute("fieldType")).equals(SupportedFieldType.FOREIGN_KEY)) {
					property.setValue(dataSource.stripDuplicateAllowSpecialCharacters(linkedValue));
				} else {
					property.setValue(dataSource.stripDuplicateAllowSpecialCharacters(record.getAttribute(attribute)));
				}
				property.setName(dataSource.getField(attribute).getAttribute("rawName"));
				properties.add(property);
			}
		}
		Property[] props = new Property[properties.size()];
		props = properties.toArray(props);
		entity.setProperties(props);
		
		return entity;
	}
    
    public void buildFields(final AsyncCallback<DataSource> cb) {
		AppServices.DYNAMIC_ENTITY.inspect(ceilingEntityFullyQualifiedClassname, persistencePerspective, new AbstractCallback<DynamicResultSet>() {
			
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
				filterProperties(metadata, new MergedPropertyType[]{MergedPropertyType.PRIMARY, MergedPropertyType.JOINTABLE});
				
				//Add a hidden field to store the polymorphic type for this entity
				DataSourceField typeField = new DataSourceTextField("type");
				typeField.setCanEdit(false);
				typeField.setHidden(true);
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
	
	protected void filterProperties(ClassMetadata metadata, MergedPropertyType[] includeTypes) throws IllegalStateException {
		for (Property property : metadata.getProperties()) {
			String mergedPropertyType = property.getMergedPropertyType();
			if (Arrays.binarySearch(includeTypes, MergedPropertyType.valueOf(mergedPropertyType)) >= 0) {
				String rawName = property.getName();
				String propertyName = rawName;
				String fieldType = property.getType();
				Long length = property.getLength();
				Boolean required = property.getRequired();
				if (required == null) {
					required = false;
				}
				Boolean mutable = property.getMutable();
				String inheritedFromType = property.getInheritedFromType();
				String availableToTypes = property.getAvailableToTypes();
				String foreignKeyClass = property.getForeignKeyClass();
				String foreignKeyProperty = property.getForeignKeyProperty();
				String friendlyName = property.getFriendlyName();
				if (friendlyName == null) {
					friendlyName = property.getName();
				}
				Boolean hidden = property.getHidden();
				String group = property.getGroup();
				Boolean largeEntry = property.getLargeEntry();
				Boolean prominent = property.getProminent();
				Integer order = property.getOrder();
				DataSourceField field;
				switch(SupportedFieldType.valueOf(fieldType)){
				case ID:
					field = new DataSourceTextField("id");
					field.setCanEdit(false);
					field.setPrimaryKey(true);
					field.setHidden(true);
					field.setAttribute("permanentlyHidden", true);
					field.setRequired(required);
					break;
				case BOOLEAN:
					field = new DataSourceBooleanField(propertyName, friendlyName);
					field.setCanEdit(mutable);
					break;
				case DATE:
					field = new DataSourceDateTimeField(propertyName, friendlyName);
					field.setCanEdit(mutable);
					field.setRequired(required);
					break;
				case INTEGER:
					field = new DataSourceIntegerField(propertyName, friendlyName);
					field.setCanEdit(mutable);
					field.setRequired(required);
					break;
				case DECIMAL:
					field = new DataSourceFloatField(propertyName, friendlyName);
					field.setCanEdit(mutable);
					field.setRequired(required);
					break;
				case EMAIL:
					field = new DataSourceTextField(propertyName, friendlyName);
			        field.setValidators(Validators.EMAIL);
			        field.setCanEdit(mutable);
			        field.setRequired(required);
			        break;
				case MONEY:
					field = new DataSourceFloatField(propertyName, friendlyName);
			        field.setValidators(Validators.USCURRENCY);
			        field.setCanEdit(mutable);
			        field.setRequired(required);
			        break;
				case FOREIGN_KEY:
					field = new DataSourceTextField(propertyName, friendlyName);
					String dataSourceName = null;
					ForeignKey foreignField = (ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);
					if (foreignField != null && foreignField.getForeignKeyClass().equals(foreignKeyClass)) {
						dataSourceName = foreignField.getDataSourceName();
					}
					if (dataSourceName == null) {
						field.setForeignKey(foreignKeyProperty);
					} else {
						field.setForeignKey(dataSourceName+"."+foreignKeyProperty);
					}
					//field.setForeignKey(foreignKeyProperty);
					field.setHidden(true);
					field.setAttribute("permanentlyHidden", true);
					field.setRequired(required);
					break;
				case ADDITIONAL_FOREIGN_KEY:
					field = new DataSourceTextField(propertyName, friendlyName);
					field.setHidden(true);
					field.setAttribute("permanentlyHidden", true);
					field.setRequired(required);
					break;
				default:
					field = new DataSourceTextField(propertyName, friendlyName);
					field.setCanEdit(mutable);
					field.setRequired(required);
					break;
				}
				if (hidden != null) {
					field.setHidden(hidden);
					field.setAttribute("permanentlyHidden", hidden);
				}
				if (group != null) {
					field.setAttribute("formGroup", group);
				}
				if (largeEntry != null) {
					field.setAttribute("largeEntry", largeEntry);
				}
				if (prominent != null) {
					field.setAttribute("prominent", prominent);
				}
				if (order != null) {
					field.setAttribute("presentationLayerOrder", order);
				}
				if (length != null) {
					field.setLength(length.intValue());
				}
				field.setAttribute("inheritedFromType", inheritedFromType);
				field.setAttribute("availableToTypes", availableToTypes);
				field.setAttribute("fieldType", fieldType);
				field.setAttribute("mergedPropertyType", mergedPropertyType);
				field.setAttribute("rawName", rawName);
				dataSource.addField(field);
			}
		}
	}

	public void setDataSource(DynamicEntityDataSource dataSource) {
		this.dataSource = dataSource;
	}
}
