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
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class RoleImplDiffblueTest {
  /**
   * Test {@link RoleImpl#equals(Object)}, and {@link RoleImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link RoleImpl#equals(Object)}
   *   <li>{@link RoleImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean RoleImpl.equals(Object)", "int RoleImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual() {
    // Arrange
    RoleImpl roleImpl = new RoleImpl();
    roleImpl.setId(1L);
    roleImpl.setRoleName("Role Name");

    RoleImpl roleImpl2 = new RoleImpl();
    roleImpl2.setId(1L);
    roleImpl2.setRoleName("Role Name");

    // Act and Assert
    assertEquals(roleImpl, roleImpl2);
    int expectedHashCodeResult = roleImpl.hashCode();
    assertEquals(expectedHashCodeResult, roleImpl2.hashCode());
  }

  /**
   * Test {@link RoleImpl#equals(Object)}, and {@link RoleImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link RoleImpl#equals(Object)}
   *   <li>{@link RoleImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean RoleImpl.equals(Object)", "int RoleImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual2() {
    // Arrange
    RoleImpl roleImpl = new RoleImpl();
    roleImpl.setId(null);
    roleImpl.setRoleName("Role Name");

    RoleImpl roleImpl2 = new RoleImpl();
    roleImpl2.setId(1L);
    roleImpl2.setRoleName("Role Name");

    // Act and Assert
    assertEquals(roleImpl, roleImpl2);
    int expectedHashCodeResult = roleImpl.hashCode();
    assertEquals(expectedHashCodeResult, roleImpl2.hashCode());
  }

  /**
   * Test {@link RoleImpl#equals(Object)}, and {@link RoleImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link RoleImpl#equals(Object)}
   *   <li>{@link RoleImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean RoleImpl.equals(Object)", "int RoleImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual3() {
    // Arrange
    RoleImpl roleImpl = new RoleImpl();
    roleImpl.setId(1L);
    roleImpl.setRoleName("Role Name");

    RoleImpl roleImpl2 = new RoleImpl();
    roleImpl2.setId(null);
    roleImpl2.setRoleName("Role Name");

    // Act and Assert
    assertEquals(roleImpl, roleImpl2);
    int expectedHashCodeResult = roleImpl.hashCode();
    assertEquals(expectedHashCodeResult, roleImpl2.hashCode());
  }

  /**
   * Test {@link RoleImpl#equals(Object)}, and {@link RoleImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link RoleImpl#equals(Object)}
   *   <li>{@link RoleImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean RoleImpl.equals(Object)", "int RoleImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual4() {
    // Arrange
    RoleImpl roleImpl = new RoleImpl();
    roleImpl.setId(null);
    roleImpl.setRoleName(null);

    RoleImpl roleImpl2 = new RoleImpl();
    roleImpl2.setId(1L);
    roleImpl2.setRoleName(null);

    // Act and Assert
    assertEquals(roleImpl, roleImpl2);
    int expectedHashCodeResult = roleImpl.hashCode();
    assertEquals(expectedHashCodeResult, roleImpl2.hashCode());
  }

  /**
   * Test {@link RoleImpl#equals(Object)}, and {@link RoleImpl#hashCode()}.
   * <ul>
   *   <li>When other is same.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link RoleImpl#equals(Object)}
   *   <li>{@link RoleImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean RoleImpl.equals(Object)", "int RoleImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual() {
    // Arrange
    RoleImpl roleImpl = new RoleImpl();
    roleImpl.setId(1L);
    roleImpl.setRoleName("Role Name");

    // Act and Assert
    assertEquals(roleImpl, roleImpl);
    int expectedHashCodeResult = roleImpl.hashCode();
    assertEquals(expectedHashCodeResult, roleImpl.hashCode());
  }

  /**
   * Test {@link RoleImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link RoleImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean RoleImpl.equals(Object)", "int RoleImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual() {
    // Arrange
    RoleImpl roleImpl = new RoleImpl();
    roleImpl.setId(2L);
    roleImpl.setRoleName("Role Name");

    RoleImpl roleImpl2 = new RoleImpl();
    roleImpl2.setId(1L);
    roleImpl2.setRoleName("Role Name");

    // Act and Assert
    assertNotEquals(roleImpl, roleImpl2);
  }

  /**
   * Test {@link RoleImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link RoleImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean RoleImpl.equals(Object)", "int RoleImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual2() {
    // Arrange
    RoleImpl roleImpl = new RoleImpl();
    roleImpl.setId(null);
    roleImpl.setRoleName(null);

    RoleImpl roleImpl2 = new RoleImpl();
    roleImpl2.setId(1L);
    roleImpl2.setRoleName("Role Name");

    // Act and Assert
    assertNotEquals(roleImpl, roleImpl2);
  }

  /**
   * Test {@link RoleImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link RoleImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean RoleImpl.equals(Object)", "int RoleImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual3() {
    // Arrange
    RoleImpl roleImpl = new RoleImpl();
    roleImpl.setId(null);
    roleImpl.setRoleName("42");

    RoleImpl roleImpl2 = new RoleImpl();
    roleImpl2.setId(1L);
    roleImpl2.setRoleName("Role Name");

    // Act and Assert
    assertNotEquals(roleImpl, roleImpl2);
  }

  /**
   * Test {@link RoleImpl#equals(Object)}.
   * <ul>
   *   <li>When other is {@code null}.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link RoleImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean RoleImpl.equals(Object)", "int RoleImpl.hashCode()"})
  public void testEquals_whenOtherIsNull_thenReturnNotEqual() {
    // Arrange
    RoleImpl roleImpl = new RoleImpl();
    roleImpl.setId(1L);
    roleImpl.setRoleName("Role Name");

    // Act and Assert
    assertNotEquals(roleImpl, null);
  }

  /**
   * Test {@link RoleImpl#equals(Object)}.
   * <ul>
   *   <li>When other is wrong type.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link RoleImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean RoleImpl.equals(Object)", "int RoleImpl.hashCode()"})
  public void testEquals_whenOtherIsWrongType_thenReturnNotEqual() {
    // Arrange
    RoleImpl roleImpl = new RoleImpl();
    roleImpl.setId(1L);
    roleImpl.setRoleName("Role Name");

    // Act and Assert
    assertNotEquals(roleImpl, "Different type to RoleImpl");
  }

  /**
   * Test getters and setters.
   * <p>
   * Methods under test:
   * <ul>
   *   <li>default or parameterless constructor of {@link RoleImpl}
   *   <li>{@link RoleImpl#setId(Long)}
   *   <li>{@link RoleImpl#setRoleName(String)}
   *   <li>{@link RoleImpl#getId()}
   *   <li>{@link RoleImpl#getRoleName()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void RoleImpl.<init>()", "Long RoleImpl.getId()", "String RoleImpl.getRoleName()",
      "void RoleImpl.setId(Long)", "void RoleImpl.setRoleName(String)"})
  public void testGettersAndSetters() {
    // Arrange and Act
    RoleImpl actualRoleImpl = new RoleImpl();
    actualRoleImpl.setId(1L);
    actualRoleImpl.setRoleName("Role Name");
    Long actualId = actualRoleImpl.getId();

    // Assert
    assertEquals("Role Name", actualRoleImpl.getRoleName());
    assertEquals(1L, actualId.longValue());
  }
}
