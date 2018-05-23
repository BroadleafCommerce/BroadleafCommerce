/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.common.web.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.resource.service.ResourceBundlingService;
import org.broadleafcommerce.common.web.processor.attributes.ResourceTagAttributes;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.broadleafcommerce.presentation.dialect.AbstractBroadleafTagReplacementProcessor;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.broadleafcommerce.presentation.model.BroadleafTemplateElement;
import org.broadleafcommerce.presentation.model.BroadleafTemplateModel;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


/**
 * <p>
 * Works with the blc:bundle tag.   
 * 
 * <p>
 * This processor does not do the actual bundling.   It merely changes the URL which causes the 
 * other bundling components to be invoked through the normal static resource handling processes.
 * 
 * <p>
 * This processor relies {@code bundle.enabled}.   If this property is false (typical for dev) then the list of
 * resources will be output as individual SCRIPT or LINK elements for each JavaScript or CSS file respectively.
 * 
 * <p>
 * To use this processor, supply a name, mapping prefix, and list of files.   
 * 
 * <pre>
 * {@code
 * <blc:bundle name="lib.js" 
 *             mapping-prefix="/js/"
 *             files="plugins.js,
 *                    libs/jquery.MetaData.js,
 *                    libs/jquery.rating.pack.js,
 *                    libs/jquery.dotdotdot-1.5.1.js" />
 *  }
 * </pre>                  
 * 
 * <p>
 * With bundling enabled this will turn into:
 * 
 * <pre>
 * 
 * {@code
 *  <script type="text/javascript" src="/js/lib-blbundle12345.js" />
 * }
 * </pre>
 * 
 * <p>
 * Where the <b>-blbundle12345</b> is used by the BundleUrlResourceResolver to determine the
 * actual bundle name.  
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
 * This processor also supports producing the 'async' and 'defer' attributes for Javascript files. For instance:
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
 * <p>
 * <b>Tag attributes:</b>
 * <ul>
 *     <li>name - (required) the final name prefix of the bundle</li>
 *     <li>mapping-prefix - (required) the prefix appended to the final tag output whether that be</li>
 *     <li>files - (required) a comma-separated list of files that should be bundled together</li>
 *     <li>async - (optional) true to set <code>async="true"</code> on the resulting tags</li>
 *     <li>defer - (optional) true to set <code>defer="true"</code> on the resulting tags</li>
 *     <li>
 *         includeAsyncDeferUnbundled - (optional) true to include async and defer (if enabled) on the unbundled
 *         replacement tags. They are not included by default when unbundled due to race conditions between
 *         JavaScript dependencies.
 *     </li>
 *     <li>
 *         bundle-dependency-event - name of a JavaScript event to wait for before adding this bundle to the DOM.
 *         The JavaScript event must be named the value of this attribute. The event must also be added as a global
 *         variable of the value of this attribute with "Event" on the end
 *         (<code>bundleDependencyEventValue</code>Event). Lastly, the event must be dispatched to the body.
 *         <br/>
 *         For example, if the value of this attribute was "test",
 *         <br/>
 *         <code>var testEvent = new CustomEvent("test"); document.body.dispatchEvent(testEvent);</code>
 *     </li>
 * </ul>
 * @author apazzolini
 * @author bpolster
 * @see ResourceBundlingService
 */
@Component("blResourceBundleProcessor")
@ConditionalOnTemplating
public class ResourceBundleProcessor extends AbstractBroadleafTagReplacementProcessor {
    
    @Resource(name = "blResourceBundlingService")
    protected ResourceBundlingService bundlingService;

    @Resource
    protected Environment environment;
    
    protected boolean getBundleEnabled() {
        return Boolean.parseBoolean(environment.getProperty("bundle.enabled"));
    }
    
    @Override
    public String getName() {
        return "bundle";
    }
    
    @Override
    public int getPrecedence() {
        return 10000;
    }
    
    @Override
    public BroadleafTemplateModel getReplacementModel(String tagName, Map<String, String> tagAttributes, BroadleafTemplateContext context) {
        ResourceTagAttributes resourceTagAttributes = buildResourceTagAttributes(tagAttributes);

        List<String> files = getRequestedFiles(tagAttributes.get("files"));

        List<String> additionalBundleFiles = bundlingService.getAdditionalBundleFiles(resourceTagAttributes.name());
        if (additionalBundleFiles != null) {
            files.addAll(additionalBundleFiles);
        }

        BroadleafTemplateModel model = context.createModel();
        if (getBundleEnabled()) {
            buildReplacementModelBundled(resourceTagAttributes, files, context, model);
        } else {
            buildReplacementModelUnbundled(resourceTagAttributes, files, context, model);
        }

        return model;
    }

    /**
     * Adds the unbundled files to replacement model
     * @param attributes the original bundle tag attributes
     * @param files the list of files to add to the model
     * @param context the context of the original bundle tag
     * @param model the model to add the script to
     */
    protected void buildReplacementModelUnbundled(ResourceTagAttributes attributes, List<String> files, BroadleafTemplateContext context, BroadleafTemplateModel model) {
        for (String fileName : files) {
            ResourceTagAttributes unbundledAttributes = new ResourceTagAttributes(attributes)
                    .src(getFullUnbundledFileName(fileName, attributes, context));
            addElementToModel(unbundledAttributes, context, model);
        }
    }

    /**
     * Adds the bundle to replacement model
     * @param attributes the original bundle tag attributes
     * @param files the list of files to add to the model
     * @param context the context of the original bundle tag
     * @param model the model to add the script to
     */
    protected void buildReplacementModelBundled(ResourceTagAttributes attributes, List<String> files, BroadleafTemplateContext context, BroadleafTemplateModel model) {
        final String bundleResourceName = bundlingService.resolveBundleResourceName(attributes.name(),
                attributes.mappingPrefix(),
                files);

        final String bundleUrl = getBundleUrl(bundleResourceName, context);
        attributes.src(bundleUrl);

        addElementToModel(attributes, context, model);
    }

    /**
     * Adds the context path to the bundleUrl.    We don't use the Thymeleaf "@" syntax or any other mechanism to 
     * encode this URL as the resolvers could have a conflict.   
     * 
     * For example, resolving a bundle named "style.css" that has a file also named "style.css" creates problems as
     * the TF or version resolvers both want to version this file.
     *
     * @param bundleName the path of the bundle to add
     * @param context the context of the original bundle tag
     *
     * @return the full bundle URL
     */
    protected String getBundleUrl(String bundleName, BroadleafTemplateContext context) {
        String bundleUrl = bundleName;

        if (!StringUtils.startsWith(bundleUrl, "/")) {
            bundleUrl = "/" + bundleUrl;
        }
        
        HttpServletRequest request = context.getRequest();
        String contextPath = "";
        if (request != null) {
            contextPath = request.getContextPath();
        }
        if (StringUtils.isNotEmpty(contextPath)) {
            bundleUrl = contextPath + bundleUrl;
        }

        return bundleUrl;
    }

    /**
     * @deprecated Use {@link #addElementToModel(ResourceTagAttributes, BroadleafTemplateContext, BroadleafTemplateModel)} instead
     */
    @Deprecated
    protected void addElementToModel(String src, boolean async, boolean defer, BroadleafTemplateContext context, BroadleafTemplateModel model) {
        addElementToModel(src, async, defer, null, context, model);
    }

    /**
     * @deprecated Use {@link #addElementToModel(ResourceTagAttributes, BroadleafTemplateContext, BroadleafTemplateModel)} instead
     */
    @Deprecated
    protected void addElementToModel(String src, boolean async, boolean defer, String dependencyEvent, BroadleafTemplateContext context, BroadleafTemplateModel model) {
        ResourceTagAttributes tagAttributes = new ResourceTagAttributes()
                .src(src)
                .async(async)
                .defer(defer)
                .dependencyEvent(dependencyEvent);
        addElementToModel(tagAttributes, context, model);
    }

    /**
     * Adds the bundle to the model.
     * @param attributes the original bundle tag attributes and the src of the bundle to add
     * @param context the context of the original bundle tag
     * @param model the model to add the script to
     */
    protected void addElementToModel(ResourceTagAttributes attributes, BroadleafTemplateContext context, BroadleafTemplateModel model) {
        String src = attributes.src();
        src = attributes.src().contains(";") ? src.substring(0, src.indexOf(';')) : src;

        if (src.endsWith(".js")) {
            addJavaScriptToModel(attributes, context, model);
        } else if (src.endsWith(".css")) {
            addCssToModel(attributes, context, model);
        } else {
            throw new IllegalArgumentException("Unknown extension for: " + src + " - only .js and .css are supported by default.");
        }
    }

    /**
     * Adds JavaScript to the model in a &lt;script&gt; tag
     * @param attributes the original bundle tag attributes and the src of the JavaScript to add
     * @param context the context of the original bundle tag
     * @param model the model to add the script to
     */
    protected void addJavaScriptToModel(ResourceTagAttributes attributes, BroadleafTemplateContext context, BroadleafTemplateModel model) {
        if (!StringUtils.isEmpty(attributes.dependencyEvent())) {
            addDependentBundleRestrictionToModel(attributes, context, model);
        } else {
            model.addElement(context.createNonVoidElement("script", getScriptAttributes(attributes), true));
        }
    }

    /**
     * Adds the CSS to the model in a &lt;link&gt; tag
     * @param attributes the original bundle tag attributes and the src of the CSS to add
     * @param context the context of the original bundle tag
     * @param model the model to add the link to
     */
    protected void addCssToModel(ResourceTagAttributes attributes, BroadleafTemplateContext context, BroadleafTemplateModel model) {
        model.addElement(context.createNonVoidElement("link", getLinkAttributes(attributes), true));
    }

    /**
     * @deprecated Use {@link #addDependentBundleRestrictionToModel(ResourceTagAttributes, BroadleafTemplateContext, BroadleafTemplateModel)} instead
     */
    @Deprecated
    protected void addDependentBundleRestrictionToModel(String src, boolean async, boolean defer, String dependencyEvent, BroadleafTemplateContext context, BroadleafTemplateModel model) {
        ResourceTagAttributes attributes = new ResourceTagAttributes()
                .src(src)
                .async(async)
                .defer(defer)
                .dependencyEvent(dependencyEvent);
        addDependentBundleRestrictionToModel(attributes, context, model);
    }

    /**
     * Adds JavaScript to the model that will insert a given script tag when the dependency event is fired
     * @param attributes the original bundle tag attributes and src of the script to add
     * @param context the context of the original bundle tag
     * @param model the model to add the script to
     */
    protected void addDependentBundleRestrictionToModel(ResourceTagAttributes attributes, BroadleafTemplateContext context, BroadleafTemplateModel model) {
        String methodName = attributes.src();
        String dependencyEvent = attributes.dependencyEvent();
        if (methodName.contains("/")) {
            methodName = methodName.substring(methodName.lastIndexOf("/")+1, methodName.length());
        }
        methodName = methodName.replaceAll("-", "_");
        methodName = methodName.replaceAll("\\.", "_");

        String script = "<script>\n" +
                "if (typeof(" + dependencyEvent + "Event) !== 'undefined'){" +
                "$(function() {" +
                "handle" + methodName + "();" +
                "});" +
                "} else {" +
                "document.body.addEventListener('" + dependencyEvent + "'," +
                "function (elem) {" +
                "$(function() {" +
                "handle" + methodName + "();" +
                "});" +
                "}, false" +
                ");" +
                "}" +
                "function handle" + methodName + "() {" +
                "var script= document.createElement('script');" +
                "script.type= 'text/javascript';" +
                "script.src= '" + attributes.src() + "';" +
                "document.body.append(script);" +
                "}" +
                "\n</script>";
        BroadleafTemplateElement linkedData = context.createTextElement(script);
        model.addElement(linkedData);
    }

    /**
     * @deprecated Use {@link #getScriptAttributes(ResourceTagAttributes)} instead
     */
    protected Map<String, String> getScriptAttributes(String src, boolean async, boolean defer) {
        ResourceTagAttributes resourceTagAttributes = new ResourceTagAttributes()
                .src(src)
                .async(async)
                .defer(defer);
        return getScriptAttributes(resourceTagAttributes);
    }

    /**
     * Gets the attributes to put on the &lt;script&gt; tag
     * @param tagAttributes the attributes of the original bundle tag and src of this script
     * @return attributes to put on the replacement script tag
     */
    protected Map<String, String> getScriptAttributes(ResourceTagAttributes tagAttributes) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("type", "text/javascript");
        attributes.put("src", tagAttributes.src());
        if (getBundleEnabled() || tagAttributes.includeAsyncDeferUnbundled()) {
            if (tagAttributes.async()) {
                attributes.put("async", null);
            }
            if (tagAttributes.defer()) {
                attributes.put("defer", null);
            }
        }
        return attributes;
    }

    /**
     * @deprecated Use {@link #getLinkAttributes(ResourceTagAttributes)} instead
     */
    protected Map<String, String> getLinkAttributes(String src) {
        return getLinkAttributes(new ResourceTagAttributes().src(src));
    }

    /**
     * Builds a map of the attributes to put on a &lt;link&gt; tag
     * @param tagAttributes the attributes on the original bundle tag
     * @return map of attributes to put on the link tag
     */
    protected Map<String, String> getLinkAttributes(ResourceTagAttributes tagAttributes) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("rel", "stylesheet");
        attributes.put("href", tagAttributes.src());
        return attributes;
    }

    /**
     * Builds the tag attributes of the bundle tag
     * @param tagAttributes the original attributes of the bundle tag
     * @return a {@link ResourceTagAttributes} containing the original bundle tag attributes
     */
    protected ResourceTagAttributes buildResourceTagAttributes(Map<String, String> tagAttributes) {
        return new ResourceTagAttributes()
            .name(tagAttributes.get("name"))
            .mappingPrefix("mapping-prefix")
            .async(tagAttributes.containsKey("async"))
            .defer(tagAttributes.containsKey("defer"))
            .includeAsyncDeferUnbundled(tagAttributes.containsKey("includeAsyncDeferUnbundled") && Boolean.parseBoolean(tagAttributes.get("includeAsyncDeferUnbundled")))
            .dependencyEvent(tagAttributes.get("bundle-dependency-event"));
    }

    /**
     * Gets the full path of an unbundled file.
     * @param fileName the file name to parse
     * @param resourceTagAttributes the tag attributes of the original bundle tag (<code>fileName</code> will be used instead of <code>src</code>)
     * @param context the template context
     * @return the full path of the unbundled file
     */
    protected String getFullUnbundledFileName(String fileName, ResourceTagAttributes resourceTagAttributes, BroadleafTemplateContext context) {
        return context.parseExpression("@{'" + resourceTagAttributes.mappingPrefix() + fileName.trim() + "'}");
    }

    /**
     * Gets a list of the requested files for bundling
     * @param rawFiles comma separated list of files
     * @return list of requested files with space trimmed
     */
    protected List<String> getRequestedFiles(String rawFiles) {
        final String[] splitFiles = rawFiles.split(",");
        List<String> files = new ArrayList<>(splitFiles.length);

        for (String file : splitFiles) {
            files.add(file.trim());
        }

        return files;
    }
}
