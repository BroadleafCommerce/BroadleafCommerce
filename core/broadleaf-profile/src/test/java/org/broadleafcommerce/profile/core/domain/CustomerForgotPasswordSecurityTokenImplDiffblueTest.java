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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class CustomerForgotPasswordSecurityTokenImplDiffblueTest {
  /**
   * Test {@link CustomerForgotPasswordSecurityTokenImpl#equals(Object)}, and {@link CustomerForgotPasswordSecurityTokenImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerForgotPasswordSecurityTokenImpl#equals(Object)}
   *   <li>{@link CustomerForgotPasswordSecurityTokenImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerForgotPasswordSecurityTokenImpl.equals(Object)",
      "int CustomerForgotPasswordSecurityTokenImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual() {
    // Arrange
    CustomerForgotPasswordSecurityTokenImpl customerForgotPasswordSecurityTokenImpl = new CustomerForgotPasswordSecurityTokenImpl();
    customerForgotPasswordSecurityTokenImpl
        .setCreateDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    customerForgotPasswordSecurityTokenImpl.setCustomerId(1L);
    customerForgotPasswordSecurityTokenImpl.setToken("ABC123");
    customerForgotPasswordSecurityTokenImpl
        .setTokenUsedDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    customerForgotPasswordSecurityTokenImpl.setTokenUsedFlag(true);

    CustomerForgotPasswordSecurityTokenImpl customerForgotPasswordSecurityTokenImpl2 = new CustomerForgotPasswordSecurityTokenImpl();
    customerForgotPasswordSecurityTokenImpl2
        .setCreateDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    customerForgotPasswordSecurityTokenImpl2.setCustomerId(1L);
    customerForgotPasswordSecurityTokenImpl2.setToken("ABC123");
    customerForgotPasswordSecurityTokenImpl2
        .setTokenUsedDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    customerForgotPasswordSecurityTokenImpl2.setTokenUsedFlag(true);

    // Act and Assert
    assertEquals(customerForgotPasswordSecurityTokenImpl, customerForgotPasswordSecurityTokenImpl2);
    int expectedHashCodeResult = customerForgotPasswordSecurityTokenImpl.hashCode();
    assertEquals(expectedHashCodeResult, customerForgotPasswordSecurityTokenImpl2.hashCode());
  }

  /**
   * Test {@link CustomerForgotPasswordSecurityTokenImpl#equals(Object)}, and {@link CustomerForgotPasswordSecurityTokenImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerForgotPasswordSecurityTokenImpl#equals(Object)}
   *   <li>{@link CustomerForgotPasswordSecurityTokenImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerForgotPasswordSecurityTokenImpl.equals(Object)",
      "int CustomerForgotPasswordSecurityTokenImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual2() {
    // Arrange
    CustomerForgotPasswordSecurityTokenImpl customerForgotPasswordSecurityTokenImpl = new CustomerForgotPasswordSecurityTokenImpl();
    customerForgotPasswordSecurityTokenImpl
        .setCreateDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    customerForgotPasswordSecurityTokenImpl.setCustomerId(1L);
    customerForgotPasswordSecurityTokenImpl.setToken(null);
    customerForgotPasswordSecurityTokenImpl
        .setTokenUsedDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    customerForgotPasswordSecurityTokenImpl.setTokenUsedFlag(true);

    CustomerForgotPasswordSecurityTokenImpl customerForgotPasswordSecurityTokenImpl2 = new CustomerForgotPasswordSecurityTokenImpl();
    customerForgotPasswordSecurityTokenImpl2
        .setCreateDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    customerForgotPasswordSecurityTokenImpl2.setCustomerId(1L);
    customerForgotPasswordSecurityTokenImpl2.setToken(null);
    customerForgotPasswordSecurityTokenImpl2
        .setTokenUsedDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    customerForgotPasswordSecurityTokenImpl2.setTokenUsedFlag(true);

    // Act and Assert
    assertEquals(customerForgotPasswordSecurityTokenImpl, customerForgotPasswordSecurityTokenImpl2);
    int expectedHashCodeResult = customerForgotPasswordSecurityTokenImpl.hashCode();
    assertEquals(expectedHashCodeResult, customerForgotPasswordSecurityTokenImpl2.hashCode());
  }

  /**
   * Test {@link CustomerForgotPasswordSecurityTokenImpl#equals(Object)}, and {@link CustomerForgotPasswordSecurityTokenImpl#hashCode()}.
   * <ul>
   *   <li>When other is same.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerForgotPasswordSecurityTokenImpl#equals(Object)}
   *   <li>{@link CustomerForgotPasswordSecurityTokenImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerForgotPasswordSecurityTokenImpl.equals(Object)",
      "int CustomerForgotPasswordSecurityTokenImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual() {
    // Arrange
    CustomerForgotPasswordSecurityTokenImpl customerForgotPasswordSecurityTokenImpl = new CustomerForgotPasswordSecurityTokenImpl();
    customerForgotPasswordSecurityTokenImpl
        .setCreateDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    customerForgotPasswordSecurityTokenImpl.setCustomerId(1L);
    customerForgotPasswordSecurityTokenImpl.setToken("ABC123");
    customerForgotPasswordSecurityTokenImpl
        .setTokenUsedDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    customerForgotPasswordSecurityTokenImpl.setTokenUsedFlag(true);

    // Act and Assert
    assertEquals(customerForgotPasswordSecurityTokenImpl, customerForgotPasswordSecurityTokenImpl);
    int expectedHashCodeResult = customerForgotPasswordSecurityTokenImpl.hashCode();
    assertEquals(expectedHashCodeResult, customerForgotPasswordSecurityTokenImpl.hashCode());
  }

  /**
   * Test {@link CustomerForgotPasswordSecurityTokenImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerForgotPasswordSecurityTokenImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerForgotPasswordSecurityTokenImpl.equals(Object)",
      "int CustomerForgotPasswordSecurityTokenImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual() {
    // Arrange
    CustomerForgotPasswordSecurityTokenImpl customerForgotPasswordSecurityTokenImpl = new CustomerForgotPasswordSecurityTokenImpl();
    customerForgotPasswordSecurityTokenImpl
        .setCreateDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    customerForgotPasswordSecurityTokenImpl.setCustomerId(1L);
    customerForgotPasswordSecurityTokenImpl.setToken("Token");
    customerForgotPasswordSecurityTokenImpl
        .setTokenUsedDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    customerForgotPasswordSecurityTokenImpl.setTokenUsedFlag(true);

    CustomerForgotPasswordSecurityTokenImpl customerForgotPasswordSecurityTokenImpl2 = new CustomerForgotPasswordSecurityTokenImpl();
    customerForgotPasswordSecurityTokenImpl2
        .setCreateDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    customerForgotPasswordSecurityTokenImpl2.setCustomerId(1L);
    customerForgotPasswordSecurityTokenImpl2.setToken("ABC123");
    customerForgotPasswordSecurityTokenImpl2
        .setTokenUsedDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    customerForgotPasswordSecurityTokenImpl2.setTokenUsedFlag(true);

    // Act and Assert
    assertNotEquals(customerForgotPasswordSecurityTokenImpl, customerForgotPasswordSecurityTokenImpl2);
  }

  /**
   * Test {@link CustomerForgotPasswordSecurityTokenImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerForgotPasswordSecurityTokenImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerForgotPasswordSecurityTokenImpl.equals(Object)",
      "int CustomerForgotPasswordSecurityTokenImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual2() {
    // Arrange
    CustomerForgotPasswordSecurityTokenImpl customerForgotPasswordSecurityTokenImpl = new CustomerForgotPasswordSecurityTokenImpl();
    customerForgotPasswordSecurityTokenImpl
        .setCreateDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    customerForgotPasswordSecurityTokenImpl.setCustomerId(1L);
    customerForgotPasswordSecurityTokenImpl.setToken(null);
    customerForgotPasswordSecurityTokenImpl
        .setTokenUsedDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    customerForgotPasswordSecurityTokenImpl.setTokenUsedFlag(true);

    CustomerForgotPasswordSecurityTokenImpl customerForgotPasswordSecurityTokenImpl2 = new CustomerForgotPasswordSecurityTokenImpl();
    customerForgotPasswordSecurityTokenImpl2
        .setCreateDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    customerForgotPasswordSecurityTokenImpl2.setCustomerId(1L);
    customerForgotPasswordSecurityTokenImpl2.setToken("ABC123");
    customerForgotPasswordSecurityTokenImpl2
        .setTokenUsedDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    customerForgotPasswordSecurityTokenImpl2.setTokenUsedFlag(true);

    // Act and Assert
    assertNotEquals(customerForgotPasswordSecurityTokenImpl, customerForgotPasswordSecurityTokenImpl2);
  }

  /**
   * Test {@link CustomerForgotPasswordSecurityTokenImpl#equals(Object)}.
   * <ul>
   *   <li>When other is {@code null}.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerForgotPasswordSecurityTokenImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerForgotPasswordSecurityTokenImpl.equals(Object)",
      "int CustomerForgotPasswordSecurityTokenImpl.hashCode()"})
  public void testEquals_whenOtherIsNull_thenReturnNotEqual() {
    // Arrange
    CustomerForgotPasswordSecurityTokenImpl customerForgotPasswordSecurityTokenImpl = new CustomerForgotPasswordSecurityTokenImpl();
    customerForgotPasswordSecurityTokenImpl
        .setCreateDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    customerForgotPasswordSecurityTokenImpl.setCustomerId(1L);
    customerForgotPasswordSecurityTokenImpl.setToken("ABC123");
    customerForgotPasswordSecurityTokenImpl
        .setTokenUsedDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    customerForgotPasswordSecurityTokenImpl.setTokenUsedFlag(true);

    // Act and Assert
    assertNotEquals(customerForgotPasswordSecurityTokenImpl, null);
  }

  /**
   * Test {@link CustomerForgotPasswordSecurityTokenImpl#equals(Object)}.
   * <ul>
   *   <li>When other is wrong type.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerForgotPasswordSecurityTokenImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerForgotPasswordSecurityTokenImpl.equals(Object)",
      "int CustomerForgotPasswordSecurityTokenImpl.hashCode()"})
  public void testEquals_whenOtherIsWrongType_thenReturnNotEqual() {
    // Arrange
    CustomerForgotPasswordSecurityTokenImpl customerForgotPasswordSecurityTokenImpl = new CustomerForgotPasswordSecurityTokenImpl();
    customerForgotPasswordSecurityTokenImpl
        .setCreateDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    customerForgotPasswordSecurityTokenImpl.setCustomerId(1L);
    customerForgotPasswordSecurityTokenImpl.setToken("ABC123");
    customerForgotPasswordSecurityTokenImpl
        .setTokenUsedDate(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    customerForgotPasswordSecurityTokenImpl.setTokenUsedFlag(true);

    // Act and Assert
    assertNotEquals(customerForgotPasswordSecurityTokenImpl,
        "Different type to CustomerForgotPasswordSecurityTokenImpl");
  }

  /**
   * Test getters and setters.
   * <p>
   * Methods under test:
   * <ul>
   *   <li>default or parameterless constructor of {@link CustomerForgotPasswordSecurityTokenImpl}
   *   <li>{@link CustomerForgotPasswordSecurityTokenImpl#setCreateDate(Date)}
   *   <li>{@link CustomerForgotPasswordSecurityTokenImpl#setCustomerId(Long)}
   *   <li>{@link CustomerForgotPasswordSecurityTokenImpl#setToken(String)}
   *   <li>{@link CustomerForgotPasswordSecurityTokenImpl#setTokenUsedDate(Date)}
   *   <li>{@link CustomerForgotPasswordSecurityTokenImpl#setTokenUsedFlag(boolean)}
   *   <li>{@link CustomerForgotPasswordSecurityTokenImpl#getCreateDate()}
   *   <li>{@link CustomerForgotPasswordSecurityTokenImpl#getCustomerId()}
   *   <li>{@link CustomerForgotPasswordSecurityTokenImpl#getToken()}
   *   <li>{@link CustomerForgotPasswordSecurityTokenImpl#getTokenUsedDate()}
   *   <li>{@link CustomerForgotPasswordSecurityTokenImpl#isTokenUsedFlag()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerForgotPasswordSecurityTokenImpl.<init>()",
      "Date CustomerForgotPasswordSecurityTokenImpl.getCreateDate()",
      "Long CustomerForgotPasswordSecurityTokenImpl.getCustomerId()",
      "String CustomerForgotPasswordSecurityTokenImpl.getToken()",
      "Date CustomerForgotPasswordSecurityTokenImpl.getTokenUsedDate()",
      "boolean CustomerForgotPasswordSecurityTokenImpl.isTokenUsedFlag()",
      "void CustomerForgotPasswordSecurityTokenImpl.setCreateDate(Date)",
      "void CustomerForgotPasswordSecurityTokenImpl.setCustomerId(Long)",
      "void CustomerForgotPasswordSecurityTokenImpl.setToken(String)",
      "void CustomerForgotPasswordSecurityTokenImpl.setTokenUsedDate(Date)",
      "void CustomerForgotPasswordSecurityTokenImpl.setTokenUsedFlag(boolean)"})
  public void testGettersAndSetters() {
    // Arrange and Act
    CustomerForgotPasswordSecurityTokenImpl actualCustomerForgotPasswordSecurityTokenImpl = new CustomerForgotPasswordSecurityTokenImpl();
    Date createDate = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    actualCustomerForgotPasswordSecurityTokenImpl.setCreateDate(createDate);
    actualCustomerForgotPasswordSecurityTokenImpl.setCustomerId(1L);
    actualCustomerForgotPasswordSecurityTokenImpl.setToken("ABC123");
    Date tokenUsedDate = Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
    actualCustomerForgotPasswordSecurityTokenImpl.setTokenUsedDate(tokenUsedDate);
    actualCustomerForgotPasswordSecurityTokenImpl.setTokenUsedFlag(true);
    Date actualCreateDate = actualCustomerForgotPasswordSecurityTokenImpl.getCreateDate();
    Long actualCustomerId = actualCustomerForgotPasswordSecurityTokenImpl.getCustomerId();
    String actualToken = actualCustomerForgotPasswordSecurityTokenImpl.getToken();
    Date actualTokenUsedDate = actualCustomerForgotPasswordSecurityTokenImpl.getTokenUsedDate();
    boolean actualIsTokenUsedFlagResult = actualCustomerForgotPasswordSecurityTokenImpl.isTokenUsedFlag();

    // Assert
    assertEquals("ABC123", actualToken);
    assertEquals(1L, actualCustomerId.longValue());
    assertTrue(actualIsTokenUsedFlagResult);
    assertSame(createDate, actualCreateDate);
    assertSame(tokenUsedDate, actualTokenUsedDate);
  }
}
