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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = {"/bl-profile-applicationContext-entity.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ChallengeQuestionImplDiffblueTest {
  @Autowired
  private ChallengeQuestionImpl challengeQuestionImpl;

  /**
   * Test {@link ChallengeQuestionImpl#getQuestion()}.
   * <p>
   * Method under test: {@link ChallengeQuestionImpl#getQuestion()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"String ChallengeQuestionImpl.getQuestion()"})
  public void testGetQuestion() {
    // Arrange, Act and Assert
    assertNull((new ChallengeQuestionImpl()).getQuestion());
  }

  /**
   * Test {@link ChallengeQuestionImpl#equals(Object)}, and {@link ChallengeQuestionImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link ChallengeQuestionImpl#equals(Object)}
   *   <li>{@link ChallengeQuestionImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean ChallengeQuestionImpl.equals(Object)", "int ChallengeQuestionImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual() {
    // Arrange
    ChallengeQuestionImpl challengeQuestionImpl = new ChallengeQuestionImpl();
    challengeQuestionImpl.setId(1L);
    challengeQuestionImpl.setQuestion("What is your favorite color?");

    ChallengeQuestionImpl challengeQuestionImpl2 = new ChallengeQuestionImpl();
    challengeQuestionImpl2.setId(1L);
    challengeQuestionImpl2.setQuestion("What is your favorite color?");

    // Act and Assert
    assertEquals(challengeQuestionImpl, challengeQuestionImpl2);
    int expectedHashCodeResult = challengeQuestionImpl.hashCode();
    assertEquals(expectedHashCodeResult, challengeQuestionImpl2.hashCode());
  }

  /**
   * Test {@link ChallengeQuestionImpl#equals(Object)}, and {@link ChallengeQuestionImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link ChallengeQuestionImpl#equals(Object)}
   *   <li>{@link ChallengeQuestionImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean ChallengeQuestionImpl.equals(Object)", "int ChallengeQuestionImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual2() {
    // Arrange
    ChallengeQuestionImpl challengeQuestionImpl = new ChallengeQuestionImpl();
    challengeQuestionImpl.setId(null);
    challengeQuestionImpl.setQuestion("What is your favorite color?");

    ChallengeQuestionImpl challengeQuestionImpl2 = new ChallengeQuestionImpl();
    challengeQuestionImpl2.setId(1L);
    challengeQuestionImpl2.setQuestion("What is your favorite color?");

    // Act and Assert
    assertEquals(challengeQuestionImpl, challengeQuestionImpl2);
    int expectedHashCodeResult = challengeQuestionImpl.hashCode();
    assertEquals(expectedHashCodeResult, challengeQuestionImpl2.hashCode());
  }

  /**
   * Test {@link ChallengeQuestionImpl#equals(Object)}, and {@link ChallengeQuestionImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link ChallengeQuestionImpl#equals(Object)}
   *   <li>{@link ChallengeQuestionImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean ChallengeQuestionImpl.equals(Object)", "int ChallengeQuestionImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual3() {
    // Arrange
    ChallengeQuestionImpl challengeQuestionImpl = new ChallengeQuestionImpl();
    challengeQuestionImpl.setId(1L);
    challengeQuestionImpl.setQuestion("What is your favorite color?");

    ChallengeQuestionImpl challengeQuestionImpl2 = new ChallengeQuestionImpl();
    challengeQuestionImpl2.setId(null);
    challengeQuestionImpl2.setQuestion("What is your favorite color?");

    // Act and Assert
    assertEquals(challengeQuestionImpl, challengeQuestionImpl2);
    int expectedHashCodeResult = challengeQuestionImpl.hashCode();
    assertEquals(expectedHashCodeResult, challengeQuestionImpl2.hashCode());
  }

  /**
   * Test {@link ChallengeQuestionImpl#equals(Object)}, and {@link ChallengeQuestionImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link ChallengeQuestionImpl#equals(Object)}
   *   <li>{@link ChallengeQuestionImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean ChallengeQuestionImpl.equals(Object)", "int ChallengeQuestionImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual4() {
    // Arrange
    ChallengeQuestionImpl challengeQuestionImpl = new ChallengeQuestionImpl();
    challengeQuestionImpl.setId(null);
    challengeQuestionImpl.setQuestion(null);

    ChallengeQuestionImpl challengeQuestionImpl2 = new ChallengeQuestionImpl();
    challengeQuestionImpl2.setId(1L);
    challengeQuestionImpl2.setQuestion(null);

    // Act and Assert
    assertEquals(challengeQuestionImpl, challengeQuestionImpl2);
    int expectedHashCodeResult = challengeQuestionImpl.hashCode();
    assertEquals(expectedHashCodeResult, challengeQuestionImpl2.hashCode());
  }

  /**
   * Test {@link ChallengeQuestionImpl#equals(Object)}, and {@link ChallengeQuestionImpl#hashCode()}.
   * <ul>
   *   <li>When other is same.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link ChallengeQuestionImpl#equals(Object)}
   *   <li>{@link ChallengeQuestionImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean ChallengeQuestionImpl.equals(Object)", "int ChallengeQuestionImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual() {
    // Arrange
    ChallengeQuestionImpl challengeQuestionImpl = new ChallengeQuestionImpl();
    challengeQuestionImpl.setId(1L);
    challengeQuestionImpl.setQuestion("What is your favorite color?");

    // Act and Assert
    assertEquals(challengeQuestionImpl, challengeQuestionImpl);
    int expectedHashCodeResult = challengeQuestionImpl.hashCode();
    assertEquals(expectedHashCodeResult, challengeQuestionImpl.hashCode());
  }

  /**
   * Test {@link ChallengeQuestionImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link ChallengeQuestionImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean ChallengeQuestionImpl.equals(Object)", "int ChallengeQuestionImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual() {
    // Arrange
    ChallengeQuestionImpl challengeQuestionImpl = new ChallengeQuestionImpl();
    challengeQuestionImpl.setId(2L);
    challengeQuestionImpl.setQuestion("What is your favorite color?");

    ChallengeQuestionImpl challengeQuestionImpl2 = new ChallengeQuestionImpl();
    challengeQuestionImpl2.setId(1L);
    challengeQuestionImpl2.setQuestion("What is your favorite color?");

    // Act and Assert
    assertNotEquals(challengeQuestionImpl, challengeQuestionImpl2);
  }

  /**
   * Test {@link ChallengeQuestionImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link ChallengeQuestionImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean ChallengeQuestionImpl.equals(Object)", "int ChallengeQuestionImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual2() {
    // Arrange
    ChallengeQuestionImpl challengeQuestionImpl = new ChallengeQuestionImpl();
    challengeQuestionImpl.setId(null);
    challengeQuestionImpl.setQuestion("Question");

    ChallengeQuestionImpl challengeQuestionImpl2 = new ChallengeQuestionImpl();
    challengeQuestionImpl2.setId(1L);
    challengeQuestionImpl2.setQuestion("What is your favorite color?");

    // Act and Assert
    assertNotEquals(challengeQuestionImpl, challengeQuestionImpl2);
  }

  /**
   * Test {@link ChallengeQuestionImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link ChallengeQuestionImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean ChallengeQuestionImpl.equals(Object)", "int ChallengeQuestionImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual3() {
    // Arrange
    ChallengeQuestionImpl challengeQuestionImpl = new ChallengeQuestionImpl();
    challengeQuestionImpl.setId(null);
    challengeQuestionImpl.setQuestion(null);

    ChallengeQuestionImpl challengeQuestionImpl2 = new ChallengeQuestionImpl();
    challengeQuestionImpl2.setId(1L);
    challengeQuestionImpl2.setQuestion("What is your favorite color?");

    // Act and Assert
    assertNotEquals(challengeQuestionImpl, challengeQuestionImpl2);
  }

  /**
   * Test {@link ChallengeQuestionImpl#equals(Object)}.
   * <ul>
   *   <li>When other is {@code null}.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link ChallengeQuestionImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean ChallengeQuestionImpl.equals(Object)", "int ChallengeQuestionImpl.hashCode()"})
  public void testEquals_whenOtherIsNull_thenReturnNotEqual() {
    // Arrange
    ChallengeQuestionImpl challengeQuestionImpl = new ChallengeQuestionImpl();
    challengeQuestionImpl.setId(1L);
    challengeQuestionImpl.setQuestion("What is your favorite color?");

    // Act and Assert
    assertNotEquals(challengeQuestionImpl, null);
  }

  /**
   * Test {@link ChallengeQuestionImpl#equals(Object)}.
   * <ul>
   *   <li>When other is wrong type.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link ChallengeQuestionImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean ChallengeQuestionImpl.equals(Object)", "int ChallengeQuestionImpl.hashCode()"})
  public void testEquals_whenOtherIsWrongType_thenReturnNotEqual() {
    // Arrange
    ChallengeQuestionImpl challengeQuestionImpl = new ChallengeQuestionImpl();
    challengeQuestionImpl.setId(1L);
    challengeQuestionImpl.setQuestion("What is your favorite color?");

    // Act and Assert
    assertNotEquals(challengeQuestionImpl, "Different type to ChallengeQuestionImpl");
  }

  /**
   * Test getters and setters.
   * <p>
   * Methods under test:
   * <ul>
   *   <li>default or parameterless constructor of {@link ChallengeQuestionImpl}
   *   <li>{@link ChallengeQuestionImpl#setId(Long)}
   *   <li>{@link ChallengeQuestionImpl#setQuestion(String)}
   *   <li>{@link ChallengeQuestionImpl#getId()}
   *   <li>{@link ChallengeQuestionImpl#toString()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void ChallengeQuestionImpl.<init>()", "Long ChallengeQuestionImpl.getId()",
      "void ChallengeQuestionImpl.setId(Long)", "void ChallengeQuestionImpl.setQuestion(String)",
      "String ChallengeQuestionImpl.toString()"})
  public void testGettersAndSetters() {
    // Arrange and Act
    ChallengeQuestionImpl actualChallengeQuestionImpl = new ChallengeQuestionImpl();
    actualChallengeQuestionImpl.setId(1L);
    actualChallengeQuestionImpl.setQuestion("What is your favorite color?");
    Long actualId = actualChallengeQuestionImpl.getId();

    // Assert
    assertEquals("What is your favorite color?", actualChallengeQuestionImpl.toString());
    assertEquals(1L, actualId.longValue());
  }
}
