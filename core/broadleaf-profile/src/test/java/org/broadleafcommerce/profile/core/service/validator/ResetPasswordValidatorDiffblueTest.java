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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.util.List;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

public class ResetPasswordValidatorDiffblueTest {
  /**
   * Test {@link ResetPasswordValidator#validate(String, String, String, Errors)} with {@code username}, {@code password}, {@code confirmPassword}, {@code errors}.
   * <p>
   * Method under test: {@link ResetPasswordValidator#validate(String, String, String, Errors)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void ResetPasswordValidator.validate(String, String, String, Errors)"})
  public void testValidateWithUsernamePasswordConfirmPasswordErrors() {
    // Arrange
    ResetPasswordValidator resetPasswordValidator = new ResetPasswordValidator();

    BindException errors = new BindException("Target", "Object Name");
    errors.addError(new ObjectError("password.valid.regex", "password.valid.regex"));

    // Act
    resetPasswordValidator.validate("janedoe", "iloveyou", "iloveyou", errors);

    // Assert that nothing has changed
    assertEquals("org.springframework.validation.BeanPropertyBindingResult: 1 errors\n"
        + "Error in object 'password.valid.regex': codes []; arguments []; default message [password.valid" + ".regex]",
        errors.getLocalizedMessage());
    assertEquals("org.springframework.validation.BeanPropertyBindingResult: 1 errors\n"
        + "Error in object 'password.valid.regex': codes []; arguments []; default message [password.valid" + ".regex]",
        errors.getMessage());
    assertEquals(1, errors.getAllErrors().size());
  }

  /**
   * Test {@link ResetPasswordValidator#validate(String, String, String, Errors)} with {@code username}, {@code password}, {@code confirmPassword}, {@code errors}.
   * <p>
   * Method under test: {@link ResetPasswordValidator#validate(String, String, String, Errors)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void ResetPasswordValidator.validate(String, String, String, Errors)"})
  public void testValidateWithUsernamePasswordConfirmPasswordErrors2() {
    // Arrange
    ResetPasswordValidator resetPasswordValidator = new ResetPasswordValidator();

    BindException errors = new BindException("Target", "Object Name");
    errors.addError(new ObjectError("password.valid.regex", "password.valid.regex"));

    // Act
    resetPasswordValidator.validate("", "iloveyou", "iloveyou", errors);

    // Assert
    assertEquals("org.springframework.validation.BeanPropertyBindingResult: 2 errors\n"
        + "Error in object 'password.valid.regex': codes []; arguments []; default message [password.valid.regex]"
        + "\n" + "Error in object 'Object Name': codes [username.Object Name,username]; arguments []; default message"
        + " [username.required]", errors.getLocalizedMessage());
    assertEquals("org.springframework.validation.BeanPropertyBindingResult: 2 errors\n"
        + "Error in object 'password.valid.regex': codes []; arguments []; default message [password.valid.regex]"
        + "\n" + "Error in object 'Object Name': codes [username.Object Name,username]; arguments []; default message"
        + " [username.required]", errors.getMessage());
    List<ObjectError> allErrors = errors.getAllErrors();
    assertEquals(2, allErrors.size());
    ObjectError getResult = allErrors.get(1);
    assertEquals("username", getResult.getCode());
    assertEquals("username.required", getResult.getDefaultMessage());
    assertArrayEquals(new String[]{"username.Object Name", "username"}, getResult.getCodes());
  }

  /**
   * Test {@link ResetPasswordValidator#validate(String, String, String, Errors)} with {@code username}, {@code password}, {@code confirmPassword}, {@code errors}.
   * <p>
   * Method under test: {@link ResetPasswordValidator#validate(String, String, String, Errors)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void ResetPasswordValidator.validate(String, String, String, Errors)"})
  public void testValidateWithUsernamePasswordConfirmPasswordErrors3() {
    // Arrange
    ResetPasswordValidator resetPasswordValidator = new ResetPasswordValidator();

    BindException errors = new BindException("Target", "Object Name");
    errors.addError(new ObjectError("password.valid.regex", "password.valid.regex"));

    // Act
    resetPasswordValidator.validate("janedoe", "", "iloveyou", errors);

    // Assert
    assertEquals("org.springframework.validation.BeanPropertyBindingResult: 2 errors\n"
        + "Error in object 'password.valid.regex': codes []; arguments []; default message [password.valid.regex]"
        + "\n" + "Error in object 'Object Name': codes [password.Object Name,password]; arguments []; default message"
        + " [password.required]", errors.getLocalizedMessage());
    assertEquals("org.springframework.validation.BeanPropertyBindingResult: 2 errors\n"
        + "Error in object 'password.valid.regex': codes []; arguments []; default message [password.valid.regex]"
        + "\n" + "Error in object 'Object Name': codes [password.Object Name,password]; arguments []; default message"
        + " [password.required]", errors.getMessage());
    List<ObjectError> allErrors = errors.getAllErrors();
    assertEquals(2, allErrors.size());
    ObjectError getResult = allErrors.get(1);
    assertEquals("password", getResult.getCode());
    assertEquals("password.required", getResult.getDefaultMessage());
    assertArrayEquals(new String[]{"password.Object Name", "password"}, getResult.getCodes());
  }

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
