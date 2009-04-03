package org.broadleafcommerce.profile.service;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.profile.dao.CountryDao;
import org.broadleafcommerce.profile.domain.Country;
import org.springframework.stereotype.Service;

@Service("countryService")
public class CountryServiceImpl implements CountryService {

    @Resource
    private CountryDao countryDao;

    public List<Country> findCountries() {
        return countryDao.findCountries();
    }

    public Country findCountryByAbbreviation(String abbreviation) {
        return countryDao.findCountryByAbbreviation(abbreviation);
    }
}