/*-
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.web.processor;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.resource.service.ResourceBundlingService;
import org.broadleafcommerce.common.web.processor.attributes.ResourceTagAttributes;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.broadleafcommerce.presentation.model.BroadleafTemplateElement;
import org.broadleafcommerce.presentation.model.BroadleafTemplateModel;
import org.broadleafcommerce.presentation.model.BroadleafTemplateNonVoidElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 *     <li>
 *         files - (required) a comma-separated list of files that should be bundled together. May be excluded
 *         in some cases. See note at bottom.
 *     </li>
 *     <li>
 *         async - (optional) true to set <code>async="true"</code> on the resulting tags. Note, this will be ignored
 *         when unbundled for JavaScript unless <code>includeAsyncDeferUnbundled</code> is specified.
 *     </li>
 *     <li>
 *         defer - (optional) true to set <code>defer="true"</code> on JS or to add non-render-blocking CSS. Note,
 *         this will be ignored when unbundled for JavaScript unless <code>includeAsyncDeferUnbundled</code> is
 *         specified.
 *     </li>
 *     <li>
 *         includeAsyncDeferUnbundled - (optional) true to include async and defer (if enabled) on the unbundled
 *         replacement tags. They are not included by default when unbundled due to race conditions between
 *         JavaScript dependencies.
 *     </li>
 *     <li>
 *         bundle-dependency-event - (optional) name of a JavaScript event to wait for before adding this bundle to the
 *         DOM. The JavaScript event must be named the value of this attribute. The event must also be added as a global
 *         variable of the value of this attribute with "Event" on the end
 *         (<code>bundleDependencyEventValue</code>Event). Lastly, the event must be dispatched to the body.
 *         <br/>
 *         For example, if the value of this attribute was "test",
 *         <br/>
 *         <code>var testEvent = new CustomEvent("test"); document.body.dispatchEvent(testEvent);</code>
 *     </li>
 *     <li>
 *         bundle-completed-event - (optional) name of JS event to fire when this bundle has completed. See
 *         bundle-dependency-event for specifics. Use this together with bundle-dependency-event to chain load
 *         dependencies. Note that if using this along with a &lt;blc:bundlepreload&gt;, both the &lt;blc:bundle&gt; and
 *         &lt;blc:bundlepreload&gt; tags need to have this event, otherwise the <code>bundlepreload</code> may generate
 *         the bundle without the JS that fires the event.
 *     </li>
 * </ul>
 * <p>
 * <p>
 * This processor has the ability to retrieve a bundle that has already been requested earlier in the template
 * looking it up with the bundle name. See {@link org.broadleafcommerce.common.web.request.ResourcesRequest} for
 * more information. This helps with not having to duplicate the bundle information across the &lt;blc:bundlepreload&gt;
 * and &lt;blc:bundle&gt; tags.
 *
 * @author apazzolini
 * @author bpolster
 * @author Jacob Mitash (jmitash)
 * @see ResourceBundlingService
 */
@Component("blResourceBundleProcessor")
@ConditionalOnTemplating
public class ResourceBundleProcessor extends AbstractResourceProcessor {

    protected final Map<String, String> deferredCssAttributes;
    protected final Map<String, String> normalCssAttributes;
    @Value("${resource.versioning.enabled:true}")
    protected boolean resourceVersioningEnabled;

    public ResourceBundleProcessor() {
        Map<String, String> deferredCssAttributes = new HashMap<>();
        deferredCssAttributes.put("rel", "preload");
        deferredCssAttributes.put("as", "style");
        deferredCssAttributes.put("onload", "this.onload=null;this.rel='stylesheet';");
        this.deferredCssAttributes = Collections.unmodifiableMap(deferredCssAttributes);

        Map<String, String> normalCssAttributes = new HashMap<>();
        normalCssAttributes.put("rel", "stylesheet");
        this.normalCssAttributes = Collections.unmodifiableMap(normalCssAttributes);
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
    protected BroadleafTemplateModel buildModelUnbundled(
            List<String> attributeFiles,
            ResourceTagAttributes attributes,
            BroadleafTemplateContext context
    ) {
        final BroadleafTemplateModel model = context.createModel();

        final List<String> files = postProcessUnbundledFileList(attributeFiles, attributes, context);

        if (StringUtils.isEmpty(attributes.bundleDependencyEvent())) {
            double random = Math.random();
            // add files one by one
            for (String file : files) {
                ResourceTagAttributes unbundledAttributes = null;
                if (resourceVersioningEnabled) {
                    unbundledAttributes = new ResourceTagAttributes(attributes)
                            .src(file + "?v=" + random);
                } else {
                    unbundledAttributes = new ResourceTagAttributes(attributes)
                            .src(file);
                }
                addElementToModel(unbundledAttributes, context, model);
            }

            // add bundle complete script if needed/supported
            final BroadleafTemplateElement bundleCompleteElement = buildUnbundledSyncCompletedEventElement(
                    attributes, context
            );
            if (bundleCompleteElement != null) {
                model.addElement(bundleCompleteElement);
            }
        } else {
            // Since everything here needs to be added to the DOM after the dependency event, the only thing we add
            // is the JS that handles the dependency event itself
            addDependencyRestrictionToModel(files, attributes, context, model);
        }

        return model;
    }

    @Override
    protected BroadleafTemplateModel buildModelBundled(
            List<String> attributeFiles,
            ResourceTagAttributes attributes,
            BroadleafTemplateContext context
    ) {
        final BroadleafTemplateModel model = context.createModel();

        final String bundleResourcePath = getBundlePath(attributes, attributeFiles);
        final String bundleUrl = getBundleUrl(bundleResourcePath, context);
        attributes.src(bundleUrl);

        if (StringUtils.isEmpty(attributes.bundleDependencyEvent())) {
            addElementToModel(attributes, context, model);
        } else {
            // Since the bundle needs to be added to the DOM after the dependency event, the only thing we add
            // is the JS that handles the dependency event itself
            addDependencyRestrictionToModel(Collections.singletonList(bundleUrl), attributes, context, model);
        }

        return model;
    }

    /**
     * @deprecated Use {@link #addElementToModel(ResourceTagAttributes, BroadleafTemplateContext, BroadleafTemplateModel)} instead
     */
    @Deprecated
    protected void addElementToModel(
            String src,
            boolean async,
            boolean defer,
            BroadleafTemplateContext context,
            BroadleafTemplateModel model
    ) {
        addElementToModel(src, async, defer, null, context, model);
    }

    /**
     * @deprecated Use {@link #addElementToModel(ResourceTagAttributes, BroadleafTemplateContext, BroadleafTemplateModel)} instead
     */
    @Deprecated
    protected void addElementToModel(
            String src,
            boolean async,
            boolean defer,
            String dependencyEvent,
            BroadleafTemplateContext context,
            BroadleafTemplateModel model
    ) {
        ResourceTagAttributes tagAttributes = new ResourceTagAttributes()
                .src(src)
                .async(async)
                .defer(defer)
                .bundleDependencyEvent(dependencyEvent);
        addElementToModel(tagAttributes, context, model);
    }

    /**
     * Adds the bundle to the model.
     *
     * @param attributes the original bundle tag attributes and the src of the bundle to add
     * @param context    the context of the original bundle tag
     * @param model      the model to add the script to
     */
    protected void addElementToModel(
            ResourceTagAttributes attributes,
            BroadleafTemplateContext context,
            BroadleafTemplateModel model
    ) {
        String src = attributes.src();
        src = attributes.src().contains(";") ? src.substring(0, src.indexOf(';')) : src;

        if (src.contains(".js")) {
            addJavaScriptToModel(attributes, context, model);
        } else if (src.contains(".css")) {
            addCssToModel(attributes, context, model);
        } else {
            throw new IllegalArgumentException("Unknown extension for: " + src
                    + " - only .js and .css are supported by default.");
        }
    }

    /**
     * Adds JavaScript to the model in a &lt;script&gt; tag
     *
     * @param attributes the original bundle tag attributes and the src of the JavaScript to add
     * @param context    the context of the original bundle tag
     * @param model      the model to add the script to
     */
    protected void addJavaScriptToModel(
            ResourceTagAttributes attributes,
            BroadleafTemplateContext context,
            BroadleafTemplateModel model
    ) {
        model.addElement(context.createNonVoidElement("script", getScriptAttributes(attributes), true));
    }

    /**
     * Adds the CSS to the model in a &lt;link&gt; tag
     *
     * @param attributes the original bundle tag attributes and the src of the CSS to add
     * @param context    the context of the original bundle tag
     * @param model      the model to add the link to
     */
    protected void addCssToModel(
            ResourceTagAttributes attributes,
            BroadleafTemplateContext context,
            BroadleafTemplateModel model
    ) {
        if (attributes.defer()) {
            List<BroadleafTemplateElement> deferredCssElements = getDeferredCssElements(attributes, context);
            for (BroadleafTemplateElement element : deferredCssElements) {
                model.addElement(element);
            }
        } else {
            model.addElement(context.createStandaloneElement("link", getNormalCssAttributes(attributes), true));
        }
    }

    /**
     * Gets a list of elements to add to the model for deferred CSS
     *
     * @param attributes the attributes of the original resource tag and the src of the CSS to include
     * @param context    the context of the original resource tag
     * @return list of elements needed for deferred CSS
     */
    protected List<BroadleafTemplateElement> getDeferredCssElements(
            ResourceTagAttributes attributes,
            BroadleafTemplateContext context
    ) {
        List<BroadleafTemplateElement> elements = new ArrayList<>();

        Map<String, String> deferredCssAttributes = new HashMap<>(this.deferredCssAttributes);
        deferredCssAttributes.put("href", attributes.src());
        elements.add(context.createStandaloneElement("link", deferredCssAttributes, true));

        BroadleafTemplateNonVoidElement noScriptElement = context.createNonVoidElement("noscript");
        noScriptElement.addChild(context.createStandaloneElement("link", getNormalCssAttributes(attributes), true));
        elements.add(noScriptElement);

        return elements;
    }

    /**
     * @deprecated Use {@link #addDependencyRestrictionToModel(List, ResourceTagAttributes, BroadleafTemplateContext, BroadleafTemplateModel)} instead
     */
    @Deprecated
    protected void addDependentBundleRestrictionToModel(
            String src,
            boolean async,
            boolean defer,
            String dependencyEvent,
            BroadleafTemplateContext context,
            BroadleafTemplateModel model
    ) {
        ResourceTagAttributes attributes = new ResourceTagAttributes()
                .src(src)
                .async(async)
                .defer(defer)
                .bundleDependencyEvent(dependencyEvent);
        addDependencyRestrictionToModel(Collections.singletonList(src), attributes, context, model);
    }

    /**
     * Adds JavaScript to the model that will insert a given script tag when the dependency event is fired
     *
     * @param attributes the original bundle tag attributes and src of the script to add
     * @param context    the context of the original bundle tag
     * @param model      the model to add the script to
     */
    protected void addDependencyRestrictionToModel(
            List<String> files,
            ResourceTagAttributes attributes,
            BroadleafTemplateContext context,
            BroadleafTemplateModel model
    ) {
        final String functionName = cleanUpJavaScriptName(attributes.name());
        final String dependencyEvent = attributes.bundleDependencyEvent();

        List<String> formattedFiles = new ArrayList<>(files.size());
        for (String file : files) {
            formattedFiles.add("'" + file + "'");
        }

        String completedJavaScript = "";

        // Unbundled, async bundle complete events not supported, only add when unbundled and sync. Bundled JS has
        // the event baked in, so we don't need to add it here
        if (!getBundleEnabled() && !useAsyncJavaScript(attributes)) {
            completedJavaScript =
                    "" +
                            "if (idx === arr.length - 1) {" + //last script
                            "    script.addEventListener('load', function () {" +
                            "        " + getBundleAppendText(attributes) + ";" +
                            "    });" +
                            "}";
        }

        final String script =
                "<script>" +
                        "function runOnReady(callback, event) {" +
                        "    var watchEvent = typeof(event) == 'undefined' ? 'DOMContentLoaded' : event;" +
                        "    if (document.readyState != 'loading' && !event) {" +
                        "        callback();" +
                        "    } else {" +
                        "        document.addEventListener(watchEvent, callback);" +
                        "    }" +
                        "}; " +
                        "" +
                        "if (typeof(" + dependencyEvent + "Event) !== 'undefined') {" +
                        "    runOnReady(handle" + functionName + ");" +
                        "} else {" +
                        "    runOnReady(function() {" +
                        "            runOnReady(handle" + functionName + ");" +
                        "        }," +
                        "        '" + dependencyEvent + "');" +
                        "} " +
                        "function handle" + functionName + "() {" +
                        "    var lastScript = null;" +
                        "    [" + StringUtils.join(formattedFiles, ",") + "].forEach(function (elem, idx, arr) {" +
                        "        var script = document.createElement('script');" +
                        "        script.type = 'text/javascript';" +
                        "        script.src = elem;" +
                        "        script.async = " + useAsyncJavaScript(attributes) + ";" +
                        "        document.body.appendChild(script);" +
                        completedJavaScript +
                        "    });" +
                        "};" +
                        "</script>";

        BroadleafTemplateElement element = context.createTextElement(script);
        model.addElement(element);
    }

    /**
     * Cleans up the name that will be used in the dependency handling JavaScript
     *
     * @param original the original name
     * @return clean name
     */
    protected String cleanUpJavaScriptName(final String original) {
        String cleanName = original;
        if (cleanName.contains("/")) {
            cleanName = cleanName.substring(cleanName.lastIndexOf("/") + 1, cleanName.length());
        }
        cleanName = cleanName.replaceAll("-", "_");
        cleanName = cleanName.replaceAll("\\.", "_");

        return cleanName;
    }

    /**
     * @deprecated Use {@link #getScriptAttributes(ResourceTagAttributes)} instead
     */
    @Deprecated
    protected Map<String, String> getScriptAttributes(String src, boolean async, boolean defer) {
        ResourceTagAttributes resourceTagAttributes = new ResourceTagAttributes()
                .src(src)
                .async(async)
                .defer(defer);
        return getScriptAttributes(resourceTagAttributes);
    }

    /**
     * Gets the attributes to put on the &lt;script&gt; tag
     *
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
     * @deprecated Use {@link #getNormalCssAttributes(ResourceTagAttributes)} instead
     */
    @Deprecated
    protected Map<String, String> getLinkAttributes(String src) {
        return getNormalCssAttributes(new ResourceTagAttributes().src(src));
    }

    /**
     * Builds a map of normal (non-deferred) attributes to put on a CSS &lt;link&gt; tag
     *
     * @param tagAttributes the attributes on the original bundle tag
     * @return map of attributes to put on the link tag
     */
    protected Map<String, String> getNormalCssAttributes(ResourceTagAttributes tagAttributes) {
        Map<String, String> attributes = new HashMap<>(normalCssAttributes);
        attributes.put("href", tagAttributes.src());
        return attributes;
    }

    /**
     * Tells if the JavaScript added to the page should be asynchronous
     *
     * @param attributes the attributes on the original bundle tag
     * @return true if the JavaScript should be async and false otherwise
     */
    protected boolean useAsyncJavaScript(ResourceTagAttributes attributes) {
        return attributes.async() && (getBundleEnabled() || attributes.includeAsyncDeferUnbundled());
    }

    @Override
    protected void validateTagAttributes(ResourceTagAttributes attributes) {
        super.validateTagAttributes(attributes);

        if (!attributes.name().endsWith(".js")) {
            if (attributes.bundleDependencyEvent() != null) {
                throw new InvalidParameterException("A 'bundle-dependency-event' attribute was specified but is only " +
                        "supported for JavaScript bundles.");
            }
            if (attributes.bundleCompletedEvent() != null) {
                throw new InvalidParameterException("A 'bundle-completed-event' attribute was specified but is only " +
                        "supported for JavaScript bundles.");
            }
        }

        if (attributes.bundleCompletedEvent() != null && !getBundleEnabled() && useAsyncJavaScript(attributes)) {
            throw new InvalidParameterException("A 'bundle-completed-event' attribute was specified, but is not " +
                    "supported when using asynchronous, unbundled JavaScript");
        }
    }

    /**
     * Builds a script element that fires the bundle complete event only when supported
     *
     * @param attributes the attributes of the bundle tag
     * @param context    the context of the bundle tag
     * @return the script element or null if not supported
     */
    protected BroadleafTemplateElement buildUnbundledSyncCompletedEventElement(
            ResourceTagAttributes attributes,
            BroadleafTemplateContext context
    ) {
        if (getBundleEnabled() || useAsyncJavaScript(attributes) || attributes.bundleCompletedEvent() == null) {
            return null;
        }

        final String bundleCompleteEventJavaScript = getBundleCompleteEventJavaScript(attributes);
        if (bundleCompleteEventJavaScript == null) {
            return null;
        }

        final BroadleafTemplateNonVoidElement script = context.createNonVoidElement("script");
        script.addChild(context.createTextElement(bundleCompleteEventJavaScript));

        return script;
    }

}
