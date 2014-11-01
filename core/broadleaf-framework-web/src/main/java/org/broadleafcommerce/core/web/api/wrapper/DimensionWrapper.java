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

import org.broadleafcommerce.core.catalog.domain.Dimension;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlElement;

/**
 * JAXB wrapper for Dimension
 * <p/>
 * User: Kelly Tisdell
 * Date: 4/10/12
 */
public class DimensionWrapper extends BaseWrapper implements APIWrapper<Dimension>{

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
}
