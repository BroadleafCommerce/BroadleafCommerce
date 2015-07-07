/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.profile.web.core.service;

import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.State;
import org.broadleafcommerce.test.CommonSetupBaseTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class AddressTest extends CommonSetupBaseTest {

    List<Long> addressIds = new ArrayList<Long>();
    String userName = new String();
    Long userId;

    @Test(groups = "createCountry")
    public void createCountry() {
        super.createCountry();
    }

    @Test(groups = "findCountries", dependsOnGroups = "createCountry")
    public void findCountries() {
        List<Country> countries = countryService.findCountries();
        assert countries.size() > 0;
    }

    @Test(groups = "findCountryByShortName", dependsOnGroups = "createCountry")
    public void findCountryByShortName() {
        Country country = countryService.findCountryByAbbreviation("US");
        assert country != null;
    }

    @Test(groups = "createState")
    public void createState() {
        super.createState();
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

}
