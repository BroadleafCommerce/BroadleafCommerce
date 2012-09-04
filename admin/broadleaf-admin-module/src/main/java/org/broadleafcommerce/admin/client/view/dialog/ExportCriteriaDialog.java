/*
 * Copyright 2012 the original author or authors.
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

package org.broadleafcommerce.admin.client.view.dialog;

import org.broadleafcommerce.admin.client.dto.AdminExporterDTO;
import org.broadleafcommerce.openadmin.client.BLCMain;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.FormBuilder;

import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.FormMethod;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.layout.HLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialog to show the extra criteria (if available) for an export. If no criteria is
 * available for the exporter, the form is immediately submitted to the server
 * 
 * @author Phillip Verheyden
 */
public class ExportCriteriaDialog extends Window {

    protected IButton submitButton;
    protected DynamicForm form;
    protected List<FormItem> formItems;
    protected FormItem exporterNameField;
    protected boolean shouldShowForm;

    public ExportCriteriaDialog(AdminExporterDTO exporter) {
        super();
        
        //only show the form if there is extra criteria
        setShouldShowForm(exporter.getAdditionalCriteriaProperties() != null && exporter.getAdditionalCriteriaProperties().size() > 0);
        
        form = new DynamicForm();
        form.setCellPadding(8);
        form.setWidth100();
        form.setCanSubmit(true);
        form.setMethod(FormMethod.POST);
        form.setAction(BLCMain.webAppContext + "/export");
        
        //add the hidden input for the exporter
        exporterNameField = new HiddenItem("exporter");
        exporterNameField.setValue(exporter.getName());

        HLayout formLayout = new HLayout();
        formLayout.addMember(form);
        addItem(formLayout);

        if (getShouldShowForm()) {
            this.setTitle("Custom Criteria");
            this.setIsModal(true);
            this.setShowModalMask(true);
            this.setShowMinimizeButton(false);
            this.setWidth(550);
            this.setHeight(400);
            this.setCanDragResize(true);
            this.setOverflow(Overflow.AUTO);
            this.setVisible(false);

            formLayout.setLayoutMargin(20);
            formLayout.setAlign(Alignment.CENTER);
            formLayout.setWidth100();
            
            
            ArrayList<FormItem> formItems = new ArrayList<FormItem>();
            for (Property property : exporter.getAdditionalCriteriaProperties()) {
                Boolean presentationLargeEntry = property.getMetadata().getPresentationAttributes().isLargeEntry();
                boolean largeEntry = (presentationLargeEntry) == null ? false : presentationLargeEntry;
                //created in order to not throw NPEs when creating the initial form item
                DataSourceField lengthField = new DataSourceField();
                lengthField.setLength(property.getMetadata().getLength() == null ? 255 : property.getMetadata().getLength());
                FormItem formItem = FormBuilder.buildField(null, lengthField, property.getMetadata().getFieldType().toString(), largeEntry, form);
                
                formItem.setName(property.getName());
                formItem.setTitle(property.getMetadata().getPresentationAttributes().getFriendlyName());
                formItem.setWrapTitle(false);
                Boolean required = property.getMetadata().getPresentationAttributes().getRequiredOverride() == null ? false : property.getMetadata().getPresentationAttributes().getRequiredOverride();
                formItem.setRequired(required);
                String prompt = property.getMetadata().getPresentationAttributes().getTooltip() == null ? "" : property.getMetadata().getPresentationAttributes().getTooltip();
                formItem.setPrompt(prompt);
                
                formItems.add(formItem);
            }
            
            formItems.add(exporterNameField);
            
            setFormItems(formItems);
            form.setFields(formItems.toArray(new FormItem[]{}));

            submitButton = new IButton("Submit");
            submitButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    if (form.validate()) {
                        hide();
                        form.submit();
                    }
                }
            });

            IButton cancelButton = new IButton("Cancel");  
            cancelButton.addClickHandler(new ClickHandler() {  
                @Override
                public void onClick(ClickEvent event) {  
                    hide();
                }  
            });
            
            HLayout hLayout = new HLayout(10);
            hLayout.setAlign(Alignment.CENTER);
            hLayout.addMember(submitButton);
            hLayout.addMember(cancelButton);
            hLayout.setLayoutTopMargin(10);
            hLayout.setLayoutBottomMargin(10);
            
            addItem(hLayout);

        } else {
            this.setWidth(1);
            this.setHeight(1);
            form.setFields(new FormItem[]{exporterNameField});
        }

    }
    
    public void launch() {
        String exporterName = exporterNameField.getValue().toString();
        form.clearValues();
        exporterNameField.setValue(exporterName);
        centerInPage();
        show();
        if (!getShouldShowForm()) {
            form.submit();
            hide();
        }
    }

    public IButton getSubmitButton() {
        return submitButton;
    }

    public void setSubmitButton(IButton submitButton) {
        this.submitButton = submitButton;
    }
    
    public List<FormItem> getFormItems() {
        return formItems;
    }
    
    public void setFormItems(List<FormItem> formItems) {
        this.formItems = formItems;
    }
    
    public boolean getShouldShowForm() {
        return shouldShowForm;
    }
    
    public void setShouldShowForm(boolean shouldShowForm) {
        this.shouldShowForm = shouldShowForm;
    }

}