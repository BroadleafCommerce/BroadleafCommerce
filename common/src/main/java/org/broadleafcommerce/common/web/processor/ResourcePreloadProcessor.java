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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.broadleafcommerce.common.web.processor.attributes.ResourceTagAttributes;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.broadleafcommerce.presentation.model.BroadleafTemplateElement;
import org.broadleafcommerce.presentation.model.BroadleafTemplateModel;
import org.springframework.stereotype.Component;

/**
 * Adds &lt;link&gt; tags to the model that preload resources.
 * <p>
 * This is useful in combination with bundling where one bundle might depend on another and must wait for the other to
 * finish before it can be added to the DOM. Since the script isn't immediately in the DOM, the browser doesn't download
 * the resource until it's added to the DOM, increasing the time before the bundle can be used.
 * <p>
 * This processor adds preload link tags which tell the browser to preload (download) a resource even though it isn't
 * yet in the DOM. Doing so decreases the latency when the script is ready to execute.
 * <p>
 * This processor has the ability to retrieve a bundle that has already been requested earlier in the template
 * looking it up with the bundle name. See {@link org.broadleafcommerce.common.web.request.ResourcesRequest} for
 * more information. This helps with not having to duplicate the bundle information across the &lt;blc:bundlepreload&gt;
 * and &lt;blc:bundle&gt; tags.
 * <p>
 * The &lt;bundlepreload&gt; accepts all the parameters that {@link ResourceBundleProcessor} accepts, but will only ever
 * use them if they are relevant to generating the bundle.
 * <br/>
 *
 * @author Jacob Mitash
 */
@Component("blResourcePreloadProcessor")
@ConditionalOnTemplating
public class ResourcePreloadProcessor extends AbstractResourceProcessor {

    @Override
    public String getName() {
        return "bundlepreload";
    }

    @Override
    public int getPrecedence() {
        return 10000;
    }

    @Override
    protected BroadleafTemplateModel buildModelBundled(List<String> attributeFiles, ResourceTagAttributes resourceTagAttributes, BroadleafTemplateContext context) {
        BroadleafTemplateModel model = context.createModel();

        final String bundleResourcePath = getBundlePath(resourceTagAttributes, attributeFiles);
        final String bundleUrl = getBundleUrl(bundleResourcePath, context);

        BroadleafTemplateElement bundlePreload = buildPreloadElement(bundleUrl, context);
        model.addElement(bundlePreload);

        return model;
    }

    @Override
    protected BroadleafTemplateModel buildModelUnbundled(List<String> attributeFiles, ResourceTagAttributes resourceTagAttributes, BroadleafTemplateContext context) {
        BroadleafTemplateModel model = context.createModel();

        final List<String> files = postProcessUnbundledFileList(attributeFiles, resourceTagAttributes, context);

        for (final String file : files) {
            BroadleafTemplateElement element = buildPreloadElement(file, context);
            model.addElement(element);
        }

        return model;
    }

    /**
     * Builds a preload link for the given path
     * @param href the path of the file to create the link with
     * @param context the context of the bundlepreload tag
     * @return a link element linking to the given resource
     */
    protected BroadleafTemplateElement buildPreloadElement(String href, BroadleafTemplateContext context) {
        final String as = getAs(href);
        Map<String, String> attributes = getPreloadAttributes(href, as);

        return context.createStandaloneElement("link", attributes, true);
    }

    /**
     * Builds a map of the attributes that should be put on the &lt;link&gt; tag.
     * @param href the href of the resource to preload
     * @param as the value the "as" attribute should have or null if it shouldn't be included
     * @return a map of attributes to place on the link tag
     */
    protected Map<String, String> getPreloadAttributes(String href, String as) {
        Map<String, String> attributes = new HashMap<>();

        attributes.put("href", href);
        attributes.put("rel", "preload");
        if (as != null) {
            attributes.put("as", as);
        }

        return attributes;
    }

    /**
     * Gets the "as" attribute for the link based off of the file name
     * @param file the name of the file
     * @return an appropriate "as" value or null if none was found
     */
    protected String getAs(String file) {
        if (file.endsWith(".js")) {
            return "script";
        } else if (file.endsWith(".css")) {
            return "style";
        } else {
            return null;
        }
    }
}
