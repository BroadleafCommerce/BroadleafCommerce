package org.broadleafcommerce.profile.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.Country;
import org.broadleafcommerce.profile.domain.CountryImpl;
import org.broadleafcommerce.profile.domain.StateProvince;
import org.broadleafcommerce.profile.domain.StateProvinceImpl;
import org.broadleafcommerce.profile.service.AddressService;
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
    private AddressService addressService;

    @Test(groups = "createStateProvince")
    @Rollback(false)
    public void createStateProvince() {
        StateProvince state = new StateProvinceImpl();
        state.setShortName("KY");
        state.setLongName("Kentucky");
        em.persist(state);
    }

    @Test(groups = "findStateProvinces", dependsOnGroups = "createStateProvince")
    public void findStateProvinces() {
        List<StateProvince> states = addressService.findStateProvinces();
        assert states.size() > 0;
    }

    @Test(groups = "findStateProvinceByShortName", dependsOnGroups = "findStateProvinces")
    public void findStateProvinceByShortName() {
        StateProvince state = addressService.findStateProvinceByShortName("KY");
        assert state != null;
    }

    @Test(groups = "createCountry")
    @Rollback(false)
    public void createCountry() {
        Country country = new CountryImpl();
        country.setShortName("US");
        country.setLongName("United States");
        em.persist(country);
    }

    @Test(groups = "findCountries", dependsOnGroups = "createCountry")
    public void findCountries() {
        List<Country> countries = addressService.findCountries();
        assert countries.size() > 0;
    }

    @Test(groups = "findCountryByShortName", dependsOnGroups = "findCountries")
    public void findCountryByShortName() {
        Country country = addressService.findCountryByShortName("US");
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
