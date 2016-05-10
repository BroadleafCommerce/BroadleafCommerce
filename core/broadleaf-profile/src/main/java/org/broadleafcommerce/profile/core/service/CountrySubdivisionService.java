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
package org.broadleafcommerce.profile.core.service;

import org.broadleafcommerce.profile.core.domain.CountrySubdivision;
import java.util.List;

/**
 * @author Elbert Bautista (elbertbautista)
 */
public interface CountrySubdivisionService {

    public List<CountrySubdivision> findSubdivisions();

    public List<CountrySubdivision> findSubdivisions(String countryAbbreviation);

    public List<CountrySubdivision> findSubdivisionsByCountryAndCategory(String countryAbbreviation, String category);

    public CountrySubdivision findSubdivisionByAbbreviation(String abbreviation);

    public CountrySubdivision findSubdivisionByCountryAndAltAbbreviation(String countryAbbreviation, String altAbbreviation);

    public CountrySubdivision findSubdivisionByCountryAndName(String countryAbbreviation, String name);

    public CountrySubdivision save(CountrySubdivision subdivision);

}
