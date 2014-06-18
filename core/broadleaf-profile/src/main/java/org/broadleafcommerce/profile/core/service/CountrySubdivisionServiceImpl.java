/*
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.profile.core.service;

import org.broadleafcommerce.profile.core.dao.CountrySubdivisionDao;
import org.broadleafcommerce.profile.core.domain.CountrySubdivision;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import javax.annotation.Resource;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blCountrySubdivisionService")
public class CountrySubdivisionServiceImpl implements CountrySubdivisionService {

    @Resource(name="blCountrySubdivisionDao")
    protected CountrySubdivisionDao countrySubdivisionDao;

    @Override
    public List<CountrySubdivision> findSubdivisions() {
        return countrySubdivisionDao.findSubdivisions();
    }

    @Override
    public List<CountrySubdivision> findSubdivisions(String countryAbbreviation) {
        return countrySubdivisionDao.findSubdivisions(countryAbbreviation);
    }

    @Override
    public CountrySubdivision findSubdivisionByAbbreviation(String abbreviation) {
        return countrySubdivisionDao.findSubdivisionByAbbreviation(abbreviation);
    }

    @Override
    @Transactional("blTransactionManager")
    public CountrySubdivision save(CountrySubdivision subdivision) {
        return countrySubdivisionDao.save(subdivision);
    }
}
