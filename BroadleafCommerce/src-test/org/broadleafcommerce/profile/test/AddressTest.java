package org.broadleafcommerce.profile.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    List<Long> addressIds = new ArrayList<Long>();
    String userName = new String();
    Long userId;

    @Resource
    private StateService stateService;

    @Resource
    private CountryService countryService;

    @Test(groups = "createState")
    @Rollback(false)
    public void createState() {
        State state = new StateImpl();
        state.setAbbreviation("KY");
        state.setName("Kentucky");
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
    /*
    @Test(groups = "createAddress", dataProvider = "setupAddress", dataProviderClass = AddressDataProvider.class)
    @Rollback(false)
    public void createAddress(Address address) {
        assert address.getId() == null;
        address = addressService.saveAddress(address);
        assert address.getId() != null;
    }
     */
}
