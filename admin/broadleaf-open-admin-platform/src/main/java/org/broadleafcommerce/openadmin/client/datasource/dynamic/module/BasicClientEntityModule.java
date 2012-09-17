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

package org.broadleafcommerce.openadmin.client.datasource.dynamic.module;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
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
import com.smartgwt.client.data.fields.DataSourceEnumField;
import com.smartgwt.client.data.fields.DataSourceFloatField;
import com.smartgwt.client.data.fields.DataSourceImageField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourcePasswordField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.types.OperatorId;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.util.JSON;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.validator.Validator;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.tree.TreeNode;
import org.broadleafcommerce.common.presentation.client.OperationType;
import org.broadleafcommerce.common.presentation.client.PersistencePerspectiveItemType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.datasource.LookupMetadata;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.operation.EntityOperationType;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.operation.EntityServiceAsyncCallback;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.BasicCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.MapMetadata;
import org.broadleafcommerce.openadmin.client.dto.MergedPropertyType;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.client.dto.visitor.MetadataVisitorAdapter;
import org.broadleafcommerce.openadmin.client.presenter.entity.DynamicEntityPresenter;
import org.broadleafcommerce.openadmin.client.service.AbstractCallback;
import org.broadleafcommerce.openadmin.client.service.AppServices;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityServiceAsync;
import org.broadleafcommerce.openadmin.client.setup.AsyncCallbackAdapter;
import org.broadleafcommerce.openadmin.client.setup.PresenterSequenceSetupManager;
import org.broadleafcommerce.openadmin.client.validation.ValidationFactoryManager;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.FormHiddenEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;

/**
 * 
 * @author jfischer
 *
 */
public class BasicClientEntityModule implements DataSourceModule {

	protected final DateTimeFormat formatter = DateTimeFormat.getFormat("yyyy.MM.dd HH:mm:ss Z");
	
	protected ForeignKey currentForeignKey;
	protected AbstractDynamicDataSource dataSource;
	protected String linkedValue;
	protected DynamicEntityServiceAsync service;
	protected final String ceilingEntityFullyQualifiedClassname;
    protected final String fetchTypeFullyQualifiedClassname;
	protected PersistencePerspective persistencePerspective;
	protected Long loadLevelCount = 0L;
	
	public BasicClientEntityModule(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service) {
		this(ceilingEntityFullyQualifiedClassname, null, persistencePerspective, service);
	}

    public BasicClientEntityModule(String ceilingEntityFullyQualifiedClassname, String fetchTypeFullyQualifiedClassname, PersistencePerspective persistencePerspective, DynamicEntityServiceAsync service) {
        this.service = service;
        this.ceilingEntityFullyQualifiedClassname = ceilingEntityFullyQualifiedClassname;
        this.fetchTypeFullyQualifiedClassname = fetchTypeFullyQualifiedClassname;
        this.persistencePerspective = persistencePerspective;
    }
	
	/**
     * Transforms the given <tt>request</tt> into
     * {@link CriteriaTransferObject} instance.
     * <p>
     * We are doing this because we can apply seamless
     * CTO-to-criteria conversions back on the server.
     */
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
		Map filterData = criteria.getValues();
        Set<String> filterFieldNames = filterData.keySet();
        for (String fieldName : filterFieldNames) {
        	if (!fieldName.equals("_constructor") && !fieldName.equals("operator")) {
        		if (!fieldName.equals("criteria")) {
        			FilterAndSortCriteria filterCriteria = cto.get(fieldName);
                    Object filterValue = filterData.get(fieldName);
                    String filterString = null;
                    if (filterValue != null) {
                        filterString = filterValue.toString();
                    }
                    String fieldTypeVal = null;
                    DataSourceField field = dataSource.getField(fieldName);
                    if (field != null) {
                        fieldTypeVal = field.getAttribute("fieldType");
                    }
                    SupportedFieldType fieldType = fieldTypeVal==null?SupportedFieldType.STRING:SupportedFieldType.valueOf(fieldTypeVal);
                    if (fieldType != null) {
                        switch (fieldType) {
                            case DECIMAL:
                                processFilterValueClause(filterCriteria, filterString);
                                break;
                            case INTEGER:
                                processFilterValueClause(filterCriteria, filterString);
                                break;
                            case MONEY:
                                processFilterValueClause(filterCriteria, filterString);
                                break;
                            default:
                                filterCriteria.setFilterValue(dataSource.stripDuplicateAllowSpecialCharacters(filterString));
                                break;
                        }
                    } else {
                        filterCriteria.setFilterValue(dataSource.stripDuplicateAllowSpecialCharacters(filterString));
                    }
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

    protected void processFilterValueClause(FilterAndSortCriteria filterCriteria, String filterString) {
        String filterVal = dataSource.stripDuplicateAllowSpecialCharacters(filterString);
        int pos = filterVal.indexOf("-");
        String decimalSeparator = LocaleInfo.getCurrentLocale().getNumberConstants().decimalSeparator();
        if (pos > 0) {
            //TODO this method does not deal well with negative values that may be entered into the filter field.
            String filterValue1 = filterVal.substring(0, pos).trim();
            int decimalPos = filterValue1.indexOf(decimalSeparator);
            if (decimalPos >= 0) {
                filterValue1 = filterValue1.substring(0, decimalPos) + "." + filterValue1.substring(decimalPos + 1, filterValue1.length());
            }
            String filterValue2 = filterVal.substring(pos + 1, filterVal.length()).trim();
            decimalPos = filterValue2.indexOf(decimalSeparator);
            if (decimalPos >= 0) {
                filterValue2 = filterValue2.substring(0, decimalPos) + "." + filterValue2.substring(decimalPos + 1, filterValue2.length());
            }
            filterCriteria.setFilterValues(filterValue1, filterValue2);
        } else {
            int decimalPos = filterVal.indexOf(decimalSeparator);
            if (decimalPos >= 0) {
                filterVal = filterVal.substring(0, decimalPos) + "." + filterVal.substring(decimalPos + 1, filterVal.length());
            }
            filterCriteria.setFilterValue(filterVal);
        }
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
                        newItems[j] = value.toString();
                        newItems[j] = newItems[j].replaceAll("\"", "");
                        
                        String fieldTypeVal = null;
                        DataSourceField field = dataSource.getField(val.isString().stringValue());
                        if (field != null) {
                            fieldTypeVal = field.getAttribute("fieldType");
                        }
                        SupportedFieldType fieldType = fieldTypeVal==null?SupportedFieldType.STRING:SupportedFieldType.valueOf(fieldTypeVal);
                        if (fieldType != null) {
                            switch (fieldType) {
                                case DECIMAL:
                                    processFilterValueClause(filterCriteria, newItems[j]);
                                    break;
                                case INTEGER:
                                    processFilterValueClause(filterCriteria, newItems[j]);
                                    break;
                                case MONEY:
                                    processFilterValueClause(filterCriteria, newItems[j]);
                                    break;
                                case DATE:
                                    if (newItems.length > 1) {
                                        for (int x=0;x<newItems.length;x++) {
                                            newItems[x] = updateMinutesFromDateFilter(newItems[x], x);
                                        }
                                        filterCriteria.setFilterValues(newItems);
                                    } else {
                                        String[] dateItems = new String[2];
                                        JSONValue operator = itemObj.get("operator");
                                        String op = operator.isString().stringValue();
                                        if (op.startsWith("greater")) {
                                            dateItems[0] = newItems[0];
                                            dateItems[1] = null;
                                        } else {
                                            dateItems[0] = null;
                                            dateItems[1] = newItems[0];
                                        }
                                        for (int x=0;x<dateItems.length;x++) {
                                            dateItems[x] = updateMinutesFromDateFilter(dateItems[x], x);
                                        }
                                        filterCriteria.setFilterValues(dateItems);
                                    }
                                    break;
                                default:
                                    filterCriteria.setFilterValues(newItems);
                                    break;
                            }
                        } else {
                            filterCriteria.setFilterValues(newItems);
                        }
					}
				}
			}
		}
    }
    
    protected String updateMinutesFromDateFilter(String originalDateString, int position) {
        // Default the timezone to the server's current timezone
        String timezone = DateTimeFormat.getFormat("Z").format(new Date());
        if (originalDateString != null) {
            int pos = originalDateString.indexOf("T");
            
            // Possibly adjust the timezone for the date the user selected
            Date date = DateTimeFormat.getFormat("yyyy-MM-dd").parse(originalDateString.substring(0, pos));
            if (date != null) {
                timezone = DateTimeFormat.getFormat("Z").format(date);
            }
            
            switch (position) {
                case 0: 
                    if (pos >= 0) {
                        return originalDateString.substring(0, pos) + "T00:00:00" + " " + timezone;
                    }
                    break;
                default:
                    if (pos >= 0) {
                        return originalDateString.substring(0, pos) + "T23:59:00" + " " + timezone;
                    }
                    break;
            }
        }
        return originalDateString == null?null : originalDateString + " " + timezone;
    }
    
    public boolean isCompatible(OperationType operationType) {
    	return OperationType.BASIC.equals(operationType) || OperationType.NONDESTRUCTIVEREMOVE.equals(operationType);
    }
    
    public void executeFetch(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb) {
    	BLCMain.NON_MODAL_PROGRESS.startProgress();
        if (request.getCriteria() != null && request.getCriteria().getAttribute("blc.fetch.from.cache") != null) {
            Criteria currentCriteria = request.getCriteria();
            String cacheFetchId = currentCriteria.getAttribute("blc.fetch.from.cache");
            Record cachedData = dataSource.getAddedRecord();
            if (cachedData != null) {
                Record[] recordList = new Record[]{cachedData};
                response.setData(recordList);
                response.setTotalRows(1);
                if (cb != null) {
                    cb.onSuccess(dataSource);
                }
                dataSource.processResponse(requestId, response);
            } else {
                throw new RuntimeException("Unable to find cached record with id: " + cacheFetchId);
            }
        } else {
            CriteriaTransferObject cto = getCto(request);
            service.fetch(new PersistencePackage(ceilingEntityFullyQualifiedClassname, fetchTypeFullyQualifiedClassname, null, persistencePerspective, customCriteria, BLCMain.csrfToken), cto, new EntityServiceAsyncCallback<DynamicResultSet>(EntityOperationType.FETCH, requestId, request, response, dataSource) {
                public void onSuccess(DynamicResultSet result) {
                    super.onSuccess(result);
                    TreeNode[] recordList = buildRecords(result, null);
                    response.setData(recordList);
                    response.setTotalRows(result.getTotalRecords());
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
	}
    
    public void executeAdd(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb) {
    	BLCMain.NON_MODAL_PROGRESS.startProgress();
		JavaScriptObject data = request.getData();
        TreeNode record = new TreeNode(data);
        Entity entity = buildEntity(record, request);
        service.add(new PersistencePackage(ceilingEntityFullyQualifiedClassname, entity, persistencePerspective, customCriteria, BLCMain.csrfToken), new EntityServiceAsyncCallback<Entity>(EntityOperationType.ADD, requestId, request, response, dataSource) {
			public void onSuccess(Entity result) {
				super.onSuccess(result);
                if (processResult(result, requestId, response, dataSource)) {
                    TreeNode record = (TreeNode) buildRecord(result, false);
                    TreeNode[] recordList = new TreeNode[]{record};
                    response.setData(recordList);
                    if (cb != null) {
                        cb.onSuccess(dataSource);
                    }
                    dataSource.processResponse(requestId, response);
                }
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
    
    public void executeUpdate(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb) {
    	BLCMain.NON_MODAL_PROGRESS.startProgress();
		JavaScriptObject data = request.getData();
        final TreeNode record = new TreeNode(data);
        Entity entity = buildEntity(record, request);
		String componentId = request.getComponentId();
        if (componentId != null) {
            if (entity.getType() == null) {
            	String[] type = ((ListGrid) Canvas.getById(componentId)).getSelectedRecord().getAttributeAsStringArray("_type");
            	entity.setType(type);
            }
        }
        service.update(new PersistencePackage(ceilingEntityFullyQualifiedClassname, entity, persistencePerspective, customCriteria, BLCMain.csrfToken), new EntityServiceAsyncCallback<Entity>(EntityOperationType.UPDATE, requestId, request, response, dataSource) {
			public void onSuccess(Entity result) {
				super.onSuccess(null);
                if (processResult(result, requestId, response, dataSource)) {
                    TreeNode record = (TreeNode) buildRecord(result, false);
                    TreeNode[] recordList = new TreeNode[]{record};
                    response.setData(recordList);

                    if (cb != null) {
                        cb.onSuccess(dataSource);
                    }
                    dataSource.processResponse(requestId, response);
                }
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
    
    public void executeRemove(final String requestId, final DSRequest request, final DSResponse response, final String[] customCriteria, final AsyncCallback<DataSource> cb) {
    	BLCMain.NON_MODAL_PROGRESS.startProgress();
		JavaScriptObject data = request.getData();
        TreeNode record = new TreeNode(data);
        Entity entity = buildEntity(record, request);
		String componentId = request.getComponentId();
        if (componentId != null) {
            if (entity.getType() == null) {
            	String[] type = ((ListGrid) Canvas.getById(componentId)).getSelectedRecord().getAttributeAsStringArray("_type");
            	entity.setType(type);
            }
        }
        service.remove(new PersistencePackage(ceilingEntityFullyQualifiedClassname, entity, persistencePerspective, customCriteria, BLCMain.csrfToken), new EntityServiceAsyncCallback<Void>(EntityOperationType.REMOVE, requestId, request, response, dataSource) {
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
    
    public Record buildRecord(Entity entity, Boolean updateId) {
		TreeNode record = new TreeNode();
		return updateRecord(entity, record, updateId);
	}

	public Record updateRecord(Entity entity, Record record, Boolean updateId) {
		String id = entity.findProperty(dataSource.getPrimaryKeyFieldName()).getValue();
		if (updateId) {
			id = id + "_^_" + loadLevelCount;
			loadLevelCount++;
		}
		for (Property property : entity.getProperties()){
			String attributeName = property.getName();
            if (dataSource.getField(attributeName) != null) {
                if (
                    property.getValue() != null &&
                    dataSource.getField(attributeName).getType().equals(FieldType.DATETIME)
                ) {
                    if (property.getValue() != null && !property.getValue().equals("null")) {
                        record.setAttribute(attributeName, formatter.parse(property.getValue()));
                    }
                } else if (
                    dataSource.getField(attributeName).getType().equals(FieldType.BOOLEAN)
                ) {
                    if (property.getValue() == null) {
                        record.setAttribute(attributeName, false);
                    } else {
                        String lower = property.getValue().toLowerCase();
                        if (lower.equals("y") || lower.equals("yes") || lower.equals("true") || lower.equals("1")) {
                            record.setAttribute(attributeName, true);
                        } else {
                            record.setAttribute(attributeName, false);
                        }
                    }
                } else if (
                    dataSource.getField(attributeName).getAttributeAsString("fieldType").equals(SupportedFieldType.PASSWORD.toString())
                ) {
                    String propertyValue = property.getValue();
                    record.setAttribute(attributeName, propertyValue);
                    if (dataSource.getField(attributeName).getValidators() != null && dataSource.getField(attributeName).getValidators().length > 0) {
                        for (Validator validator : dataSource.getField(attributeName).getValidators()) {
                            if (validator.getAttribute("type").equals("matchesField") && validator.getAttribute("otherField") != null) {
                                record.setAttribute(validator.getAttribute("otherField"), propertyValue);
                                break;
                            }
                        }
                    }
                } else if (
                    property.getMetadata() != null && ((BasicFieldMetadata) property.getMetadata()).getFieldType() != null &&
                    ((BasicFieldMetadata) property.getMetadata()).getFieldType().equals(SupportedFieldType.FOREIGN_KEY)
                ) {
                    record.setAttribute(attributeName, linkedValue);
                } else if (
                    property.getValue() != null &&
                    dataSource.getField(attributeName).getType().equals(FieldType.FLOAT)
                ) {
                    String propertyValue = property.getValue();
                    record.setAttribute(attributeName, propertyValue==null?null:Double.parseDouble(String.valueOf(propertyValue)));
                } else {
                    String propertyValue;
                    if (property.getName().equals(dataSource.getPrimaryKeyFieldName())) {
                        record.setAttribute(dataSource.getPrimaryKeyFieldName(), id);
                    } else {
                        propertyValue = property.getValue();
                        record.setAttribute(attributeName, propertyValue);
                    }
                }
            }
			if (property.getDisplayValue() != null) {
				record.setAttribute("__display_"+attributeName, property.getDisplayValue());
			}
            //if (property.getIsDirty()) {
                //record.setAttribute("_hilite", "listGridDirtyPropertyHilite");
                //record.setAttribute("__dirty_"+attributeName, true);
            //}
		}
        if (entity.isDirty()) {
            record.setAttribute("_hilite", "listGridDirtyPropertyHilite");
            record.setAttribute("__dirty", true);
        }
		String[] entityType = entity.getType();
		record.setAttribute("_type", entityType);
        if (!entity.isDirty()) {
            if (entity.getInactive()) {
                record.setAttribute("_hilite", "listGridInActivePropertyHilite");
            }
            if (entity.getDeleted()) {
                record.setAttribute("_hilite", "listGridDeletedPropertyHilite");
            }
        }
        if (entity.getLocked()) {
            record.setAttribute("_hilite", "listGridLockedPropertyHilite");
            record.setAttribute("__locked", true);
            record.setAttribute("__lockedUserName", entity.getLockedBy()==null?"":entity.getLockedBy());
            record.setAttribute("__lockedDate", entity.getLockedDate()==null?"":entity.getLockedDate());
        }
		return record;
	}
    
    public TreeNode[] buildRecords(DynamicResultSet result, String[] filterOutIds) {
		List<TreeNode> recordList = new ArrayList<TreeNode>();
		int decrement = 0;
		for (Entity entity : result.getRecords()){
			if (filterOutIds == null || (filterOutIds != null && Arrays.binarySearch(filterOutIds, entity.findProperty(dataSource.getPrimaryKeyFieldName()).getValue()) < 0)) {
				TreeNode record = (TreeNode) buildRecord(entity, false);
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
    
    public Entity buildEntity(Record record, DSRequest request) {
		Entity entity = new Entity();
		//Map<String, Object> dirtyValues = request.getAttributeAsMap("dirtyValues");
		List<Property> properties = new ArrayList<Property>();
		String[] attributes = record.getAttributes();
		for (String attribute : attributes) {
			if (!attribute.equals("_type") && !attribute.startsWith("__") && dataSource.getField(attribute) != null) {
				Property property = new Property();
				if (record.getAttribute(attribute) != null && dataSource.getField(attribute) != null && dataSource.getField(attribute).getType().equals(FieldType.DATETIME)) {
					property.setValue(formatter.format(record.getAttributeAsDate(attribute)));
                } else if (linkedValue != null && dataSource.getField(attribute).getAttribute("fieldType") != null && SupportedFieldType.valueOf(dataSource.getField(attribute).getAttribute("fieldType")).equals(SupportedFieldType.FOREIGN_KEY)) {
					property.setValue(dataSource.stripDuplicateAllowSpecialCharacters(linkedValue));
                    property.setIsDirty(true);
				} else {
					property.setValue(dataSource.stripDuplicateAllowSpecialCharacters(record.getAttribute(attribute)));
				}
				property.setName(dataSource.getField(attribute).getAttribute("rawName"));
				//if (dirtyValues != null && dirtyValues.containsKey(property.getName())) {
					//property.setIsDirty(true);
				//}
				properties.add(property);
			} else if (attribute.equals("_type")) {
                entity.setType(record.getAttributeAsStringArray("_type"));
            }
		}
		
		Property fullyQualifiedName = new Property();
		fullyQualifiedName.setName("ceilingEntityFullyQualifiedClassname");
		fullyQualifiedName.setValue(ceilingEntityFullyQualifiedClassname);
        fullyQualifiedName.setIsDirty(true);
		properties.add(fullyQualifiedName);
		
		Property[] props = new Property[properties.size()];
		props = properties.toArray(props);
		entity.setProperties(props);
		
		return entity;
	}
    
    public void buildFields(final String[] customCriteria, final Boolean overrideFieldSort, final AsyncCallback<DataSource> cb) {
        AppServices.DYNAMIC_ENTITY.inspect(new PersistencePackage(ceilingEntityFullyQualifiedClassname, null, persistencePerspective, customCriteria, BLCMain.csrfToken), new AbstractCallback<DynamicResultSet>() {

            @Override
            protected void onOtherException(Throwable exception) {
                super.onOtherException(exception);
                if (cb != null) {
                    cb.onFailure(exception);
                }
            }

            @Override
            protected void onSecurityException(ApplicationSecurityException exception) {
                super.onSecurityException(exception);
                if (cb != null) {
                    cb.onFailure(exception);
                }
            }

            public void onSuccess(DynamicResultSet result) {
                super.onSuccess(result);
                ClassMetadata metadata = result.getClassMetaData();
                filterProperties(metadata, new MergedPropertyType[]{MergedPropertyType.PRIMARY, MergedPropertyType.ADORNEDTARGETLIST}, overrideFieldSort, ((AsyncCallbackAdapter) cb).getDataSourceSetupManager());

                //Add a hidden field to store the polymorphic type for this entity
                DataSourceField typeField = new DataSourceTextField("_type");
                typeField.setCanEdit(false);
                typeField.setHidden(true);
                typeField.setAttribute("permanentlyHidden", true);
                dataSource.addField(typeField);
                dataSource.setPolymorphicEntityTree(metadata.getPolymorphicEntities());
                dataSource.setDefaultNewEntityFullyQualifiedClassname(dataSource.getPolymorphicEntities().keySet().iterator().next());

                if (cb != null) {
                    cb.onSuccess(dataSource);
                }
            }

        });
	}
    
    protected OperatorId[] getBasicIdOperators() {
    	return new OperatorId[]{OperatorId.CONTAINS, OperatorId.EQUALS, OperatorId.GREATER_OR_EQUAL, OperatorId.GREATER_THAN, OperatorId.NOT_EQUAL, OperatorId.LESS_OR_EQUAL, OperatorId.LESS_THAN};
    }
    
    protected OperatorId[] getBasicBooleanOperators() {
    	return new OperatorId[]{OperatorId.EQUALS, OperatorId.NOT_EQUAL, OperatorId.NOT_NULL, OperatorId.EQUALS_FIELD, OperatorId.NOT_EQUAL_FIELD};
    }
    
    protected OperatorId[] getBasicDateOperators() {
    	return new OperatorId[]{OperatorId.EQUALS, OperatorId.GREATER_OR_EQUAL, OperatorId.GREATER_THAN, OperatorId.NOT_EQUAL, OperatorId.LESS_OR_EQUAL, OperatorId.LESS_THAN, OperatorId.NOT_NULL, OperatorId.EQUALS_FIELD, OperatorId.GREATER_OR_EQUAL_FIELD, OperatorId.GREATER_THAN_FIELD, OperatorId.LESS_OR_EQUAL_FIELD, OperatorId.LESS_THAN_FIELD, OperatorId.NOT_EQUAL_FIELD};
    }
    
    protected OperatorId[] getBasicNumericOperators() {
    	return new OperatorId[]{OperatorId.EQUALS, OperatorId.GREATER_OR_EQUAL, OperatorId.GREATER_THAN, OperatorId.NOT_EQUAL, OperatorId.LESS_OR_EQUAL, OperatorId.LESS_THAN, OperatorId.NOT_NULL, OperatorId.EQUALS_FIELD, OperatorId.GREATER_OR_EQUAL_FIELD, OperatorId.GREATER_THAN_FIELD, OperatorId.LESS_OR_EQUAL_FIELD, OperatorId.LESS_THAN_FIELD, OperatorId.NOT_EQUAL_FIELD, OperatorId.IN_SET, OperatorId.NOT_IN_SET};
    }
    
    protected OperatorId[] getBasicTextOperators() {
    	return new OperatorId[]{OperatorId.CONTAINS, OperatorId.NOT_CONTAINS, OperatorId.STARTS_WITH, OperatorId.ENDS_WITH, OperatorId.NOT_STARTS_WITH, OperatorId.NOT_ENDS_WITH, OperatorId.EQUALS, OperatorId.NOT_EQUAL, OperatorId.NOT_NULL, OperatorId.EQUALS_FIELD, OperatorId.NOT_EQUAL_FIELD, OperatorId.IN_SET, OperatorId.NOT_IN_SET};
    }
    
    protected OperatorId[] getBasicEnumerationOperators() {
    	return new OperatorId[]{OperatorId.EQUALS, OperatorId.NOT_EQUAL, OperatorId.NOT_NULL, OperatorId.EQUALS_FIELD, OperatorId.NOT_EQUAL_FIELD};
    }
	
	protected void filterProperties(ClassMetadata metadata, final MergedPropertyType[] includeTypes, Boolean overrideFieldSort, final PresenterSequenceSetupManager presenterSequenceSetupManager) throws IllegalStateException {
		if (BLCMain.isLogDebugEnabled("classmetadata")) {
			Map<String, List<String>> props = new HashMap<String, List<String>>();
			for (Property property : metadata.getProperties()) {
				String type = property.getMetadata().getInheritedFromType();
				List<String> myProps = props.get(type);
				if (myProps == null) {
					props.put(type, new ArrayList<String>());
					myProps = props.get(type);
				}
				myProps.add(property.getName());
			}
			for (String key: props.keySet()) {
				List<String> myProps = props.get(key);
				for (String prop : myProps) {
					BLCMain.logDebug(key + " : " + prop, "classmetadata");
				}
			}
		}
		//sort properties based on their display name
		Property[] properties = metadata.getProperties();
		if (overrideFieldSort) {
			Arrays.sort(properties, new Comparator<Property>() {
				public int compare(Property o1, Property o2) {
					if (o1.getMetadata().getFriendlyName() == null && o2.getMetadata().getFriendlyName() == null) {
						return 0;
					} else if (o1.getMetadata().getFriendlyName() == null) {
						return -1;
					} else if (o2.getMetadata().getFriendlyName() == null) {
						return 1;
					} else {
						return o1.getMetadata().getFriendlyName().compareTo(o2.getMetadata().getFriendlyName());
					}
				}
			});
		}
		for (final Property property : metadata.getProperties()) {
            property.getMetadata().accept(new MetadataVisitorAdapter() {
                @Override
                public void visit(BasicFieldMetadata metadata) {
                    String mergedPropertyType = metadata.getMergedPropertyType().toString();
                    if (Arrays.binarySearch(includeTypes, MergedPropertyType.valueOf(mergedPropertyType)) >= 0) {
                        Boolean isDirty = property.getIsDirty();
                        String rawName = property.getName();
                        String propertyName = rawName;
                        String fieldType = metadata.getFieldType()==null?null:metadata.getFieldType().toString();
                        String secondaryFieldType = metadata.getSecondaryType()==null?null:metadata.getSecondaryType().toString();
                        Long length = metadata.getLength()==null?null:metadata.getLength().longValue();
                        Boolean required;
                        if (metadata.getRequiredOverride() != null) {
                            required = metadata.getRequiredOverride();
                        } else {
                            required = metadata.getRequired();
                            if (required == null) {
                                required = false;
                            }
                        }
                        Boolean mutable = metadata.getMutable();
                        String inheritedFromType = metadata.getInheritedFromType();
                        String[] availableToTypes = metadata.getAvailableToTypes();
                        String foreignKeyClass = metadata.getForeignKeyClass();
                        String foreignKeyProperty = metadata.getForeignKeyProperty();
                        String friendlyName = metadata.getFriendlyName();
                        if (friendlyName == null || friendlyName.equals("")) {
                            friendlyName = property.getName();
                        } else {
                            friendlyName = getLocalizedString(friendlyName);
                        }
                        String securityLevel = metadata.getSecurityLevel();
                        VisibilityEnum visibility = metadata.getVisibility();
                        if (visibility == null) {
                            visibility = VisibilityEnum.HIDDEN_ALL;
                        }
                        Boolean hidden = visibility == VisibilityEnum.HIDDEN_ALL || visibility == VisibilityEnum.GRID_HIDDEN;
                        FormHiddenEnum formHidden;
                        switch (visibility) {
                            case FORM_HIDDEN:
                                formHidden = FormHiddenEnum.HIDDEN;
                                break;
                            default:
                                formHidden = FormHiddenEnum.NOT_SPECIFIED;
                                break;
                            case GRID_HIDDEN:
                                formHidden = FormHiddenEnum.VISIBLE;
                                break;
                        }
                        String group = metadata.getGroup();
                        if (group != null && !group.equals("")) {
                            group = getLocalizedString(group);
                        }
                        Integer groupOrder = metadata.getGroupOrder();
                        Boolean groupCollapsed = metadata.getGroupCollapsed();

                        String tooltip = metadata.getTooltip();
                        if (tooltip != null && !tooltip.equals("")) {
                            tooltip = getLocalizedString(tooltip);
                        }

                        String helpText = metadata.getHelpText();
                        if (helpText != null && !helpText.equals("")) {
                            helpText = getLocalizedString(helpText);
                        }

                        String hint = metadata.getHint();
                        if (hint != null && !hint.equals("")) {
                            hint = getLocalizedString(hint);
                        }

                        Boolean largeEntry = metadata.isLargeEntry();
                        Boolean prominent = metadata.isProminent();
                        Integer order = metadata.getOrder();
                        String columnWidth = metadata.getColumnWidth();
                        String[][] enumerationValues = metadata.getEnumerationValues();
                        String enumerationClass = metadata.getEnumerationClass();
                        Boolean canEditEnumeration = metadata.getOptionCanEditValues()!=null && metadata.getOptionCanEditValues();
                        if (mutable) {
                            Boolean isReadOnly = metadata.getReadOnly();
                            if (isReadOnly != null) {
                                mutable = !isReadOnly;
                            }
                        }
                        DataSourceField field;
                        switch(SupportedFieldType.valueOf(fieldType)){
                        case ID:
                            field = new DataSourceTextField(propertyName, friendlyName);
                            if (propertyName.indexOf(".") < 0) {
                                field.setPrimaryKey(true);
                            }
                            field.setCanEdit(false);
                            field.setRequired(required);
                            //field.setValidOperators(getBasicIdOperators());
                            break;
                        case BOOLEAN:
                            field = new DataSourceBooleanField(propertyName, friendlyName);
                            field.setCanEdit(mutable);
                            //field.setValidOperators(getBasicBooleanOperators());
                            break;
                        case DATE:
                            field = new DataSourceDateTimeField(propertyName, friendlyName);
                            field.setCanEdit(mutable);
                            field.setRequired(required);
                            //field.setValidOperators(getBasicDateOperators());
                            break;
                        case INTEGER:
                            field = new DataSourceIntegerField(propertyName, friendlyName);
                            field.setCanEdit(mutable);
                            field.setRequired(required);
                            //field.setValidOperators(getBasicNumericOperators());
                            break;
                        case DECIMAL:
                            field = new DataSourceFloatField(propertyName, friendlyName);
                            field.setCanEdit(mutable);
                            field.setRequired(required);
                            //field.setValidOperators(getBasicNumericOperators());
                            break;
                        case EMAIL:
                            field = new DataSourceTextField(propertyName, friendlyName);
                            field.setCanEdit(mutable);
                            field.setRequired(required);
                            //field.setValidOperators(getBasicTextOperators());
                            break;
                        case MONEY:
                            field = new DataSourceFloatField(propertyName, friendlyName);
                            field.setCanEdit(mutable);
                            field.setRequired(required);
                            //field.setValidOperators(getBasicNumericOperators());
                            break;
                        case FOREIGN_KEY:{
                            field = new DataSourceTextField(propertyName, friendlyName);
                            field.setCanEdit(mutable);
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
                            if (hidden == null) {
                                hidden = true;
                            }
                            field.setRequired(required);
                            //field.setValidOperators(getBasicNumericOperators());
                            break;}
                        case ADDITIONAL_FOREIGN_KEY:{
                            field = new DataSourceTextField(propertyName, friendlyName);
                            field.setCanEdit(mutable);
                            if (hidden == null) {
                                hidden = true;
                            }
                            field.setRequired(required);
                            if (metadata.getForeignKeyDisplayValueProperty() != null) {
                                ForeignKey foreignKey = new ForeignKey(foreignKeyProperty, foreignKeyClass);
                                foreignKey.setDisplayValueProperty(metadata.getForeignKeyDisplayValueProperty());
                                LookupMetadata lookupMetadata = new LookupMetadata();
                                lookupMetadata.setLookupForeignKey(foreignKey);
                                lookupMetadata.setParentDataSourceName(metadata.getLookupParentDataSourceName());
                                lookupMetadata.setTargetDynamicFormDisplayId(metadata.getTargetDynamicFormDisplayId());
                                lookupMetadata.setFriendlyName(friendlyName);
                                lookupMetadata.setFieldType(SupportedFieldType.ADDITIONAL_FOREIGN_KEY);
                                DynamicEntityPresenter.lookupMetadatas.put(presenterSequenceSetupManager.getPresenter().getClass().getName() + "_" + property.getName(), lookupMetadata);
                            }
                            //field.setValidOperators(getBasicNumericOperators());
                            break;}
                        case BROADLEAF_ENUMERATION:{
                            if (canEditEnumeration) {
                                field = new DataSourceTextField(propertyName, friendlyName);
                                field.setCanEdit(mutable);
                                field.setRequired(required);
                                ComboBoxItem item = new ComboBoxItem();
                                item.setDefaultToFirstOption(true);
                                LinkedHashMap<String,String> valueMap = new LinkedHashMap<String,String>();
                                for (String[] enumerationValue : enumerationValues) {
                                    valueMap.put(enumerationValue[0], enumerationValue[1]);
                                }
                                field.setValueMap(valueMap);
                                field.setEditorType(item);
                            } else {
                                field = new DataSourceEnumField(propertyName, friendlyName);
                                field.setCanEdit(mutable);
                                field.setRequired(required);
                                LinkedHashMap<String,String> valueMap = new LinkedHashMap<String,String>();
                                for (String[] enumerationValue : enumerationValues) {
                                    valueMap.put(enumerationValue[0], enumerationValue[1]);
                                }
                                field.setValueMap(valueMap);
                            }
                            //field.setValidOperators(getBasicEnumerationOperators());
                            break;}
                        case EXPLICIT_ENUMERATION:{
                            if (canEditEnumeration) {
                                field = new DataSourceTextField(propertyName, friendlyName);
                                field.setCanEdit(mutable);
                                field.setRequired(required);
                                ComboBoxItem item = new ComboBoxItem();
                                item.setDefaultToFirstOption(true);
                                LinkedHashMap<String,String> valueMap = new LinkedHashMap<String,String>();
                                for (String[] enumerationValue : enumerationValues) {
                                    valueMap.put(enumerationValue[0], enumerationValue[1]);
                                }
                                field.setValueMap(valueMap);
                                field.setEditorType(item);
                            } else {
                                field = new DataSourceEnumField(propertyName, friendlyName);
                                field.setCanEdit(mutable);
                                field.setRequired(required);
                                LinkedHashMap<String,String> valueMap = new LinkedHashMap<String,String>();
                                for (String[] enumerationValue : enumerationValues) {
                                    valueMap.put(enumerationValue[0], enumerationValue[1]);
                                }
                                field.setValueMap(valueMap);
                            }
                            //field.setValidOperators(getBasicEnumerationOperators());
                            break;}
                        case DATA_DRIVEN_ENUMERATION:{
                            if (canEditEnumeration) {
                                field = new DataSourceTextField(propertyName, friendlyName);
                                field.setCanEdit(mutable);
                                field.setRequired(required);
                                ComboBoxItem item = new ComboBoxItem();
                                item.setDefaultToFirstOption(true);
                                LinkedHashMap<String,String> valueMap = new LinkedHashMap<String,String>();
                                for (String[] enumerationValue : enumerationValues) {
                                    valueMap.put(enumerationValue[0], enumerationValue[1]);
                                }
                                field.setValueMap(valueMap);
                                field.setEditorType(item);
                            } else {
                                field = new DataSourceEnumField(propertyName, friendlyName);
                                field.setCanEdit(mutable);
                                field.setRequired(required);
                                LinkedHashMap<String,String> valueMap = new LinkedHashMap<String,String>();
                                for (String[] enumerationValue : enumerationValues) {
                                    valueMap.put(enumerationValue[0], enumerationValue[1]);
                                }
                                field.setValueMap(valueMap);
                            }
                            //field.setValidOperators(getBasicEnumerationOperators());
                            break;}
                        case PASSWORD:
                            field = new DataSourcePasswordField(propertyName, friendlyName);
                            field.setCanEdit(mutable);
                            field.setRequired(required);
                            //field.setValidOperators(getBasicTextOperators());
                            break;
                        case ASSET:
                            field = new DataSourceImageField(propertyName, friendlyName);
                            field.setCanEdit(mutable);
                            field.setRequired(required);
                            break;
                        default:
                            field = new DataSourceTextField(propertyName, friendlyName);
                            field.setCanEdit(mutable);
                            field.setRequired(required);
                            //field.setValidOperators(getBasicTextOperators());
                            break;
                        }
                        field.setAttribute("friendlyName", friendlyName);
                        if (metadata.getValidationConfigurations().size() > 0) {
                            field.setValidators(ValidationFactoryManager.getInstance().createValidators(metadata.getValidationConfigurations(), propertyName));
                        }
                        if (fieldType.equals(SupportedFieldType.ID.toString())) {
                            field.setHidden(hidden);
                            field.setAttribute("permanentlyHidden", hidden);
                            formHidden = FormHiddenEnum.VISIBLE;
                        } else if (hidden != null) {
                            field.setHidden(hidden);
                            field.setAttribute("permanentlyHidden", hidden);
                        } else if (field.getAttribute("permanentlyHidden")==null){
                            field.setHidden(false);
                            field.setAttribute("permanentlyHidden", false);
                        }

                        if (securityLevel != null && !"".equals(securityLevel)){
                            String uniqueID = ceilingEntityFullyQualifiedClassname + field.getName();
                            org.broadleafcommerce.openadmin.client.security.SecurityManager.getInstance().registerField(uniqueID, securityLevel);
                            field.setAttribute("uniqueID", uniqueID);
                            field.setAttribute("securityLevel", securityLevel);
                        }
                        field.setAttribute("formHidden", formHidden);
                        if (group != null) {
                            field.setAttribute("formGroup", group);
                        }
                        if (groupOrder != null) {
                            field.setAttribute("formGroupOrder", groupOrder);
                        }
                        if (groupCollapsed != null) {
                            field.setAttribute("formGroupCollapsed", groupCollapsed);
                        }
                        if (tooltip != null) {
                            field.setPrompt(tooltip);
                        }
                        if (helpText != null) {
                            field.setAttribute("helpText", helpText);
                        }
                        if (hint != null) {
                            field.setAttribute("hint", hint);
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
                        if (columnWidth != null) {
                            field.setAttribute("columnWidth", columnWidth);
                        }
                        if (enumerationValues != null) {
                            field.setAttribute("enumerationValues", enumerationValues);
                        }
                        if (enumerationClass != null) {
                            field.setAttribute("enumerationClass", enumerationClass);
                        }
                        field.setAttribute("canEditEnumeration", canEditEnumeration);
                        if (isDirty != null) {
                            field.setAttribute("isEdited", isDirty);
                        } else {
                            field.setAttribute("isEdited", false);
                        }
                        field.setAttribute("inheritedFromType", inheritedFromType);
                        field.setAttribute("availableToTypes", availableToTypes);
                        field.setAttribute("fieldType", fieldType);
                        field.setAttribute("secondaryFieldType", secondaryFieldType);
                        field.setAttribute("mergedPropertyType", mergedPropertyType);
                        field.setAttribute("rawName", rawName);
                        dataSource.addField(field);
                    }
                }

                @Override
                public void visit(final BasicCollectionMetadata metadata) {
                    DynamicEntityPresenter.collectionMetadatas.put(presenterSequenceSetupManager.getPresenter().getClass().getName() + "_" + property.getName(), metadata);
                }

                @Override
                public void visit(AdornedTargetCollectionMetadata metadata) {
                    DynamicEntityPresenter.collectionMetadatas.put(presenterSequenceSetupManager.getPresenter().getClass().getName() + "_" + property.getName(), metadata);
                }

                @Override
                public void visit(MapMetadata metadata) {
                    DynamicEntityPresenter.collectionMetadatas.put(presenterSequenceSetupManager.getPresenter().getClass().getName() + "_" + property.getName(), metadata);
                }
            });
		}
        dataSource.setAttribute("blcCurrencyCode", metadata.getCurrencyCode(), true);
	}
	
	/**
	 * Looks up the given value as a key via the MessageManager. If it's not
	 * found, return the original value passed in
	 * 
	 * @return the message for the key specified by <b>value</b> if it exists
	 * or <b>value</b> if it does not
	 */
	public String getLocalizedString(String value) {
	    String result = value;
	    try {
            //check if the value name is an i18N key
            String val = BLCMain.getMessageManager().getString(result);
            if (val != null) {
                result = val;
            }
	    } catch (MissingResourceException e) {
            //do nothing
        }
        return result;
    }

	public void setDataSource(AbstractDynamicDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getCeilingEntityFullyQualifiedClassname() {
		return ceilingEntityFullyQualifiedClassname;
	}
	
}
