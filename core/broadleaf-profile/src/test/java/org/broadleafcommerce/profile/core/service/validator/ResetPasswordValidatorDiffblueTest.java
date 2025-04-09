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
package org.broadleafcommerce.profile.core.service.validator;

import static org.junit.Assert.assertFalse;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class ResetPasswordValidatorDiffblueTest {
  /**
   * Test {@link ResetPasswordValidator#supports(Class)}.
   * <p>
   * Method under test: {@link ResetPasswordValidator#supports(Class)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean ResetPasswordValidator.supports(Class)"})
  public void testSupports() {
    // Arrange
    ResetPasswordValidator resetPasswordValidator = new ResetPasswordValidator();
    Class<Object> clazz = Object.class;

    // Act and Assert
    assertFalse(resetPasswordValidator.supports(clazz));
  }
}
