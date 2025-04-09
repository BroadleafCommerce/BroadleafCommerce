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
package org.broadleafcommerce.profile.core.dao;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import javax.persistence.NoResultException;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.profile.core.domain.CountryImpl;
import org.broadleafcommerce.profile.core.domain.CountrySubdivision;
import org.broadleafcommerce.profile.core.domain.CountrySubdivisionCategoryImpl;
import org.broadleafcommerce.profile.core.domain.CountrySubdivisionImpl;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CountrySubdivisionDaoImplDiffblueTest {
  @InjectMocks
  private CountrySubdivisionDaoImpl countrySubdivisionDaoImpl;

  @Mock
  private EntityConfiguration entityConfiguration;

  /**
   * Test {@link CountrySubdivisionDaoImpl#findSubdivisionByAbbreviation(String)}.
   * <ul>
   *   <li>When {@code null}.</li>
   *   <li>Then return {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CountrySubdivisionDaoImpl#findSubdivisionByAbbreviation(String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CountrySubdivision CountrySubdivisionDaoImpl.findSubdivisionByAbbreviation(String)"})
  public void testFindSubdivisionByAbbreviation_whenNull_thenReturnNull() {
    // Arrange, Act and Assert
    assertNull(countrySubdivisionDaoImpl.findSubdivisionByAbbreviation(null));
  }

  /**
   * Test {@link CountrySubdivisionDaoImpl#create()}.
   * <ul>
   *   <li>Then return {@link CountrySubdivisionImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link CountrySubdivisionDaoImpl#create()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CountrySubdivision CountrySubdivisionDaoImpl.create()"})
  public void testCreate_thenReturnCountrySubdivisionImpl() {
    // Arrange
    CountrySubdivisionImpl countrySubdivisionImpl = new CountrySubdivisionImpl();
    countrySubdivisionImpl.setAbbreviation("US-TX");
    countrySubdivisionImpl.setAlternateAbbreviation("TX");
    countrySubdivisionImpl.setCategory(new CountrySubdivisionCategoryImpl());
    countrySubdivisionImpl.setCountry(new CountryImpl());
    countrySubdivisionImpl.setName("Texas");
    when(entityConfiguration.createEntityInstance(Mockito.<String>any())).thenReturn(countrySubdivisionImpl);

    // Act
    CountrySubdivision actualCreateResult = countrySubdivisionDaoImpl.create();

    // Assert
    verify(entityConfiguration)
        .createEntityInstance(eq("org.broadleafcommerce.profile.core.domain.CountrySubdivision"));
    assertSame(countrySubdivisionImpl, actualCreateResult);
  }

  /**
   * Test {@link CountrySubdivisionDaoImpl#create()}.
   * <ul>
   *   <li>Then throw {@link NoResultException}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CountrySubdivisionDaoImpl#create()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CountrySubdivision CountrySubdivisionDaoImpl.create()"})
  public void testCreate_thenThrowNoResultException() {
    // Arrange
    when(entityConfiguration.createEntityInstance(Mockito.<String>any()))
        .thenThrow(new NoResultException("An error occurred"));

    // Act and Assert
    assertThrows(NoResultException.class, () -> countrySubdivisionDaoImpl.create());
    verify(entityConfiguration)
        .createEntityInstance(eq("org.broadleafcommerce.profile.core.domain.CountrySubdivision"));
  }
}
