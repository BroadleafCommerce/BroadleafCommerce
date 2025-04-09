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
package org.broadleafcommerce.profile.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.util.ArrayList;
import java.util.List;
import org.broadleafcommerce.common.config.domain.AbstractModuleConfiguration;
import org.broadleafcommerce.common.config.domain.ModuleConfiguration;
import org.broadleafcommerce.common.config.service.ModuleConfigurationService;
import org.broadleafcommerce.common.config.service.type.ModuleConfigurationType;
import org.broadleafcommerce.common.i18n.domain.ISOCountryImpl;
import org.broadleafcommerce.common.sitemap.domain.SiteMapConfigurationImpl;
import org.broadleafcommerce.profile.core.dao.AddressDao;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.CountryImpl;
import org.broadleafcommerce.profile.core.domain.CountrySubdivisionImpl;
import org.broadleafcommerce.profile.core.domain.Phone;
import org.broadleafcommerce.profile.core.domain.PhoneImpl;
import org.broadleafcommerce.profile.core.domain.StateImpl;
import org.broadleafcommerce.profile.core.service.exception.AddressVerificationException;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@RunWith(MockitoJUnitRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class AddressServiceImplDiffblueTest {
  @Mock
  private AddressDao addressDao;

  @InjectMocks
  private AddressServiceImpl addressServiceImpl;

  @Mock
  private CountrySubdivisionService countrySubdivisionService;

  @Mock
  private List<AddressVerificationProvider> list;

  @Mock
  private ModuleConfigurationService moduleConfigurationService;

  @Mock
  private PhoneService phoneService;

  /**
   * Test {@link AddressServiceImpl#saveAddress(Address)}.
   * <p>
   * Method under test: {@link AddressServiceImpl#saveAddress(Address)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Address AddressServiceImpl.saveAddress(Address)"})
  public void testSaveAddress() {
    // Arrange
    AddressImpl addressImpl = new AddressImpl();
    when(addressDao.save(Mockito.<Address>any())).thenReturn(addressImpl);

    // Act
    Address actualSaveAddressResult = addressServiceImpl.saveAddress(new AddressImpl());

    // Assert
    verify(addressDao).save(isA(Address.class));
    assertSame(addressImpl, actualSaveAddressResult);
  }

  /**
   * Test {@link AddressServiceImpl#readAddressById(Long)}.
   * <p>
   * Method under test: {@link AddressServiceImpl#readAddressById(Long)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Address AddressServiceImpl.readAddressById(Long)"})
  public void testReadAddressById() {
    // Arrange
    AddressImpl addressImpl = new AddressImpl();
    when(addressDao.readAddressById(Mockito.<Long>any())).thenReturn(addressImpl);

    // Act
    Address actualReadAddressByIdResult = addressServiceImpl.readAddressById(1L);

    // Assert
    verify(addressDao).readAddressById(eq(1L));
    assertSame(addressImpl, actualReadAddressByIdResult);
  }

  /**
   * Test {@link AddressServiceImpl#create()}.
   * <p>
   * Method under test: {@link AddressServiceImpl#create()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Address AddressServiceImpl.create()"})
  public void testCreate() {
    // Arrange
    AddressImpl addressImpl = new AddressImpl();
    when(addressDao.create()).thenReturn(addressImpl);

    // Act
    Address actualCreateResult = addressServiceImpl.create();

    // Assert
    verify(addressDao).create();
    assertSame(addressImpl, actualCreateResult);
  }

  /**
   * Test {@link AddressServiceImpl#delete(Address)}.
   * <p>
   * Method under test: {@link AddressServiceImpl#delete(Address)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void AddressServiceImpl.delete(Address)"})
  public void testDelete() {
    // Arrange
    doNothing().when(addressDao).delete(Mockito.<Address>any());

    // Act
    addressServiceImpl.delete(new AddressImpl());

    // Assert
    verify(addressDao).delete(isA(Address.class));
  }

  /**
   * Test {@link AddressServiceImpl#verifyAddress(Address)}.
   * <p>
   * Method under test: {@link AddressServiceImpl#verifyAddress(Address)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"List AddressServiceImpl.verifyAddress(Address)"})
  public void testVerifyAddress() throws AddressVerificationException {
    // Arrange
    SiteMapConfigurationImpl siteMapConfigurationImpl = mock(SiteMapConfigurationImpl.class);
    when(siteMapConfigurationImpl.getIsDefault()).thenReturn(true);

    ArrayList<ModuleConfiguration> moduleConfigurationList = new ArrayList<>();
    moduleConfigurationList.add(siteMapConfigurationImpl);
    when(moduleConfigurationService.findActiveConfigurationsByType(Mockito.<ModuleConfigurationType>any()))
        .thenReturn(moduleConfigurationList);
    AddressVerificationProvider addressVerificationProvider = mock(AddressVerificationProvider.class);
    when(addressVerificationProvider.validateAddress(Mockito.<Address>any(), Mockito.<ModuleConfiguration>any()))
        .thenThrow(new AddressVerificationException("Arg0"));
    when(addressVerificationProvider.canRespond(Mockito.<ModuleConfiguration>any())).thenReturn(true);

    ArrayList<AddressVerificationProvider> addressVerificationProviderList = new ArrayList<>();
    addressVerificationProviderList.add(addressVerificationProvider);
    when(list.iterator()).thenReturn(addressVerificationProviderList.iterator());
    when(list.isEmpty()).thenReturn(false);
    AddressImpl address = mock(AddressImpl.class);
    when(address.getStandardized()).thenReturn(null);

    // Act and Assert
    assertThrows(AddressVerificationException.class, () -> addressServiceImpl.verifyAddress(address));
    verify(list).isEmpty();
    verify(list).iterator();
    verify(siteMapConfigurationImpl).getIsDefault();
    verify(moduleConfigurationService).findActiveConfigurationsByType(isA(ModuleConfigurationType.class));
    verify(addressVerificationProvider).canRespond(isA(ModuleConfiguration.class));
    verify(address).getStandardized();
    verify(addressVerificationProvider).validateAddress(isA(Address.class), isA(ModuleConfiguration.class));
  }

  /**
   * Test {@link AddressServiceImpl#verifyAddress(Address)}.
   * <ul>
   *   <li>Given {@link AddressServiceImpl} (default constructor) MustValidateAddresses is {@code true}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressServiceImpl#verifyAddress(Address)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"List AddressServiceImpl.verifyAddress(Address)"})
  public void testVerifyAddress_givenAddressServiceImplMustValidateAddressesIsTrue()
      throws AddressVerificationException {
    // Arrange
    AddressServiceImpl addressServiceImpl = new AddressServiceImpl();
    addressServiceImpl.setMustValidateAddresses(true);
    AddressImpl address = mock(AddressImpl.class);
    when(address.getStandardized()).thenReturn(null);

    // Act and Assert
    assertThrows(AddressVerificationException.class, () -> addressServiceImpl.verifyAddress(address));
    verify(address).getStandardized();
  }

  /**
   * Test {@link AddressServiceImpl#verifyAddress(Address)}.
   * <ul>
   *   <li>Given {@link AddressServiceImpl} (default constructor).</li>
   *   <li>Then first return {@link AddressImpl}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressServiceImpl#verifyAddress(Address)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"List AddressServiceImpl.verifyAddress(Address)"})
  public void testVerifyAddress_givenAddressServiceImpl_thenFirstReturnAddressImpl()
      throws AddressVerificationException {
    // Arrange
    AddressServiceImpl addressServiceImpl = new AddressServiceImpl();
    AddressImpl address = new AddressImpl();

    // Act
    List<Address> actualVerifyAddressResult = addressServiceImpl.verifyAddress(address);

    // Assert
    assertEquals(1, actualVerifyAddressResult.size());
    Address getResult = actualVerifyAddressResult.get(0);
    assertTrue(getResult instanceof AddressImpl);
    assertSame(address, getResult);
  }

  /**
   * Test {@link AddressServiceImpl#verifyAddress(Address)}.
   * <ul>
   *   <li>Given {@link ArrayList#ArrayList()} add {@link SiteMapConfigurationImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressServiceImpl#verifyAddress(Address)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"List AddressServiceImpl.verifyAddress(Address)"})
  public void testVerifyAddress_givenArrayListAddSiteMapConfigurationImpl() throws AddressVerificationException {
    // Arrange
    ArrayList<ModuleConfiguration> moduleConfigurationList = new ArrayList<>();
    moduleConfigurationList.add(new SiteMapConfigurationImpl());
    when(moduleConfigurationService.findActiveConfigurationsByType(Mockito.<ModuleConfigurationType>any()))
        .thenReturn(moduleConfigurationList);

    ArrayList<AddressVerificationProvider> addressVerificationProviderList = new ArrayList<>();
    when(list.iterator()).thenReturn(addressVerificationProviderList.iterator());
    when(list.isEmpty()).thenReturn(false);
    AddressImpl address = mock(AddressImpl.class);
    when(address.getStandardized()).thenReturn(null);

    // Act
    List<Address> actualVerifyAddressResult = addressServiceImpl.verifyAddress(address);

    // Assert
    verify(list).isEmpty();
    verify(list).iterator();
    verify(moduleConfigurationService).findActiveConfigurationsByType(isA(ModuleConfigurationType.class));
    verify(address).getStandardized();
    assertEquals(1, actualVerifyAddressResult.size());
    assertSame(address, actualVerifyAddressResult.get(0));
  }

  /**
   * Test {@link AddressServiceImpl#verifyAddress(Address)}.
   * <ul>
   *   <li>Given {@link List}.</li>
   *   <li>When {@link AddressImpl} {@link AddressImpl#getStandardized()} return {@code true}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressServiceImpl#verifyAddress(Address)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"List AddressServiceImpl.verifyAddress(Address)"})
  public void testVerifyAddress_givenList_whenAddressImplGetStandardizedReturnTrue()
      throws AddressVerificationException {
    // Arrange
    AddressImpl address = mock(AddressImpl.class);
    when(address.getStandardized()).thenReturn(true);

    // Act
    List<Address> actualVerifyAddressResult = addressServiceImpl.verifyAddress(address);

    // Assert
    verify(address, atLeast(1)).getStandardized();
    assertEquals(1, actualVerifyAddressResult.size());
    assertSame(address, actualVerifyAddressResult.get(0));
  }

  /**
   * Test {@link AddressServiceImpl#verifyAddress(Address)}.
   * <ul>
   *   <li>Given {@link ModuleConfigurationService}.</li>
   *   <li>Then first return {@link AddressImpl}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressServiceImpl#verifyAddress(Address)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"List AddressServiceImpl.verifyAddress(Address)"})
  public void testVerifyAddress_givenModuleConfigurationService_thenFirstReturnAddressImpl()
      throws AddressVerificationException {
    // Arrange
    when(list.isEmpty()).thenReturn(true);
    AddressImpl address = new AddressImpl();

    // Act
    List<Address> actualVerifyAddressResult = addressServiceImpl.verifyAddress(address);

    // Assert
    verify(list).isEmpty();
    assertEquals(1, actualVerifyAddressResult.size());
    Address getResult = actualVerifyAddressResult.get(0);
    assertTrue(getResult instanceof AddressImpl);
    assertSame(address, getResult);
  }

  /**
   * Test {@link AddressServiceImpl#verifyAddress(Address)}.
   * <ul>
   *   <li>Given {@link ModuleConfigurationService}.</li>
   *   <li>Then return first is {@link AddressImpl}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressServiceImpl#verifyAddress(Address)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"List AddressServiceImpl.verifyAddress(Address)"})
  public void testVerifyAddress_givenModuleConfigurationService_thenReturnFirstIsAddressImpl()
      throws AddressVerificationException {
    // Arrange
    when(list.isEmpty()).thenReturn(true);
    AddressImpl address = mock(AddressImpl.class);
    when(address.getStandardized()).thenReturn(null);

    // Act
    List<Address> actualVerifyAddressResult = addressServiceImpl.verifyAddress(address);

    // Assert
    verify(list).isEmpty();
    verify(address).getStandardized();
    assertEquals(1, actualVerifyAddressResult.size());
    assertSame(address, actualVerifyAddressResult.get(0));
  }

  /**
   * Test {@link AddressServiceImpl#verifyAddress(Address)}.
   * <ul>
   *   <li>Then calls {@link AbstractModuleConfiguration#getIsDefault()}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressServiceImpl#verifyAddress(Address)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"List AddressServiceImpl.verifyAddress(Address)"})
  public void testVerifyAddress_thenCallsGetIsDefault() throws AddressVerificationException {
    // Arrange
    SiteMapConfigurationImpl siteMapConfigurationImpl = mock(SiteMapConfigurationImpl.class);
    when(siteMapConfigurationImpl.getIsDefault()).thenReturn(true);

    ArrayList<ModuleConfiguration> moduleConfigurationList = new ArrayList<>();
    moduleConfigurationList.add(siteMapConfigurationImpl);
    when(moduleConfigurationService.findActiveConfigurationsByType(Mockito.<ModuleConfigurationType>any()))
        .thenReturn(moduleConfigurationList);

    ArrayList<AddressVerificationProvider> addressVerificationProviderList = new ArrayList<>();
    when(list.iterator()).thenReturn(addressVerificationProviderList.iterator());
    when(list.isEmpty()).thenReturn(false);
    AddressImpl address = mock(AddressImpl.class);
    when(address.getStandardized()).thenReturn(null);

    // Act
    List<Address> actualVerifyAddressResult = addressServiceImpl.verifyAddress(address);

    // Assert
    verify(list).isEmpty();
    verify(list).iterator();
    verify(siteMapConfigurationImpl).getIsDefault();
    verify(moduleConfigurationService).findActiveConfigurationsByType(isA(ModuleConfigurationType.class));
    verify(address).getStandardized();
    assertEquals(1, actualVerifyAddressResult.size());
    assertSame(address, actualVerifyAddressResult.get(0));
  }

  /**
   * Test {@link AddressServiceImpl#verifyAddress(Address)}.
   * <ul>
   *   <li>Then return Empty.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressServiceImpl#verifyAddress(Address)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"List AddressServiceImpl.verifyAddress(Address)"})
  public void testVerifyAddress_thenReturnEmpty() throws AddressVerificationException {
    // Arrange
    SiteMapConfigurationImpl siteMapConfigurationImpl = mock(SiteMapConfigurationImpl.class);
    when(siteMapConfigurationImpl.getIsDefault()).thenReturn(true);

    ArrayList<ModuleConfiguration> moduleConfigurationList = new ArrayList<>();
    moduleConfigurationList.add(siteMapConfigurationImpl);
    when(moduleConfigurationService.findActiveConfigurationsByType(Mockito.<ModuleConfigurationType>any()))
        .thenReturn(moduleConfigurationList);
    AddressVerificationProvider addressVerificationProvider = mock(AddressVerificationProvider.class);
    when(addressVerificationProvider.validateAddress(Mockito.<Address>any(), Mockito.<ModuleConfiguration>any()))
        .thenReturn(new ArrayList<>());
    when(addressVerificationProvider.canRespond(Mockito.<ModuleConfiguration>any())).thenReturn(true);

    ArrayList<AddressVerificationProvider> addressVerificationProviderList = new ArrayList<>();
    addressVerificationProviderList.add(addressVerificationProvider);
    when(list.iterator()).thenReturn(addressVerificationProviderList.iterator());
    when(list.isEmpty()).thenReturn(false);
    AddressImpl address = mock(AddressImpl.class);
    when(address.getStandardized()).thenReturn(null);

    // Act
    List<Address> actualVerifyAddressResult = addressServiceImpl.verifyAddress(address);

    // Assert
    verify(list).isEmpty();
    verify(list).iterator();
    verify(siteMapConfigurationImpl).getIsDefault();
    verify(moduleConfigurationService).findActiveConfigurationsByType(isA(ModuleConfigurationType.class));
    verify(addressVerificationProvider).canRespond(isA(ModuleConfiguration.class));
    verify(address).getStandardized();
    verify(addressVerificationProvider).validateAddress(isA(Address.class), isA(ModuleConfiguration.class));
    assertTrue(actualVerifyAddressResult.isEmpty());
  }

  /**
   * Test {@link AddressServiceImpl#verifyAddress(Address)}.
   * <ul>
   *   <li>When {@link AddressImpl} (default constructor).</li>
   *   <li>Then first return {@link AddressImpl}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressServiceImpl#verifyAddress(Address)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"List AddressServiceImpl.verifyAddress(Address)"})
  public void testVerifyAddress_whenAddressImpl_thenFirstReturnAddressImpl() throws AddressVerificationException {
    // Arrange
    when(moduleConfigurationService.findActiveConfigurationsByType(Mockito.<ModuleConfigurationType>any()))
        .thenReturn(new ArrayList<>());
    when(list.isEmpty()).thenReturn(false);
    AddressImpl address = new AddressImpl();

    // Act
    List<Address> actualVerifyAddressResult = addressServiceImpl.verifyAddress(address);

    // Assert
    verify(list).isEmpty();
    verify(moduleConfigurationService).findActiveConfigurationsByType(isA(ModuleConfigurationType.class));
    assertEquals(1, actualVerifyAddressResult.size());
    Address getResult = actualVerifyAddressResult.get(0);
    assertTrue(getResult instanceof AddressImpl);
    assertSame(address, getResult);
  }

  /**
   * Test {@link AddressServiceImpl#copyAddress(Address, Address)} with {@code dest}, {@code orig}.
   * <ul>
   *   <li>Given {@link AddressDao}.</li>
   *   <li>Then return {@link AddressImpl}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressServiceImpl#copyAddress(Address, Address)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Address AddressServiceImpl.copyAddress(Address, Address)"})
  public void testCopyAddressWithDestOrig_givenAddressDao_thenReturnAddressImpl() {
    // Arrange
    PhoneImpl phoneImpl = new PhoneImpl();
    when(phoneService.copyPhone(Mockito.<Phone>any(), Mockito.<Phone>any())).thenReturn(phoneImpl);
    AddressImpl dest = new AddressImpl();

    // Act
    Address actualCopyAddressResult = addressServiceImpl.copyAddress(dest, new AddressImpl());

    // Assert
    verify(phoneService, atLeast(1)).copyPhone(isNull(), isNull());
    assertTrue(actualCopyAddressResult instanceof AddressImpl);
    assertSame(phoneImpl, dest.getPhoneFax());
    assertSame(phoneImpl, dest.getPhonePrimary());
    assertSame(phoneImpl, dest.getPhoneSecondary());
  }

  /**
   * Test {@link AddressServiceImpl#copyAddress(Address, Address)} with {@code dest}, {@code orig}.
   * <ul>
   *   <li>Given {@link PhoneService}.</li>
   *   <li>When {@code null}.</li>
   *   <li>Then return {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressServiceImpl#copyAddress(Address, Address)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Address AddressServiceImpl.copyAddress(Address, Address)"})
  public void testCopyAddressWithDestOrig_givenPhoneService_whenNull_thenReturnNull() {
    // Arrange
    AddressImpl dest = new AddressImpl();

    // Act and Assert
    assertNull(addressServiceImpl.copyAddress(dest, null));
    assertNull(dest.getPhoneFax());
    assertNull(dest.getPhonePrimary());
    assertNull(dest.getPhoneSecondary());
  }

  /**
   * Test {@link AddressServiceImpl#copyAddress(Address, Address)} with {@code dest}, {@code orig}.
   * <ul>
   *   <li>Then return {@link AddressImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressServiceImpl#copyAddress(Address, Address)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Address AddressServiceImpl.copyAddress(Address, Address)"})
  public void testCopyAddressWithDestOrig_thenReturnAddressImpl() {
    // Arrange
    AddressImpl addressImpl = new AddressImpl();
    when(addressDao.create()).thenReturn(addressImpl);
    when(phoneService.copyPhone(Mockito.<Phone>any(), Mockito.<Phone>any())).thenReturn(new PhoneImpl());

    // Act
    Address actualCopyAddressResult = addressServiceImpl.copyAddress(null, new AddressImpl());

    // Assert
    verify(addressDao).create();
    verify(phoneService, atLeast(1)).copyPhone(isNull(), isNull());
    assertSame(addressImpl, actualCopyAddressResult);
  }

  /**
   * Test {@link AddressServiceImpl#copyAddress(Address)} with {@code orig}.
   * <ul>
   *   <li>Given {@link PhoneService}.</li>
   *   <li>When {@code null}.</li>
   *   <li>Then return {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressServiceImpl#copyAddress(Address)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Address AddressServiceImpl.copyAddress(Address)"})
  public void testCopyAddressWithOrig_givenPhoneService_whenNull_thenReturnNull() {
    // Arrange
    when(addressDao.create()).thenReturn(new AddressImpl());

    // Act
    Address actualCopyAddressResult = addressServiceImpl.copyAddress(null);

    // Assert
    verify(addressDao).create();
    assertNull(actualCopyAddressResult);
  }

  /**
   * Test {@link AddressServiceImpl#copyAddress(Address)} with {@code orig}.
   * <ul>
   *   <li>Then return {@link AddressImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressServiceImpl#copyAddress(Address)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Address AddressServiceImpl.copyAddress(Address)"})
  public void testCopyAddressWithOrig_thenReturnAddressImpl() {
    // Arrange
    AddressImpl addressImpl = new AddressImpl();
    when(addressDao.create()).thenReturn(addressImpl);
    when(phoneService.copyPhone(Mockito.<Phone>any(), Mockito.<Phone>any())).thenReturn(new PhoneImpl());

    // Act
    Address actualCopyAddressResult = addressServiceImpl.copyAddress(new AddressImpl());

    // Assert
    verify(addressDao).create();
    verify(phoneService, atLeast(1)).copyPhone(isNull(), isNull());
    assertSame(addressImpl, actualCopyAddressResult);
  }

  /**
   * Test {@link AddressServiceImpl#populateAddressISOCountrySub(Address)}.
   * <p>
   * Method under test: {@link AddressServiceImpl#populateAddressISOCountrySub(Address)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void AddressServiceImpl.populateAddressISOCountrySub(Address)"})
  public void testPopulateAddressISOCountrySub() {
    // Arrange
    when(countrySubdivisionService.findSubdivisionByCountryAndAltAbbreviation(Mockito.<String>any(),
        Mockito.<String>any())).thenReturn(new CountrySubdivisionImpl());

    AddressImpl address = new AddressImpl();
    address.setActive(true);
    address.setAddressLine1("42 Main St");
    address.setAddressLine2("42 Main St");
    address.setAddressLine3("42 Main St");
    address.setBusiness(true);
    address.setCity("Oxford");
    address.setCompanyName("Company Name");
    address.setCountry(new CountryImpl());
    address.setCounty("3");
    address.setDefault(true);
    address.setEmailAddress("42 Main St");
    address.setFax("Fax");
    address.setFirstName("Jane");
    address.setFullName("Dr Jane Doe");
    address.setId(1L);
    address.setLastName("Doe");
    address.setMailing(true);
    address.setPhoneFax(new PhoneImpl());
    address.setPhonePrimary(new PhoneImpl());
    address.setPhoneSecondary(new PhoneImpl());
    address.setPostalCode("Postal Code");
    address.setPrimaryPhone("6625550144");
    address.setSecondaryPhone("6625550144");
    address.setStandardized(true);
    address.setState(new StateImpl());
    address.setStreet(true);
    address.setTokenizedAddress("42 Main St");
    address.setVerificationLevel("Verification Level");
    address.setZipFour("21654");
    address.setIsoCountrySubdivision(" ");
    address.setIsoCountryAlpha2(new ISOCountryImpl());
    address.setStateProvinceRegion("not blank");

    // Act
    addressServiceImpl.populateAddressISOCountrySub(address);

    // Assert
    verify(countrySubdivisionService).findSubdivisionByCountryAndAltAbbreviation(isNull(), eq("not blank"));
    assertNull(address.getIsoCountrySubdivision());
  }

  /**
   * Test {@link AddressServiceImpl#populateAddressISOCountrySub(Address)}.
   * <ul>
   *   <li>Then {@link AddressImpl} (default constructor) IsoCountrySubdivision is {@code 42 Main St}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressServiceImpl#populateAddressISOCountrySub(Address)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void AddressServiceImpl.populateAddressISOCountrySub(Address)"})
  public void testPopulateAddressISOCountrySub_thenAddressImplIsoCountrySubdivisionIs42MainSt() {
    // Arrange
    AddressImpl address = new AddressImpl();
    address.setActive(true);
    address.setAddressLine1("42 Main St");
    address.setAddressLine2("42 Main St");
    address.setAddressLine3("42 Main St");
    address.setBusiness(true);
    address.setCity("Oxford");
    address.setCompanyName("Company Name");
    address.setCountry(new CountryImpl());
    address.setCounty("3");
    address.setDefault(true);
    address.setEmailAddress("42 Main St");
    address.setFax("Fax");
    address.setFirstName("Jane");
    address.setFullName("Dr Jane Doe");
    address.setId(1L);
    address.setLastName("Doe");
    address.setMailing(true);
    address.setPhoneFax(new PhoneImpl());
    address.setPhonePrimary(new PhoneImpl());
    address.setPhoneSecondary(new PhoneImpl());
    address.setPostalCode("Postal Code");
    address.setPrimaryPhone("6625550144");
    address.setSecondaryPhone("6625550144");
    address.setStandardized(true);
    address.setState(new StateImpl());
    address.setStreet(true);
    address.setTokenizedAddress("42 Main St");
    address.setVerificationLevel("Verification Level");
    address.setZipFour("21654");
    address.setIsoCountrySubdivision("42 Main St");
    address.setIsoCountryAlpha2(null);
    address.setStateProvinceRegion("not blank");

    // Act
    addressServiceImpl.populateAddressISOCountrySub(address);

    // Assert that nothing has changed
    assertEquals("42 Main St", address.getIsoCountrySubdivision());
  }

  /**
   * Test {@link AddressServiceImpl#populateAddressISOCountrySub(Address)}.
   * <ul>
   *   <li>Then {@link AddressImpl} (default constructor) IsoCountrySubdivision is space.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressServiceImpl#populateAddressISOCountrySub(Address)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void AddressServiceImpl.populateAddressISOCountrySub(Address)"})
  public void testPopulateAddressISOCountrySub_thenAddressImplIsoCountrySubdivisionIsSpace() {
    // Arrange
    AddressImpl address = new AddressImpl();
    address.setActive(true);
    address.setAddressLine1("42 Main St");
    address.setAddressLine2("42 Main St");
    address.setAddressLine3("42 Main St");
    address.setBusiness(true);
    address.setCity("Oxford");
    address.setCompanyName("Company Name");
    address.setCountry(new CountryImpl());
    address.setCounty("3");
    address.setDefault(true);
    address.setEmailAddress("42 Main St");
    address.setFax("Fax");
    address.setFirstName("Jane");
    address.setFullName("Dr Jane Doe");
    address.setId(1L);
    address.setLastName("Doe");
    address.setMailing(true);
    address.setPhoneFax(new PhoneImpl());
    address.setPhonePrimary(new PhoneImpl());
    address.setPhoneSecondary(new PhoneImpl());
    address.setPostalCode("Postal Code");
    address.setPrimaryPhone("6625550144");
    address.setSecondaryPhone("6625550144");
    address.setStandardized(true);
    address.setState(new StateImpl());
    address.setStreet(true);
    address.setTokenizedAddress("42 Main St");
    address.setVerificationLevel("Verification Level");
    address.setZipFour("21654");
    address.setIsoCountrySubdivision(" ");
    address.setIsoCountryAlpha2(null);
    address.setStateProvinceRegion("not blank");

    // Act
    addressServiceImpl.populateAddressISOCountrySub(address);

    // Assert that nothing has changed
    assertEquals(" ", address.getIsoCountrySubdivision());
  }

  /**
   * Test {@link AddressServiceImpl#populateAddressISOCountrySub(Address)}.
   * <ul>
   *   <li>Then calls {@link CountrySubdivisionService#findSubdivisionByCountryAndName(String, String)}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressServiceImpl#populateAddressISOCountrySub(Address)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void AddressServiceImpl.populateAddressISOCountrySub(Address)"})
  public void testPopulateAddressISOCountrySub_thenCallsFindSubdivisionByCountryAndName() {
    // Arrange
    when(countrySubdivisionService.findSubdivisionByCountryAndAltAbbreviation(Mockito.<String>any(),
        Mockito.<String>any())).thenReturn(null);
    when(countrySubdivisionService.findSubdivisionByCountryAndName(Mockito.<String>any(), Mockito.<String>any()))
        .thenReturn(new CountrySubdivisionImpl());

    AddressImpl address = new AddressImpl();
    address.setActive(true);
    address.setAddressLine1("42 Main St");
    address.setAddressLine2("42 Main St");
    address.setAddressLine3("42 Main St");
    address.setBusiness(true);
    address.setCity("Oxford");
    address.setCompanyName("Company Name");
    address.setCountry(new CountryImpl());
    address.setCounty("3");
    address.setDefault(true);
    address.setEmailAddress("42 Main St");
    address.setFax("Fax");
    address.setFirstName("Jane");
    address.setFullName("Dr Jane Doe");
    address.setId(1L);
    address.setLastName("Doe");
    address.setMailing(true);
    address.setPhoneFax(new PhoneImpl());
    address.setPhonePrimary(new PhoneImpl());
    address.setPhoneSecondary(new PhoneImpl());
    address.setPostalCode("Postal Code");
    address.setPrimaryPhone("6625550144");
    address.setSecondaryPhone("6625550144");
    address.setStandardized(true);
    address.setState(new StateImpl());
    address.setStreet(true);
    address.setTokenizedAddress("42 Main St");
    address.setVerificationLevel("Verification Level");
    address.setZipFour("21654");
    address.setIsoCountrySubdivision(" ");
    address.setIsoCountryAlpha2(new ISOCountryImpl());
    address.setStateProvinceRegion("not blank");

    // Act
    addressServiceImpl.populateAddressISOCountrySub(address);

    // Assert
    verify(countrySubdivisionService).findSubdivisionByCountryAndAltAbbreviation(isNull(), eq("not blank"));
    verify(countrySubdivisionService).findSubdivisionByCountryAndName(isNull(), eq("not blank"));
    assertNull(address.getIsoCountrySubdivision());
  }

  /**
   * Test {@link AddressServiceImpl#populateAddressISOCountrySub(Address)}.
   * <ul>
   *   <li>When {@link AddressImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressServiceImpl#populateAddressISOCountrySub(Address)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void AddressServiceImpl.populateAddressISOCountrySub(Address)"})
  public void testPopulateAddressISOCountrySub_whenAddressImpl() {
    // Arrange
    AddressImpl address = new AddressImpl();

    // Act
    addressServiceImpl.populateAddressISOCountrySub(address);

    // Assert that nothing has changed
    assertNull(address.getIsoCountrySubdivision());
  }

  /**
   * Test {@link AddressServiceImpl#populateAddressISOCountrySub(Address)}.
   * <ul>
   *   <li>When {@link AddressImpl} (default constructor) StateProvinceRegion is {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressServiceImpl#populateAddressISOCountrySub(Address)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void AddressServiceImpl.populateAddressISOCountrySub(Address)"})
  public void testPopulateAddressISOCountrySub_whenAddressImplStateProvinceRegionIsNull() {
    // Arrange
    AddressImpl address = new AddressImpl();
    address.setActive(true);
    address.setAddressLine1("42 Main St");
    address.setAddressLine2("42 Main St");
    address.setAddressLine3("42 Main St");
    address.setBusiness(true);
    address.setCity("Oxford");
    address.setCompanyName("Company Name");
    address.setCountry(new CountryImpl());
    address.setCounty("3");
    address.setDefault(true);
    address.setEmailAddress("42 Main St");
    address.setFax("Fax");
    address.setFirstName("Jane");
    address.setFullName("Dr Jane Doe");
    address.setId(1L);
    address.setLastName("Doe");
    address.setMailing(true);
    address.setPhoneFax(new PhoneImpl());
    address.setPhonePrimary(new PhoneImpl());
    address.setPhoneSecondary(new PhoneImpl());
    address.setPostalCode("Postal Code");
    address.setPrimaryPhone("6625550144");
    address.setSecondaryPhone("6625550144");
    address.setStandardized(true);
    address.setState(new StateImpl());
    address.setStreet(true);
    address.setTokenizedAddress("42 Main St");
    address.setVerificationLevel("Verification Level");
    address.setZipFour("21654");
    address.setIsoCountrySubdivision(" ");
    address.setIsoCountryAlpha2(new ISOCountryImpl());
    address.setStateProvinceRegion(null);

    // Act
    addressServiceImpl.populateAddressISOCountrySub(address);

    // Assert that nothing has changed
    assertEquals(" ", address.getIsoCountrySubdivision());
  }
}
