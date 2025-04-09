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

import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.domain.CustomerAddress;
import org.broadleafcommerce.profile.core.domain.CustomerAddressImpl;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CustomerAddressDaoImplDiffblueTest {
  @InjectMocks
  private CustomerAddressDaoImpl customerAddressDaoImpl;

  @Mock
  private EntityConfiguration entityConfiguration;

  /**
   * Test {@link CustomerAddressDaoImpl#create()}.
   * <p>
   * Method under test: {@link CustomerAddressDaoImpl#create()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerAddress CustomerAddressDaoImpl.create()"})
  public void testCreate() {
    // Arrange
    CustomerAddressImpl customerAddressImpl = new CustomerAddressImpl();
    customerAddressImpl.setAddress(new AddressImpl());
    customerAddressImpl.setAddressName("42 Main St");
    customerAddressImpl.setCustomer(new CustomerImpl());
    customerAddressImpl.setId(1L);
    when(entityConfiguration.createEntityInstance(Mockito.<String>any())).thenReturn(customerAddressImpl);

    // Act
    CustomerAddress actualCreateResult = customerAddressDaoImpl.create();

    // Assert
    verify(entityConfiguration).createEntityInstance(eq("org.broadleafcommerce.profile.core.domain.CustomerAddress"));
    assertSame(customerAddressImpl, actualCreateResult);
  }
}
