/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.web.form.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;



public class EntityFormAction implements Cloneable {
    
    protected String buttonType = "button";
    protected String buttonClass = "";
    protected String urlPostfix = "";
    protected String iconClass = "";
    protected String displayText = "";
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EntityFormAction) {
            EntityFormAction that = (EntityFormAction) obj;
            return new EqualsBuilder()
                .append(buttonClass, that.buttonClass)
                .append(urlPostfix, that.urlPostfix)
                .append(iconClass, that.iconClass)
                .append(displayText, that.displayText)
                .build();
        }
        return false;
    }
    
    @Override
    public EntityFormAction clone() {
        EntityFormAction cloned = new EntityFormAction();
        cloned.buttonType = buttonType;
        cloned.buttonClass = buttonClass;
        cloned.urlPostfix = urlPostfix;
        cloned.iconClass = iconClass;
        cloned.displayText = displayText;
        return cloned;
    }
    
    public EntityFormAction withButtonType(String buttonType) {
        setButtonType(buttonType);
        return this;
    }
    
    public EntityFormAction withButtonClass(String buttonClass) {
        setButtonClass(buttonClass);
        return this;
    }
    
    public EntityFormAction withUrlPostfix(String urlPostfix) {
        setUrlPostfix(urlPostfix);
        return this;
    }
    
    public EntityFormAction withIconClass(String iconClass) {
        setIconClass(iconClass);
        return this;
    }
    
    public EntityFormAction withDisplayText(String displayText) {
        setDisplayText(displayText);
        return this;
    }
    
    public String getButtonType() {
        return buttonType;
    }
    
    public void setButtonType(String buttonType) {
        this.buttonType = buttonType;
    }

    public String getButtonClass() {
        return buttonClass;
    }
    
    public void setButtonClass(String buttonClass) {
        this.buttonClass = buttonClass;
    }

    public String getUrlPostfix() {
        return urlPostfix;
    }
    
    public void setUrlPostfix(String urlPostfix) {
        this.urlPostfix = urlPostfix;
    }
    
    public String getIconClass() {
        return iconClass;
    }
    
    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }
    
    public String getDisplayText() {
        return displayText;
    }
    
    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

}
