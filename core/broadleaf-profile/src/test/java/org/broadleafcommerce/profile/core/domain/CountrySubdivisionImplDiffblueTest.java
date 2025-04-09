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
public class CountrySubdivisionImplDiffblueTest {
  @Autowired
  private CountrySubdivisionImpl countrySubdivisionImpl;

  /**
   * Test {@link CountrySubdivisionImpl#getName()}.
   * <p>
   * Method under test: {@link CountrySubdivisionImpl#getName()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"String CountrySubdivisionImpl.getName()"})
  public void testGetName() {
    // Arrange, Act and Assert
    assertNull((new CountrySubdivisionImpl()).getName());
  }

  /**
   * Test {@link CountrySubdivisionImpl#getMainEntityName()}.
   * <p>
   * Method under test: {@link CountrySubdivisionImpl#getMainEntityName()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"String CountrySubdivisionImpl.getMainEntityName()"})
  public void testGetMainEntityName() {
    // Arrange, Act and Assert
    assertNull((new CountrySubdivisionImpl()).getMainEntityName());
  }

  /**
   * Test getters and setters.
   * <p>
   * Methods under test:
   * <ul>
   *   <li>default or parameterless constructor of {@link CountrySubdivisionImpl}
   *   <li>{@link CountrySubdivisionImpl#setAbbreviation(String)}
   *   <li>{@link CountrySubdivisionImpl#setAlternateAbbreviation(String)}
   *   <li>{@link CountrySubdivisionImpl#setCategory(CountrySubdivisionCategory)}
   *   <li>{@link CountrySubdivisionImpl#setCountry(Country)}
   *   <li>{@link CountrySubdivisionImpl#setName(String)}
   *   <li>{@link CountrySubdivisionImpl#getAbbreviation()}
   *   <li>{@link CountrySubdivisionImpl#getAlternateAbbreviation()}
   *   <li>{@link CountrySubdivisionImpl#getCategory()}
   *   <li>{@link CountrySubdivisionImpl#getCountry()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CountrySubdivisionImpl.<init>()", "String CountrySubdivisionImpl.getAbbreviation()",
      "String CountrySubdivisionImpl.getAlternateAbbreviation()",
      "CountrySubdivisionCategory CountrySubdivisionImpl.getCategory()", "Country CountrySubdivisionImpl.getCountry()",
      "void CountrySubdivisionImpl.setAbbreviation(String)",
      "void CountrySubdivisionImpl.setAlternateAbbreviation(String)",
      "void CountrySubdivisionImpl.setCategory(CountrySubdivisionCategory)",
      "void CountrySubdivisionImpl.setCountry(Country)", "void CountrySubdivisionImpl.setName(String)"})
  public void testGettersAndSetters() {
    // Arrange and Act
    CountrySubdivisionImpl actualCountrySubdivisionImpl = new CountrySubdivisionImpl();
    actualCountrySubdivisionImpl.setAbbreviation("US-TX");
    actualCountrySubdivisionImpl.setAlternateAbbreviation("TX");
    CountrySubdivisionCategoryImpl category = new CountrySubdivisionCategoryImpl();
    actualCountrySubdivisionImpl.setCategory(category);
    CountryImpl country = new CountryImpl();
    actualCountrySubdivisionImpl.setCountry(country);
    actualCountrySubdivisionImpl.setName("Texas");
    String actualAbbreviation = actualCountrySubdivisionImpl.getAbbreviation();
    String actualAlternateAbbreviation = actualCountrySubdivisionImpl.getAlternateAbbreviation();
    CountrySubdivisionCategory actualCategory = actualCountrySubdivisionImpl.getCategory();

    // Assert
    assertEquals("TX", actualAlternateAbbreviation);
    assertEquals("US-TX", actualAbbreviation);
    assertSame(country, actualCountrySubdivisionImpl.getCountry());
    assertSame(category, actualCategory);
  }
}
