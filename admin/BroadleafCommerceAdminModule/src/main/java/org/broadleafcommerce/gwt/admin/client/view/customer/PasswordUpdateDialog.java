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
package org.broadleafcommerce.gwt.admin.client.view.customer;

import org.broadleafcommerce.gwt.admin.client.AdminModule;
import org.broadleafcommerce.gwt.client.BLCMain;
import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationType;
import org.broadleafcommerce.gwt.client.datasource.relations.operations.OperationTypes;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.Property;
import org.broadleafcommerce.gwt.client.service.AbstractCallback;
import org.broadleafcommerce.gwt.client.service.AppServices;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.BooleanItem;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.validator.MatchesFieldValidator;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;

/**
 * 
 * @author jfischer
 *
 */
public class PasswordUpdateDialog extends Window {
	
	private DynamicForm dynamicForm;
	private Record associatedRecord;
	
	public PasswordUpdateDialog() {
		this.setIsModal(true);
		this.setShowModalMask(true);
		this.setShowMinimizeButton(false);
		this.setWidth(600);
		this.setAutoHeight();
		this.setHeight(300);
		this.setCanDragResize(true);
		this.setOverflow(Overflow.VISIBLE);
		
		VStack stack = new VStack();
		stack.setWidth100();
		stack.setLayoutRightMargin(20);
        
		dynamicForm = new DynamicForm(); 
		dynamicForm.setNumCols(2);
        dynamicForm.setPadding(10);
        stack.addMember(dynamicForm);
        addItem(stack);
        
        IButton saveButton = new IButton(AdminModule.ADMINMESSAGES.saveButtonTitle());  
        saveButton.addClickHandler(new ClickHandler() {  
            public void onClick(ClickEvent event) {  
            	if (dynamicForm.validate()) {
            		PersistencePerspective tempPerspective = new PersistencePerspective();
            		OperationTypes opTypes = new OperationTypes();
            		opTypes.setUpdateType(OperationType.ENTITY);
            		tempPerspective.setOperationTypes(opTypes);
            		
            		final Entity entity = new Entity();
            		Property prop1 = new Property();
            		prop1.setName("password");
            		prop1.setValue((String) dynamicForm.getField("password1").getValue());
            		Property prop2 = new Property();
            		prop2.setName("changeRequired");
            		prop2.setValue(String.valueOf(dynamicForm.getField("changeRequired").getValue()));
            		Property prop3 = new Property();
            		prop3.setName("username");
            		prop3.setValue(associatedRecord.getAttribute("username"));
            		entity.setProperties(new Property[]{prop1, prop2, prop3});
            		
            		BLCMain.NON_MODAL_PROGRESS.startProgress();
            		AppServices.DYNAMIC_ENTITY.update(entity, tempPerspective, new String[]{"passwordUpdate"}, new AbstractCallback<Entity>() {
						public void onSuccess(Entity arg0) {
							//associatedRecord.setAttribute("changeRequired", dynamicForm.getField("changeRequired").getValue());
							BLCMain.NON_MODAL_PROGRESS.stopProgress();
						}	
            		}); 
            		hide();
            	}
            }  
        });  

        IButton cancelButton = new IButton(AdminModule.ADMINMESSAGES.cancelButtonTitle());  
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
	}
	
	public void updatePassword(Record associatedRecord) {
		this.associatedRecord = associatedRecord;
		setTitle(AdminModule.ADMINMESSAGES.updateCustomerPasswordTitle());
		buildFields(dynamicForm);
        dynamicForm.setWrapItemTitles(false);
        centerInPage();
		show();
	}

	public void buildFields(DynamicForm dynamicForm) {
		MatchesFieldValidator validator = new MatchesFieldValidator();  
        validator.setOtherField("password2");  
        validator.setErrorMessage(AdminModule.ADMINMESSAGES.passwordNotMatchError());
        
		FormItem password1 = new PasswordItem("password1");
		password1.setTitle(AdminModule.ADMINMESSAGES.passwordPrompt());
		password1.setWidth("100%");
		password1.setRequired(true);
		password1.setValidators(validator); 
		
		FormItem password2 = new PasswordItem("password2");
		password2.setTitle(AdminModule.ADMINMESSAGES.passwordAgainPrompt());
		password2.setWidth("100%");
		password2.setRequired(true);
		
		FormItem changeRequired = new BooleanItem();
		changeRequired.setName("changeRequired");
		changeRequired.setTitle(AdminModule.ADMINMESSAGES.passwordChangeRequiredTitle());
		changeRequired.setWidth("100%");
		changeRequired.setValue(false);
	        	
		FormItem[] allFormItems = new FormItem[]{password1, password2, changeRequired};
		dynamicForm.setItems(allFormItems);
	}
}
