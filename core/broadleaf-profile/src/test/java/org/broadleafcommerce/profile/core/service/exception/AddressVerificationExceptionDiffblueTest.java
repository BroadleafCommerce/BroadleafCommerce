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
package org.broadleafcommerce.profile.core.service.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class AddressVerificationExceptionDiffblueTest {
  /**
   * Test {@link AddressVerificationException#AddressVerificationException(Throwable)}.
   * <ul>
   *   <li>Then return Message is {@code Throwable}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressVerificationException#AddressVerificationException(Throwable)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void AddressVerificationException.<init>()", "void AddressVerificationException.<init>(String)",
      "void AddressVerificationException.<init>(String, Throwable)",
      "void AddressVerificationException.<init>(Throwable)"})
  public void testNewAddressVerificationException_thenReturnMessageIsJavaLangThrowable() {
    // Arrange
    Throwable arg0 = new Throwable();

    // Act
    AddressVerificationException actualAddressVerificationException = new AddressVerificationException(arg0);

    // Assert
    assertEquals("java.lang.Throwable", actualAddressVerificationException.getMessage());
    assertEquals(0, actualAddressVerificationException.getSuppressed().length);
    assertSame(arg0, actualAddressVerificationException.getCause());
  }

  /**
   * Test {@link AddressVerificationException#AddressVerificationException()}.
   * <ul>
   *   <li>Then return Message is {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressVerificationException#AddressVerificationException()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void AddressVerificationException.<init>()", "void AddressVerificationException.<init>(String)",
      "void AddressVerificationException.<init>(String, Throwable)",
      "void AddressVerificationException.<init>(Throwable)"})
  public void testNewAddressVerificationException_thenReturnMessageIsNull() {
    // Arrange and Act
    AddressVerificationException actualAddressVerificationException = new AddressVerificationException();

    // Assert
    assertNull(actualAddressVerificationException.getMessage());
    assertNull(actualAddressVerificationException.getCause());
    assertEquals(0, actualAddressVerificationException.getSuppressed().length);
  }

  /**
   * Test {@link AddressVerificationException#AddressVerificationException(String)}.
   * <ul>
   *   <li>When {@code Arg0}.</li>
   *   <li>Then return Message is {@code Arg0}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressVerificationException#AddressVerificationException(String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void AddressVerificationException.<init>()", "void AddressVerificationException.<init>(String)",
      "void AddressVerificationException.<init>(String, Throwable)",
      "void AddressVerificationException.<init>(Throwable)"})
  public void testNewAddressVerificationException_whenArg0_thenReturnMessageIsArg0() {
    // Arrange and Act
    AddressVerificationException actualAddressVerificationException = new AddressVerificationException("Arg0");

    // Assert
    assertEquals("Arg0", actualAddressVerificationException.getMessage());
    assertNull(actualAddressVerificationException.getCause());
    assertEquals(0, actualAddressVerificationException.getSuppressed().length);
  }

  /**
   * Test {@link AddressVerificationException#AddressVerificationException(String, Throwable)}.
   * <ul>
   *   <li>When {@code Arg0}.</li>
   *   <li>Then return Message is {@code Arg0}.</li>
   * </ul>
   * <p>
   * Method under test: {@link AddressVerificationException#AddressVerificationException(String, Throwable)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void AddressVerificationException.<init>()", "void AddressVerificationException.<init>(String)",
      "void AddressVerificationException.<init>(String, Throwable)",
      "void AddressVerificationException.<init>(Throwable)"})
  public void testNewAddressVerificationException_whenArg0_thenReturnMessageIsArg02() {
    // Arrange
    Throwable arg1 = new Throwable();

    // Act
    AddressVerificationException actualAddressVerificationException = new AddressVerificationException("Arg0", arg1);

    // Assert
    assertEquals("Arg0", actualAddressVerificationException.getMessage());
    assertEquals(0, actualAddressVerificationException.getSuppressed().length);
    assertSame(arg1, actualAddressVerificationException.getCause());
  }
}
