package org.broadleafcommerce.profile.dao;

import java.util.List;

import org.broadleafcommerce.profile.domain.Country;

public interface CountryDao {

    public List<Country> findCountries();

    public Country findCountryByAbbreviation(String abbreviation);
}
