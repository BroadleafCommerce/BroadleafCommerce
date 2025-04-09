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
package org.broadleafcommerce.profile.core.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopierExtensionManager;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.i18n.domain.ISOCountry;
import org.broadleafcommerce.common.i18n.domain.ISOCountryImpl;
import org.broadleafcommerce.common.service.GenericEntityService;
import org.broadleafcommerce.common.site.domain.CatalogImpl;
import org.broadleafcommerce.common.site.domain.SiteImpl;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = {"/bl-profile-applicationContext-entity.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class AddressImplDiffblueTest {
  @Autowired
  private AddressImpl addressImpl;

  /**
   * Test getters and setters.
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link AddressImpl#setActive(boolean)}
   *   <li>{@link AddressImpl#setAddressLine1(String)}
   *   <li>{@link AddressImpl#setAddressLine2(String)}
   *   <li>{@link AddressImpl#setAddressLine3(String)}
   *   <li>{@link AddressImpl#setBusiness(boolean)}
   *   <li>{@link AddressImpl#setCity(String)}
   *   <li>{@link AddressImpl#setCompanyName(String)}
   *   <li>{@link AddressImpl#setCountry(Country)}
   *   <li>{@link AddressImpl#setCounty(String)}
   *   <li>{@link AddressImpl#setDefault(boolean)}
   *   <li>{@link AddressImpl#setEmailAddress(String)}
   *   <li>{@link AddressImpl#setFax(String)}
   *   <li>{@link AddressImpl#setFirstName(String)}
   *   <li>{@link AddressImpl#setFullName(String)}
   *   <li>{@link AddressImpl#setId(Long)}
   *   <li>{@link AddressImpl#setIsoCountryAlpha2(ISOCountry)}
   *   <li>{@link AddressImpl#setIsoCountrySubdivision(String)}
   *   <li>{@link AddressImpl#setLastName(String)}
   *   <li>{@link AddressImpl#setMailing(boolean)}
   *   <li>{@link AddressImpl#setPhoneFax(Phone)}
   *   <li>{@link AddressImpl#setPhonePrimary(Phone)}
   *   <li>{@link AddressImpl#setPhoneSecondary(Phone)}
   *   <li>{@link AddressImpl#setPostalCode(String)}
   *   <li>{@link AddressImpl#setPrimaryPhone(String)}
   *   <li>{@link AddressImpl#setSecondaryPhone(String)}
   *   <li>{@link AddressImpl#setStandardized(Boolean)}
   *   <li>{@link AddressImpl#setState(State)}
   *   <li>{@link AddressImpl#setStateProvinceRegion(String)}
   *   <li>{@link AddressImpl#setStreet(boolean)}
   *   <li>{@link AddressImpl#setTokenizedAddress(String)}
   *   <li>{@link AddressImpl#setVerificationLevel(String)}
   *   <li>{@link AddressImpl#setZipFour(String)}
   *   <li>{@link AddressImpl#getAddressLine1()}
   *   <li>{@link AddressImpl#getAddressLine2()}
   *   <li>{@link AddressImpl#getAddressLine3()}
   *   <li>{@link AddressImpl#getCity()}
   *   <li>{@link AddressImpl#getCompanyName()}
   *   <li>{@link AddressImpl#getCountry()}
   *   <li>{@link AddressImpl#getCounty()}
   *   <li>{@link AddressImpl#getEmailAddress()}
   *   <li>{@link AddressImpl#getFax()}
   *   <li>{@link AddressImpl#getFirstName()}
   *   <li>{@link AddressImpl#getFullName()}
   *   <li>{@link AddressImpl#getId()}
   *   <li>{@link AddressImpl#getIsoCountryAlpha2()}
   *   <li>{@link AddressImpl#getIsoCountrySubdivision()}
   *   <li>{@link AddressImpl#getLastName()}
   *   <li>{@link AddressImpl#getPostalCode()}
   *   <li>{@link AddressImpl#getPrimaryPhone()}
   *   <li>{@link AddressImpl#getSecondaryPhone()}
   *   <li>{@link AddressImpl#getStandardized()}
   *   <li>{@link AddressImpl#getState()}
   *   <li>{@link AddressImpl#getStateProvinceRegion()}
   *   <li>{@link AddressImpl#getTokenizedAddress()}
   *   <li>{@link AddressImpl#getVerificationLevel()}
   *   <li>{@link AddressImpl#getZipFour()}
   *   <li>{@link AddressImpl#isActive()}
   *   <li>{@link AddressImpl#isBusiness()}
   *   <li>{@link AddressImpl#isDefault()}
   *   <li>{@link AddressImpl#isMailing()}
   *   <li>{@link AddressImpl#isStreet()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"String AddressImpl.getAddressLine1()", "String AddressImpl.getAddressLine2()",
      "String AddressImpl.getAddressLine3()", "String AddressImpl.getCity()", "String AddressImpl.getCompanyName()",
      "Country AddressImpl.getCountry()", "String AddressImpl.getCounty()", "String AddressImpl.getEmailAddress()",
      "String AddressImpl.getFax()", "String AddressImpl.getFirstName()", "String AddressImpl.getFullName()",
      "Long AddressImpl.getId()", "ISOCountry AddressImpl.getIsoCountryAlpha2()",
      "String AddressImpl.getIsoCountrySubdivision()", "String AddressImpl.getLastName()",
      "String AddressImpl.getPostalCode()", "String AddressImpl.getPrimaryPhone()",
      "String AddressImpl.getSecondaryPhone()", "Boolean AddressImpl.getStandardized()", "State AddressImpl.getState()",
      "String AddressImpl.getStateProvinceRegion()", "String AddressImpl.getTokenizedAddress()",
      "String AddressImpl.getVerificationLevel()", "String AddressImpl.getZipFour()", "boolean AddressImpl.isActive()",
      "boolean AddressImpl.isBusiness()", "boolean AddressImpl.isDefault()", "boolean AddressImpl.isMailing()",
      "boolean AddressImpl.isStreet()", "void AddressImpl.setActive(boolean)",
      "void AddressImpl.setAddressLine1(String)", "void AddressImpl.setAddressLine2(String)",
      "void AddressImpl.setAddressLine3(String)", "void AddressImpl.setBusiness(boolean)",
      "void AddressImpl.setCity(String)", "void AddressImpl.setCompanyName(String)",
      "void AddressImpl.setCountry(Country)", "void AddressImpl.setCounty(String)",
      "void AddressImpl.setDefault(boolean)", "void AddressImpl.setEmailAddress(String)",
      "void AddressImpl.setFax(String)", "void AddressImpl.setFirstName(String)",
      "void AddressImpl.setFullName(String)", "void AddressImpl.setId(Long)",
      "void AddressImpl.setIsoCountryAlpha2(ISOCountry)", "void AddressImpl.setIsoCountrySubdivision(String)",
      "void AddressImpl.setLastName(String)", "void AddressImpl.setMailing(boolean)",
      "void AddressImpl.setPhoneFax(Phone)", "void AddressImpl.setPhonePrimary(Phone)",
      "void AddressImpl.setPhoneSecondary(Phone)", "void AddressImpl.setPostalCode(String)",
      "void AddressImpl.setPrimaryPhone(String)", "void AddressImpl.setSecondaryPhone(String)",
      "void AddressImpl.setStandardized(Boolean)", "void AddressImpl.setState(State)",
      "void AddressImpl.setStateProvinceRegion(String)", "void AddressImpl.setStreet(boolean)",
      "void AddressImpl.setTokenizedAddress(String)", "void AddressImpl.setVerificationLevel(String)",
      "void AddressImpl.setZipFour(String)"})
  public void testGettersAndSetters() {
    // Arrange
    AddressImpl addressImpl = new AddressImpl();

    // Act
    addressImpl.setActive(true);
    addressImpl.setAddressLine1("42 Main St");
    addressImpl.setAddressLine2("42 Main St");
    addressImpl.setAddressLine3("42 Main St");
    addressImpl.setBusiness(true);
    addressImpl.setCity("Oxford");
    addressImpl.setCompanyName("Company Name");
    CountryImpl country = new CountryImpl();
    addressImpl.setCountry(country);
    addressImpl.setCounty("3");
    addressImpl.setDefault(true);
    addressImpl.setEmailAddress("42 Main St");
    addressImpl.setFax("Fax");
    addressImpl.setFirstName("Jane");
    addressImpl.setFullName("Dr Jane Doe");
    addressImpl.setId(1L);
    ISOCountryImpl isoCountryAlpha2 = new ISOCountryImpl();
    addressImpl.setIsoCountryAlpha2(isoCountryAlpha2);
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
    StateImpl state = new StateImpl();
    addressImpl.setState(state);
    addressImpl.setStateProvinceRegion("us-east-2");
    addressImpl.setStreet(true);
    addressImpl.setTokenizedAddress("42 Main St");
    addressImpl.setVerificationLevel("Verification Level");
    addressImpl.setZipFour("21654");
    String actualAddressLine1 = addressImpl.getAddressLine1();
    String actualAddressLine2 = addressImpl.getAddressLine2();
    String actualAddressLine3 = addressImpl.getAddressLine3();
    String actualCity = addressImpl.getCity();
    String actualCompanyName = addressImpl.getCompanyName();
    Country actualCountry = addressImpl.getCountry();
    String actualCounty = addressImpl.getCounty();
    String actualEmailAddress = addressImpl.getEmailAddress();
    String actualFax = addressImpl.getFax();
    String actualFirstName = addressImpl.getFirstName();
    String actualFullName = addressImpl.getFullName();
    Long actualId = addressImpl.getId();
    ISOCountry actualIsoCountryAlpha2 = addressImpl.getIsoCountryAlpha2();
    String actualIsoCountrySubdivision = addressImpl.getIsoCountrySubdivision();
    String actualLastName = addressImpl.getLastName();
    String actualPostalCode = addressImpl.getPostalCode();
    String actualPrimaryPhone = addressImpl.getPrimaryPhone();
    String actualSecondaryPhone = addressImpl.getSecondaryPhone();
    Boolean actualStandardized = addressImpl.getStandardized();
    State actualState = addressImpl.getState();
    String actualStateProvinceRegion = addressImpl.getStateProvinceRegion();
    String actualTokenizedAddress = addressImpl.getTokenizedAddress();
    String actualVerificationLevel = addressImpl.getVerificationLevel();
    String actualZipFour = addressImpl.getZipFour();
    boolean actualIsActiveResult = addressImpl.isActive();
    boolean actualIsBusinessResult = addressImpl.isBusiness();
    boolean actualIsDefaultResult = addressImpl.isDefault();
    boolean actualIsMailingResult = addressImpl.isMailing();
    boolean actualIsStreetResult = addressImpl.isStreet();

    // Assert
    assertEquals("21654", actualZipFour);
    assertEquals("3", actualCounty);
    assertEquals("42 Main St", actualAddressLine1);
    assertEquals("42 Main St", actualAddressLine2);
    assertEquals("42 Main St", actualAddressLine3);
    assertEquals("42 Main St", actualEmailAddress);
    assertEquals("42 Main St", actualTokenizedAddress);
    assertEquals("6625550144", actualPrimaryPhone);
    assertEquals("6625550144", actualSecondaryPhone);
    assertEquals("Company Name", actualCompanyName);
    assertEquals("Doe", actualLastName);
    assertEquals("Dr Jane Doe", actualFullName);
    assertEquals("Fax", actualFax);
    assertEquals("GB", actualIsoCountrySubdivision);
    assertEquals("Jane", actualFirstName);
    assertEquals("Oxford", actualCity);
    assertEquals("Postal Code", actualPostalCode);
    assertEquals("Verification Level", actualVerificationLevel);
    assertEquals("us-east-2", actualStateProvinceRegion);
    assertEquals(1L, actualId.longValue());
    assertTrue(actualStandardized);
    assertTrue(actualIsActiveResult);
    assertTrue(actualIsBusinessResult);
    assertTrue(actualIsDefaultResult);
    assertTrue(actualIsMailingResult);
    assertTrue(actualIsStreetResult);
    assertSame(isoCountryAlpha2, actualIsoCountryAlpha2);
    assertSame(country, actualCountry);
    assertSame(state, actualState);
  }

  /**
   * Test {@link AddressImpl#getPhonePrimary()}.
   * <ul>
   *   <li>Given {@link AddressImpl} (default constructor) PhonePrimary is {@code null}.</li>
   *   <li>Then return {@link PhoneImpl}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressImpl#getPhonePrimary()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Phone AddressImpl.getPhonePrimary()"})
  public void testGetPhonePrimary_givenAddressImplPhonePrimaryIsNull_thenReturnPhoneImpl() {
    // Arrange
    AddressImpl addressImpl2 = new AddressImpl();
    addressImpl2.setActive(true);
    addressImpl2.setAddressLine1("42 Main St");
    addressImpl2.setAddressLine2("42 Main St");
    addressImpl2.setAddressLine3("42 Main St");
    addressImpl2.setBusiness(true);
    addressImpl2.setCity("Oxford");
    addressImpl2.setCompanyName("Company Name");
    addressImpl2.setCountry(new CountryImpl());
    addressImpl2.setCounty("3");
    addressImpl2.setDefault(true);
    addressImpl2.setEmailAddress("42 Main St");
    addressImpl2.setFax("Fax");
    addressImpl2.setFirstName("Jane");
    addressImpl2.setFullName("Dr Jane Doe");
    addressImpl2.setId(1L);
    addressImpl2.setIsoCountryAlpha2(new ISOCountryImpl());
    addressImpl2.setIsoCountrySubdivision("GB");
    addressImpl2.setLastName("Doe");
    addressImpl2.setMailing(true);
    addressImpl2.setPhoneFax(new PhoneImpl());
    addressImpl2.setPhoneSecondary(new PhoneImpl());
    addressImpl2.setPostalCode("Postal Code");
    addressImpl2.setSecondaryPhone("6625550144");
    addressImpl2.setStandardized(true);
    addressImpl2.setState(new StateImpl());
    addressImpl2.setStateProvinceRegion("us-east-2");
    addressImpl2.setStreet(true);
    addressImpl2.setTokenizedAddress("42 Main St");
    addressImpl2.setVerificationLevel("Verification Level");
    addressImpl2.setZipFour("21654");
    addressImpl2.setPhonePrimary(null);
    addressImpl2.setPrimaryPhone("foo");

    // Act
    Phone actualPhonePrimary = addressImpl2.getPhonePrimary();

    // Assert
    assertTrue(actualPhonePrimary instanceof PhoneImpl);
    assertEquals("foo", actualPhonePrimary.getPhoneNumber());
    assertNull(actualPhonePrimary.getId());
    assertNull(actualPhonePrimary.getCountryCode());
    assertNull(actualPhonePrimary.getExtension());
    assertFalse(actualPhonePrimary.isDefault());
    assertTrue(actualPhonePrimary.isActive());
  }

  /**
   * Test {@link AddressImpl#getPhonePrimary()}.
   * <ul>
   *   <li>Given {@link AddressImpl} (default constructor) PhonePrimary is {@link PhoneImpl} (default constructor).</li>
   *   <li>Then return {@link PhoneImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressImpl#getPhonePrimary()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Phone AddressImpl.getPhonePrimary()"})
  public void testGetPhonePrimary_givenAddressImplPhonePrimaryIsPhoneImpl_thenReturnPhoneImpl() {
    // Arrange
    AddressImpl addressImpl2 = new AddressImpl();
    addressImpl2.setActive(true);
    addressImpl2.setAddressLine1("42 Main St");
    addressImpl2.setAddressLine2("42 Main St");
    addressImpl2.setAddressLine3("42 Main St");
    addressImpl2.setBusiness(true);
    addressImpl2.setCity("Oxford");
    addressImpl2.setCompanyName("Company Name");
    addressImpl2.setCountry(new CountryImpl());
    addressImpl2.setCounty("3");
    addressImpl2.setDefault(true);
    addressImpl2.setEmailAddress("42 Main St");
    addressImpl2.setFax("Fax");
    addressImpl2.setFirstName("Jane");
    addressImpl2.setFullName("Dr Jane Doe");
    addressImpl2.setId(1L);
    addressImpl2.setIsoCountryAlpha2(new ISOCountryImpl());
    addressImpl2.setIsoCountrySubdivision("GB");
    addressImpl2.setLastName("Doe");
    addressImpl2.setMailing(true);
    addressImpl2.setPhoneFax(new PhoneImpl());
    addressImpl2.setPhoneSecondary(new PhoneImpl());
    addressImpl2.setPostalCode("Postal Code");
    addressImpl2.setSecondaryPhone("6625550144");
    addressImpl2.setStandardized(true);
    addressImpl2.setState(new StateImpl());
    addressImpl2.setStateProvinceRegion("us-east-2");
    addressImpl2.setStreet(true);
    addressImpl2.setTokenizedAddress("42 Main St");
    addressImpl2.setVerificationLevel("Verification Level");
    addressImpl2.setZipFour("21654");
    PhoneImpl phonePrimary = new PhoneImpl();
    addressImpl2.setPhonePrimary(phonePrimary);
    addressImpl2.setPrimaryPhone(null);

    // Act and Assert
    assertSame(phonePrimary, addressImpl2.getPhonePrimary());
  }

  /**
   * Test {@link AddressImpl#getPhonePrimary()}.
   * <ul>
   *   <li>Given {@link AddressImpl} (default constructor).</li>
   *   <li>Then return {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressImpl#getPhonePrimary()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Phone AddressImpl.getPhonePrimary()"})
  public void testGetPhonePrimary_givenAddressImpl_thenReturnNull() {
    // Arrange, Act and Assert
    assertNull((new AddressImpl()).getPhonePrimary());
  }

  /**
   * Test {@link AddressImpl#getPhoneSecondary()}.
   * <ul>
   *   <li>Given {@link AddressImpl} (default constructor) PhoneSecondary is {@code null}.</li>
   *   <li>Then return {@link PhoneImpl}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressImpl#getPhoneSecondary()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Phone AddressImpl.getPhoneSecondary()"})
  public void testGetPhoneSecondary_givenAddressImplPhoneSecondaryIsNull_thenReturnPhoneImpl() {
    // Arrange
    AddressImpl addressImpl2 = new AddressImpl();
    addressImpl2.setActive(true);
    addressImpl2.setAddressLine1("42 Main St");
    addressImpl2.setAddressLine2("42 Main St");
    addressImpl2.setAddressLine3("42 Main St");
    addressImpl2.setBusiness(true);
    addressImpl2.setCity("Oxford");
    addressImpl2.setCompanyName("Company Name");
    addressImpl2.setCountry(new CountryImpl());
    addressImpl2.setCounty("3");
    addressImpl2.setDefault(true);
    addressImpl2.setEmailAddress("42 Main St");
    addressImpl2.setFax("Fax");
    addressImpl2.setFirstName("Jane");
    addressImpl2.setFullName("Dr Jane Doe");
    addressImpl2.setId(1L);
    addressImpl2.setIsoCountryAlpha2(new ISOCountryImpl());
    addressImpl2.setIsoCountrySubdivision("GB");
    addressImpl2.setLastName("Doe");
    addressImpl2.setMailing(true);
    addressImpl2.setPhoneFax(new PhoneImpl());
    addressImpl2.setPhonePrimary(new PhoneImpl());
    addressImpl2.setPostalCode("Postal Code");
    addressImpl2.setPrimaryPhone("6625550144");
    addressImpl2.setStandardized(true);
    addressImpl2.setState(new StateImpl());
    addressImpl2.setStateProvinceRegion("us-east-2");
    addressImpl2.setStreet(true);
    addressImpl2.setTokenizedAddress("42 Main St");
    addressImpl2.setVerificationLevel("Verification Level");
    addressImpl2.setZipFour("21654");
    addressImpl2.setPhoneSecondary(null);
    addressImpl2.setSecondaryPhone("foo");

    // Act
    Phone actualPhoneSecondary = addressImpl2.getPhoneSecondary();

    // Assert
    assertTrue(actualPhoneSecondary instanceof PhoneImpl);
    assertEquals("foo", actualPhoneSecondary.getPhoneNumber());
    assertNull(actualPhoneSecondary.getId());
    assertNull(actualPhoneSecondary.getCountryCode());
    assertNull(actualPhoneSecondary.getExtension());
    assertFalse(actualPhoneSecondary.isDefault());
    assertTrue(actualPhoneSecondary.isActive());
  }

  /**
   * Test {@link AddressImpl#getPhoneSecondary()}.
   * <ul>
   *   <li>Given {@link AddressImpl} (default constructor).</li>
   *   <li>Then return {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressImpl#getPhoneSecondary()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Phone AddressImpl.getPhoneSecondary()"})
  public void testGetPhoneSecondary_givenAddressImpl_thenReturnNull() {
    // Arrange, Act and Assert
    assertNull((new AddressImpl()).getPhoneSecondary());
  }

  /**
   * Test {@link AddressImpl#getPhoneSecondary()}.
   * <ul>
   *   <li>Then return {@link PhoneImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressImpl#getPhoneSecondary()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Phone AddressImpl.getPhoneSecondary()"})
  public void testGetPhoneSecondary_thenReturnPhoneImpl() {
    // Arrange
    AddressImpl addressImpl2 = new AddressImpl();
    addressImpl2.setActive(true);
    addressImpl2.setAddressLine1("42 Main St");
    addressImpl2.setAddressLine2("42 Main St");
    addressImpl2.setAddressLine3("42 Main St");
    addressImpl2.setBusiness(true);
    addressImpl2.setCity("Oxford");
    addressImpl2.setCompanyName("Company Name");
    addressImpl2.setCountry(new CountryImpl());
    addressImpl2.setCounty("3");
    addressImpl2.setDefault(true);
    addressImpl2.setEmailAddress("42 Main St");
    addressImpl2.setFax("Fax");
    addressImpl2.setFirstName("Jane");
    addressImpl2.setFullName("Dr Jane Doe");
    addressImpl2.setId(1L);
    addressImpl2.setIsoCountryAlpha2(new ISOCountryImpl());
    addressImpl2.setIsoCountrySubdivision("GB");
    addressImpl2.setLastName("Doe");
    addressImpl2.setMailing(true);
    addressImpl2.setPhoneFax(new PhoneImpl());
    addressImpl2.setPhonePrimary(new PhoneImpl());
    addressImpl2.setPostalCode("Postal Code");
    addressImpl2.setPrimaryPhone("6625550144");
    addressImpl2.setStandardized(true);
    addressImpl2.setState(new StateImpl());
    addressImpl2.setStateProvinceRegion("us-east-2");
    addressImpl2.setStreet(true);
    addressImpl2.setTokenizedAddress("42 Main St");
    addressImpl2.setVerificationLevel("Verification Level");
    addressImpl2.setZipFour("21654");
    PhoneImpl phoneSecondary = new PhoneImpl();
    addressImpl2.setPhoneSecondary(phoneSecondary);
    addressImpl2.setSecondaryPhone(null);

    // Act and Assert
    assertSame(phoneSecondary, addressImpl2.getPhoneSecondary());
  }

  /**
   * Test {@link AddressImpl#getPhoneFax()}.
   * <ul>
   *   <li>Given {@link AddressImpl} (default constructor) PhoneFax is {@code null}.</li>
   *   <li>Then return {@link PhoneImpl}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressImpl#getPhoneFax()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Phone AddressImpl.getPhoneFax()"})
  public void testGetPhoneFax_givenAddressImplPhoneFaxIsNull_thenReturnPhoneImpl() {
    // Arrange
    AddressImpl addressImpl2 = new AddressImpl();
    addressImpl2.setActive(true);
    addressImpl2.setAddressLine1("42 Main St");
    addressImpl2.setAddressLine2("42 Main St");
    addressImpl2.setAddressLine3("42 Main St");
    addressImpl2.setBusiness(true);
    addressImpl2.setCity("Oxford");
    addressImpl2.setCompanyName("Company Name");
    addressImpl2.setCountry(new CountryImpl());
    addressImpl2.setCounty("3");
    addressImpl2.setDefault(true);
    addressImpl2.setEmailAddress("42 Main St");
    addressImpl2.setFirstName("Jane");
    addressImpl2.setFullName("Dr Jane Doe");
    addressImpl2.setId(1L);
    addressImpl2.setIsoCountryAlpha2(new ISOCountryImpl());
    addressImpl2.setIsoCountrySubdivision("GB");
    addressImpl2.setLastName("Doe");
    addressImpl2.setMailing(true);
    addressImpl2.setPhonePrimary(new PhoneImpl());
    addressImpl2.setPhoneSecondary(new PhoneImpl());
    addressImpl2.setPostalCode("Postal Code");
    addressImpl2.setPrimaryPhone("6625550144");
    addressImpl2.setSecondaryPhone("6625550144");
    addressImpl2.setStandardized(true);
    addressImpl2.setState(new StateImpl());
    addressImpl2.setStateProvinceRegion("us-east-2");
    addressImpl2.setStreet(true);
    addressImpl2.setTokenizedAddress("42 Main St");
    addressImpl2.setVerificationLevel("Verification Level");
    addressImpl2.setZipFour("21654");
    addressImpl2.setPhoneFax(null);
    addressImpl2.setFax("foo");

    // Act
    Phone actualPhoneFax = addressImpl2.getPhoneFax();

    // Assert
    assertTrue(actualPhoneFax instanceof PhoneImpl);
    assertEquals("foo", actualPhoneFax.getPhoneNumber());
    assertNull(actualPhoneFax.getId());
    assertNull(actualPhoneFax.getCountryCode());
    assertNull(actualPhoneFax.getExtension());
    assertFalse(actualPhoneFax.isDefault());
    assertTrue(actualPhoneFax.isActive());
  }

  /**
   * Test {@link AddressImpl#getPhoneFax()}.
   * <ul>
   *   <li>Given {@link AddressImpl} (default constructor) PhoneFax is {@link PhoneImpl} (default constructor).</li>
   *   <li>Then return {@link PhoneImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressImpl#getPhoneFax()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Phone AddressImpl.getPhoneFax()"})
  public void testGetPhoneFax_givenAddressImplPhoneFaxIsPhoneImpl_thenReturnPhoneImpl() {
    // Arrange
    AddressImpl addressImpl2 = new AddressImpl();
    addressImpl2.setActive(true);
    addressImpl2.setAddressLine1("42 Main St");
    addressImpl2.setAddressLine2("42 Main St");
    addressImpl2.setAddressLine3("42 Main St");
    addressImpl2.setBusiness(true);
    addressImpl2.setCity("Oxford");
    addressImpl2.setCompanyName("Company Name");
    addressImpl2.setCountry(new CountryImpl());
    addressImpl2.setCounty("3");
    addressImpl2.setDefault(true);
    addressImpl2.setEmailAddress("42 Main St");
    addressImpl2.setFirstName("Jane");
    addressImpl2.setFullName("Dr Jane Doe");
    addressImpl2.setId(1L);
    addressImpl2.setIsoCountryAlpha2(new ISOCountryImpl());
    addressImpl2.setIsoCountrySubdivision("GB");
    addressImpl2.setLastName("Doe");
    addressImpl2.setMailing(true);
    addressImpl2.setPhonePrimary(new PhoneImpl());
    addressImpl2.setPhoneSecondary(new PhoneImpl());
    addressImpl2.setPostalCode("Postal Code");
    addressImpl2.setPrimaryPhone("6625550144");
    addressImpl2.setSecondaryPhone("6625550144");
    addressImpl2.setStandardized(true);
    addressImpl2.setState(new StateImpl());
    addressImpl2.setStateProvinceRegion("us-east-2");
    addressImpl2.setStreet(true);
    addressImpl2.setTokenizedAddress("42 Main St");
    addressImpl2.setVerificationLevel("Verification Level");
    addressImpl2.setZipFour("21654");
    PhoneImpl phoneFax = new PhoneImpl();
    addressImpl2.setPhoneFax(phoneFax);
    addressImpl2.setFax(null);

    // Act and Assert
    assertSame(phoneFax, addressImpl2.getPhoneFax());
  }

  /**
   * Test {@link AddressImpl#getPhoneFax()}.
   * <ul>
   *   <li>Given {@link AddressImpl} (default constructor).</li>
   *   <li>Then return {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressImpl#getPhoneFax()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Phone AddressImpl.getPhoneFax()"})
  public void testGetPhoneFax_givenAddressImpl_thenReturnNull() {
    // Arrange, Act and Assert
    assertNull((new AddressImpl()).getPhoneFax());
  }

  /**
   * Test {@link AddressImpl#equals(Object)}, and {@link AddressImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link AddressImpl#equals(Object)}
   *   <li>{@link AddressImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean AddressImpl.equals(Object)", "int AddressImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual() {
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

    AddressImpl addressImpl2 = new AddressImpl();
    addressImpl2.setActive(true);
    addressImpl2.setAddressLine1("42 Main St");
    addressImpl2.setAddressLine2("42 Main St");
    addressImpl2.setAddressLine3("42 Main St");
    addressImpl2.setBusiness(true);
    addressImpl2.setCity("Oxford");
    addressImpl2.setCompanyName("Company Name");
    addressImpl2.setCountry(new CountryImpl());
    addressImpl2.setCounty("3");
    addressImpl2.setDefault(true);
    addressImpl2.setEmailAddress("42 Main St");
    addressImpl2.setFax("Fax");
    addressImpl2.setFirstName("Jane");
    addressImpl2.setFullName("Dr Jane Doe");
    addressImpl2.setId(1L);
    addressImpl2.setIsoCountryAlpha2(new ISOCountryImpl());
    addressImpl2.setIsoCountrySubdivision("GB");
    addressImpl2.setLastName("Doe");
    addressImpl2.setMailing(true);
    addressImpl2.setPhoneFax(new PhoneImpl());
    addressImpl2.setPhonePrimary(new PhoneImpl());
    addressImpl2.setPhoneSecondary(new PhoneImpl());
    addressImpl2.setPostalCode("Postal Code");
    addressImpl2.setPrimaryPhone("6625550144");
    addressImpl2.setSecondaryPhone("6625550144");
    addressImpl2.setStandardized(true);
    addressImpl2.setState(new StateImpl());
    addressImpl2.setStateProvinceRegion("us-east-2");
    addressImpl2.setStreet(true);
    addressImpl2.setTokenizedAddress("42 Main St");
    addressImpl2.setVerificationLevel("Verification Level");
    addressImpl2.setZipFour("21654");

    // Act and Assert
    assertEquals(addressImpl, addressImpl2);
    int expectedHashCodeResult = addressImpl.hashCode();
    assertEquals(expectedHashCodeResult, addressImpl2.hashCode());
  }

  /**
   * Test {@link AddressImpl#equals(Object)}, and {@link AddressImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link AddressImpl#equals(Object)}
   *   <li>{@link AddressImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean AddressImpl.equals(Object)", "int AddressImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual2() {
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
    addressImpl.setId(null);
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

    AddressImpl addressImpl2 = new AddressImpl();
    addressImpl2.setActive(true);
    addressImpl2.setAddressLine1("42 Main St");
    addressImpl2.setAddressLine2("42 Main St");
    addressImpl2.setAddressLine3("42 Main St");
    addressImpl2.setBusiness(true);
    addressImpl2.setCity("Oxford");
    addressImpl2.setCompanyName("Company Name");
    addressImpl2.setCountry(new CountryImpl());
    addressImpl2.setCounty("3");
    addressImpl2.setDefault(true);
    addressImpl2.setEmailAddress("42 Main St");
    addressImpl2.setFax("Fax");
    addressImpl2.setFirstName("Jane");
    addressImpl2.setFullName("Dr Jane Doe");
    addressImpl2.setId(1L);
    addressImpl2.setIsoCountryAlpha2(new ISOCountryImpl());
    addressImpl2.setIsoCountrySubdivision("GB");
    addressImpl2.setLastName("Doe");
    addressImpl2.setMailing(true);
    addressImpl2.setPhoneFax(new PhoneImpl());
    addressImpl2.setPhonePrimary(new PhoneImpl());
    addressImpl2.setPhoneSecondary(new PhoneImpl());
    addressImpl2.setPostalCode("Postal Code");
    addressImpl2.setPrimaryPhone("6625550144");
    addressImpl2.setSecondaryPhone("6625550144");
    addressImpl2.setStandardized(true);
    addressImpl2.setState(new StateImpl());
    addressImpl2.setStateProvinceRegion("us-east-2");
    addressImpl2.setStreet(true);
    addressImpl2.setTokenizedAddress("42 Main St");
    addressImpl2.setVerificationLevel("Verification Level");
    addressImpl2.setZipFour("21654");

    // Act and Assert
    assertEquals(addressImpl, addressImpl2);
    int notExpectedHashCodeResult = addressImpl.hashCode();
    assertNotEquals(notExpectedHashCodeResult, addressImpl2.hashCode());
  }

  /**
   * Test {@link AddressImpl#equals(Object)}, and {@link AddressImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link AddressImpl#equals(Object)}
   *   <li>{@link AddressImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean AddressImpl.equals(Object)", "int AddressImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual3() {
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

    AddressImpl addressImpl2 = new AddressImpl();
    addressImpl2.setActive(true);
    addressImpl2.setAddressLine1("42 Main St");
    addressImpl2.setAddressLine2("42 Main St");
    addressImpl2.setAddressLine3("42 Main St");
    addressImpl2.setBusiness(true);
    addressImpl2.setCity("Oxford");
    addressImpl2.setCompanyName("Company Name");
    addressImpl2.setCountry(new CountryImpl());
    addressImpl2.setCounty("3");
    addressImpl2.setDefault(true);
    addressImpl2.setEmailAddress("42 Main St");
    addressImpl2.setFax("Fax");
    addressImpl2.setFirstName("Jane");
    addressImpl2.setFullName("Dr Jane Doe");
    addressImpl2.setId(null);
    addressImpl2.setIsoCountryAlpha2(new ISOCountryImpl());
    addressImpl2.setIsoCountrySubdivision("GB");
    addressImpl2.setLastName("Doe");
    addressImpl2.setMailing(true);
    addressImpl2.setPhoneFax(new PhoneImpl());
    addressImpl2.setPhonePrimary(new PhoneImpl());
    addressImpl2.setPhoneSecondary(new PhoneImpl());
    addressImpl2.setPostalCode("Postal Code");
    addressImpl2.setPrimaryPhone("6625550144");
    addressImpl2.setSecondaryPhone("6625550144");
    addressImpl2.setStandardized(true);
    addressImpl2.setState(new StateImpl());
    addressImpl2.setStateProvinceRegion("us-east-2");
    addressImpl2.setStreet(true);
    addressImpl2.setTokenizedAddress("42 Main St");
    addressImpl2.setVerificationLevel("Verification Level");
    addressImpl2.setZipFour("21654");

    // Act and Assert
    assertEquals(addressImpl, addressImpl2);
    int notExpectedHashCodeResult = addressImpl.hashCode();
    assertNotEquals(notExpectedHashCodeResult, addressImpl2.hashCode());
  }

  /**
   * Test {@link AddressImpl#equals(Object)}, and {@link AddressImpl#hashCode()}.
   * <ul>
   *   <li>When other is same.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link AddressImpl#equals(Object)}
   *   <li>{@link AddressImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean AddressImpl.equals(Object)", "int AddressImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual() {
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

    // Act and Assert
    assertEquals(addressImpl, addressImpl);
    int expectedHashCodeResult = addressImpl.hashCode();
    assertEquals(expectedHashCodeResult, addressImpl.hashCode());
  }

  /**
   * Test {@link AddressImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean AddressImpl.equals(Object)", "int AddressImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual() {
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
    addressImpl.setId(2L);
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

    AddressImpl addressImpl2 = new AddressImpl();
    addressImpl2.setActive(true);
    addressImpl2.setAddressLine1("42 Main St");
    addressImpl2.setAddressLine2("42 Main St");
    addressImpl2.setAddressLine3("42 Main St");
    addressImpl2.setBusiness(true);
    addressImpl2.setCity("Oxford");
    addressImpl2.setCompanyName("Company Name");
    addressImpl2.setCountry(new CountryImpl());
    addressImpl2.setCounty("3");
    addressImpl2.setDefault(true);
    addressImpl2.setEmailAddress("42 Main St");
    addressImpl2.setFax("Fax");
    addressImpl2.setFirstName("Jane");
    addressImpl2.setFullName("Dr Jane Doe");
    addressImpl2.setId(1L);
    addressImpl2.setIsoCountryAlpha2(new ISOCountryImpl());
    addressImpl2.setIsoCountrySubdivision("GB");
    addressImpl2.setLastName("Doe");
    addressImpl2.setMailing(true);
    addressImpl2.setPhoneFax(new PhoneImpl());
    addressImpl2.setPhonePrimary(new PhoneImpl());
    addressImpl2.setPhoneSecondary(new PhoneImpl());
    addressImpl2.setPostalCode("Postal Code");
    addressImpl2.setPrimaryPhone("6625550144");
    addressImpl2.setSecondaryPhone("6625550144");
    addressImpl2.setStandardized(true);
    addressImpl2.setState(new StateImpl());
    addressImpl2.setStateProvinceRegion("us-east-2");
    addressImpl2.setStreet(true);
    addressImpl2.setTokenizedAddress("42 Main St");
    addressImpl2.setVerificationLevel("Verification Level");
    addressImpl2.setZipFour("21654");

    // Act and Assert
    assertNotEquals(addressImpl, addressImpl2);
  }

  /**
   * Test {@link AddressImpl#equals(Object)}.
   * <ul>
   *   <li>When other is {@code null}.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean AddressImpl.equals(Object)", "int AddressImpl.hashCode()"})
  public void testEquals_whenOtherIsNull_thenReturnNotEqual() {
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

    // Act and Assert
    assertNotEquals(addressImpl, null);
  }

  /**
   * Test {@link AddressImpl#equals(Object)}.
   * <ul>
   *   <li>When other is wrong type.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean AddressImpl.equals(Object)", "int AddressImpl.hashCode()"})
  public void testEquals_whenOtherIsWrongType_thenReturnNotEqual() {
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

    // Act and Assert
    assertNotEquals(addressImpl, "Different type to AddressImpl");
  }

  /**
   * Test {@link AddressImpl#createOrRetrieveCopyInstance(MultiTenantCopyContext)}.
   * <p>
   * Method under test: {@link AddressImpl#createOrRetrieveCopyInstance(MultiTenantCopyContext)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CreateResponse AddressImpl.createOrRetrieveCopyInstance(MultiTenantCopyContext)"})
  public void testCreateOrRetrieveCopyInstance() throws CloneNotSupportedException {
    // Arrange
    AddressImpl addressImpl2 = new AddressImpl();
    MultiTenantCopyContext context = mock(MultiTenantCopyContext.class);
    CreateResponse<Object> createResponse = new CreateResponse<>("Clone", true);

    when(context.createOrRetrieveCopyInstance(Mockito.<Object>any())).thenReturn(createResponse);

    // Act
    CreateResponse<Address> actualCreateOrRetrieveCopyInstanceResult = addressImpl2
        .createOrRetrieveCopyInstance(context);

    // Assert
    verify(context).createOrRetrieveCopyInstance(isA(Object.class));
    assertSame(createResponse, actualCreateOrRetrieveCopyInstanceResult);
  }

  /**
   * Test {@link AddressImpl#createOrRetrieveCopyInstance(MultiTenantCopyContext)}.
   * <ul>
   *   <li>Given {@code Object}.</li>
   *   <li>Then Clone return {@link AddressImpl}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressImpl#createOrRetrieveCopyInstance(MultiTenantCopyContext)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CreateResponse AddressImpl.createOrRetrieveCopyInstance(MultiTenantCopyContext)"})
  public void testCreateOrRetrieveCopyInstance_givenJavaLangObject_thenCloneReturnAddressImpl()
      throws CloneNotSupportedException {
    // Arrange
    AddressImpl addressImpl2 = new AddressImpl();
    GenericEntityService genericEntityService = mock(GenericEntityService.class);
    when(genericEntityService.getIdentifier(Mockito.<Object>any())).thenReturn(null);
    Class<Object> forNameResult = Object.class;
    Mockito.<Class<?>>when(genericEntityService.getCeilingImplClass(Mockito.<String>any())).thenReturn(forNameResult);
    CatalogImpl fromCatalog = new CatalogImpl();
    CatalogImpl toCatalog = new CatalogImpl();
    SiteImpl fromSite = new SiteImpl();
    SiteImpl toSite = new SiteImpl();

    // Act
    CreateResponse<Address> actualCreateOrRetrieveCopyInstanceResult = addressImpl2
        .createOrRetrieveCopyInstance(new MultiTenantCopyContext(fromCatalog, toCatalog, fromSite, toSite,
            genericEntityService, new MultiTenantCopierExtensionManager()));

    // Assert
    verify(genericEntityService).getCeilingImplClass(eq("org.broadleafcommerce.profile.core.domain.AddressImpl"));
    verify(genericEntityService).getIdentifier(isA(Object.class));
    Address clone = actualCreateOrRetrieveCopyInstanceResult.getClone();
    assertTrue(clone instanceof AddressImpl);
    assertFalse(actualCreateOrRetrieveCopyInstanceResult.isAlreadyPopulated());
    assertEquals(addressImpl2, clone);
  }

  /**
   * Test new {@link AddressImpl} (default constructor).
   * <p>
   * Method under test: default or parameterless constructor of {@link AddressImpl}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void AddressImpl.<init>()"})
  public void testNewAddressImpl() {
    // Arrange and Act
    AddressImpl actualAddressImpl = new AddressImpl();

    // Assert
    assertNull(actualAddressImpl.getId());
    assertNull(actualAddressImpl.getAddressLine1());
    assertNull(actualAddressImpl.getAddressLine2());
    assertNull(actualAddressImpl.getAddressLine3());
    assertNull(actualAddressImpl.getCity());
    assertNull(actualAddressImpl.getCompanyName());
    assertNull(actualAddressImpl.getCounty());
    assertNull(actualAddressImpl.getEmailAddress());
    assertNull(actualAddressImpl.getFax());
    assertNull(actualAddressImpl.getFirstName());
    assertNull(actualAddressImpl.getFullName());
    assertNull(actualAddressImpl.getIsoCountrySubdivision());
    assertNull(actualAddressImpl.getLastName());
    assertNull(actualAddressImpl.getPostalCode());
    assertNull(actualAddressImpl.getPrimaryPhone());
    assertNull(actualAddressImpl.getSecondaryPhone());
    assertNull(actualAddressImpl.getStateProvinceRegion());
    assertNull(actualAddressImpl.getTokenizedAddress());
    assertNull(actualAddressImpl.getVerificationLevel());
    assertNull(actualAddressImpl.getZipFour());
    assertNull(actualAddressImpl.getIsoCountryAlpha2());
    assertNull(actualAddressImpl.getCountry());
    assertNull(actualAddressImpl.getPhoneFax());
    assertNull(actualAddressImpl.getPhonePrimary());
    assertNull(actualAddressImpl.getPhoneSecondary());
    assertNull(actualAddressImpl.phoneFax);
    assertNull(actualAddressImpl.phonePrimary);
    assertNull(actualAddressImpl.phoneSecondary);
    assertNull(actualAddressImpl.getState());
    assertFalse(actualAddressImpl.getStandardized());
    assertFalse(actualAddressImpl.isBusiness());
    assertFalse(actualAddressImpl.isDefault());
    assertFalse(actualAddressImpl.isMailing());
    assertFalse(actualAddressImpl.isStreet());
    assertTrue(actualAddressImpl.isActive());
  }
}
