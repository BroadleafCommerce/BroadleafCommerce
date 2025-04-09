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

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.util.ArrayList;
import java.util.List;
import org.broadleafcommerce.profile.core.dao.CustomerPhoneDao;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.broadleafcommerce.profile.core.domain.CustomerPhone;
import org.broadleafcommerce.profile.core.domain.CustomerPhoneImpl;
import org.broadleafcommerce.profile.core.domain.PhoneImpl;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CustomerPhoneServiceImplDiffblueTest {
  @Mock
  private CustomerPhoneDao customerPhoneDao;

  @InjectMocks
  private CustomerPhoneServiceImpl customerPhoneServiceImpl;

  /**
   * Test {@link CustomerPhoneServiceImpl#saveCustomerPhone(CustomerPhone)}.
   * <ul>
   *   <li>Given {@link CustomerPhoneImpl} {@link CustomerPhoneImpl#getId()} return one.</li>
   *   <li>Then calls {@link CustomerPhoneImpl#getId()}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerPhoneServiceImpl#saveCustomerPhone(CustomerPhone)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerPhone CustomerPhoneServiceImpl.saveCustomerPhone(CustomerPhone)"})
  public void testSaveCustomerPhone_givenCustomerPhoneImplGetIdReturnOne_thenCallsGetId() {
    // Arrange
    CustomerPhoneImpl customerPhoneImpl = mock(CustomerPhoneImpl.class);
    when(customerPhoneImpl.getId()).thenReturn(1L);

    ArrayList<CustomerPhone> customerPhoneList = new ArrayList<>();
    customerPhoneList.add(customerPhoneImpl);
    CustomerPhoneImpl customerPhoneImpl2 = new CustomerPhoneImpl();
    when(customerPhoneDao.save(Mockito.<CustomerPhone>any())).thenReturn(customerPhoneImpl2);
    when(customerPhoneDao.readActiveCustomerPhonesByCustomerId(Mockito.<Long>any())).thenReturn(customerPhoneList);
    PhoneImpl phoneImpl = mock(PhoneImpl.class);
    when(phoneImpl.isDefault()).thenReturn(true);
    CustomerPhoneImpl customerPhone = mock(CustomerPhoneImpl.class);
    when(customerPhone.getId()).thenReturn(1L);
    when(customerPhone.getPhone()).thenReturn(phoneImpl);
    when(customerPhone.getCustomer()).thenReturn(new CustomerImpl());

    // Act
    CustomerPhone actualSaveCustomerPhoneResult = customerPhoneServiceImpl.saveCustomerPhone(customerPhone);

    // Assert
    verify(customerPhoneDao).readActiveCustomerPhonesByCustomerId(isNull());
    verify(customerPhoneDao).save(isA(CustomerPhone.class));
    verify(customerPhone).getCustomer();
    verify(customerPhoneImpl).getId();
    verify(customerPhone).getId();
    verify(customerPhone).getPhone();
    verify(phoneImpl).isDefault();
    assertSame(customerPhoneImpl2, actualSaveCustomerPhoneResult);
  }

  /**
   * Test {@link CustomerPhoneServiceImpl#saveCustomerPhone(CustomerPhone)}.
   * <ul>
   *   <li>Given {@link CustomerPhoneImpl} {@link CustomerPhoneImpl#getPhone()} return {@link PhoneImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerPhoneServiceImpl#saveCustomerPhone(CustomerPhone)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerPhone CustomerPhoneServiceImpl.saveCustomerPhone(CustomerPhone)"})
  public void testSaveCustomerPhone_givenCustomerPhoneImplGetPhoneReturnPhoneImpl() {
    // Arrange
    CustomerPhoneImpl customerPhoneImpl = mock(CustomerPhoneImpl.class);
    when(customerPhoneImpl.getId()).thenReturn(4L);
    when(customerPhoneImpl.getPhone()).thenReturn(new PhoneImpl());

    ArrayList<CustomerPhone> customerPhoneList = new ArrayList<>();
    customerPhoneList.add(customerPhoneImpl);
    CustomerPhoneImpl customerPhoneImpl2 = new CustomerPhoneImpl();
    when(customerPhoneDao.save(Mockito.<CustomerPhone>any())).thenReturn(customerPhoneImpl2);
    when(customerPhoneDao.readActiveCustomerPhonesByCustomerId(Mockito.<Long>any())).thenReturn(customerPhoneList);
    PhoneImpl phoneImpl = mock(PhoneImpl.class);
    when(phoneImpl.isDefault()).thenReturn(true);
    CustomerPhoneImpl customerPhone = mock(CustomerPhoneImpl.class);
    when(customerPhone.getId()).thenReturn(1L);
    when(customerPhone.getPhone()).thenReturn(phoneImpl);
    when(customerPhone.getCustomer()).thenReturn(new CustomerImpl());

    // Act
    CustomerPhone actualSaveCustomerPhoneResult = customerPhoneServiceImpl.saveCustomerPhone(customerPhone);

    // Assert
    verify(customerPhoneDao).readActiveCustomerPhonesByCustomerId(isNull());
    verify(customerPhoneDao).save(isA(CustomerPhone.class));
    verify(customerPhone).getCustomer();
    verify(customerPhoneImpl).getId();
    verify(customerPhone).getId();
    verify(customerPhoneImpl).getPhone();
    verify(customerPhone).getPhone();
    verify(phoneImpl).isDefault();
    assertSame(customerPhoneImpl2, actualSaveCustomerPhoneResult);
  }

  /**
   * Test {@link CustomerPhoneServiceImpl#saveCustomerPhone(CustomerPhone)}.
   * <ul>
   *   <li>Given {@link PhoneImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerPhoneServiceImpl#saveCustomerPhone(CustomerPhone)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerPhone CustomerPhoneServiceImpl.saveCustomerPhone(CustomerPhone)"})
  public void testSaveCustomerPhone_givenPhoneImpl() {
    // Arrange
    CustomerPhoneImpl customerPhoneImpl = new CustomerPhoneImpl();
    when(customerPhoneDao.save(Mockito.<CustomerPhone>any())).thenReturn(customerPhoneImpl);
    when(customerPhoneDao.readActiveCustomerPhonesByCustomerId(Mockito.<Long>any())).thenReturn(new ArrayList<>());
    CustomerPhoneImpl customerPhone = mock(CustomerPhoneImpl.class);
    when(customerPhone.getPhone()).thenReturn(new PhoneImpl());
    when(customerPhone.getCustomer()).thenReturn(new CustomerImpl());

    // Act
    CustomerPhone actualSaveCustomerPhoneResult = customerPhoneServiceImpl.saveCustomerPhone(customerPhone);

    // Assert
    verify(customerPhoneDao).readActiveCustomerPhonesByCustomerId(isNull());
    verify(customerPhoneDao).save(isA(CustomerPhone.class));
    verify(customerPhone).getCustomer();
    verify(customerPhone).getPhone();
    assertSame(customerPhoneImpl, actualSaveCustomerPhoneResult);
  }

  /**
   * Test {@link CustomerPhoneServiceImpl#saveCustomerPhone(CustomerPhone)}.
   * <ul>
   *   <li>Given {@link PhoneImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerPhoneServiceImpl#saveCustomerPhone(CustomerPhone)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerPhone CustomerPhoneServiceImpl.saveCustomerPhone(CustomerPhone)"})
  public void testSaveCustomerPhone_givenPhoneImpl2() {
    // Arrange
    ArrayList<CustomerPhone> customerPhoneList = new ArrayList<>();
    customerPhoneList.add(new CustomerPhoneImpl());
    CustomerPhoneImpl customerPhoneImpl = new CustomerPhoneImpl();
    when(customerPhoneDao.save(Mockito.<CustomerPhone>any())).thenReturn(customerPhoneImpl);
    when(customerPhoneDao.readActiveCustomerPhonesByCustomerId(Mockito.<Long>any())).thenReturn(customerPhoneList);
    CustomerPhoneImpl customerPhone = mock(CustomerPhoneImpl.class);
    when(customerPhone.getPhone()).thenReturn(new PhoneImpl());
    when(customerPhone.getCustomer()).thenReturn(new CustomerImpl());

    // Act
    CustomerPhone actualSaveCustomerPhoneResult = customerPhoneServiceImpl.saveCustomerPhone(customerPhone);

    // Assert
    verify(customerPhoneDao).readActiveCustomerPhonesByCustomerId(isNull());
    verify(customerPhoneDao).save(isA(CustomerPhone.class));
    verify(customerPhone).getCustomer();
    verify(customerPhone).getPhone();
    assertSame(customerPhoneImpl, actualSaveCustomerPhoneResult);
  }

  /**
   * Test {@link CustomerPhoneServiceImpl#saveCustomerPhone(CustomerPhone)}.
   * <ul>
   *   <li>Given {@link PhoneImpl} {@link PhoneImpl#setDefault(boolean)} does nothing.</li>
   *   <li>Then calls {@link PhoneImpl#setDefault(boolean)}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerPhoneServiceImpl#saveCustomerPhone(CustomerPhone)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerPhone CustomerPhoneServiceImpl.saveCustomerPhone(CustomerPhone)"})
  public void testSaveCustomerPhone_givenPhoneImplSetDefaultDoesNothing_thenCallsSetDefault() {
    // Arrange
    PhoneImpl phoneImpl = mock(PhoneImpl.class);
    doNothing().when(phoneImpl).setDefault(anyBoolean());
    when(phoneImpl.isDefault()).thenReturn(true);
    CustomerPhoneImpl customerPhoneImpl = mock(CustomerPhoneImpl.class);
    when(customerPhoneImpl.getId()).thenReturn(4L);
    when(customerPhoneImpl.getPhone()).thenReturn(phoneImpl);

    ArrayList<CustomerPhone> customerPhoneList = new ArrayList<>();
    customerPhoneList.add(customerPhoneImpl);
    CustomerPhoneImpl customerPhoneImpl2 = new CustomerPhoneImpl();
    when(customerPhoneDao.save(Mockito.<CustomerPhone>any())).thenReturn(customerPhoneImpl2);
    when(customerPhoneDao.readActiveCustomerPhonesByCustomerId(Mockito.<Long>any())).thenReturn(customerPhoneList);
    PhoneImpl phoneImpl2 = mock(PhoneImpl.class);
    when(phoneImpl2.isDefault()).thenReturn(true);
    CustomerPhoneImpl customerPhone = mock(CustomerPhoneImpl.class);
    when(customerPhone.getId()).thenReturn(1L);
    when(customerPhone.getPhone()).thenReturn(phoneImpl2);
    when(customerPhone.getCustomer()).thenReturn(new CustomerImpl());

    // Act
    CustomerPhone actualSaveCustomerPhoneResult = customerPhoneServiceImpl.saveCustomerPhone(customerPhone);

    // Assert
    verify(customerPhoneDao).readActiveCustomerPhonesByCustomerId(isNull());
    verify(customerPhoneDao, atLeast(1)).save(Mockito.<CustomerPhone>any());
    verify(customerPhone).getCustomer();
    verify(customerPhoneImpl).getId();
    verify(customerPhone).getId();
    verify(customerPhone).getPhone();
    verify(customerPhoneImpl, atLeast(1)).getPhone();
    verify(phoneImpl).isDefault();
    verify(phoneImpl2).isDefault();
    verify(phoneImpl).setDefault(eq(false));
    assertSame(customerPhoneImpl2, actualSaveCustomerPhoneResult);
  }

  /**
   * Test {@link CustomerPhoneServiceImpl#readActiveCustomerPhonesByCustomerId(Long)}.
   * <p>
   * Method under test: {@link CustomerPhoneServiceImpl#readActiveCustomerPhonesByCustomerId(Long)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"List CustomerPhoneServiceImpl.readActiveCustomerPhonesByCustomerId(Long)"})
  public void testReadActiveCustomerPhonesByCustomerId() {
    // Arrange
    when(customerPhoneDao.readActiveCustomerPhonesByCustomerId(Mockito.<Long>any())).thenReturn(new ArrayList<>());

    // Act
    List<CustomerPhone> actualReadActiveCustomerPhonesByCustomerIdResult = customerPhoneServiceImpl
        .readActiveCustomerPhonesByCustomerId(1L);

    // Assert
    verify(customerPhoneDao).readActiveCustomerPhonesByCustomerId(eq(1L));
    assertTrue(actualReadActiveCustomerPhonesByCustomerIdResult.isEmpty());
  }

  /**
   * Test {@link CustomerPhoneServiceImpl#readCustomerPhoneById(Long)}.
   * <p>
   * Method under test: {@link CustomerPhoneServiceImpl#readCustomerPhoneById(Long)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerPhone CustomerPhoneServiceImpl.readCustomerPhoneById(Long)"})
  public void testReadCustomerPhoneById() {
    // Arrange
    CustomerPhoneImpl customerPhoneImpl = new CustomerPhoneImpl();
    when(customerPhoneDao.readCustomerPhoneById(Mockito.<Long>any())).thenReturn(customerPhoneImpl);

    // Act
    CustomerPhone actualReadCustomerPhoneByIdResult = customerPhoneServiceImpl.readCustomerPhoneById(1L);

    // Assert
    verify(customerPhoneDao).readCustomerPhoneById(eq(1L));
    assertSame(customerPhoneImpl, actualReadCustomerPhoneByIdResult);
  }

  /**
   * Test {@link CustomerPhoneServiceImpl#makeCustomerPhoneDefault(Long, Long)}.
   * <p>
   * Method under test: {@link CustomerPhoneServiceImpl#makeCustomerPhoneDefault(Long, Long)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerPhoneServiceImpl.makeCustomerPhoneDefault(Long, Long)"})
  public void testMakeCustomerPhoneDefault() {
    // Arrange
    doNothing().when(customerPhoneDao).makeCustomerPhoneDefault(Mockito.<Long>any(), Mockito.<Long>any());

    // Act
    customerPhoneServiceImpl.makeCustomerPhoneDefault(1L, 1L);

    // Assert
    verify(customerPhoneDao).makeCustomerPhoneDefault(eq(1L), eq(1L));
  }

  /**
   * Test {@link CustomerPhoneServiceImpl#deleteCustomerPhoneById(Long)}.
   * <p>
   * Method under test: {@link CustomerPhoneServiceImpl#deleteCustomerPhoneById(Long)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerPhoneServiceImpl.deleteCustomerPhoneById(Long)"})
  public void testDeleteCustomerPhoneById() {
    // Arrange
    doNothing().when(customerPhoneDao).deleteCustomerPhoneById(Mockito.<Long>any());

    // Act
    customerPhoneServiceImpl.deleteCustomerPhoneById(1L);

    // Assert
    verify(customerPhoneDao).deleteCustomerPhoneById(eq(1L));
  }

  /**
   * Test {@link CustomerPhoneServiceImpl#findDefaultCustomerPhone(Long)}.
   * <p>
   * Method under test: {@link CustomerPhoneServiceImpl#findDefaultCustomerPhone(Long)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerPhone CustomerPhoneServiceImpl.findDefaultCustomerPhone(Long)"})
  public void testFindDefaultCustomerPhone() {
    // Arrange
    CustomerPhoneImpl customerPhoneImpl = new CustomerPhoneImpl();
    when(customerPhoneDao.findDefaultCustomerPhone(Mockito.<Long>any())).thenReturn(customerPhoneImpl);

    // Act
    CustomerPhone actualFindDefaultCustomerPhoneResult = customerPhoneServiceImpl.findDefaultCustomerPhone(1L);

    // Assert
    verify(customerPhoneDao).findDefaultCustomerPhone(eq(1L));
    assertSame(customerPhoneImpl, actualFindDefaultCustomerPhoneResult);
  }

  /**
   * Test {@link CustomerPhoneServiceImpl#readAllCustomerPhonesByCustomerId(Long)}.
   * <p>
   * Method under test: {@link CustomerPhoneServiceImpl#readAllCustomerPhonesByCustomerId(Long)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"List CustomerPhoneServiceImpl.readAllCustomerPhonesByCustomerId(Long)"})
  public void testReadAllCustomerPhonesByCustomerId() {
    // Arrange
    when(customerPhoneDao.readAllCustomerPhonesByCustomerId(Mockito.<Long>any())).thenReturn(new ArrayList<>());

    // Act
    List<CustomerPhone> actualReadAllCustomerPhonesByCustomerIdResult = customerPhoneServiceImpl
        .readAllCustomerPhonesByCustomerId(1L);

    // Assert
    verify(customerPhoneDao).readAllCustomerPhonesByCustomerId(eq(1L));
    assertTrue(actualReadAllCustomerPhonesByCustomerIdResult.isEmpty());
  }

  /**
   * Test {@link CustomerPhoneServiceImpl#create()}.
   * <p>
   * Method under test: {@link CustomerPhoneServiceImpl#create()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerPhone CustomerPhoneServiceImpl.create()"})
  public void testCreate() {
    // Arrange
    CustomerPhoneImpl customerPhoneImpl = new CustomerPhoneImpl();
    when(customerPhoneDao.create()).thenReturn(customerPhoneImpl);

    // Act
    CustomerPhone actualCreateResult = customerPhoneServiceImpl.create();

    // Assert
    verify(customerPhoneDao).create();
    assertSame(customerPhoneImpl, actualCreateResult);
  }
}
