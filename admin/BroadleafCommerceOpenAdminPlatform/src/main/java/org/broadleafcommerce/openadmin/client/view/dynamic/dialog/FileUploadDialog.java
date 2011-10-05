/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.openadmin.client.view.dynamic.dialog;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Encoding;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.JSON;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.module.DataSourceModule;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.OperationType;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.client.event.NewItemCreatedEvent;
import org.broadleafcommerce.openadmin.client.event.NewItemCreatedEventHandler;
import org.broadleafcommerce.openadmin.client.setup.AppController;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.FormBuilder;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.upload.UploadStatusProgress;

import java.util.Map;

/**
 * 
 * @author jfischer
 *
 */
public class FileUploadDialog extends Window {

	private DynamicForm dynamicForm;
	private NewItemCreatedEventHandler handler;
    private Element synthesizedFrame;
    private IButton saveButton;
    private IButton cancelButton;

	public FileUploadDialog() {
		this.setIsModal(true);
		this.setShowModalMask(true);
		this.setShowMinimizeButton(false);
		this.setAutoSize(true);
		this.setCanDragResize(true);
		this.setOverflow(Overflow.HIDDEN);
		
		VStack stack = new VStack();
        stack.setWidth(630);
        stack.setHeight(300);
		dynamicForm = new DynamicForm();
        dynamicForm.setEncoding(Encoding.MULTIPART);
        dynamicForm.setTarget("hidden_frame");
        dynamicForm.setAction("cms.upload.service");
		dynamicForm.setNumCols(3);
        dynamicForm.setPadding(10);
        dynamicForm.setHeight100();
        stack.addMember(dynamicForm);

        cancelButton = new IButton("Cancel");
        cancelButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	hide();
            }
        });

        saveButton = new IButton("Upload");
        saveButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {  
            	if (dynamicForm.validate()) {
                    String callbackName = JavaScriptMethodHelper.registerCallbackFunction(new JavaScriptMethodCallback() {
                        public void execute(JavaScriptObject obj) {
                            try {
                                String jsObj = JSON.encode(obj);
                                JSONObject entityJs = JSONParser.parse(jsObj).isObject();
                                JSONValue errorJs = entityJs.get("error");

                                if (errorJs != null) {
                                    SC.warn(errorJs.isString().stringValue());
                                } else {
                                    Entity entity = new Entity();
                                    String type = entityJs.get("type").isString().stringValue();
                                    entity.setType(new String[]{type});

                                    JSONArray propArrayJs = entityJs.get("properties").isArray();
                                    int length = propArrayJs.size();
                                    Property[] props = new Property[length];
                                    for (int j=0; j<=length-1; j++) {
                                        JSONObject propJs = propArrayJs.get(j).isObject();
                                        Property property = new Property();
                                        property.setName(propJs.get("name").isString().stringValue());
                                        property.setValue(propJs.get("value").isString().stringValue());
                                        props[j] = property;
                                    }
                                    entity.setProperties(props);
                                    DataSourceModule module = ((DynamicEntityDataSource) dynamicForm.getDataSource()).getCompatibleModule(OperationType.ENTITY);
                                    Record record = module.buildRecord(entity, false);
                                    if (handler != null) {
                                        AppController.getInstance().getEventBus().addHandler(NewItemCreatedEvent.TYPE, handler);
                                        try {
                                            AppController.getInstance().getEventBus().fireEvent(new NewItemCreatedEvent((ListGridRecord) record, dynamicForm.getDataSource()));
                                        } finally {
                                            AppController.getInstance().getEventBus().removeHandler(NewItemCreatedEvent.TYPE, handler);
                                        }
                                    }
                                }
                            } finally {
                                UploadStatusProgress progress = (UploadStatusProgress) ((CanvasItem) dynamicForm.getField("__display_file")).getCanvas();
                                progress.stopProgress();
                                Timer timer = new Timer() {
                                    public void run() {
                                        hide();
                                    }
                                };
                                timer.schedule(500);
                            }
                        }
                    });
                    UploadStatusProgress progress = (UploadStatusProgress) ((CanvasItem) dynamicForm.getField("__display_file")).getCanvas();
                    progress.setCallbackName(callbackName);
                    progress.startProgress();
                    dynamicForm.getField("callbackName").setValue(callbackName);
                    dynamicForm.submitForm();
                    saveButton.disable();
                    cancelButton.disable();
            	}
            }
        });
        
        VLayout vLayout = new VLayout();
        vLayout.setAlign(VerticalAlignment.BOTTOM);
        HLayout hLayout = new HLayout(10);
        hLayout.setAlign(Alignment.CENTER);  
        hLayout.addMember(saveButton);  
        hLayout.addMember(cancelButton);
        hLayout.setLayoutTopMargin(20);
        hLayout.setLayoutBottomMargin(20);
        vLayout.addMember(hLayout);
        stack.addMember(vLayout);

        addItem(stack);
        
        createFrame();
	}

    private void createFrame() {
        // Attach a hidden IFrame to the form. This is the target iframe to
        // which
        // the form will be submitted. We have to create the iframe using
        // innerHTML,
        // because setting an iframe's 'name' property dynamically doesn't work
        // on
        // most browsers.
        Element dummy = Document.get().createDivElement();
        dummy.setInnerHTML("<iframe src=\"javascript:''\" name='hidden_frame' style='position:absolute;width:0;height:0;border:0'>");

        synthesizedFrame = dummy.getFirstChildElement();
        Document.get().getBody().appendChild(synthesizedFrame);
    }
	
	@SuppressWarnings("rawtypes")
	public void editNewRecord(DynamicEntityDataSource dataSource, Map initialValues, NewItemCreatedEventHandler handler, String[] fieldNames) {
		editNewRecord(null, dataSource, initialValues, handler, null, fieldNames, null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void editNewRecord(String title, DynamicEntityDataSource dataSource, Map initialValues, NewItemCreatedEventHandler handler, String heightOverride, String[] fieldNames, String[] ignoreFields) {
		initialValues.put(dataSource.getPrimaryKeyFieldName(), "");
		this.handler = handler;
		if (heightOverride != null) {
			setHeight(heightOverride);
		}
		if (fieldNames != null && fieldNames.length > 0) {
			dataSource.resetVisibilityOnly(fieldNames);
		} else {
			dataSource.resetPermanentFieldVisibility();
		}
		if (ignoreFields != null) {
			for (String fieldName : ignoreFields) {
				dataSource.getField(fieldName).setHidden(true);
			}
		}
		if (title != null) {
			this.setTitle(title);
		} else {
			this.setTitle("Add new entity: " + dataSource.getPolymorphicEntities().get(dataSource.getDefaultNewEntityFullyQualifiedClassname()));
		}
		buildFields(dataSource, dynamicForm);
        dynamicForm.editNewRecord(initialValues);
        centerInPage();
		show();
        saveButton.enable();
        cancelButton.enable();
	}
	
	protected void buildFields(DataSource dataSource, DynamicForm dynamicForm) {
		FormBuilder.buildForm(dataSource, dynamicForm, false);
	}
}
