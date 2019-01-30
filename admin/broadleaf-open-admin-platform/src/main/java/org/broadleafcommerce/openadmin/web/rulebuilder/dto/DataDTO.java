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

import org.apache.commons.lang3.builder.EqualsBuilder;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public class DataDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Long pk;
    protected Long containedPk;
    protected Long previousPk;
    protected Long previousContainedPk;
    protected Integer quantity;
    protected String condition;
    protected ArrayList<DataDTO> rules = new ArrayList<DataDTO>();

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public Long getPreviousPk() {
        return previousPk;
    }

    public void setPreviousPk(Long previousPk) {
        this.previousPk = previousPk;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public ArrayList<DataDTO> getRules() {
        return rules;
    }

    public void setRules(ArrayList<DataDTO> rules) {
        this.rules = rules;
    }

    public Long getContainedPk() {
        return containedPk;
    }

    public void setContainedPk(Long containedPk) {
        this.containedPk = containedPk;
    }

    public Long getPreviousContainedPk() {
        return previousContainedPk;
    }

    public void setPreviousContainedPk(Long previousContainedPk) {
        this.previousContainedPk = previousContainedPk;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && getClass().isAssignableFrom(obj.getClass())) {
            DataDTO that = (DataDTO) obj;
            return new EqualsBuilder()
                .append(pk, that.pk)
                .append(quantity, that.quantity)
                .append(condition, that.condition)
                .append(rules.toArray(), that.rules.toArray())
                .build();
        }
        return false;
    }
}
