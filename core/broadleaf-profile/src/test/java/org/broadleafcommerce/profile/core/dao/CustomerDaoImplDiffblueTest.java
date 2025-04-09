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
import static org.mockito.ArgumentMatchers.eq;
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
import org.broadleafcommerce.common.audit.Auditable;
import org.broadleafcommerce.common.locale.domain.LocaleImpl;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.profile.core.domain.ChallengeQuestionImpl;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CustomerDaoImplDiffblueTest {
  @InjectMocks
  private CustomerDaoImpl customerDaoImpl;

  @Mock
  private EntityConfiguration entityConfiguration;

  /**
   * Test {@link CustomerDaoImpl#readCustomersByIds(List)}.
   * <ul>
   *   <li>When {@link ArrayList#ArrayList()}.</li>
   *   <li>Then return {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerDaoImpl#readCustomersByIds(List)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"List CustomerDaoImpl.readCustomersByIds(List)"})
  public void testReadCustomersByIds_whenArrayList_thenReturnNull() {
    // Arrange, Act and Assert
    assertNull(customerDaoImpl.readCustomersByIds(new ArrayList<>()));
  }

  /**
   * Test {@link CustomerDaoImpl#readCustomersByIds(List)}.
   * <ul>
   *   <li>When {@code null}.</li>
   *   <li>Then return {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerDaoImpl#readCustomersByIds(List)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"List CustomerDaoImpl.readCustomersByIds(List)"})
  public void testReadCustomersByIds_whenNull_thenReturnNull() {
    // Arrange, Act and Assert
    assertNull(customerDaoImpl.readCustomersByIds(null));
  }

  /**
   * Test {@link CustomerDaoImpl#create()}.
   * <p>
   * Method under test: {@link CustomerDaoImpl#create()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerDaoImpl.create()"})
  public void testCreate() {
    // Arrange
    Auditable auditable = new Auditable();
    auditable.setCreatedBy(1L);
    auditable.setDateCreated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setDateUpdated(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));
    auditable.setUpdatedBy(1L);

    CustomerImpl customerImpl = new CustomerImpl();
    customerImpl.setAuditable(auditable);
    customerImpl.setChallengeAnswer("Challenge Answer");
    customerImpl.setChallengeQuestion(new ChallengeQuestionImpl());
    customerImpl.setCustomerAddresses(new ArrayList<>());
    customerImpl.setCustomerAttributes(new HashMap<>());
    customerImpl.setCustomerLocale(new LocaleImpl());
    customerImpl.setCustomerPayments(new ArrayList<>());
    customerImpl.setCustomerPhones(new ArrayList<>());
    customerImpl.setDeactivated(true);
    customerImpl.setEmailAddress("42 Main St");
    customerImpl.setExternalId("42");
    customerImpl.setFirstName("Jane");
    customerImpl.setId(1L);
    customerImpl.setLastName("Doe");
    customerImpl.setPassword("iloveyou");
    customerImpl.setPasswordChangeRequired(true);
    customerImpl.setReceiveEmail(true);
    customerImpl.setRegistered(true);
    customerImpl.setUnencodedChallengeAnswer("secret");
    customerImpl.setUnencodedPassword("secret");
    customerImpl.setUsername("janedoe");
    when(entityConfiguration.createEntityInstance(Mockito.<String>any())).thenReturn(customerImpl);

    // Act
    Customer actualCreateResult = customerDaoImpl.create();

    // Assert
    verify(entityConfiguration).createEntityInstance(eq("org.broadleafcommerce.profile.core.domain.Customer"));
    assertSame(customerImpl, actualCreateResult);
  }
}
