/*
 * #%L
 * BroadleafCommerce Profile
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
package org.broadleafcommerce.profile.core.domain;

import java.io.Serializable;

/**
 * This entity should be used only for lookup and filtering purposes only.
 * For example, to help populate a drop-down to those Countries you only wish to ship to.
 *
 * {@link org.broadleafcommerce.profile.core.domain.Address} no longer references this and Address
 * implementations should be updated to use {@link org.broadleafcommerce.common.i18n.domain.ISOCountry} instead.
 * This is to accommodate International Billing/Shipping Addresses which may not necessarily be restricted
 * to countries represented by this entity.
 *
 * {@link http://www.iso.org/iso/country_codes.htm}
 * {@link http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2}
 *
 * @author Elbert Bautista (elbertbautista)
 */
public interface Country extends Serializable {

    /**
     * The primary key - Ideally, implementations should use the ISO 3166-1 alpha-2 code of the country.
     * e.g. "US" or "GB"
     * {@link http://en.wikipedia.org/wiki/ISO_3166-1_alpha-2}
     */
    public String getAbbreviation();

    /**
     * sets the abbreviation for this Country
     * @param abbreviation - e.g. "US" or "GB"
     */
    public void setAbbreviation(String abbreviation);

    /**
     * The name for the Country
     * e.g. "United States", "United Kingdom"
     * @return - the name of the Country
     */
    public String getName();

    /**
     * sets the name of the Country
     * @param name - e.g. "United States", "United Kingdom"
     */
    public void setName(String name);

}
