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

package org.broadleafcommerce.core.order.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.ProductOption;
import org.broadleafcommerce.core.catalog.service.type.ProductOptionValidationType;
import org.broadleafcommerce.core.order.service.exception.ProductOptionValidationException;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service("blProductOptionValidationService")
public class ProductOptionValidationServiceImpl implements ProductOptionValidationService  {

    private static final Log LOG = LogFactory.getLog(ProductOptionValidationServiceImpl.class);


    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.order.service.ProductOptionValidationService#validate(org.broadleafcommerce.core.catalog.domain.ProductOption, java.lang.String)
     */
    @Override
    public Boolean validate(ProductOption productOption, String value) {
        if (productOption.getProductOptionValidationType() == ProductOptionValidationType.REGEX) {
            if (!validateRegex(productOption.getValidationSring(), value))
            {
                LOG.error(productOption.getErrorMessage() + ". Value [" + value + "] does not match regex string [" + productOption.getValidationSring() + "]");
                throw new ProductOptionValidationException(productOption.getErrorMessage(), productOption.getErrorCode());
            }
        }
        return true;
    }
    
    protected Boolean validateRegex(String regex, String value) {
        if (value == null) {
            return false;
        }
        return Pattern.matches(regex, value);
    }
    

}
