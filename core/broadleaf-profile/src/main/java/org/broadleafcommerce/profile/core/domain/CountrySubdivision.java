/*-
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
 * Represents a principal subdivision of a Country (e.g. province or state).
 * <p>
 * This entity should be used only for lookup and filtering purposes only.
 * For example, to help populate a drop-down to those States/Provinces/Regions you only wish to ship to.
 * <p>
 * {@link http://www.iso.org/iso/country_codes.htm}
 * {@link http://en.wikipedia.org/wiki/ISO_3166-2}
 *
 * @author Elbert Bautista (elbertbautista)
 */
public interface CountrySubdivision extends Serializable {

    /**
     * The primary key - Ideally, implementations should use the ISO 3166-2 code of the country subdivision.
     * e.g. "US-TX" or "GB-WSM"
     * {@link http://en.wikipedia.org/wiki/ISO_3166-2}
     */
    String getAbbreviation();

    /**
     * Sets the primary key abbreviation
     *
     * @param abbreviation - e.g. "US-TX" or "GB-WSM"
     */
    void setAbbreviation(String abbreviation);

    /**
     * More friendly abbreviation that can be used for display purposes
     * e.g. "TX", "CA", "NY"
     *
     * @return - the alternate abbreviation
     */
    String getAlternateAbbreviation();

    /**
     * sets the alternate abbreviation
     *
     * @param alternateAbbreviation - e.g. "TX", "CA", "NY"
     */
    void setAlternateAbbreviation(String alternateAbbreviation);

    /**
     * Full name for display purposes
     * e.g. "Texas", "California", "New York"
     *
     * @return - the alternate abbreviation
     */
    String getName();

    /**
     * sets the name
     *
     * @param name - e.g. "Texas", "California", "New York"
     */
    void setName(String name);

    /**
     * A category that represents the subdivision of this Country.
     * e.g. "State", "Province", "District"
     *
     * @return - the country subdivision category
     */
    CountrySubdivisionCategory getCategory();

    /**
     * sets the country subdivision category
     *
     * @param category - e.g. "State", "Province", "District"
     */
    void setCategory(CountrySubdivisionCategory category);

    /**
     * The country where this subdivision resides
     * e.g. "US", "GB"
     *
     * @return - the country where this subdivision resides
     */
    Country getCountry();

    /**
     * sets the country in which this subdivision resides
     *
     * @param country - e.g. "US", "GB"
     */
    void setCountry(Country country);

}
