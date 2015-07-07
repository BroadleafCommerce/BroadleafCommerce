/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.catalog.domain;


import java.math.BigDecimal;

public interface RelatedProduct extends PromotableProduct {
    
    public Long getId();

    public Product getProduct();
    
    public Category getCategory();

    public Product getRelatedProduct();

    public String getPromotionMessage();

    public BigDecimal getSequence();

    public void setId(Long id);

    public void setProduct(Product product);
    
    public void setCategory(Category category);

    public void setRelatedProduct(Product relatedProduct);

    public void setPromotionMessage(String promotionMessage);

    public void setSequence(BigDecimal sequence);
}
