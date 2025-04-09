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

import static org.junit.Assert.assertNull;
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
import org.broadleafcommerce.profile.core.dao.CustomerPaymentDao;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;
import org.broadleafcommerce.profile.core.domain.CustomerPaymentImpl;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CustomerPaymentServiceImplDiffblueTest {
  @Mock
  private CustomerPaymentDao customerPaymentDao;

  @InjectMocks
  private CustomerPaymentServiceImpl customerPaymentServiceImpl;

  @Mock
  private CustomerService customerService;

  /**
   * Test {@link CustomerPaymentServiceImpl#saveCustomerPayment(CustomerPayment)}.
   * <p>
   * Method under test: {@link CustomerPaymentServiceImpl#saveCustomerPayment(CustomerPayment)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerPayment CustomerPaymentServiceImpl.saveCustomerPayment(CustomerPayment)"})
  public void testSaveCustomerPayment() {
    // Arrange
    CustomerPaymentImpl customerPaymentImpl = new CustomerPaymentImpl();
    when(customerPaymentDao.save(Mockito.<CustomerPayment>any())).thenReturn(customerPaymentImpl);

    // Act
    CustomerPayment actualSaveCustomerPaymentResult = customerPaymentServiceImpl
        .saveCustomerPayment(new CustomerPaymentImpl());

    // Assert
    verify(customerPaymentDao).save(isA(CustomerPayment.class));
    assertSame(customerPaymentImpl, actualSaveCustomerPaymentResult);
  }

  /**
   * Test {@link CustomerPaymentServiceImpl#readCustomerPaymentsByCustomerId(Long)}.
   * <p>
   * Method under test: {@link CustomerPaymentServiceImpl#readCustomerPaymentsByCustomerId(Long)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"List CustomerPaymentServiceImpl.readCustomerPaymentsByCustomerId(Long)"})
  public void testReadCustomerPaymentsByCustomerId() {
    // Arrange
    when(customerPaymentDao.readCustomerPaymentsByCustomerId(Mockito.<Long>any())).thenReturn(new ArrayList<>());

    // Act
    List<CustomerPayment> actualReadCustomerPaymentsByCustomerIdResult = customerPaymentServiceImpl
        .readCustomerPaymentsByCustomerId(1L);

    // Assert
    verify(customerPaymentDao).readCustomerPaymentsByCustomerId(eq(1L));
    assertTrue(actualReadCustomerPaymentsByCustomerIdResult.isEmpty());
  }

  /**
   * Test {@link CustomerPaymentServiceImpl#readCustomerPaymentById(Long)}.
   * <p>
   * Method under test: {@link CustomerPaymentServiceImpl#readCustomerPaymentById(Long)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerPayment CustomerPaymentServiceImpl.readCustomerPaymentById(Long)"})
  public void testReadCustomerPaymentById() {
    // Arrange
    CustomerPaymentImpl customerPaymentImpl = new CustomerPaymentImpl();
    when(customerPaymentDao.readCustomerPaymentById(Mockito.<Long>any())).thenReturn(customerPaymentImpl);

    // Act
    CustomerPayment actualReadCustomerPaymentByIdResult = customerPaymentServiceImpl.readCustomerPaymentById(1L);

    // Assert
    verify(customerPaymentDao).readCustomerPaymentById(eq(1L));
    assertSame(customerPaymentImpl, actualReadCustomerPaymentByIdResult);
  }

  /**
   * Test {@link CustomerPaymentServiceImpl#readCustomerPaymentByToken(String)}.
   * <p>
   * Method under test: {@link CustomerPaymentServiceImpl#readCustomerPaymentByToken(String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerPayment CustomerPaymentServiceImpl.readCustomerPaymentByToken(String)"})
  public void testReadCustomerPaymentByToken() {
    // Arrange
    CustomerPaymentImpl customerPaymentImpl = new CustomerPaymentImpl();
    when(customerPaymentDao.readCustomerPaymentByToken(Mockito.<String>any())).thenReturn(customerPaymentImpl);

    // Act
    CustomerPayment actualReadCustomerPaymentByTokenResult = customerPaymentServiceImpl
        .readCustomerPaymentByToken("sampleToken");

    // Assert
    verify(customerPaymentDao).readCustomerPaymentByToken(eq("sampleToken"));
    assertSame(customerPaymentImpl, actualReadCustomerPaymentByTokenResult);
  }

  /**
   * Test {@link CustomerPaymentServiceImpl#deleteCustomerPaymentById(Long)}.
   * <p>
   * Method under test: {@link CustomerPaymentServiceImpl#deleteCustomerPaymentById(Long)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerPaymentServiceImpl.deleteCustomerPaymentById(Long)"})
  public void testDeleteCustomerPaymentById() {
    // Arrange
    doNothing().when(customerPaymentDao).deleteCustomerPaymentById(Mockito.<Long>any());

    // Act
    customerPaymentServiceImpl.deleteCustomerPaymentById(1L);

    // Assert
    verify(customerPaymentDao).deleteCustomerPaymentById(eq(1L));
  }

  /**
   * Test {@link CustomerPaymentServiceImpl#deleteCustomerPaymentByToken(String)}.
   * <p>
   * Method under test: {@link CustomerPaymentServiceImpl#deleteCustomerPaymentByToken(String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerPaymentServiceImpl.deleteCustomerPaymentByToken(String)"})
  public void testDeleteCustomerPaymentByToken() {
    // Arrange
    doNothing().when(customerPaymentDao).deleteCustomerPaymentByToken(Mockito.<String>any());

    // Act
    customerPaymentServiceImpl.deleteCustomerPaymentByToken("sampleToken");

    // Assert
    verify(customerPaymentDao).deleteCustomerPaymentByToken(eq("sampleToken"));
  }

  /**
   * Test {@link CustomerPaymentServiceImpl#create()}.
   * <p>
   * Method under test: {@link CustomerPaymentServiceImpl#create()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerPayment CustomerPaymentServiceImpl.create()"})
  public void testCreate() {
    // Arrange
    CustomerPaymentImpl customerPaymentImpl = new CustomerPaymentImpl();
    when(customerPaymentDao.create()).thenReturn(customerPaymentImpl);

    // Act
    CustomerPayment actualCreateResult = customerPaymentServiceImpl.create();

    // Assert
    verify(customerPaymentDao).create();
    assertSame(customerPaymentImpl, actualCreateResult);
  }

  /**
   * Test {@link CustomerPaymentServiceImpl#findDefaultPaymentForCustomer(Customer)}.
   * <ul>
   *   <li>Given {@link ArrayList#ArrayList()} add {@link CustomerPaymentImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerPaymentServiceImpl#findDefaultPaymentForCustomer(Customer)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerPayment CustomerPaymentServiceImpl.findDefaultPaymentForCustomer(Customer)"})
  public void testFindDefaultPaymentForCustomer_givenArrayListAddCustomerPaymentImpl() {
    // Arrange
    ArrayList<CustomerPayment> customerPaymentList = new ArrayList<>();
    customerPaymentList.add(new CustomerPaymentImpl());
    when(customerPaymentDao.readCustomerPaymentsByCustomerId(Mockito.<Long>any())).thenReturn(customerPaymentList);

    // Act
    CustomerPayment actualFindDefaultPaymentForCustomerResult = customerPaymentServiceImpl
        .findDefaultPaymentForCustomer(new CustomerImpl());

    // Assert
    verify(customerPaymentDao).readCustomerPaymentsByCustomerId(isNull());
    assertNull(actualFindDefaultPaymentForCustomerResult);
  }

  /**
   * Test {@link CustomerPaymentServiceImpl#findDefaultPaymentForCustomer(Customer)}.
   * <ul>
   *   <li>Given {@link CustomerPaymentDao}.</li>
   *   <li>When {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerPaymentServiceImpl#findDefaultPaymentForCustomer(Customer)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerPayment CustomerPaymentServiceImpl.findDefaultPaymentForCustomer(Customer)"})
  public void testFindDefaultPaymentForCustomer_givenCustomerPaymentDao_whenNull() {
    // Arrange, Act and Assert
    assertNull(customerPaymentServiceImpl.findDefaultPaymentForCustomer(null));
  }

  /**
   * Test {@link CustomerPaymentServiceImpl#findDefaultPaymentForCustomer(Customer)}.
   * <ul>
   *   <li>Then calls {@link CustomerPaymentImpl#isDefault()}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerPaymentServiceImpl#findDefaultPaymentForCustomer(Customer)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerPayment CustomerPaymentServiceImpl.findDefaultPaymentForCustomer(Customer)"})
  public void testFindDefaultPaymentForCustomer_thenCallsIsDefault() {
    // Arrange
    CustomerPaymentImpl customerPaymentImpl = mock(CustomerPaymentImpl.class);
    when(customerPaymentImpl.isDefault()).thenReturn(true);

    ArrayList<CustomerPayment> customerPaymentList = new ArrayList<>();
    customerPaymentList.add(customerPaymentImpl);
    when(customerPaymentDao.readCustomerPaymentsByCustomerId(Mockito.<Long>any())).thenReturn(customerPaymentList);

    // Act
    customerPaymentServiceImpl.findDefaultPaymentForCustomer(new CustomerImpl());

    // Assert
    verify(customerPaymentDao).readCustomerPaymentsByCustomerId(isNull());
    verify(customerPaymentImpl).isDefault();
  }

  /**
   * Test {@link CustomerPaymentServiceImpl#findDefaultPaymentForCustomer(Customer)}.
   * <ul>
   *   <li>Then return {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerPaymentServiceImpl#findDefaultPaymentForCustomer(Customer)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerPayment CustomerPaymentServiceImpl.findDefaultPaymentForCustomer(Customer)"})
  public void testFindDefaultPaymentForCustomer_thenReturnNull() {
    // Arrange
    when(customerPaymentDao.readCustomerPaymentsByCustomerId(Mockito.<Long>any())).thenReturn(new ArrayList<>());

    // Act
    CustomerPayment actualFindDefaultPaymentForCustomerResult = customerPaymentServiceImpl
        .findDefaultPaymentForCustomer(new CustomerImpl());

    // Assert
    verify(customerPaymentDao).readCustomerPaymentsByCustomerId(isNull());
    assertNull(actualFindDefaultPaymentForCustomerResult);
  }

  /**
   * Test {@link CustomerPaymentServiceImpl#setAsDefaultPayment(CustomerPayment)}.
   * <ul>
   *   <li>Given {@link ArrayList#ArrayList()} add {@link CustomerPaymentImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerPaymentServiceImpl#setAsDefaultPayment(CustomerPayment)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerPayment CustomerPaymentServiceImpl.setAsDefaultPayment(CustomerPayment)"})
  public void testSetAsDefaultPayment_givenArrayListAddCustomerPaymentImpl() {
    // Arrange
    ArrayList<CustomerPayment> customerPaymentList = new ArrayList<>();
    customerPaymentList.add(new CustomerPaymentImpl());
    when(customerPaymentDao.readCustomerPaymentsByCustomerId(Mockito.<Long>any())).thenReturn(customerPaymentList);
    CustomerPaymentImpl customerPaymentImpl = new CustomerPaymentImpl();
    when(customerPaymentDao.save(Mockito.<CustomerPayment>any())).thenReturn(customerPaymentImpl);
    CustomerPaymentImpl payment = mock(CustomerPaymentImpl.class);
    when(payment.getCustomer()).thenReturn(new CustomerImpl());
    doNothing().when(payment).setIsDefault(anyBoolean());

    // Act
    CustomerPayment actualSetAsDefaultPaymentResult = customerPaymentServiceImpl.setAsDefaultPayment(payment);

    // Assert
    verify(customerPaymentDao).readCustomerPaymentsByCustomerId(isNull());
    verify(customerPaymentDao).save(isA(CustomerPayment.class));
    verify(payment).getCustomer();
    verify(payment).setIsDefault(eq(true));
    assertSame(customerPaymentImpl, actualSetAsDefaultPaymentResult);
  }

  /**
   * Test {@link CustomerPaymentServiceImpl#setAsDefaultPayment(CustomerPayment)}.
   * <ul>
   *   <li>Then calls {@link CustomerPaymentImpl#isDefault()}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerPaymentServiceImpl#setAsDefaultPayment(CustomerPayment)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerPayment CustomerPaymentServiceImpl.setAsDefaultPayment(CustomerPayment)"})
  public void testSetAsDefaultPayment_thenCallsIsDefault() {
    // Arrange
    CustomerPaymentImpl customerPaymentImpl = mock(CustomerPaymentImpl.class);
    doNothing().when(customerPaymentImpl).setIsDefault(anyBoolean());
    when(customerPaymentImpl.isDefault()).thenReturn(true);

    ArrayList<CustomerPayment> customerPaymentList = new ArrayList<>();
    customerPaymentList.add(customerPaymentImpl);
    when(customerPaymentDao.readCustomerPaymentsByCustomerId(Mockito.<Long>any())).thenReturn(customerPaymentList);
    CustomerPaymentImpl customerPaymentImpl2 = new CustomerPaymentImpl();
    when(customerPaymentDao.save(Mockito.<CustomerPayment>any())).thenReturn(customerPaymentImpl2);
    CustomerPaymentImpl payment = mock(CustomerPaymentImpl.class);
    when(payment.getCustomer()).thenReturn(new CustomerImpl());
    doNothing().when(payment).setIsDefault(anyBoolean());

    // Act
    CustomerPayment actualSetAsDefaultPaymentResult = customerPaymentServiceImpl.setAsDefaultPayment(payment);

    // Assert
    verify(customerPaymentDao).readCustomerPaymentsByCustomerId(isNull());
    verify(customerPaymentDao, atLeast(1)).save(Mockito.<CustomerPayment>any());
    verify(payment).getCustomer();
    verify(customerPaymentImpl).isDefault();
    verify(customerPaymentImpl).setIsDefault(eq(false));
    verify(payment).setIsDefault(eq(true));
    assertSame(customerPaymentImpl2, actualSetAsDefaultPaymentResult);
  }

  /**
   * Test {@link CustomerPaymentServiceImpl#setAsDefaultPayment(CustomerPayment)}.
   * <ul>
   *   <li>Then calls {@link CustomerPaymentDao#readCustomerPaymentsByCustomerId(Long)}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerPaymentServiceImpl#setAsDefaultPayment(CustomerPayment)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerPayment CustomerPaymentServiceImpl.setAsDefaultPayment(CustomerPayment)"})
  public void testSetAsDefaultPayment_thenCallsReadCustomerPaymentsByCustomerId() {
    // Arrange
    when(customerPaymentDao.readCustomerPaymentsByCustomerId(Mockito.<Long>any())).thenReturn(new ArrayList<>());
    CustomerPaymentImpl customerPaymentImpl = new CustomerPaymentImpl();
    when(customerPaymentDao.save(Mockito.<CustomerPayment>any())).thenReturn(customerPaymentImpl);
    CustomerPaymentImpl payment = mock(CustomerPaymentImpl.class);
    when(payment.getCustomer()).thenReturn(new CustomerImpl());
    doNothing().when(payment).setIsDefault(anyBoolean());

    // Act
    CustomerPayment actualSetAsDefaultPaymentResult = customerPaymentServiceImpl.setAsDefaultPayment(payment);

    // Assert
    verify(customerPaymentDao).readCustomerPaymentsByCustomerId(isNull());
    verify(customerPaymentDao).save(isA(CustomerPayment.class));
    verify(payment).getCustomer();
    verify(payment).setIsDefault(eq(true));
    assertSame(customerPaymentImpl, actualSetAsDefaultPaymentResult);
  }

  /**
   * Test {@link CustomerPaymentServiceImpl#setAsDefaultPayment(CustomerPayment)}.
   * <ul>
   *   <li>When {@link CustomerPaymentImpl} (default constructor).</li>
   *   <li>Then {@link CustomerPaymentImpl} (default constructor) Default.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerPaymentServiceImpl#setAsDefaultPayment(CustomerPayment)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerPayment CustomerPaymentServiceImpl.setAsDefaultPayment(CustomerPayment)"})
  public void testSetAsDefaultPayment_whenCustomerPaymentImpl_thenCustomerPaymentImplDefault() {
    // Arrange
    CustomerPaymentImpl customerPaymentImpl = new CustomerPaymentImpl();
    when(customerPaymentDao.save(Mockito.<CustomerPayment>any())).thenReturn(customerPaymentImpl);
    CustomerPaymentImpl payment = new CustomerPaymentImpl();

    // Act
    CustomerPayment actualSetAsDefaultPaymentResult = customerPaymentServiceImpl.setAsDefaultPayment(payment);

    // Assert
    verify(customerPaymentDao).save(isA(CustomerPayment.class));
    assertTrue(payment.isDefault());
    assertSame(customerPaymentImpl, actualSetAsDefaultPaymentResult);
  }

  /**
   * Test {@link CustomerPaymentServiceImpl#clearDefaultPaymentStatus(Customer)}.
   * <ul>
   *   <li>Given {@link ArrayList#ArrayList()} add {@link CustomerPaymentImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerPaymentServiceImpl#clearDefaultPaymentStatus(Customer)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerPaymentServiceImpl.clearDefaultPaymentStatus(Customer)"})
  public void testClearDefaultPaymentStatus_givenArrayListAddCustomerPaymentImpl() {
    // Arrange
    ArrayList<CustomerPayment> customerPaymentList = new ArrayList<>();
    customerPaymentList.add(new CustomerPaymentImpl());
    when(customerPaymentDao.readCustomerPaymentsByCustomerId(Mockito.<Long>any())).thenReturn(customerPaymentList);

    // Act
    customerPaymentServiceImpl.clearDefaultPaymentStatus(new CustomerImpl());

    // Assert
    verify(customerPaymentDao).readCustomerPaymentsByCustomerId(isNull());
  }

  /**
   * Test {@link CustomerPaymentServiceImpl#clearDefaultPaymentStatus(Customer)}.
   * <ul>
   *   <li>Then calls {@link CustomerPaymentDao#readCustomerPaymentsByCustomerId(Long)}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerPaymentServiceImpl#clearDefaultPaymentStatus(Customer)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerPaymentServiceImpl.clearDefaultPaymentStatus(Customer)"})
  public void testClearDefaultPaymentStatus_thenCallsReadCustomerPaymentsByCustomerId() {
    // Arrange
    when(customerPaymentDao.readCustomerPaymentsByCustomerId(Mockito.<Long>any())).thenReturn(new ArrayList<>());

    // Act
    customerPaymentServiceImpl.clearDefaultPaymentStatus(new CustomerImpl());

    // Assert
    verify(customerPaymentDao).readCustomerPaymentsByCustomerId(isNull());
  }

  /**
   * Test {@link CustomerPaymentServiceImpl#clearDefaultPaymentStatus(Customer)}.
   * <ul>
   *   <li>Then calls {@link CustomerPaymentDao#save(CustomerPayment)}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerPaymentServiceImpl#clearDefaultPaymentStatus(Customer)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerPaymentServiceImpl.clearDefaultPaymentStatus(Customer)"})
  public void testClearDefaultPaymentStatus_thenCallsSave() {
    // Arrange
    CustomerPaymentImpl customerPaymentImpl = mock(CustomerPaymentImpl.class);
    doNothing().when(customerPaymentImpl).setIsDefault(anyBoolean());
    when(customerPaymentImpl.isDefault()).thenReturn(true);

    ArrayList<CustomerPayment> customerPaymentList = new ArrayList<>();
    customerPaymentList.add(customerPaymentImpl);
    when(customerPaymentDao.save(Mockito.<CustomerPayment>any())).thenReturn(new CustomerPaymentImpl());
    when(customerPaymentDao.readCustomerPaymentsByCustomerId(Mockito.<Long>any())).thenReturn(customerPaymentList);

    // Act
    customerPaymentServiceImpl.clearDefaultPaymentStatus(new CustomerImpl());

    // Assert
    verify(customerPaymentDao).readCustomerPaymentsByCustomerId(isNull());
    verify(customerPaymentDao).save(isA(CustomerPayment.class));
    verify(customerPaymentImpl).isDefault();
    verify(customerPaymentImpl).setIsDefault(eq(false));
  }

  /**
   * Test {@link CustomerPaymentServiceImpl#deleteCustomerPaymentFromCustomer(Customer, CustomerPayment)}.
   * <ul>
   *   <li>When {@link CustomerImpl} (default constructor).</li>
   *   <li>Then return {@link CustomerImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerPaymentServiceImpl#deleteCustomerPaymentFromCustomer(Customer, CustomerPayment)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({
      "Customer CustomerPaymentServiceImpl.deleteCustomerPaymentFromCustomer(Customer, CustomerPayment)"})
  public void testDeleteCustomerPaymentFromCustomer_whenCustomerImpl_thenReturnCustomerImpl() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerService.saveCustomer(Mockito.<Customer>any())).thenReturn(customerImpl);
    CustomerImpl customer = new CustomerImpl();

    // Act
    Customer actualDeleteCustomerPaymentFromCustomerResult = customerPaymentServiceImpl
        .deleteCustomerPaymentFromCustomer(customer, new CustomerPaymentImpl());

    // Assert
    verify(customerService).saveCustomer(isA(Customer.class));
    assertSame(customerImpl, actualDeleteCustomerPaymentFromCustomerResult);
  }
}
