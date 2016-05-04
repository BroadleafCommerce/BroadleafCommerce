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
package org.broadleafcommerce.core.catalog.dao;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.domain.ProductOptionValue;
import org.broadleafcommerce.core.catalog.domain.dto.AssignedProductOptionDTO;

import java.util.List;

/**
 * 
 * @author Phillip Verheyden
 *
 */
public interface ProductOptionDao {
    
    public List<ProductOption> readAllProductOptions();
    
    public ProductOption readProductOptionById(Long id);
    
    public ProductOption saveProductOption(ProductOption option);
    
    public ProductOptionValue readProductOptionValueById(Long id);

    /**
     * Returns a list of {@link org.broadleafcommerce.core.catalog.domain.dto.AssignedProductOptionDTO}
     * found for given the productId.
     *
     * @param productId
     * @return
     */
    public List<AssignedProductOptionDTO> findAssignedProductOptionsByProductId(Long productId);

    /**
     * Returns a list of {@link org.broadleafcommerce.core.catalog.domain.dto.AssignedProductOptionDTO}
     * found for given the {@link org.broadleafcommerce.core.catalog.domain.Product}.
     *
     * @param product
     * @return
     */
    public List<AssignedProductOptionDTO> findAssignedProductOptionsByProduct(Product product);


}
