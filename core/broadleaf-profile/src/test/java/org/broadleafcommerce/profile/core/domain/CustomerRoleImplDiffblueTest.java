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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class CustomerRoleImplDiffblueTest {
  /**
   * Test {@link CustomerRoleImpl#getRoleName()}.
   * <ul>
   *   <li>Given {@link CustomerRoleImpl} (default constructor) Customer is {@link CustomerImpl} (default constructor).</li>
   *   <li>Then return {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerRoleImpl#getRoleName()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"java.lang.String CustomerRoleImpl.getRoleName()"})
  public void testGetRoleName_givenCustomerRoleImplCustomerIsCustomerImpl_thenReturnNull() {
    // Arrange
    CustomerRoleImpl customerRoleImpl = new CustomerRoleImpl();
    customerRoleImpl.setCustomer(new CustomerImpl());
    customerRoleImpl.setId(1L);
    customerRoleImpl.setRole(new RoleImpl());

    // Act and Assert
    assertNull(customerRoleImpl.getRoleName());
  }

  /**
   * Test {@link CustomerRoleImpl#equals(Object)}, and {@link CustomerRoleImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerRoleImpl#equals(Object)}
   *   <li>{@link CustomerRoleImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerRoleImpl.equals(Object)", "int CustomerRoleImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual() {
    // Arrange
    CustomerRoleImpl customerRoleImpl = new CustomerRoleImpl();
    customerRoleImpl.setCustomer(new CustomerImpl());
    customerRoleImpl.setId(1L);
    customerRoleImpl.setRole(new RoleImpl());

    CustomerRoleImpl customerRoleImpl2 = new CustomerRoleImpl();
    customerRoleImpl2.setCustomer(new CustomerImpl());
    customerRoleImpl2.setId(1L);
    customerRoleImpl2.setRole(new RoleImpl());

    // Act and Assert
    assertEquals(customerRoleImpl, customerRoleImpl2);
    int expectedHashCodeResult = customerRoleImpl.hashCode();
    assertEquals(expectedHashCodeResult, customerRoleImpl2.hashCode());
  }

  /**
   * Test {@link CustomerRoleImpl#equals(Object)}, and {@link CustomerRoleImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerRoleImpl#equals(Object)}
   *   <li>{@link CustomerRoleImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerRoleImpl.equals(Object)", "int CustomerRoleImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual2() {
    // Arrange
    CustomerRoleImpl customerRoleImpl = new CustomerRoleImpl();
    customerRoleImpl.setCustomer(new CustomerImpl());
    customerRoleImpl.setId(null);
    customerRoleImpl.setRole(new RoleImpl());

    CustomerRoleImpl customerRoleImpl2 = new CustomerRoleImpl();
    customerRoleImpl2.setCustomer(new CustomerImpl());
    customerRoleImpl2.setId(1L);
    customerRoleImpl2.setRole(new RoleImpl());

    // Act and Assert
    assertEquals(customerRoleImpl, customerRoleImpl2);
    int expectedHashCodeResult = customerRoleImpl.hashCode();
    assertEquals(expectedHashCodeResult, customerRoleImpl2.hashCode());
  }

  /**
   * Test {@link CustomerRoleImpl#equals(Object)}, and {@link CustomerRoleImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerRoleImpl#equals(Object)}
   *   <li>{@link CustomerRoleImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerRoleImpl.equals(Object)", "int CustomerRoleImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual3() {
    // Arrange
    CustomerRoleImpl customerRoleImpl = new CustomerRoleImpl();
    customerRoleImpl.setCustomer(new CustomerImpl());
    customerRoleImpl.setId(1L);
    customerRoleImpl.setRole(new RoleImpl());

    CustomerRoleImpl customerRoleImpl2 = new CustomerRoleImpl();
    customerRoleImpl2.setCustomer(new CustomerImpl());
    customerRoleImpl2.setId(null);
    customerRoleImpl2.setRole(new RoleImpl());

    // Act and Assert
    assertEquals(customerRoleImpl, customerRoleImpl2);
    int expectedHashCodeResult = customerRoleImpl.hashCode();
    assertEquals(expectedHashCodeResult, customerRoleImpl2.hashCode());
  }

  /**
   * Test {@link CustomerRoleImpl#equals(Object)}, and {@link CustomerRoleImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerRoleImpl#equals(Object)}
   *   <li>{@link CustomerRoleImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerRoleImpl.equals(Object)", "int CustomerRoleImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual4() {
    // Arrange
    CustomerRoleImpl customerRoleImpl = new CustomerRoleImpl();
    customerRoleImpl.setCustomer(null);
    customerRoleImpl.setId(null);
    customerRoleImpl.setRole(new RoleImpl());

    CustomerRoleImpl customerRoleImpl2 = new CustomerRoleImpl();
    customerRoleImpl2.setCustomer(null);
    customerRoleImpl2.setId(1L);
    customerRoleImpl2.setRole(new RoleImpl());

    // Act and Assert
    assertEquals(customerRoleImpl, customerRoleImpl2);
    int expectedHashCodeResult = customerRoleImpl.hashCode();
    assertEquals(expectedHashCodeResult, customerRoleImpl2.hashCode());
  }

  /**
   * Test {@link CustomerRoleImpl#equals(Object)}, and {@link CustomerRoleImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerRoleImpl#equals(Object)}
   *   <li>{@link CustomerRoleImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerRoleImpl.equals(Object)", "int CustomerRoleImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual5() {
    // Arrange
    CustomerRoleImpl customerRoleImpl = new CustomerRoleImpl();
    customerRoleImpl.setCustomer(new CustomerImpl());
    customerRoleImpl.setId(null);
    customerRoleImpl.setRole(null);

    CustomerRoleImpl customerRoleImpl2 = new CustomerRoleImpl();
    customerRoleImpl2.setCustomer(new CustomerImpl());
    customerRoleImpl2.setId(1L);
    customerRoleImpl2.setRole(null);

    // Act and Assert
    assertEquals(customerRoleImpl, customerRoleImpl2);
    int expectedHashCodeResult = customerRoleImpl.hashCode();
    assertEquals(expectedHashCodeResult, customerRoleImpl2.hashCode());
  }

  /**
   * Test {@link CustomerRoleImpl#equals(Object)}, and {@link CustomerRoleImpl#hashCode()}.
   * <ul>
   *   <li>When other is same.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerRoleImpl#equals(Object)}
   *   <li>{@link CustomerRoleImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerRoleImpl.equals(Object)", "int CustomerRoleImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual() {
    // Arrange
    CustomerRoleImpl customerRoleImpl = new CustomerRoleImpl();
    customerRoleImpl.setCustomer(new CustomerImpl());
    customerRoleImpl.setId(1L);
    customerRoleImpl.setRole(new RoleImpl());

    // Act and Assert
    assertEquals(customerRoleImpl, customerRoleImpl);
    int expectedHashCodeResult = customerRoleImpl.hashCode();
    assertEquals(expectedHashCodeResult, customerRoleImpl.hashCode());
  }

  /**
   * Test {@link CustomerRoleImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerRoleImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerRoleImpl.equals(Object)", "int CustomerRoleImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual() {
    // Arrange
    CustomerRoleImpl customerRoleImpl = new CustomerRoleImpl();
    customerRoleImpl.setCustomer(new CustomerImpl());
    customerRoleImpl.setId(2L);
    customerRoleImpl.setRole(new RoleImpl());

    CustomerRoleImpl customerRoleImpl2 = new CustomerRoleImpl();
    customerRoleImpl2.setCustomer(new CustomerImpl());
    customerRoleImpl2.setId(1L);
    customerRoleImpl2.setRole(new RoleImpl());

    // Act and Assert
    assertNotEquals(customerRoleImpl, customerRoleImpl2);
  }

  /**
   * Test {@link CustomerRoleImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerRoleImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerRoleImpl.equals(Object)", "int CustomerRoleImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual2() {
    // Arrange
    CustomerRoleImpl customerRoleImpl = new CustomerRoleImpl();
    customerRoleImpl.setCustomer(null);
    customerRoleImpl.setId(null);
    customerRoleImpl.setRole(new RoleImpl());

    CustomerRoleImpl customerRoleImpl2 = new CustomerRoleImpl();
    customerRoleImpl2.setCustomer(new CustomerImpl());
    customerRoleImpl2.setId(1L);
    customerRoleImpl2.setRole(new RoleImpl());

    // Act and Assert
    assertNotEquals(customerRoleImpl, customerRoleImpl2);
  }

  /**
   * Test {@link CustomerRoleImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerRoleImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerRoleImpl.equals(Object)", "int CustomerRoleImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual3() {
    // Arrange
    CustomerRoleImpl customerRoleImpl = new CustomerRoleImpl();
    customerRoleImpl.setCustomer(mock(CustomerImpl.class));
    customerRoleImpl.setId(null);
    customerRoleImpl.setRole(new RoleImpl());

    CustomerRoleImpl customerRoleImpl2 = new CustomerRoleImpl();
    customerRoleImpl2.setCustomer(new CustomerImpl());
    customerRoleImpl2.setId(1L);
    customerRoleImpl2.setRole(new RoleImpl());

    // Act and Assert
    assertNotEquals(customerRoleImpl, customerRoleImpl2);
  }

  /**
   * Test {@link CustomerRoleImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerRoleImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerRoleImpl.equals(Object)", "int CustomerRoleImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual4() {
    // Arrange
    CustomerRoleImpl customerRoleImpl = new CustomerRoleImpl();
    customerRoleImpl.setCustomer(new CustomerImpl());
    customerRoleImpl.setId(null);
    customerRoleImpl.setRole(null);

    CustomerRoleImpl customerRoleImpl2 = new CustomerRoleImpl();
    customerRoleImpl2.setCustomer(new CustomerImpl());
    customerRoleImpl2.setId(1L);
    customerRoleImpl2.setRole(new RoleImpl());

    // Act and Assert
    assertNotEquals(customerRoleImpl, customerRoleImpl2);
  }

  /**
   * Test {@link CustomerRoleImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerRoleImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerRoleImpl.equals(Object)", "int CustomerRoleImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual5() {
    // Arrange
    CustomerRoleImpl customerRoleImpl = new CustomerRoleImpl();
    customerRoleImpl.setCustomer(new CustomerImpl());
    customerRoleImpl.setId(null);
    customerRoleImpl.setRole(mock(RoleImpl.class));

    CustomerRoleImpl customerRoleImpl2 = new CustomerRoleImpl();
    customerRoleImpl2.setCustomer(new CustomerImpl());
    customerRoleImpl2.setId(1L);
    customerRoleImpl2.setRole(new RoleImpl());

    // Act and Assert
    assertNotEquals(customerRoleImpl, customerRoleImpl2);
  }

  /**
   * Test {@link CustomerRoleImpl#equals(Object)}.
   * <ul>
   *   <li>When other is {@code null}.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerRoleImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerRoleImpl.equals(Object)", "int CustomerRoleImpl.hashCode()"})
  public void testEquals_whenOtherIsNull_thenReturnNotEqual() {
    // Arrange
    CustomerRoleImpl customerRoleImpl = new CustomerRoleImpl();
    customerRoleImpl.setCustomer(new CustomerImpl());
    customerRoleImpl.setId(1L);
    customerRoleImpl.setRole(new RoleImpl());

    // Act and Assert
    assertNotEquals(customerRoleImpl, null);
  }

  /**
   * Test {@link CustomerRoleImpl#equals(Object)}.
   * <ul>
   *   <li>When other is wrong type.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerRoleImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerRoleImpl.equals(Object)", "int CustomerRoleImpl.hashCode()"})
  public void testEquals_whenOtherIsWrongType_thenReturnNotEqual() {
    // Arrange
    CustomerRoleImpl customerRoleImpl = new CustomerRoleImpl();
    customerRoleImpl.setCustomer(new CustomerImpl());
    customerRoleImpl.setId(1L);
    customerRoleImpl.setRole(new RoleImpl());

    // Act and Assert
    assertNotEquals(customerRoleImpl, "Different type to CustomerRoleImpl");
  }

  /**
   * Test getters and setters.
   * <p>
   * Methods under test:
   * <ul>
   *   <li>default or parameterless constructor of {@link CustomerRoleImpl}
   *   <li>{@link CustomerRoleImpl#setCustomer(Customer)}
   *   <li>{@link CustomerRoleImpl#setId(Long)}
   *   <li>{@link CustomerRoleImpl#setRole(Role)}
   *   <li>{@link CustomerRoleImpl#getCustomer()}
   *   <li>{@link CustomerRoleImpl#getId()}
   *   <li>{@link CustomerRoleImpl#getRole()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerRoleImpl.<init>()", "Customer CustomerRoleImpl.getCustomer()",
      "Long CustomerRoleImpl.getId()", "Role CustomerRoleImpl.getRole()", "void CustomerRoleImpl.setCustomer(Customer)",
      "void CustomerRoleImpl.setId(Long)", "void CustomerRoleImpl.setRole(Role)"})
  public void testGettersAndSetters() {
    // Arrange and Act
    CustomerRoleImpl actualCustomerRoleImpl = new CustomerRoleImpl();
    CustomerImpl customer = new CustomerImpl();
    actualCustomerRoleImpl.setCustomer(customer);
    actualCustomerRoleImpl.setId(1L);
    RoleImpl role = new RoleImpl();
    actualCustomerRoleImpl.setRole(role);
    Customer actualCustomer = actualCustomerRoleImpl.getCustomer();
    Long actualId = actualCustomerRoleImpl.getId();
    Role actualRole = actualCustomerRoleImpl.getRole();

    // Assert
    assertEquals(1L, actualId.longValue());
    assertSame(customer, actualCustomer);
    assertSame(role, actualRole);
  }
}
