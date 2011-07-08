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
package org.broadleafcommerce.openadmin.client.presenter.entity;

import java.util.HashMap;

import org.broadleafcommerce.openadmin.client.datasource.dynamic.AbstractDynamicDataSource;
import org.broadleafcommerce.openadmin.client.datasource.dynamic.DynamicEntityDataSource;
import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspective;
import org.broadleafcommerce.openadmin.client.dto.PersistencePerspectiveItemType;
import org.broadleafcommerce.openadmin.client.event.SearchItemSelectedEvent;
import org.broadleafcommerce.openadmin.client.event.SearchItemSelectedEventHandler;
import org.broadleafcommerce.openadmin.client.view.dynamic.dialog.EntitySearchDialog;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;

import com.google.gwt.user.client.Timer;
import com.smartgwt.client.widgets.form.fields.FormItem;

/**
 * 
 * @author jfischer
 *
 */
public class FormItemCallbackHandlerManager {
	
	private static final long serialVersionUID = 1L;
	
	protected HashMap<String, FormItemCallback> callbacks = new HashMap<String, FormItemCallback>();

	public void addSearchFormItemCallback(String fieldName, final EntitySearchDialog searchView, final String searchDialogTitle, final DynamicFormDisplay dynamicFormDisplay) {
		callbacks.put(fieldName, new FormItemCallback() {
			public void execute(final FormItem formItem) {
				searchView.search(searchDialogTitle, new SearchItemSelectedEventHandler() {
					public void onSearchItemSelected(SearchItemSelectedEvent event) {
						final String myId = ((AbstractDynamicDataSource) event.getDataSource()).getPrimaryKeyValue(event.getRecord());
						PersistencePerspective persistencePerspective = ((DynamicEntityDataSource) dynamicFormDisplay.getFormOnlyDisplay().getForm().getDataSource()).getPersistencePerspective();
						ForeignKey mainForeignKey = (ForeignKey) persistencePerspective.getPersistencePerspectiveItems().get(PersistencePerspectiveItemType.FOREIGNKEY);
						String displayFieldName = "name";
						if (mainForeignKey != null && mainForeignKey.getManyToField().equals(formItem.getName())) {
							displayFieldName = mainForeignKey.getDisplayValueProperty();
						} else {
							ForeignKey[] additionalKeys = persistencePerspective.getAdditionalForeignKeys();
							if (additionalKeys != null) {
								for (ForeignKey foreignKey : additionalKeys) {
									if (foreignKey.getManyToField().equals(formItem.getName())) {
										displayFieldName = foreignKey.getDisplayValueProperty();
										break;
									}
								}
							}
						}
						String myName = event.getRecord().getAttribute(displayFieldName);
						formItem.getForm().getField("__display_"+formItem.getName()).setValue(myName);
						Timer timer = new Timer() {  
				            public void run() {  
				            	formItem.setValue(myId);
				            	dynamicFormDisplay.getSaveButton().enable();
				            }  
				        };
				        timer.schedule(100);
					}
				});
			}	
		});
	}
	
	public void addSearchFormItemCallback(String fieldName, FormItemCallback formItemCallback) {
		callbacks.put(fieldName, formItemCallback);
	}
	
	public FormItemCallback getSearchFormItemCallback(String fieldName) {
		return callbacks.get(fieldName);
	}
	
}
