/*
 * #%L
 * BroadleafCommerce Common Libraries
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

package org.broadleafcommerce.common.i18n.domain;

/**
 * This domain object represents a translated value for a given property on an entity for a specific locale.
 * 
 * @author Andre Azzolini (apazzolini)
 * @see TranslatedEntity
 */
public interface Translation {
    
    public Long getId();

    public void setId(Long id);

    public TranslatedEntity getEntityType();

    public void setEntityType(TranslatedEntity entityType);

    public String getEntityId();

    public void setEntityId(String entityId);

    public String getFieldName();

    public void setFieldName(String fieldName);

    public String getLocaleCode();

    public void setLocaleCode(String localeCode);

    public String getTranslatedValue();

    public void setTranslatedValue(String translatedValue);

}
