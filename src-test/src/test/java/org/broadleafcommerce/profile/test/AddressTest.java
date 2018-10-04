/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.profile.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.profile.domain.Country;
import org.broadleafcommerce.profile.domain.CountryImpl;
import org.broadleafcommerce.profile.domain.State;
import org.broadleafcommerce.profile.domain.StateImpl;
import org.broadleafcommerce.profile.service.CountryService;
import org.broadleafcommerce.profile.service.StateService;
import org.broadleafcommerce.test.integration.BaseTest;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class AddressTest extends BaseTest {

    List<Long> addressIds = new ArrayList<Long>();
    String userName = new String();
    Long userId;

    @Resource
    private StateService stateService;

    @Resource
    private CountryService countryService;

    @Test(groups = "createCountry")
    @Rollback(false)
    public void createCountry() {
        Country country = new CountryImpl();
        country.setAbbreviation("US");
        country.setName("United States");
        em.persist(country);
    }

    @Test(groups = "findCountries", dependsOnGroups = "createCountry")
    public void findCountries() {
        List<Country> countries = countryService.findCountries();
        assert countries.size() > 0;
    }

    @Test(groups = "findCountryByShortName", dependsOnGroups = "findCountries")
    public void findCountryByShortName() {
        Country country = countryService.findCountryByAbbreviation("US");
        assert country != null;
    }

    @Test(groups = "createState", dependsOnGroups = "createCountry")
    @Rollback(false)
    public void createState() {
        State state = new StateImpl();
        state.setAbbreviation("KY");
        state.setName("Kentucky");
        state.setCountry(countryService.findCountryByAbbreviation("US"));
        em.persist(state);
    }

    @Test(groups = "findStates", dependsOnGroups = "createState")
    public void findStates() {
        List<State> states = stateService.findStates();
        assert states.size() > 0;
    }

    @Test(groups = "findStateByAbbreviation", dependsOnGroups = "findStates")
    public void findStateByAbbreviation() {
        State state = stateService.findStateByAbbreviation("KY");
        assert state != null;
    }

    /*
     * @Test(groups = "createAddress", dataProvider = "setupAddress",
     * dataProviderClass = AddressDataProvider.class)
     * @Rollback(false) public void createAddress(Address address) { assert
     * address.getId() == null; address = addressService.saveAddress(address);
     * assert address.getId() != null; }
     */
}
