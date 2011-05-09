package org.broadleafcommerce.gwt.client.datasource.dynamic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.broadleafcommerce.gwt.client.Main;
import org.broadleafcommerce.gwt.client.datasource.JoinTable;
import org.broadleafcommerce.gwt.client.datasource.EntityServiceDataSource;
import org.broadleafcommerce.gwt.client.datasource.ForeignKey;
import org.broadleafcommerce.gwt.client.datasource.Validators;
import org.broadleafcommerce.gwt.client.datasource.results.ClassMetadata;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.PolymorphicEntity;
import org.broadleafcommerce.gwt.client.datasource.results.Property;
import org.broadleafcommerce.gwt.client.datasource.results.RemoveType;
import org.broadleafcommerce.gwt.client.service.AbstractCallback;
import org.broadleafcommerce.gwt.client.service.AppServices;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;
import org.broadleafcommerce.presentation.SupportedFieldType;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtincubator.security.exception.ApplicationSecurityException;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceFloatField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

public class DynamicEntityDataSource extends EntityServiceDataSource {

	protected final DateTimeFormat formatter = DateTimeFormat.getFormat("yyyy.MM.dd HH:mm:ss");
	protected final String ceilingEntityFullyQualifiedClassname;
	protected HashMap<String, String> polymorphicEntities = new HashMap<String, String>();
	protected DynamicEntityServiceAsync service;
	protected ForeignKey[] foreignFields;
	protected JoinTable joinTable;
	protected String defaultNewEntityFullyQualifiedClassname;
	protected String linkedValue;
	protected RemoveType removeType;
	protected Long loadLevelCount = 0L;
	protected String[] additionalNonPersistentProperties;
	
	public DynamicEntityDataSource(String ceilingEntityFullyQualifiedClassname, ForeignKey[] foreignFields, String name, DynamicEntityServiceAsync service, RemoveType removeType, String[] additionalNonPersistentProperties) {
		super(name);
		this.ceilingEntityFullyQualifiedClassname = ceilingEntityFullyQualifiedClassname;
		this.service = service;
		this.foreignFields = foreignFields;
		this.removeType = removeType;
		this.additionalNonPersistentProperties = additionalNonPersistentProperties;
	}
	
	public DynamicEntityDataSource(String ceilingEntityFullyQualifiedClassname, JoinTable joinTable, String name, DynamicEntityServiceAsync service, RemoveType removeType, String[] additionalNonPersistentProperties) {
		super(name);
		this.ceilingEntityFullyQualifiedClassname = ceilingEntityFullyQualifiedClassname;
		this.service = service;
		this.joinTable = joinTable;
		this.removeType = removeType;
		this.additionalNonPersistentProperties = additionalNonPersistentProperties;
	}
	
	public HashMap<String, String> getPolymorphicEntities() {
		return polymorphicEntities;
	}
	
	public String getCeilingEntityFullyQualifiedClassname() {
		return ceilingEntityFullyQualifiedClassname;
	}

	public String getDefaultNewEntityFullyQualifiedClassname() {
		return defaultNewEntityFullyQualifiedClassname;
	}

	public void setDefaultNewEntityFullyQualifiedClassname(String defaultNewEntityFullyQualifiedClassname) {
		this.defaultNewEntityFullyQualifiedClassname = defaultNewEntityFullyQualifiedClassname;
	}

	public void buildFields(final AsyncCallback<DataSource> cb) {
		Main.NON_MODAL_PROGRESS.startProgress();
		AppServices.DYNAMIC_ENTITY.inspect(ceilingEntityFullyQualifiedClassname, foreignFields, additionalNonPersistentProperties, new AbstractCallback<DynamicResultSet>() {
			
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
				for (Property property : metadata.getProperties()) {
					String propertyName = property.getName();
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
						field = new DataSourceBooleanField(propertyName, friendlyName!=null?friendlyName:propertyName);
						field.setCanEdit(mutable);
						break;
					case DATE:
						field = new DataSourceDateTimeField(propertyName, friendlyName!=null?friendlyName:propertyName);
						field.setCanEdit(mutable);
						field.setRequired(required);
						break;
					case INTEGER:
						field = new DataSourceIntegerField(propertyName, friendlyName!=null?friendlyName:propertyName);
						field.setCanEdit(mutable);
						field.setRequired(required);
						break;
					case DECIMAL:
						field = new DataSourceFloatField(propertyName, friendlyName!=null?friendlyName:propertyName);
						field.setCanEdit(mutable);
						field.setRequired(required);
						break;
					case EMAIL:
						field = new DataSourceTextField(propertyName, friendlyName!=null?friendlyName:propertyName);
				        field.setValidators(Validators.EMAIL);
				        field.setCanEdit(mutable);
				        field.setRequired(required);
				        break;
					case MONEY:
						field = new DataSourceFloatField(propertyName, friendlyName!=null?friendlyName:propertyName);
				        field.setValidators(Validators.USCURRENCY);
				        field.setCanEdit(mutable);
				        field.setRequired(required);
				        break;
					case FOREIGN_KEY:
						field = new DataSourceTextField(propertyName, friendlyName!=null?friendlyName:propertyName);
						String dataSourceName = null;
						for (ForeignKey foreignKey : foreignFields) {
							if (foreignKey.getForeignKeyClass().equals(foreignKeyClass)) {
								dataSourceName = foreignKey.getDataSourceName();
								break;
							}
						}
						if (dataSourceName == null) {
							field.setForeignKey(foreignKeyProperty);
						} else {
							field.setForeignKey(dataSourceName+"."+foreignKeyProperty);
						}
						field.setForeignKey(foreignKeyProperty);
						field.setHidden(true);
						field.setAttribute("permanentlyHidden", true);
						field.setRequired(required);
						break;
					default:
						field = new DataSourceTextField(propertyName, friendlyName!=null?friendlyName:propertyName);
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
						field.setAttribute("order", order);
					}
					if (length != null) {
						field.setLength(length.intValue());
					}
					field.setAttribute("inheritedFromType", inheritedFromType);
					field.setAttribute("availableToTypes", availableToTypes);
					field.setAttribute("fieldType", fieldType);
					addField(field);
				}
				
				//Add a hidden field to store the polymorphic type for this entity
				DataSourceField typeField = new DataSourceTextField("type");
				typeField.setCanEdit(false);
				typeField.setHidden(true);
				addField(typeField);
				
				for (PolymorphicEntity polymorphicEntity : metadata.getPolymorphicEntities()){
					String name = polymorphicEntity.getName();
					String type = polymorphicEntity.getType();
					polymorphicEntities.put(type, name);
				}
				defaultNewEntityFullyQualifiedClassname = polymorphicEntities.keySet().iterator().next();
				
				cb.onSuccess(DynamicEntityDataSource.this);
			}
		});
	}
	
	@Override
	protected void executeFetch(final String requestId, final DSRequest request, final DSResponse response) {
		Main.NON_MODAL_PROGRESS.startProgress();
		if (joinTable != null) {
			service.fetch(ceilingEntityFullyQualifiedClassname, joinTable, getCto(request), additionalNonPersistentProperties, new EntityServiceAsyncCallback<DynamicResultSet>(EntityOperationType.FETCH, requestId, request, response) {
				public void onSuccess(DynamicResultSet result) {
					super.onSuccess(result);
					TreeNode[] recordList = buildRecords(result);
					response.setData(recordList);
					response.setTotalRows(result.getTotalRecords());
					processResponse(requestId, response);
				}
			});
		} else {
			service.fetch(ceilingEntityFullyQualifiedClassname, foreignFields, getCto(request), additionalNonPersistentProperties, new EntityServiceAsyncCallback<DynamicResultSet>(EntityOperationType.FETCH, requestId, request, response) {
				public void onSuccess(DynamicResultSet result) {
					super.onSuccess(result);
					TreeNode[] recordList = buildRecords(result);
					response.setData(recordList);
					response.setTotalRows(result.getTotalRecords());
					processResponse(requestId, response);
				}
			});
		}
	}

	@Override
	protected void executeAdd(final String requestId, final DSRequest request, final DSResponse response) {
		Main.NON_MODAL_PROGRESS.startProgress();
		JavaScriptObject data = request.getData();
        TreeNode record = new TreeNode(data);
        Entity entity = buildEntity(record);
		service.add(entity, foreignFields, additionalNonPersistentProperties, new EntityServiceAsyncCallback<Entity>(EntityOperationType.ADD, requestId, request, response) {
			public void onSuccess(Entity result) {
				super.onSuccess(result);
				TreeNode record = buildRecord(result);
				TreeNode[] recordList = new TreeNode[]{record};
				response.setData(recordList);
				processResponse(requestId, response);
			}
		});
	}

	@Override
	protected void executeUpdate(final String requestId, final DSRequest request, final DSResponse response) {
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
		service.update(entity, foreignFields, additionalNonPersistentProperties, new EntityServiceAsyncCallback<Entity>(EntityOperationType.UPDATE, requestId, request, response) {
			public void onSuccess(Entity result) {
				super.onSuccess(result);
				TreeNode myRecord = updateRecord(result, record, false);
				TreeNode[] recordList = new TreeNode[]{myRecord};
				response.setData(recordList);
				processResponse(requestId, response);
			}
		});
	}

	@Override
	protected void executeRemove(final String requestId, final DSRequest request, final DSResponse response) {
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
        service.remove(entity, foreignFields, removeType, additionalNonPersistentProperties, new EntityServiceAsyncCallback<Void>(EntityOperationType.REMOVE, requestId, request, response) {
			public void onSuccess(Void item) {
				super.onSuccess(null);
				processResponse(requestId, response);
			}
		});
	}
	
	public Entity buildEntity(TreeNode record) {
		Entity entity = new Entity();
		entity.setType(record.getAttribute("type"));
		List<Property> properties = new ArrayList<Property>();
		for (String attribute : record.getAttributes()) {
			if (!attribute.equals("type") && !attribute.startsWith("__") && getField(attribute) != null) {
				Property property = new Property();
				if (record.getAttribute(attribute) != null && this.getField(attribute) != null && getField(attribute).getType().equals(FieldType.DATETIME)) {
					property.setValue(formatter.format(record.getAttributeAsDate(attribute)));
				} else if (linkedValue != null && SupportedFieldType.valueOf(getField(attribute).getAttribute("fieldType")).equals(SupportedFieldType.FOREIGN_KEY)) {
					property.setValue(stripDuplicateAllowSpecialCharacters(linkedValue));
				} else {
					property.setValue(stripDuplicateAllowSpecialCharacters(record.getAttribute(attribute)));
				}
				property.setName(attribute);
				properties.add(property);
			}
		}
		Property[] props = new Property[properties.size()];
		props = properties.toArray(props);
		entity.setProperties(props);
		
		return entity;
	}

	public TreeNode[] buildRecords(DynamicResultSet result) {
		TreeNode[] recordList = new TreeNode[result.getRecords().length];
		int j = 0;
		for (Entity entity : result.getRecords()){
			TreeNode record = buildRecord(entity);
			recordList[j] = record;
			j++;
		}
		return recordList;
	}
	
	public TreeNode buildRecord(Entity entity) {
		TreeNode record = new TreeNode();
		return updateRecord(entity, record, true);
	}

	public TreeNode updateRecord(Entity entity, TreeNode record, Boolean updateId) {
		for (Property property : entity.getProperties()){
			if (property.getValue() != null && this.getField(property.getName()).getType().equals(FieldType.DATETIME)) {
				record.setAttribute(property.getName(), formatter.parse(property.getValue()));
			} else if (getField(property.getName()).getType().equals(FieldType.BOOLEAN) && property.getValue() == null && getField(property.getName()).getRequired()) {
				record.setAttribute(property.getName(), false);
			} else if (property.getType() != null && SupportedFieldType.valueOf(property.getType()).equals(SupportedFieldType.FOREIGN_KEY)) {
				record.setAttribute(property.getName(), linkedValue);
			} else {
				String propertyValue;
				if (property.getName().equals("id")) {
					if (updateId) {
						propertyValue = property.getValue() + "_" + loadLevelCount;
						loadLevelCount++;
						record.setAttribute(property.getName(), propertyValue);
					}
				} else {
					propertyValue = property.getValue();
					record.setAttribute(property.getName(), propertyValue);
				}
			}
		}
		String entityType = entity.getType();
		record.setAttribute("type", entityType);
		return record;
	}
	
	public void resetFieldVisibility() {
		resetFieldVisibilityBasedOnType(getDefaultNewEntityFullyQualifiedClassname());
	}
	
	public void resetFieldVisibility(String... fieldNames) {
		DataSourceField[] fields = getFields();
		Arrays.sort(fieldNames);
		for (DataSourceField field : fields) {
			String fieldName = field.getName();
			int pos = Arrays.binarySearch(fieldNames, fieldName);
			if (pos >= 0) {
				field.setHidden(false);
				field.setAttribute("permanentlyHidden", false);
				field.setAttribute("prominent", true);
			} else {
				field.setHidden(true);
				field.setAttribute("permanentlyHidden", true);
				field.setAttribute("prominent", false);
			}
		}
	}

	public void resetFieldVisibilityBasedOnType(String type) {
		DataSourceField[] fields = getFields();
		for (DataSourceField field : fields) {
			if (field.getAttribute("permanentlyHidden") == null || field.getAttributeAsBoolean("permanentlyHidden") == false) {
				if (field.getAttribute("availableToTypes") != null && field.getAttribute("availableToTypes").contains(type)) {
					field.setHidden(false);
					field.setAttribute("permanentlyHidden", false);
					field.setAttribute("prominent", true);
				} else {
					field.setHidden(true);
					field.setAttribute("permanentlyHidden", true);
					field.setAttribute("prominent", false);
				}
			}
		}
	}

	public String getLinkedValue() {
		return linkedValue;
	}

	public void setLinkedValue(String linkedValue) {
		this.linkedValue = linkedValue;
	}

}
