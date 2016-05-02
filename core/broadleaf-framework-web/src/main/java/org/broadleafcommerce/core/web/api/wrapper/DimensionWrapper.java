/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License” located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License” located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.common.util.DimensionUnitOfMeasureType;
import org.broadleafcommerce.common.vendor.service.type.ContainerShapeType;
import org.broadleafcommerce.common.vendor.service.type.ContainerSizeType;
import org.broadleafcommerce.core.catalog.domain.Dimension;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlElement;

/**
 * JAXB wrapper for Dimension
 * <p/>
 * User: Kelly Tisdell
 * Date: 4/10/12
 */
public class DimensionWrapper extends BaseWrapper implements APIWrapper<Dimension>, APIUnwrapper<Dimension> {

    @XmlElement
    protected BigDecimal width;

    @XmlElement
    protected BigDecimal height;

    @XmlElement
    protected BigDecimal depth;

    @XmlElement
    protected BigDecimal girth;

    @XmlElement
    protected String container;

    @XmlElement
    protected String size;
    
    @XmlElement
    protected String dimensionUnitOfMeasure;
    
    @Override
    public void wrapDetails(Dimension model, HttpServletRequest request) {
        this.width = model.getWidth();
        this.depth = model.getDepth();
        this.height = model.getHeight();
        this.girth = model.getGirth();

        if (model.getDimensionUnitOfMeasure() != null) {
            this.dimensionUnitOfMeasure = model.getDimensionUnitOfMeasure().getType();
        }

        if (model.getSize() != null) {
            this.size = model.getSize().getType();
        }

        if (model.getContainer() != null) {
            this.container = model.getContainer().getType();
        }
    }

    @Override
    public void wrapSummary(Dimension model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    
    /**
     * @return the width
     */
    public BigDecimal getWidth() {
        return width;
    }

    
    /**
     * @param width the width to set
     */
    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    
    /**
     * @return the height
     */
    public BigDecimal getHeight() {
        return height;
    }

    
    /**
     * @param height the height to set
     */
    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    
    /**
     * @return the depth
     */
    public BigDecimal getDepth() {
        return depth;
    }

    
    /**
     * @param depth the depth to set
     */
    public void setDepth(BigDecimal depth) {
        this.depth = depth;
    }

    
    /**
     * @return the girth
     */
    public BigDecimal getGirth() {
        return girth;
    }

    
    /**
     * @param girth the girth to set
     */
    public void setGirth(BigDecimal girth) {
        this.girth = girth;
    }

    
    /**
     * @return the container
     */
    public String getContainer() {
        return container;
    }

    
    /**
     * @param container the container to set
     */
    public void setContainer(String container) {
        this.container = container;
    }

    
    /**
     * @return the size
     */
    public String getSize() {
        return size;
    }

    
    /**
     * @param size the size to set
     */
    public void setSize(String size) {
        this.size = size;
    }

    
    /**
     * @return the dimensionUnitOfMeasure
     */
    public String getDimensionUnitOfMeasure() {
        return dimensionUnitOfMeasure;
    }

    
    /**
     * @param dimensionUnitOfMeasure the dimensionUnitOfMeasure to set
     */
    public void setDimensionUnitOfMeasure(String dimensionUnitOfMeasure) {
        this.dimensionUnitOfMeasure = dimensionUnitOfMeasure;
    }

    public Dimension unwrap(HttpServletRequest request, ApplicationContext context) {
        Dimension dim = new Dimension();
        dim.setContainer(ContainerShapeType.getInstance(this.container));
        dim.setDimensionUnitOfMeasure(DimensionUnitOfMeasureType.getInstance(this.dimensionUnitOfMeasure));
        dim.setDepth(this.depth);
        dim.setGirth(this.girth);
        dim.setHeight(this.height);
        dim.setSize(ContainerSizeType.getInstance(this.size));
        dim.setWidth(this.width);
        return dim;
    }
}
