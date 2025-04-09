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
import static org.junit.Assert.assertTrue;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class PhoneImplDiffblueTest {
  /**
   * Test {@link PhoneImpl#equals(Object)}, and {@link PhoneImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link PhoneImpl#equals(Object)}
   *   <li>{@link PhoneImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean PhoneImpl.equals(Object)", "int PhoneImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual() {
    // Arrange
    PhoneImpl phoneImpl = new PhoneImpl();
    phoneImpl.setActive(true);
    phoneImpl.setCountryCode("GB");
    phoneImpl.setDefault(true);
    phoneImpl.setExtension("Extension");
    phoneImpl.setId(1L);
    phoneImpl.setPhoneNumber("6625550144");

    PhoneImpl phoneImpl2 = new PhoneImpl();
    phoneImpl2.setActive(true);
    phoneImpl2.setCountryCode("GB");
    phoneImpl2.setDefault(true);
    phoneImpl2.setExtension("Extension");
    phoneImpl2.setId(1L);
    phoneImpl2.setPhoneNumber("6625550144");

    // Act and Assert
    assertEquals(phoneImpl, phoneImpl2);
    int expectedHashCodeResult = phoneImpl.hashCode();
    assertEquals(expectedHashCodeResult, phoneImpl2.hashCode());
  }

  /**
   * Test {@link PhoneImpl#equals(Object)}, and {@link PhoneImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link PhoneImpl#equals(Object)}
   *   <li>{@link PhoneImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean PhoneImpl.equals(Object)", "int PhoneImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual2() {
    // Arrange
    PhoneImpl phoneImpl = new PhoneImpl();
    phoneImpl.setActive(true);
    phoneImpl.setCountryCode("GB");
    phoneImpl.setDefault(true);
    phoneImpl.setExtension("Extension");
    phoneImpl.setId(null);
    phoneImpl.setPhoneNumber("6625550144");

    PhoneImpl phoneImpl2 = new PhoneImpl();
    phoneImpl2.setActive(true);
    phoneImpl2.setCountryCode("GB");
    phoneImpl2.setDefault(true);
    phoneImpl2.setExtension("Extension");
    phoneImpl2.setId(1L);
    phoneImpl2.setPhoneNumber("6625550144");

    // Act and Assert
    assertEquals(phoneImpl, phoneImpl2);
    int expectedHashCodeResult = phoneImpl.hashCode();
    assertEquals(expectedHashCodeResult, phoneImpl2.hashCode());
  }

  /**
   * Test {@link PhoneImpl#equals(Object)}, and {@link PhoneImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link PhoneImpl#equals(Object)}
   *   <li>{@link PhoneImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean PhoneImpl.equals(Object)", "int PhoneImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual3() {
    // Arrange
    PhoneImpl phoneImpl = new PhoneImpl();
    phoneImpl.setActive(true);
    phoneImpl.setCountryCode("GB");
    phoneImpl.setDefault(true);
    phoneImpl.setExtension("Extension");
    phoneImpl.setId(1L);
    phoneImpl.setPhoneNumber("6625550144");

    PhoneImpl phoneImpl2 = new PhoneImpl();
    phoneImpl2.setActive(true);
    phoneImpl2.setCountryCode("GB");
    phoneImpl2.setDefault(true);
    phoneImpl2.setExtension("Extension");
    phoneImpl2.setId(null);
    phoneImpl2.setPhoneNumber("6625550144");

    // Act and Assert
    assertEquals(phoneImpl, phoneImpl2);
    int expectedHashCodeResult = phoneImpl.hashCode();
    assertEquals(expectedHashCodeResult, phoneImpl2.hashCode());
  }

  /**
   * Test {@link PhoneImpl#equals(Object)}, and {@link PhoneImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link PhoneImpl#equals(Object)}
   *   <li>{@link PhoneImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean PhoneImpl.equals(Object)", "int PhoneImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual4() {
    // Arrange
    PhoneImpl phoneImpl = new PhoneImpl();
    phoneImpl.setActive(true);
    phoneImpl.setCountryCode(null);
    phoneImpl.setDefault(true);
    phoneImpl.setExtension("Extension");
    phoneImpl.setId(null);
    phoneImpl.setPhoneNumber("6625550144");

    PhoneImpl phoneImpl2 = new PhoneImpl();
    phoneImpl2.setActive(true);
    phoneImpl2.setCountryCode(null);
    phoneImpl2.setDefault(true);
    phoneImpl2.setExtension("Extension");
    phoneImpl2.setId(1L);
    phoneImpl2.setPhoneNumber("6625550144");

    // Act and Assert
    assertEquals(phoneImpl, phoneImpl2);
    int expectedHashCodeResult = phoneImpl.hashCode();
    assertEquals(expectedHashCodeResult, phoneImpl2.hashCode());
  }

  /**
   * Test {@link PhoneImpl#equals(Object)}, and {@link PhoneImpl#hashCode()}.
   * <ul>
   *   <li>When other is same.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link PhoneImpl#equals(Object)}
   *   <li>{@link PhoneImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean PhoneImpl.equals(Object)", "int PhoneImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual() {
    // Arrange
    PhoneImpl phoneImpl = new PhoneImpl();
    phoneImpl.setActive(true);
    phoneImpl.setCountryCode("GB");
    phoneImpl.setDefault(true);
    phoneImpl.setExtension("Extension");
    phoneImpl.setId(1L);
    phoneImpl.setPhoneNumber("6625550144");

    // Act and Assert
    assertEquals(phoneImpl, phoneImpl);
    int expectedHashCodeResult = phoneImpl.hashCode();
    assertEquals(expectedHashCodeResult, phoneImpl.hashCode());
  }

  /**
   * Test {@link PhoneImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link PhoneImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean PhoneImpl.equals(Object)", "int PhoneImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual() {
    // Arrange
    PhoneImpl phoneImpl = new PhoneImpl();
    phoneImpl.setActive(true);
    phoneImpl.setCountryCode("GB");
    phoneImpl.setDefault(true);
    phoneImpl.setExtension("Extension");
    phoneImpl.setId(2L);
    phoneImpl.setPhoneNumber("6625550144");

    PhoneImpl phoneImpl2 = new PhoneImpl();
    phoneImpl2.setActive(true);
    phoneImpl2.setCountryCode("GB");
    phoneImpl2.setDefault(true);
    phoneImpl2.setExtension("Extension");
    phoneImpl2.setId(1L);
    phoneImpl2.setPhoneNumber("6625550144");

    // Act and Assert
    assertNotEquals(phoneImpl, phoneImpl2);
  }

  /**
   * Test {@link PhoneImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link PhoneImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean PhoneImpl.equals(Object)", "int PhoneImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual2() {
    // Arrange
    PhoneImpl phoneImpl = new PhoneImpl();
    phoneImpl.setActive(false);
    phoneImpl.setCountryCode("GB");
    phoneImpl.setDefault(true);
    phoneImpl.setExtension("Extension");
    phoneImpl.setId(null);
    phoneImpl.setPhoneNumber("6625550144");

    PhoneImpl phoneImpl2 = new PhoneImpl();
    phoneImpl2.setActive(true);
    phoneImpl2.setCountryCode("GB");
    phoneImpl2.setDefault(true);
    phoneImpl2.setExtension("Extension");
    phoneImpl2.setId(1L);
    phoneImpl2.setPhoneNumber("6625550144");

    // Act and Assert
    assertNotEquals(phoneImpl, phoneImpl2);
  }

  /**
   * Test {@link PhoneImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link PhoneImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean PhoneImpl.equals(Object)", "int PhoneImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual3() {
    // Arrange
    PhoneImpl phoneImpl = new PhoneImpl();
    phoneImpl.setActive(true);
    phoneImpl.setCountryCode("GBR");
    phoneImpl.setDefault(true);
    phoneImpl.setExtension("Extension");
    phoneImpl.setId(null);
    phoneImpl.setPhoneNumber("6625550144");

    PhoneImpl phoneImpl2 = new PhoneImpl();
    phoneImpl2.setActive(true);
    phoneImpl2.setCountryCode("GB");
    phoneImpl2.setDefault(true);
    phoneImpl2.setExtension("Extension");
    phoneImpl2.setId(1L);
    phoneImpl2.setPhoneNumber("6625550144");

    // Act and Assert
    assertNotEquals(phoneImpl, phoneImpl2);
  }

  /**
   * Test {@link PhoneImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link PhoneImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean PhoneImpl.equals(Object)", "int PhoneImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual4() {
    // Arrange
    PhoneImpl phoneImpl = new PhoneImpl();
    phoneImpl.setActive(true);
    phoneImpl.setCountryCode(null);
    phoneImpl.setDefault(true);
    phoneImpl.setExtension("Extension");
    phoneImpl.setId(null);
    phoneImpl.setPhoneNumber("6625550144");

    PhoneImpl phoneImpl2 = new PhoneImpl();
    phoneImpl2.setActive(true);
    phoneImpl2.setCountryCode("GB");
    phoneImpl2.setDefault(true);
    phoneImpl2.setExtension("Extension");
    phoneImpl2.setId(1L);
    phoneImpl2.setPhoneNumber("6625550144");

    // Act and Assert
    assertNotEquals(phoneImpl, phoneImpl2);
  }

  /**
   * Test {@link PhoneImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link PhoneImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean PhoneImpl.equals(Object)", "int PhoneImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual5() {
    // Arrange
    PhoneImpl phoneImpl = new PhoneImpl();
    phoneImpl.setActive(true);
    phoneImpl.setCountryCode("GB");
    phoneImpl.setDefault(false);
    phoneImpl.setExtension("Extension");
    phoneImpl.setId(null);
    phoneImpl.setPhoneNumber("6625550144");

    PhoneImpl phoneImpl2 = new PhoneImpl();
    phoneImpl2.setActive(true);
    phoneImpl2.setCountryCode("GB");
    phoneImpl2.setDefault(true);
    phoneImpl2.setExtension("Extension");
    phoneImpl2.setId(1L);
    phoneImpl2.setPhoneNumber("6625550144");

    // Act and Assert
    assertNotEquals(phoneImpl, phoneImpl2);
  }

  /**
   * Test {@link PhoneImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link PhoneImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean PhoneImpl.equals(Object)", "int PhoneImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual6() {
    // Arrange
    PhoneImpl phoneImpl = new PhoneImpl();
    phoneImpl.setActive(true);
    phoneImpl.setCountryCode("GB");
    phoneImpl.setDefault(true);
    phoneImpl.setExtension("GB");
    phoneImpl.setId(null);
    phoneImpl.setPhoneNumber("6625550144");

    PhoneImpl phoneImpl2 = new PhoneImpl();
    phoneImpl2.setActive(true);
    phoneImpl2.setCountryCode("GB");
    phoneImpl2.setDefault(true);
    phoneImpl2.setExtension("Extension");
    phoneImpl2.setId(1L);
    phoneImpl2.setPhoneNumber("6625550144");

    // Act and Assert
    assertNotEquals(phoneImpl, phoneImpl2);
  }

  /**
   * Test {@link PhoneImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link PhoneImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean PhoneImpl.equals(Object)", "int PhoneImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual7() {
    // Arrange
    PhoneImpl phoneImpl = new PhoneImpl();
    phoneImpl.setActive(true);
    phoneImpl.setCountryCode("GB");
    phoneImpl.setDefault(true);
    phoneImpl.setExtension(null);
    phoneImpl.setId(null);
    phoneImpl.setPhoneNumber("6625550144");

    PhoneImpl phoneImpl2 = new PhoneImpl();
    phoneImpl2.setActive(true);
    phoneImpl2.setCountryCode("GB");
    phoneImpl2.setDefault(true);
    phoneImpl2.setExtension("Extension");
    phoneImpl2.setId(1L);
    phoneImpl2.setPhoneNumber("6625550144");

    // Act and Assert
    assertNotEquals(phoneImpl, phoneImpl2);
  }

  /**
   * Test {@link PhoneImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link PhoneImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean PhoneImpl.equals(Object)", "int PhoneImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual8() {
    // Arrange
    PhoneImpl phoneImpl = new PhoneImpl();
    phoneImpl.setActive(true);
    phoneImpl.setCountryCode("GB");
    phoneImpl.setDefault(true);
    phoneImpl.setExtension("Extension");
    phoneImpl.setId(null);
    phoneImpl.setPhoneNumber("8605550118");

    PhoneImpl phoneImpl2 = new PhoneImpl();
    phoneImpl2.setActive(true);
    phoneImpl2.setCountryCode("GB");
    phoneImpl2.setDefault(true);
    phoneImpl2.setExtension("Extension");
    phoneImpl2.setId(1L);
    phoneImpl2.setPhoneNumber("6625550144");

    // Act and Assert
    assertNotEquals(phoneImpl, phoneImpl2);
  }

  /**
   * Test {@link PhoneImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link PhoneImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean PhoneImpl.equals(Object)", "int PhoneImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual9() {
    // Arrange
    PhoneImpl phoneImpl = new PhoneImpl();
    phoneImpl.setActive(true);
    phoneImpl.setCountryCode("GB");
    phoneImpl.setDefault(true);
    phoneImpl.setExtension("Extension");
    phoneImpl.setId(null);
    phoneImpl.setPhoneNumber(null);

    PhoneImpl phoneImpl2 = new PhoneImpl();
    phoneImpl2.setActive(true);
    phoneImpl2.setCountryCode("GB");
    phoneImpl2.setDefault(true);
    phoneImpl2.setExtension("Extension");
    phoneImpl2.setId(1L);
    phoneImpl2.setPhoneNumber("6625550144");

    // Act and Assert
    assertNotEquals(phoneImpl, phoneImpl2);
  }

  /**
   * Test {@link PhoneImpl#equals(Object)}.
   * <ul>
   *   <li>When other is {@code null}.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link PhoneImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean PhoneImpl.equals(Object)", "int PhoneImpl.hashCode()"})
  public void testEquals_whenOtherIsNull_thenReturnNotEqual() {
    // Arrange
    PhoneImpl phoneImpl = new PhoneImpl();
    phoneImpl.setActive(true);
    phoneImpl.setCountryCode("GB");
    phoneImpl.setDefault(true);
    phoneImpl.setExtension("Extension");
    phoneImpl.setId(1L);
    phoneImpl.setPhoneNumber("6625550144");

    // Act and Assert
    assertNotEquals(phoneImpl, null);
  }

  /**
   * Test {@link PhoneImpl#equals(Object)}.
   * <ul>
   *   <li>When other is wrong type.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link PhoneImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean PhoneImpl.equals(Object)", "int PhoneImpl.hashCode()"})
  public void testEquals_whenOtherIsWrongType_thenReturnNotEqual() {
    // Arrange
    PhoneImpl phoneImpl = new PhoneImpl();
    phoneImpl.setActive(true);
    phoneImpl.setCountryCode("GB");
    phoneImpl.setDefault(true);
    phoneImpl.setExtension("Extension");
    phoneImpl.setId(1L);
    phoneImpl.setPhoneNumber("6625550144");

    // Act and Assert
    assertNotEquals(phoneImpl, "Different type to PhoneImpl");
  }

  /**
   * Test getters and setters.
   * <p>
   * Methods under test:
   * <ul>
   *   <li>default or parameterless constructor of {@link PhoneImpl}
   *   <li>{@link PhoneImpl#setActive(boolean)}
   *   <li>{@link PhoneImpl#setCountryCode(String)}
   *   <li>{@link PhoneImpl#setDefault(boolean)}
   *   <li>{@link PhoneImpl#setExtension(String)}
   *   <li>{@link PhoneImpl#setId(Long)}
   *   <li>{@link PhoneImpl#setPhoneNumber(String)}
   *   <li>{@link PhoneImpl#getCountryCode()}
   *   <li>{@link PhoneImpl#getExtension()}
   *   <li>{@link PhoneImpl#getId()}
   *   <li>{@link PhoneImpl#getPhoneNumber()}
   *   <li>{@link PhoneImpl#isActive()}
   *   <li>{@link PhoneImpl#isDefault()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void PhoneImpl.<init>()", "String PhoneImpl.getCountryCode()", "String PhoneImpl.getExtension()",
      "Long PhoneImpl.getId()", "String PhoneImpl.getPhoneNumber()", "boolean PhoneImpl.isActive()",
      "boolean PhoneImpl.isDefault()", "void PhoneImpl.setActive(boolean)", "void PhoneImpl.setCountryCode(String)",
      "void PhoneImpl.setDefault(boolean)", "void PhoneImpl.setExtension(String)", "void PhoneImpl.setId(Long)",
      "void PhoneImpl.setPhoneNumber(String)"})
  public void testGettersAndSetters() {
    // Arrange and Act
    PhoneImpl actualPhoneImpl = new PhoneImpl();
    actualPhoneImpl.setActive(true);
    actualPhoneImpl.setCountryCode("GB");
    actualPhoneImpl.setDefault(true);
    actualPhoneImpl.setExtension("Extension");
    actualPhoneImpl.setId(1L);
    actualPhoneImpl.setPhoneNumber("6625550144");
    String actualCountryCode = actualPhoneImpl.getCountryCode();
    String actualExtension = actualPhoneImpl.getExtension();
    Long actualId = actualPhoneImpl.getId();
    String actualPhoneNumber = actualPhoneImpl.getPhoneNumber();
    boolean actualIsActiveResult = actualPhoneImpl.isActive();
    boolean actualIsDefaultResult = actualPhoneImpl.isDefault();

    // Assert
    assertEquals("6625550144", actualPhoneNumber);
    assertEquals("Extension", actualExtension);
    assertEquals("GB", actualCountryCode);
    assertEquals(1L, actualId.longValue());
    assertTrue(actualIsActiveResult);
    assertTrue(actualIsDefaultResult);
  }
}
