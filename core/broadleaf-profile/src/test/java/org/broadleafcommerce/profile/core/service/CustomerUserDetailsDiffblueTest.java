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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class CustomerUserDetailsDiffblueTest {
  /**
   * Test {@link CustomerUserDetails#CustomerUserDetails(Long, String, String, Collection)}.
   * <ul>
   *   <li>Then return Authorities size is one.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerUserDetails#CustomerUserDetails(Long, String, String, Collection)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerUserDetails.<init>(Long, String, String, Collection)"})
  public void testNewCustomerUserDetails_thenReturnAuthoritiesSizeIsOne() {
    // Arrange
    ArrayList<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("Role"));
    authorities.add(new SimpleGrantedAuthority("Role"));

    // Act
    CustomerUserDetails actualCustomerUserDetails = new CustomerUserDetails(1L, "testUser", "password123", authorities);

    // Assert
    Collection<GrantedAuthority> authorities2 = actualCustomerUserDetails.getAuthorities();
    assertEquals(1, authorities2.size());
    assertTrue(authorities2 instanceof Set);
    assertEquals("password123", actualCustomerUserDetails.getPassword());
    assertEquals("testUser", actualCustomerUserDetails.getUsername());
    assertEquals(1L, actualCustomerUserDetails.getId().longValue());
    assertTrue(actualCustomerUserDetails.isAccountNonExpired());
    assertTrue(actualCustomerUserDetails.isAccountNonLocked());
    assertTrue(actualCustomerUserDetails.isCredentialsNonExpired());
    assertTrue(actualCustomerUserDetails.isEnabled());
  }

  /**
   * Test {@link CustomerUserDetails#CustomerUserDetails(Long, String, String, boolean, boolean, boolean, boolean, Collection)}.
   * <ul>
   *   <li>Then return Authorities size is one.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerUserDetails#CustomerUserDetails(Long, String, String, boolean, boolean, boolean, boolean, Collection)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({
      "void CustomerUserDetails.<init>(Long, String, String, boolean, boolean, boolean, boolean, Collection)"})
  public void testNewCustomerUserDetails_thenReturnAuthoritiesSizeIsOne2() {
    // Arrange
    ArrayList<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("Role"));
    authorities.add(new SimpleGrantedAuthority("Role"));

    // Act
    CustomerUserDetails actualCustomerUserDetails = new CustomerUserDetails(1L, "testUser", "password123", true, true,
        true, true, authorities);

    // Assert
    Collection<GrantedAuthority> authorities2 = actualCustomerUserDetails.getAuthorities();
    assertEquals(1, authorities2.size());
    assertTrue(authorities2 instanceof Set);
    assertEquals("password123", actualCustomerUserDetails.getPassword());
    assertEquals("testUser", actualCustomerUserDetails.getUsername());
    assertEquals(1L, actualCustomerUserDetails.getId().longValue());
    assertTrue(actualCustomerUserDetails.isAccountNonExpired());
    assertTrue(actualCustomerUserDetails.isAccountNonLocked());
    assertTrue(actualCustomerUserDetails.isCredentialsNonExpired());
    assertTrue(actualCustomerUserDetails.isEnabled());
  }

  /**
   * Test {@link CustomerUserDetails#CustomerUserDetails(Long, String, String, Collection)}.
   * <ul>
   *   <li>When {@link ArrayList#ArrayList()}.</li>
   *   <li>Then return Authorities Empty.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerUserDetails#CustomerUserDetails(Long, String, String, Collection)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"void CustomerUserDetails.<init>(Long, String, String, Collection)"})
  public void testNewCustomerUserDetails_whenArrayList_thenReturnAuthoritiesEmpty() {
    // Arrange and Act
    CustomerUserDetails actualCustomerUserDetails = new CustomerUserDetails(1L, "testUser", "password123",
        new ArrayList<>());

    // Assert
    Collection<GrantedAuthority> authorities = actualCustomerUserDetails.getAuthorities();
    assertTrue(authorities instanceof Set);
    assertEquals("password123", actualCustomerUserDetails.getPassword());
    assertEquals("testUser", actualCustomerUserDetails.getUsername());
    assertEquals(1L, actualCustomerUserDetails.getId().longValue());
    assertTrue(authorities.isEmpty());
    assertTrue(actualCustomerUserDetails.isAccountNonExpired());
    assertTrue(actualCustomerUserDetails.isAccountNonLocked());
    assertTrue(actualCustomerUserDetails.isCredentialsNonExpired());
    assertTrue(actualCustomerUserDetails.isEnabled());
  }

  /**
   * Test {@link CustomerUserDetails#CustomerUserDetails(Long, String, String, boolean, boolean, boolean, boolean, Collection)}.
   * <ul>
   *   <li>When {@link ArrayList#ArrayList()}.</li>
   *   <li>Then return Authorities Empty.</li>
   * </ul>
   * <p>
   * Method under test: {@link CustomerUserDetails#CustomerUserDetails(Long, String, String, boolean, boolean, boolean, boolean, Collection)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({
      "void CustomerUserDetails.<init>(Long, String, String, boolean, boolean, boolean, boolean, Collection)"})
  public void testNewCustomerUserDetails_whenArrayList_thenReturnAuthoritiesEmpty2() {
    // Arrange and Act
    CustomerUserDetails actualCustomerUserDetails = new CustomerUserDetails(1L, "testUser", "password123", true, true,
        true, true, new ArrayList<>());

    // Assert
    Collection<GrantedAuthority> authorities = actualCustomerUserDetails.getAuthorities();
    assertTrue(authorities instanceof Set);
    assertEquals("password123", actualCustomerUserDetails.getPassword());
    assertEquals("testUser", actualCustomerUserDetails.getUsername());
    assertEquals(1L, actualCustomerUserDetails.getId().longValue());
    assertTrue(authorities.isEmpty());
    assertTrue(actualCustomerUserDetails.isAccountNonExpired());
    assertTrue(actualCustomerUserDetails.isAccountNonLocked());
    assertTrue(actualCustomerUserDetails.isCredentialsNonExpired());
    assertTrue(actualCustomerUserDetails.isEnabled());
  }

  /**
   * Test {@link CustomerUserDetails#withId(Long)}.
   * <p>
   * Method under test: {@link CustomerUserDetails#withId(Long)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"CustomerUserDetails CustomerUserDetails.withId(Long)"})
  public void testWithId() {
    // Arrange
    CustomerUserDetails customerUserDetails = new CustomerUserDetails(1L, "testUser", "password123", new ArrayList<>());

    // Act and Assert
    assertSame(customerUserDetails, customerUserDetails.withId(1L));
  }

  /**
   * Test getters and setters.
   * <p>
   * Methods under test:
   * <ul>
   *   <li>{@link CustomerUserDetails#setId(Long)}
   *   <li>{@link CustomerUserDetails#getId()}
   * </ul>
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"Long CustomerUserDetails.getId()", "void CustomerUserDetails.setId(Long)"})
  public void testGettersAndSetters() {
    // Arrange
    CustomerUserDetails customerUserDetails = new CustomerUserDetails(1L, "testUser", "password123", new ArrayList<>());

    // Act
    customerUserDetails.setId(1L);

    // Assert
    assertEquals(1L, customerUserDetails.getId().longValue());
  }
}
