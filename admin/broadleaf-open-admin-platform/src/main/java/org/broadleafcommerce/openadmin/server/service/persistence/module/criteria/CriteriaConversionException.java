/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria;

/**
 * Thrown when a problem converting a particular {@link org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FieldPath}
 * from a {@link org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FilterMapping} is detected
 * during JPA criteria translation for fetch operation.
 *
 * @see org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.CriteriaTranslatorImpl
 * @author Jeff Fischer
 */
public class CriteriaConversionException extends RuntimeException {

    protected FieldPath fieldPath;

    public CriteriaConversionException(String message, FieldPath fieldPath) {
        super(message);
        this.fieldPath = fieldPath;
    }

    public FieldPath getFieldPath() {
        return fieldPath;
    }

    public void setFieldPath(FieldPath fieldPath) {
        this.fieldPath = fieldPath;
    }
}
