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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
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
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopierExtensionManager;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.locale.domain.LocaleImpl;
import org.broadleafcommerce.common.service.GenericEntityService;
import org.broadleafcommerce.common.site.domain.CatalogImpl;
import org.broadleafcommerce.common.site.domain.SiteImpl;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = {"/bl-profile-applicationContext-entity.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class CustomerImplDiffblueTest {
  @Autowired
  private CustomerImpl customerImpl;

  /**
   * Test {@link CustomerImpl#isPasswordChangeRequired()}.
   * <ul>
   *   <li>Given {@link Auditable} (default constructor) CreatedBy is one.</li>
   *   <li>Then return {@code true}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#isPasswordChangeRequired()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerImpl.isPasswordChangeRequired()"})
  public void testIsPasswordChangeRequired_givenAuditableCreatedByIsOne_thenReturnTrue() {
    // Arrange
    Auditable auditable = new Auditable();
    auditable.setCreatedBy(1L);
    auditable.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setUpdatedBy(1L);

    CustomerImpl customerImpl2 = new CustomerImpl();
    customerImpl2.setAuditable(auditable);
    customerImpl2.setChallengeAnswer("challengeAnswer");
    customerImpl2.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl2.setCustomerAddresses(new ArrayList<>());
    customerImpl2.setCustomerAttributes(new HashMap<>());
    customerImpl2.setCustomerLocale(new LocaleImpl());
    customerImpl2.setCustomerPayments(new ArrayList<>());
    customerImpl2.setCustomerPhones(new ArrayList<>());
    customerImpl2.setDeactivated(false);
    customerImpl2.setEmailAddress("defaultEmail@example.com");
    customerImpl2.setExternalId("externalId123");
    customerImpl2.setFirstName("John");
    customerImpl2.setId(1L);
    customerImpl2.setLastName("Doe");
    customerImpl2.setPassword("defaultPassword");
    customerImpl2.setReceiveEmail(false);
    customerImpl2.setRegistered(false);
    customerImpl2.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl2.setUnencodedPassword("defaultPassword");
    customerImpl2.setUsername("defaultUsername");
    customerImpl2.setPasswordChangeRequired(true);

    // Act and Assert
    assertTrue(customerImpl2.isPasswordChangeRequired());
  }

  /**
   * Test {@link CustomerImpl#isPasswordChangeRequired()}.
   * <ul>
   *   <li>Given {@link CustomerImpl} (default constructor).</li>
   *   <li>Then return {@code false}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#isPasswordChangeRequired()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerImpl.isPasswordChangeRequired()"})
  public void testIsPasswordChangeRequired_givenCustomerImpl_thenReturnFalse() {
    // Arrange, Act and Assert
    assertFalse((new CustomerImpl()).isPasswordChangeRequired());
  }

  /**
   * Test {@link CustomerImpl#isReceiveEmail()}.
   * <ul>
   *   <li>Given {@link Auditable} (default constructor) CreatedBy is one.</li>
   *   <li>Then return {@code true}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#isReceiveEmail()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerImpl.isReceiveEmail()"})
  public void testIsReceiveEmail_givenAuditableCreatedByIsOne_thenReturnTrue() {
    // Arrange
    Auditable auditable = new Auditable();
    auditable.setCreatedBy(1L);
    auditable.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setUpdatedBy(1L);

    CustomerImpl customerImpl2 = new CustomerImpl();
    customerImpl2.setAuditable(auditable);
    customerImpl2.setChallengeAnswer("challengeAnswer");
    customerImpl2.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl2.setCustomerAddresses(new ArrayList<>());
    customerImpl2.setCustomerAttributes(new HashMap<>());
    customerImpl2.setCustomerLocale(new LocaleImpl());
    customerImpl2.setCustomerPayments(new ArrayList<>());
    customerImpl2.setCustomerPhones(new ArrayList<>());
    customerImpl2.setDeactivated(false);
    customerImpl2.setEmailAddress("defaultEmail@example.com");
    customerImpl2.setExternalId("externalId123");
    customerImpl2.setFirstName("John");
    customerImpl2.setId(1L);
    customerImpl2.setLastName("Doe");
    customerImpl2.setPassword("defaultPassword");
    customerImpl2.setPasswordChangeRequired(false);
    customerImpl2.setRegistered(false);
    customerImpl2.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl2.setUnencodedPassword("defaultPassword");
    customerImpl2.setUsername("defaultUsername");
    customerImpl2.setReceiveEmail(true);

    // Act and Assert
    assertTrue(customerImpl2.isReceiveEmail());
  }

  /**
   * Test {@link CustomerImpl#isReceiveEmail()}.
   * <ul>
   *   <li>Given {@link CustomerImpl} (default constructor).</li>
   *   <li>Then return {@code false}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#isReceiveEmail()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerImpl.isReceiveEmail()"})
  public void testIsReceiveEmail_givenCustomerImpl_thenReturnFalse() {
    // Arrange, Act and Assert
    assertFalse((new CustomerImpl()).isReceiveEmail());
  }

  /**
   * Test {@link CustomerImpl#isRegistered()}.
   * <ul>
   *   <li>Given {@link Auditable} (default constructor) CreatedBy is one.</li>
   *   <li>Then return {@code true}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#isRegistered()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerImpl.isRegistered()"})
  public void testIsRegistered_givenAuditableCreatedByIsOne_thenReturnTrue() {
    // Arrange
    Auditable auditable = new Auditable();
    auditable.setCreatedBy(1L);
    auditable.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setUpdatedBy(1L);

    CustomerImpl customerImpl2 = new CustomerImpl();
    customerImpl2.setAuditable(auditable);
    customerImpl2.setChallengeAnswer("challengeAnswer");
    customerImpl2.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl2.setCustomerAddresses(new ArrayList<>());
    customerImpl2.setCustomerAttributes(new HashMap<>());
    customerImpl2.setCustomerLocale(new LocaleImpl());
    customerImpl2.setCustomerPayments(new ArrayList<>());
    customerImpl2.setCustomerPhones(new ArrayList<>());
    customerImpl2.setDeactivated(false);
    customerImpl2.setEmailAddress("defaultEmail@example.com");
    customerImpl2.setExternalId("externalId123");
    customerImpl2.setFirstName("John");
    customerImpl2.setId(1L);
    customerImpl2.setLastName("Doe");
    customerImpl2.setPassword("defaultPassword");
    customerImpl2.setPasswordChangeRequired(false);
    customerImpl2.setReceiveEmail(false);
    customerImpl2.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl2.setUnencodedPassword("defaultPassword");
    customerImpl2.setUsername("defaultUsername");
    customerImpl2.setRegistered(true);

    // Act and Assert
    assertTrue(customerImpl2.isRegistered());
  }

  /**
   * Test {@link CustomerImpl#isRegistered()}.
   * <ul>
   *   <li>Given {@link CustomerImpl} (default constructor).</li>
   *   <li>Then return {@code false}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#isRegistered()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerImpl.isRegistered()"})
  public void testIsRegistered_givenCustomerImpl_thenReturnFalse() {
    // Arrange, Act and Assert
    assertFalse((new CustomerImpl()).isRegistered());
  }

  /**
   * Test getters and setters.
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerImpl#setAuditable(Auditable)}
   *   <li>{@link CustomerImpl#setChallengeAnswer(String)}
   *   <li>{@link CustomerImpl#setChallengeQuestion(ChallengeQuestion)}
   *   <li>{@link CustomerImpl#setCustomerAddresses(List)}
   *   <li>{@link CustomerImpl#setCustomerAttributes(Map)}
   *   <li>{@link CustomerImpl#setCustomerLocale(Locale)}
   *   <li>{@link CustomerImpl#setCustomerPayments(List)}
   *   <li>{@link CustomerImpl#setCustomerPhones(List)}
   *   <li>{@link CustomerImpl#setEmailAddress(String)}
   *   <li>{@link CustomerImpl#setExternalId(String)}
   *   <li>{@link CustomerImpl#setFirstName(String)}
   *   <li>{@link CustomerImpl#setId(Long)}
   *   <li>{@link CustomerImpl#setLastName(String)}
   *   <li>{@link CustomerImpl#setPassword(String)}
   *   <li>{@link CustomerImpl#setUnencodedChallengeAnswer(String)}
   *   <li>{@link CustomerImpl#setUnencodedPassword(String)}
   *   <li>{@link CustomerImpl#setUsername(String)}
   *   <li>{@link CustomerImpl#getAuditable()}
   *   <li>{@link CustomerImpl#getChallengeAnswer()}
   *   <li>{@link CustomerImpl#getChallengeQuestion()}
   *   <li>{@link CustomerImpl#getCustomerAddresses()}
   *   <li>{@link CustomerImpl#getCustomerAttributes()}
   *   <li>{@link CustomerImpl#getCustomerLocale()}
   *   <li>{@link CustomerImpl#getCustomerPayments()}
   *   <li>{@link CustomerImpl#getCustomerPhones()}
   *   <li>{@link CustomerImpl#getEmailAddress()}
   *   <li>{@link CustomerImpl#getExternalId()}
   *   <li>{@link CustomerImpl#getFirstName()}
   *   <li>{@link CustomerImpl#getId()}
   *   <li>{@link CustomerImpl#getLastName()}
   *   <li>{@link CustomerImpl#getPassword()}
   *   <li>{@link CustomerImpl#getTaxExemptionCode()}
   *   <li>{@link CustomerImpl#getTransientProperties()}
   *   <li>{@link CustomerImpl#getUnencodedChallengeAnswer()}
   *   <li>{@link CustomerImpl#getUnencodedPassword()}
   *   <li>{@link CustomerImpl#getUsername()}
   *   <li>{@link CustomerImpl#isAnonymous()}
   *   <li>{@link CustomerImpl#isCookied()}
   *   <li>{@link CustomerImpl#isLoggedIn()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Auditable CustomerImpl.getAuditable()", "String CustomerImpl.getChallengeAnswer()",
      "ChallengeQuestion CustomerImpl.getChallengeQuestion()", "List CustomerImpl.getCustomerAddresses()",
      "Map CustomerImpl.getCustomerAttributes()", "Locale CustomerImpl.getCustomerLocale()",
      "List CustomerImpl.getCustomerPayments()", "List CustomerImpl.getCustomerPhones()",
      "String CustomerImpl.getEmailAddress()", "String CustomerImpl.getExternalId()",
      "String CustomerImpl.getFirstName()", "Long CustomerImpl.getId()", "String CustomerImpl.getLastName()",
      "String CustomerImpl.getPassword()", "String CustomerImpl.getTaxExemptionCode()",
      "Map CustomerImpl.getTransientProperties()", "String CustomerImpl.getUnencodedChallengeAnswer()",
      "String CustomerImpl.getUnencodedPassword()", "String CustomerImpl.getUsername()",
      "boolean CustomerImpl.isAnonymous()", "boolean CustomerImpl.isCookied()", "boolean CustomerImpl.isLoggedIn()",
      "void CustomerImpl.setAuditable(Auditable)", "void CustomerImpl.setChallengeAnswer(String)",
      "void CustomerImpl.setChallengeQuestion(ChallengeQuestion)", "void CustomerImpl.setCustomerAddresses(List)",
      "void CustomerImpl.setCustomerAttributes(Map)", "void CustomerImpl.setCustomerLocale(Locale)",
      "void CustomerImpl.setCustomerPayments(List)", "void CustomerImpl.setCustomerPhones(List)",
      "void CustomerImpl.setEmailAddress(String)", "void CustomerImpl.setExternalId(String)",
      "void CustomerImpl.setFirstName(String)", "void CustomerImpl.setId(Long)",
      "void CustomerImpl.setLastName(String)", "void CustomerImpl.setPassword(String)",
      "void CustomerImpl.setUnencodedChallengeAnswer(String)", "void CustomerImpl.setUnencodedPassword(String)",
      "void CustomerImpl.setUsername(String)"})
  public void testGettersAndSetters() {
    // Arrange
    CustomerImpl customerImpl = new CustomerImpl();

    Auditable auditable = new Auditable();
    auditable.setCreatedBy(1L);
    auditable.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setUpdatedBy(1L);

    // Act
    customerImpl.setAuditable(auditable);
    customerImpl.setChallengeAnswer("challengeAnswer");
    ChallengeQuestionImpl challengeQuestion = new ChallengeQuestionImpl();
    customerImpl.setChallengeQuestion(challengeQuestion);
    ArrayList<CustomerAddress> customerAddresses = new ArrayList<>();
    customerImpl.setCustomerAddresses(customerAddresses);
    HashMap<String, CustomerAttribute> customerAttributes = new HashMap<>();
    customerImpl.setCustomerAttributes(customerAttributes);
    LocaleImpl customerLocale = new LocaleImpl();
    customerImpl.setCustomerLocale(customerLocale);
    ArrayList<CustomerPayment> customerPayments = new ArrayList<>();
    customerImpl.setCustomerPayments(customerPayments);
    ArrayList<CustomerPhone> customerPhones = new ArrayList<>();
    customerImpl.setCustomerPhones(customerPhones);
    customerImpl.setEmailAddress("defaultEmail@example.com");
    customerImpl.setExternalId("externalId123");
    customerImpl.setFirstName("John");
    customerImpl.setId(1L);
    customerImpl.setLastName("Doe");
    customerImpl.setPassword("defaultPassword");
    customerImpl.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl.setUnencodedPassword("defaultPassword");
    customerImpl.setUsername("defaultUsername");
    Auditable actualAuditable = customerImpl.getAuditable();
    String actualChallengeAnswer = customerImpl.getChallengeAnswer();
    ChallengeQuestion actualChallengeQuestion = customerImpl.getChallengeQuestion();
    List<CustomerAddress> actualCustomerAddresses = customerImpl.getCustomerAddresses();
    Map<String, CustomerAttribute> actualCustomerAttributes = customerImpl.getCustomerAttributes();
    Locale actualCustomerLocale = customerImpl.getCustomerLocale();
    List<CustomerPayment> actualCustomerPayments = customerImpl.getCustomerPayments();
    List<CustomerPhone> actualCustomerPhones = customerImpl.getCustomerPhones();
    String actualEmailAddress = customerImpl.getEmailAddress();
    String actualExternalId = customerImpl.getExternalId();
    String actualFirstName = customerImpl.getFirstName();
    Long actualId = customerImpl.getId();
    String actualLastName = customerImpl.getLastName();
    String actualPassword = customerImpl.getPassword();
    String actualTaxExemptionCode = customerImpl.getTaxExemptionCode();
    Map<String, Object> actualTransientProperties = customerImpl.getTransientProperties();
    String actualUnencodedChallengeAnswer = customerImpl.getUnencodedChallengeAnswer();
    String actualUnencodedPassword = customerImpl.getUnencodedPassword();
    String actualUsername = customerImpl.getUsername();
    boolean actualIsAnonymousResult = customerImpl.isAnonymous();
    boolean actualIsCookiedResult = customerImpl.isCookied();
    boolean actualIsLoggedInResult = customerImpl.isLoggedIn();

    // Assert
    assertEquals("Doe", actualLastName);
    assertEquals("John", actualFirstName);
    assertEquals("challengeAnswer", actualChallengeAnswer);
    assertEquals("challengeAnswer", actualUnencodedChallengeAnswer);
    assertEquals("defaultEmail@example.com", actualEmailAddress);
    assertEquals("defaultPassword", actualPassword);
    assertEquals("defaultPassword", actualUnencodedPassword);
    assertEquals("defaultUsername", actualUsername);
    assertEquals("externalId123", actualExternalId);
    assertNull(actualTaxExemptionCode);
    assertEquals(1L, actualId.longValue());
    assertFalse(actualIsAnonymousResult);
    assertFalse(actualIsCookiedResult);
    assertFalse(actualIsLoggedInResult);
    assertTrue(actualCustomerAddresses.isEmpty());
    assertTrue(actualCustomerPayments.isEmpty());
    assertTrue(actualCustomerPhones.isEmpty());
    assertTrue(actualCustomerAttributes.isEmpty());
    assertTrue(actualTransientProperties.isEmpty());
    assertSame(customerAddresses, actualCustomerAddresses);
    assertSame(customerPayments, actualCustomerPayments);
    assertSame(customerPhones, actualCustomerPhones);
    assertSame(customerAttributes, actualCustomerAttributes);
    assertSame(auditable, actualAuditable);
    assertSame(customerLocale, actualCustomerLocale);
    assertSame(challengeQuestion, actualChallengeQuestion);
  }

  /**
   * Test {@link CustomerImpl#setAnonymous(boolean)}.
   * <ul>
   *   <li>Given {@link Auditable} (default constructor) CreatedBy is one.</li>
   *   <li>Then not {@link CustomerImpl} (default constructor) Anonymous.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#setAnonymous(boolean)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerImpl.setAnonymous(boolean)"})
  public void testSetAnonymous_givenAuditableCreatedByIsOne_thenNotCustomerImplAnonymous() {
    // Arrange
    Auditable auditable = new Auditable();
    auditable.setCreatedBy(1L);
    auditable.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setUpdatedBy(1L);

    CustomerImpl customerImpl2 = new CustomerImpl();
    customerImpl2.setAuditable(auditable);
    customerImpl2.setChallengeAnswer("challengeAnswer");
    customerImpl2.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl2.setCustomerAddresses(new ArrayList<>());
    customerImpl2.setCustomerAttributes(new HashMap<>());
    customerImpl2.setCustomerLocale(new LocaleImpl());
    customerImpl2.setCustomerPayments(new ArrayList<>());
    customerImpl2.setCustomerPhones(new ArrayList<>());
    customerImpl2.setDeactivated(false);
    customerImpl2.setEmailAddress("defaultEmail@example.com");
    customerImpl2.setExternalId("externalId123");
    customerImpl2.setFirstName("John");
    customerImpl2.setId(1L);
    customerImpl2.setLastName("Doe");
    customerImpl2.setPassword("defaultPassword");
    customerImpl2.setPasswordChangeRequired(false);
    customerImpl2.setReceiveEmail(false);
    customerImpl2.setRegistered(false);
    customerImpl2.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl2.setUnencodedPassword("defaultPassword");
    customerImpl2.setUsername("defaultUsername");

    // Act
    customerImpl2.setAnonymous(false);

    // Assert that nothing has changed
    assertFalse(customerImpl2.isAnonymous());
  }

  /**
   * Test {@link CustomerImpl#setAnonymous(boolean)}.
   * <ul>
   *   <li>Given {@link CustomerImpl} (default constructor).</li>
   *   <li>When {@code true}.</li>
   *   <li>Then {@link CustomerImpl} (default constructor) Anonymous.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#setAnonymous(boolean)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerImpl.setAnonymous(boolean)"})
  public void testSetAnonymous_givenCustomerImpl_whenTrue_thenCustomerImplAnonymous() {
    // Arrange
    CustomerImpl customerImpl2 = new CustomerImpl();

    // Act
    customerImpl2.setAnonymous(true);

    // Assert
    assertTrue(customerImpl2.isAnonymous());
  }

  /**
   * Test {@link CustomerImpl#setCookied(boolean)}.
   * <ul>
   *   <li>Given {@link Auditable} (default constructor) CreatedBy is one.</li>
   *   <li>When {@code false}.</li>
   *   <li>Then not {@link CustomerImpl} (default constructor) Cookied.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#setCookied(boolean)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerImpl.setCookied(boolean)"})
  public void testSetCookied_givenAuditableCreatedByIsOne_whenFalse_thenNotCustomerImplCookied() {
    // Arrange
    Auditable auditable = new Auditable();
    auditable.setCreatedBy(1L);
    auditable.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setUpdatedBy(1L);

    CustomerImpl customerImpl2 = new CustomerImpl();
    customerImpl2.setAuditable(auditable);
    customerImpl2.setChallengeAnswer("challengeAnswer");
    customerImpl2.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl2.setCustomerAddresses(new ArrayList<>());
    customerImpl2.setCustomerAttributes(new HashMap<>());
    customerImpl2.setCustomerLocale(new LocaleImpl());
    customerImpl2.setCustomerPayments(new ArrayList<>());
    customerImpl2.setCustomerPhones(new ArrayList<>());
    customerImpl2.setDeactivated(false);
    customerImpl2.setEmailAddress("defaultEmail@example.com");
    customerImpl2.setExternalId("externalId123");
    customerImpl2.setFirstName("John");
    customerImpl2.setId(1L);
    customerImpl2.setLastName("Doe");
    customerImpl2.setPassword("defaultPassword");
    customerImpl2.setPasswordChangeRequired(false);
    customerImpl2.setReceiveEmail(false);
    customerImpl2.setRegistered(false);
    customerImpl2.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl2.setUnencodedPassword("defaultPassword");
    customerImpl2.setUsername("defaultUsername");

    // Act
    customerImpl2.setCookied(false);

    // Assert that nothing has changed
    assertFalse(customerImpl2.isCookied());
  }

  /**
   * Test {@link CustomerImpl#setCookied(boolean)}.
   * <ul>
   *   <li>Given {@link CustomerImpl} (default constructor).</li>
   *   <li>When {@code true}.</li>
   *   <li>Then {@link CustomerImpl} (default constructor) Cookied.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#setCookied(boolean)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerImpl.setCookied(boolean)"})
  public void testSetCookied_givenCustomerImpl_whenTrue_thenCustomerImplCookied() {
    // Arrange
    CustomerImpl customerImpl2 = new CustomerImpl();

    // Act
    customerImpl2.setCookied(true);

    // Assert
    assertTrue(customerImpl2.isCookied());
  }

  /**
   * Test {@link CustomerImpl#setLoggedIn(boolean)}.
   * <ul>
   *   <li>Given {@link Auditable} (default constructor) CreatedBy is one.</li>
   *   <li>Then not {@link CustomerImpl} (default constructor) LoggedIn.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#setLoggedIn(boolean)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerImpl.setLoggedIn(boolean)"})
  public void testSetLoggedIn_givenAuditableCreatedByIsOne_thenNotCustomerImplLoggedIn() {
    // Arrange
    Auditable auditable = new Auditable();
    auditable.setCreatedBy(1L);
    auditable.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setUpdatedBy(1L);

    CustomerImpl customerImpl2 = new CustomerImpl();
    customerImpl2.setAuditable(auditable);
    customerImpl2.setChallengeAnswer("challengeAnswer");
    customerImpl2.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl2.setCustomerAddresses(new ArrayList<>());
    customerImpl2.setCustomerAttributes(new HashMap<>());
    customerImpl2.setCustomerLocale(new LocaleImpl());
    customerImpl2.setCustomerPayments(new ArrayList<>());
    customerImpl2.setCustomerPhones(new ArrayList<>());
    customerImpl2.setDeactivated(false);
    customerImpl2.setEmailAddress("defaultEmail@example.com");
    customerImpl2.setExternalId("externalId123");
    customerImpl2.setFirstName("John");
    customerImpl2.setId(1L);
    customerImpl2.setLastName("Doe");
    customerImpl2.setPassword("defaultPassword");
    customerImpl2.setPasswordChangeRequired(false);
    customerImpl2.setReceiveEmail(false);
    customerImpl2.setRegistered(false);
    customerImpl2.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl2.setUnencodedPassword("defaultPassword");
    customerImpl2.setUsername("defaultUsername");

    // Act
    customerImpl2.setLoggedIn(false);

    // Assert that nothing has changed
    assertFalse(customerImpl2.isLoggedIn());
  }

  /**
   * Test {@link CustomerImpl#setLoggedIn(boolean)}.
   * <ul>
   *   <li>Given {@link CustomerImpl} (default constructor).</li>
   *   <li>When {@code true}.</li>
   *   <li>Then {@link CustomerImpl} (default constructor) LoggedIn.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#setLoggedIn(boolean)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerImpl.setLoggedIn(boolean)"})
  public void testSetLoggedIn_givenCustomerImpl_whenTrue_thenCustomerImplLoggedIn() {
    // Arrange
    CustomerImpl customerImpl2 = new CustomerImpl();

    // Act
    customerImpl2.setLoggedIn(true);

    // Assert
    assertTrue(customerImpl2.isLoggedIn());
  }

  /**
   * Test {@link CustomerImpl#isDeactivated()}.
   * <ul>
   *   <li>Given {@link Auditable} (default constructor) CreatedBy is one.</li>
   *   <li>Then return {@code true}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#isDeactivated()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerImpl.isDeactivated()"})
  public void testIsDeactivated_givenAuditableCreatedByIsOne_thenReturnTrue() {
    // Arrange
    Auditable auditable = new Auditable();
    auditable.setCreatedBy(1L);
    auditable.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setUpdatedBy(1L);

    CustomerImpl customerImpl2 = new CustomerImpl();
    customerImpl2.setAuditable(auditable);
    customerImpl2.setChallengeAnswer("challengeAnswer");
    customerImpl2.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl2.setCustomerAddresses(new ArrayList<>());
    customerImpl2.setCustomerAttributes(new HashMap<>());
    customerImpl2.setCustomerLocale(new LocaleImpl());
    customerImpl2.setCustomerPayments(new ArrayList<>());
    customerImpl2.setCustomerPhones(new ArrayList<>());
    customerImpl2.setEmailAddress("defaultEmail@example.com");
    customerImpl2.setExternalId("externalId123");
    customerImpl2.setFirstName("John");
    customerImpl2.setId(1L);
    customerImpl2.setLastName("Doe");
    customerImpl2.setPassword("defaultPassword");
    customerImpl2.setPasswordChangeRequired(false);
    customerImpl2.setReceiveEmail(false);
    customerImpl2.setRegistered(false);
    customerImpl2.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl2.setUnencodedPassword("defaultPassword");
    customerImpl2.setUsername("defaultUsername");
    customerImpl2.setDeactivated(true);

    // Act and Assert
    assertTrue(customerImpl2.isDeactivated());
  }

  /**
   * Test {@link CustomerImpl#isDeactivated()}.
   * <ul>
   *   <li>Given {@link CustomerImpl} (default constructor).</li>
   *   <li>Then return {@code false}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#isDeactivated()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerImpl.isDeactivated()"})
  public void testIsDeactivated_givenCustomerImpl_thenReturnFalse() {
    // Arrange, Act and Assert
    assertFalse((new CustomerImpl()).isDeactivated());
  }

  /**
   * Test {@link CustomerImpl#getMainEntityName()}.
   * <ul>
   *   <li>Given {@link CustomerImpl} (default constructor) FirstName is empty string.</li>
   *   <li>Then return {@code 1}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#getMainEntityName()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"String CustomerImpl.getMainEntityName()"})
  public void testGetMainEntityName_givenCustomerImplFirstNameIsEmptyString_thenReturn1() {
    // Arrange
    Auditable auditable = new Auditable();
    auditable.setCreatedBy(1L);
    auditable.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setUpdatedBy(1L);

    CustomerImpl customerImpl2 = new CustomerImpl();
    customerImpl2.setAuditable(auditable);
    customerImpl2.setChallengeAnswer("challengeAnswer");
    customerImpl2.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl2.setCustomerAddresses(new ArrayList<>());
    customerImpl2.setCustomerAttributes(new HashMap<>());
    customerImpl2.setCustomerLocale(new LocaleImpl());
    customerImpl2.setCustomerPayments(new ArrayList<>());
    customerImpl2.setCustomerPhones(new ArrayList<>());
    customerImpl2.setDeactivated(false);
    customerImpl2.setEmailAddress("defaultEmail@example.com");
    customerImpl2.setExternalId("externalId123");
    customerImpl2.setId(1L);
    customerImpl2.setPassword("defaultPassword");
    customerImpl2.setPasswordChangeRequired(false);
    customerImpl2.setReceiveEmail(false);
    customerImpl2.setRegistered(false);
    customerImpl2.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl2.setUnencodedPassword("defaultPassword");
    customerImpl2.setFirstName("");
    customerImpl2.setLastName("");
    customerImpl2.setUsername("");

    // Act and Assert
    assertEquals("1", customerImpl2.getMainEntityName());
  }

  /**
   * Test {@link CustomerImpl#getMainEntityName()}.
   * <ul>
   *   <li>Given {@link CustomerImpl} (default constructor) FirstName is {@code foo}.</li>
   *   <li>Then return {@code 1}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#getMainEntityName()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"String CustomerImpl.getMainEntityName()"})
  public void testGetMainEntityName_givenCustomerImplFirstNameIsFoo_thenReturn1() {
    // Arrange
    Auditable auditable = new Auditable();
    auditable.setCreatedBy(1L);
    auditable.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setUpdatedBy(1L);

    CustomerImpl customerImpl2 = new CustomerImpl();
    customerImpl2.setAuditable(auditable);
    customerImpl2.setChallengeAnswer("challengeAnswer");
    customerImpl2.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl2.setCustomerAddresses(new ArrayList<>());
    customerImpl2.setCustomerAttributes(new HashMap<>());
    customerImpl2.setCustomerLocale(new LocaleImpl());
    customerImpl2.setCustomerPayments(new ArrayList<>());
    customerImpl2.setCustomerPhones(new ArrayList<>());
    customerImpl2.setDeactivated(false);
    customerImpl2.setEmailAddress("defaultEmail@example.com");
    customerImpl2.setExternalId("externalId123");
    customerImpl2.setId(1L);
    customerImpl2.setPassword("defaultPassword");
    customerImpl2.setPasswordChangeRequired(false);
    customerImpl2.setReceiveEmail(false);
    customerImpl2.setRegistered(false);
    customerImpl2.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl2.setUnencodedPassword("defaultPassword");
    customerImpl2.setFirstName("foo");
    customerImpl2.setLastName("");
    customerImpl2.setUsername("");

    // Act and Assert
    assertEquals("1", customerImpl2.getMainEntityName());
  }

  /**
   * Test {@link CustomerImpl#getMainEntityName()}.
   * <ul>
   *   <li>Given {@link CustomerImpl} (default constructor) LastName is {@code foo}.</li>
   *   <li>Then return {@code foo foo}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#getMainEntityName()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"String CustomerImpl.getMainEntityName()"})
  public void testGetMainEntityName_givenCustomerImplLastNameIsFoo_thenReturnFooFoo() {
    // Arrange
    Auditable auditable = new Auditable();
    auditable.setCreatedBy(1L);
    auditable.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setUpdatedBy(1L);

    CustomerImpl customerImpl2 = new CustomerImpl();
    customerImpl2.setAuditable(auditable);
    customerImpl2.setChallengeAnswer("challengeAnswer");
    customerImpl2.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl2.setCustomerAddresses(new ArrayList<>());
    customerImpl2.setCustomerAttributes(new HashMap<>());
    customerImpl2.setCustomerLocale(new LocaleImpl());
    customerImpl2.setCustomerPayments(new ArrayList<>());
    customerImpl2.setCustomerPhones(new ArrayList<>());
    customerImpl2.setDeactivated(false);
    customerImpl2.setEmailAddress("defaultEmail@example.com");
    customerImpl2.setExternalId("externalId123");
    customerImpl2.setId(1L);
    customerImpl2.setPassword("defaultPassword");
    customerImpl2.setPasswordChangeRequired(false);
    customerImpl2.setReceiveEmail(false);
    customerImpl2.setRegistered(false);
    customerImpl2.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl2.setUnencodedPassword("defaultPassword");
    customerImpl2.setFirstName("foo");
    customerImpl2.setLastName("foo");
    customerImpl2.setUsername("");

    // Act and Assert
    assertEquals("foo foo", customerImpl2.getMainEntityName());
  }

  /**
   * Test {@link CustomerImpl#getMainEntityName()}.
   * <ul>
   *   <li>Given {@link CustomerImpl} (default constructor) Username is {@code foo}.</li>
   *   <li>Then return {@code foo}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#getMainEntityName()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"String CustomerImpl.getMainEntityName()"})
  public void testGetMainEntityName_givenCustomerImplUsernameIsFoo_thenReturnFoo() {
    // Arrange
    Auditable auditable = new Auditable();
    auditable.setCreatedBy(1L);
    auditable.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setUpdatedBy(1L);

    CustomerImpl customerImpl2 = new CustomerImpl();
    customerImpl2.setAuditable(auditable);
    customerImpl2.setChallengeAnswer("challengeAnswer");
    customerImpl2.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl2.setCustomerAddresses(new ArrayList<>());
    customerImpl2.setCustomerAttributes(new HashMap<>());
    customerImpl2.setCustomerLocale(new LocaleImpl());
    customerImpl2.setCustomerPayments(new ArrayList<>());
    customerImpl2.setCustomerPhones(new ArrayList<>());
    customerImpl2.setDeactivated(false);
    customerImpl2.setEmailAddress("defaultEmail@example.com");
    customerImpl2.setExternalId("externalId123");
    customerImpl2.setId(1L);
    customerImpl2.setPassword("defaultPassword");
    customerImpl2.setPasswordChangeRequired(false);
    customerImpl2.setReceiveEmail(false);
    customerImpl2.setRegistered(false);
    customerImpl2.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl2.setUnencodedPassword("defaultPassword");
    customerImpl2.setFirstName("");
    customerImpl2.setLastName("");
    customerImpl2.setUsername("foo");

    // Act and Assert
    assertEquals("foo", customerImpl2.getMainEntityName());
  }

  /**
   * Test {@link CustomerImpl#getMainEntityName()}.
   * <ul>
   *   <li>Given {@link CustomerImpl} (default constructor).</li>
   *   <li>Then return {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#getMainEntityName()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"String CustomerImpl.getMainEntityName()"})
  public void testGetMainEntityName_givenCustomerImpl_thenReturnNull() {
    // Arrange, Act and Assert
    assertEquals("null", (new CustomerImpl()).getMainEntityName());
  }

  /**
   * Test {@link CustomerImpl#getPreview()}.
   * <ul>
   *   <li>Given {@link Auditable} (default constructor) CreatedBy is one.</li>
   *   <li>Then return {@code true}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#getPreview()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Boolean CustomerImpl.getPreview()"})
  public void testGetPreview_givenAuditableCreatedByIsOne_thenReturnTrue() {
    // Arrange
    Auditable auditable = new Auditable();
    auditable.setCreatedBy(1L);
    auditable.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setUpdatedBy(1L);

    CustomerImpl customerImpl2 = new CustomerImpl();
    customerImpl2.setAuditable(auditable);
    customerImpl2.setChallengeAnswer("challengeAnswer");
    customerImpl2.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl2.setCustomerAddresses(new ArrayList<>());
    customerImpl2.setCustomerAttributes(new HashMap<>());
    customerImpl2.setCustomerLocale(new LocaleImpl());
    customerImpl2.setCustomerPayments(new ArrayList<>());
    customerImpl2.setCustomerPhones(new ArrayList<>());
    customerImpl2.setDeactivated(false);
    customerImpl2.setEmailAddress("defaultEmail@example.com");
    customerImpl2.setExternalId("externalId123");
    customerImpl2.setFirstName("John");
    customerImpl2.setId(1L);
    customerImpl2.setLastName("Doe");
    customerImpl2.setPassword("defaultPassword");
    customerImpl2.setPasswordChangeRequired(false);
    customerImpl2.setReceiveEmail(false);
    customerImpl2.setRegistered(false);
    customerImpl2.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl2.setUnencodedPassword("defaultPassword");
    customerImpl2.setUsername("defaultUsername");
    customerImpl2.setPreview(true);

    // Act and Assert
    assertTrue(customerImpl2.getPreview());
  }

  /**
   * Test {@link CustomerImpl#getPreview()}.
   * <ul>
   *   <li>Given {@link CustomerImpl} (default constructor).</li>
   *   <li>Then return {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#getPreview()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Boolean CustomerImpl.getPreview()"})
  public void testGetPreview_givenCustomerImpl_thenReturnNull() {
    // Arrange, Act and Assert
    assertNull((new CustomerImpl()).getPreview());
  }

  /**
   * Test {@link CustomerImpl#setPreview(Boolean)}.
   * <p>
   * Method under test: {@link CustomerImpl#setPreview(Boolean)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerImpl.setPreview(Boolean)"})
  public void testSetPreview() {
    // Arrange
    CustomerImpl customerImpl2 = new CustomerImpl();

    // Act
    customerImpl2.setPreview(true);

    // Assert
    assertTrue(customerImpl2.previewable.getPreview());
    assertTrue(customerImpl2.getPreview());
  }

  /**
   * Test {@link CustomerImpl#equals(Object)}, and {@link CustomerImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerImpl#equals(Object)}
   *   <li>{@link CustomerImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerImpl.equals(Object)", "int CustomerImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual() {
    // Arrange
    Auditable auditable = new Auditable();
    auditable.setCreatedBy(1L);
    auditable.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setUpdatedBy(1L);

    CustomerImpl customerImpl = new CustomerImpl();
    customerImpl.setAuditable(auditable);
    customerImpl.setChallengeAnswer("challengeAnswer");
    customerImpl.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl.setCustomerAddresses(new ArrayList<>());
    customerImpl.setCustomerAttributes(new HashMap<>());
    customerImpl.setCustomerLocale(new LocaleImpl());
    customerImpl.setCustomerPayments(new ArrayList<>());
    customerImpl.setCustomerPhones(new ArrayList<>());
    customerImpl.setDeactivated(false);
    customerImpl.setEmailAddress("defaultEmail@example.com");
    customerImpl.setExternalId("externalId123");
    customerImpl.setFirstName("John");
    customerImpl.setId(1L);
    customerImpl.setLastName("Doe");
    customerImpl.setPassword("defaultPassword");
    customerImpl.setPasswordChangeRequired(false);
    customerImpl.setReceiveEmail(false);
    customerImpl.setRegistered(false);
    customerImpl.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl.setUnencodedPassword("defaultPassword");
    customerImpl.setUsername("defaultUsername");

    Auditable auditable2 = new Auditable();
    auditable2.setCreatedBy(1L);
    auditable2.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable2.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable2.setUpdatedBy(1L);

    CustomerImpl customerImpl2 = new CustomerImpl();
    customerImpl2.setAuditable(auditable2);
    customerImpl2.setChallengeAnswer("challengeAnswer");
    customerImpl2.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl2.setCustomerAddresses(new ArrayList<>());
    customerImpl2.setCustomerAttributes(new HashMap<>());
    customerImpl2.setCustomerLocale(new LocaleImpl());
    customerImpl2.setCustomerPayments(new ArrayList<>());
    customerImpl2.setCustomerPhones(new ArrayList<>());
    customerImpl2.setDeactivated(false);
    customerImpl2.setEmailAddress("defaultEmail@example.com");
    customerImpl2.setExternalId("externalId123");
    customerImpl2.setFirstName("John");
    customerImpl2.setId(1L);
    customerImpl2.setLastName("Doe");
    customerImpl2.setPassword("defaultPassword");
    customerImpl2.setPasswordChangeRequired(false);
    customerImpl2.setReceiveEmail(false);
    customerImpl2.setRegistered(false);
    customerImpl2.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl2.setUnencodedPassword("defaultPassword");
    customerImpl2.setUsername("defaultUsername");

    // Act and Assert
    assertEquals(customerImpl, customerImpl2);
    int expectedHashCodeResult = customerImpl.hashCode();
    assertEquals(expectedHashCodeResult, customerImpl2.hashCode());
  }

  /**
   * Test {@link CustomerImpl#equals(Object)}, and {@link CustomerImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerImpl#equals(Object)}
   *   <li>{@link CustomerImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerImpl.equals(Object)", "int CustomerImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual2() {
    // Arrange
    Auditable auditable = new Auditable();
    auditable.setCreatedBy(1L);
    auditable.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setUpdatedBy(1L);

    CustomerImpl customerImpl = new CustomerImpl();
    customerImpl.setAuditable(auditable);
    customerImpl.setChallengeAnswer("challengeAnswer");
    customerImpl.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl.setCustomerAddresses(new ArrayList<>());
    customerImpl.setCustomerAttributes(new HashMap<>());
    customerImpl.setCustomerLocale(new LocaleImpl());
    customerImpl.setCustomerPayments(new ArrayList<>());
    customerImpl.setCustomerPhones(new ArrayList<>());
    customerImpl.setDeactivated(false);
    customerImpl.setEmailAddress("defaultEmail@example.com");
    customerImpl.setExternalId("externalId123");
    customerImpl.setFirstName("John");
    customerImpl.setId(null);
    customerImpl.setLastName("Doe");
    customerImpl.setPassword("defaultPassword");
    customerImpl.setPasswordChangeRequired(false);
    customerImpl.setReceiveEmail(false);
    customerImpl.setRegistered(false);
    customerImpl.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl.setUnencodedPassword("defaultPassword");
    customerImpl.setUsername("defaultUsername");

    Auditable auditable2 = new Auditable();
    auditable2.setCreatedBy(1L);
    auditable2.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable2.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable2.setUpdatedBy(1L);

    CustomerImpl customerImpl2 = new CustomerImpl();
    customerImpl2.setAuditable(auditable2);
    customerImpl2.setChallengeAnswer("challengeAnswer");
    customerImpl2.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl2.setCustomerAddresses(new ArrayList<>());
    customerImpl2.setCustomerAttributes(new HashMap<>());
    customerImpl2.setCustomerLocale(new LocaleImpl());
    customerImpl2.setCustomerPayments(new ArrayList<>());
    customerImpl2.setCustomerPhones(new ArrayList<>());
    customerImpl2.setDeactivated(false);
    customerImpl2.setEmailAddress("defaultEmail@example.com");
    customerImpl2.setExternalId("externalId123");
    customerImpl2.setFirstName("John");
    customerImpl2.setId(1L);
    customerImpl2.setLastName("Doe");
    customerImpl2.setPassword("defaultPassword");
    customerImpl2.setPasswordChangeRequired(false);
    customerImpl2.setReceiveEmail(false);
    customerImpl2.setRegistered(false);
    customerImpl2.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl2.setUnencodedPassword("defaultPassword");
    customerImpl2.setUsername("defaultUsername");

    // Act and Assert
    assertEquals(customerImpl, customerImpl2);
    int expectedHashCodeResult = customerImpl.hashCode();
    assertEquals(expectedHashCodeResult, customerImpl2.hashCode());
  }

  /**
   * Test {@link CustomerImpl#equals(Object)}, and {@link CustomerImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerImpl#equals(Object)}
   *   <li>{@link CustomerImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerImpl.equals(Object)", "int CustomerImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual3() {
    // Arrange
    Auditable auditable = new Auditable();
    auditable.setCreatedBy(1L);
    auditable.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setUpdatedBy(1L);

    CustomerImpl customerImpl = new CustomerImpl();
    customerImpl.setAuditable(auditable);
    customerImpl.setChallengeAnswer("challengeAnswer");
    customerImpl.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl.setCustomerAddresses(new ArrayList<>());
    customerImpl.setCustomerAttributes(new HashMap<>());
    customerImpl.setCustomerLocale(new LocaleImpl());
    customerImpl.setCustomerPayments(new ArrayList<>());
    customerImpl.setCustomerPhones(new ArrayList<>());
    customerImpl.setDeactivated(false);
    customerImpl.setEmailAddress("defaultEmail@example.com");
    customerImpl.setExternalId("externalId123");
    customerImpl.setFirstName("John");
    customerImpl.setId(1L);
    customerImpl.setLastName("Doe");
    customerImpl.setPassword("defaultPassword");
    customerImpl.setPasswordChangeRequired(false);
    customerImpl.setReceiveEmail(false);
    customerImpl.setRegistered(false);
    customerImpl.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl.setUnencodedPassword("defaultPassword");
    customerImpl.setUsername("defaultUsername");

    Auditable auditable2 = new Auditable();
    auditable2.setCreatedBy(1L);
    auditable2.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable2.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable2.setUpdatedBy(1L);

    CustomerImpl customerImpl2 = new CustomerImpl();
    customerImpl2.setAuditable(auditable2);
    customerImpl2.setChallengeAnswer("challengeAnswer");
    customerImpl2.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl2.setCustomerAddresses(new ArrayList<>());
    customerImpl2.setCustomerAttributes(new HashMap<>());
    customerImpl2.setCustomerLocale(new LocaleImpl());
    customerImpl2.setCustomerPayments(new ArrayList<>());
    customerImpl2.setCustomerPhones(new ArrayList<>());
    customerImpl2.setDeactivated(false);
    customerImpl2.setEmailAddress("defaultEmail@example.com");
    customerImpl2.setExternalId("externalId123");
    customerImpl2.setFirstName("John");
    customerImpl2.setId(null);
    customerImpl2.setLastName("Doe");
    customerImpl2.setPassword("defaultPassword");
    customerImpl2.setPasswordChangeRequired(false);
    customerImpl2.setReceiveEmail(false);
    customerImpl2.setRegistered(false);
    customerImpl2.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl2.setUnencodedPassword("defaultPassword");
    customerImpl2.setUsername("defaultUsername");

    // Act and Assert
    assertEquals(customerImpl, customerImpl2);
    int expectedHashCodeResult = customerImpl.hashCode();
    assertEquals(expectedHashCodeResult, customerImpl2.hashCode());
  }

  /**
   * Test {@link CustomerImpl#equals(Object)}, and {@link CustomerImpl#hashCode()}.
   * <ul>
   *   <li>When other is same.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerImpl#equals(Object)}
   *   <li>{@link CustomerImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerImpl.equals(Object)", "int CustomerImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual() {
    // Arrange
    Auditable auditable = new Auditable();
    auditable.setCreatedBy(1L);
    auditable.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setUpdatedBy(1L);

    CustomerImpl customerImpl = new CustomerImpl();
    customerImpl.setAuditable(auditable);
    customerImpl.setChallengeAnswer("challengeAnswer");
    customerImpl.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl.setCustomerAddresses(new ArrayList<>());
    customerImpl.setCustomerAttributes(new HashMap<>());
    customerImpl.setCustomerLocale(new LocaleImpl());
    customerImpl.setCustomerPayments(new ArrayList<>());
    customerImpl.setCustomerPhones(new ArrayList<>());
    customerImpl.setDeactivated(false);
    customerImpl.setEmailAddress("defaultEmail@example.com");
    customerImpl.setExternalId("externalId123");
    customerImpl.setFirstName("John");
    customerImpl.setId(1L);
    customerImpl.setLastName("Doe");
    customerImpl.setPassword("defaultPassword");
    customerImpl.setPasswordChangeRequired(false);
    customerImpl.setReceiveEmail(false);
    customerImpl.setRegistered(false);
    customerImpl.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl.setUnencodedPassword("defaultPassword");
    customerImpl.setUsername("defaultUsername");

    // Act and Assert
    assertEquals(customerImpl, customerImpl);
    int expectedHashCodeResult = customerImpl.hashCode();
    assertEquals(expectedHashCodeResult, customerImpl.hashCode());
  }

  /**
   * Test {@link CustomerImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerImpl.equals(Object)", "int CustomerImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual() {
    // Arrange
    Auditable auditable = new Auditable();
    auditable.setCreatedBy(1L);
    auditable.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setUpdatedBy(1L);

    CustomerImpl customerImpl = new CustomerImpl();
    customerImpl.setAuditable(auditable);
    customerImpl.setChallengeAnswer("challengeAnswer");
    customerImpl.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl.setCustomerAddresses(new ArrayList<>());
    customerImpl.setCustomerAttributes(new HashMap<>());
    customerImpl.setCustomerLocale(new LocaleImpl());
    customerImpl.setCustomerPayments(new ArrayList<>());
    customerImpl.setCustomerPhones(new ArrayList<>());
    customerImpl.setDeactivated(false);
    customerImpl.setEmailAddress("defaultEmail@example.com");
    customerImpl.setExternalId("externalId123");
    customerImpl.setFirstName("John");
    customerImpl.setId(2L);
    customerImpl.setLastName("Doe");
    customerImpl.setPassword("defaultPassword");
    customerImpl.setPasswordChangeRequired(false);
    customerImpl.setReceiveEmail(false);
    customerImpl.setRegistered(false);
    customerImpl.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl.setUnencodedPassword("defaultPassword");
    customerImpl.setUsername("defaultUsername");

    Auditable auditable2 = new Auditable();
    auditable2.setCreatedBy(1L);
    auditable2.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable2.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable2.setUpdatedBy(1L);

    CustomerImpl customerImpl2 = new CustomerImpl();
    customerImpl2.setAuditable(auditable2);
    customerImpl2.setChallengeAnswer("challengeAnswer");
    customerImpl2.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl2.setCustomerAddresses(new ArrayList<>());
    customerImpl2.setCustomerAttributes(new HashMap<>());
    customerImpl2.setCustomerLocale(new LocaleImpl());
    customerImpl2.setCustomerPayments(new ArrayList<>());
    customerImpl2.setCustomerPhones(new ArrayList<>());
    customerImpl2.setDeactivated(false);
    customerImpl2.setEmailAddress("defaultEmail@example.com");
    customerImpl2.setExternalId("externalId123");
    customerImpl2.setFirstName("John");
    customerImpl2.setId(1L);
    customerImpl2.setLastName("Doe");
    customerImpl2.setPassword("defaultPassword");
    customerImpl2.setPasswordChangeRequired(false);
    customerImpl2.setReceiveEmail(false);
    customerImpl2.setRegistered(false);
    customerImpl2.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl2.setUnencodedPassword("defaultPassword");
    customerImpl2.setUsername("defaultUsername");

    // Act and Assert
    assertNotEquals(customerImpl, customerImpl2);
  }

  /**
   * Test {@link CustomerImpl#equals(Object)}.
   * <ul>
   *   <li>When other is {@code null}.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerImpl.equals(Object)", "int CustomerImpl.hashCode()"})
  public void testEquals_whenOtherIsNull_thenReturnNotEqual() {
    // Arrange
    Auditable auditable = new Auditable();
    auditable.setCreatedBy(1L);
    auditable.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setUpdatedBy(1L);

    CustomerImpl customerImpl = new CustomerImpl();
    customerImpl.setAuditable(auditable);
    customerImpl.setChallengeAnswer("challengeAnswer");
    customerImpl.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl.setCustomerAddresses(new ArrayList<>());
    customerImpl.setCustomerAttributes(new HashMap<>());
    customerImpl.setCustomerLocale(new LocaleImpl());
    customerImpl.setCustomerPayments(new ArrayList<>());
    customerImpl.setCustomerPhones(new ArrayList<>());
    customerImpl.setDeactivated(false);
    customerImpl.setEmailAddress("defaultEmail@example.com");
    customerImpl.setExternalId("externalId123");
    customerImpl.setFirstName("John");
    customerImpl.setId(1L);
    customerImpl.setLastName("Doe");
    customerImpl.setPassword("defaultPassword");
    customerImpl.setPasswordChangeRequired(false);
    customerImpl.setReceiveEmail(false);
    customerImpl.setRegistered(false);
    customerImpl.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl.setUnencodedPassword("defaultPassword");
    customerImpl.setUsername("defaultUsername");

    // Act and Assert
    assertNotEquals(customerImpl, null);
  }

  /**
   * Test {@link CustomerImpl#equals(Object)}.
   * <ul>
   *   <li>When other is wrong type.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerImpl.equals(Object)", "int CustomerImpl.hashCode()"})
  public void testEquals_whenOtherIsWrongType_thenReturnNotEqual() {
    // Arrange
    Auditable auditable = new Auditable();
    auditable.setCreatedBy(1L);
    auditable.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setUpdatedBy(1L);

    CustomerImpl customerImpl = new CustomerImpl();
    customerImpl.setAuditable(auditable);
    customerImpl.setChallengeAnswer("challengeAnswer");
    customerImpl.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl.setCustomerAddresses(new ArrayList<>());
    customerImpl.setCustomerAttributes(new HashMap<>());
    customerImpl.setCustomerLocale(new LocaleImpl());
    customerImpl.setCustomerPayments(new ArrayList<>());
    customerImpl.setCustomerPhones(new ArrayList<>());
    customerImpl.setDeactivated(false);
    customerImpl.setEmailAddress("defaultEmail@example.com");
    customerImpl.setExternalId("externalId123");
    customerImpl.setFirstName("John");
    customerImpl.setId(1L);
    customerImpl.setLastName("Doe");
    customerImpl.setPassword("defaultPassword");
    customerImpl.setPasswordChangeRequired(false);
    customerImpl.setReceiveEmail(false);
    customerImpl.setRegistered(false);
    customerImpl.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl.setUnencodedPassword("defaultPassword");
    customerImpl.setUsername("defaultUsername");

    // Act and Assert
    assertNotEquals(customerImpl, "Different type to CustomerImpl");
  }

  /**
   * Test {@link CustomerImpl#createOrRetrieveCopyInstance(MultiTenantCopyContext)}.
   * <p>
   * Method under test: {@link CustomerImpl#createOrRetrieveCopyInstance(MultiTenantCopyContext)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CreateResponse CustomerImpl.createOrRetrieveCopyInstance(MultiTenantCopyContext)"})
  public void testCreateOrRetrieveCopyInstance() throws CloneNotSupportedException {
    // Arrange
    CustomerImpl customerImpl2 = new CustomerImpl();
    MultiTenantCopyContext context = mock(MultiTenantCopyContext.class);
    CreateResponse<Object> createResponse = new CreateResponse<>("Clone", true);

    when(context.createOrRetrieveCopyInstance(Mockito.<Object>any())).thenReturn(createResponse);

    // Act
    CreateResponse<Customer> actualCreateOrRetrieveCopyInstanceResult = customerImpl2
        .createOrRetrieveCopyInstance(context);

    // Assert
    verify(context).createOrRetrieveCopyInstance(isA(Object.class));
    assertSame(createResponse, actualCreateOrRetrieveCopyInstanceResult);
  }

  /**
   * Test {@link CustomerImpl#createOrRetrieveCopyInstance(MultiTenantCopyContext)}.
   * <ul>
   *   <li>Given {@code Object}.</li>
   *   <li>Then Clone return {@link CustomerImpl}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#createOrRetrieveCopyInstance(MultiTenantCopyContext)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CreateResponse CustomerImpl.createOrRetrieveCopyInstance(MultiTenantCopyContext)"})
  public void testCreateOrRetrieveCopyInstance_givenJavaLangObject_thenCloneReturnCustomerImpl()
      throws CloneNotSupportedException {
    // Arrange
    CustomerImpl customerImpl2 = new CustomerImpl();
    GenericEntityService genericEntityService = mock(GenericEntityService.class);
    when(genericEntityService.getIdentifier(Mockito.<Object>any())).thenReturn(null);
    Class<Object> forNameResult = Object.class;
    Mockito.<Class<?>>when(genericEntityService.getCeilingImplClass(Mockito.<String>any())).thenReturn(forNameResult);
    CatalogImpl fromCatalog = new CatalogImpl();
    CatalogImpl toCatalog = new CatalogImpl();
    SiteImpl fromSite = new SiteImpl();
    SiteImpl toSite = new SiteImpl();

    // Act
    CreateResponse<Customer> actualCreateOrRetrieveCopyInstanceResult = customerImpl2
        .createOrRetrieveCopyInstance(new MultiTenantCopyContext(fromCatalog, toCatalog, fromSite, toSite,
            genericEntityService, new MultiTenantCopierExtensionManager()));

    // Assert
    verify(genericEntityService).getCeilingImplClass(eq("org.broadleafcommerce.profile.core.domain.CustomerImpl"));
    verify(genericEntityService).getIdentifier(isA(Object.class));
    Customer clone = actualCreateOrRetrieveCopyInstanceResult.getClone();
    assertTrue(clone instanceof CustomerImpl);
    assertFalse(actualCreateOrRetrieveCopyInstanceResult.isAlreadyPopulated());
    assertEquals(customerImpl2, clone);
  }

  /**
   * Test {@link CustomerImpl#setTaxExemptionCode(String)}.
   * <ul>
   *   <li>Then {@link CustomerImpl} (default constructor) TaxExemptionCode is {@code TAX123}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#setTaxExemptionCode(String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerImpl.setTaxExemptionCode(String)"})
  public void testSetTaxExemptionCode_thenCustomerImplTaxExemptionCodeIsTax123() {
    // Arrange
    CustomerImpl customerImpl2 = new CustomerImpl();

    // Act
    customerImpl2.setTaxExemptionCode("TAX123");

    // Assert
    assertEquals("TAX123", customerImpl2.getTaxExemptionCode());
    assertTrue(customerImpl2.isTaxExempt);
  }

  /**
   * Test {@link CustomerImpl#setTaxExemptionCode(String)}.
   * <ul>
   *   <li>Then not {@link CustomerImpl} (default constructor) {@link CustomerImpl#isTaxExempt}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerImpl#setTaxExemptionCode(String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerImpl.setTaxExemptionCode(String)"})
  public void testSetTaxExemptionCode_thenNotCustomerImplIsTaxExempt() {
    // Arrange
    Auditable auditable = new Auditable();
    auditable.setCreatedBy(1L);
    auditable.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setUpdatedBy(1L);

    CustomerImpl customerImpl2 = new CustomerImpl();
    customerImpl2.setAuditable(auditable);
    customerImpl2.setChallengeAnswer("challengeAnswer");
    customerImpl2.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl2.setCustomerAddresses(new ArrayList<>());
    customerImpl2.setCustomerAttributes(new HashMap<>());
    customerImpl2.setCustomerLocale(new LocaleImpl());
    customerImpl2.setCustomerPayments(new ArrayList<>());
    customerImpl2.setCustomerPhones(new ArrayList<>());
    customerImpl2.setDeactivated(false);
    customerImpl2.setEmailAddress("defaultEmail@example.com");
    customerImpl2.setExternalId("externalId123");
    customerImpl2.setFirstName("John");
    customerImpl2.setId(1L);
    customerImpl2.setLastName("Doe");
    customerImpl2.setPassword("defaultPassword");
    customerImpl2.setPasswordChangeRequired(false);
    customerImpl2.setReceiveEmail(false);
    customerImpl2.setRegistered(false);
    customerImpl2.setUnencodedChallengeAnswer("challengeAnswer");
    customerImpl2.setUnencodedPassword("defaultPassword");
    customerImpl2.setUsername("defaultUsername");

    // Act
    customerImpl2.setTaxExemptionCode(null);

    // Assert that nothing has changed
    assertFalse(customerImpl2.isTaxExempt);
  }

  /**
   * Test {@link CustomerImpl#isTaxExempt()}.
   * <p>
   * Method under test: {@link CustomerImpl#isTaxExempt()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerImpl.isTaxExempt()"})
  public void testIsTaxExempt() {
    // Arrange, Act and Assert
    assertFalse((new CustomerImpl()).isTaxExempt());
  }

  /**
   * Test new {@link CustomerImpl} (default constructor).
   * <p>
   * Method under test: default or parameterless constructor of {@link CustomerImpl}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerImpl.<init>()"})
  public void testNewCustomerImpl() {
    // Arrange and Act
    CustomerImpl actualCustomerImpl = new CustomerImpl();

    // Assert
    assertEquals("null", actualCustomerImpl.getMainEntityName());
    assertNull(actualCustomerImpl.getPreview());
    assertNull(actualCustomerImpl.getId());
    assertNull(actualCustomerImpl.getChallengeAnswer());
    assertNull(actualCustomerImpl.getEmailAddress());
    assertNull(actualCustomerImpl.getExternalId());
    assertNull(actualCustomerImpl.getFirstName());
    assertNull(actualCustomerImpl.getLastName());
    assertNull(actualCustomerImpl.getPassword());
    assertNull(actualCustomerImpl.getTaxExemptionCode());
    assertNull(actualCustomerImpl.getUnencodedChallengeAnswer());
    assertNull(actualCustomerImpl.getUnencodedPassword());
    assertNull(actualCustomerImpl.getUsername());
    assertNull(actualCustomerImpl.getCustomerLocale());
    assertNull(actualCustomerImpl.getChallengeQuestion());
    assertFalse(actualCustomerImpl.isAnonymous());
    assertFalse(actualCustomerImpl.isCookied());
    assertFalse(actualCustomerImpl.isDeactivated());
    assertFalse(actualCustomerImpl.isLoggedIn());
    assertFalse(actualCustomerImpl.isPasswordChangeRequired());
    assertFalse(actualCustomerImpl.isReceiveEmail());
    assertFalse(actualCustomerImpl.isRegistered());
    assertFalse(actualCustomerImpl.deactivated);
    assertFalse(actualCustomerImpl.isTaxExempt);
    assertFalse(actualCustomerImpl.passwordChangeRequired);
    assertFalse(actualCustomerImpl.receiveEmail);
    assertFalse(actualCustomerImpl.registered);
    assertTrue(actualCustomerImpl.getCustomerAddresses().isEmpty());
    assertTrue(actualCustomerImpl.getCustomerPayments().isEmpty());
    assertTrue(actualCustomerImpl.getCustomerPhones().isEmpty());
    assertTrue(actualCustomerImpl.getCustomerAttributes().isEmpty());
    assertTrue(actualCustomerImpl.getTransientProperties().isEmpty());
  }
}
