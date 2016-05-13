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

import org.broadleafcommerce.core.catalog.domain.RelatedProduct;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper for RelatedProducts
 *
 * @deprecated - use {@link com.broadleafcommerce.core.rest.api.v2.wrapper.RelatedProductWrapper}
 *
 * User: Kelly Tisdell
 * Date: 4/10/12
 */
@Deprecated
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
