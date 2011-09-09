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
import com.google.gwt.json.client.JSONObject;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Encoding;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.dto.jso.EntityJSO;
import org.broadleafcommerce.openadmin.client.event.NewItemCreatedEventHandler;
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

	public FileUploadDialog() {
		this.setIsModal(true);
		this.setShowModalMask(true);
		this.setShowMinimizeButton(false);
		this.setWidth(600);
		this.setAutoHeight();
		this.setHeight(320);
		this.setCanDragResize(true);
		this.setOverflow(Overflow.HIDDEN);
		
		VStack stack = new VStack();
		stack.setWidth100();
		stack.setLayoutRightMargin(20);

		dynamicForm = new DynamicForm();
        dynamicForm.setEncoding(Encoding.MULTIPART);
        dynamicForm.setTarget("hidden_frame");
        dynamicForm.setAction("cms.upload.service");
		dynamicForm.setNumCols(4);
        dynamicForm.setPadding(10);
        stack.addMember(dynamicForm);
        addItem(stack);
        
        IButton saveButton = new IButton("Upload");
        saveButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {  
            	if (dynamicForm.validate()) {
                    String callbackName = JavaScriptMethodHelper.registerCallbackFunction(new JavaScriptMethodCallback() {
                        public void execute(JavaScriptObject obj) {
                            EntityJSO jso = EntityJSO.buildEntity(new JSONObject(obj).toString());
                            //uploadFinished(obj);
                            UploadStatusProgress progress = (UploadStatusProgress) ((CanvasItem) dynamicForm.getField("__display_file")).getCanvas();
                            progress.stopProgress();
                            SC.say("Uploaded!");
                        }
                    });
                    UploadStatusProgress progress = (UploadStatusProgress) ((CanvasItem) dynamicForm.getField("__display_file")).getCanvas();
                    progress.setCallbackName(callbackName);
                    progress.startProgress();
                    dynamicForm.getField("callbackName").setValue(callbackName);
                    dynamicForm.submitForm();

            		/*dynamicForm.saveData(new DSCallback() {
						public void execute(DSResponse response, Object rawData, DSRequest request) {
							TreeNode record = new TreeNode(request.getData());
							if (handler != null) {
								AppController.getInstance().getEventBus().addHandler(NewItemCreatedEvent.TYPE, handler);
								try {
									AppController.getInstance().getEventBus().fireEvent(new NewItemCreatedEvent((ListGridRecord) record, dynamicForm.getDataSource()));
								} finally {
									AppController.getInstance().getEventBus().removeHandler(NewItemCreatedEvent.TYPE, handler);
								}
							}
						}
            		});*/
            		//hide();
            	}
            }
        });  

        IButton cancelButton = new IButton("Cancel");  
        cancelButton.addClickHandler(new ClickHandler() {  
            public void onClick(ClickEvent event) {  
            	hide();
            }  
        });
        
        VLayout vLayout = new VLayout();
        vLayout.setHeight100();
        vLayout.setAlign(VerticalAlignment.BOTTOM);
        HLayout hLayout = new HLayout(10); 
        hLayout.setAlign(Alignment.CENTER);  
        hLayout.addMember(saveButton);  
        hLayout.addMember(cancelButton);
        hLayout.setLayoutTopMargin(40);
        hLayout.setLayoutBottomMargin(40);
        vLayout.addMember(hLayout);
        addItem(vLayout);
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
	}
	
	protected void buildFields(DataSource dataSource, DynamicForm dynamicForm) {
		FormBuilder.buildForm(dataSource, dynamicForm, false);
	}
}
