/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.test.junit;

import org.broadleafcommerce.test.config.AdminSpringBootTestConfiguration;
import org.broadleafcommerce.test.helper.AdminApplication;
import org.broadleafcommerce.test.helper.AdminTestHelper;
import org.broadleafcommerce.test.helper.TestAdminRequestFilter;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockServletContext;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.RequestContextFilter;

import javax.servlet.Filter;

/**
 * TODO detailed docs
 *
 * @author Jeff Fischer
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AdminApplication.class})
@TestPropertySource(locations = "classpath:/config/bc/overrideprops/admin_springboot_testoverrides.properties")
@Import(AdminSpringBootTestConfiguration.class)
public abstract class JUnitSpringBootAdminIntegrationSetup {

    @Autowired
    @Qualifier("blRequestContextFilter")
    private RequestContextFilter requestContextFilter;

    @Autowired
    @Qualifier("openEntityManagerInViewFilter")
    private OpenEntityManagerInViewFilter openEntityManagerInViewFilter;

    @Autowired
    private TestAdminRequestFilter adminRequestFilter;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockServletContext servletContext;

    @Autowired
    @Qualifier("blAdminTestHelper")
    private AdminTestHelper h;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        Filter[] filters = getOrderedFilters(openEntityManagerInViewFilter, requestContextFilter, adminRequestFilter);
        String[] roles = getRequestRoles("PERMISSION_ALL_DEPLOYMENT");
        /*
        Normally, we would use "@AutoConfigureMockMvc" on the test class and AutoWire the MockMVC dependency. However,
        we need more control over the filters included and that route seems to be ignoring the classic Spring Boot
        approach of adding a disabled FilterRegistrationBean for the filter to negate it. Instead, we opt for a more
        explicit construction here.
         */
        mockMvc = MockMvcBuilders.webAppContextSetup(context).addFilters(filters).build();
        /*
        Allow calls to HttpServletRequest#isUserInRole to work
         */
        servletContext.declareRoles(roles);
    }

    public MockMvc getMockMvc() {
        return mockMvc;
    }

    public <T extends AdminTestHelper> T getHelper(Class<T> helperType) {
        return (T) h;
    }

    /**
     * Override to add filters and change ordering
     *
     * @param basicFilters
     * @return
     */
    protected Filter[] getOrderedFilters(Filter... basicFilters) {
        return basicFilters;
    }

    /**
     * Override to add or change roles
     *
     * @param basicRoles
     * @return
     */
    protected String[] getRequestRoles(String... basicRoles) {
        return basicRoles;
    }

}
