/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.common.util.WeightUnitOfMeasureType;
import org.broadleafcommerce.core.catalog.domain.Weight;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper around Weight
 * <p/>
 * User: Kelly Tisdell
 * Date: 4/10/12
 */
@XmlRootElement(name = "weight")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class WeightWrapper implements APIWrapper<Weight>, APIUnwrapper<Weight> {

    @XmlElement
    protected BigDecimal weight;

    @XmlElement
    protected String unitOfMeasure;

    @Override
    public void wrapDetails(Weight model, HttpServletRequest request) {
        this.weight = model.getWeight();
        if (model.getWeightUnitOfMeasure() != null) {
            this.unitOfMeasure = model.getWeightUnitOfMeasure().getType();
        }
    }

    @Override
    public void wrapSummary(Weight model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    
    /**
     * @return the weight
     */
    public BigDecimal getWeight() {
        return weight;
    }

    
    /**
     * @param weight the weight to set
     */
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    
    /**
     * @return the unitOfMeasure
     */
    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    
    /**
     * @param unitOfMeasure the unitOfMeasure to set
     */
    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    @Override
    public Weight unwrap(HttpServletRequest request, ApplicationContext context) {
        Weight wei = new Weight();
        wei.setWeight(this.weight);
        wei.setWeightUnitOfMeasure(WeightUnitOfMeasureType.getInstance(this.unitOfMeasure));
        return wei;
    }
}
