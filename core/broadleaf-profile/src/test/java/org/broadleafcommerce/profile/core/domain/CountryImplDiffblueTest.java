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
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = {"/bl-profile-applicationContext-entity.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class CountryImplDiffblueTest {
  @Autowired
  private CountryImpl countryImpl;

  /**
   * Test {@link CountryImpl#getName()}.
   * <p>
   * Method under test: {@link CountryImpl#getName()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"String CountryImpl.getName()"})
  public void testGetName() {
    // Arrange, Act and Assert
    assertNull((new CountryImpl()).getName());
  }

  /**
   * Test {@link CountryImpl#equals(Object)}, and {@link CountryImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CountryImpl#equals(Object)}
   *   <li>{@link CountryImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CountryImpl.equals(Object)", "int CountryImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual() {
    // Arrange
    CountryImpl countryImpl = new CountryImpl();
    countryImpl.setAbbreviation("US");
    countryImpl.setName("United States");

    CountryImpl countryImpl2 = new CountryImpl();
    countryImpl2.setAbbreviation("US");
    countryImpl2.setName("United States");

    // Act and Assert
    assertEquals(countryImpl, countryImpl2);
    int expectedHashCodeResult = countryImpl.hashCode();
    assertEquals(expectedHashCodeResult, countryImpl2.hashCode());
  }

  /**
   * Test {@link CountryImpl#equals(Object)}, and {@link CountryImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CountryImpl#equals(Object)}
   *   <li>{@link CountryImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CountryImpl.equals(Object)", "int CountryImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual2() {
    // Arrange
    CountryImpl countryImpl = new CountryImpl();
    countryImpl.setAbbreviation(null);
    countryImpl.setName("United States");

    CountryImpl countryImpl2 = new CountryImpl();
    countryImpl2.setAbbreviation(null);
    countryImpl2.setName("United States");

    // Act and Assert
    assertEquals(countryImpl, countryImpl2);
    int expectedHashCodeResult = countryImpl.hashCode();
    assertEquals(expectedHashCodeResult, countryImpl2.hashCode());
  }

  /**
   * Test {@link CountryImpl#equals(Object)}, and {@link CountryImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CountryImpl#equals(Object)}
   *   <li>{@link CountryImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CountryImpl.equals(Object)", "int CountryImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual3() {
    // Arrange
    CountryImpl countryImpl = new CountryImpl();
    countryImpl.setAbbreviation("US");
    countryImpl.setName(null);

    CountryImpl countryImpl2 = new CountryImpl();
    countryImpl2.setAbbreviation("US");
    countryImpl2.setName(null);

    // Act and Assert
    assertEquals(countryImpl, countryImpl2);
    int expectedHashCodeResult = countryImpl.hashCode();
    assertEquals(expectedHashCodeResult, countryImpl2.hashCode());
  }

  /**
   * Test {@link CountryImpl#equals(Object)}, and {@link CountryImpl#hashCode()}.
   * <ul>
   *   <li>When other is same.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CountryImpl#equals(Object)}
   *   <li>{@link CountryImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CountryImpl.equals(Object)", "int CountryImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual() {
    // Arrange
    CountryImpl countryImpl = new CountryImpl();
    countryImpl.setAbbreviation("US");
    countryImpl.setName("United States");

    // Act and Assert
    assertEquals(countryImpl, countryImpl);
    int expectedHashCodeResult = countryImpl.hashCode();
    assertEquals(expectedHashCodeResult, countryImpl.hashCode());
  }

  /**
   * Test {@link CountryImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CountryImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CountryImpl.equals(Object)", "int CountryImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual() {
    // Arrange
    CountryImpl countryImpl = new CountryImpl();
    countryImpl.setAbbreviation("GB");
    countryImpl.setName("United States");

    CountryImpl countryImpl2 = new CountryImpl();
    countryImpl2.setAbbreviation("US");
    countryImpl2.setName("United States");

    // Act and Assert
    assertNotEquals(countryImpl, countryImpl2);
  }

  /**
   * Test {@link CountryImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CountryImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CountryImpl.equals(Object)", "int CountryImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual2() {
    // Arrange
    CountryImpl countryImpl = new CountryImpl();
    countryImpl.setAbbreviation(null);
    countryImpl.setName("United States");

    CountryImpl countryImpl2 = new CountryImpl();
    countryImpl2.setAbbreviation("US");
    countryImpl2.setName("United States");

    // Act and Assert
    assertNotEquals(countryImpl, countryImpl2);
  }

  /**
   * Test {@link CountryImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CountryImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CountryImpl.equals(Object)", "int CountryImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual3() {
    // Arrange
    CountryImpl countryImpl = new CountryImpl();
    countryImpl.setAbbreviation("US");
    countryImpl.setName("United Kingdom");

    CountryImpl countryImpl2 = new CountryImpl();
    countryImpl2.setAbbreviation("US");
    countryImpl2.setName("United States");

    // Act and Assert
    assertNotEquals(countryImpl, countryImpl2);
  }

  /**
   * Test {@link CountryImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CountryImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CountryImpl.equals(Object)", "int CountryImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual4() {
    // Arrange
    CountryImpl countryImpl = new CountryImpl();
    countryImpl.setAbbreviation("US");
    countryImpl.setName(null);

    CountryImpl countryImpl2 = new CountryImpl();
    countryImpl2.setAbbreviation("US");
    countryImpl2.setName("United States");

    // Act and Assert
    assertNotEquals(countryImpl, countryImpl2);
  }

  /**
   * Test {@link CountryImpl#equals(Object)}.
   * <ul>
   *   <li>When other is {@code null}.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CountryImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CountryImpl.equals(Object)", "int CountryImpl.hashCode()"})
  public void testEquals_whenOtherIsNull_thenReturnNotEqual() {
    // Arrange
    CountryImpl countryImpl = new CountryImpl();
    countryImpl.setAbbreviation("US");
    countryImpl.setName("United States");

    // Act and Assert
    assertNotEquals(countryImpl, null);
  }

  /**
   * Test {@link CountryImpl#equals(Object)}.
   * <ul>
   *   <li>When other is wrong type.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CountryImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CountryImpl.equals(Object)", "int CountryImpl.hashCode()"})
  public void testEquals_whenOtherIsWrongType_thenReturnNotEqual() {
    // Arrange
    CountryImpl countryImpl = new CountryImpl();
    countryImpl.setAbbreviation("US");
    countryImpl.setName("United States");

    // Act and Assert
    assertNotEquals(countryImpl, "Different type to CountryImpl");
  }

  /**
   * Test {@link CountryImpl#getMainEntityName()}.
   * <p>
   * Method under test: {@link CountryImpl#getMainEntityName()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"String CountryImpl.getMainEntityName()"})
  public void testGetMainEntityName() {
    // Arrange, Act and Assert
    assertNull((new CountryImpl()).getMainEntityName());
  }

  /**
   * Test getters and setters.
   * <p>
   * Methods under test:
   * <ul>
   *   <li>default or parameterless constructor of {@link CountryImpl}
   *   <li>{@link CountryImpl#setAbbreviation(String)}
   *   <li>{@link CountryImpl#setName(String)}
   *   <li>{@link CountryImpl#getAbbreviation()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CountryImpl.<init>()", "String CountryImpl.getAbbreviation()",
      "void CountryImpl.setAbbreviation(String)", "void CountryImpl.setName(String)"})
  public void testGettersAndSetters() {
    // Arrange and Act
    CountryImpl actualCountryImpl = new CountryImpl();
    actualCountryImpl.setAbbreviation("US");
    actualCountryImpl.setName("United States");

    // Assert
    assertEquals("US", actualCountryImpl.getAbbreviation());
  }
}
