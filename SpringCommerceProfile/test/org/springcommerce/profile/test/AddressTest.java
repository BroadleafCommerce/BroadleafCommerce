package org.springcommerce.profile.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springcommerce.profile.test.dataprovider.AddressDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import com.mpower.domain.Address;
import com.mpower.domain.Person;
import com.mpower.domain.Site;
import com.mpower.service.AddressService;
import com.mpower.service.AuditService;
import com.mpower.util.CalendarUtils;

public class AddressTest extends BaseTest {

    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private AddressService addressService;

    @Autowired
    private AuditService auditService;

    private List<String> siteIds = new ArrayList<String>();

    private Long personId;

    private List<Long> addressIds = new ArrayList<Long>();

    @Test(groups = { "createAddresses" }, dataProvider = "setupAddresses", dataProviderClass = AddressDataProvider.class)
    public void createAddress(Site site, Person person, List<Address> addresses) {
        addressService.setAuditService(auditService);
        em.getTransaction().begin();
        em.persist(site);
        siteIds.add(site.getName());
        person.setSite(site);
        em.persist(person);
        personId = person.getId();
        int begin = addressService.readAddresses(person.getId()).size();
        for (Address address : addresses) {
            address.setPerson(person);
            address = addressService.saveAddress(address);
            addressIds.add(address.getId());
        }
        int end = addressService.readAddresses(person.getId()).size();
        logger.debug("change = " + (end - begin));
        assert (end - begin) == addresses.size();
        em.getTransaction().commit();
    }

    @Test(groups = { "queryAddresses" }, dependsOnGroups = { "createAddresses" })
    public void queryAddresses() {
        List<String> address1StringList = new ArrayList<String>();
        address1StringList.add("1-permanent-addressLine1");
        address1StringList.add("2-permanent-addressLine1");
        address1StringList.add("3-permanent-addressLine1");
        address1StringList.add("1-seasonal-addressLine1");
        address1StringList.add("2-seasonal-addressLine1");
        address1StringList.add("3-seasonal-addressLine1");
        address1StringList.add("1-temporary-addressLine1");
        address1StringList.add("2-temporary-addressLine1");
        address1StringList.add("3-temporary-addressLine1");
        List<Address> addresses = addressService.readAddresses(personId);
        assert addresses.size() == 9;
        for (Address a : addresses) {
            assert address1StringList.contains(a.getAddressLine1());
        }
    }

    @Test(groups = { "queryCurrentAddresses1" }, dependsOnGroups = { "queryAddresses" })
    public void queryCurrentAddresses1() {
        List<String> address1StringList = new ArrayList<String>();
        address1StringList.add("1-temporary-addressLine1");
        address1StringList.add("2-temporary-addressLine1");
        address1StringList.add("3-temporary-addressLine1");
        Calendar cal = CalendarUtils.getToday(false);
        cal.set(cal.get(Calendar.YEAR), 9, 15);
        List<Address> addresses = addressService.readCurrentAddresses(personId, cal, false);
        assert addresses.size() == 3;
        for (Address a : addresses) {
            assert address1StringList.contains(a.getAddressLine1());
        }
    }

    @Test(groups = { "queryCurrentAddresses2" }, dependsOnGroups = { "queryCurrentAddresses1" })
    public void queryCurrentAddresses2() {
        List<String> address1StringList = new ArrayList<String>();
        address1StringList.add("1-seasonal-addressLine1");
        address1StringList.add("2-seasonal-addressLine1");
        address1StringList.add("3-seasonal-addressLine1");
        Calendar cal = CalendarUtils.getToday(false);
        cal.set(cal.get(Calendar.YEAR), 10, 15);
        List<Address> addresses = addressService.readCurrentAddresses(personId, cal, false);
        assert addresses.size() == 3;
        for (Address a : addresses) {
            logger.debug("address: " + a.getActivationStatus() + ", effective=" + a.getEffectiveDate() + ", season start=" + a.getSeasonalStartDate() + ", season end=" + a.getSeasonalEndDate() + ", temp start=" + a.getTemporaryStartDate() + ", temp end=" + a.getTemporaryEndDate());
            assert address1StringList.contains(a.getAddressLine1());
        }

        cal = CalendarUtils.getToday(false);
        cal.set(cal.get(Calendar.YEAR), 2, 15);
        addresses = addressService.readCurrentAddresses(personId, cal, false);
        assert addresses.size() == 3;
        for (Address a : addresses) {
            assert address1StringList.contains(a.getAddressLine1());
        }
    }

    @Test(groups = { "queryCurrentAddresses3" }, dependsOnGroups = { "queryCurrentAddresses2" })
    public void queryCurrentAddresses3() {
        List<String> address1StringList = new ArrayList<String>();
        address1StringList.add("1-permanent-addressLine1");
        address1StringList.add("2-permanent-addressLine1");
        address1StringList.add("3-permanent-addressLine1");
        Calendar cal = CalendarUtils.getToday(false);
        cal.set(cal.get(Calendar.YEAR), 3, 1);
        List<Address> addresses = addressService.readCurrentAddresses(personId, cal, false);
        assert addresses.size() == 3;
        for (Address a : addresses) {
            assert address1StringList.contains(a.getAddressLine1());
        }
    }
}
