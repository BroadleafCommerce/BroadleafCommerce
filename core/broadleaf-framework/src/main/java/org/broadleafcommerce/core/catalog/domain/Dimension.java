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

package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.util.DimensionUnitOfMeasureType;
import org.broadleafcommerce.common.vendor.service.type.ContainerShapeType;
import org.broadleafcommerce.common.vendor.service.type.ContainerSizeType;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;

@Embeddable
public class Dimension implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "WIDTH")
    @AdminPresentation(friendlyName = "ProductDimension_Product_Width", order = 1000, 
        tab = ProductImpl.Presentation.Tab.Name.Shipping, tabOrder = ProductImpl.Presentation.Tab.Order.Shipping,
        group = ProductImpl.Presentation.Group.Name.Shipping, groupOrder = ProductImpl.Presentation.Group.Order.Shipping)
    protected BigDecimal width;

    @Column(name = "HEIGHT")
    @AdminPresentation(friendlyName = "ProductDimension_Product_Height", order = 2000,
        tab = ProductImpl.Presentation.Tab.Name.Shipping, tabOrder = ProductImpl.Presentation.Tab.Order.Shipping,
        group = ProductImpl.Presentation.Group.Name.Shipping, groupOrder = ProductImpl.Presentation.Group.Order.Shipping)
    protected BigDecimal height;

    @Column(name = "DEPTH")
    @AdminPresentation(friendlyName = "ProductDimension_Product_Depth", order = 3000,
        tab = ProductImpl.Presentation.Tab.Name.Shipping, tabOrder = ProductImpl.Presentation.Tab.Order.Shipping,
        group = ProductImpl.Presentation.Group.Name.Shipping, groupOrder = ProductImpl.Presentation.Group.Order.Shipping)
    protected BigDecimal depth;

    @Column(name = "GIRTH")
    @AdminPresentation(friendlyName = "ProductDimension_Product_Girth", order = 4000,
        tab = ProductImpl.Presentation.Tab.Name.Shipping, tabOrder = ProductImpl.Presentation.Tab.Order.Shipping,
        group = ProductImpl.Presentation.Group.Name.Shipping, groupOrder = ProductImpl.Presentation.Group.Order.Shipping)
    protected BigDecimal girth;

    @Column(name = "CONTAINER_SIZE")
    @AdminPresentation(friendlyName = "ProductDimension_Product_Container_Size", order = 5000,
        tab = ProductImpl.Presentation.Tab.Name.Shipping, tabOrder = ProductImpl.Presentation.Tab.Order.Shipping,
        group = ProductImpl.Presentation.Group.Name.Shipping, groupOrder = ProductImpl.Presentation.Group.Order.Shipping,
        fieldType = SupportedFieldType.BROADLEAF_ENUMERATION, 
        broadleafEnumeration = "org.broadleafcommerce.common.vendor.service.type.ContainerSizeType")
    protected String size;

    @Column(name = "CONTAINER_SHAPE")
    @AdminPresentation(friendlyName = "ProductDimension_Product_Container_Shape", order = 6000,
        tab = ProductImpl.Presentation.Tab.Name.Shipping, tabOrder = ProductImpl.Presentation.Tab.Order.Shipping,
        group = ProductImpl.Presentation.Group.Name.Shipping, groupOrder = ProductImpl.Presentation.Group.Order.Shipping,
        fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
        broadleafEnumeration = "org.broadleafcommerce.common.vendor.service.type.ContainerShapeType")
    protected String container;

    @Column(name = "DIMENSION_UNIT_OF_MEASURE")
    @AdminPresentation(friendlyName = "ProductDimension_Product_Dimension_Units", order = 7000,
        tab = ProductImpl.Presentation.Tab.Name.Shipping, tabOrder = ProductImpl.Presentation.Tab.Order.Shipping,
        group = ProductImpl.Presentation.Group.Name.Shipping, groupOrder = ProductImpl.Presentation.Group.Order.Shipping,
        fieldType = SupportedFieldType.BROADLEAF_ENUMERATION, 
        broadleafEnumeration = "org.broadleafcommerce.common.util.DimensionUnitOfMeasureType")
    protected String dimensionUnitOfMeasure;

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(final BigDecimal width) {
        this.width = width;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(final BigDecimal height) {
        this.height = height;
    }

    public BigDecimal getDepth() {
        return depth;
    }

    public void setDepth(final BigDecimal depth) {
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

    public void setGirth(final BigDecimal girth) {
        this.girth = girth;
    }

    public ContainerSizeType getSize() {
        return ContainerSizeType.getInstance(size);
    }

    public void setSize(final ContainerSizeType size) {
        if (size != null) {
            this.size = size.getType();
        }
    }

    public ContainerShapeType getContainer() {
        return ContainerShapeType.getInstance(container);
    }

    public void setContainer(final ContainerShapeType container) {
        if (container != null) {
            this.container = container.getType();
        }
    }

    public DimensionUnitOfMeasureType getDimensionUnitOfMeasure() {
        return DimensionUnitOfMeasureType.getInstance(dimensionUnitOfMeasure);
    }

    public void setDimensionUnitOfMeasure(final DimensionUnitOfMeasureType dimensionUnitOfMeasure) {
        if (dimensionUnitOfMeasure != null) {
            this.dimensionUnitOfMeasure = dimensionUnitOfMeasure.getType();
        }
    }
}
