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

package org.broadleafcommerce.openadmin.client.presenter.entity;

import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import org.broadleafcommerce.openadmin.client.view.dynamic.form.DynamicFormDisplay;

/**
 * 
 * @author jfischer
 *
 */
public class DynamicFormPresenter {

    protected DynamicFormDisplay display;
    protected HandlerRegistration saveButtonHandlerRegistration;
    protected HandlerRegistration refreshButtonHandlerRegistration;
    protected HandlerRegistration itemChangedHandlerRegistration;
    
    public DynamicFormPresenter(DynamicFormDisplay display) {
        this.display = display;
    }
    
    public void setStartState() {
        display.getSaveButton().disable();
        display.getFormOnlyDisplay().getForm().enable();
        display.getRefreshButton().disable();
    }
    
    public void enable() {
        display.getSaveButton().enable();
        display.getFormOnlyDisplay().getForm().enable();
        display.getRefreshButton().enable();
    }
    
    public void disable() {
        display.getSaveButton().disable();
        display.getFormOnlyDisplay().getForm().disable();
        display.getRefreshButton().disable();
        display.getFormOnlyDisplay().getForm().clearValues();
        display.getFormOnlyDisplay().getForm().reset();
    }
    
    public void bind() {
        saveButtonHandlerRegistration=display.getSaveButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    DSRequest requestProperties = new DSRequest();
                    //requestProperties.setAttribute("dirtyValues", display.getFormOnlyDisplay().getForm().getChangedValues());
                    if (display.getFormOnlyDisplay().getForm().validate()) {
                        display.getFormOnlyDisplay().getForm().saveData(new DSCallback() {
                            public void execute(DSResponse response, Object rawData, DSRequest request) {
                                if (response.getStatus() != RPCResponse.STATUS_VALIDATION_ERROR) {
                                    display.getSaveButton().disable();
                                    display.getRefreshButton().disable();
                                }
                            }
                        }, requestProperties);
                    }
                }
            }
        });
        refreshButtonHandlerRegistration=display.getRefreshButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (event.isLeftButtonDown()) {
                    display.getFormOnlyDisplay().getForm().reset();
                    display.getSaveButton().disable();
                    display.getRefreshButton().disable();
                }
            }
        });
        itemChangedHandlerRegistration=display.getFormOnlyDisplay().getForm().addItemChangedHandler(new ItemChangedHandler() {
            public void onItemChanged(ItemChangedEvent event) {
                display.getSaveButton().enable();
                display.getRefreshButton().enable();
            }
        });
        
        exposeNativeEnableSaveButton();
    }
    
    public void enableSaveButton() {
        display.getSaveButton().enable();
        display.getRefreshButton().enable();
    }

    private native void exposeNativeEnableSaveButton() /*-{
        var currentDynamicFormPresenter = this;
        $wnd.enableSaveButton = function() {
            currentDynamicFormPresenter.@org.broadleafcommerce.openadmin.client.presenter.entity.DynamicFormPresenter::enableSaveButton()();
        }
    }-*/;

    public HandlerRegistration getSaveButtonHandlerRegistration() {
        return saveButtonHandlerRegistration;
    }

    public HandlerRegistration getRefreshButtonHandlerRegistration() {
        return refreshButtonHandlerRegistration;
    }

    public HandlerRegistration getItemChangedHandlerRegistration() {
        return itemChangedHandlerRegistration;
    }
}
