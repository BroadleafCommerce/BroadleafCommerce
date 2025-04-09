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
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.util.ArrayList;
import java.util.List;
import org.broadleafcommerce.profile.core.dao.ChallengeQuestionDao;
import org.broadleafcommerce.profile.core.domain.ChallengeQuestion;
import org.broadleafcommerce.profile.core.domain.ChallengeQuestionImpl;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ChallengeQuestionServiceImplDiffblueTest {
  @Mock
  private ChallengeQuestionDao challengeQuestionDao;

  @InjectMocks
  private ChallengeQuestionServiceImpl challengeQuestionServiceImpl;

  /**
   * Test {@link ChallengeQuestionServiceImpl#readChallengeQuestions()}.
   * <p>
   * Method under test: {@link ChallengeQuestionServiceImpl#readChallengeQuestions()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"List ChallengeQuestionServiceImpl.readChallengeQuestions()"})
  public void testReadChallengeQuestions() {
    // Arrange
    when(challengeQuestionDao.readChallengeQuestions()).thenReturn(new ArrayList<>());

    // Act
    List<ChallengeQuestion> actualReadChallengeQuestionsResult = challengeQuestionServiceImpl.readChallengeQuestions();

    // Assert
    verify(challengeQuestionDao).readChallengeQuestions();
    assertTrue(actualReadChallengeQuestionsResult.isEmpty());
  }

  /**
   * Test {@link ChallengeQuestionServiceImpl#readChallengeQuestionById(long)}.
   * <p>
   * Method under test: {@link ChallengeQuestionServiceImpl#readChallengeQuestionById(long)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"ChallengeQuestion ChallengeQuestionServiceImpl.readChallengeQuestionById(long)"})
  public void testReadChallengeQuestionById() {
    // Arrange
    ChallengeQuestionImpl challengeQuestionImpl = new ChallengeQuestionImpl();
    when(challengeQuestionDao.readChallengeQuestionById(anyLong())).thenReturn(challengeQuestionImpl);

    // Act
    ChallengeQuestion actualReadChallengeQuestionByIdResult = challengeQuestionServiceImpl
        .readChallengeQuestionById(1L);

    // Assert
    verify(challengeQuestionDao).readChallengeQuestionById(eq(1L));
    assertSame(challengeQuestionImpl, actualReadChallengeQuestionByIdResult);
  }
}
