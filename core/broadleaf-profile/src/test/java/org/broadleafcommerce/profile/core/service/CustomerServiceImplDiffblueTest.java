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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.broadleafcommerce.common.audit.Auditable;
import org.broadleafcommerce.common.email.service.EmailService;
import org.broadleafcommerce.common.email.service.info.EmailInfo;
import org.broadleafcommerce.common.id.service.IdGenerationService;
import org.broadleafcommerce.common.locale.domain.LocaleImpl;
import org.broadleafcommerce.common.security.util.PasswordChange;
import org.broadleafcommerce.common.security.util.PasswordReset;
import org.broadleafcommerce.common.service.GenericResponse;
import org.broadleafcommerce.profile.core.dao.CustomerDao;
import org.broadleafcommerce.profile.core.dao.CustomerDaoImpl;
import org.broadleafcommerce.profile.core.dao.CustomerForgotPasswordSecurityTokenDao;
import org.broadleafcommerce.profile.core.dao.RoleDao;
import org.broadleafcommerce.profile.core.domain.ChallengeQuestionImpl;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerForgotPasswordSecurityToken;
import org.broadleafcommerce.profile.core.domain.CustomerForgotPasswordSecurityTokenImpl;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.broadleafcommerce.profile.core.domain.CustomerRole;
import org.broadleafcommerce.profile.core.domain.RoleImpl;
import org.broadleafcommerce.profile.core.dto.CustomerRuleHolder;
import org.broadleafcommerce.profile.core.service.handler.PasswordUpdatedHandler;
import org.broadleafcommerce.profile.core.service.listener.PostRegistrationObserver;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@RunWith(MockitoJUnitRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class CustomerServiceImplDiffblueTest {
  @Mock
  private CustomerDao customerDao;

  @Mock
  private CustomerForgotPasswordSecurityTokenDao customerForgotPasswordSecurityTokenDao;

  @InjectMocks
  private CustomerServiceImpl customerServiceImpl;

  @Mock
  private EmailInfo emailInfo;

  @Mock
  private EmailService emailService;

  @Mock
  private IdGenerationService idGenerationService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private RoleDao roleDao;

  /**
   * Test {@link CustomerServiceImpl#saveCustomer(Customer, boolean)} with {@code customer}, {@code register}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#saveCustomer(Customer, boolean)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.saveCustomer(Customer, boolean)"})
  public void testSaveCustomerWithCustomerRegister() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerDao.save(Mockito.<Customer>any())).thenReturn(customerImpl);
    when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");
    CustomerImpl customer = mock(CustomerImpl.class);
    when(customer.getChallengeAnswer()).thenReturn("secret");
    doNothing().when(customer).setPassword(Mockito.<String>any());
    when(customer.isRegistered()).thenReturn(true);
    when(customer.getUnencodedChallengeAnswer()).thenReturn("secret");
    when(customer.getUnencodedPassword()).thenReturn("secret");

    // Act
    Customer actualSaveCustomerResult = customerServiceImpl.saveCustomer(customer, true);

    // Assert
    verify(customerDao).save(isA(Customer.class));
    verify(customer).getChallengeAnswer();
    verify(customer, atLeast(1)).getUnencodedChallengeAnswer();
    verify(customer, atLeast(1)).getUnencodedPassword();
    verify(customer).isRegistered();
    verify(customer).setPassword(eq("secret"));
    verify(passwordEncoder).encode(isA(CharSequence.class));
    assertSame(customerImpl, actualSaveCustomerResult);
  }

  /**
   * Test {@link CustomerServiceImpl#saveCustomer(Customer, boolean)} with {@code customer}, {@code register}.
   * <ul>
   *   <li>Given {@code Challenge Answer}.</li>
   *   <li>Then calls {@link CustomerImpl#isRegistered()}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#saveCustomer(Customer, boolean)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.saveCustomer(Customer, boolean)"})
  public void testSaveCustomerWithCustomerRegister_givenChallengeAnswer_thenCallsIsRegistered() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerDao.save(Mockito.<Customer>any())).thenReturn(customerImpl);
    when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");
    CustomerImpl customer = mock(CustomerImpl.class);
    when(customer.getChallengeAnswer()).thenReturn("Challenge Answer");
    doNothing().when(customer).setChallengeAnswer(Mockito.<String>any());
    doNothing().when(customer).setPassword(Mockito.<String>any());
    when(customer.isRegistered()).thenReturn(true);
    when(customer.getUnencodedChallengeAnswer()).thenReturn("secret");
    when(customer.getUnencodedPassword()).thenReturn("secret");

    // Act
    Customer actualSaveCustomerResult = customerServiceImpl.saveCustomer(customer, true);

    // Assert
    verify(customerDao).save(isA(Customer.class));
    verify(customer).getChallengeAnswer();
    verify(customer, atLeast(1)).getUnencodedChallengeAnswer();
    verify(customer, atLeast(1)).getUnencodedPassword();
    verify(customer).isRegistered();
    verify(customer).setChallengeAnswer(eq("secret"));
    verify(customer).setPassword(eq("secret"));
    verify(passwordEncoder, atLeast(1)).encode(isA(CharSequence.class));
    assertSame(customerImpl, actualSaveCustomerResult);
  }

  /**
   * Test {@link CustomerServiceImpl#saveCustomer(Customer, boolean)} with {@code customer}, {@code register}.
   * <ul>
   *   <li>Given {@link PasswordEncoder}.</li>
   *   <li>When {@link CustomerImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#saveCustomer(Customer, boolean)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.saveCustomer(Customer, boolean)"})
  public void testSaveCustomerWithCustomerRegister_givenPasswordEncoder_whenCustomerImpl() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerDao.save(Mockito.<Customer>any())).thenReturn(customerImpl);

    // Act
    Customer actualSaveCustomerResult = customerServiceImpl.saveCustomer(new CustomerImpl(), true);

    // Assert
    verify(customerDao).save(isA(Customer.class));
    assertSame(customerImpl, actualSaveCustomerResult);
  }

  /**
   * Test {@link CustomerServiceImpl#saveCustomer(Customer, boolean)} with {@code customer}, {@code register}.
   * <ul>
   *   <li>When {@code false}.</li>
   *   <li>Then calls {@link CustomerImpl#setChallengeAnswer(String)}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#saveCustomer(Customer, boolean)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.saveCustomer(Customer, boolean)"})
  public void testSaveCustomerWithCustomerRegister_whenFalse_thenCallsSetChallengeAnswer() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerDao.save(Mockito.<Customer>any())).thenReturn(customerImpl);
    when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");
    CustomerImpl customer = mock(CustomerImpl.class);
    when(customer.getChallengeAnswer()).thenReturn("Challenge Answer");
    doNothing().when(customer).setChallengeAnswer(Mockito.<String>any());
    doNothing().when(customer).setPassword(Mockito.<String>any());
    when(customer.getUnencodedChallengeAnswer()).thenReturn("secret");
    when(customer.getUnencodedPassword()).thenReturn("secret");

    // Act
    Customer actualSaveCustomerResult = customerServiceImpl.saveCustomer(customer, false);

    // Assert
    verify(customerDao).save(isA(Customer.class));
    verify(customer).getChallengeAnswer();
    verify(customer, atLeast(1)).getUnencodedChallengeAnswer();
    verify(customer, atLeast(1)).getUnencodedPassword();
    verify(customer).setChallengeAnswer(eq("secret"));
    verify(customer).setPassword(eq("secret"));
    verify(passwordEncoder, atLeast(1)).encode(isA(CharSequence.class));
    assertSame(customerImpl, actualSaveCustomerResult);
  }

  /**
   * Test {@link CustomerServiceImpl#saveCustomer(Customer)} with {@code customer}.
   * <ul>
   *   <li>Given {@code Challenge Answer}.</li>
   *   <li>Then calls {@link CustomerImpl#setChallengeAnswer(String)}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#saveCustomer(Customer)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.saveCustomer(Customer)"})
  public void testSaveCustomerWithCustomer_givenChallengeAnswer_thenCallsSetChallengeAnswer() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerDao.save(Mockito.<Customer>any())).thenReturn(customerImpl);
    when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");
    CustomerImpl customer = mock(CustomerImpl.class);
    when(customer.getChallengeAnswer()).thenReturn("Challenge Answer");
    doNothing().when(customer).setChallengeAnswer(Mockito.<String>any());
    doNothing().when(customer).setPassword(Mockito.<String>any());
    when(customer.isRegistered()).thenReturn(true);
    when(customer.getUnencodedChallengeAnswer()).thenReturn("secret");
    when(customer.getUnencodedPassword()).thenReturn("secret");

    // Act
    Customer actualSaveCustomerResult = customerServiceImpl.saveCustomer(customer);

    // Assert
    verify(customerDao).save(isA(Customer.class));
    verify(customer).getChallengeAnswer();
    verify(customer, atLeast(1)).getUnencodedChallengeAnswer();
    verify(customer, atLeast(1)).getUnencodedPassword();
    verify(customer, atLeast(1)).isRegistered();
    verify(customer).setChallengeAnswer(eq("secret"));
    verify(customer).setPassword(eq("secret"));
    verify(passwordEncoder, atLeast(1)).encode(isA(CharSequence.class));
    assertSame(customerImpl, actualSaveCustomerResult);
  }

  /**
   * Test {@link CustomerServiceImpl#saveCustomer(Customer)} with {@code customer}.
   * <ul>
   *   <li>Given {@link PasswordEncoder}.</li>
   *   <li>When {@link CustomerImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#saveCustomer(Customer)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.saveCustomer(Customer)"})
  public void testSaveCustomerWithCustomer_givenPasswordEncoder_whenCustomerImpl() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerDao.save(Mockito.<Customer>any())).thenReturn(customerImpl);

    // Act
    Customer actualSaveCustomerResult = customerServiceImpl.saveCustomer(new CustomerImpl());

    // Assert
    verify(customerDao).save(isA(Customer.class));
    assertSame(customerImpl, actualSaveCustomerResult);
  }

  /**
   * Test {@link CustomerServiceImpl#saveCustomer(Customer)} with {@code customer}.
   * <ul>
   *   <li>When {@link CustomerImpl} {@link CustomerImpl#getChallengeAnswer()} return {@code secret}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#saveCustomer(Customer)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.saveCustomer(Customer)"})
  public void testSaveCustomerWithCustomer_whenCustomerImplGetChallengeAnswerReturnSecret() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerDao.save(Mockito.<Customer>any())).thenReturn(customerImpl);
    when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");
    CustomerImpl customer = mock(CustomerImpl.class);
    when(customer.getChallengeAnswer()).thenReturn("secret");
    doNothing().when(customer).setPassword(Mockito.<String>any());
    when(customer.isRegistered()).thenReturn(true);
    when(customer.getUnencodedChallengeAnswer()).thenReturn("secret");
    when(customer.getUnencodedPassword()).thenReturn("secret");

    // Act
    Customer actualSaveCustomerResult = customerServiceImpl.saveCustomer(customer);

    // Assert
    verify(customerDao).save(isA(Customer.class));
    verify(customer).getChallengeAnswer();
    verify(customer, atLeast(1)).getUnencodedChallengeAnswer();
    verify(customer, atLeast(1)).getUnencodedPassword();
    verify(customer, atLeast(1)).isRegistered();
    verify(customer).setPassword(eq("secret"));
    verify(passwordEncoder).encode(isA(CharSequence.class));
    assertSame(customerImpl, actualSaveCustomerResult);
  }

  /**
   * Test {@link CustomerServiceImpl#registerCustomer(Customer, String, String)}.
   * <ul>
   *   <li>Given {@code Challenge Answer}.</li>
   *   <li>Then calls {@link CustomerImpl#setChallengeAnswer(String)}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#registerCustomer(Customer, String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.registerCustomer(Customer, String, String)"})
  public void testRegisterCustomer_givenChallengeAnswer_thenCallsSetChallengeAnswer() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerDao.save(Mockito.<Customer>any())).thenReturn(customerImpl);
    when(emailService.sendTemplateEmail(Mockito.<String>any(), Mockito.<EmailInfo>any(),
        Mockito.<Map<String, Object>>any())).thenReturn(true);
    when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");
    when(roleDao.readRoleByName(Mockito.<String>any())).thenReturn(new RoleImpl());
    doNothing().when(roleDao).addRoleToCustomer(Mockito.<CustomerRole>any());
    CustomerImpl customer = mock(CustomerImpl.class);
    when(customer.getChallengeAnswer()).thenReturn("Challenge Answer");
    doNothing().when(customer).setChallengeAnswer(Mockito.<String>any());
    doNothing().when(customer).setPassword(Mockito.<String>any());
    when(customer.isRegistered()).thenReturn(true);
    when(customer.getId()).thenReturn(1L);
    when(customer.getEmailAddress()).thenReturn("42 Main St");
    when(customer.getUnencodedChallengeAnswer()).thenReturn("secret");
    when(customer.getUnencodedPassword()).thenReturn("secret");
    doNothing().when(customer).setRegistered(anyBoolean());
    doNothing().when(customer).setUnencodedPassword(Mockito.<String>any());

    // Act
    Customer actualRegisterCustomerResult = customerServiceImpl.registerCustomer(customer, "iloveyou",
        "Password Confirm");

    // Assert
    verify(emailService).sendTemplateEmail(eq("42 Main St"), (EmailInfo) isNull(), isA(Map.class));
    verify(customerDao).save(isA(Customer.class));
    verify(roleDao).addRoleToCustomer(isA(CustomerRole.class));
    verify(roleDao).readRoleByName(eq("ROLE_USER"));
    verify(customer).getChallengeAnswer();
    verify(customer).getEmailAddress();
    verify(customer).getId();
    verify(customer, atLeast(1)).getUnencodedChallengeAnswer();
    verify(customer, atLeast(1)).getUnencodedPassword();
    verify(customer, atLeast(1)).isRegistered();
    verify(customer).setChallengeAnswer(eq("secret"));
    verify(customer).setPassword(eq("secret"));
    verify(customer).setRegistered(eq(true));
    verify(customer).setUnencodedPassword(eq("iloveyou"));
    verify(passwordEncoder, atLeast(1)).encode(isA(CharSequence.class));
    assertSame(customerImpl, actualRegisterCustomerResult);
  }

  /**
   * Test {@link CustomerServiceImpl#registerCustomer(Customer, String, String)}.
   * <ul>
   *   <li>Given {@code false}.</li>
   *   <li>When {@link CustomerImpl} {@link CustomerImpl#isRegistered()} return {@code false}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#registerCustomer(Customer, String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.registerCustomer(Customer, String, String)"})
  public void testRegisterCustomer_givenFalse_whenCustomerImplIsRegisteredReturnFalse() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerDao.save(Mockito.<Customer>any())).thenReturn(customerImpl);
    when(emailService.sendTemplateEmail(Mockito.<String>any(), Mockito.<EmailInfo>any(),
        Mockito.<Map<String, Object>>any())).thenReturn(true);
    when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");
    when(roleDao.readRoleByName(Mockito.<String>any())).thenReturn(new RoleImpl());
    doNothing().when(roleDao).addRoleToCustomer(Mockito.<CustomerRole>any());
    CustomerImpl customer = mock(CustomerImpl.class);
    when(customer.getChallengeAnswer()).thenReturn("Challenge Answer");
    doNothing().when(customer).setChallengeAnswer(Mockito.<String>any());
    doNothing().when(customer).setPassword(Mockito.<String>any());
    when(customer.isRegistered()).thenReturn(false);
    when(customer.getId()).thenReturn(1L);
    when(customer.getEmailAddress()).thenReturn("42 Main St");
    when(customer.getUnencodedChallengeAnswer()).thenReturn("secret");
    when(customer.getUnencodedPassword()).thenReturn("secret");
    doNothing().when(customer).setRegistered(anyBoolean());
    doNothing().when(customer).setUnencodedPassword(Mockito.<String>any());

    // Act
    Customer actualRegisterCustomerResult = customerServiceImpl.registerCustomer(customer, "iloveyou",
        "Password Confirm");

    // Assert
    verify(emailService).sendTemplateEmail(eq("42 Main St"), (EmailInfo) isNull(), isA(Map.class));
    verify(customerDao).save(isA(Customer.class));
    verify(roleDao).addRoleToCustomer(isA(CustomerRole.class));
    verify(roleDao).readRoleByName(eq("ROLE_USER"));
    verify(customer).getChallengeAnswer();
    verify(customer).getEmailAddress();
    verify(customer).getId();
    verify(customer, atLeast(1)).getUnencodedChallengeAnswer();
    verify(customer, atLeast(1)).getUnencodedPassword();
    verify(customer).isRegistered();
    verify(customer).setChallengeAnswer(eq("secret"));
    verify(customer).setPassword(eq("secret"));
    verify(customer).setRegistered(eq(true));
    verify(customer).setUnencodedPassword(eq("iloveyou"));
    verify(passwordEncoder, atLeast(1)).encode(isA(CharSequence.class));
    assertSame(customerImpl, actualRegisterCustomerResult);
  }

  /**
   * Test {@link CustomerServiceImpl#registerCustomer(Customer, String, String)}.
   * <ul>
   *   <li>Given {@code null}.</li>
   *   <li>When {@link CustomerImpl} {@link CustomerImpl#getUnencodedPassword()} return {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#registerCustomer(Customer, String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.registerCustomer(Customer, String, String)"})
  public void testRegisterCustomer_givenNull_whenCustomerImplGetUnencodedPasswordReturnNull() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerDao.save(Mockito.<Customer>any())).thenReturn(customerImpl);
    when(emailService.sendTemplateEmail(Mockito.<String>any(), Mockito.<EmailInfo>any(),
        Mockito.<Map<String, Object>>any())).thenReturn(true);
    when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");
    when(roleDao.readRoleByName(Mockito.<String>any())).thenReturn(new RoleImpl());
    doNothing().when(roleDao).addRoleToCustomer(Mockito.<CustomerRole>any());
    CustomerImpl customer = mock(CustomerImpl.class);
    when(customer.getChallengeAnswer()).thenReturn("Challenge Answer");
    doNothing().when(customer).setChallengeAnswer(Mockito.<String>any());
    when(customer.isRegistered()).thenReturn(true);
    when(customer.getId()).thenReturn(1L);
    when(customer.getEmailAddress()).thenReturn("42 Main St");
    when(customer.getUnencodedChallengeAnswer()).thenReturn("secret");
    when(customer.getUnencodedPassword()).thenReturn(null);
    doNothing().when(customer).setRegistered(anyBoolean());
    doNothing().when(customer).setUnencodedPassword(Mockito.<String>any());

    // Act
    Customer actualRegisterCustomerResult = customerServiceImpl.registerCustomer(customer, "iloveyou",
        "Password Confirm");

    // Assert
    verify(emailService).sendTemplateEmail(eq("42 Main St"), (EmailInfo) isNull(), isA(Map.class));
    verify(customerDao).save(isA(Customer.class));
    verify(roleDao).addRoleToCustomer(isA(CustomerRole.class));
    verify(roleDao).readRoleByName(eq("ROLE_USER"));
    verify(customer).getChallengeAnswer();
    verify(customer).getEmailAddress();
    verify(customer).getId();
    verify(customer, atLeast(1)).getUnencodedChallengeAnswer();
    verify(customer).getUnencodedPassword();
    verify(customer, atLeast(1)).isRegistered();
    verify(customer).setChallengeAnswer(eq("secret"));
    verify(customer).setRegistered(eq(true));
    verify(customer).setUnencodedPassword(eq("iloveyou"));
    verify(passwordEncoder).encode(isA(CharSequence.class));
    assertSame(customerImpl, actualRegisterCustomerResult);
  }

  /**
   * Test {@link CustomerServiceImpl#registerCustomer(Customer, String, String)}.
   * <ul>
   *   <li>When {@link CustomerImpl} {@link CustomerImpl#getChallengeAnswer()} return {@code secret}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#registerCustomer(Customer, String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.registerCustomer(Customer, String, String)"})
  public void testRegisterCustomer_whenCustomerImplGetChallengeAnswerReturnSecret() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerDao.save(Mockito.<Customer>any())).thenReturn(customerImpl);
    when(emailService.sendTemplateEmail(Mockito.<String>any(), Mockito.<EmailInfo>any(),
        Mockito.<Map<String, Object>>any())).thenReturn(true);
    when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");
    when(roleDao.readRoleByName(Mockito.<String>any())).thenReturn(new RoleImpl());
    doNothing().when(roleDao).addRoleToCustomer(Mockito.<CustomerRole>any());
    CustomerImpl customer = mock(CustomerImpl.class);
    when(customer.getChallengeAnswer()).thenReturn("secret");
    doNothing().when(customer).setPassword(Mockito.<String>any());
    when(customer.isRegistered()).thenReturn(true);
    when(customer.getId()).thenReturn(1L);
    when(customer.getEmailAddress()).thenReturn("42 Main St");
    when(customer.getUnencodedChallengeAnswer()).thenReturn("secret");
    when(customer.getUnencodedPassword()).thenReturn("secret");
    doNothing().when(customer).setRegistered(anyBoolean());
    doNothing().when(customer).setUnencodedPassword(Mockito.<String>any());

    // Act
    Customer actualRegisterCustomerResult = customerServiceImpl.registerCustomer(customer, "iloveyou",
        "Password Confirm");

    // Assert
    verify(emailService).sendTemplateEmail(eq("42 Main St"), (EmailInfo) isNull(), isA(Map.class));
    verify(customerDao).save(isA(Customer.class));
    verify(roleDao).addRoleToCustomer(isA(CustomerRole.class));
    verify(roleDao).readRoleByName(eq("ROLE_USER"));
    verify(customer).getChallengeAnswer();
    verify(customer).getEmailAddress();
    verify(customer).getId();
    verify(customer, atLeast(1)).getUnencodedChallengeAnswer();
    verify(customer, atLeast(1)).getUnencodedPassword();
    verify(customer, atLeast(1)).isRegistered();
    verify(customer).setPassword(eq("secret"));
    verify(customer).setRegistered(eq(true));
    verify(customer).setUnencodedPassword(eq("iloveyou"));
    verify(passwordEncoder).encode(isA(CharSequence.class));
    assertSame(customerImpl, actualRegisterCustomerResult);
  }

  /**
   * Test {@link CustomerServiceImpl#registerCustomer(Customer, String, String)}.
   * <ul>
   *   <li>When {@link CustomerImpl} (default constructor).</li>
   *   <li>Then calls {@link IdGenerationService#findNextId(String)}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#registerCustomer(Customer, String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.registerCustomer(Customer, String, String)"})
  public void testRegisterCustomer_whenCustomerImpl_thenCallsFindNextId() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerDao.save(Mockito.<Customer>any())).thenReturn(customerImpl);
    when(emailService.sendTemplateEmail(Mockito.<String>any(), Mockito.<EmailInfo>any(),
        Mockito.<Map<String, Object>>any())).thenReturn(true);
    when(idGenerationService.findNextId(Mockito.<String>any())).thenReturn(1L);
    when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");
    when(roleDao.readRoleByName(Mockito.<String>any())).thenReturn(new RoleImpl());
    doNothing().when(roleDao).addRoleToCustomer(Mockito.<CustomerRole>any());

    // Act
    Customer actualRegisterCustomerResult = customerServiceImpl.registerCustomer(new CustomerImpl(), "iloveyou",
        "Password Confirm");

    // Assert
    verify(emailService).sendTemplateEmail((String) isNull(), (EmailInfo) isNull(), isA(Map.class));
    verify(idGenerationService).findNextId(eq("org.broadleafcommerce.profile.core.domain.Customer"));
    verify(customerDao).save(isA(Customer.class));
    verify(roleDao).addRoleToCustomer(isA(CustomerRole.class));
    verify(roleDao).readRoleByName(eq("ROLE_USER"));
    verify(passwordEncoder).encode(isA(CharSequence.class));
    assertSame(customerImpl, actualRegisterCustomerResult);
  }

  /**
   * Test {@link CustomerServiceImpl#createRegisteredCustomerRoles(Customer)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#createRegisteredCustomerRoles(Customer)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerServiceImpl.createRegisteredCustomerRoles(Customer)"})
  public void testCreateRegisteredCustomerRoles() {
    // Arrange
    when(roleDao.readRoleByName(Mockito.<String>any())).thenReturn(new RoleImpl());
    doNothing().when(roleDao).addRoleToCustomer(Mockito.<CustomerRole>any());

    // Act
    customerServiceImpl.createRegisteredCustomerRoles(new CustomerImpl());

    // Assert
    verify(roleDao).addRoleToCustomer(isA(CustomerRole.class));
    verify(roleDao).readRoleByName(eq("ROLE_USER"));
  }

  /**
   * Test {@link CustomerServiceImpl#readCustomerByEmail(String)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#readCustomerByEmail(String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.readCustomerByEmail(String)"})
  public void testReadCustomerByEmail() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerDao.readCustomerByEmail(Mockito.<String>any())).thenReturn(customerImpl);

    // Act
    Customer actualReadCustomerByEmailResult = customerServiceImpl.readCustomerByEmail("42 Main St");

    // Assert
    verify(customerDao).readCustomerByEmail(eq("42 Main St"));
    assertSame(customerImpl, actualReadCustomerByEmailResult);
  }

  /**
   * Test {@link CustomerServiceImpl#changePassword(PasswordChange)}.
   * <ul>
   *   <li>Given {@link CustomerDao} {@link CustomerDao#readCustomerByUsername(String)} return {@link CustomerImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#changePassword(PasswordChange)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.changePassword(PasswordChange)"})
  public void testChangePassword_givenCustomerDaoReadCustomerByUsernameReturnCustomerImpl() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerDao.save(Mockito.<Customer>any())).thenReturn(customerImpl);
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(new CustomerImpl());

    // Act
    Customer actualChangePasswordResult = customerServiceImpl.changePassword(new PasswordChange("janedoe"));

    // Assert
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerDao).save(isA(Customer.class));
    assertSame(customerImpl, actualChangePasswordResult);
  }

  /**
   * Test {@link CustomerServiceImpl#changePassword(PasswordChange)}.
   * <ul>
   *   <li>Given {@link CustomerImpl} {@link CustomerImpl#getChallengeAnswer()} return {@code secret}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#changePassword(PasswordChange)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.changePassword(PasswordChange)"})
  public void testChangePassword_givenCustomerImplGetChallengeAnswerReturnSecret() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.getChallengeAnswer()).thenReturn("secret");
    doNothing().when(customerImpl).setPassword(Mockito.<String>any());
    when(customerImpl.isRegistered()).thenReturn(true);
    when(customerImpl.getUnencodedChallengeAnswer()).thenReturn("secret");
    when(customerImpl.getUnencodedPassword()).thenReturn("secret");
    doNothing().when(customerImpl).setPasswordChangeRequired(anyBoolean());
    doNothing().when(customerImpl).setUnencodedPassword(Mockito.<String>any());
    CustomerImpl customerImpl2 = new CustomerImpl();
    when(customerDao.save(Mockito.<Customer>any())).thenReturn(customerImpl2);
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);
    when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");

    // Act
    Customer actualChangePasswordResult = customerServiceImpl.changePassword(new PasswordChange("janedoe"));

    // Assert
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerDao).save(isA(Customer.class));
    verify(customerImpl).getChallengeAnswer();
    verify(customerImpl, atLeast(1)).getUnencodedChallengeAnswer();
    verify(customerImpl, atLeast(1)).getUnencodedPassword();
    verify(customerImpl, atLeast(1)).isRegistered();
    verify(customerImpl).setPassword(eq("secret"));
    verify(customerImpl).setPasswordChangeRequired(eq(false));
    verify(customerImpl).setUnencodedPassword(isNull());
    verify(passwordEncoder).encode(isA(CharSequence.class));
    assertSame(customerImpl2, actualChangePasswordResult);
  }

  /**
   * Test {@link CustomerServiceImpl#changePassword(PasswordChange)}.
   * <ul>
   *   <li>Then calls {@link CustomerImpl#setChallengeAnswer(String)}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#changePassword(PasswordChange)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.changePassword(PasswordChange)"})
  public void testChangePassword_thenCallsSetChallengeAnswer() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.getChallengeAnswer()).thenReturn("Challenge Answer");
    doNothing().when(customerImpl).setChallengeAnswer(Mockito.<String>any());
    doNothing().when(customerImpl).setPassword(Mockito.<String>any());
    when(customerImpl.isRegistered()).thenReturn(true);
    when(customerImpl.getUnencodedChallengeAnswer()).thenReturn("secret");
    when(customerImpl.getUnencodedPassword()).thenReturn("secret");
    doNothing().when(customerImpl).setPasswordChangeRequired(anyBoolean());
    doNothing().when(customerImpl).setUnencodedPassword(Mockito.<String>any());
    CustomerImpl customerImpl2 = new CustomerImpl();
    when(customerDao.save(Mockito.<Customer>any())).thenReturn(customerImpl2);
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);
    when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");

    // Act
    Customer actualChangePasswordResult = customerServiceImpl.changePassword(new PasswordChange("janedoe"));

    // Assert
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerDao).save(isA(Customer.class));
    verify(customerImpl).getChallengeAnswer();
    verify(customerImpl, atLeast(1)).getUnencodedChallengeAnswer();
    verify(customerImpl, atLeast(1)).getUnencodedPassword();
    verify(customerImpl, atLeast(1)).isRegistered();
    verify(customerImpl).setChallengeAnswer(eq("secret"));
    verify(customerImpl).setPassword(eq("secret"));
    verify(customerImpl).setPasswordChangeRequired(eq(false));
    verify(customerImpl).setUnencodedPassword(isNull());
    verify(passwordEncoder, atLeast(1)).encode(isA(CharSequence.class));
    assertSame(customerImpl2, actualChangePasswordResult);
  }

  /**
   * Test {@link CustomerServiceImpl#resetPassword(PasswordReset)}.
   * <ul>
   *   <li>Given {@link CustomerDao} {@link CustomerDao#readCustomerByUsername(String)} return {@link CustomerImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#resetPassword(PasswordReset)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.resetPassword(PasswordReset)"})
  public void testResetPassword_givenCustomerDaoReadCustomerByUsernameReturnCustomerImpl() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerDao.save(Mockito.<Customer>any())).thenReturn(customerImpl);
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(new CustomerImpl());
    when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");

    // Act
    Customer actualResetPasswordResult = customerServiceImpl.resetPassword(new PasswordReset("janedoe"));

    // Assert
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerDao).save(isA(Customer.class));
    verify(passwordEncoder).encode(isA(CharSequence.class));
    assertSame(customerImpl, actualResetPasswordResult);
  }

  /**
   * Test {@link CustomerServiceImpl#resetPassword(PasswordReset)}.
   * <ul>
   *   <li>Given zero.</li>
   *   <li>Then calls {@link PasswordReset#getPasswordChangeRequired()}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#resetPassword(PasswordReset)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.resetPassword(PasswordReset)"})
  public void testResetPassword_givenZero_thenCallsGetPasswordChangeRequired() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.getChallengeAnswer()).thenReturn("Challenge Answer");
    doNothing().when(customerImpl).setChallengeAnswer(Mockito.<String>any());
    doNothing().when(customerImpl).setPassword(Mockito.<String>any());
    when(customerImpl.isRegistered()).thenReturn(true);
    when(customerImpl.getUnencodedChallengeAnswer()).thenReturn("secret");
    when(customerImpl.getUnencodedPassword()).thenReturn("secret");
    doNothing().when(customerImpl).setPasswordChangeRequired(anyBoolean());
    doNothing().when(customerImpl).setUnencodedPassword(Mockito.<String>any());
    CustomerImpl customerImpl2 = new CustomerImpl();
    when(customerDao.save(Mockito.<Customer>any())).thenReturn(customerImpl2);
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);
    when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");
    PasswordReset passwordReset = mock(PasswordReset.class);
    when(passwordReset.getPasswordChangeRequired()).thenReturn(true);
    when(passwordReset.getPasswordLength()).thenReturn(0);
    when(passwordReset.getUsername()).thenReturn("janedoe");

    // Act
    Customer actualResetPasswordResult = customerServiceImpl.resetPassword(passwordReset);

    // Assert
    verify(passwordReset).getPasswordChangeRequired();
    verify(passwordReset).getPasswordLength();
    verify(passwordReset).getUsername();
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerDao).save(isA(Customer.class));
    verify(customerImpl).getChallengeAnswer();
    verify(customerImpl, atLeast(1)).getUnencodedChallengeAnswer();
    verify(customerImpl, atLeast(1)).getUnencodedPassword();
    verify(customerImpl, atLeast(1)).isRegistered();
    verify(customerImpl).setChallengeAnswer(eq("secret"));
    verify(customerImpl).setPassword(eq("secret"));
    verify(customerImpl).setPasswordChangeRequired(eq(true));
    verify(customerImpl).setUnencodedPassword(eq(""));
    verify(passwordEncoder, atLeast(1)).encode(isA(CharSequence.class));
    assertSame(customerImpl2, actualResetPasswordResult);
  }

  /**
   * Test {@link CustomerServiceImpl#addPostRegisterListener(PostRegistrationObserver)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#addPostRegisterListener(PostRegistrationObserver)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerServiceImpl.addPostRegisterListener(PostRegistrationObserver)"})
  public void testAddPostRegisterListener() {
    // Arrange
    PostRegistrationObserver postRegisterListeners = mock(PostRegistrationObserver.class);

    // Act
    customerServiceImpl.addPostRegisterListener(postRegisterListeners);

    // Assert
    List<PostRegistrationObserver> postRegistrationObserverList = customerServiceImpl.postRegisterListeners;
    assertEquals(1, postRegistrationObserverList.size());
    assertSame(postRegisterListeners, postRegistrationObserverList.get(0));
  }

  /**
   * Test {@link CustomerServiceImpl#notifyPostRegisterListeners(Customer)}.
   * <ul>
   *   <li>Then calls {@link PostRegistrationObserver#processRegistrationEvent(Customer)}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#notifyPostRegisterListeners(Customer)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerServiceImpl.notifyPostRegisterListeners(Customer)"})
  public void testNotifyPostRegisterListeners_thenCallsProcessRegistrationEvent() {
    // Arrange
    PostRegistrationObserver postRegisterListeners = mock(PostRegistrationObserver.class);
    doNothing().when(postRegisterListeners).processRegistrationEvent(Mockito.<Customer>any());

    CustomerServiceImpl customerServiceImpl = new CustomerServiceImpl();
    customerServiceImpl.addPostRegisterListener(postRegisterListeners);

    // Act
    customerServiceImpl.notifyPostRegisterListeners(new CustomerImpl());

    // Assert
    verify(postRegisterListeners).processRegistrationEvent(isA(Customer.class));
  }

  /**
   * Test {@link CustomerServiceImpl#createCustomer()}.
   * <ul>
   *   <li>Then return {@link CustomerImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#createCustomer()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.createCustomer()"})
  public void testCreateCustomer_thenReturnCustomerImpl() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerDao.create()).thenReturn(customerImpl);
    when(idGenerationService.findNextId(Mockito.<String>any())).thenReturn(1L);

    // Act
    Customer actualCreateCustomerResult = customerServiceImpl.createCustomer();

    // Assert
    verify(idGenerationService).findNextId(eq("org.broadleafcommerce.profile.core.domain.Customer"));
    verify(customerDao).create();
    assertSame(customerImpl, actualCreateCustomerResult);
  }

  /**
   * Test {@link CustomerServiceImpl#createCustomerFromId(Long)}.
   * <ul>
   *   <li>Given {@link CustomerDao} {@link CustomerDao#readCustomerById(Long)} return {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#createCustomerFromId(Long)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.createCustomerFromId(Long)"})
  public void testCreateCustomerFromId_givenCustomerDaoReadCustomerByIdReturnNull() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerDao.create()).thenReturn(customerImpl);
    when(customerDao.readCustomerById(Mockito.<Long>any())).thenReturn(null);

    // Act
    Customer actualCreateCustomerFromIdResult = customerServiceImpl.createCustomerFromId(1L);

    // Assert
    verify(customerDao).create();
    verify(customerDao).readCustomerById(eq(1L));
    assertSame(customerImpl, actualCreateCustomerFromIdResult);
  }

  /**
   * Test {@link CustomerServiceImpl#createCustomerFromId(Long)}.
   * <ul>
   *   <li>Then calls {@link IdGenerationService#findNextId(String)}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#createCustomerFromId(Long)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.createCustomerFromId(Long)"})
  public void testCreateCustomerFromId_thenCallsFindNextId() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerDao.create()).thenReturn(customerImpl);
    when(idGenerationService.findNextId(Mockito.<String>any())).thenReturn(1L);

    // Act
    Customer actualCreateCustomerFromIdResult = customerServiceImpl.createCustomerFromId(null);

    // Assert
    verify(idGenerationService).findNextId(eq("org.broadleafcommerce.profile.core.domain.Customer"));
    verify(customerDao).create();
    assertSame(customerImpl, actualCreateCustomerFromIdResult);
  }

  /**
   * Test {@link CustomerServiceImpl#createCustomerFromId(Long)}.
   * <ul>
   *   <li>Then calls {@link CustomerDao#readCustomerById(Long)}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#createCustomerFromId(Long)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.createCustomerFromId(Long)"})
  public void testCreateCustomerFromId_thenCallsReadCustomerById() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerDao.readCustomerById(Mockito.<Long>any())).thenReturn(customerImpl);

    // Act
    Customer actualCreateCustomerFromIdResult = customerServiceImpl.createCustomerFromId(1L);

    // Assert
    verify(customerDao).readCustomerById(eq(1L));
    assertSame(customerImpl, actualCreateCustomerFromIdResult);
  }

  /**
   * Test {@link CustomerServiceImpl#findNextCustomerId()}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#findNextCustomerId()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Long CustomerServiceImpl.findNextCustomerId()"})
  public void testFindNextCustomerId() {
    // Arrange
    when(idGenerationService.findNextId(Mockito.<String>any())).thenReturn(1L);

    // Act
    Long actualFindNextCustomerIdResult = customerServiceImpl.findNextCustomerId();

    // Assert
    verify(idGenerationService).findNextId(eq("org.broadleafcommerce.profile.core.domain.Customer"));
    assertEquals(1L, actualFindNextCustomerIdResult.longValue());
  }

  /**
   * Test {@link CustomerServiceImpl#createNewCustomer()}.
   * <ul>
   *   <li>Then return {@link CustomerImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#createNewCustomer()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.createNewCustomer()"})
  public void testCreateNewCustomer_thenReturnCustomerImpl() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerDao.create()).thenReturn(customerImpl);
    when(idGenerationService.findNextId(Mockito.<String>any())).thenReturn(1L);

    // Act
    Customer actualCreateNewCustomerResult = customerServiceImpl.createNewCustomer();

    // Assert
    verify(idGenerationService).findNextId(eq("org.broadleafcommerce.profile.core.domain.Customer"));
    verify(customerDao).create();
    assertSame(customerImpl, actualCreateNewCustomerResult);
  }

  /**
   * Test {@link CustomerServiceImpl#deleteCustomer(Customer)}.
   * <ul>
   *   <li>When {@link CustomerImpl} (default constructor).</li>
   *   <li>Then calls {@link CustomerDao#delete(Customer)}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#deleteCustomer(Customer)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerServiceImpl.deleteCustomer(Customer)"})
  public void testDeleteCustomer_whenCustomerImpl_thenCallsDelete() {
    // Arrange
    doNothing().when(customerDao).delete(Mockito.<Customer>any());
    doNothing().when(roleDao).removeCustomerRolesByCustomerId(Mockito.<Long>any());

    // Act
    customerServiceImpl.deleteCustomer(new CustomerImpl());

    // Assert
    verify(customerDao).delete(isA(Customer.class));
    verify(roleDao).removeCustomerRolesByCustomerId(isNull());
  }

  /**
   * Test {@link CustomerServiceImpl#readCustomerByUsername(String)} with {@code username}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#readCustomerByUsername(String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.readCustomerByUsername(String)"})
  public void testReadCustomerByUsernameWithUsername() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);

    // Act
    Customer actualReadCustomerByUsernameResult = customerServiceImpl.readCustomerByUsername("janedoe");

    // Assert
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    assertSame(customerImpl, actualReadCustomerByUsernameResult);
  }

  /**
   * Test {@link CustomerServiceImpl#readCustomerByUsername(String, Boolean)} with {@code username}, {@code cacheable}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#readCustomerByUsername(String, Boolean)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.readCustomerByUsername(String, Boolean)"})
  public void testReadCustomerByUsernameWithUsernameCacheable() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerDao.readCustomerByUsername(Mockito.<String>any(), Mockito.<Boolean>any())).thenReturn(customerImpl);

    // Act
    Customer actualReadCustomerByUsernameResult = customerServiceImpl.readCustomerByUsername("janedoe", true);

    // Assert
    verify(customerDao).readCustomerByUsername(eq("janedoe"), eq(true));
    assertSame(customerImpl, actualReadCustomerByUsernameResult);
  }

  /**
   * Test {@link CustomerServiceImpl#readCustomerById(Long)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#readCustomerById(Long)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.readCustomerById(Long)"})
  public void testReadCustomerById() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerDao.readCustomerById(Mockito.<Long>any())).thenReturn(customerImpl);

    // Act
    Customer actualReadCustomerByIdResult = customerServiceImpl.readCustomerById(1L);

    // Assert
    verify(customerDao).readCustomerById(eq(1L));
    assertSame(customerImpl, actualReadCustomerByIdResult);
  }

  /**
   * Test {@link CustomerServiceImpl#readCustomerByExternalId(String)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#readCustomerByExternalId(String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerServiceImpl.readCustomerByExternalId(String)"})
  public void testReadCustomerByExternalId() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();
    when(customerDao.readCustomerByExternalId(Mockito.<String>any())).thenReturn(customerImpl);

    // Act
    Customer actualReadCustomerByExternalIdResult = customerServiceImpl.readCustomerByExternalId("42");

    // Assert
    verify(customerDao).readCustomerByExternalId(eq("42"));
    assertSame(customerImpl, actualReadCustomerByExternalIdResult);
  }

  /**
   * Test {@link CustomerServiceImpl#encodePassword(String)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#encodePassword(String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"String CustomerServiceImpl.encodePassword(String)"})
  public void testEncodePassword() {
    // Arrange
    when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");

    // Act
    String actualEncodePasswordResult = customerServiceImpl.encodePassword("iloveyou");

    // Assert
    verify(passwordEncoder).encode(isA(CharSequence.class));
    assertEquals("secret", actualEncodePasswordResult);
  }

  /**
   * Test {@link CustomerServiceImpl#isPasswordValid(String, String)}.
   * <ul>
   *   <li>Given {@link PasswordEncoder} {@link PasswordEncoder#matches(CharSequence, String)} return {@code false}.</li>
   *   <li>Then return {@code false}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#isPasswordValid(String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerServiceImpl.isPasswordValid(String, String)"})
  public void testIsPasswordValid_givenPasswordEncoderMatchesReturnFalse_thenReturnFalse() {
    // Arrange
    when(passwordEncoder.matches(Mockito.<CharSequence>any(), Mockito.<String>any())).thenReturn(false);

    // Act
    boolean actualIsPasswordValidResult = customerServiceImpl.isPasswordValid("iloveyou", "secret");

    // Assert
    verify(passwordEncoder).matches(isA(CharSequence.class), eq("secret"));
    assertFalse(actualIsPasswordValidResult);
  }

  /**
   * Test {@link CustomerServiceImpl#isPasswordValid(String, String)}.
   * <ul>
   *   <li>Given {@link PasswordEncoder} {@link PasswordEncoder#matches(CharSequence, String)} return {@code true}.</li>
   *   <li>Then return {@code true}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#isPasswordValid(String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerServiceImpl.isPasswordValid(String, String)"})
  public void testIsPasswordValid_givenPasswordEncoderMatchesReturnTrue_thenReturnTrue() {
    // Arrange
    when(passwordEncoder.matches(Mockito.<CharSequence>any(), Mockito.<String>any())).thenReturn(true);

    // Act
    boolean actualIsPasswordValidResult = customerServiceImpl.isPasswordValid("iloveyou", "secret");

    // Assert
    verify(passwordEncoder).matches(isA(CharSequence.class), eq("secret"));
    assertTrue(actualIsPasswordValidResult);
  }

  /**
   * Test {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerServiceImpl.customerPassesCustomerRule(Customer, CustomerRuleHolder)"})
  public void testCustomerPassesCustomerRule() {
    // Arrange
    CustomerImpl customer = new CustomerImpl();

    // Act and Assert
    assertTrue(customerServiceImpl.customerPassesCustomerRule(customer, new CustomerRuleHolder("")));
  }

  /**
   * Test {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerServiceImpl.customerPassesCustomerRule(Customer, CustomerRuleHolder)"})
  public void testCustomerPassesCustomerRule2() {
    // Arrange
    CustomerImpl customer = new CustomerImpl();

    // Act and Assert
    assertFalse(
        customerServiceImpl.customerPassesCustomerRule(customer, new CustomerRuleHolder("getProductAttributes()[xx]")));
  }

  /**
   * Test {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerServiceImpl.customerPassesCustomerRule(Customer, CustomerRuleHolder)"})
  public void testCustomerPassesCustomerRule3() {
    // Arrange
    CustomerImpl customer = new CustomerImpl();

    // Act and Assert
    assertFalse(customerServiceImpl.customerPassesCustomerRule(customer,
        new CustomerRuleHolder("getCategoryAttributesMap()[xx]")));
  }

  /**
   * Test {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerServiceImpl.customerPassesCustomerRule(Customer, CustomerRuleHolder)"})
  public void testCustomerPassesCustomerRule4() {
    // Arrange
    CustomerImpl customer = new CustomerImpl();

    // Act and Assert
    assertFalse(
        customerServiceImpl.customerPassesCustomerRule(customer, new CustomerRuleHolder("getSkuAttributes()[xx]")));
  }

  /**
   * Test {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerServiceImpl.customerPassesCustomerRule(Customer, CustomerRuleHolder)"})
  public void testCustomerPassesCustomerRule5() {
    // Arrange
    CustomerImpl customer = new CustomerImpl();

    // Act and Assert
    assertFalse(customerServiceImpl.customerPassesCustomerRule(customer,
        new CustomerRuleHolder("getOrderItemAttributes()[xx]")));
  }

  /**
   * Test {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerServiceImpl.customerPassesCustomerRule(Customer, CustomerRuleHolder)"})
  public void testCustomerPassesCustomerRule6() {
    // Arrange
    CustomerImpl customer = new CustomerImpl();

    // Act and Assert
    assertFalse(customerServiceImpl.customerPassesCustomerRule(customer,
        new CustomerRuleHolder("getCustomerAttributes()[xx]")));
  }

  /**
   * Test {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerServiceImpl.customerPassesCustomerRule(Customer, CustomerRuleHolder)"})
  public void testCustomerPassesCustomerRule7() {
    // Arrange
    CustomerImpl customer = new CustomerImpl();

    // Act and Assert
    assertFalse(customerServiceImpl.customerPassesCustomerRule(customer,
        new CustomerRuleHolder("getAdditionalAttributes()[xx]")));
  }

  /**
   * Test {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerServiceImpl.customerPassesCustomerRule(Customer, CustomerRuleHolder)"})
  public void testCustomerPassesCustomerRule8() {
    // Arrange
    CustomerImpl customer = new CustomerImpl();

    // Act and Assert
    assertFalse(
        customerServiceImpl.customerPassesCustomerRule(customer, new CustomerRuleHolder("getAdditionalFields()[xx]")));
  }

  /**
   * Test {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerServiceImpl.customerPassesCustomerRule(Customer, CustomerRuleHolder)"})
  public void testCustomerPassesCustomerRule9() {
    // Arrange, Act and Assert
    assertFalse(
        customerServiceImpl.customerPassesCustomerRule(null, new CustomerRuleHolder("getProductAttributes()[xx]")));
  }

  /**
   * Test {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}.
   * <ul>
   *   <li>When {@link CustomerRuleHolder#CustomerRuleHolder(String)} with customerRule is {@code 42}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerServiceImpl.customerPassesCustomerRule(Customer, CustomerRuleHolder)"})
  public void testCustomerPassesCustomerRule_whenCustomerRuleHolderWithCustomerRuleIs42() {
    // Arrange
    CustomerImpl customer = new CustomerImpl();

    // Act and Assert
    assertFalse(customerServiceImpl.customerPassesCustomerRule(customer, new CustomerRuleHolder("42")));
  }

  /**
   * Test {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}.
   * <ul>
   *   <li>When {@link CustomerRuleHolder#CustomerRuleHolder(String)} with customerRule is {@code customer}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerServiceImpl.customerPassesCustomerRule(Customer, CustomerRuleHolder)"})
  public void testCustomerPassesCustomerRule_whenCustomerRuleHolderWithCustomerRuleIsCustomer() {
    // Arrange
    CustomerImpl customer = new CustomerImpl();

    // Act and Assert
    assertFalse(customerServiceImpl.customerPassesCustomerRule(customer, new CustomerRuleHolder("customer")));
  }

  /**
   * Test {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}.
   * <ul>
   *   <li>When {@link CustomerRuleHolder#CustomerRuleHolder(String)} with customerRule is {@code customer}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerServiceImpl.customerPassesCustomerRule(Customer, CustomerRuleHolder)"})
  public void testCustomerPassesCustomerRule_whenCustomerRuleHolderWithCustomerRuleIsCustomer2() {
    // Arrange, Act and Assert
    assertTrue(customerServiceImpl.customerPassesCustomerRule(null, new CustomerRuleHolder("customer")));
  }

  /**
   * Test {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}.
   * <ul>
   *   <li>When {@link CustomerRuleHolder#CustomerRuleHolder(String)} with customerRule is {@code MVEL}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerServiceImpl.customerPassesCustomerRule(Customer, CustomerRuleHolder)"})
  public void testCustomerPassesCustomerRule_whenCustomerRuleHolderWithCustomerRuleIsMvel() {
    // Arrange
    CustomerImpl customer = new CustomerImpl();

    // Act and Assert
    assertFalse(customerServiceImpl.customerPassesCustomerRule(customer, new CustomerRuleHolder("MVEL")));
  }

  /**
   * Test {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}.
   * <ul>
   *   <li>When {@link CustomerRuleHolder#CustomerRuleHolder(String)} with customerRule is {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#customerPassesCustomerRule(Customer, CustomerRuleHolder)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerServiceImpl.customerPassesCustomerRule(Customer, CustomerRuleHolder)"})
  public void testCustomerPassesCustomerRule_whenCustomerRuleHolderWithCustomerRuleIsNull() {
    // Arrange
    CustomerImpl customer = new CustomerImpl();

    // Act and Assert
    assertTrue(customerServiceImpl.customerPassesCustomerRule(customer, new CustomerRuleHolder(null)));
  }

  /**
   * Test {@link CustomerServiceImpl#buildCustomerRuleParams(Customer)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#buildCustomerRuleParams(Customer)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Map CustomerServiceImpl.buildCustomerRuleParams(Customer)"})
  public void testBuildCustomerRuleParams() {
    // Arrange
    CustomerImpl customer = new CustomerImpl();

    // Act
    Map<String, Object> actualBuildCustomerRuleParamsResult = customerServiceImpl.buildCustomerRuleParams(customer);

    // Assert
    assertEquals(1, actualBuildCustomerRuleParamsResult.size());
    Object getResult = actualBuildCustomerRuleParamsResult.get("customer");
    assertTrue(getResult instanceof CustomerImpl);
    assertSame(customer, getResult);
  }

  /**
   * Test {@link CustomerServiceImpl#sendForgotUsernameNotification(String)}.
   * <ul>
   *   <li>Given {@link CustomerDao}.</li>
   *   <li>When {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#sendForgotUsernameNotification(String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.sendForgotUsernameNotification(String)"})
  public void testSendForgotUsernameNotification_givenCustomerDao_whenNull() {
    // Arrange and Act
    GenericResponse actualSendForgotUsernameNotificationResult = customerServiceImpl
        .sendForgotUsernameNotification(null);

    // Assert
    List<String> errorCodesList = actualSendForgotUsernameNotificationResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("notFound", errorCodesList.get(0));
    assertTrue(actualSendForgotUsernameNotificationResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#sendForgotUsernameNotification(String)}.
   * <ul>
   *   <li>Then return ErrorCodesList first is {@code inactiveUser}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#sendForgotUsernameNotification(String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.sendForgotUsernameNotification(String)"})
  public void testSendForgotUsernameNotification_thenReturnErrorCodesListFirstIsInactiveUser() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.isDeactivated()).thenReturn(true);

    ArrayList<Customer> customerList = new ArrayList<>();
    customerList.add(customerImpl);
    when(customerDao.readCustomersByEmail(Mockito.<String>any())).thenReturn(customerList);

    // Act
    GenericResponse actualSendForgotUsernameNotificationResult = customerServiceImpl
        .sendForgotUsernameNotification("42 Main St");

    // Assert
    verify(customerDao).readCustomersByEmail(eq("42 Main St"));
    verify(customerImpl).isDeactivated();
    List<String> errorCodesList = actualSendForgotUsernameNotificationResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("inactiveUser", errorCodesList.get(0));
    assertTrue(actualSendForgotUsernameNotificationResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#sendForgotUsernameNotification(String)}.
   * <ul>
   *   <li>Then return ErrorCodesList first is {@code notFound}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#sendForgotUsernameNotification(String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.sendForgotUsernameNotification(String)"})
  public void testSendForgotUsernameNotification_thenReturnErrorCodesListFirstIsNotFound() {
    // Arrange
    when(customerDao.readCustomersByEmail(Mockito.<String>any())).thenReturn(new ArrayList<>());

    // Act
    GenericResponse actualSendForgotUsernameNotificationResult = customerServiceImpl
        .sendForgotUsernameNotification("42 Main St");

    // Assert
    verify(customerDao).readCustomersByEmail(eq("42 Main St"));
    List<String> errorCodesList = actualSendForgotUsernameNotificationResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("notFound", errorCodesList.get(0));
    assertTrue(actualSendForgotUsernameNotificationResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#sendForgotUsernameNotification(String)}.
   * <ul>
   *   <li>Then return not HasErrors.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#sendForgotUsernameNotification(String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.sendForgotUsernameNotification(String)"})
  public void testSendForgotUsernameNotification_thenReturnNotHasErrors() {
    // Arrange
    ArrayList<Customer> customerList = new ArrayList<>();
    customerList.add(new CustomerImpl());
    when(customerDao.readCustomersByEmail(Mockito.<String>any())).thenReturn(customerList);
    when(emailService.sendTemplateEmail(Mockito.<String>any(), Mockito.<EmailInfo>any(),
        Mockito.<Map<String, Object>>any())).thenReturn(true);

    // Act
    GenericResponse actualSendForgotUsernameNotificationResult = customerServiceImpl
        .sendForgotUsernameNotification("42 Main St");

    // Assert
    verify(emailService).sendTemplateEmail(eq("42 Main St"), (EmailInfo) isNull(), isA(Map.class));
    verify(customerDao).readCustomersByEmail(eq("42 Main St"));
    assertFalse(actualSendForgotUsernameNotificationResult.getHasErrors());
    assertTrue(actualSendForgotUsernameNotificationResult.getErrorCodesList().isEmpty());
  }

  /**
   * Test {@link CustomerServiceImpl#sendForgotPasswordNotification(String, String)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#sendForgotPasswordNotification(String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.sendForgotPasswordNotification(String, String)"})
  public void testSendForgotPasswordNotification() {
    // Arrange
    customerServiceImpl.setPasswordTokenLength(0);

    // Act
    GenericResponse actualSendForgotPasswordNotificationResult = customerServiceImpl
        .sendForgotPasswordNotification(null, "");

    // Assert
    List<String> errorCodesList = actualSendForgotPasswordNotificationResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("invalidCustomer", errorCodesList.get(0));
    assertTrue(actualSendForgotPasswordNotificationResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#sendForgotPasswordNotification(String, String)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#sendForgotPasswordNotification(String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.sendForgotPasswordNotification(String, String)"})
  public void testSendForgotPasswordNotification2() {
    // Arrange
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(new CustomerImpl());

    // Act
    GenericResponse actualSendForgotPasswordNotificationResult = customerServiceImpl
        .sendForgotPasswordNotification("janedoe", "https://example.org/example");

    // Assert
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    List<String> errorCodesList = actualSendForgotPasswordNotificationResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("emailNotFound", errorCodesList.get(0));
    assertTrue(actualSendForgotPasswordNotificationResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#sendForgotPasswordNotification(String, String)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#sendForgotPasswordNotification(String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.sendForgotPasswordNotification(String, String)"})
  public void testSendForgotPasswordNotification3() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.getEmailAddress()).thenReturn("");
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);

    // Act
    GenericResponse actualSendForgotPasswordNotificationResult = customerServiceImpl
        .sendForgotPasswordNotification("janedoe", "https://example.org/example");

    // Assert
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerImpl).getEmailAddress();
    List<String> errorCodesList = actualSendForgotPasswordNotificationResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("emailNotFound", errorCodesList.get(0));
    assertTrue(actualSendForgotPasswordNotificationResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#sendForgotPasswordNotification(String, String)}.
   * <ul>
   *   <li>Then return ErrorCodesList first is {@code inactiveUser}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#sendForgotPasswordNotification(String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.sendForgotPasswordNotification(String, String)"})
  public void testSendForgotPasswordNotification_thenReturnErrorCodesListFirstIsInactiveUser() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.isDeactivated()).thenReturn(true);
    when(customerImpl.getEmailAddress()).thenReturn("42 Main St");
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);

    // Act
    GenericResponse actualSendForgotPasswordNotificationResult = customerServiceImpl
        .sendForgotPasswordNotification("janedoe", "https://example.org/example");

    // Assert
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerImpl).getEmailAddress();
    verify(customerImpl).isDeactivated();
    List<String> errorCodesList = actualSendForgotPasswordNotificationResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("inactiveUser", errorCodesList.get(0));
    assertTrue(actualSendForgotPasswordNotificationResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#sendForgotPasswordNotification(String, String)}.
   * <ul>
   *   <li>Then return not HasErrors.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#sendForgotPasswordNotification(String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.sendForgotPasswordNotification(String, String)"})
  public void testSendForgotPasswordNotification_thenReturnNotHasErrors() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.isDeactivated()).thenReturn(false);
    when(customerImpl.getId()).thenReturn(1L);
    when(customerImpl.getEmailAddress()).thenReturn("42 Main St");
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);
    when(customerForgotPasswordSecurityTokenDao.saveToken(Mockito.<CustomerForgotPasswordSecurityToken>any()))
        .thenReturn(new CustomerForgotPasswordSecurityTokenImpl());
    when(emailService.sendTemplateEmail(Mockito.<String>any(), Mockito.<EmailInfo>any(),
        Mockito.<Map<String, Object>>any())).thenReturn(true);
    when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");

    // Act
    GenericResponse actualSendForgotPasswordNotificationResult = customerServiceImpl
        .sendForgotPasswordNotification("janedoe", "https://example.org/example");

    // Assert
    verify(emailService).sendTemplateEmail(eq("42 Main St"), (EmailInfo) isNull(), isA(Map.class));
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerForgotPasswordSecurityTokenDao).saveToken(isA(CustomerForgotPasswordSecurityToken.class));
    verify(customerImpl, atLeast(1)).getEmailAddress();
    verify(customerImpl).getId();
    verify(customerImpl).isDeactivated();
    verify(passwordEncoder).encode(isA(CharSequence.class));
    assertFalse(actualSendForgotPasswordNotificationResult.getHasErrors());
    assertTrue(actualSendForgotPasswordNotificationResult.getErrorCodesList().isEmpty());
  }

  /**
   * Test {@link CustomerServiceImpl#sendForgotPasswordNotification(String, String)}.
   * <ul>
   *   <li>When empty string.</li>
   *   <li>Then return not HasErrors.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#sendForgotPasswordNotification(String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.sendForgotPasswordNotification(String, String)"})
  public void testSendForgotPasswordNotification_whenEmptyString_thenReturnNotHasErrors() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.isDeactivated()).thenReturn(false);
    when(customerImpl.getId()).thenReturn(1L);
    when(customerImpl.getEmailAddress()).thenReturn("42 Main St");
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);
    when(customerForgotPasswordSecurityTokenDao.saveToken(Mockito.<CustomerForgotPasswordSecurityToken>any()))
        .thenReturn(new CustomerForgotPasswordSecurityTokenImpl());
    when(emailService.sendTemplateEmail(Mockito.<String>any(), Mockito.<EmailInfo>any(),
        Mockito.<Map<String, Object>>any())).thenReturn(true);
    when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");

    // Act
    GenericResponse actualSendForgotPasswordNotificationResult = customerServiceImpl
        .sendForgotPasswordNotification("janedoe", "");

    // Assert
    verify(emailService).sendTemplateEmail(eq("42 Main St"), (EmailInfo) isNull(), isA(Map.class));
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerForgotPasswordSecurityTokenDao).saveToken(isA(CustomerForgotPasswordSecurityToken.class));
    verify(customerImpl, atLeast(1)).getEmailAddress();
    verify(customerImpl).getId();
    verify(customerImpl).isDeactivated();
    verify(passwordEncoder).encode(isA(CharSequence.class));
    assertFalse(actualSendForgotPasswordNotificationResult.getHasErrors());
    assertTrue(actualSendForgotPasswordNotificationResult.getErrorCodesList().isEmpty());
  }

  /**
   * Test {@link CustomerServiceImpl#sendForgotPasswordNotification(String, String)}.
   * <ul>
   *   <li>When {@code null}.</li>
   *   <li>Then return not HasErrors.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#sendForgotPasswordNotification(String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.sendForgotPasswordNotification(String, String)"})
  public void testSendForgotPasswordNotification_whenNull_thenReturnNotHasErrors() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.isDeactivated()).thenReturn(false);
    when(customerImpl.getId()).thenReturn(1L);
    when(customerImpl.getEmailAddress()).thenReturn("42 Main St");
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);
    when(customerForgotPasswordSecurityTokenDao.saveToken(Mockito.<CustomerForgotPasswordSecurityToken>any()))
        .thenReturn(new CustomerForgotPasswordSecurityTokenImpl());
    when(emailService.sendTemplateEmail(Mockito.<String>any(), Mockito.<EmailInfo>any(),
        Mockito.<Map<String, Object>>any())).thenReturn(true);
    when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");

    // Act
    GenericResponse actualSendForgotPasswordNotificationResult = customerServiceImpl
        .sendForgotPasswordNotification("janedoe", null);

    // Assert
    verify(emailService).sendTemplateEmail(eq("42 Main St"), (EmailInfo) isNull(), isA(Map.class));
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerForgotPasswordSecurityTokenDao).saveToken(isA(CustomerForgotPasswordSecurityToken.class));
    verify(customerImpl, atLeast(1)).getEmailAddress();
    verify(customerImpl).getId();
    verify(customerImpl).isDeactivated();
    verify(passwordEncoder).encode(isA(CharSequence.class));
    assertFalse(actualSendForgotPasswordNotificationResult.getHasErrors());
    assertTrue(actualSendForgotPasswordNotificationResult.getErrorCodesList().isEmpty());
  }

  /**
   * Test {@link CustomerServiceImpl#sendForgotPasswordNotification(String, String)}.
   * <ul>
   *   <li>When {@code ?}.</li>
   *   <li>Then return not HasErrors.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#sendForgotPasswordNotification(String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.sendForgotPasswordNotification(String, String)"})
  public void testSendForgotPasswordNotification_whenQuestionMark_thenReturnNotHasErrors() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.isDeactivated()).thenReturn(false);
    when(customerImpl.getId()).thenReturn(1L);
    when(customerImpl.getEmailAddress()).thenReturn("42 Main St");
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);
    when(customerForgotPasswordSecurityTokenDao.saveToken(Mockito.<CustomerForgotPasswordSecurityToken>any()))
        .thenReturn(new CustomerForgotPasswordSecurityTokenImpl());
    when(emailService.sendTemplateEmail(Mockito.<String>any(), Mockito.<EmailInfo>any(),
        Mockito.<Map<String, Object>>any())).thenReturn(true);
    when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");

    // Act
    GenericResponse actualSendForgotPasswordNotificationResult = customerServiceImpl
        .sendForgotPasswordNotification("janedoe", "?");

    // Assert
    verify(emailService).sendTemplateEmail(eq("42 Main St"), (EmailInfo) isNull(), isA(Map.class));
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerForgotPasswordSecurityTokenDao).saveToken(isA(CustomerForgotPasswordSecurityToken.class));
    verify(customerImpl, atLeast(1)).getEmailAddress();
    verify(customerImpl).getId();
    verify(customerImpl).isDeactivated();
    verify(passwordEncoder).encode(isA(CharSequence.class));
    assertFalse(actualSendForgotPasswordNotificationResult.getHasErrors());
    assertTrue(actualSendForgotPasswordNotificationResult.getErrorCodesList().isEmpty());
  }

  /**
   * Test {@link CustomerServiceImpl#sendForcedPasswordChangeNotification(String, String)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#sendForcedPasswordChangeNotification(String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.sendForcedPasswordChangeNotification(String, String)"})
  public void testSendForcedPasswordChangeNotification() {
    // Arrange
    customerServiceImpl.setPasswordTokenLength(0);

    // Act
    GenericResponse actualSendForcedPasswordChangeNotificationResult = customerServiceImpl
        .sendForcedPasswordChangeNotification(null, null);

    // Assert
    List<String> errorCodesList = actualSendForcedPasswordChangeNotificationResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("invalidCustomer", errorCodesList.get(0));
    assertTrue(actualSendForcedPasswordChangeNotificationResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#sendForcedPasswordChangeNotification(String, String)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#sendForcedPasswordChangeNotification(String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.sendForcedPasswordChangeNotification(String, String)"})
  public void testSendForcedPasswordChangeNotification2() {
    // Arrange
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(new CustomerImpl());

    // Act
    GenericResponse actualSendForcedPasswordChangeNotificationResult = customerServiceImpl
        .sendForcedPasswordChangeNotification("janedoe", "https://example.org/example");

    // Assert
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    List<String> errorCodesList = actualSendForcedPasswordChangeNotificationResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("emailNotFound", errorCodesList.get(0));
    assertTrue(actualSendForcedPasswordChangeNotificationResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#sendForcedPasswordChangeNotification(String, String)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#sendForcedPasswordChangeNotification(String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.sendForcedPasswordChangeNotification(String, String)"})
  public void testSendForcedPasswordChangeNotification3() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.isDeactivated()).thenReturn(true);
    when(customerImpl.getEmailAddress()).thenReturn("42 Main St");
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);

    // Act
    GenericResponse actualSendForcedPasswordChangeNotificationResult = customerServiceImpl
        .sendForcedPasswordChangeNotification("janedoe", "https://example.org/example");

    // Assert
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerImpl).getEmailAddress();
    verify(customerImpl).isDeactivated();
    List<String> errorCodesList = actualSendForcedPasswordChangeNotificationResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("inactiveUser", errorCodesList.get(0));
    assertTrue(actualSendForcedPasswordChangeNotificationResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#sendForcedPasswordChangeNotification(String, String)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#sendForcedPasswordChangeNotification(String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.sendForcedPasswordChangeNotification(String, String)"})
  public void testSendForcedPasswordChangeNotification4() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.getEmailAddress()).thenReturn("");
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);

    // Act
    GenericResponse actualSendForcedPasswordChangeNotificationResult = customerServiceImpl
        .sendForcedPasswordChangeNotification("janedoe", "https://example.org/example");

    // Assert
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerImpl).getEmailAddress();
    List<String> errorCodesList = actualSendForcedPasswordChangeNotificationResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("emailNotFound", errorCodesList.get(0));
    assertTrue(actualSendForcedPasswordChangeNotificationResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#sendForcedPasswordChangeNotification(String, String)}.
   * <ul>
   *   <li>Then return not HasErrors.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#sendForcedPasswordChangeNotification(String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.sendForcedPasswordChangeNotification(String, String)"})
  public void testSendForcedPasswordChangeNotification_thenReturnNotHasErrors() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.isDeactivated()).thenReturn(false);
    when(customerImpl.getId()).thenReturn(1L);
    when(customerImpl.getEmailAddress()).thenReturn("42 Main St");
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);
    when(customerForgotPasswordSecurityTokenDao.saveToken(Mockito.<CustomerForgotPasswordSecurityToken>any()))
        .thenReturn(new CustomerForgotPasswordSecurityTokenImpl());
    when(emailService.sendTemplateEmail(Mockito.<String>any(), Mockito.<EmailInfo>any(),
        Mockito.<Map<String, Object>>any())).thenReturn(true);
    when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");

    // Act
    GenericResponse actualSendForcedPasswordChangeNotificationResult = customerServiceImpl
        .sendForcedPasswordChangeNotification("janedoe", "https://example.org/example");

    // Assert
    verify(emailService).sendTemplateEmail(eq("42 Main St"), (EmailInfo) isNull(), isA(Map.class));
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerForgotPasswordSecurityTokenDao).saveToken(isA(CustomerForgotPasswordSecurityToken.class));
    verify(customerImpl, atLeast(1)).getEmailAddress();
    verify(customerImpl).getId();
    verify(customerImpl).isDeactivated();
    verify(passwordEncoder).encode(isA(CharSequence.class));
    assertFalse(actualSendForcedPasswordChangeNotificationResult.getHasErrors());
    assertTrue(actualSendForcedPasswordChangeNotificationResult.getErrorCodesList().isEmpty());
  }

  /**
   * Test {@link CustomerServiceImpl#sendForcedPasswordChangeNotification(String, String)}.
   * <ul>
   *   <li>When empty string.</li>
   *   <li>Then return not HasErrors.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#sendForcedPasswordChangeNotification(String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.sendForcedPasswordChangeNotification(String, String)"})
  public void testSendForcedPasswordChangeNotification_whenEmptyString_thenReturnNotHasErrors() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.isDeactivated()).thenReturn(false);
    when(customerImpl.getId()).thenReturn(1L);
    when(customerImpl.getEmailAddress()).thenReturn("42 Main St");
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);
    when(customerForgotPasswordSecurityTokenDao.saveToken(Mockito.<CustomerForgotPasswordSecurityToken>any()))
        .thenReturn(new CustomerForgotPasswordSecurityTokenImpl());
    when(emailService.sendTemplateEmail(Mockito.<String>any(), Mockito.<EmailInfo>any(),
        Mockito.<Map<String, Object>>any())).thenReturn(true);
    when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");

    // Act
    GenericResponse actualSendForcedPasswordChangeNotificationResult = customerServiceImpl
        .sendForcedPasswordChangeNotification("janedoe", "");

    // Assert
    verify(emailService).sendTemplateEmail(eq("42 Main St"), (EmailInfo) isNull(), isA(Map.class));
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerForgotPasswordSecurityTokenDao).saveToken(isA(CustomerForgotPasswordSecurityToken.class));
    verify(customerImpl, atLeast(1)).getEmailAddress();
    verify(customerImpl).getId();
    verify(customerImpl).isDeactivated();
    verify(passwordEncoder).encode(isA(CharSequence.class));
    assertFalse(actualSendForcedPasswordChangeNotificationResult.getHasErrors());
    assertTrue(actualSendForcedPasswordChangeNotificationResult.getErrorCodesList().isEmpty());
  }

  /**
   * Test {@link CustomerServiceImpl#sendForcedPasswordChangeNotification(String, String)}.
   * <ul>
   *   <li>When {@code null}.</li>
   *   <li>Then return not HasErrors.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#sendForcedPasswordChangeNotification(String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.sendForcedPasswordChangeNotification(String, String)"})
  public void testSendForcedPasswordChangeNotification_whenNull_thenReturnNotHasErrors() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.isDeactivated()).thenReturn(false);
    when(customerImpl.getId()).thenReturn(1L);
    when(customerImpl.getEmailAddress()).thenReturn("42 Main St");
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);
    when(customerForgotPasswordSecurityTokenDao.saveToken(Mockito.<CustomerForgotPasswordSecurityToken>any()))
        .thenReturn(new CustomerForgotPasswordSecurityTokenImpl());
    when(emailService.sendTemplateEmail(Mockito.<String>any(), Mockito.<EmailInfo>any(),
        Mockito.<Map<String, Object>>any())).thenReturn(true);
    when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");

    // Act
    GenericResponse actualSendForcedPasswordChangeNotificationResult = customerServiceImpl
        .sendForcedPasswordChangeNotification("janedoe", null);

    // Assert
    verify(emailService).sendTemplateEmail(eq("42 Main St"), (EmailInfo) isNull(), isA(Map.class));
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerForgotPasswordSecurityTokenDao).saveToken(isA(CustomerForgotPasswordSecurityToken.class));
    verify(customerImpl, atLeast(1)).getEmailAddress();
    verify(customerImpl).getId();
    verify(customerImpl).isDeactivated();
    verify(passwordEncoder).encode(isA(CharSequence.class));
    assertFalse(actualSendForcedPasswordChangeNotificationResult.getHasErrors());
    assertTrue(actualSendForcedPasswordChangeNotificationResult.getErrorCodesList().isEmpty());
  }

  /**
   * Test {@link CustomerServiceImpl#sendForcedPasswordChangeNotification(String, String)}.
   * <ul>
   *   <li>When {@code ?}.</li>
   *   <li>Then return not HasErrors.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#sendForcedPasswordChangeNotification(String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.sendForcedPasswordChangeNotification(String, String)"})
  public void testSendForcedPasswordChangeNotification_whenQuestionMark_thenReturnNotHasErrors() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.isDeactivated()).thenReturn(false);
    when(customerImpl.getId()).thenReturn(1L);
    when(customerImpl.getEmailAddress()).thenReturn("42 Main St");
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);
    when(customerForgotPasswordSecurityTokenDao.saveToken(Mockito.<CustomerForgotPasswordSecurityToken>any()))
        .thenReturn(new CustomerForgotPasswordSecurityTokenImpl());
    when(emailService.sendTemplateEmail(Mockito.<String>any(), Mockito.<EmailInfo>any(),
        Mockito.<Map<String, Object>>any())).thenReturn(true);
    when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");

    // Act
    GenericResponse actualSendForcedPasswordChangeNotificationResult = customerServiceImpl
        .sendForcedPasswordChangeNotification("janedoe", "?");

    // Assert
    verify(emailService).sendTemplateEmail(eq("42 Main St"), (EmailInfo) isNull(), isA(Map.class));
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerForgotPasswordSecurityTokenDao).saveToken(isA(CustomerForgotPasswordSecurityToken.class));
    verify(customerImpl, atLeast(1)).getEmailAddress();
    verify(customerImpl).getId();
    verify(customerImpl).isDeactivated();
    verify(passwordEncoder).encode(isA(CharSequence.class));
    assertFalse(actualSendForcedPasswordChangeNotificationResult.getHasErrors());
    assertTrue(actualSendForcedPasswordChangeNotificationResult.getErrorCodesList().isEmpty());
  }

  /**
   * Test {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer)} with {@code token}, {@code customer}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.checkPasswordResetToken(String, Customer)"})
  public void testCheckPasswordResetTokenWithTokenCustomer() {
    // Arrange and Act
    GenericResponse actualCheckPasswordResetTokenResult = customerServiceImpl.checkPasswordResetToken(null, null);

    // Assert
    List<String> errorCodesList = actualCheckPasswordResetTokenResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("invalidToken", errorCodesList.get(0));
    assertTrue(actualCheckPasswordResetTokenResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer)} with {@code token}, {@code customer}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.checkPasswordResetToken(String, Customer)"})
  public void testCheckPasswordResetTokenWithTokenCustomer2() {
    // Arrange
    when(customerForgotPasswordSecurityTokenDao.readUnusedTokensByCustomerId(Mockito.<Long>any()))
        .thenReturn(new ArrayList<>());

    // Act
    GenericResponse actualCheckPasswordResetTokenResult = customerServiceImpl.checkPasswordResetToken("ABC123",
        new CustomerImpl());

    // Assert
    verify(customerForgotPasswordSecurityTokenDao).readUnusedTokensByCustomerId(isNull());
    List<String> errorCodesList = actualCheckPasswordResetTokenResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("invalidToken", errorCodesList.get(0));
    assertTrue(actualCheckPasswordResetTokenResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer)} with {@code token}, {@code customer}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.checkPasswordResetToken(String, Customer)"})
  public void testCheckPasswordResetTokenWithTokenCustomer3() {
    // Arrange
    CustomerForgotPasswordSecurityTokenImpl customerForgotPasswordSecurityTokenImpl = mock(
        CustomerForgotPasswordSecurityTokenImpl.class);
    when(customerForgotPasswordSecurityTokenImpl.isTokenUsedFlag()).thenReturn(true);
    when(customerForgotPasswordSecurityTokenImpl.getToken()).thenReturn("ABC123");

    ArrayList<CustomerForgotPasswordSecurityToken> customerForgotPasswordSecurityTokenList = new ArrayList<>();
    customerForgotPasswordSecurityTokenList.add(customerForgotPasswordSecurityTokenImpl);
    when(customerForgotPasswordSecurityTokenDao.readUnusedTokensByCustomerId(Mockito.<Long>any()))
        .thenReturn(customerForgotPasswordSecurityTokenList);
    when(passwordEncoder.matches(Mockito.<CharSequence>any(), Mockito.<String>any())).thenReturn(true);

    // Act
    GenericResponse actualCheckPasswordResetTokenResult = customerServiceImpl.checkPasswordResetToken("ABC123",
        new CustomerImpl());

    // Assert
    verify(customerForgotPasswordSecurityTokenDao).readUnusedTokensByCustomerId(isNull());
    verify(customerForgotPasswordSecurityTokenImpl).getToken();
    verify(customerForgotPasswordSecurityTokenImpl).isTokenUsedFlag();
    verify(passwordEncoder).matches(isA(CharSequence.class), eq("ABC123"));
    List<String> errorCodesList = actualCheckPasswordResetTokenResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("tokenUsed", errorCodesList.get(0));
    assertTrue(actualCheckPasswordResetTokenResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer)} with {@code token}, {@code customer}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.checkPasswordResetToken(String, Customer)"})
  public void testCheckPasswordResetTokenWithTokenCustomer4() {
    // Arrange
    CustomerForgotPasswordSecurityTokenImpl customerForgotPasswordSecurityTokenImpl = mock(
        CustomerForgotPasswordSecurityTokenImpl.class);
    when(customerForgotPasswordSecurityTokenImpl.getToken()).thenReturn("ABC123");

    ArrayList<CustomerForgotPasswordSecurityToken> customerForgotPasswordSecurityTokenList = new ArrayList<>();
    customerForgotPasswordSecurityTokenList.add(customerForgotPasswordSecurityTokenImpl);
    when(customerForgotPasswordSecurityTokenDao.readUnusedTokensByCustomerId(Mockito.<Long>any()))
        .thenReturn(customerForgotPasswordSecurityTokenList);
    when(passwordEncoder.matches(Mockito.<CharSequence>any(), Mockito.<String>any())).thenReturn(false);

    // Act
    GenericResponse actualCheckPasswordResetTokenResult = customerServiceImpl.checkPasswordResetToken("ABC123",
        new CustomerImpl());

    // Assert
    verify(customerForgotPasswordSecurityTokenDao).readUnusedTokensByCustomerId(isNull());
    verify(customerForgotPasswordSecurityTokenImpl).getToken();
    verify(passwordEncoder).matches(isA(CharSequence.class), eq("ABC123"));
    List<String> errorCodesList = actualCheckPasswordResetTokenResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("invalidToken", errorCodesList.get(0));
    assertTrue(actualCheckPasswordResetTokenResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer, GenericResponse)} with {@code token}, {@code customer}, {@code response}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer, GenericResponse)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({
      "CustomerForgotPasswordSecurityToken CustomerServiceImpl.checkPasswordResetToken(String, Customer, GenericResponse)"})
  public void testCheckPasswordResetTokenWithTokenCustomerResponse() {
    // Arrange
    when(customerForgotPasswordSecurityTokenDao.readUnusedTokensByCustomerId(Mockito.<Long>any()))
        .thenReturn(new ArrayList<>());
    CustomerImpl customer = new CustomerImpl();
    GenericResponse response = new GenericResponse();

    // Act
    CustomerForgotPasswordSecurityToken actualCheckPasswordResetTokenResult = customerServiceImpl
        .checkPasswordResetToken("ABC123", customer, response);

    // Assert
    verify(customerForgotPasswordSecurityTokenDao).readUnusedTokensByCustomerId(isNull());
    List<String> errorCodesList = response.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("invalidToken", errorCodesList.get(0));
    assertNull(actualCheckPasswordResetTokenResult);
    assertTrue(response.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer, GenericResponse)} with {@code token}, {@code customer}, {@code response}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer, GenericResponse)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({
      "CustomerForgotPasswordSecurityToken CustomerServiceImpl.checkPasswordResetToken(String, Customer, GenericResponse)"})
  public void testCheckPasswordResetTokenWithTokenCustomerResponse2() {
    // Arrange
    CustomerForgotPasswordSecurityTokenImpl customerForgotPasswordSecurityTokenImpl = mock(
        CustomerForgotPasswordSecurityTokenImpl.class);
    when(customerForgotPasswordSecurityTokenImpl.isTokenUsedFlag()).thenReturn(true);
    when(customerForgotPasswordSecurityTokenImpl.getToken()).thenReturn("ABC123");

    ArrayList<CustomerForgotPasswordSecurityToken> customerForgotPasswordSecurityTokenList = new ArrayList<>();
    customerForgotPasswordSecurityTokenList.add(customerForgotPasswordSecurityTokenImpl);
    when(customerForgotPasswordSecurityTokenDao.readUnusedTokensByCustomerId(Mockito.<Long>any()))
        .thenReturn(customerForgotPasswordSecurityTokenList);
    when(passwordEncoder.matches(Mockito.<CharSequence>any(), Mockito.<String>any())).thenReturn(true);
    CustomerImpl customer = new CustomerImpl();
    GenericResponse response = new GenericResponse();

    // Act
    customerServiceImpl.checkPasswordResetToken("ABC123", customer, response);

    // Assert
    verify(customerForgotPasswordSecurityTokenDao).readUnusedTokensByCustomerId(isNull());
    verify(customerForgotPasswordSecurityTokenImpl).getToken();
    verify(customerForgotPasswordSecurityTokenImpl).isTokenUsedFlag();
    verify(passwordEncoder).matches(isA(CharSequence.class), eq("ABC123"));
    List<String> errorCodesList = response.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("tokenUsed", errorCodesList.get(0));
    assertTrue(response.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer, GenericResponse)} with {@code token}, {@code customer}, {@code response}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer, GenericResponse)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({
      "CustomerForgotPasswordSecurityToken CustomerServiceImpl.checkPasswordResetToken(String, Customer, GenericResponse)"})
  public void testCheckPasswordResetTokenWithTokenCustomerResponse3() {
    // Arrange
    CustomerForgotPasswordSecurityTokenImpl customerForgotPasswordSecurityTokenImpl = mock(
        CustomerForgotPasswordSecurityTokenImpl.class);
    when(customerForgotPasswordSecurityTokenImpl.getToken()).thenReturn("ABC123");

    ArrayList<CustomerForgotPasswordSecurityToken> customerForgotPasswordSecurityTokenList = new ArrayList<>();
    customerForgotPasswordSecurityTokenList.add(customerForgotPasswordSecurityTokenImpl);
    when(customerForgotPasswordSecurityTokenDao.readUnusedTokensByCustomerId(Mockito.<Long>any()))
        .thenReturn(customerForgotPasswordSecurityTokenList);
    when(passwordEncoder.matches(Mockito.<CharSequence>any(), Mockito.<String>any())).thenReturn(false);
    CustomerImpl customer = new CustomerImpl();
    GenericResponse response = new GenericResponse();

    // Act
    CustomerForgotPasswordSecurityToken actualCheckPasswordResetTokenResult = customerServiceImpl
        .checkPasswordResetToken("ABC123", customer, response);

    // Assert
    verify(customerForgotPasswordSecurityTokenDao).readUnusedTokensByCustomerId(isNull());
    verify(customerForgotPasswordSecurityTokenImpl).getToken();
    verify(passwordEncoder).matches(isA(CharSequence.class), eq("ABC123"));
    List<String> errorCodesList = response.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("invalidToken", errorCodesList.get(0));
    assertNull(actualCheckPasswordResetTokenResult);
    assertTrue(response.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer, GenericResponse)} with {@code token}, {@code customer}, {@code response}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer, GenericResponse)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({
      "CustomerForgotPasswordSecurityToken CustomerServiceImpl.checkPasswordResetToken(String, Customer, GenericResponse)"})
  public void testCheckPasswordResetTokenWithTokenCustomerResponse4() {
    // Arrange
    CustomerImpl customer = new CustomerImpl();
    GenericResponse response = new GenericResponse();

    // Act
    CustomerForgotPasswordSecurityToken actualCheckPasswordResetTokenResult = customerServiceImpl
        .checkPasswordResetToken(null, customer, response);

    // Assert
    List<String> errorCodesList = response.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("invalidToken", errorCodesList.get(0));
    assertNull(actualCheckPasswordResetTokenResult);
    assertTrue(response.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer, GenericResponse)} with {@code token}, {@code customer}, {@code response}.
   * <ul>
   *   <li>Then calls {@link CustomerForgotPasswordSecurityTokenImpl#getCreateDate()}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer, GenericResponse)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({
      "CustomerForgotPasswordSecurityToken CustomerServiceImpl.checkPasswordResetToken(String, Customer, GenericResponse)"})
  public void testCheckPasswordResetTokenWithTokenCustomerResponse_thenCallsGetCreateDate() {
    // Arrange
    CustomerForgotPasswordSecurityTokenImpl customerForgotPasswordSecurityTokenImpl = mock(
        CustomerForgotPasswordSecurityTokenImpl.class);
    when(customerForgotPasswordSecurityTokenImpl.isTokenUsedFlag()).thenReturn(false);
    when(customerForgotPasswordSecurityTokenImpl.getToken()).thenReturn("ABC123");
    when(customerForgotPasswordSecurityTokenImpl.getCreateDate())
        .thenReturn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));

    ArrayList<CustomerForgotPasswordSecurityToken> customerForgotPasswordSecurityTokenList = new ArrayList<>();
    customerForgotPasswordSecurityTokenList.add(customerForgotPasswordSecurityTokenImpl);
    when(customerForgotPasswordSecurityTokenDao.readUnusedTokensByCustomerId(Mockito.<Long>any()))
        .thenReturn(customerForgotPasswordSecurityTokenList);
    when(passwordEncoder.matches(Mockito.<CharSequence>any(), Mockito.<String>any())).thenReturn(true);
    CustomerImpl customer = new CustomerImpl();

    // Act
    customerServiceImpl.checkPasswordResetToken("ABC123", customer, new GenericResponse());

    // Assert that nothing has changed
    verify(customerForgotPasswordSecurityTokenDao).readUnusedTokensByCustomerId(isNull());
    verify(customerForgotPasswordSecurityTokenImpl).getCreateDate();
    verify(customerForgotPasswordSecurityTokenImpl).getToken();
    verify(customerForgotPasswordSecurityTokenImpl).isTokenUsedFlag();
    verify(passwordEncoder).matches(isA(CharSequence.class), eq("ABC123"));
  }

  /**
   * Test {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer, GenericResponse)} with {@code token}, {@code customer}, {@code response}.
   * <ul>
   *   <li>When empty string.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer, GenericResponse)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({
      "CustomerForgotPasswordSecurityToken CustomerServiceImpl.checkPasswordResetToken(String, Customer, GenericResponse)"})
  public void testCheckPasswordResetTokenWithTokenCustomerResponse_whenEmptyString() {
    // Arrange
    CustomerImpl customer = new CustomerImpl();
    GenericResponse response = new GenericResponse();

    // Act
    CustomerForgotPasswordSecurityToken actualCheckPasswordResetTokenResult = customerServiceImpl
        .checkPasswordResetToken("", customer, response);

    // Assert
    List<String> errorCodesList = response.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("invalidToken", errorCodesList.get(0));
    assertNull(actualCheckPasswordResetTokenResult);
    assertTrue(response.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer, GenericResponse)} with {@code token}, {@code customer}, {@code response}.
   * <ul>
   *   <li>When space.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer, GenericResponse)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({
      "CustomerForgotPasswordSecurityToken CustomerServiceImpl.checkPasswordResetToken(String, Customer, GenericResponse)"})
  public void testCheckPasswordResetTokenWithTokenCustomerResponse_whenSpace() {
    // Arrange
    CustomerImpl customer = new CustomerImpl();
    GenericResponse response = new GenericResponse();

    // Act
    CustomerForgotPasswordSecurityToken actualCheckPasswordResetTokenResult = customerServiceImpl
        .checkPasswordResetToken(" ", customer, response);

    // Assert
    List<String> errorCodesList = response.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("invalidToken", errorCodesList.get(0));
    assertNull(actualCheckPasswordResetTokenResult);
    assertTrue(response.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer)} with {@code token}, {@code customer}.
   * <ul>
   *   <li>Then calls {@link CustomerForgotPasswordSecurityTokenImpl#getCreateDate()}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.checkPasswordResetToken(String, Customer)"})
  public void testCheckPasswordResetTokenWithTokenCustomer_thenCallsGetCreateDate() {
    // Arrange
    CustomerForgotPasswordSecurityTokenImpl customerForgotPasswordSecurityTokenImpl = mock(
        CustomerForgotPasswordSecurityTokenImpl.class);
    when(customerForgotPasswordSecurityTokenImpl.isTokenUsedFlag()).thenReturn(false);
    when(customerForgotPasswordSecurityTokenImpl.getToken()).thenReturn("ABC123");
    when(customerForgotPasswordSecurityTokenImpl.getCreateDate())
        .thenReturn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));

    ArrayList<CustomerForgotPasswordSecurityToken> customerForgotPasswordSecurityTokenList = new ArrayList<>();
    customerForgotPasswordSecurityTokenList.add(customerForgotPasswordSecurityTokenImpl);
    when(customerForgotPasswordSecurityTokenDao.readUnusedTokensByCustomerId(Mockito.<Long>any()))
        .thenReturn(customerForgotPasswordSecurityTokenList);
    when(passwordEncoder.matches(Mockito.<CharSequence>any(), Mockito.<String>any())).thenReturn(true);

    // Act
    customerServiceImpl.checkPasswordResetToken("ABC123", new CustomerImpl());

    // Assert
    verify(customerForgotPasswordSecurityTokenDao).readUnusedTokensByCustomerId(isNull());
    verify(customerForgotPasswordSecurityTokenImpl).getCreateDate();
    verify(customerForgotPasswordSecurityTokenImpl).getToken();
    verify(customerForgotPasswordSecurityTokenImpl).isTokenUsedFlag();
    verify(passwordEncoder).matches(isA(CharSequence.class), eq("ABC123"));
  }

  /**
   * Test {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer)} with {@code token}, {@code customer}.
   * <ul>
   *   <li>When empty string.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.checkPasswordResetToken(String, Customer)"})
  public void testCheckPasswordResetTokenWithTokenCustomer_whenEmptyString() {
    // Arrange and Act
    GenericResponse actualCheckPasswordResetTokenResult = customerServiceImpl.checkPasswordResetToken("",
        new CustomerImpl());

    // Assert
    List<String> errorCodesList = actualCheckPasswordResetTokenResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("invalidToken", errorCodesList.get(0));
    assertTrue(actualCheckPasswordResetTokenResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer)} with {@code token}, {@code customer}.
   * <ul>
   *   <li>When space.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#checkPasswordResetToken(String, Customer)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.checkPasswordResetToken(String, Customer)"})
  public void testCheckPasswordResetTokenWithTokenCustomer_whenSpace() {
    // Arrange and Act
    GenericResponse actualCheckPasswordResetTokenResult = customerServiceImpl.checkPasswordResetToken(" ", null);

    // Assert
    List<String> errorCodesList = actualCheckPasswordResetTokenResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("invalidToken", errorCodesList.get(0));
    assertTrue(actualCheckPasswordResetTokenResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#resetPasswordUsingToken(String, String, String, String)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#resetPasswordUsingToken(String, String, String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.resetPasswordUsingToken(String, String, String, String)"})
  public void testResetPasswordUsingToken() {
    // Arrange
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(new CustomerImpl());

    // Act
    GenericResponse actualResetPasswordUsingTokenResult = customerServiceImpl.resetPasswordUsingToken("janedoe",
        "ABC123", "iloveyou", "iloveyou");

    // Assert
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    List<String> errorCodesList = actualResetPasswordUsingTokenResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("emailNotFound", errorCodesList.get(0));
    assertTrue(actualResetPasswordUsingTokenResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#resetPasswordUsingToken(String, String, String, String)}.
   * <ul>
   *   <li>Given {@link CustomerDao} {@link CustomerDao#readCustomerByUsername(String)} return {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#resetPasswordUsingToken(String, String, String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.resetPasswordUsingToken(String, String, String, String)"})
  public void testResetPasswordUsingToken_givenCustomerDaoReadCustomerByUsernameReturnNull() {
    // Arrange
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(null);

    // Act
    GenericResponse actualResetPasswordUsingTokenResult = customerServiceImpl.resetPasswordUsingToken("janedoe",
        "ABC123", "iloveyou", "iloveyou");

    // Assert
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    List<String> errorCodesList = actualResetPasswordUsingTokenResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("invalidCustomer", errorCodesList.get(0));
    assertTrue(actualResetPasswordUsingTokenResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#resetPasswordUsingToken(String, String, String, String)}.
   * <ul>
   *   <li>Given {@link CustomerDao}.</li>
   *   <li>When {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#resetPasswordUsingToken(String, String, String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.resetPasswordUsingToken(String, String, String, String)"})
  public void testResetPasswordUsingToken_givenCustomerDao_whenNull() {
    // Arrange and Act
    GenericResponse actualResetPasswordUsingTokenResult = customerServiceImpl.resetPasswordUsingToken(null, "ABC123",
        "iloveyou", "iloveyou");

    // Assert
    List<String> errorCodesList = actualResetPasswordUsingTokenResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("invalidCustomer", errorCodesList.get(0));
    assertTrue(actualResetPasswordUsingTokenResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#resetPasswordUsingToken(String, String, String, String)}.
   * <ul>
   *   <li>Given {@link CustomerImpl} {@link CustomerImpl#getEmailAddress()} return empty string.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#resetPasswordUsingToken(String, String, String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.resetPasswordUsingToken(String, String, String, String)"})
  public void testResetPasswordUsingToken_givenCustomerImplGetEmailAddressReturnEmptyString() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.getEmailAddress()).thenReturn("");
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);

    // Act
    GenericResponse actualResetPasswordUsingTokenResult = customerServiceImpl.resetPasswordUsingToken("janedoe",
        "ABC123", "iloveyou", "iloveyou");

    // Assert
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerImpl).getEmailAddress();
    List<String> errorCodesList = actualResetPasswordUsingTokenResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("emailNotFound", errorCodesList.get(0));
    assertTrue(actualResetPasswordUsingTokenResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#resetPasswordUsingToken(String, String, String, String)}.
   * <ul>
   *   <li>Given {@link PasswordEncoder} {@link PasswordEncoder#matches(CharSequence, String)} return {@code false}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#resetPasswordUsingToken(String, String, String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.resetPasswordUsingToken(String, String, String, String)"})
  public void testResetPasswordUsingToken_givenPasswordEncoderMatchesReturnFalse() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.isDeactivated()).thenReturn(false);
    when(customerImpl.getId()).thenReturn(1L);
    when(customerImpl.getEmailAddress()).thenReturn("42 Main St");
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);
    CustomerForgotPasswordSecurityTokenImpl customerForgotPasswordSecurityTokenImpl = mock(
        CustomerForgotPasswordSecurityTokenImpl.class);
    when(customerForgotPasswordSecurityTokenImpl.getToken()).thenReturn("ABC123");

    ArrayList<CustomerForgotPasswordSecurityToken> customerForgotPasswordSecurityTokenList = new ArrayList<>();
    customerForgotPasswordSecurityTokenList.add(customerForgotPasswordSecurityTokenImpl);
    when(customerForgotPasswordSecurityTokenDao.readUnusedTokensByCustomerId(Mockito.<Long>any()))
        .thenReturn(customerForgotPasswordSecurityTokenList);
    when(passwordEncoder.matches(Mockito.<CharSequence>any(), Mockito.<String>any())).thenReturn(false);

    // Act
    GenericResponse actualResetPasswordUsingTokenResult = customerServiceImpl.resetPasswordUsingToken("janedoe",
        "ABC123", "iloveyou", "iloveyou");

    // Assert
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerForgotPasswordSecurityTokenDao).readUnusedTokensByCustomerId(eq(1L));
    verify(customerForgotPasswordSecurityTokenImpl).getToken();
    verify(customerImpl).getEmailAddress();
    verify(customerImpl).getId();
    verify(customerImpl).isDeactivated();
    verify(passwordEncoder).matches(isA(CharSequence.class), eq("ABC123"));
    List<String> errorCodesList = actualResetPasswordUsingTokenResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("invalidToken", errorCodesList.get(0));
    assertTrue(actualResetPasswordUsingTokenResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#resetPasswordUsingToken(String, String, String, String)}.
   * <ul>
   *   <li>Then return ErrorCodesList first is {@code inactiveUser}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#resetPasswordUsingToken(String, String, String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.resetPasswordUsingToken(String, String, String, String)"})
  public void testResetPasswordUsingToken_thenReturnErrorCodesListFirstIsInactiveUser() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.isDeactivated()).thenReturn(true);
    when(customerImpl.getEmailAddress()).thenReturn("42 Main St");
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);

    // Act
    GenericResponse actualResetPasswordUsingTokenResult = customerServiceImpl.resetPasswordUsingToken("janedoe",
        "ABC123", "iloveyou", "iloveyou");

    // Assert
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerImpl).getEmailAddress();
    verify(customerImpl).isDeactivated();
    List<String> errorCodesList = actualResetPasswordUsingTokenResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("inactiveUser", errorCodesList.get(0));
    assertTrue(actualResetPasswordUsingTokenResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#resetPasswordUsingToken(String, String, String, String)}.
   * <ul>
   *   <li>Then return ErrorCodesList first is {@code invalidToken}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#resetPasswordUsingToken(String, String, String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.resetPasswordUsingToken(String, String, String, String)"})
  public void testResetPasswordUsingToken_thenReturnErrorCodesListFirstIsInvalidToken() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.isDeactivated()).thenReturn(false);
    when(customerImpl.getId()).thenReturn(1L);
    when(customerImpl.getEmailAddress()).thenReturn("42 Main St");
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);
    when(customerForgotPasswordSecurityTokenDao.readUnusedTokensByCustomerId(Mockito.<Long>any()))
        .thenReturn(new ArrayList<>());

    // Act
    GenericResponse actualResetPasswordUsingTokenResult = customerServiceImpl.resetPasswordUsingToken("janedoe",
        "ABC123", "iloveyou", "iloveyou");

    // Assert
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerForgotPasswordSecurityTokenDao).readUnusedTokensByCustomerId(eq(1L));
    verify(customerImpl).getEmailAddress();
    verify(customerImpl).getId();
    verify(customerImpl).isDeactivated();
    List<String> errorCodesList = actualResetPasswordUsingTokenResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("invalidToken", errorCodesList.get(0));
    assertTrue(actualResetPasswordUsingTokenResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#resetPasswordUsingToken(String, String, String, String)}.
   * <ul>
   *   <li>Then return ErrorCodesList first is {@code tokenUsed}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#resetPasswordUsingToken(String, String, String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.resetPasswordUsingToken(String, String, String, String)"})
  public void testResetPasswordUsingToken_thenReturnErrorCodesListFirstIsTokenUsed() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.isDeactivated()).thenReturn(false);
    when(customerImpl.getId()).thenReturn(1L);
    when(customerImpl.getEmailAddress()).thenReturn("42 Main St");
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);
    CustomerForgotPasswordSecurityTokenImpl customerForgotPasswordSecurityTokenImpl = mock(
        CustomerForgotPasswordSecurityTokenImpl.class);
    when(customerForgotPasswordSecurityTokenImpl.isTokenUsedFlag()).thenReturn(true);
    when(customerForgotPasswordSecurityTokenImpl.getToken()).thenReturn("ABC123");

    ArrayList<CustomerForgotPasswordSecurityToken> customerForgotPasswordSecurityTokenList = new ArrayList<>();
    customerForgotPasswordSecurityTokenList.add(customerForgotPasswordSecurityTokenImpl);
    when(customerForgotPasswordSecurityTokenDao.readUnusedTokensByCustomerId(Mockito.<Long>any()))
        .thenReturn(customerForgotPasswordSecurityTokenList);
    when(passwordEncoder.matches(Mockito.<CharSequence>any(), Mockito.<String>any())).thenReturn(true);

    // Act
    GenericResponse actualResetPasswordUsingTokenResult = customerServiceImpl.resetPasswordUsingToken("janedoe",
        "ABC123", "iloveyou", "iloveyou");

    // Assert
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerForgotPasswordSecurityTokenDao).readUnusedTokensByCustomerId(eq(1L));
    verify(customerForgotPasswordSecurityTokenImpl).getToken();
    verify(customerForgotPasswordSecurityTokenImpl).isTokenUsedFlag();
    verify(customerImpl).getEmailAddress();
    verify(customerImpl).getId();
    verify(customerImpl).isDeactivated();
    verify(passwordEncoder).matches(isA(CharSequence.class), eq("ABC123"));
    List<String> errorCodesList = actualResetPasswordUsingTokenResult.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("tokenUsed", errorCodesList.get(0));
    assertTrue(actualResetPasswordUsingTokenResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#resetPasswordUsingToken(String, String, String, String)}.
   * <ul>
   *   <li>Then return ErrorCodesList second is {@code invalidPassword}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#resetPasswordUsingToken(String, String, String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.resetPasswordUsingToken(String, String, String, String)"})
  public void testResetPasswordUsingToken_thenReturnErrorCodesListSecondIsInvalidPassword() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.isDeactivated()).thenReturn(true);
    when(customerImpl.getEmailAddress()).thenReturn("42 Main St");
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);

    // Act
    GenericResponse actualResetPasswordUsingTokenResult = customerServiceImpl.resetPasswordUsingToken("janedoe",
        "ABC123", "", "iloveyou");

    // Assert
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerImpl).getEmailAddress();
    verify(customerImpl).isDeactivated();
    List<String> errorCodesList = actualResetPasswordUsingTokenResult.getErrorCodesList();
    assertEquals(2, errorCodesList.size());
    assertEquals("inactiveUser", errorCodesList.get(0));
    assertEquals("invalidPassword", errorCodesList.get(1));
    assertTrue(actualResetPasswordUsingTokenResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#resetPasswordUsingToken(String, String, String, String)}.
   * <ul>
   *   <li>Then return ErrorCodesList second is {@code invalidPassword}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#resetPasswordUsingToken(String, String, String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.resetPasswordUsingToken(String, String, String, String)"})
  public void testResetPasswordUsingToken_thenReturnErrorCodesListSecondIsInvalidPassword2() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.isDeactivated()).thenReturn(true);
    when(customerImpl.getEmailAddress()).thenReturn("42 Main St");
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);

    // Act
    GenericResponse actualResetPasswordUsingTokenResult = customerServiceImpl.resetPasswordUsingToken("janedoe",
        "ABC123", "iloveyou", "");

    // Assert
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerImpl).getEmailAddress();
    verify(customerImpl).isDeactivated();
    List<String> errorCodesList = actualResetPasswordUsingTokenResult.getErrorCodesList();
    assertEquals(2, errorCodesList.size());
    assertEquals("inactiveUser", errorCodesList.get(0));
    assertEquals("invalidPassword", errorCodesList.get(1));
    assertTrue(actualResetPasswordUsingTokenResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#resetPasswordUsingToken(String, String, String, String)}.
   * <ul>
   *   <li>Then return ErrorCodesList second is {@code invalidToken}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#resetPasswordUsingToken(String, String, String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.resetPasswordUsingToken(String, String, String, String)"})
  public void testResetPasswordUsingToken_thenReturnErrorCodesListSecondIsInvalidToken() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.isDeactivated()).thenReturn(true);
    when(customerImpl.getEmailAddress()).thenReturn("42 Main St");
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);

    // Act
    GenericResponse actualResetPasswordUsingTokenResult = customerServiceImpl.resetPasswordUsingToken("janedoe", "",
        "iloveyou", "iloveyou");

    // Assert
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerImpl).getEmailAddress();
    verify(customerImpl).isDeactivated();
    List<String> errorCodesList = actualResetPasswordUsingTokenResult.getErrorCodesList();
    assertEquals(2, errorCodesList.size());
    assertEquals("inactiveUser", errorCodesList.get(0));
    assertEquals("invalidToken", errorCodesList.get(1));
    assertTrue(actualResetPasswordUsingTokenResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#resetPasswordUsingToken(String, String, String, String)}.
   * <ul>
   *   <li>Then return ErrorCodesList second is {@code passwordMismatch}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#resetPasswordUsingToken(String, String, String, String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"GenericResponse CustomerServiceImpl.resetPasswordUsingToken(String, String, String, String)"})
  public void testResetPasswordUsingToken_thenReturnErrorCodesListSecondIsPasswordMismatch() {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.isDeactivated()).thenReturn(true);
    when(customerImpl.getEmailAddress()).thenReturn("42 Main St");
    when(customerDao.readCustomerByUsername(Mockito.<String>any())).thenReturn(customerImpl);

    // Act
    GenericResponse actualResetPasswordUsingTokenResult = customerServiceImpl.resetPasswordUsingToken("janedoe",
        "ABC123", "Password", "iloveyou");

    // Assert
    verify(customerDao).readCustomerByUsername(eq("janedoe"));
    verify(customerImpl).getEmailAddress();
    verify(customerImpl).isDeactivated();
    List<String> errorCodesList = actualResetPasswordUsingTokenResult.getErrorCodesList();
    assertEquals(2, errorCodesList.size());
    assertEquals("inactiveUser", errorCodesList.get(0));
    assertEquals("passwordMismatch", errorCodesList.get(1));
    assertTrue(actualResetPasswordUsingTokenResult.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#invalidateAllTokensForCustomer(Customer)}.
   * <ul>
   *   <li>Then calls {@link CustomerForgotPasswordSecurityTokenDao#readUnusedTokensByCustomerId(Long)}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#invalidateAllTokensForCustomer(Customer)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerServiceImpl.invalidateAllTokensForCustomer(Customer)"})
  public void testInvalidateAllTokensForCustomer_thenCallsReadUnusedTokensByCustomerId() {
    // Arrange
    when(customerForgotPasswordSecurityTokenDao.readUnusedTokensByCustomerId(Mockito.<Long>any()))
        .thenReturn(new ArrayList<>());

    // Act
    customerServiceImpl.invalidateAllTokensForCustomer(new CustomerImpl());

    // Assert
    verify(customerForgotPasswordSecurityTokenDao).readUnusedTokensByCustomerId(isNull());
  }

  /**
   * Test {@link CustomerServiceImpl#invalidateAllTokensForCustomer(Customer)}.
   * <ul>
   *   <li>Then calls {@link CustomerForgotPasswordSecurityTokenDao#saveToken(CustomerForgotPasswordSecurityToken)}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#invalidateAllTokensForCustomer(Customer)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerServiceImpl.invalidateAllTokensForCustomer(Customer)"})
  public void testInvalidateAllTokensForCustomer_thenCallsSaveToken() {
    // Arrange
    ArrayList<CustomerForgotPasswordSecurityToken> customerForgotPasswordSecurityTokenList = new ArrayList<>();
    customerForgotPasswordSecurityTokenList.add(new CustomerForgotPasswordSecurityTokenImpl());
    when(customerForgotPasswordSecurityTokenDao.saveToken(Mockito.<CustomerForgotPasswordSecurityToken>any()))
        .thenReturn(new CustomerForgotPasswordSecurityTokenImpl());
    when(customerForgotPasswordSecurityTokenDao.readUnusedTokensByCustomerId(Mockito.<Long>any()))
        .thenReturn(customerForgotPasswordSecurityTokenList);

    // Act
    customerServiceImpl.invalidateAllTokensForCustomer(new CustomerImpl());

    // Assert
    verify(customerForgotPasswordSecurityTokenDao).readUnusedTokensByCustomerId(isNull());
    verify(customerForgotPasswordSecurityTokenDao).saveToken(isA(CustomerForgotPasswordSecurityToken.class));
  }

  /**
   * Test {@link CustomerServiceImpl#checkCustomer(Customer, GenericResponse)}.
   * <ul>
   *   <li>Given empty string.</li>
   *   <li>Then calls {@link CustomerImpl#getEmailAddress()}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#checkCustomer(Customer, GenericResponse)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerServiceImpl.checkCustomer(Customer, GenericResponse)"})
  public void testCheckCustomer_givenEmptyString_thenCallsGetEmailAddress() {
    // Arrange
    CustomerImpl customer = mock(CustomerImpl.class);
    when(customer.getEmailAddress()).thenReturn("");
    GenericResponse response = new GenericResponse();

    // Act
    customerServiceImpl.checkCustomer(customer, response);

    // Assert
    verify(customer).getEmailAddress();
    List<String> errorCodesList = response.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("emailNotFound", errorCodesList.get(0));
    assertTrue(response.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#checkCustomer(Customer, GenericResponse)}.
   * <ul>
   *   <li>Given {@code false}.</li>
   *   <li>Then not {@link GenericResponse} (default constructor) HasErrors.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#checkCustomer(Customer, GenericResponse)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerServiceImpl.checkCustomer(Customer, GenericResponse)"})
  public void testCheckCustomer_givenFalse_thenNotGenericResponseHasErrors() {
    // Arrange
    CustomerImpl customer = mock(CustomerImpl.class);
    when(customer.isDeactivated()).thenReturn(false);
    when(customer.getEmailAddress()).thenReturn("42 Main St");
    GenericResponse response = new GenericResponse();

    // Act
    customerServiceImpl.checkCustomer(customer, response);

    // Assert that nothing has changed
    verify(customer).getEmailAddress();
    verify(customer).isDeactivated();
    assertFalse(response.getHasErrors());
    assertTrue(response.getErrorCodesList().isEmpty());
  }

  /**
   * Test {@link CustomerServiceImpl#checkCustomer(Customer, GenericResponse)}.
   * <ul>
   *   <li>Given space.</li>
   *   <li>When {@link CustomerImpl} (default constructor) EmailAddress is space.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#checkCustomer(Customer, GenericResponse)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerServiceImpl.checkCustomer(Customer, GenericResponse)"})
  public void testCheckCustomer_givenSpace_whenCustomerImplEmailAddressIsSpace() {
    // Arrange
    Auditable auditable = new Auditable();
    auditable.setCreatedBy(1L);
    auditable.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setUpdatedBy(1L);

    CustomerImpl customer = new CustomerImpl();
    customer.setAuditable(auditable);
    customer.setChallengeAnswer("Challenge Answer");
    customer.setChallengeQuestion(new ChallengeQuestionImpl());
    customer.setCustomerAddresses(new ArrayList<>());
    customer.setCustomerAttributes(new HashMap<>());
    customer.setCustomerLocale(new LocaleImpl());
    customer.setCustomerPayments(new ArrayList<>());
    customer.setCustomerPhones(new ArrayList<>());
    customer.setDeactivated(true);
    customer.setExternalId("42");
    customer.setFirstName("Jane");
    customer.setId(1L);
    customer.setLastName("Doe");
    customer.setPassword("iloveyou");
    customer.setPasswordChangeRequired(true);
    customer.setReceiveEmail(true);
    customer.setRegistered(true);
    customer.setUnencodedChallengeAnswer("secret");
    customer.setUnencodedPassword("secret");
    customer.setUsername("janedoe");
    customer.setEmailAddress(" ");
    GenericResponse response = new GenericResponse();

    // Act
    customerServiceImpl.checkCustomer(customer, response);

    // Assert
    List<String> errorCodesList = response.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("emailNotFound", errorCodesList.get(0));
    assertTrue(response.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#checkCustomer(Customer, GenericResponse)}.
   * <ul>
   *   <li>Then {@link GenericResponse} (default constructor) ErrorCodesList first is {@code inactiveUser}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#checkCustomer(Customer, GenericResponse)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerServiceImpl.checkCustomer(Customer, GenericResponse)"})
  public void testCheckCustomer_thenGenericResponseErrorCodesListFirstIsInactiveUser() {
    // Arrange
    Auditable auditable = new Auditable();
    auditable.setCreatedBy(1L);
    auditable.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setUpdatedBy(1L);

    CustomerImpl customer = new CustomerImpl();
    customer.setAuditable(auditable);
    customer.setChallengeAnswer("Challenge Answer");
    customer.setChallengeQuestion(new ChallengeQuestionImpl());
    customer.setCustomerAddresses(new ArrayList<>());
    customer.setCustomerAttributes(new HashMap<>());
    customer.setCustomerLocale(new LocaleImpl());
    customer.setCustomerPayments(new ArrayList<>());
    customer.setCustomerPhones(new ArrayList<>());
    customer.setDeactivated(true);
    customer.setExternalId("42");
    customer.setFirstName("Jane");
    customer.setId(1L);
    customer.setLastName("Doe");
    customer.setPassword("iloveyou");
    customer.setPasswordChangeRequired(true);
    customer.setReceiveEmail(true);
    customer.setRegistered(true);
    customer.setUnencodedChallengeAnswer("secret");
    customer.setUnencodedPassword("secret");
    customer.setUsername("janedoe");
    customer.setEmailAddress("Customer");
    GenericResponse response = new GenericResponse();

    // Act
    customerServiceImpl.checkCustomer(customer, response);

    // Assert
    List<String> errorCodesList = response.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("inactiveUser", errorCodesList.get(0));
    assertTrue(response.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#checkCustomer(Customer, GenericResponse)}.
   * <ul>
   *   <li>Then {@link GenericResponse} (default constructor) ErrorCodesList first is {@code invalidCustomer}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#checkCustomer(Customer, GenericResponse)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerServiceImpl.checkCustomer(Customer, GenericResponse)"})
  public void testCheckCustomer_thenGenericResponseErrorCodesListFirstIsInvalidCustomer() {
    // Arrange
    GenericResponse response = new GenericResponse();

    // Act
    customerServiceImpl.checkCustomer(null, response);

    // Assert
    List<String> errorCodesList = response.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("invalidCustomer", errorCodesList.get(0));
    assertTrue(response.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#checkCustomer(Customer, GenericResponse)}.
   * <ul>
   *   <li>When {@link CustomerImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#checkCustomer(Customer, GenericResponse)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerServiceImpl.checkCustomer(Customer, GenericResponse)"})
  public void testCheckCustomer_whenCustomerImpl() {
    // Arrange
    CustomerImpl customer = new CustomerImpl();
    GenericResponse response = new GenericResponse();

    // Act
    customerServiceImpl.checkCustomer(customer, response);

    // Assert
    List<String> errorCodesList = response.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("emailNotFound", errorCodesList.get(0));
    assertTrue(response.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#checkPassword(String, String, GenericResponse)}.
   * <ul>
   *   <li>Then {@link GenericResponse} (default constructor) ErrorCodesList first is {@code invalidPassword}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#checkPassword(String, String, GenericResponse)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerServiceImpl.checkPassword(String, String, GenericResponse)"})
  public void testCheckPassword_thenGenericResponseErrorCodesListFirstIsInvalidPassword() {
    // Arrange
    GenericResponse response = new GenericResponse();

    // Act
    customerServiceImpl.checkPassword("", "iloveyou", response);

    // Assert
    List<String> errorCodesList = response.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("invalidPassword", errorCodesList.get(0));
    assertTrue(response.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#checkPassword(String, String, GenericResponse)}.
   * <ul>
   *   <li>Then {@link GenericResponse} (default constructor) ErrorCodesList first is {@code invalidPassword}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#checkPassword(String, String, GenericResponse)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerServiceImpl.checkPassword(String, String, GenericResponse)"})
  public void testCheckPassword_thenGenericResponseErrorCodesListFirstIsInvalidPassword2() {
    // Arrange
    GenericResponse response = new GenericResponse();

    // Act
    customerServiceImpl.checkPassword("iloveyou", "", response);

    // Assert
    List<String> errorCodesList = response.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("invalidPassword", errorCodesList.get(0));
    assertTrue(response.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#checkPassword(String, String, GenericResponse)}.
   * <ul>
   *   <li>Then {@link GenericResponse} (default constructor) ErrorCodesList first is {@code passwordMismatch}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#checkPassword(String, String, GenericResponse)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerServiceImpl.checkPassword(String, String, GenericResponse)"})
  public void testCheckPassword_thenGenericResponseErrorCodesListFirstIsPasswordMismatch() {
    // Arrange
    GenericResponse response = new GenericResponse();

    // Act
    customerServiceImpl.checkPassword("Password", "iloveyou", response);

    // Assert
    List<String> errorCodesList = response.getErrorCodesList();
    assertEquals(1, errorCodesList.size());
    assertEquals("passwordMismatch", errorCodesList.get(0));
    assertTrue(response.getHasErrors());
  }

  /**
   * Test {@link CustomerServiceImpl#checkPassword(String, String, GenericResponse)}.
   * <ul>
   *   <li>When {@link GenericResponse} (default constructor).</li>
   *   <li>Then not {@link GenericResponse} (default constructor) HasErrors.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#checkPassword(String, String, GenericResponse)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerServiceImpl.checkPassword(String, String, GenericResponse)"})
  public void testCheckPassword_whenGenericResponse_thenNotGenericResponseHasErrors() {
    // Arrange
    GenericResponse response = new GenericResponse();

    // Act
    customerServiceImpl.checkPassword("iloveyou", "iloveyou", response);

    // Assert that nothing has changed
    assertFalse(response.getHasErrors());
    assertTrue(response.getErrorCodesList().isEmpty());
  }

  /**
   * Test {@link CustomerServiceImpl#isTokenExpired(CustomerForgotPasswordSecurityToken)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#isTokenExpired(CustomerForgotPasswordSecurityToken)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerServiceImpl.isTokenExpired(CustomerForgotPasswordSecurityToken)"})
  public void testIsTokenExpired() {
    // Arrange
    CustomerForgotPasswordSecurityToken fpst = mock(CustomerForgotPasswordSecurityToken.class);
    when(fpst.getCreateDate())
        .thenReturn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));

    // Act
    customerServiceImpl.isTokenExpired(fpst);

    // Assert
    verify(fpst).getCreateDate();
  }

  /**
   * Test {@link CustomerServiceImpl#isTokenExpired(CustomerForgotPasswordSecurityToken)}.
   * <ul>
   *   <li>Given {@link Date#Date()}.</li>
   *   <li>Then return {@code false}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerServiceImpl#isTokenExpired(CustomerForgotPasswordSecurityToken)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerServiceImpl.isTokenExpired(CustomerForgotPasswordSecurityToken)"})
  public void testIsTokenExpired_givenDate_thenReturnFalse() {
    // Arrange
    CustomerForgotPasswordSecurityToken fpst = mock(CustomerForgotPasswordSecurityToken.class);
    when(fpst.getCreateDate()).thenReturn(new Date());

    // Act
    boolean actualIsTokenExpiredResult = customerServiceImpl.isTokenExpired(fpst);

    // Assert
    verify(fpst).getCreateDate();
    assertFalse(actualIsTokenExpiredResult);
  }

  /**
   * Test {@link CustomerServiceImpl#sendEmail(String, EmailInfo, Map)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#sendEmail(String, EmailInfo, Map)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerServiceImpl.sendEmail(String, EmailInfo, Map)"})
  public void testSendEmail() {
    // Arrange
    when(emailService.sendTemplateEmail(Mockito.<String>any(), Mockito.<EmailInfo>any(),
        Mockito.<Map<String, Object>>any())).thenReturn(true);

    // Act
    customerServiceImpl.sendEmail("42 Main St", emailInfo, new HashMap<>());

    // Assert
    verify(emailService).sendTemplateEmail(eq("42 Main St"), isA(EmailInfo.class), isA(Map.class));
  }

  /**
   * Test getters and setters.
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerServiceImpl#setChangePasswordEmailInfo(EmailInfo)}
   *   <li>{@link CustomerServiceImpl#setCustomerDao(CustomerDao)}
   *   <li>{@link CustomerServiceImpl#setForgotPasswordEmailInfo(EmailInfo)}
   *   <li>{@link CustomerServiceImpl#setForgotUsernameEmailInfo(EmailInfo)}
   *   <li>{@link CustomerServiceImpl#setPasswordChangedHandlers(List)}
   *   <li>{@link CustomerServiceImpl#setPasswordResetHandlers(List)}
   *   <li>{@link CustomerServiceImpl#setPasswordTokenLength(int)}
   *   <li>{@link CustomerServiceImpl#setRegistrationEmailInfo(EmailInfo)}
   *   <li>{@link CustomerServiceImpl#setTokenExpiredMinutes(int)}
   *   <li>{@link CustomerServiceImpl#getChangePasswordEmailInfo()}
   *   <li>{@link CustomerServiceImpl#getForgotPasswordEmailInfo()}
   *   <li>{@link CustomerServiceImpl#getForgotUsernameEmailInfo()}
   *   <li>{@link CustomerServiceImpl#getPasswordChangedHandlers()}
   *   <li>{@link CustomerServiceImpl#getPasswordResetHandlers()}
   *   <li>{@link CustomerServiceImpl#getPasswordTokenLength()}
   *   <li>{@link CustomerServiceImpl#getRegistrationEmailInfo()}
   *   <li>{@link CustomerServiceImpl#getTokenExpiredMinutes()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"EmailInfo CustomerServiceImpl.getChangePasswordEmailInfo()",
      "EmailInfo CustomerServiceImpl.getForgotPasswordEmailInfo()",
      "EmailInfo CustomerServiceImpl.getForgotUsernameEmailInfo()",
      "List CustomerServiceImpl.getPasswordChangedHandlers()", "List CustomerServiceImpl.getPasswordResetHandlers()",
      "int CustomerServiceImpl.getPasswordTokenLength()", "EmailInfo CustomerServiceImpl.getRegistrationEmailInfo()",
      "int CustomerServiceImpl.getTokenExpiredMinutes()",
      "void CustomerServiceImpl.setChangePasswordEmailInfo(EmailInfo)",
      "void CustomerServiceImpl.setCustomerDao(CustomerDao)",
      "void CustomerServiceImpl.setForgotPasswordEmailInfo(EmailInfo)",
      "void CustomerServiceImpl.setForgotUsernameEmailInfo(EmailInfo)",
      "void CustomerServiceImpl.setPasswordChangedHandlers(List)",
      "void CustomerServiceImpl.setPasswordResetHandlers(List)", "void CustomerServiceImpl.setPasswordTokenLength(int)",
      "void CustomerServiceImpl.setRegistrationEmailInfo(EmailInfo)",
      "void CustomerServiceImpl.setTokenExpiredMinutes(int)"})
  public void testGettersAndSetters() {
    // Arrange
    CustomerServiceImpl customerServiceImpl = new CustomerServiceImpl();
    EmailInfo changePasswordEmailInfo = new EmailInfo();

    // Act
    customerServiceImpl.setChangePasswordEmailInfo(changePasswordEmailInfo);
    customerServiceImpl.setCustomerDao(new CustomerDaoImpl());
    EmailInfo forgotPasswordEmailInfo = new EmailInfo();
    customerServiceImpl.setForgotPasswordEmailInfo(forgotPasswordEmailInfo);
    EmailInfo forgotUsernameEmailInfo = new EmailInfo();
    customerServiceImpl.setForgotUsernameEmailInfo(forgotUsernameEmailInfo);
    ArrayList<PasswordUpdatedHandler> passwordChangedHandlers = new ArrayList<>();
    customerServiceImpl.setPasswordChangedHandlers(passwordChangedHandlers);
    ArrayList<PasswordUpdatedHandler> passwordResetHandlers = new ArrayList<>();
    customerServiceImpl.setPasswordResetHandlers(passwordResetHandlers);
    customerServiceImpl.setPasswordTokenLength(3);
    EmailInfo registrationEmailInfo = new EmailInfo();
    customerServiceImpl.setRegistrationEmailInfo(registrationEmailInfo);
    customerServiceImpl.setTokenExpiredMinutes(1);
    EmailInfo actualChangePasswordEmailInfo = customerServiceImpl.getChangePasswordEmailInfo();
    EmailInfo actualForgotPasswordEmailInfo = customerServiceImpl.getForgotPasswordEmailInfo();
    EmailInfo actualForgotUsernameEmailInfo = customerServiceImpl.getForgotUsernameEmailInfo();
    List<PasswordUpdatedHandler> actualPasswordChangedHandlers = customerServiceImpl.getPasswordChangedHandlers();
    List<PasswordUpdatedHandler> actualPasswordResetHandlers = customerServiceImpl.getPasswordResetHandlers();
    int actualPasswordTokenLength = customerServiceImpl.getPasswordTokenLength();
    EmailInfo actualRegistrationEmailInfo = customerServiceImpl.getRegistrationEmailInfo();

    // Assert
    assertEquals(1, customerServiceImpl.getTokenExpiredMinutes());
    assertEquals(3, actualPasswordTokenLength);
    assertTrue(actualPasswordChangedHandlers.isEmpty());
    assertTrue(actualPasswordResetHandlers.isEmpty());
    assertSame(passwordChangedHandlers, actualPasswordChangedHandlers);
    assertSame(passwordResetHandlers, actualPasswordResetHandlers);
    assertSame(changePasswordEmailInfo, actualChangePasswordEmailInfo);
    assertSame(forgotPasswordEmailInfo, actualForgotPasswordEmailInfo);
    assertSame(forgotUsernameEmailInfo, actualForgotUsernameEmailInfo);
    assertSame(registrationEmailInfo, actualRegistrationEmailInfo);
  }

  /**
   * Test {@link CustomerServiceImpl#readBatchCustomers(int, int)}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#readBatchCustomers(int, int)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"List CustomerServiceImpl.readBatchCustomers(int, int)"})
  public void testReadBatchCustomers() {
    // Arrange
    when(customerDao.readBatchCustomers(anyInt(), anyInt())).thenReturn(new ArrayList<>());

    // Act
    List<Customer> actualReadBatchCustomersResult = customerServiceImpl.readBatchCustomers(1, 3);

    // Assert
    verify(customerDao).readBatchCustomers(eq(1), eq(3));
    assertTrue(actualReadBatchCustomersResult.isEmpty());
  }

  /**
   * Test {@link CustomerServiceImpl#readNumberOfCustomers()}.
   * <p>
   * Method under test: {@link CustomerServiceImpl#readNumberOfCustomers()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Long CustomerServiceImpl.readNumberOfCustomers()"})
  public void testReadNumberOfCustomers() {
    // Arrange
    when(customerDao.readNumberOfCustomers()).thenReturn(1L);

    // Act
    Long actualReadNumberOfCustomersResult = customerServiceImpl.readNumberOfCustomers();

    // Assert
    verify(customerDao).readNumberOfCustomers();
    assertEquals(1L, actualReadNumberOfCustomersResult.longValue());
  }
}
