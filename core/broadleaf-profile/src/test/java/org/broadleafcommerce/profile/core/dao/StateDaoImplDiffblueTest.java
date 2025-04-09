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

import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.profile.core.domain.CountryImpl;
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
public class StateDaoImplDiffblueTest {
  @Mock
  private EntityConfiguration entityConfiguration;

  @InjectMocks
  private StateDaoImpl stateDaoImpl;

  /**
   * Test {@link StateDaoImpl#create()}.
   * <p>
   * Method under test: {@link StateDaoImpl#create()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"State StateDaoImpl.create()"})
  public void testCreate() {
    // Arrange
    StateImpl stateImpl = new StateImpl();
    stateImpl.setAbbreviation("CA");
    stateImpl.setCountry(new CountryImpl());
    stateImpl.setName("California");
    when(entityConfiguration.createEntityInstance(Mockito.<String>any())).thenReturn(stateImpl);

    // Act
    State actualCreateResult = stateDaoImpl.create();

    // Assert
    verify(entityConfiguration).createEntityInstance(eq("org.broadleafcommerce.profile.core.domain.State"));
    assertSame(stateImpl, actualCreateResult);
  }
}
