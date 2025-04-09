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
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.util.HashMap;
import javax.persistence.NoResultException;
import org.broadleafcommerce.common.payment.PaymentGatewayType;
import org.broadleafcommerce.common.payment.PaymentType;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
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
public class CustomerPaymentDaoImplDiffblueTest {
  @InjectMocks
  private CustomerPaymentDaoImpl customerPaymentDaoImpl;

  @Mock
  private EntityConfiguration entityConfiguration;

  /**
   * Test {@link CustomerPaymentDaoImpl#create()}.
   * <ul>
   *   <li>Then return {@link CustomerPaymentImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerPaymentDaoImpl#create()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerPayment CustomerPaymentDaoImpl.create()"})
  public void testCreate_thenReturnCustomerPaymentImpl() {
    // Arrange
    CustomerPaymentImpl customerPaymentImpl = new CustomerPaymentImpl();
    customerPaymentImpl.setAdditionalFields(new HashMap<>());
    customerPaymentImpl.setBillingAddress(new AddressImpl());
    customerPaymentImpl.setCustomer(new CustomerImpl());
    customerPaymentImpl.setId(1L);
    customerPaymentImpl.setIsDefault(true);
    customerPaymentImpl.setPaymentGatewayType(new PaymentGatewayType("Type", "Friendly Type"));
    customerPaymentImpl.setPaymentToken("sampleToken");
    customerPaymentImpl.setPaymentType(new PaymentType("Type", "Friendly Type"));
    when(entityConfiguration.createEntityInstance(Mockito.<String>any())).thenReturn(customerPaymentImpl);

    // Act
    CustomerPayment actualCreateResult = customerPaymentDaoImpl.create();

    // Assert
    verify(entityConfiguration).createEntityInstance(eq("org.broadleafcommerce.profile.core.domain.CustomerPayment"));
    assertSame(customerPaymentImpl, actualCreateResult);
  }

  /**
   * Test {@link CustomerPaymentDaoImpl#create()}.
   * <ul>
   *   <li>Then throw {@link NoResultException}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerPaymentDaoImpl#create()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerPayment CustomerPaymentDaoImpl.create()"})
  public void testCreate_thenThrowNoResultException() {
    // Arrange
    when(entityConfiguration.createEntityInstance(Mockito.<String>any()))
        .thenThrow(new NoResultException("An error occurred"));

    // Act and Assert
    assertThrows(NoResultException.class, () -> customerPaymentDaoImpl.create());
    verify(entityConfiguration).createEntityInstance(eq("org.broadleafcommerce.profile.core.domain.CustomerPayment"));
  }
}
