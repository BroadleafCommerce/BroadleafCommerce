package com.mpower.test.dataprovider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.testng.annotations.DataProvider;

import com.mpower.domain.Address;
import com.mpower.domain.Person;
import com.mpower.domain.Site;
import com.mpower.util.CalendarUtils;

public class AddressDataProvider {

    @DataProvider(name = "setupAddresses")
    public static Object[][] createAddresses() {
        Site site1 = new Site();
        site1.setName("setupAddressSite-1");

        Person person1 = new Person();
        person1.setFirstName("createAddressFirstName-1");
        person1.setLastName("createAddressLastName-1");

        List<Address> addresses = new ArrayList<Address>();
        Address address = new Address();
        address.setAddressLine1("1-permanent-addressLine1");
        address.setCity("city");
        address.setCountry("US");
        address.setPostalCode("11111");
        address.setStateProvince("state");
        address.setActivationStatus("permanent");
        address.setAddressType("home");
        address.setPerson(person1);
        addresses.add(address);

        address = new Address();
        address.setAddressLine1("2-permanent-addressLine1");
        address.setCity("city");
        address.setCountry("US");
        address.setPostalCode("11111");
        address.setStateProvince("state");
        address.setActivationStatus("permanent");
        address.setAddressType("work");
        address.setPerson(person1);
        addresses.add(address);

        address = new Address();
        address.setAddressLine1("3-permanent-addressLine1");
        address.setCity("city");
        address.setCountry("US");
        address.setPostalCode("11111");
        address.setStateProvince("state");
        address.setActivationStatus("permanent");
        address.setAddressType("home");
        address.setPerson(person1);
        addresses.add(address);

        // set seasonals to be 10/1 - 3/31
        address = new Address();
        address.setAddressLine1("1-seasonal-addressLine1");
        address.setCity("city");
        address.setCountry("US");
        address.setPostalCode("11111");
        address.setStateProvince("state");
        address.setActivationStatus("seasonal");
        Calendar today = CalendarUtils.getToday(false);
        Calendar seasonStart = new GregorianCalendar(today.get(Calendar.YEAR) - 1, 9, 1);
        address.setSeasonalStartDate(seasonStart.getTime());
        Calendar seasonEnd = new GregorianCalendar(today.get(Calendar.YEAR), 2, 31);
        address.setSeasonalEndDate(seasonEnd.getTime());
        address.setAddressType("home");
        address.setPerson(person1);
        addresses.add(address);

        address = new Address();
        address.setAddressLine1("2-seasonal-addressLine1");
        address.setCity("city");
        address.setCountry("US");
        address.setPostalCode("11111");
        address.setStateProvince("state");
        address.setActivationStatus("seasonal");
        address.setSeasonalStartDate(seasonStart.getTime());
        address.setSeasonalEndDate(seasonEnd.getTime());
        address.setAddressType("work");
        address.setPerson(person1);
        addresses.add(address);

        address = new Address();
        address.setAddressLine1("3-seasonal-addressLine1");
        address.setCity("city");
        address.setCountry("US");
        address.setPostalCode("11111");
        address.setStateProvince("state");
        address.setActivationStatus("seasonal");
        address.setSeasonalStartDate(seasonStart.getTime());
        address.setSeasonalEndDate(seasonEnd.getTime());
        address.setAddressType("home");
        address.setPerson(person1);
        addresses.add(address);

        // set temporary addresses for 10/15/<today's year> - 11/14/<today's year>
        address = new Address();
        address.setAddressLine1("1-temporary-addressLine1");
        address.setCity("city");
        address.setCountry("US");
        address.setPostalCode("11111");
        address.setStateProvince("state");
        address.setActivationStatus("temporary");
        Calendar tempStart = CalendarUtils.getToday(false);
        tempStart.set(tempStart.get(Calendar.YEAR), 9, 15);
        address.setTemporaryStartDate(tempStart.getTime());
        Calendar tempEnd = CalendarUtils.getToday(false);
        tempEnd.set(tempEnd.get(Calendar.YEAR), 10, 14);
        address.setTemporaryEndDate(tempEnd.getTime());
        address.setAddressType("home");
        address.setPerson(person1);
        addresses.add(address);

        address = new Address();
        address.setAddressLine1("2-temporary-addressLine1");
        address.setCity("city");
        address.setCountry("US");
        address.setPostalCode("11111");
        address.setStateProvince("state");
        address.setActivationStatus("temporary");
        address.setTemporaryStartDate(tempStart.getTime());
        address.setTemporaryEndDate(tempEnd.getTime());
        address.setAddressType("work");
        address.setPerson(person1);
        addresses.add(address);

        address = new Address();
        address.setAddressLine1("3-temporary-addressLine1");
        address.setCity("city");
        address.setCountry("US");
        address.setPostalCode("11111");
        address.setStateProvince("state");
        address.setActivationStatus("temporary");
        address.setTemporaryStartDate(tempStart.getTime());
        address.setTemporaryEndDate(tempEnd.getTime());
        address.setAddressType("home");
        address.setPerson(person1);
        addresses.add(address);

        return new Object[][] { new Object[] { site1, person1, addresses } };
    }
}
