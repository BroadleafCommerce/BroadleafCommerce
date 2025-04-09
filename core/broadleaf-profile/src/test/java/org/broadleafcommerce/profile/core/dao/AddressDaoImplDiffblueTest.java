/*-
 * #%L
 * BroadleafCommerce Profile
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.profile.core.dao;

import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.broadleafcommerce.common.i18n.domain.ISOCountryImpl;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.CountryImpl;
import org.broadleafcommerce.profile.core.domain.PhoneImpl;
import org.broadleafcommerce.profile.core.domain.StateImpl;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AddressDaoImplDiffblueTest {
  @InjectMocks
  private AddressDaoImpl addressDaoImpl;

  @Mock
  private EntityConfiguration entityConfiguration;

  /**
   * Test {@link AddressDaoImpl#create()}.
   * <p>
   * Method under test: {@link AddressDaoImpl#create()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Address AddressDaoImpl.create()"})
  public void testCreate() {
    // Arrange
    AddressImpl addressImpl = new AddressImpl();
    addressImpl.setActive(true);
    addressImpl.setAddressLine1("42 Main St");
    addressImpl.setAddressLine2("42 Main St");
    addressImpl.setAddressLine3("42 Main St");
    addressImpl.setBusiness(true);
    addressImpl.setCity("Oxford");
    addressImpl.setCompanyName("Company Name");
    addressImpl.setCountry(new CountryImpl());
    addressImpl.setCounty("3");
    addressImpl.setDefault(true);
    addressImpl.setEmailAddress("42 Main St");
    addressImpl.setFax("Fax");
    addressImpl.setFirstName("Jane");
    addressImpl.setFullName("Dr Jane Doe");
    addressImpl.setId(1L);
    addressImpl.setIsoCountryAlpha2(new ISOCountryImpl());
    addressImpl.setIsoCountrySubdivision("GB");
    addressImpl.setLastName("Doe");
    addressImpl.setMailing(true);
    addressImpl.setPhoneFax(new PhoneImpl());
    addressImpl.setPhonePrimary(new PhoneImpl());
    addressImpl.setPhoneSecondary(new PhoneImpl());
    addressImpl.setPostalCode("Postal Code");
    addressImpl.setPrimaryPhone("6625550144");
    addressImpl.setSecondaryPhone("6625550144");
    addressImpl.setStandardized(true);
    addressImpl.setState(new StateImpl());
    addressImpl.setStateProvinceRegion("us-east-2");
    addressImpl.setStreet(true);
    addressImpl.setTokenizedAddress("42 Main St");
    addressImpl.setVerificationLevel("Verification Level");
    addressImpl.setZipFour("21654");
    when(entityConfiguration.createEntityInstance(Mockito.<String>any())).thenReturn(addressImpl);

    // Act
    Address actualCreateResult = addressDaoImpl.create();

    // Assert
    verify(entityConfiguration).createEntityInstance(eq("org.broadleafcommerce.profile.core.domain.Address"));
    assertSame(addressImpl, actualCreateResult);
  }
}
