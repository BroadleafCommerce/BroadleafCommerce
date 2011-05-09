package org.broadleafcommerce.gwt.client.datasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.broadleafcommerce.gwt.client.Main;
import org.broadleafcommerce.gwt.client.datasource.results.ClassMetadata;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.PolymorphicEntity;
import org.broadleafcommerce.gwt.client.datasource.results.Property;
import org.broadleafcommerce.gwt.client.datasource.results.SupportedFieldType;
import org.broadleafcommerce.gwt.client.event.DataSourcePreparedEvent;
import org.broadleafcommerce.gwt.client.service.AbstractCallback;
import org.broadleafcommerce.gwt.client.service.AppServices;
import org.broadleafcommerce.gwt.client.service.DynamicEntityServiceAsync;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceDateTimeField;
import com.smartgwt.client.data.fields.DataSourceFloatField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.validator.RegExpValidator;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

public class DynamicEntityDataSource extends EntityServiceDataSource {

	protected final DateTimeFormat formatter = DateTimeFormat.getFormat("yyyy.MM.dd HH:mm:ss");
	protected final String ceilingEntityFullyQualifiedClassname;
	protected HashMap<String, String> polymorphicEntities = new HashMap<String, String>();
	protected HandlerManager eventBus;
	protected String token;
	protected DynamicEntityServiceAsync service;
	protected String[] optionalFields;
	protected String defaultNewEntityFullyQualifiedClassname;
	
	public DynamicEntityDataSource(String ceilingEntityFullyQualifiedClassname, HandlerManager eventBus, String token, DynamicEntityServiceAsync service, String[] optionalFields) {
		this.ceilingEntityFullyQualifiedClassname = ceilingEntityFullyQualifiedClassname;
		this.eventBus = eventBus;
		this.token = token;
		this.service = service;
		this.optionalFields = optionalFields;
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

	public void buildFields() {
		Main.NON_MODAL_PROGRESS.startProgress();
		AppServices.DYNAMIC_ENTITY.inspect(ceilingEntityFullyQualifiedClassname, optionalFields, new AbstractCallback<DynamicResultSet>() {
			public void onSuccess(DynamicResultSet result) {
				super.onSuccess(result);
				ClassMetadata metadata = result.getClassMetaData();
				for (Property property : metadata.getProperties()) {
					String propertyName = property.getName();
					String fieldType = property.getType();
					Long length = property.getLength();
					Boolean required = property.getRequired();
					Boolean mutable = property.getMutable();
					String inheritedFromType = property.getInheritedFromType();
					String availableToTypes = property.getAvailableToTypes();
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
						field = new DataSourceBooleanField(propertyName, propertyName);
						field.setCanEdit(mutable);
						break;
					case DATE:
						field = new DataSourceDateTimeField(propertyName, propertyName);
						field.setCanEdit(mutable);
						field.setRequired(required);
						break;
					case INTEGER:
						field = new DataSourceIntegerField(propertyName, propertyName);
						field.setCanEdit(mutable);
						field.setRequired(required);
						break;
					case DECIMAL:
						field = new DataSourceFloatField(propertyName, propertyName);
						field.setCanEdit(mutable);
						field.setRequired(required);
						break;
					case EMAIL:
						field = new DataSourceTextField(propertyName, propertyName);
						RegExpValidator emailValidator = new RegExpValidator();  
				        emailValidator.setErrorMessage("Invalid email address");  
				        emailValidator.setExpression("^([a-zA-Z0-9_.\\-+])+@(([a-zA-Z0-9\\-])+\\.)+[a-zA-Z0-9]{2,4}$");
				        field.setValidators(emailValidator);
				        field.setCanEdit(mutable);
				        field.setRequired(required);
				        break;
					case MONEY:
						field = new DataSourceFloatField(propertyName, propertyName);
						RegExpValidator usCurrencyValidator = new RegExpValidator();  
						usCurrencyValidator.setErrorMessage("Invalid currency amount");  
						usCurrencyValidator.setExpression("^(\\d{1,3}(\\,\\d{3})*|(\\d+))(\\.\\d{2})?$");
				        field.setValidators(usCurrencyValidator);
				        field.setCanEdit(mutable);
				        field.setRequired(required);
				        break;
					case HIERARCHY_KEY:
						field = new DataSourceTextField(propertyName, propertyName);
						field.setForeignKey("id");
						field.setHidden(true);
						field.setAttribute("permanentlyHidden", true);
						field.setRequired(required);
						break;
					default:
						field = new DataSourceTextField(propertyName, propertyName);
						field.setCanEdit(mutable);
						field.setRequired(required);
						break;
					}
					field.setLength(length.intValue());
					field.setAttribute("inheritedFromType", inheritedFromType);
					field.setAttribute("availableToTypes", availableToTypes);
					
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
				
				eventBus.fireEvent(new DataSourcePreparedEvent(DynamicEntityDataSource.this, token));
			}
		});
	}
	
	@Override
	protected void executeFetch(final String requestId, final DSRequest request, final DSResponse response) {
		Main.NON_MODAL_PROGRESS.startProgress();
		service.fetch(ceilingEntityFullyQualifiedClassname, getCto(request), optionalFields, new EntityServiceAsyncCallback<DynamicResultSet>(EntityOperationType.FETCH, requestId, request, response) {
			public void onSuccess(DynamicResultSet result) {
				super.onSuccess(result);
				TreeNode[] recordList = buildRecords(result);
				response.setData(recordList);
				response.setTotalRows(result.getTotalRecords());
				processResponse(requestId, response);
			}
		});
	}

	@Override
	protected void executeAdd(final String requestId, final DSRequest request, final DSResponse response) {
		Main.NON_MODAL_PROGRESS.startProgress();
		JavaScriptObject data = request.getData();
        TreeNode record = new TreeNode(data);
        Entity entity = buildEntity(record);
		service.add(entity, optionalFields, new EntityServiceAsyncCallback<Entity>(EntityOperationType.ADD, requestId, request, response) {
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
        TreeNode record = new TreeNode(data);
        Entity entity = buildEntity(record);
        String componentId = request.getComponentId();
        if (componentId != null) {
            if (entity.getType() == null) {
            	String type = ((ListGrid) Canvas.getById(componentId)).getSelectedRecord().getAttribute("type");
            	entity.setType(type);
            }
        }
		service.update(entity, optionalFields, new EntityServiceAsyncCallback<Entity>(EntityOperationType.UPDATE, requestId, request, response) {
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
        service.remove(entity, new EntityServiceAsyncCallback<Void>(EntityOperationType.REMOVE, requestId, request, response) {
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
				} else {
					property.setValue(record.getAttribute(attribute));
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
		for (Property property : entity.getProperties()){
			if (property.getValue() != null && this.getField(property.getName()).getType().equals(FieldType.DATETIME)) {
				record.setAttribute(property.getName(), formatter.parse(property.getValue()));
			} else if (getField(property.getName()).getType().equals(FieldType.BOOLEAN) && property.getValue() == null && getField(property.getName()).getRequired()) {
				record.setAttribute(property.getName(), false);
			} else {
				record.setAttribute(property.getName(), property.getValue());
			}
		}
		String entityType = entity.getType();
		record.setAttribute("type", entityType);
		return record;
	}
	
	public void resetFieldVisibility() {
		resetFieldVisibilityBasedOnType(getDefaultNewEntityFullyQualifiedClassname());
	}

	public void resetFieldVisibilityBasedOnType(String type) {
		DataSourceField[] fields = getFields();
		for (DataSourceField field : fields) {
			if (field.getAttribute("permanentlyHidden") == null || field.getAttributeAsBoolean("permanentlyHidden") == false) {
				if (field.getAttribute("availableToTypes") != null && field.getAttribute("availableToTypes").contains(type)) {
					field.setHidden(false);
				} else {
					field.setHidden(true);
				}
			}
		}
	}
}
