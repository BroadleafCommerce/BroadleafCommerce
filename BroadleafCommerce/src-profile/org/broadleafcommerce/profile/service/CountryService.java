package org.broadleafcommerce.profile.service;

import java.util.List;

import org.broadleafcommerce.profile.domain.Country;

public interface CountryService {

    public List<Country> findCountries();

    public Country findCountryByAbbreviation(String abbreviation);
}