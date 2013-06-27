/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.profile.core.service;

import org.broadleafcommerce.profile.core.dao.CountryDao;
import org.broadleafcommerce.profile.core.domain.Country;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service("blCountryService")
public class CountryServiceImpl implements CountryService {

    @Resource(name="blCountryDao")
    protected CountryDao countryDao;

    public List<Country> findCountries() {
        return countryDao.findCountries();
    }

    public Country findCountryByAbbreviation(String abbreviation) {
        return countryDao.findCountryByAbbreviation(abbreviation);
    }

    @Transactional("blTransactionManager")
    public Country save(Country country) {
        return countryDao.save(country);
    }
}