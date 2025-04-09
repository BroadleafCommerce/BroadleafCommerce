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
public class CountrySubdivisionCategoryImplDiffblueTest {
  @Autowired
  private CountrySubdivisionCategoryImpl countrySubdivisionCategoryImpl;

  /**
   * Test {@link CountrySubdivisionCategoryImpl#getName()}.
   * <p>
   * Method under test: {@link CountrySubdivisionCategoryImpl#getName()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"String CountrySubdivisionCategoryImpl.getName()"})
  public void testGetName() {
    // Arrange, Act and Assert
    assertNull((new CountrySubdivisionCategoryImpl()).getName());
  }

  /**
   * Test {@link CountrySubdivisionCategoryImpl#getMainEntityName()}.
   * <p>
   * Method under test: {@link CountrySubdivisionCategoryImpl#getMainEntityName()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"String CountrySubdivisionCategoryImpl.getMainEntityName()"})
  public void testGetMainEntityName() {
    // Arrange, Act and Assert
    assertNull((new CountrySubdivisionCategoryImpl()).getMainEntityName());
  }

  /**
   * Test getters and setters.
   * <p>
   * Methods under test:
   * <ul>
   *   <li>default or parameterless constructor of {@link CountrySubdivisionCategoryImpl}
   *   <li>{@link CountrySubdivisionCategoryImpl#setId(Long)}
   *   <li>{@link CountrySubdivisionCategoryImpl#setName(String)}
   *   <li>{@link CountrySubdivisionCategoryImpl#getId()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CountrySubdivisionCategoryImpl.<init>()", "Long CountrySubdivisionCategoryImpl.getId()",
      "void CountrySubdivisionCategoryImpl.setId(Long)", "void CountrySubdivisionCategoryImpl.setName(String)"})
  public void testGettersAndSetters() {
    // Arrange and Act
    CountrySubdivisionCategoryImpl actualCountrySubdivisionCategoryImpl = new CountrySubdivisionCategoryImpl();
    actualCountrySubdivisionCategoryImpl.setId(1L);
    actualCountrySubdivisionCategoryImpl.setName("Name");

    // Assert
    assertEquals(1L, actualCountrySubdivisionCategoryImpl.getId().longValue());
  }
}
