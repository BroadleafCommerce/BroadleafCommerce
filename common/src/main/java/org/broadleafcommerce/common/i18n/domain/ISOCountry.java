/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
import org.broadleafcommerce.common.i18n.service.type.ISOCodeStatusType;
import java.io.Serializable;

/**
 * This domain object represents the ISO 3166 standard published by the International Organization for Standardization (ISO),
 * and defines codes for the names of countries, dependent territories, and special areas of geographical interest.
 *
 * The Primary Key and ID for this entity will be the alpha-2 code for the respective Country.
 *
 * {@link http://en.wikipedia.org/wiki/ISO_3166-1}
 * {@link http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2}
 * {@link http://www.iso.org/iso/iso-3166-1_decoding_table}
 *
 * @author Elbert Bautista (elbertbautista)
 */
public interface ISOCountry extends Serializable {

    public String getAlpha2();

    public void setAlpha2(String alpha2);

    public String getName();

    public void setName(String name);

    public String getAlpha3();

    public void setAlpha3(String alpha3);

    public Integer getNumericCode();

    public void setNumericCode(Integer numericCode);

    public ISOCodeStatusType getStatus();

    public void setStatus(ISOCodeStatusType status);

}
