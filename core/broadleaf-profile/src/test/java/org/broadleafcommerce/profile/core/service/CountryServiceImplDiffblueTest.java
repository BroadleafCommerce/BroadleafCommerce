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
package org.broadleafcommerce.profile.core.service;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.util.ArrayList;
import java.util.List;
import org.broadleafcommerce.profile.core.dao.CountryDao;
import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.CountryImpl;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CountryServiceImplDiffblueTest {
  @Mock
  private CountryDao countryDao;

  @InjectMocks
  private CountryServiceImpl countryServiceImpl;

  /**
   * Test {@link CountryServiceImpl#findCountries()}.
   * <p>
   * Method under test: {@link CountryServiceImpl#findCountries()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"List CountryServiceImpl.findCountries()"})
  public void testFindCountries() {
    // Arrange
    when(countryDao.findCountries()).thenReturn(new ArrayList<>());

    // Act
    List<Country> actualFindCountriesResult = countryServiceImpl.findCountries();

    // Assert
    verify(countryDao).findCountries();
    assertTrue(actualFindCountriesResult.isEmpty());
  }

  /**
   * Test {@link CountryServiceImpl#findCountryByAbbreviation(String)}.
   * <p>
   * Method under test: {@link CountryServiceImpl#findCountryByAbbreviation(String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Country CountryServiceImpl.findCountryByAbbreviation(String)"})
  public void testFindCountryByAbbreviation() {
    // Arrange
    CountryImpl countryImpl = new CountryImpl();
    when(countryDao.findCountryByAbbreviation(Mockito.<String>any())).thenReturn(countryImpl);

    // Act
    Country actualFindCountryByAbbreviationResult = countryServiceImpl.findCountryByAbbreviation("US");

    // Assert
    verify(countryDao).findCountryByAbbreviation(eq("US"));
    assertSame(countryImpl, actualFindCountryByAbbreviationResult);
  }

  /**
   * Test {@link CountryServiceImpl#save(Country)}.
   * <p>
   * Method under test: {@link CountryServiceImpl#save(Country)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Country CountryServiceImpl.save(Country)"})
  public void testSave() {
    // Arrange
    CountryImpl countryImpl = new CountryImpl();
    when(countryDao.save(Mockito.<Country>any())).thenReturn(countryImpl);

    // Act
    Country actualSaveResult = countryServiceImpl.save(new CountryImpl());

    // Assert
    verify(countryDao).save(isA(Country.class));
    assertSame(countryImpl, actualSaveResult);
  }
}
