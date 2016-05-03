/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
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
