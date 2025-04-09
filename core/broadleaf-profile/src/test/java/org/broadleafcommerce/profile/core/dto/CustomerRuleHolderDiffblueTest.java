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
package org.broadleafcommerce.profile.core.dto;

import static org.junit.Assert.assertEquals;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class CustomerRuleHolderDiffblueTest {
  /**
   * Test getters and setters.
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerRuleHolder#CustomerRuleHolder()}
   *   <li>{@link CustomerRuleHolder#setCustomerRule(String)}
   *   <li>{@link CustomerRuleHolder#getCustomerRule()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerRuleHolder.<init>()", "void CustomerRuleHolder.<init>(String)",
      "String CustomerRuleHolder.getCustomerRule()", "void CustomerRuleHolder.setCustomerRule(String)"})
  public void testGettersAndSetters() {
    // Arrange and Act
    CustomerRuleHolder actualCustomerRuleHolder = new CustomerRuleHolder();
    actualCustomerRuleHolder.setCustomerRule("defaultRule");

    // Assert
    assertEquals("defaultRule", actualCustomerRuleHolder.getCustomerRule());
  }

  /**
   * Test getters and setters.
   * <ul>
   *   <li>When {@code Customer Rule}.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerRuleHolder#CustomerRuleHolder(String)}
   *   <li>{@link CustomerRuleHolder#setCustomerRule(String)}
   *   <li>{@link CustomerRuleHolder#getCustomerRule()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerRuleHolder.<init>()", "void CustomerRuleHolder.<init>(String)",
      "String CustomerRuleHolder.getCustomerRule()", "void CustomerRuleHolder.setCustomerRule(String)"})
  public void testGettersAndSetters_whenCustomerRule() {
    // Arrange and Act
    CustomerRuleHolder actualCustomerRuleHolder = new CustomerRuleHolder("Customer Rule");
    actualCustomerRuleHolder.setCustomerRule("defaultRule");

    // Assert
    assertEquals("defaultRule", actualCustomerRuleHolder.getCustomerRule());
  }
}
