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
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopierExtensionManager;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.service.GenericEntityService;
import org.broadleafcommerce.common.site.domain.CatalogImpl;
import org.broadleafcommerce.common.site.domain.SiteImpl;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = {"/bl-profile-applicationContext-entity.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class CustomerAttributeImplDiffblueTest {
  @Autowired
  private CustomerAttributeImpl customerAttributeImpl;

  /**
   * Test {@link CustomerAttributeImpl#getValue()}.
   * <p>
   * Method under test: {@link CustomerAttributeImpl#getValue()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"String CustomerAttributeImpl.getValue()"})
  public void testGetValue() {
    // Arrange, Act and Assert
    assertNull((new CustomerAttributeImpl()).getValue());
  }

  /**
   * Test {@link CustomerAttributeImpl#getName()}.
   * <p>
   * Method under test: {@link CustomerAttributeImpl#getName()}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"String CustomerAttributeImpl.getName()"})
  public void testGetName() {
    // Arrange, Act and Assert
    assertNull((new CustomerAttributeImpl()).getName());
  }

  /**
   * Test {@link CustomerAttributeImpl#equals(Object)}, and {@link CustomerAttributeImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerAttributeImpl#equals(Object)}
   *   <li>{@link CustomerAttributeImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerAttributeImpl.equals(Object)", "int CustomerAttributeImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual() {
    // Arrange
    CustomerAttributeImpl customerAttributeImpl = new CustomerAttributeImpl();
    customerAttributeImpl.setCustomer(new CustomerImpl());
    customerAttributeImpl.setId(1L);
    customerAttributeImpl.setName("defaultName");
    customerAttributeImpl.setValue("defaultValue");

    CustomerAttributeImpl customerAttributeImpl2 = new CustomerAttributeImpl();
    customerAttributeImpl2.setCustomer(new CustomerImpl());
    customerAttributeImpl2.setId(1L);
    customerAttributeImpl2.setName("defaultName");
    customerAttributeImpl2.setValue("defaultValue");

    // Act and Assert
    assertEquals(customerAttributeImpl, customerAttributeImpl2);
    int expectedHashCodeResult = customerAttributeImpl.hashCode();
    assertEquals(expectedHashCodeResult, customerAttributeImpl2.hashCode());
  }

  /**
   * Test {@link CustomerAttributeImpl#equals(Object)}, and {@link CustomerAttributeImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerAttributeImpl#equals(Object)}
   *   <li>{@link CustomerAttributeImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerAttributeImpl.equals(Object)", "int CustomerAttributeImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual2() {
    // Arrange
    CustomerAttributeImpl customerAttributeImpl = new CustomerAttributeImpl();
    customerAttributeImpl.setCustomer(new CustomerImpl());
    customerAttributeImpl.setId(null);
    customerAttributeImpl.setName("defaultName");
    customerAttributeImpl.setValue("defaultValue");

    CustomerAttributeImpl customerAttributeImpl2 = new CustomerAttributeImpl();
    customerAttributeImpl2.setCustomer(new CustomerImpl());
    customerAttributeImpl2.setId(1L);
    customerAttributeImpl2.setName("defaultName");
    customerAttributeImpl2.setValue("defaultValue");

    // Act and Assert
    assertEquals(customerAttributeImpl, customerAttributeImpl2);
    int expectedHashCodeResult = customerAttributeImpl.hashCode();
    assertEquals(expectedHashCodeResult, customerAttributeImpl2.hashCode());
  }

  /**
   * Test {@link CustomerAttributeImpl#equals(Object)}, and {@link CustomerAttributeImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerAttributeImpl#equals(Object)}
   *   <li>{@link CustomerAttributeImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerAttributeImpl.equals(Object)", "int CustomerAttributeImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual3() {
    // Arrange
    CustomerAttributeImpl customerAttributeImpl = new CustomerAttributeImpl();
    customerAttributeImpl.setCustomer(new CustomerImpl());
    customerAttributeImpl.setId(1L);
    customerAttributeImpl.setName("defaultName");
    customerAttributeImpl.setValue("defaultValue");

    CustomerAttributeImpl customerAttributeImpl2 = new CustomerAttributeImpl();
    customerAttributeImpl2.setCustomer(new CustomerImpl());
    customerAttributeImpl2.setId(null);
    customerAttributeImpl2.setName("defaultName");
    customerAttributeImpl2.setValue("defaultValue");

    // Act and Assert
    assertEquals(customerAttributeImpl, customerAttributeImpl2);
    int expectedHashCodeResult = customerAttributeImpl.hashCode();
    assertEquals(expectedHashCodeResult, customerAttributeImpl2.hashCode());
  }

  /**
   * Test {@link CustomerAttributeImpl#equals(Object)}, and {@link CustomerAttributeImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerAttributeImpl#equals(Object)}
   *   <li>{@link CustomerAttributeImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerAttributeImpl.equals(Object)", "int CustomerAttributeImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual4() {
    // Arrange
    CustomerAttributeImpl customerAttributeImpl = new CustomerAttributeImpl();
    customerAttributeImpl.setCustomer(null);
    customerAttributeImpl.setId(null);
    customerAttributeImpl.setName("defaultName");
    customerAttributeImpl.setValue("defaultValue");

    CustomerAttributeImpl customerAttributeImpl2 = new CustomerAttributeImpl();
    customerAttributeImpl2.setCustomer(null);
    customerAttributeImpl2.setId(1L);
    customerAttributeImpl2.setName("defaultName");
    customerAttributeImpl2.setValue("defaultValue");

    // Act and Assert
    assertEquals(customerAttributeImpl, customerAttributeImpl2);
    int expectedHashCodeResult = customerAttributeImpl.hashCode();
    assertEquals(expectedHashCodeResult, customerAttributeImpl2.hashCode());
  }

  /**
   * Test {@link CustomerAttributeImpl#equals(Object)}, and {@link CustomerAttributeImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerAttributeImpl#equals(Object)}
   *   <li>{@link CustomerAttributeImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerAttributeImpl.equals(Object)", "int CustomerAttributeImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual5() {
    // Arrange
    CustomerAttributeImpl customerAttributeImpl = new CustomerAttributeImpl();
    customerAttributeImpl.setCustomer(new CustomerImpl());
    customerAttributeImpl.setId(null);
    customerAttributeImpl.setName(null);
    customerAttributeImpl.setValue("defaultValue");

    CustomerAttributeImpl customerAttributeImpl2 = new CustomerAttributeImpl();
    customerAttributeImpl2.setCustomer(new CustomerImpl());
    customerAttributeImpl2.setId(1L);
    customerAttributeImpl2.setName(null);
    customerAttributeImpl2.setValue("defaultValue");

    // Act and Assert
    assertEquals(customerAttributeImpl, customerAttributeImpl2);
    int expectedHashCodeResult = customerAttributeImpl.hashCode();
    assertEquals(expectedHashCodeResult, customerAttributeImpl2.hashCode());
  }

  /**
   * Test {@link CustomerAttributeImpl#equals(Object)}, and {@link CustomerAttributeImpl#hashCode()}.
   * <ul>
   *   <li>When other is equal.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerAttributeImpl#equals(Object)}
   *   <li>{@link CustomerAttributeImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerAttributeImpl.equals(Object)", "int CustomerAttributeImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsEqual_thenReturnEqual6() {
    // Arrange
    CustomerAttributeImpl customerAttributeImpl = new CustomerAttributeImpl();
    customerAttributeImpl.setCustomer(new CustomerImpl());
    customerAttributeImpl.setId(null);
    customerAttributeImpl.setName("defaultName");
    customerAttributeImpl.setValue(null);

    CustomerAttributeImpl customerAttributeImpl2 = new CustomerAttributeImpl();
    customerAttributeImpl2.setCustomer(new CustomerImpl());
    customerAttributeImpl2.setId(1L);
    customerAttributeImpl2.setName("defaultName");
    customerAttributeImpl2.setValue(null);

    // Act and Assert
    assertEquals(customerAttributeImpl, customerAttributeImpl2);
    int expectedHashCodeResult = customerAttributeImpl.hashCode();
    assertEquals(expectedHashCodeResult, customerAttributeImpl2.hashCode());
  }

  /**
   * Test {@link CustomerAttributeImpl#equals(Object)}, and {@link CustomerAttributeImpl#hashCode()}.
   * <ul>
   *   <li>When other is same.</li>
   *   <li>Then return equal.</li>
   * </ul>
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerAttributeImpl#equals(Object)}
   *   <li>{@link CustomerAttributeImpl#hashCode()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerAttributeImpl.equals(Object)", "int CustomerAttributeImpl.hashCode()"})
  public void testEqualsAndHashCode_whenOtherIsSame_thenReturnEqual() {
    // Arrange
    CustomerAttributeImpl customerAttributeImpl = new CustomerAttributeImpl();
    customerAttributeImpl.setCustomer(new CustomerImpl());
    customerAttributeImpl.setId(1L);
    customerAttributeImpl.setName("defaultName");
    customerAttributeImpl.setValue("defaultValue");

    // Act and Assert
    assertEquals(customerAttributeImpl, customerAttributeImpl);
    int expectedHashCodeResult = customerAttributeImpl.hashCode();
    assertEquals(expectedHashCodeResult, customerAttributeImpl.hashCode());
  }

  /**
   * Test {@link CustomerAttributeImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerAttributeImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerAttributeImpl.equals(Object)", "int CustomerAttributeImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual() {
    // Arrange
    CustomerAttributeImpl customerAttributeImpl = new CustomerAttributeImpl();
    customerAttributeImpl.setCustomer(new CustomerImpl());
    customerAttributeImpl.setId(2L);
    customerAttributeImpl.setName("defaultName");
    customerAttributeImpl.setValue("defaultValue");

    CustomerAttributeImpl customerAttributeImpl2 = new CustomerAttributeImpl();
    customerAttributeImpl2.setCustomer(new CustomerImpl());
    customerAttributeImpl2.setId(1L);
    customerAttributeImpl2.setName("defaultName");
    customerAttributeImpl2.setValue("defaultValue");

    // Act and Assert
    assertNotEquals(customerAttributeImpl, customerAttributeImpl2);
  }

  /**
   * Test {@link CustomerAttributeImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerAttributeImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerAttributeImpl.equals(Object)", "int CustomerAttributeImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual2() {
    // Arrange
    CustomerAttributeImpl customerAttributeImpl = new CustomerAttributeImpl();
    customerAttributeImpl.setCustomer(null);
    customerAttributeImpl.setId(null);
    customerAttributeImpl.setName("defaultName");
    customerAttributeImpl.setValue("defaultValue");

    CustomerAttributeImpl customerAttributeImpl2 = new CustomerAttributeImpl();
    customerAttributeImpl2.setCustomer(new CustomerImpl());
    customerAttributeImpl2.setId(1L);
    customerAttributeImpl2.setName("defaultName");
    customerAttributeImpl2.setValue("defaultValue");

    // Act and Assert
    assertNotEquals(customerAttributeImpl, customerAttributeImpl2);
  }

  /**
   * Test {@link CustomerAttributeImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerAttributeImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerAttributeImpl.equals(Object)", "int CustomerAttributeImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual3() {
    // Arrange
    CustomerAttributeImpl customerAttributeImpl = new CustomerAttributeImpl();
    customerAttributeImpl.setCustomer(mock(CustomerImpl.class));
    customerAttributeImpl.setId(null);
    customerAttributeImpl.setName("defaultName");
    customerAttributeImpl.setValue("defaultValue");

    CustomerAttributeImpl customerAttributeImpl2 = new CustomerAttributeImpl();
    customerAttributeImpl2.setCustomer(new CustomerImpl());
    customerAttributeImpl2.setId(1L);
    customerAttributeImpl2.setName("defaultName");
    customerAttributeImpl2.setValue("defaultValue");

    // Act and Assert
    assertNotEquals(customerAttributeImpl, customerAttributeImpl2);
  }

  /**
   * Test {@link CustomerAttributeImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerAttributeImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerAttributeImpl.equals(Object)", "int CustomerAttributeImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual4() {
    // Arrange
    CustomerAttributeImpl customerAttributeImpl = new CustomerAttributeImpl();
    customerAttributeImpl.setCustomer(new CustomerImpl());
    customerAttributeImpl.setId(null);
    customerAttributeImpl.setName("defaultValue");
    customerAttributeImpl.setValue("defaultValue");

    CustomerAttributeImpl customerAttributeImpl2 = new CustomerAttributeImpl();
    customerAttributeImpl2.setCustomer(new CustomerImpl());
    customerAttributeImpl2.setId(1L);
    customerAttributeImpl2.setName("defaultName");
    customerAttributeImpl2.setValue("defaultValue");

    // Act and Assert
    assertNotEquals(customerAttributeImpl, customerAttributeImpl2);
  }

  /**
   * Test {@link CustomerAttributeImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerAttributeImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerAttributeImpl.equals(Object)", "int CustomerAttributeImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual5() {
    // Arrange
    CustomerAttributeImpl customerAttributeImpl = new CustomerAttributeImpl();
    customerAttributeImpl.setCustomer(new CustomerImpl());
    customerAttributeImpl.setId(null);
    customerAttributeImpl.setName(null);
    customerAttributeImpl.setValue("defaultValue");

    CustomerAttributeImpl customerAttributeImpl2 = new CustomerAttributeImpl();
    customerAttributeImpl2.setCustomer(new CustomerImpl());
    customerAttributeImpl2.setId(1L);
    customerAttributeImpl2.setName("defaultName");
    customerAttributeImpl2.setValue("defaultValue");

    // Act and Assert
    assertNotEquals(customerAttributeImpl, customerAttributeImpl2);
  }

  /**
   * Test {@link CustomerAttributeImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerAttributeImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerAttributeImpl.equals(Object)", "int CustomerAttributeImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual6() {
    // Arrange
    CustomerAttributeImpl customerAttributeImpl = new CustomerAttributeImpl();
    customerAttributeImpl.setCustomer(new CustomerImpl());
    customerAttributeImpl.setId(null);
    customerAttributeImpl.setName("defaultName");
    customerAttributeImpl.setValue("42");

    CustomerAttributeImpl customerAttributeImpl2 = new CustomerAttributeImpl();
    customerAttributeImpl2.setCustomer(new CustomerImpl());
    customerAttributeImpl2.setId(1L);
    customerAttributeImpl2.setName("defaultName");
    customerAttributeImpl2.setValue("defaultValue");

    // Act and Assert
    assertNotEquals(customerAttributeImpl, customerAttributeImpl2);
  }

  /**
   * Test {@link CustomerAttributeImpl#equals(Object)}.
   * <ul>
   *   <li>When other is different.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerAttributeImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerAttributeImpl.equals(Object)", "int CustomerAttributeImpl.hashCode()"})
  public void testEquals_whenOtherIsDifferent_thenReturnNotEqual7() {
    // Arrange
    CustomerAttributeImpl customerAttributeImpl = new CustomerAttributeImpl();
    customerAttributeImpl.setCustomer(new CustomerImpl());
    customerAttributeImpl.setId(null);
    customerAttributeImpl.setName("defaultName");
    customerAttributeImpl.setValue(null);

    CustomerAttributeImpl customerAttributeImpl2 = new CustomerAttributeImpl();
    customerAttributeImpl2.setCustomer(new CustomerImpl());
    customerAttributeImpl2.setId(1L);
    customerAttributeImpl2.setName("defaultName");
    customerAttributeImpl2.setValue("defaultValue");

    // Act and Assert
    assertNotEquals(customerAttributeImpl, customerAttributeImpl2);
  }

  /**
   * Test {@link CustomerAttributeImpl#equals(Object)}.
   * <ul>
   *   <li>When other is {@code null}.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerAttributeImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerAttributeImpl.equals(Object)", "int CustomerAttributeImpl.hashCode()"})
  public void testEquals_whenOtherIsNull_thenReturnNotEqual() {
    // Arrange
    CustomerAttributeImpl customerAttributeImpl = new CustomerAttributeImpl();
    customerAttributeImpl.setCustomer(new CustomerImpl());
    customerAttributeImpl.setId(1L);
    customerAttributeImpl.setName("defaultName");
    customerAttributeImpl.setValue("defaultValue");

    // Act and Assert
    assertNotEquals(customerAttributeImpl, null);
  }

  /**
   * Test {@link CustomerAttributeImpl#equals(Object)}.
   * <ul>
   *   <li>When other is wrong type.</li>
   *   <li>Then return not equal.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerAttributeImpl#equals(Object)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"boolean CustomerAttributeImpl.equals(Object)", "int CustomerAttributeImpl.hashCode()"})
  public void testEquals_whenOtherIsWrongType_thenReturnNotEqual() {
    // Arrange
    CustomerAttributeImpl customerAttributeImpl = new CustomerAttributeImpl();
    customerAttributeImpl.setCustomer(new CustomerImpl());
    customerAttributeImpl.setId(1L);
    customerAttributeImpl.setName("defaultName");
    customerAttributeImpl.setValue("defaultValue");

    // Act and Assert
    assertNotEquals(customerAttributeImpl, "Different type to CustomerAttributeImpl");
  }

  /**
   * Test {@link CustomerAttributeImpl#createOrRetrieveCopyInstance(MultiTenantCopyContext)}.
   * <p>
   * Method under test: {@link CustomerAttributeImpl#createOrRetrieveCopyInstance(MultiTenantCopyContext)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CreateResponse CustomerAttributeImpl.createOrRetrieveCopyInstance(MultiTenantCopyContext)"})
  public void testCreateOrRetrieveCopyInstance() throws CloneNotSupportedException {
    // Arrange
    CustomerAttributeImpl customerAttributeImpl2 = new CustomerAttributeImpl();
    MultiTenantCopyContext context = mock(MultiTenantCopyContext.class);
    CreateResponse<Object> createResponse = new CreateResponse<>("Clone", true);

    when(context.createOrRetrieveCopyInstance(Mockito.<Object>any())).thenReturn(createResponse);

    // Act
    CreateResponse<CustomerAttribute> actualCreateOrRetrieveCopyInstanceResult = customerAttributeImpl2
        .createOrRetrieveCopyInstance(context);

    // Assert
    verify(context).createOrRetrieveCopyInstance(isA(Object.class));
    assertSame(createResponse, actualCreateOrRetrieveCopyInstanceResult);
  }

  /**
   * Test {@link CustomerAttributeImpl#createOrRetrieveCopyInstance(MultiTenantCopyContext)}.
   * <ul>
   *   <li>Then Clone return {@link CustomerAttributeImpl}.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerAttributeImpl#createOrRetrieveCopyInstance(MultiTenantCopyContext)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CreateResponse CustomerAttributeImpl.createOrRetrieveCopyInstance(MultiTenantCopyContext)"})
  public void testCreateOrRetrieveCopyInstance_thenCloneReturnCustomerAttributeImpl()
      throws CloneNotSupportedException {
    // Arrange
    CustomerAttributeImpl customerAttributeImpl2 = new CustomerAttributeImpl();
    GenericEntityService genericEntityService = mock(GenericEntityService.class);
    when(genericEntityService.getIdentifier(Mockito.<Object>any())).thenReturn(null);
    Class<Object> forNameResult = Object.class;
    Mockito.<Class<?>>when(genericEntityService.getCeilingImplClass(Mockito.<String>any())).thenReturn(forNameResult);
    CatalogImpl fromCatalog = new CatalogImpl();
    CatalogImpl toCatalog = new CatalogImpl();
    SiteImpl fromSite = new SiteImpl();
    SiteImpl toSite = new SiteImpl();

    // Act
    CreateResponse<CustomerAttribute> actualCreateOrRetrieveCopyInstanceResult = customerAttributeImpl2
        .createOrRetrieveCopyInstance(new MultiTenantCopyContext(fromCatalog, toCatalog, fromSite, toSite,
            genericEntityService, new MultiTenantCopierExtensionManager()));

    // Assert
    verify(genericEntityService)
        .getCeilingImplClass(eq("org.broadleafcommerce.profile.core.domain.CustomerAttributeImpl"));
    verify(genericEntityService).getIdentifier(isA(Object.class));
    CustomerAttribute clone = actualCreateOrRetrieveCopyInstanceResult.getClone();
    assertTrue(clone instanceof CustomerAttributeImpl);
    assertFalse(actualCreateOrRetrieveCopyInstanceResult.isAlreadyPopulated());
    assertEquals(customerAttributeImpl2, clone);
  }

  /**
   * Test getters and setters.
   * <p>
   * Methods under test:
   * <ul>
   *   <li>default or parameterless constructor of {@link CustomerAttributeImpl}
   *   <li>{@link CustomerAttributeImpl#setCustomer(Customer)}
   *   <li>{@link CustomerAttributeImpl#setId(Long)}
   *   <li>{@link CustomerAttributeImpl#setName(String)}
   *   <li>{@link CustomerAttributeImpl#setValue(String)}
   *   <li>{@link CustomerAttributeImpl#getCustomer()}
   *   <li>{@link CustomerAttributeImpl#getId()}
   *   <li>{@link CustomerAttributeImpl#toString()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerAttributeImpl.<init>()", "Customer CustomerAttributeImpl.getCustomer()",
      "Long CustomerAttributeImpl.getId()", "void CustomerAttributeImpl.setCustomer(Customer)",
      "void CustomerAttributeImpl.setId(Long)", "void CustomerAttributeImpl.setName(String)",
      "void CustomerAttributeImpl.setValue(String)", "String CustomerAttributeImpl.toString()"})
  public void testGettersAndSetters() {
    // Arrange and Act
    CustomerAttributeImpl actualCustomerAttributeImpl = new CustomerAttributeImpl();
    CustomerImpl customer = new CustomerImpl();
    actualCustomerAttributeImpl.setCustomer(customer);
    actualCustomerAttributeImpl.setId(1L);
    actualCustomerAttributeImpl.setName("defaultName");
    actualCustomerAttributeImpl.setValue("defaultValue");
    Customer actualCustomer = actualCustomerAttributeImpl.getCustomer();
    Long actualId = actualCustomerAttributeImpl.getId();

    // Assert
    assertEquals("defaultValue", actualCustomerAttributeImpl.toString());
    assertEquals(1L, actualId.longValue());
    assertSame(customer, actualCustomer);
  }
}
