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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.broadleafcommerce.profile.core.dao.PhoneDao;
import org.broadleafcommerce.profile.core.domain.Phone;
import org.broadleafcommerce.profile.core.domain.PhoneImpl;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PhoneServiceImplDiffblueTest {
  @Mock
  private PhoneDao phoneDao;

  @InjectMocks
  private PhoneServiceImpl phoneServiceImpl;

  /**
   * Test {@link PhoneServiceImpl#savePhone(Phone)}.
   * <p>
   * Method under test: {@link PhoneServiceImpl#savePhone(Phone)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Phone PhoneServiceImpl.savePhone(Phone)"})
  public void testSavePhone() {
    // Arrange
    PhoneImpl phoneImpl = new PhoneImpl();
    when(phoneDao.save(Mockito.<Phone>any())).thenReturn(phoneImpl);

    // Act
    Phone actualSavePhoneResult = phoneServiceImpl.savePhone(new PhoneImpl());

    // Assert
    verify(phoneDao).save(isA(Phone.class));
    assertSame(phoneImpl, actualSavePhoneResult);
  }

  /**
   * Test {@link PhoneServiceImpl#readPhoneById(Long)}.
   * <p>
   * Method under test: {@link PhoneServiceImpl#readPhoneById(Long)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Phone PhoneServiceImpl.readPhoneById(Long)"})
  public void testReadPhoneById() {
    // Arrange
    PhoneImpl phoneImpl = new PhoneImpl();
    when(phoneDao.readPhoneById(Mockito.<Long>any())).thenReturn(phoneImpl);

    // Act
    Phone actualReadPhoneByIdResult = phoneServiceImpl.readPhoneById(1L);

    // Assert
    verify(phoneDao).readPhoneById(eq(1L));
    assertSame(phoneImpl, actualReadPhoneByIdResult);
  }

  /**
   * Test {@link PhoneServiceImpl#create()}.
   * <p>
   * Method under test: {@link PhoneServiceImpl#create()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Phone PhoneServiceImpl.create()"})
  public void testCreate() {
    // Arrange
    PhoneImpl phoneImpl = new PhoneImpl();
    when(phoneDao.create()).thenReturn(phoneImpl);

    // Act
    Phone actualCreateResult = phoneServiceImpl.create();

    // Assert
    verify(phoneDao).create();
    assertSame(phoneImpl, actualCreateResult);
  }

  /**
   * Test {@link PhoneServiceImpl#copyPhone(Phone, Phone)} with {@code dest}, {@code orig}.
   * <ul>
   *   <li>Given {@link PhoneDao} {@link PhoneDao#create()} return {@link PhoneImpl} (default constructor).</li>
   *   <li>Then calls {@link PhoneDao#create()}.</li>
   * </ul>
   * <p>
   * Method under test: {@link PhoneServiceImpl#copyPhone(Phone, Phone)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Phone PhoneServiceImpl.copyPhone(Phone, Phone)"})
  public void testCopyPhoneWithDestOrig_givenPhoneDaoCreateReturnPhoneImpl_thenCallsCreate() {
    // Arrange
    when(phoneDao.create()).thenReturn(new PhoneImpl());

    // Act
    Phone actualCopyPhoneResult = phoneServiceImpl.copyPhone(null, null);

    // Assert
    verify(phoneDao).create();
    assertNull(actualCopyPhoneResult);
  }

  /**
   * Test {@link PhoneServiceImpl#copyPhone(Phone, Phone)} with {@code dest}, {@code orig}.
   * <ul>
   *   <li>Given {@link PhoneDao}.</li>
   *   <li>When {@link PhoneImpl} (default constructor).</li>
   *   <li>Then return {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link PhoneServiceImpl#copyPhone(Phone, Phone)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Phone PhoneServiceImpl.copyPhone(Phone, Phone)"})
  public void testCopyPhoneWithDestOrig_givenPhoneDao_whenPhoneImpl_thenReturnNull() {
    // Arrange, Act and Assert
    assertNull(phoneServiceImpl.copyPhone(new PhoneImpl(), null));
  }

  /**
   * Test {@link PhoneServiceImpl#copyPhone(Phone, Phone)} with {@code dest}, {@code orig}.
   * <ul>
   *   <li>Given {@link PhoneDao}.</li>
   *   <li>When {@link PhoneImpl} (default constructor).</li>
   *   <li>Then return {@link PhoneImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link PhoneServiceImpl#copyPhone(Phone, Phone)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Phone PhoneServiceImpl.copyPhone(Phone, Phone)"})
  public void testCopyPhoneWithDestOrig_givenPhoneDao_whenPhoneImpl_thenReturnPhoneImpl() {
    // Arrange
    PhoneImpl dest = new PhoneImpl();

    // Act and Assert
    assertSame(dest, phoneServiceImpl.copyPhone(dest, new PhoneImpl()));
  }

  /**
   * Test {@link PhoneServiceImpl#copyPhone(Phone)} with {@code orig}.
   * <ul>
   *   <li>Given {@link PhoneDao} {@link PhoneDao#create()} return {@link PhoneImpl} (default constructor).</li>
   *   <li>Then return {@link PhoneImpl} (default constructor).</li>
   * </ul>
   * <p>
   * Method under test: {@link PhoneServiceImpl#copyPhone(Phone)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Phone PhoneServiceImpl.copyPhone(Phone)"})
  public void testCopyPhoneWithOrig_givenPhoneDaoCreateReturnPhoneImpl_thenReturnPhoneImpl() {
    // Arrange
    PhoneImpl phoneImpl = new PhoneImpl();
    when(phoneDao.create()).thenReturn(phoneImpl);

    // Act
    Phone actualCopyPhoneResult = phoneServiceImpl.copyPhone(new PhoneImpl());

    // Assert
    verify(phoneDao).create();
    assertSame(phoneImpl, actualCopyPhoneResult);
  }

  /**
   * Test {@link PhoneServiceImpl#copyPhone(Phone)} with {@code orig}.
   * <ul>
   *   <li>Given {@link PhoneDao} {@link PhoneDao#create()} return {@link PhoneImpl} (default constructor).</li>
   *   <li>When {@code null}.</li>
   *   <li>Then return {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link PhoneServiceImpl#copyPhone(Phone)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Phone PhoneServiceImpl.copyPhone(Phone)"})
  public void testCopyPhoneWithOrig_givenPhoneDaoCreateReturnPhoneImpl_whenNull_thenReturnNull() {
    // Arrange
    when(phoneDao.create()).thenReturn(new PhoneImpl());

    // Act
    Phone actualCopyPhoneResult = phoneServiceImpl.copyPhone(null);

    // Assert
    verify(phoneDao).create();
    assertNull(actualCopyPhoneResult);
  }
}
