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
package org.broadleafcommerce.catalog.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.broadleafcommerce.util.DimensionUnitOfMeasureType;
import org.broadleafcommerce.vendor.service.type.ContainerShapeType;
import org.broadleafcommerce.vendor.service.type.ContainerSizeType;

@Embeddable
public class ProductDimension implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "WIDTH")
    protected BigDecimal width;

    @Column(name = "HEIGHT")
    protected BigDecimal height;

    @Column(name = "DEPTH")
    protected BigDecimal depth;

    @Column(name = "GIRTH")
    protected BigDecimal girth;

    @Column(name = "CONTAINER_SIZE")
    protected String size;

    @Column(name = "CONTAINER_SHAPE")
    protected String container;

    @Column(name = "DIMENSION_UNIT_OF_MEASURE")
    protected String dimensionUnitOfMeasure;

    public DimensionUnitOfMeasureType getDimensionUnitOfMeasure() {
        return dimensionUnitOfMeasure == null ? null : DimensionUnitOfMeasureType.getInstance(dimensionUnitOfMeasure);
    }

    public void setDimensionUnitOfMeasure(DimensionUnitOfMeasureType dimensionUnitOfMeasure) {
        this.dimensionUnitOfMeasure = dimensionUnitOfMeasure.getType();
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public BigDecimal getDepth() {
        return depth;
    }

    public void setDepth(BigDecimal depth) {
        this.depth = depth;
    }

    /**
     * Returns the product dimensions as a String (assumes measurements are in
     * inches)
     * @return a String value of the product dimensions
     */
    public String getDimensionString() {
        return height + "Hx" + width + "Wx" + depth + "D\"";
    }

    public BigDecimal getGirth() {
        return girth;
    }

    public void setGirth(BigDecimal girth) {
        this.girth = girth;
    }

    public ContainerSizeType getSize() {
        return size == null ? null : ContainerSizeType.getInstance(size);
    }

    public void setSize(ContainerSizeType size) {
        this.size = size.getType();
    }

    public ContainerShapeType getContainer() {
        return container == null ? null : ContainerShapeType.getInstance(container);
    }

    public void setContainer(ContainerShapeType container) {
        this.container = container.getType();
    }

}
