/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.openadmin.web.rulebuilder.dto;

import java.io.Serializable;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class FieldDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String id;

    protected String label;

    protected String type;

    protected String input;

    protected String operators;

    protected String selectizeSectionKey;

    protected String values;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOperators() {
        return operators;
    }

    public void setOperators(String operators) {
        this.operators = operators;
    }

    public String getSelectizeSectionKey() {
        return selectizeSectionKey;
    }

    public void setSelectizeSectionKey(String selectizeSectionKey) {
        this.selectizeSectionKey = selectizeSectionKey;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }
}
