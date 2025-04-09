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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.diffblue.cover.annotations.MaintainedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.broadleafcommerce.profile.core.domain.CustomerImpl;
import org.broadleafcommerce.profile.core.domain.CustomerRole;
import org.broadleafcommerce.profile.core.domain.CustomerRoleImpl;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailsServiceImplDiffblueTest {
  @Mock
  private CustomerService customerService;

  @Mock
  private RoleService roleService;

  @InjectMocks
  private UserDetailsServiceImpl userDetailsServiceImpl;

  /**
   * Test {@link UserDetailsServiceImpl#loadUserByUsername(String)}.
   * <p>
   * Method under test: {@link UserDetailsServiceImpl#loadUserByUsername(String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"UserDetails UserDetailsServiceImpl.loadUserByUsername(String)"})
  public void testLoadUserByUsername() throws DataAccessException, UsernameNotFoundException {
    // Arrange
    when(customerService.readCustomerByUsername(Mockito.<String>any(), Mockito.<Boolean>any()))
        .thenReturn(new CustomerImpl());
    when(roleService.findCustomerRolesByCustomerId(Mockito.<Long>any()))
        .thenThrow(new UsernameNotFoundException("ROLE_USER"));

    // Act and Assert
    assertThrows(UsernameNotFoundException.class, () -> userDetailsServiceImpl.loadUserByUsername("janedoe"));
    verify(customerService).readCustomerByUsername(eq("janedoe"), eq(false));
    verify(roleService).findCustomerRolesByCustomerId(isNull());
  }

  /**
   * Test {@link UserDetailsServiceImpl#loadUserByUsername(String)}.
   * <ul>
   *   <li>Given {@link CustomerRoleImpl} {@link CustomerRoleImpl#getRoleName()} return {@code ROLE_USER}.</li>
   * </ul>
   * <p>
   * Method under test: {@link UserDetailsServiceImpl#loadUserByUsername(String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"UserDetails UserDetailsServiceImpl.loadUserByUsername(String)"})
  public void testLoadUserByUsername_givenCustomerRoleImplGetRoleNameReturnRoleUser()
      throws DataAccessException, UsernameNotFoundException {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.isDeactivated()).thenReturn(true);
    when(customerImpl.isPasswordChangeRequired()).thenReturn(true);
    when(customerImpl.getId()).thenReturn(1L);
    when(customerImpl.getPassword()).thenReturn("iloveyou");
    when(customerService.readCustomerByUsername(Mockito.<String>any(), Mockito.<Boolean>any()))
        .thenReturn(customerImpl);
    CustomerRoleImpl customerRoleImpl = mock(CustomerRoleImpl.class);
    when(customerRoleImpl.getRoleName()).thenReturn("ROLE_USER");

    ArrayList<CustomerRole> customerRoleList = new ArrayList<>();
    customerRoleList.add(customerRoleImpl);
    when(roleService.findCustomerRolesByCustomerId(Mockito.<Long>any())).thenReturn(customerRoleList);

    // Act
    UserDetails actualLoadUserByUsernameResult = userDetailsServiceImpl.loadUserByUsername("janedoe");

    // Assert
    verify(customerImpl, atLeast(1)).getId();
    verify(customerImpl).getPassword();
    verify(customerImpl).isDeactivated();
    verify(customerImpl).isPasswordChangeRequired();
    verify(customerRoleImpl, atLeast(1)).getRoleName();
    verify(customerService).readCustomerByUsername(eq("janedoe"), eq(false));
    verify(roleService).findCustomerRolesByCustomerId(eq(1L));
    Collection<? extends GrantedAuthority> authorities = actualLoadUserByUsernameResult.getAuthorities();
    assertEquals(1, authorities.size());
    assertTrue(authorities instanceof Set);
    assertTrue(actualLoadUserByUsernameResult instanceof CustomerUserDetails);
    assertEquals("iloveyou", actualLoadUserByUsernameResult.getPassword());
    assertEquals("janedoe", actualLoadUserByUsernameResult.getUsername());
    assertEquals(1L, ((CustomerUserDetails) actualLoadUserByUsernameResult).getId().longValue());
    assertFalse(actualLoadUserByUsernameResult.isCredentialsNonExpired());
    assertFalse(actualLoadUserByUsernameResult.isEnabled());
    assertTrue(actualLoadUserByUsernameResult.isAccountNonExpired());
    assertTrue(actualLoadUserByUsernameResult.isAccountNonLocked());
  }

  /**
   * Test {@link UserDetailsServiceImpl#loadUserByUsername(String)}.
   * <ul>
   *   <li>Given {@link CustomerService} {@link CustomerService#readCustomerByUsername(String, Boolean)} return {@code null}.</li>
   * </ul>
   * <p>
   * Method under test: {@link UserDetailsServiceImpl#loadUserByUsername(String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"UserDetails UserDetailsServiceImpl.loadUserByUsername(String)"})
  public void testLoadUserByUsername_givenCustomerServiceReadCustomerByUsernameReturnNull()
      throws DataAccessException, UsernameNotFoundException {
    // Arrange
    when(customerService.readCustomerByUsername(Mockito.<String>any(), Mockito.<Boolean>any())).thenReturn(null);

    // Act and Assert
    assertThrows(UsernameNotFoundException.class, () -> userDetailsServiceImpl.loadUserByUsername("janedoe"));
    verify(customerService).readCustomerByUsername(eq("janedoe"), eq(false));
  }

  /**
   * Test {@link UserDetailsServiceImpl#loadUserByUsername(String)}.
   * <ul>
   *   <li>Then return Authorities size is one.</li>
   * </ul>
   * <p>
   * Method under test: {@link UserDetailsServiceImpl#loadUserByUsername(String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"UserDetails UserDetailsServiceImpl.loadUserByUsername(String)"})
  public void testLoadUserByUsername_thenReturnAuthoritiesSizeIsOne()
      throws DataAccessException, UsernameNotFoundException {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.isDeactivated()).thenReturn(true);
    when(customerImpl.isPasswordChangeRequired()).thenReturn(true);
    when(customerImpl.getId()).thenReturn(1L);
    when(customerImpl.getPassword()).thenReturn("iloveyou");
    when(customerService.readCustomerByUsername(Mockito.<String>any(), Mockito.<Boolean>any()))
        .thenReturn(customerImpl);
    when(roleService.findCustomerRolesByCustomerId(Mockito.<Long>any())).thenReturn(new ArrayList<>());

    // Act
    UserDetails actualLoadUserByUsernameResult = userDetailsServiceImpl.loadUserByUsername("janedoe");

    // Assert
    verify(customerImpl, atLeast(1)).getId();
    verify(customerImpl).getPassword();
    verify(customerImpl).isDeactivated();
    verify(customerImpl).isPasswordChangeRequired();
    verify(customerService).readCustomerByUsername(eq("janedoe"), eq(false));
    verify(roleService).findCustomerRolesByCustomerId(eq(1L));
    Collection<? extends GrantedAuthority> authorities = actualLoadUserByUsernameResult.getAuthorities();
    assertEquals(1, authorities.size());
    assertTrue(authorities instanceof Set);
    assertTrue(actualLoadUserByUsernameResult instanceof CustomerUserDetails);
    assertEquals("iloveyou", actualLoadUserByUsernameResult.getPassword());
    assertEquals("janedoe", actualLoadUserByUsernameResult.getUsername());
    assertEquals(1L, ((CustomerUserDetails) actualLoadUserByUsernameResult).getId().longValue());
    assertFalse(actualLoadUserByUsernameResult.isCredentialsNonExpired());
    assertFalse(actualLoadUserByUsernameResult.isEnabled());
    assertTrue(actualLoadUserByUsernameResult.isAccountNonExpired());
    assertTrue(actualLoadUserByUsernameResult.isAccountNonLocked());
  }

  /**
   * Test {@link UserDetailsServiceImpl#loadUserByUsername(String)}.
   * <ul>
   *   <li>Then return Authorities size is two.</li>
   * </ul>
   * <p>
   * Method under test: {@link UserDetailsServiceImpl#loadUserByUsername(String)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"UserDetails UserDetailsServiceImpl.loadUserByUsername(String)"})
  public void testLoadUserByUsername_thenReturnAuthoritiesSizeIsTwo()
      throws DataAccessException, UsernameNotFoundException {
    // Arrange
    CustomerImpl customerImpl = mock(CustomerImpl.class);
    when(customerImpl.isDeactivated()).thenReturn(true);
    when(customerImpl.isPasswordChangeRequired()).thenReturn(true);
    when(customerImpl.getId()).thenReturn(1L);
    when(customerImpl.getPassword()).thenReturn("iloveyou");
    when(customerService.readCustomerByUsername(Mockito.<String>any(), Mockito.<Boolean>any()))
        .thenReturn(customerImpl);
    CustomerRoleImpl customerRoleImpl = mock(CustomerRoleImpl.class);
    when(customerRoleImpl.getRoleName()).thenReturn("Role Name");

    ArrayList<CustomerRole> customerRoleList = new ArrayList<>();
    customerRoleList.add(customerRoleImpl);
    when(roleService.findCustomerRolesByCustomerId(Mockito.<Long>any())).thenReturn(customerRoleList);

    // Act
    UserDetails actualLoadUserByUsernameResult = userDetailsServiceImpl.loadUserByUsername("janedoe");

    // Assert
    verify(customerImpl, atLeast(1)).getId();
    verify(customerImpl).getPassword();
    verify(customerImpl).isDeactivated();
    verify(customerImpl).isPasswordChangeRequired();
    verify(customerRoleImpl, atLeast(1)).getRoleName();
    verify(customerService).readCustomerByUsername(eq("janedoe"), eq(false));
    verify(roleService).findCustomerRolesByCustomerId(eq(1L));
    Collection<? extends GrantedAuthority> authorities = actualLoadUserByUsernameResult.getAuthorities();
    assertEquals(2, authorities.size());
    assertTrue(authorities instanceof Set);
    assertTrue(actualLoadUserByUsernameResult instanceof CustomerUserDetails);
    assertEquals("iloveyou", actualLoadUserByUsernameResult.getPassword());
    assertEquals("janedoe", actualLoadUserByUsernameResult.getUsername());
    assertEquals(1L, ((CustomerUserDetails) actualLoadUserByUsernameResult).getId().longValue());
    assertFalse(actualLoadUserByUsernameResult.isCredentialsNonExpired());
    assertFalse(actualLoadUserByUsernameResult.isEnabled());
    assertTrue(actualLoadUserByUsernameResult.isAccountNonExpired());
    assertTrue(actualLoadUserByUsernameResult.isAccountNonLocked());
  }

  /**
   * Test {@link UserDetailsServiceImpl#createGrantedAuthorities(List)}.
   * <ul>
   *   <li>Given {@link CustomerRoleImpl} {@link CustomerRoleImpl#getRoleName()} return {@code ROLE_USER}.</li>
   * </ul>
   * <p>
   * Method under test: {@link UserDetailsServiceImpl#createGrantedAuthorities(List)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"List UserDetailsServiceImpl.createGrantedAuthorities(List)"})
  public void testCreateGrantedAuthorities_givenCustomerRoleImplGetRoleNameReturnRoleUser() {
    // Arrange
    CustomerRoleImpl customerRoleImpl = mock(CustomerRoleImpl.class);
    when(customerRoleImpl.getRoleName()).thenReturn("ROLE_USER");

    ArrayList<CustomerRole> customerRoles = new ArrayList<>();
    customerRoles.add(customerRoleImpl);

    // Act
    List<GrantedAuthority> actualCreateGrantedAuthoritiesResult = userDetailsServiceImpl
        .createGrantedAuthorities(customerRoles);

    // Assert
    verify(customerRoleImpl, atLeast(1)).getRoleName();
    assertEquals(1, actualCreateGrantedAuthoritiesResult.size());
    GrantedAuthority getResult = actualCreateGrantedAuthoritiesResult.get(0);
    assertTrue(getResult instanceof SimpleGrantedAuthority);
    assertEquals("ROLE_USER", getResult.toString());
    assertEquals("ROLE_USER", getResult.getAuthority());
  }

  /**
   * Test {@link UserDetailsServiceImpl#createGrantedAuthorities(List)}.
   * <ul>
   *   <li>Then return size is two.</li>
   * </ul>
   * <p>
   * Method under test: {@link UserDetailsServiceImpl#createGrantedAuthorities(List)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"List UserDetailsServiceImpl.createGrantedAuthorities(List)"})
  public void testCreateGrantedAuthorities_thenReturnSizeIsTwo() {
    // Arrange
    CustomerRoleImpl customerRoleImpl = mock(CustomerRoleImpl.class);
    when(customerRoleImpl.getRoleName()).thenReturn("Role Name");

    ArrayList<CustomerRole> customerRoles = new ArrayList<>();
    customerRoles.add(customerRoleImpl);

    // Act
    List<GrantedAuthority> actualCreateGrantedAuthoritiesResult = userDetailsServiceImpl
        .createGrantedAuthorities(customerRoles);

    // Assert
    verify(customerRoleImpl, atLeast(1)).getRoleName();
    assertEquals(2, actualCreateGrantedAuthoritiesResult.size());
    GrantedAuthority getResult = actualCreateGrantedAuthoritiesResult.get(0);
    assertTrue(getResult instanceof SimpleGrantedAuthority);
    GrantedAuthority getResult2 = actualCreateGrantedAuthoritiesResult.get(1);
    assertTrue(getResult2 instanceof SimpleGrantedAuthority);
    assertEquals("ROLE_USER", getResult2.toString());
    assertEquals("ROLE_USER", getResult2.getAuthority());
    assertEquals("Role Name", getResult.toString());
    assertEquals("Role Name", getResult.getAuthority());
  }

  /**
   * Test {@link UserDetailsServiceImpl#createGrantedAuthorities(List)}.
   * <ul>
   *   <li>When {@link ArrayList#ArrayList()}.</li>
   *   <li>Then return size is one.</li>
   * </ul>
   * <p>
   * Method under test: {@link UserDetailsServiceImpl#createGrantedAuthorities(List)}
   */
  @Test
  @Category(MaintainedByDiffblue.class)
  @MethodsUnderTest({"List UserDetailsServiceImpl.createGrantedAuthorities(List)"})
  public void testCreateGrantedAuthorities_whenArrayList_thenReturnSizeIsOne() {
    // Arrange and Act
    List<GrantedAuthority> actualCreateGrantedAuthoritiesResult = userDetailsServiceImpl
        .createGrantedAuthorities(new ArrayList<>());

    // Assert
    assertEquals(1, actualCreateGrantedAuthoritiesResult.size());
    GrantedAuthority getResult = actualCreateGrantedAuthoritiesResult.get(0);
    assertTrue(getResult instanceof SimpleGrantedAuthority);
    assertEquals("ROLE_USER", getResult.toString());
    assertEquals("ROLE_USER", getResult.getAuthority());
  }
}
