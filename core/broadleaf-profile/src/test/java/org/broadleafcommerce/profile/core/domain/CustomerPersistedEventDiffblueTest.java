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

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class CustomerPersistedEventDiffblueTest {
  /**
   * Test {@link CustomerPersistedEvent#CustomerPersistedEvent(Customer)}.
   * <p>
   * Method under test: {@link CustomerPersistedEvent#CustomerPersistedEvent(Customer)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerPersistedEvent.<init>(Customer)"})
  public void testNewCustomerPersistedEvent() {
    // Arrange
    CustomerImpl customer = new CustomerImpl();

    // Act
    CustomerPersistedEvent actualCustomerPersistedEvent = new CustomerPersistedEvent(customer);

    // Assert
    Customer customer2 = actualCustomerPersistedEvent.getCustomer();
    assertTrue(customer2 instanceof CustomerImpl);
    assertSame(customer, actualCustomerPersistedEvent.getSource());
    assertSame(customer, customer2);
  }

  /**
   * Test {@link CustomerPersistedEvent#getCustomer()}.
   * <p>
   * Method under test: {@link CustomerPersistedEvent#getCustomer()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Customer CustomerPersistedEvent.getCustomer()"})
  public void testGetCustomer() {
    // Arrange
    CustomerImpl customer = new CustomerImpl();

    // Act and Assert
    assertSame(customer, (new CustomerPersistedEvent(customer)).getCustomer());
  }
}
