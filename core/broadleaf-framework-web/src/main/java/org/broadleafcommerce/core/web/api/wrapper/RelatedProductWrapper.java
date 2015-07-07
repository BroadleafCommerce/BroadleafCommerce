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

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.broadleafcommerce.core.catalog.domain.RelatedProduct;

/**
 * This is a JAXB wrapper for RelatedProducts
 *
 * User: Kelly Tisdell
 * Date: 4/10/12
 */
@XmlRootElement(name = "relatedProduct")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class RelatedProductWrapper extends BaseWrapper implements APIWrapper<RelatedProduct> {

    @XmlElement
    protected Long id;
    
    @XmlElement
    protected BigDecimal sequence;
    
    @XmlElement
    protected String promotionalMessage;

    @XmlElement
    protected ProductWrapper product;
    
    @Override
    public void wrapDetails(RelatedProduct model, HttpServletRequest request) {
        this.id = model.getId();
        this.sequence = model.getSequence();
        this.promotionalMessage = model.getPromotionMessage();
        product = (ProductWrapper) context.getBean(ProductWrapper.class.getName());
        product.wrapSummary(model.getRelatedProduct(), request);
    }

    @Override
    public void wrapSummary(RelatedProduct model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

    
    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    
    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    
    /**
     * @return the sequence
     */
    public BigDecimal getSequence() {
        return sequence;
    }

    
    /**
     * @param sequence the sequence to set
     */
    public void setSequence(BigDecimal sequence) {
        this.sequence = sequence;
    }

    
    /**
     * @return the promotionalMessage
     */
    public String getPromotionalMessage() {
        return promotionalMessage;
    }

    
    /**
     * @param promotionalMessage the promotionalMessage to set
     */
    public void setPromotionalMessage(String promotionalMessage) {
        this.promotionalMessage = promotionalMessage;
    }

    
    /**
     * @return the product
     */
    public ProductWrapper getProduct() {
        return product;
    }

    
    /**
     * @param product the product to set
     */
    public void setProduct(ProductWrapper product) {
        this.product = product;
    }
}
