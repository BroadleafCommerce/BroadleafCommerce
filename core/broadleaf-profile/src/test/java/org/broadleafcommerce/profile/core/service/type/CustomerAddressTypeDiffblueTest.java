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
package org.broadleafcommerce.profile.core.service.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class CustomerAddressTypeDiffblueTest {
  /**
   * Test {@link CustomerAddressType#getInstance(String)}.
   * <p>
   * Method under test: {@link CustomerAddressType#getInstance(String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerAddressType CustomerAddressType.getInstance(String)"})
  public void testGetInstance() {
    // Arrange and Act
    CustomerAddressType actualInstance = CustomerAddressType.getInstance("Type");

    // Assert
    assertEquals("Friendly Type", actualInstance.getFriendlyType());
    assertEquals("Type", actualInstance.getType());
  }

  /**
   * Test getters and setters.
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerAddressType#CustomerAddressType()}
   *   <li>{@link CustomerAddressType#getFriendlyType()}
   *   <li>{@link CustomerAddressType#getType()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerAddressType.<init>()", "String CustomerAddressType.getFriendlyType()",
      "String CustomerAddressType.getType()"})
  public void testGettersAndSetters() {
    // Arrange and Act
    CustomerAddressType actualCustomerAddressType = new CustomerAddressType();
    String actualFriendlyType = actualCustomerAddressType.getFriendlyType();

    // Assert
    assertNull(actualFriendlyType);
    assertNull(actualCustomerAddressType.getType());
  }

  /**
   * Test {@link CustomerAddressType#CustomerAddressType(String, String)}.
   * <ul>
   *   <li>When {@code Other}.</li>
   *   <li>Then return Type is {@code Other}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerAddressType#CustomerAddressType(String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerAddressType.<init>(String, String)"})
  public void testNewCustomerAddressType_whenOther_thenReturnTypeIsOther() {
    // Arrange and Act
    CustomerAddressType actualCustomerAddressType = new CustomerAddressType("Other", "Friendly Type");

    // Assert
    assertEquals("Friendly Type", actualCustomerAddressType.getFriendlyType());
    assertEquals("Other", actualCustomerAddressType.getType());
  }

  /**
   * Test {@link CustomerAddressType#CustomerAddressType(String, String)}.
   * <ul>
   *   <li>When {@code Type}.</li>
   *   <li>Then return {@code Type}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerAddressType#CustomerAddressType(String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerAddressType.<init>(String, String)"})
  public void testNewCustomerAddressType_whenType_thenReturnType() {
    // Arrange and Act
    CustomerAddressType actualCustomerAddressType = new CustomerAddressType("Type", "Friendly Type");

    // Assert
    assertEquals("Friendly Type", actualCustomerAddressType.getFriendlyType());
    assertEquals("Type", actualCustomerAddressType.getType());
  }

  /**
   * Test {@link CustomerAddressType#equals(Object)}, and {@link CustomerAddressType#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerAddressType#equals(Object)}
   *   <li>{@link CustomerAddressType#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerAddressType.equals(Object)", "int CustomerAddressType.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual() {
    // Arrange
    CustomerAddressType customerAddressType = CustomerAddressType.BILLING;
    CustomerAddressType customerAddressType2 = CustomerAddressType.BILLING;

    // Act and Assert
    assertEquals(customerAddressType, customerAddressType2);
    int expectedHashCodeResult = customerAddressType.hashCode();
    assertEquals(expectedHashCodeResult, customerAddressType2.hashCode());
  }

  /**
   * Test {@link CustomerAddressType#equals(Object)}, and {@link CustomerAddressType#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerAddressType#equals(Object)}
   *   <li>{@link CustomerAddressType#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerAddressType.equals(Object)", "int CustomerAddressType.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual2() {
    // Arrange
    CustomerAddressType customerAddressType = new CustomerAddressType();
    CustomerAddressType customerAddressType2 = new CustomerAddressType();

    // Act and Assert
    assertEquals(customerAddressType, customerAddressType2);
    int expectedHashCodeResult = customerAddressType.hashCode();
    assertEquals(expectedHashCodeResult, customerAddressType2.hashCode());
  }

  /**
   * Test {@link CustomerAddressType#equals(Object)}, and {@link CustomerAddressType#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerAddressType#equals(Object)}
   *   <li>{@link CustomerAddressType#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerAddressType.equals(Object)", "int CustomerAddressType.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual3() {
    // Arrange
    CustomerAddressType customerAddressType = new CustomerAddressType("BILLING", "Friendly Type");
    CustomerAddressType customerAddressType2 = CustomerAddressType.BILLING;

    // Act and Assert
    assertEquals(customerAddressType, customerAddressType2);
    int expectedHashCodeResult = customerAddressType.hashCode();
    assertEquals(expectedHashCodeResult, customerAddressType2.hashCode());
  }

  /**
   * Test {@link CustomerAddressType#equals(Object)}, and {@link CustomerAddressType#hashCode()}.
   * <ul>
   *   <li>When other is same.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerAddressType#equals(Object)}
   *   <li>{@link CustomerAddressType#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerAddressType.equals(Object)", "int CustomerAddressType.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual() {
    // Arrange
    CustomerAddressType customerAddressType = CustomerAddressType.BILLING;

    // Act and Assert
    assertEquals(customerAddressType, customerAddressType);
    int expectedHashCodeResult = customerAddressType.hashCode();
    assertEquals(expectedHashCodeResult, customerAddressType.hashCode());
  }

  /**
   * Test {@link CustomerAddressType#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerAddressType#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerAddressType.equals(Object)", "int CustomerAddressType.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual() {
    // Arrange, Act and Assert
    assertNotEquals(CustomerAddressType.OTHER, CustomerAddressType.BILLING);
  }

  /**
   * Test {@link CustomerAddressType#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerAddressType#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerAddressType.equals(Object)", "int CustomerAddressType.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual2() {
    // Arrange, Act and Assert
    assertNotEquals(new CustomerAddressType(), CustomerAddressType.BILLING);
  }

  /**
   * Test {@link CustomerAddressType#equals(Object)}.
   * <ul>
   *   <li>When other is {@code null}.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerAddressType#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerAddressType.equals(Object)", "int CustomerAddressType.hashCode()"})
  public void testEquals_whenOtherIsNull_thenReturnNotEqual() {
    // Arrange, Act and Assert
    assertNotEquals(CustomerAddressType.BILLING, null);
  }

  /**
   * Test {@link CustomerAddressType#equals(Object)}.
   * <ul>
   *   <li>When other is wrong type.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerAddressType#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerAddressType.equals(Object)", "int CustomerAddressType.hashCode()"})
  public void testEquals_whenOtherIsWrongType_thenReturnNotEqual() {
    // Arrange, Act and Assert
    assertNotEquals(CustomerAddressType.BILLING, "Different type to CustomerAddressType");
  }
}
