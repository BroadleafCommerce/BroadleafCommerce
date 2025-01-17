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
package org.broadleafcommerce.profile.core.dao;

import org.broadleafcommerce.profile.core.domain.CountrySubdivision;

import java.util.List;

import jakarta.annotation.Nonnull;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public interface CountrySubdivisionDao {

    List<CountrySubdivision> findSubdivisions();

    List<CountrySubdivision> findSubdivisions(String countryAbbreviation);

    List<CountrySubdivision> findSubdivisionsByCountryAndCategory(String countryAbbreviation, String category);

    CountrySubdivision findSubdivisionByAbbreviation(String abbreviation);

    CountrySubdivision findSubdivisionByCountryAndAltAbbreviation(@Nonnull String countryAbbreviation, @Nonnull String altAbbreviation);

    CountrySubdivision findSubdivisionByCountryAndName(@Nonnull String countryAbbreviation, @Nonnull String name);

    CountrySubdivision create();

    CountrySubdivision save(CountrySubdivision subdivision);

}
