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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.util.HashMap;
import java.util.Map;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.payment.PaymentGatewayType;
import org.broadleafcommerce.common.payment.PaymentType;
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
public class CustomerPaymentImplDiffblueTest {
  @Autowired
  private CustomerPaymentImpl customerPaymentImpl;

  /**
   * Test {@link CustomerPaymentImpl#getPaymentType()}.
   * <ul>
   *   <li>Then return {@link PaymentType#PaymentType(String, String)} with {@code Type} and {@code Friendly Type}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerPaymentImpl#getPaymentType()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"PaymentType CustomerPaymentImpl.getPaymentType()"})
  public void testGetPaymentType_thenReturnPaymentTypeWithTypeAndFriendlyType() {
    // Arrange
    CustomerPaymentImpl customerPaymentImpl2 = new CustomerPaymentImpl();
    PaymentType paymentType = new PaymentType("Type", "Friendly Type");

    customerPaymentImpl2.setPaymentType(paymentType);

    // Act and Assert
    assertEquals(paymentType, customerPaymentImpl2.getPaymentType());
  }

  /**
   * Test {@link CustomerPaymentImpl#setPaymentType(PaymentType)}.
   * <p>
   * Method under test: {@link CustomerPaymentImpl#setPaymentType(PaymentType)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerPaymentImpl.setPaymentType(PaymentType)"})
  public void testSetPaymentType() {
    // Arrange
    CustomerPaymentImpl customerPaymentImpl2 = new CustomerPaymentImpl();
    PaymentType paymentType = new PaymentType("Type", "Friendly Type");

    // Act
    customerPaymentImpl2.setPaymentType(paymentType);

    // Assert
    assertEquals("Type", customerPaymentImpl2.paymentType);
    assertEquals(paymentType, customerPaymentImpl2.getPaymentType());
  }

  /**
   * Test {@link CustomerPaymentImpl#setPaymentType(PaymentType)}.
   * <ul>
   *   <li>Given {@code Type}.</li>
   *   <li>Then {@link CustomerPaymentImpl} (default constructor) PaymentType Type is {@code Type}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerPaymentImpl#setPaymentType(PaymentType)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerPaymentImpl.setPaymentType(PaymentType)"})
  public void testSetPaymentType_givenType_thenCustomerPaymentImplPaymentTypeTypeIsType() {
    // Arrange
    CustomerPaymentImpl customerPaymentImpl2 = new CustomerPaymentImpl();
    PaymentType paymentType = mock(PaymentType.class);
    when(paymentType.getType()).thenReturn("Type");

    // Act
    customerPaymentImpl2.setPaymentType(paymentType);

    // Assert
    verify(paymentType).getType();
    assertEquals("Type", customerPaymentImpl2.getPaymentType().getType());
    assertEquals("Type", customerPaymentImpl2.paymentType);
  }

  /**
   * Test {@link CustomerPaymentImpl#getPaymentGatewayType()}.
   * <p>
   * Method under test: {@link CustomerPaymentImpl#getPaymentGatewayType()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"PaymentGatewayType CustomerPaymentImpl.getPaymentGatewayType()"})
  public void testGetPaymentGatewayType() {
    // Arrange, Act and Assert
    assertNull((new CustomerPaymentImpl()).getPaymentGatewayType());
  }

  /**
   * Test {@link CustomerPaymentImpl#setPaymentGatewayType(PaymentGatewayType)}.
   * <p>
   * Method under test: {@link CustomerPaymentImpl#setPaymentGatewayType(PaymentGatewayType)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerPaymentImpl.setPaymentGatewayType(PaymentGatewayType)"})
  public void testSetPaymentGatewayType() {
    // Arrange
    CustomerPaymentImpl customerPaymentImpl2 = new CustomerPaymentImpl();
    PaymentGatewayType paymentGatewayType = new PaymentGatewayType("Type", "Friendly Type");

    // Act
    customerPaymentImpl2.setPaymentGatewayType(paymentGatewayType);

    // Assert
    assertEquals("Type", customerPaymentImpl2.paymentGatewayType);
    assertEquals(paymentGatewayType, customerPaymentImpl2.getPaymentGatewayType());
  }

  /**
   * Test {@link CustomerPaymentImpl#setPaymentGatewayType(PaymentGatewayType)}.
   * <ul>
   *   <li>Then {@link CustomerPaymentImpl} (default constructor) PaymentGatewayType Type is {@code Type}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerPaymentImpl#setPaymentGatewayType(PaymentGatewayType)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerPaymentImpl.setPaymentGatewayType(PaymentGatewayType)"})
  public void testSetPaymentGatewayType_thenCustomerPaymentImplPaymentGatewayTypeTypeIsType() {
    // Arrange
    CustomerPaymentImpl customerPaymentImpl2 = new CustomerPaymentImpl();
    PaymentGatewayType paymentGatewayType = mock(PaymentGatewayType.class);
    when(paymentGatewayType.getType()).thenReturn("Type");

    // Act
    customerPaymentImpl2.setPaymentGatewayType(paymentGatewayType);

    // Assert
    verify(paymentGatewayType).getType();
    assertEquals("Type", customerPaymentImpl2.getPaymentGatewayType().getType());
    assertEquals("Type", customerPaymentImpl2.paymentGatewayType);
  }

  /**
   * Test {@link CustomerPaymentImpl#createOrRetrieveCopyInstance(MultiTenantCopyContext)}.
   * <p>
   * Method under test: {@link CustomerPaymentImpl#createOrRetrieveCopyInstance(MultiTenantCopyContext)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CreateResponse CustomerPaymentImpl.createOrRetrieveCopyInstance(MultiTenantCopyContext)"})
  public void testCreateOrRetrieveCopyInstance() throws CloneNotSupportedException {
    // Arrange
    CustomerPaymentImpl customerPaymentImpl2 = new CustomerPaymentImpl();
    MultiTenantCopyContext context = mock(MultiTenantCopyContext.class);
    CreateResponse<Object> createResponse = new CreateResponse<>("Clone", true);

    when(context.createOrRetrieveCopyInstance(Mockito.<Object>any())).thenReturn(createResponse);

    // Act
    CreateResponse<CustomerPayment> actualCreateOrRetrieveCopyInstanceResult = customerPaymentImpl2
        .createOrRetrieveCopyInstance(context);

    // Assert
    verify(context).createOrRetrieveCopyInstance(isA(Object.class));
    assertSame(createResponse, actualCreateOrRetrieveCopyInstanceResult);
  }

  /**
   * Test getters and setters.
   * <p>
   * Methods under test:
   * <ul>
   *   <li>default or parameterless constructor of {@link CustomerPaymentImpl}
   *   <li>{@link CustomerPaymentImpl#setAdditionalFields(Map)}
   *   <li>{@link CustomerPaymentImpl#setBillingAddress(Address)}
   *   <li>{@link CustomerPaymentImpl#setCustomer(Customer)}
   *   <li>{@link CustomerPaymentImpl#setId(Long)}
   *   <li>{@link CustomerPaymentImpl#setIsDefault(boolean)}
   *   <li>{@link CustomerPaymentImpl#setPaymentToken(String)}
   *   <li>{@link CustomerPaymentImpl#getAdditionalFields()}
   *   <li>{@link CustomerPaymentImpl#getBillingAddress()}
   *   <li>{@link CustomerPaymentImpl#getCustomer()}
   *   <li>{@link CustomerPaymentImpl#getId()}
   *   <li>{@link CustomerPaymentImpl#getPaymentToken()}
   *   <li>{@link CustomerPaymentImpl#isDefault()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerPaymentImpl.<init>()", "Map CustomerPaymentImpl.getAdditionalFields()",
      "Address CustomerPaymentImpl.getBillingAddress()", "Customer CustomerPaymentImpl.getCustomer()",
      "Long CustomerPaymentImpl.getId()", "String CustomerPaymentImpl.getPaymentToken()",
      "boolean CustomerPaymentImpl.isDefault()", "void CustomerPaymentImpl.setAdditionalFields(Map)",
      "void CustomerPaymentImpl.setBillingAddress(Address)", "void CustomerPaymentImpl.setCustomer(Customer)",
      "void CustomerPaymentImpl.setId(Long)", "void CustomerPaymentImpl.setIsDefault(boolean)",
      "void CustomerPaymentImpl.setPaymentToken(String)"})
  public void testGettersAndSetters() {
    // Arrange and Act
    CustomerPaymentImpl actualCustomerPaymentImpl = new CustomerPaymentImpl();
    HashMap<String, String> additionalFields = new HashMap<>();
    actualCustomerPaymentImpl.setAdditionalFields(additionalFields);
    AddressImpl billingAddress = new AddressImpl();
    actualCustomerPaymentImpl.setBillingAddress(billingAddress);
    CustomerImpl customer = new CustomerImpl();
    actualCustomerPaymentImpl.setCustomer(customer);
    actualCustomerPaymentImpl.setId(1L);
    actualCustomerPaymentImpl.setIsDefault(true);
    actualCustomerPaymentImpl.setPaymentToken("sampleToken");
    Map<String, String> actualAdditionalFields = actualCustomerPaymentImpl.getAdditionalFields();
    Address actualBillingAddress = actualCustomerPaymentImpl.getBillingAddress();
    Customer actualCustomer = actualCustomerPaymentImpl.getCustomer();
    Long actualId = actualCustomerPaymentImpl.getId();
    String actualPaymentToken = actualCustomerPaymentImpl.getPaymentToken();
    boolean actualIsDefaultResult = actualCustomerPaymentImpl.isDefault();

    // Assert
    assertEquals("sampleToken", actualPaymentToken);
    assertEquals(1L, actualId.longValue());
    assertTrue(actualAdditionalFields.isEmpty());
    assertTrue(actualIsDefaultResult);
    assertSame(additionalFields, actualAdditionalFields);
    assertSame(billingAddress, actualBillingAddress);
    assertSame(customer, actualCustomer);
  }
}
