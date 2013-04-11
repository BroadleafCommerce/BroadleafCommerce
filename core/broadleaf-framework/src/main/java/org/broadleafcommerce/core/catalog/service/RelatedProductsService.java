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

package org.broadleafcommerce.core.catalog.service;

import org.broadleafcommerce.core.catalog.domain.PromotableProduct;
import org.broadleafcommerce.core.catalog.domain.RelatedProductDTO;

import java.util.List;

/**
 * Interface for finding related products.   Could be extended to support more complex
 * related product functions.    
 * 
 * @author bpolster
 *
 */
public interface RelatedProductsService {   
    
    /**
     * Uses the data in the passed in DTO to return a list of relatedProducts.
     * 
     * For example, upSale, crossSale, or featured products.
     * 
     * @param relatedProductDTO
     * @return
     */
    public List<? extends PromotableProduct> findRelatedProducts(RelatedProductDTO relatedProductDTO);
}
