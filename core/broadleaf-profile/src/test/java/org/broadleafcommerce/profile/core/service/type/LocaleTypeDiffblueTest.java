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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class LocaleTypeDiffblueTest {
  /**
   * Test {@link LocaleType#getInstance(String)}.
   * <p>
   * Method under test: {@link LocaleType#getInstance(String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"LocaleType LocaleType.getInstance(String)"})
  public void testGetInstance() throws MissingResourceException {
    // Arrange and Act
    LocaleType actualInstance = LocaleType.getInstance("en");

    // Assert
    Locale locale = actualInstance.getLocale();
    assertEquals("", locale.getDisplayScript());
    assertEquals("", locale.getDisplayVariant());
    assertEquals("", locale.getScript());
    assertEquals("", locale.getVariant());
    assertEquals("English (United Kingdom)", locale.getDisplayName());
    assertEquals("English", locale.getDisplayLanguage());
    assertEquals("GB", locale.getCountry());
    assertEquals("GBR", locale.getISO3Country());
    assertEquals("United Kingdom", locale.getDisplayCountry());
    assertEquals("en", locale.getLanguage());
    assertEquals("en", actualInstance.getFriendlyType());
    assertEquals("en", actualInstance.getType());
    assertEquals("eng", locale.getISO3Language());
    assertFalse(locale.hasExtensions());
    Set<Character> extensionKeys = locale.getExtensionKeys();
    assertTrue(extensionKeys.isEmpty());
    assertSame(extensionKeys, locale.getUnicodeLocaleAttributes());
    assertSame(extensionKeys, locale.getUnicodeLocaleKeys());
  }

  /**
   * Test getters and setters.
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link LocaleType#LocaleType()}
   *   <li>{@link LocaleType#getFriendlyType()}
   *   <li>{@link LocaleType#getLocale()}
   *   <li>{@link LocaleType#getType()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void LocaleType.<init>()", "String LocaleType.getFriendlyType()", "Locale LocaleType.getLocale()",
      "String LocaleType.getType()"})
  public void testGettersAndSetters() {
    // Arrange and Act
    LocaleType actualLocaleType = new LocaleType();
    String actualFriendlyType = actualLocaleType.getFriendlyType();
    Locale actualLocale = actualLocaleType.getLocale();

    // Assert
    assertNull(actualFriendlyType);
    assertNull(actualLocaleType.getType());
    assertNull(actualLocale);
  }

  /**
   * Test {@link LocaleType#LocaleType(String, String, Locale)}.
   * <ul>
   *   <li>When Default.</li>
   *   <li>Then return Type is {@code en}.</li>
   * </ul>
   * <p>
   * Method under test: {@link LocaleType#LocaleType(String, String, Locale)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void LocaleType.<init>(String, String, Locale)"})
  public void testNewLocaleType_whenDefault_thenReturnTypeIsEn() {
    // Arrange
    Locale locale = Locale.getDefault();

    // Act
    LocaleType actualLocaleType = new LocaleType("en", "en", locale);

    // Assert
    assertEquals("en", actualLocaleType.getFriendlyType());
    assertEquals("en", actualLocaleType.getType());
    Locale expectedLocale = locale.UK;
    assertSame(expectedLocale, actualLocaleType.getLocale());
  }

  /**
   * Test {@link LocaleType#LocaleType(String, String, Locale)}.
   * <ul>
   *   <li>When {@code FR}.</li>
   *   <li>Then return Type is {@code FR}.</li>
   * </ul>
   * <p>
   * Method under test: {@link LocaleType#LocaleType(String, String, Locale)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void LocaleType.<init>(String, String, Locale)"})
  public void testNewLocaleType_whenFr_thenReturnTypeIsFr() {
    // Arrange
    Locale locale = Locale.getDefault();

    // Act
    LocaleType actualLocaleType = new LocaleType("FR", "en", locale);

    // Assert
    assertEquals("FR", actualLocaleType.getType());
    assertEquals("en", actualLocaleType.getFriendlyType());
    Locale expectedLocale = locale.UK;
    assertSame(expectedLocale, actualLocaleType.getLocale());
  }

  /**
   * Test {@link LocaleType#equals(Object)}, and {@link LocaleType#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link LocaleType#equals(Object)}
   *   <li>{@link LocaleType#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean LocaleType.equals(Object)", "int LocaleType.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual() {
    // Arrange
    LocaleType localeType = LocaleType.CANADA;
    LocaleType localeType2 = LocaleType.CANADA;

    // Act and Assert
    assertEquals(localeType, localeType2);
    int expectedHashCodeResult = localeType.hashCode();
    assertEquals(expectedHashCodeResult, localeType2.hashCode());
  }

  /**
   * Test {@link LocaleType#equals(Object)}, and {@link LocaleType#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link LocaleType#equals(Object)}
   *   <li>{@link LocaleType#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean LocaleType.equals(Object)", "int LocaleType.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual2() {
    // Arrange
    LocaleType localeType = new LocaleType();
    LocaleType localeType2 = new LocaleType();

    // Act and Assert
    assertEquals(localeType, localeType2);
    int expectedHashCodeResult = localeType.hashCode();
    assertEquals(expectedHashCodeResult, localeType2.hashCode());
  }

  /**
   * Test {@link LocaleType#equals(Object)}, and {@link LocaleType#hashCode()}.
   * <ul>
   *   <li>When other is same.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link LocaleType#equals(Object)}
   *   <li>{@link LocaleType#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean LocaleType.equals(Object)", "int LocaleType.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual() {
    // Arrange
    LocaleType localeType = LocaleType.CANADA;

    // Act and Assert
    assertEquals(localeType, localeType);
    int expectedHashCodeResult = localeType.hashCode();
    assertEquals(expectedHashCodeResult, localeType.hashCode());
  }

  /**
   * Test {@link LocaleType#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link LocaleType#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean LocaleType.equals(Object)", "int LocaleType.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual() {
    // Arrange, Act and Assert
    assertNotEquals(LocaleType.CANADA_FRENCH, LocaleType.CANADA);
  }

  /**
   * Test {@link LocaleType#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link LocaleType#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean LocaleType.equals(Object)", "int LocaleType.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual2() {
    // Arrange, Act and Assert
    assertNotEquals(new LocaleType(), LocaleType.CANADA);
  }

  /**
   * Test {@link LocaleType#equals(Object)}.
   * <ul>
   *   <li>When other is {@code null}.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link LocaleType#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean LocaleType.equals(Object)", "int LocaleType.hashCode()"})
  public void testEquals_whenOtherIsNull_thenReturnNotEqual() {
    // Arrange, Act and Assert
    assertNotEquals(LocaleType.CANADA, null);
  }

  /**
   * Test {@link LocaleType#equals(Object)}.
   * <ul>
   *   <li>When other is wrong type.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link LocaleType#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean LocaleType.equals(Object)", "int LocaleType.hashCode()"})
  public void testEquals_whenOtherIsWrongType_thenReturnNotEqual() {
    // Arrange, Act and Assert
    assertNotEquals(LocaleType.CANADA, "Different type to LocaleType");
  }
}
