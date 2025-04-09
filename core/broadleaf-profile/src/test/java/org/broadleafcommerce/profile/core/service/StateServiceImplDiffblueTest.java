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
import org.broadleafcommerce.profile.core.dao.StateDao;
import org.broadleafcommerce.profile.core.domain.State;
import org.broadleafcommerce.profile.core.domain.StateImpl;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StateServiceImplDiffblueTest {
  @Mock
  private StateDao stateDao;

  @InjectMocks
  private StateServiceImpl stateServiceImpl;

  /**
   * Test {@link StateServiceImpl#findStates()}.
   * <p>
   * Method under test: {@link StateServiceImpl#findStates()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"List StateServiceImpl.findStates()"})
  public void testFindStates() {
    // Arrange
    when(stateDao.findStates()).thenReturn(new ArrayList<>());

    // Act
    List<State> actualFindStatesResult = stateServiceImpl.findStates();

    // Assert
    verify(stateDao).findStates();
    assertTrue(actualFindStatesResult.isEmpty());
  }

  /**
   * Test {@link StateServiceImpl#findStates(String)} with {@code String}.
   * <p>
   * Method under test: {@link StateServiceImpl#findStates(String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"List StateServiceImpl.findStates(String)"})
  public void testFindStatesWithString() {
    // Arrange
    when(stateDao.findStates(Mockito.<String>any())).thenReturn(new ArrayList<>());

    // Act
    List<State> actualFindStatesResult = stateServiceImpl.findStates("GB");

    // Assert
    verify(stateDao).findStates(eq("GB"));
    assertTrue(actualFindStatesResult.isEmpty());
  }

  /**
   * Test {@link StateServiceImpl#findStateByAbbreviation(String)}.
   * <p>
   * Method under test: {@link StateServiceImpl#findStateByAbbreviation(String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"State StateServiceImpl.findStateByAbbreviation(String)"})
  public void testFindStateByAbbreviation() {
    // Arrange
    StateImpl stateImpl = new StateImpl();
    when(stateDao.findStateByAbbreviation(Mockito.<String>any())).thenReturn(stateImpl);

    // Act
    State actualFindStateByAbbreviationResult = stateServiceImpl.findStateByAbbreviation("Abbreviation");

    // Assert
    verify(stateDao).findStateByAbbreviation(eq("Abbreviation"));
    assertSame(stateImpl, actualFindStateByAbbreviationResult);
  }

  /**
   * Test {@link StateServiceImpl#save(State)}.
   * <p>
   * Method under test: {@link StateServiceImpl#save(State)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"State StateServiceImpl.save(State)"})
  public void testSave() {
    // Arrange
    StateImpl stateImpl = new StateImpl();
    when(stateDao.save(Mockito.<State>any())).thenReturn(stateImpl);

    // Act
    State actualSaveResult = stateServiceImpl.save(new StateImpl());

    // Assert
    verify(stateDao).save(isA(State.class));
    assertSame(stateImpl, actualSaveResult);
  }
}
