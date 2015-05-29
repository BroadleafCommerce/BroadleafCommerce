/*
 * #%L
 * BroadleafCommerce Custom Field
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.broadleafcommerce.test;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.extensibility.context.StandardConfigLocations;
import org.broadleafcommerce.common.web.extensibility.MergeXmlWebApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.support.AbstractContextLoader;
import org.springframework.test.context.web.AbstractGenericWebContextLoader;
import org.springframework.test.context.web.GenericGroovyXmlWebContextLoader;
import org.springframework.test.context.web.GenericXmlWebContextLoader;
import org.springframework.test.context.web.WebMergedContextConfiguration;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

/**
 * <p>
 * This class was created due to AbstractGenericWebContextLoader utilizing the qualifier "final" for its
 * loadContext(MergedContextConfiguration) method which we needed to override and provide our Broadleaf created
 * MergeXmlWebApplicationContext object in place of the GenericWebApplicationContext it used. Since we are using
 * Groovy/Spock, the other methods included are to support Groovy test classes. As such, are included some methods
 * from the GenericGroovyXmlWebContextLoader and GenericXmlWebContextLoader classes, refactored to compensate for
 * the levels of inheritance between them.
 * <p>
 * This class loader should be used with the @ContextConfiguration annotation to be placed in the 'loader'
 * parameter for all Broadleaf integration tests which use the Spock testing framework.
 * 
 * @author austinrooke
 *
 */
public class BroadleafGenericGroovyXmlWebContextLoader extends AbstractContextLoader {

    /*
     * Some notes on usage of this context loader.
     * 
     * The set up for the spock/groovy test is as follows. You will need to include the dependencies:
     * spock-spring
     * spring-test
     * javax-servlet
     * integration
     * 
     * Then you will need to also set the following notations on the test class itself:
     * @TransactionConfiguration(transactionManager = "blTransactionManager")
     * @ContextConfiguration(locations = [Include application context's that are required for your
     *  integration test but you MUST include the following application context as the last
     *  entry into this set, "classpath:/bl-applicationContext-test.xml"]
     * @WebAppConfiguration
     * 
     * With these annotations, you are now set up to test any features and classes.
     * 
     * In addition, with the usage of spring's testframework and spock/groovy, we can also test
     * RESTful services directly utilizing spring's mockMVC api. Please see its documentation
     * if you would like to test RESTful services.
     */

    /**
     * {@code BroadleafGenericGroovyXmlWebContextLoader} supports the XML merging that
     * Broadleaf's framework features, but this is handled during the .refresh() method
     * in the loadContext method so this method was only pulled from the original class
     * {@link GenericGroovyXmlWebContextLoader} in case of the Spring-Test framework requiring
     * this method to have a particular behavior.
     */
    protected String[] getResourceSuffixes() {
        return new String[] { "-context.xml", "Context.groovy" };
    }

    /**
     * {@code BroadleafGenericGroovyXmlWebContextLoader} supports the XML merging that
     * Broadleaf's framework features, but this is handled during the .refresh() method
     * in the loadContext method so this method was only pulled from the original class
     * {@link GenericGroovyXmlWebContextLoader} in case of the Spring-Test framework requiring
     * this method to have a particular behavior.
     */
    @Override
    protected String getResourceSuffix() {
        throw new UnsupportedOperationException(
                "BroadleafGenericGroovyXmlWebContextLoader does not support the getResourceSuffix() method");
    }

    /**
     * {@code BroadleafGenericGroovyXmlWebContextLoader} supports the XML merging that
     * Broadleaf's framework features, but this is handled during the .refresh() method
     * in the loadContext method so this method was only pulled from the original class
     * {@link GenericXmlWebContextLoader} in case of the Spring-Test framework requiring
     * this method to have a particular behavior.
     *
     * @see AbstractGenericWebContextLoader#validateMergedContextConfiguration
     */
    protected void validateMergedContextConfiguration(WebMergedContextConfiguration webMergedConfig) {
        if (webMergedConfig.hasClasses()) {
            String msg = String.format(
                    "Test class [%s] has been configured with @ContextConfiguration's 'classes' attribute %s, "
                            + "but %s does not support annotated classes.", webMergedConfig.getTestClass().getName(),
                    ObjectUtils.nullSafeToString(webMergedConfig.getClasses()), getClass().getSimpleName());
            throw new IllegalStateException(msg);
        }
    }

    /**
     * {@code BroadleafGenericGroovyXmlWebContextLoader} should be used as a
     * {@link org.springframework.test.context.SmartContextLoader SmartContextLoader},
     * not as a legacy {@link org.springframework.test.context.ContextLoader ContextLoader}.
     * Consequently, this method is not supported.
     * 
     * This method was pulled from {@code AbstractGenericWebContextLoader}.
     *
     * @see org.springframework.test.context.ContextLoader#loadContext(java.lang.String[])
     * @throws UnsupportedOperationException
     */
    @Override
    public final ApplicationContext loadContext(String... locations) throws Exception {
        throw new UnsupportedOperationException(
                "BroadleafGenericGroovyXmlWebContextLoader does not support the loadContext(String... locations) method");
    }

    /**
     * Load a {@link MergeXmlWebApplicationContext} from the supplied {@link MergedContextConfiguration}
     * 
     * <p>Implementation details:
     * 
     * <ul>
     * <li>Calls {@link #validateMergedContextConfiguration(WebMergedContextConfiguration)}
     * to allow subclasses to validate the supplied configuration before proceeding.</li>
     * <li>Creates a {@link MergeXmlWebApplicationContext} instance.</li>
     * <li>If the supplied {@link MergeXmlWebApplicationContext} references a
     * {@linkplain MergeXmlWebApplicationContext#getParent() parent configuration},
     * the corresponding {@link MergeXmlWebApplicationContext#getParentApplicationContext()
     * ApplicationContext} will be retrieved and 
     * {@linkplain MergeXmlWebApplicationContext#setParent(ApplicationContext) set as the parent}
     * for the context created by this method.</li>
     * <li>Converts the patch locations into a single string to be set via
     * {@link MergeXmlWebApplicationContext#setPatchLocation(String)}</li>
     * <li>Sets the patch locations via {@link MergeXmlWebApplicationContext#setStandardLocationTypes(String)}
     * to the {@link StandardConfigLocations.TESTCONTEXTTYPE} for integration tests.</li>
     * <li>Delegates to {@link #configureWebResources} to create the {@link MockServletContext} and
     * set it in the {@code MergeXmlWebApplicationContext}.</li>
     * <li>Calls {@link #prepareContext} to allow for customizing the context before bean
     * definitions are loaded.</li>
     * <li>{@link ConfigurableApplicationContext#refresh Refreshes} the context and registers
     * a JVM shutdown hook for it.</li>
     * </ul></p>
     * 
     * Refactored from {@link org.springframework.test.context.web.AbstractGenericWebContextLoader#loadContext(MergedContextConfiguration)}
     * 
     * @return a new merge xml web application context
     * @see org.springframework.test.context.SmartContextLoader#loadContext(MergedContextConfiguration)
     * @see MergeXmlWebApplicationContext
     */
    @Override
    public ConfigurableApplicationContext loadContext(MergedContextConfiguration mergedConfig) throws Exception {

        if (!(mergedConfig instanceof WebMergedContextConfiguration)) {
            throw new IllegalArgumentException(String.format(
                    "Cannot load WebApplicationContext from non-web merged context configuration %s. "
                            + "Consider annotating your test class with @WebAppConfiguration.", mergedConfig));
        }
        WebMergedContextConfiguration webMergedConfig = (WebMergedContextConfiguration) mergedConfig;

        validateMergedContextConfiguration(webMergedConfig);

        MergeXmlWebApplicationContext context = new MergeXmlWebApplicationContext();
        context.setPatchLocation("");

        ApplicationContext parent = mergedConfig.getParentApplicationContext();
        if (parent != null) {
            context.setParent(parent);
            context.setPatchLocation(StringUtils.removeEnd(((MergeXmlWebApplicationContext) parent).getPatchLocation(), "classpath:/bl-applicationContext-test.xml"));
            System.out.println(context.getPatchLocation());
        }
        //Calls unique to Broadleaf Implementation of the Smart Context Loader
        // the ";classpath:/bl-applicationContext-test.xml" is required by all integration tests so we add it here.
        context.setPatchLocation(context.getPatchLocation() + StringUtils.join(mergedConfig.getLocations(), ";") +";classpath:/bl-applicationContext-test.xml");
        context.setStandardLocationTypes(StandardConfigLocations.TESTCONTEXTTYPE);
        
        configureWebResources(context, webMergedConfig);
        prepareContext(context, webMergedConfig);
        context.refresh();
        context.registerShutdownHook();
        return context;
    }

    /**
     * Configures web resources for the supplied web application context (WAC).
     *
     * <h4>Implementation Details</h4>
     *
     * <p>If the supplied WAC has no parent or its parent is not a WAC, the
     * supplied WAC will be configured as the Root WAC (see "<em>Root WAC
     * Configuration</em>" below).
     *
     * <p>Otherwise the context hierarchy of the supplied WAC will be traversed
     * to find the top-most WAC (i.e., the root); and the {@link ServletContext}
     * of the Root WAC will be set as the {@code ServletContext} for the supplied
     * WAC.
     *
     * <h4>Root WAC Configuration</h4>
     *
     * <ul>
     * <li>The resource base path is retrieved from the supplied
     * {@code WebMergedContextConfiguration}.</li>
     * <li>A {@link ResourceLoader} is instantiated for the {@link MockServletContext}:
     * if the resource base path is prefixed with "{@code classpath:/}", a
     * {@link DefaultResourceLoader} will be used; otherwise, a
     * {@link FileSystemResourceLoader} will be used.</li>
     * <li>A {@code MockServletContext} will be created using the resource base
     * path and resource loader.</li>
     * <li>The supplied {@link MergeXmlWebApplicationContext} is then stored in
     * the {@code MockServletContext} under the
     * {@link MergeXmlWebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE} key.</li>
     * <li>Finally, the {@code MockServletContext} is set in the
     * {@code MergeXmlWebApplicationContext}.</li>
     *
     * @param context the merge xml web application context for which to configure the web
     * resources
     * @param webMergedConfig the merged context configuration to use to load the
     * merge xml web application context
     */
    protected void configureWebResources(MergeXmlWebApplicationContext context,
            WebMergedContextConfiguration webMergedConfig) {

        ApplicationContext parent = context.getParent();

        // if the WAC has no parent or the parent is not a WAC, set the WAC as
        // the Root WAC:
        if (parent == null || (!(parent instanceof WebApplicationContext))) {
            String resourceBasePath = webMergedConfig.getResourceBasePath();
            ResourceLoader resourceLoader = resourceBasePath.startsWith(ResourceLoader.CLASSPATH_URL_PREFIX) ? new DefaultResourceLoader()
                    : new FileSystemResourceLoader();

            ServletContext servletContext = new MockServletContext(resourceBasePath, resourceLoader);
            servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
            context.setServletContext(servletContext);
        }
        else {
            ServletContext servletContext = null;

            // find the Root WAC
            while (parent != null) {
                if (parent instanceof WebApplicationContext && !(parent.getParent() instanceof WebApplicationContext)) {
                    servletContext = ((WebApplicationContext) parent).getServletContext();
                    break;
                }
                parent = parent.getParent();
            }
            Assert.state(servletContext != null, "Failed to find Root MergeXmlWebApplicationContext in the context hierarchy");
            context.setServletContext(servletContext);
        }
    }

}
