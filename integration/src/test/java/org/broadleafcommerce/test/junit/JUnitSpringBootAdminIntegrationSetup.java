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
 * This is a convenient base class for launching a spring boot test against the admin application. The primary testing technologies
 * employed here are JUnit and Spring's MockMVC. This integration test focuses on testing the controller API. It has the following beneficial attributes:
 * <ul>
 *     <li>The test is much less brittle than a traditional presentation-tier functional test, but with nearly the same full lifecycle coverage benefits.</li>
 *     <li>Development iteration and test execution times are less.</li>
 *     <li>This test will generally survive a admin pipeline refactor without much change. It has good longevity characteristics.</li>
 *     <li>Raw database confirmation of operations is trivial.</li>
 * </ul>
 * Notably, the spring boot instance launched does not include a embedded container, but
 * does include a full HSQL database. The database is fully network ready and can be queried by an external sql tool while the test
 * is running.
 * </p>
 * Tests should extend {@link JUnitSpringBootAdminIntegrationSetup} and employ standard MockMVC test calls to simulate
 * admin controller interactions. The {@link AdminTestHelper} is included for convenience API around starting an
 * Entity-Manager-In-View session for retrieving and verifying records from the database. This is useful when directly
 * confirming data results from admin MockMVC calls.
 * </p>
 * {@link JUnitSpringBootAdminIntegrationSetup} already introduces the most likely filters and user request roles required
 * for admin interaction. Extending tests can customize by overriding {@link #getOrderedFilters(Filter...)} and
 * {@link #getRequestRoles(String...)}. Note, this is different than the usage of @WithMockUser, which should be annotated
 * on your test methods and specify the admin user name to associate with the test.
 * </p>
 * If you have existing, non-spring-boot tests already running, you'll want to separate the execution of your spring boot
 * tests. Otherwise, you'll likely run into problems during test app context initialization in regard to some classloader
 * nuances and static variables. This can be achieved by creating multiple &lt;execution&gt; elements in your
 * maven-surefire-plugin configuration in your pom.xml. Review the documentation for the maven plugin on how to include and
 * exclude tests into separate executions. http://maven.apache.org/surefire/maven-surefire-plugin/examples/inclusion-exclusion.html
 * </p>
 * Several libraries are required to be on the test classpath in order to get admin spring boot testing. You can include the
 * following in your pom.xml &lt;dependencies&gt; section.
 * <pre>
 *     &lt;dependency&gt;
 *        &lt;groupId&gt;org.broadleafcommerce&lt;/groupId&gt;
 *        &lt;artifactId&gt;integration&lt;/artifactId&gt;
 *        &lt;version&gt;${blc.version}&lt;/version&gt;
 *        &lt;classifier&gt;tests&lt;/classifier&gt;
 *        &lt;scope&gt;test&lt;/scope&gt;
 *    &lt;/dependency&gt;
 *    &lt;dependency&gt;
 *        &lt;groupId&gt;org.broadleafcommerce&lt;/groupId&gt;
 *        &lt;artifactId&gt;integration&lt;/artifactId&gt;
 *        &lt;version&gt;${blc.version}&lt;/version&gt;
 *        &lt;scope&gt;test&lt;/scope&gt;
 *    &lt;/dependency&gt;
 *   &lt;dependency&gt;
 *        &lt;groupId&gt;org.broadleafcommerce&lt;/groupId&gt;
 *        &lt;artifactId&gt;broadleaf-thymeleaf3-presentation&lt;/artifactId&gt;
 *        &lt;version&gt;${broadleaf-presentation.version}&lt;/version&gt;
 *        &lt;scope&gt;test&lt;/scope&gt;
 *    &lt;/dependency&gt;
 *    &lt;dependency&gt;
 *        &lt;groupId&gt;org.springframework.security&lt;/groupId&gt;
 *        &lt;artifactId&gt;spring-security-test&lt;/artifactId&gt;
 *        &lt;version&gt;${spring.security.version}&lt;/version&gt;
 *        &lt;scope&gt;test&lt;/scope&gt;
 *    &lt;/dependency&gt;
 *    &lt;dependency&gt;
 *        &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
 *        &lt;artifactId&gt;spring-boot-starter-test&lt;/artifactId&gt;
 *        &lt;version&gt;${spring.boot.test.version}&lt;/version&gt;
 *        &lt;scope&gt;test&lt;/scope&gt;
 *    &lt;/dependency&gt;
 *    &lt;dependency&gt;
 *        &lt;groupId&gt;com.broadleafcommerce&lt;/groupId&gt;
 *        &lt;artifactId&gt;broadleaf-boot-starter-hsql-database&lt;/artifactId&gt;
 *        &lt;version&gt;${database.starter.version}&lt;/version&gt;
 *        &lt;scope&gt;test&lt;/scope&gt;
 *    &lt;/dependency&gt;
 * </pre>
 * </p>
 * An example test will look something like this:
 * <pre>
 *   public class MyIT extends JUnitSpringBootAdminIntegrationSetup {
 *
 *        protected AdminTestHelper h;
 *
 *        {@literal @}Override
 *        public void setUp() throws Exception {
 *            super.setUp();
 *            h = getHelper(AdminTestHelper.class);
 *        }
 *
 *        {@literal @}Test
 *        {@literal @}WithMockUser(username = "admin")
 *        public void testProductAdd() throws Throwable {
 *            MvcResult result = getMockMvc().perform(post("/admin/product/new/enterprise-add").contextPath("/admin").accept("text/html")
 *                    .param("entityType", ProductImpl.class.getName())
 *                    .header("host", "global.local.com"))
 *                    .andReturn();
 *            String redirectUrl = result.getResponse().getRedirectedUrl();
 *            Assert.assertTrue(redirectUrl.startsWith("/admin/product"));
 *            Long id = Long.parseLong(redirectUrl.substring(redirectUrl.lastIndexOf("/") + 1, redirectUrl.indexOf("?")));
 *            result = getMockMvc().perform(post("/admin/product/" + id).contextPath("/admin").accept("text/html")
 *                    .param("isPostAdd", "true")
 *                    .param("entityType", ProductImpl.class.getName())
 *                    .param("ceilingEntityClassname", Product.class.getName())
 *                    .param("id", String.valueOf(id))
 *                    .param("sectionCrumbs", ProductImpl.class.getName() + "--" + id)
 *                    .param("fields['defaultSku.name'].value", "Vendor Product")
 *                    .param("fields['defaultSku.retailPrice'].value", "12")
 *                    .param("fields['defaultCategory'].value", "3002")
 *                    .param("fields['url'].value", "/hot-sauces/thing")
 *                    .header("host", "global.local.com"))
 *                    .andReturn();
 *            Assert.assertEquals(result.getResponse().getStatus(), HttpServletResponse.SC_FOUND);
 *        }
 *   }
 * </pre>
 *</p>
 * Finally, a warning. When the spring-boot-starter-test dependency is on the classpath, MockitoPostProcessor is automatically
 * included, which causes class scanning while looking for {@literal @}Configuration classes. This ends up loading some
 * domain classes in the classloader at startup that interferes with normal Load Time Weaving operations in Broadleaf. As a
 * result, {@link AdminSpringBootTestConfiguration} automatically configures a no-op implementation of MockitoPostProcessor
 * to avoid the scanning, at the cost of removing Mockito support in the Spring Boot test.
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
     * Override to add or change roles. These roles are important for anywhere in an admin flow where the
     * HttpServletRequest#isUserInRole API is used.
     *
     * @param basicRoles
     * @return
     */
    protected String[] getRequestRoles(String... basicRoles) {
        return basicRoles;
    }

}
