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

package org.broadleafcommerce.profile.web.core.service.legacy;

import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.State;
import org.broadleafcommerce.test.legacy.LegacyCommonSetupBaseTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class LegacyAddressTest extends LegacyCommonSetupBaseTest {

    List<Long> addressIds = new ArrayList<Long>();
    String userName = new String();
    Long userId;

    @Test(groups = "createCountryLegacy")
    public void createCountry() {
    	super.createCountry();
    }

    @Test(groups = "findCountriesLegacy", dependsOnGroups = "createCountryLegacy")
    public void findCountries() {
        List<Country> countries = countryService.findCountries();
        assert countries.size() > 0;
    }

    @Test(groups = "findCountryByShortNameLegacy", dependsOnGroups = "createCountryLegacy")
    public void findCountryByShortName() {
        Country country = countryService.findCountryByAbbreviation("US");
        assert country != null;
    }

    @Test(groups = "createStateLegacy")
    public void createState() {
        super.createState();
    }

    @Test(groups = "findStatesLegacy", dependsOnGroups = "createStateLegacy")
    public void findStates() {
        List<State> states = stateService.findStates();
        assert states.size() > 0;
    }

    @Test(groups = "findStateByAbbreviationLegacy", dependsOnGroups = "findStatesLegacy")
    public void findStateByAbbreviation() {
        State state = stateService.findStateByAbbreviation("KY");
        assert state != null;
    }

}
