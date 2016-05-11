/*
 * #%L
 * BroadleafCommerce Common Libraries
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

package org.broadleafcommerce.common.i18n.domain;

import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.io.Serializable;

/**
 * This domain object represents a translated value for a given property on an entity for a specific locale.
 * 
 * @author Andre Azzolini (apazzolini)
 * @see TranslatedEntity
 */
public interface Translation extends MultiTenantCloneable<Translation>, Serializable {

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
