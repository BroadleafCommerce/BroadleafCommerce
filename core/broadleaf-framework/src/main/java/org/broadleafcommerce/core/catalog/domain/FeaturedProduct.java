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

public interface FeaturedProduct extends PromotableProduct {

    Long getId();

    void setId(Long id);

    Category getCategory();

    void setCategory(Category category);

    Product getProduct();

    void setProduct(Product product);

    void setSequence(BigDecimal sequence);
    
    BigDecimal getSequence();

    String getPromotionMessage();

    void setPromotionMessage(String promotionMessage);

    /**
     * Pass through to getProdcut() to meet the contract for promotable product.
     * @return
     */
    Product getRelatedProduct();

}
