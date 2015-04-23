/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.common.web.processor;


import org.broadleafcommerce.common.resource.service.ResourceBundlingService;
import org.broadleafcommerce.common.resource.service.ResourceMinificationService;
import org.broadleafcommerce.common.resource.service.ResourceModificationService;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.element.AbstractElementProcessor;


/**
 * <p>
 * Takes a single resource (like CSS or JS file) and provides services to improve performance and SEO.  To combine 
 * multiple files, use the {@link ResourceBundleProcessor} instead.
 *
 * <p>
 * Uses the following properties ...
 * 
 * <p>
 * Minifies (using the Yahoo YUI minifier) file if the minify.enabled system property is set to true.
 *  
 * <p>
 * Versions the file if the resource.versioning system property is set to true.    
 * and versions the file based on the resource.versioning
 * 
 * <p>
 * The operation of this processor is dependent upon the {@code bundle.enabled} system property. If bundling is disabled
 * via this system property then each file is individually linked in the HTML source
 * 
 * <pre>
 * Supported parameters:
 *     resources:             A comma separated list of relative paths to resources that are static
 *     resource:              A single resource to process     
 *     modifiable-resources:  A comma separated list of relative paths to resources that have replaceable tokens
 *     modifiable-resource:   A single modifiable-resource to process
 *     
 *     bundle-name:           Required if using "bundle" as the bundle-name.   Ignored otherwise.
 *     bundle:                If true (and bundle.enabled system property is also true) will bundle the listed files.
 *     mapping-prefix:        Adds the "prefix" to the beginning of the final resource name.
 *      
 *     async:                 Will add the async attribute to the resulting script tag if this attribute exists
 *     defer:                 Will add the defer attribute to the resulting script tag if this attribute exists
 *     
 *     Aliases (for backward compatibility:
 *     files:                 Alias for modifiable-resources
 *     name:                  Alias for bundle-name
 *     
 * </pre>
 * 
 * Example:
 * <pre>
 * {@code
 *     <blc:resouce file="/js/plugins.js" mapping-prefix="/js/" />
 * }
 * </pre> 
 * 
 * <p>
 * Assuming some modification of this file (e.g. minification, versioning, or modifiable in play), the 
 * HTML will be replaced with the following where 123 represents the version.
 * 
 * <pre>
 * {@code
 *  <script type="text/javascript" src="/js/plugins-123.js" />
 * }
 * </pre>
 * 
 * <p>
 * Where <b>lib-123412.js</b> is the result of minifying and combining all of the referenced <b>files</b> together.
 * 
 * <p>
 * With bundling disabled this turns into:
 * 
 * <pre>
 * {@code
 *  <script type="text/javascript" src="/js/plugins.js" />
 *  <script type="text/javascript" src="/js/jquery.MetaData.js" />
 *  <script type="text/javascript" src="/js/jquery.rating.pack.js.js" />
 *  <script type="text/javascript" src="/js/jquery.dotdotdot-1.5.1.js" />
 * }
 * </pre>
 * 
 * <p>
 * The files are presented without any additional processing done on them. This is beneficial for development when things
 * like Javascript debugging is necessary.
 * 
 * <p>
 * As of 3.1.9-GA, this also supports producing the 'async' and 'defer' attributes for Javascript files. For instance:
 * 
 * <pre>
 * {@code
 * <blc:bundle name="lib.js" 
 *             async="true"
 *             defer="true"
 *             mapping-prefix="/js/"
 *             files="plugins.js,
 *                    libs/jquery.MetaData.js,
 *                    libs/jquery.rating.pack.js,
 *                    libs/jquery.dotdotdot-1.5.1.js" />
 *  }
 * </pre>
 * 
 * <p>
 * If bundling is turned on, the single output file contains the 'async' and 'defer' name-only attributes. When bundling is
 * turned off, then those name-only attributes are applied to each individual file reference.
 * 
 * <p>
 * This processor only supports files that end in <b>.js</b> and <b>.css</b>
 * 
 * @param name (required) the final name of the minified bundle
 * @param <b>mapping-prefix</b> (required) the prefix appended to the final tag output whether that be the list of <b>files</b>
 * or the single minified file
 * @param files (required) a comma-separated list of files that should be bundled together
 * 
 * @author bpolster
 * @see {@link ResourceModificationService}
 * @see {@link ResourceBundlingService}
 * @see {@link ResourceMinificationService}
 */
public class ResourceProcessor extends AbstractElementProcessor {

    public ResourceProcessor() {
        super("resource");
    }

    @Override
    protected ProcessorResult processElement(Arguments arguments, Element element) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getPrecedence() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    //    protected static final Log LOG = LogFactory.getLog(ResourceProcessor.class);
    //    
    //    // @Resource(name = "blResourceBundlingService")
    //    // protected ResourceBundlingService bundlingService;    
    //
    //    private boolean defaultBundleAttribute;
    //    private String tagName;
    //
    //    @javax.annotation.Resource(name = "blResourceModificationService")
    //    protected ResourceModificationService resourceModificationService;
    //    
    //    public ResourceProcessor() {
    //        this("resource", false);
    //    }
    //
    //    public ResourceProcessor(String name, boolean defaultBundleAttribute) {
    //        super(name);
    //        this.tagName = name;
    //        this.defaultBundleAttribute = defaultBundleAttribute;
    //    }
    //    
    //    protected ProcessorResult processElement(Arguments arguments, Element element) {
    //        List<Resource> resources = new ArrayList<Resource>();
    //        addNonModifiableResourceNames(element, resources);
    //        addModifiableResourceNames(element, resources);
    //        
    //        if (shouldBundleResources(element)) {
    //            String bundleName = getRequestedBundleName(element);
    //            if (StringUtils.isEmpty(bundleName)) {
    //                throw new IllegalArgumentException("Bundle name is required when using blc:" + tagName);
    //            }
    //        } else {
    //
    //        }
    //
    //        //  boolean async = element.hasAttribute("async");
    //        //  boolean defer = element.hasAttribute("defer");
    //
    //        element.getParent().removeChild(element);
    //        return ProcessorResult.OK;
    //    }
    //
    //    protected List<String> getModifiedFileNames(Element element) {
    //
    //    }
    //
    //
    //    @Override
    //    protected ProcessorResult processBundleElement(Arguments arguments, Element element) {
    //
    //        String name = element.getAttributeValue("name");
    //        String mappingPrefix = element.getAttributeValue("mapping-prefix");
    //        boolean async = element.hasAttribute("async");
    //        boolean defer = element.hasAttribute("defer");
    //        NestableNode parent = element.getParent();
    //        List<String> files = new ArrayList<String>();
    //        for (String file : element.getAttributeValue("files").split(",")) {
    //            files.add(file.trim());
    //        }
    //        
    //        if (getBundleEnabled()) {
    //            String versionedBundle = bundlingService.getVersionedBundleName(name, files);
    //
    //            if (StringUtils.isBlank(versionedBundle)) {
    //                BroadleafResourceHttpRequestHandler reqHandler = getRequestHandler(name, arguments);
    //                try {
    //                    versionedBundle = bundlingService.registerBundle(name, files, reqHandler);
    //                } catch (IOException e) {
    //                    throw new RuntimeException(e);
    //                }
    //            }
    //            Expression expression = (Expression) StandardExpressions.getExpressionParser(arguments.getConfiguration())
    //                    .parseExpression(arguments.getConfiguration(), arguments, "@{'" + mappingPrefix + versionedBundle + "'}");
    //            String value = (String) expression.execute(arguments.getConfiguration(), arguments);
    //            Element e = getElement(value, async, defer);
    //            parent.insertAfter(element, e);
    //        } else {
    //            List<String> additionalBundleFiles = bundlingService.getAdditionalBundleFiles(name);
    //            if (additionalBundleFiles != null) {
    //                files.addAll(additionalBundleFiles);
    //            }
    //            for (String file : files) {
    //                file = file.trim();
    //                Expression expression = (Expression) StandardExpressions.getExpressionParser(arguments.getConfiguration())
    //                        .parseExpression(arguments.getConfiguration(), arguments, "@{'" + mappingPrefix + file + "'}");
    //                String value = (String) expression.execute(arguments.getConfiguration(), arguments);
    //                Element e = getElement(value, async, defer);
    //                parent.insertBefore(element, e);
    //            }
    //        }
    //        
    //        parent.removeChild(element);
    //        return ProcessorResult.OK;
    //    }
    //    
    //    /**
    //     * @deprecated use {@link #getScriptElement(String, boolean, boolean)} instead
    //     */
    //    @Deprecated
    //    protected Element getScriptElement(String src) {
    //        return getScriptElement(src, false, false);
    //    }
    //    
    //    protected Element getScriptElement(String src, boolean async, boolean defer) {
    //        Element e = new Element("script");
    //        e.setAttribute("type", "text/javascript");
    //        e.setAttribute("src", src);
    //        if (async) {
    //            e.setAttribute("async", true, null);
    //        }
    //        if (defer) {
    //            e.setAttribute("defer", true, null);
    //        }
    //        return e;
    //    }
    //    
    //    protected Element getLinkElement(String src) {
    //        Element e = new Element("link");
    //        e.setAttribute("rel", "stylesheet");
    //        e.setAttribute("href", src);
    //        return e;
    //    }
    //    
    //    /**
    //     * @deprecated use {@link #getElement(String, boolean, boolean)} instead
    //     */
    //    @Deprecated
    //    protected Element getElement(String src) {
    //        return getElement(src, false, false);
    //    }
    //    
    //    protected Element getElement(String src, boolean async, boolean defer) {
    //        if (src.contains(";")) {
    //            src = src.substring(0, src.indexOf(';'));
    //        }
    //        
    //        if (src.endsWith(".js")) {
    //            return getScriptElement(src, async, defer);
    //        } else if (src.endsWith(".css")) {
    //            return getLinkElement(src);
    //        } else {
    //            throw new IllegalArgumentException("Unknown extension for: " + src + " - only .js and .css are supported");
    //        }
    //    }
    //    
    //    protected BroadleafResourceHttpRequestHandler getRequestHandler(String name, Arguments arguments) {
    //        BroadleafResourceHttpRequestHandler handler = null;
    //        if (name.endsWith(".js")) {
    //            handler = ProcessorUtils.getJsRequestHandler(arguments);
    //        } else if (name.endsWith(".css")) {
    //            handler = ProcessorUtils.getCssRequestHandler(arguments);
    //        }
    //        
    //        if (handler == null) {
    //            throw new IllegalArgumentException("Unknown extension for: " + name + " - only .js and .css are supported");
    //        }
    //        
    //        return handler;
    //    }
    //    
    //    @Override
    //    public int getPrecedence() {
    //        return 10000;
    //    }
    //
    //    protected boolean shouldBundleResources(Element element) {
    //        // Note 1: BP - Copied this from prior implementation.   Not sure that it is needed.
    //        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext(); // Note 1
    //
    //        boolean shouldBundle = BLCSystemProperty.resolveBooleanSystemProperty("bundle.enabled");
    //        if (shouldBundle) {
    //            if (element.hasAttribute("bundle")) {
    //                shouldBundle = Boolean.parseBoolean(element.getAttributeValue("bundle"));
    //            } else {
    //                shouldBundle = defaultBundleAttribute;
    //            }
    //        }
    //        return shouldBundle;
    //    }
    //
    //    protected String getMappingPrefix(Element element) {
    //        String mappingPrefix = null;
    //        if (element.hasAttribute("mapping-prefix")) {
    //            mappingPrefix = element.getAttributeValue("mapping-prefix");
    //        }
    //        return mappingPrefix;
    //    }
    //
    //    protected void addNonModifiableResourceNames(Element element, List<Resource> resources) {
    //        if (element.hasAttribute("resource")) {
    //            String resourceName = element.getAttributeValue("resource");
    //            if (StringUtils.isNotEmpty(resourceName)) {
    //                resources.add(resourceModificationService.getNonModifiedResource(resourceName.trim()));
    //            }
    //        }
    //
    //        if (element.hasAttribute("resources")) {
    //            for (String resourceName : element.getAttributeValue("resources").split(",")) {
    //                resources.add(resourceModificationService.getNonModifiedResource(resourceName.trim()));
    //            }
    //        }
    //    }
    //
    //    protected void addModifiableResourceNames(Element element, List<Resource> resources) {
    //        if (element.hasAttribute("modifiable-resource")) {
    //            String resourceName = element.getAttributeValue("modifiable-resource");
    //            if (StringUtils.isNotEmpty(resourceName)) {
    //                resources.add(resourceModificationService.getModifiedResource(resourceName.trim()));
    //            }
    //        }
    //
    //        if (element.hasAttribute("resources")) {
    //            for (String resourceName : element.getAttributeValue("modifiable-resources").split(",")) {
    //                resources.add(resourceModificationService.getModifiedResource(resourceName.trim()));
    //            }
    //        }
    //    }
    //
    //    protected String getRequestedBundleName(Element element) {
    //        String bundleName = null;
    //        if (element.hasAttribute("bundle-name")) {
    //            bundleName = element.getAttributeValue("bundle-name");
    //        }
    //        
    //        // For backwards compatibility, check "name" attribute
    //        if (bundleName == null && element.hasAttribute("name")) {
    //            bundleName = element.getAttributeValue("name");
    //        }
    //
    //        return bundleName;
    //    }
}
