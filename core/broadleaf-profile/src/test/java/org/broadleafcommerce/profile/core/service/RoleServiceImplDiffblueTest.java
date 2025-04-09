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

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.util.ArrayList;
import java.util.List;
import org.broadleafcommerce.profile.core.dao.RoleDao;
import org.broadleafcommerce.profile.core.domain.CustomerRole;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RoleServiceImplDiffblueTest {
  @Mock
  private RoleDao roleDao;

  @InjectMocks
  private RoleServiceImpl roleServiceImpl;

  /**
   * Test {@link RoleServiceImpl#findCustomerRolesByCustomerId(Long)}.
   * <p>
   * Method under test: {@link RoleServiceImpl#findCustomerRolesByCustomerId(Long)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"List RoleServiceImpl.findCustomerRolesByCustomerId(Long)"})
  public void testFindCustomerRolesByCustomerId() {
    // Arrange
    when(roleDao.readCustomerRolesByCustomerId(Mockito.<Long>any())).thenReturn(new ArrayList<>());

    // Act
    List<CustomerRole> actualFindCustomerRolesByCustomerIdResult = roleServiceImpl.findCustomerRolesByCustomerId(1L);

    // Assert
    verify(roleDao).readCustomerRolesByCustomerId(eq(1L));
    assertTrue(actualFindCustomerRolesByCustomerIdResult.isEmpty());
  }
}
