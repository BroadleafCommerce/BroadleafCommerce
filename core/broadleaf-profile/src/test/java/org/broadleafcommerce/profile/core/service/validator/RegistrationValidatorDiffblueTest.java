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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import org.springframework.validation.AbstractBindingResult;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

public class RegistrationValidatorDiffblueTest {
  /**
   * Test {@link RegistrationValidator#validate(Customer, String, String, Errors)} with {@code customer}, {@code password}, {@code passwordConfirm}, {@code errors}.
   * <ul>
   *   <li>Then calls {@link AbstractBindingResult#getFieldValue(String)}.</li>
   * </ul>
   * <p>
   * Method under test: {@link RegistrationValidator#validate(Customer, String, String, Errors)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void RegistrationValidator.validate(Customer, String, String, Errors)"})
  public void testValidateWithCustomerPasswordPasswordConfirmErrors_thenCallsGetFieldValue()
      throws IllegalStateException {
    // Arrange
    RegistrationValidator registrationValidator = new RegistrationValidator();
    CustomerImpl customer = new CustomerImpl();
    BeanPropertyBindingResult errors = mock(BeanPropertyBindingResult.class);
    when(errors.hasErrors()).thenReturn(false);
    when(errors.getFieldValue(Mockito.<String>any())).thenReturn("Field Value");
    doNothing().when(errors).popNestedPath();
    doNothing().when(errors).pushNestedPath(Mockito.<String>any());

    // Act
    registrationValidator.validate(customer, "iloveyou", "Password Confirm", errors);

    // Assert
    verify(errors, atLeast(1)).getFieldValue(Mockito.<String>any());
    verify(errors).hasErrors();
    verify(errors).popNestedPath();
    verify(errors).pushNestedPath(eq("customer"));
  }

  /**
   * Test {@link RegistrationValidator#supports(Class)}.
   * <p>
   * Method under test: {@link RegistrationValidator#supports(Class)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean RegistrationValidator.supports(Class)"})
  public void testSupports() {
    // Arrange
    RegistrationValidator registrationValidator = new RegistrationValidator();
    Class<Object> clazz = Object.class;

    // Act and Assert
    assertFalse(registrationValidator.supports(clazz));
  }
}
