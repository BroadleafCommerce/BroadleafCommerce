/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2018 Broadleaf Commerce
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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.resource.service.ResourceBundlingService;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.processor.attributes.ResourceTagAttributes;
import org.broadleafcommerce.common.web.request.ResourcesRequest;
import org.broadleafcommerce.common.web.request.ResourcesRequestBundle;
import org.broadleafcommerce.presentation.dialect.AbstractBroadleafTagReplacementProcessor;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.broadleafcommerce.presentation.model.BroadleafTemplateModel;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * An abstract tag replacement processor that provides methods to help get resource/bundle information
 *
 * @author Jacob Mitash (jmitash)
 */
public abstract class AbstractResourceProcessor extends AbstractBroadleafTagReplacementProcessor {

    @Resource
    protected Environment environment;

    @Resource
    protected ResourceBundlingService bundlingService;

    @Resource
    protected ResourcesRequest resourcesRequest;

    /**
     * Tells if bundling is enabled
     * @return true if enabled, false otherwise
     */
    protected boolean getBundleEnabled() {
        return Boolean.parseBoolean(environment.getProperty("bundle.enabled"));
    }

    @Override
    public BroadleafTemplateModel getReplacementModel(String tagName, Map<String, String> tagAttributes, BroadleafTemplateContext context) {
        ResourceTagAttributes resourceTagAttributes = buildResourceTagAttributes(tagAttributes);
        validateTagAttributes(resourceTagAttributes);

        final List<String> files = buildBundledFilesList(resourceTagAttributes);

        final BroadleafTemplateModel model;
        if (getBundleEnabled()) {
            model = buildModelBundled(files, resourceTagAttributes, context);
        } else {
            model = buildModelUnbundled(files, resourceTagAttributes, context);
        }

        return model;
    }

    /**
     * Builds the model that contains the unbundled resources the tag should be replaced with
     * @param attributeFiles list of files that are to be included
     * @param attributes the attributes of the original tag this processor replaces
     * @param context the context of the original tag
     * @return model containing resources the tag should be replaced with
     */
    protected abstract BroadleafTemplateModel buildModelUnbundled(List<String> attributeFiles, ResourceTagAttributes attributes, BroadleafTemplateContext context);

    /**
     * Builds the model that contains the bundled resources the tag should be replaced with
     * @param attributeFiles list of files that are to be bundled
     * @param attributes the attributes of the original tag this processor replaces
     * @param context the context of the original tag
     * @return model containing resources the tag should be replaced with
     */
    protected abstract BroadleafTemplateModel buildModelBundled(List<String> attributeFiles, ResourceTagAttributes attributes, BroadleafTemplateContext context);

    /**
     * Gets a list of the requested files for bundling
     * @param rawFileNames comma separated list of files
     * @return list of requested files with space trimmed or null if rawFileNames is null
     */
    protected List<String> getRequestedFileNames(String rawFileNames) {
        List<String> fileNames = new ArrayList<>();

        if (StringUtils.isNotBlank(rawFileNames)) {
            final String[] splitFileNames = rawFileNames.split(",");

            for (String fileName : splitFileNames) {
                fileNames.add(fileName.trim());
            }
        }

        return fileNames;
    }

    /**
     * Builds the tag attributes of the bundle tag
     * @param tagAttributes the original attributes of the bundle tag
     * @return a {@link ResourceTagAttributes} containing the original bundle tag attributes
     */
    protected ResourceTagAttributes buildResourceTagAttributes(Map<String, String> tagAttributes) {
        return new ResourceTagAttributes()
            .name(tagAttributes.get("name"))
            .mappingPrefix(tagAttributes.get("mapping-prefix"))
            .async(Boolean.parseBoolean(tagAttributes.get("async")))
            .defer(Boolean.parseBoolean(tagAttributes.get("defer")))
            .includeAsyncDeferUnbundled(Boolean.parseBoolean(tagAttributes.get("includeAsyncDeferUnbundled")))
            .bundleDependencyEvent(tagAttributes.get("bundle-dependency-event"))
            .files(tagAttributes.get("files"))
            .bundleCompletedEvent(tagAttributes.get("bundle-completed-event"));
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
     * Adds the context path to the bundleUrl.    We don't use the Thymeleaf "@" syntax or any other mechanism to
     * encode this URL as the resolvers could have a conflict.
     *
     * For example, resolving a bundle named "style.css" that has a file also named "style.css" creates problems as
     * the TF or version resolvers both want to version this file.
     *
     * @param bundleName the path of the bundle to add
     * @param context the context of the original bundle tag
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
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();
        if (brc != null && brc.getTheme() != null) {
            Long themeId = brc.getTheme().getId();
            return bundleUrl + "?themeConfigId=" + themeId;
        }
        else {
            return bundleUrl;
        }

    }

    /**
     * Gets all the files that should be included in the bundle
     * @param tagAttributes the tag attributes of the resource tag to replace
     * @return list of all the files to include in the bundle
     */
    protected List<String> buildBundledFilesList(ResourceTagAttributes tagAttributes) {
        final List<String> requestedFileNames = getRequestedFileNames(tagAttributes.files());
        final List<String> allFileNames = new ArrayList<>(requestedFileNames);

        if (CollectionUtils.isNotEmpty(allFileNames)) {
            final List<String> additionalBundleFileNames = bundlingService.getAdditionalBundleFiles(tagAttributes.name());

            if (additionalBundleFileNames != null) {
                allFileNames.addAll(additionalBundleFileNames);
            }
        }

        return allFileNames;
    }

    /**
     * Gets the bundle path. The path should still be put through {@link #getBundleUrl(String, BroadleafTemplateContext)}
     * to get the href/src appropriate for the HTML.
     * @param attributes the attributes on the original resource tag
     * @param files the files requested with the bundle or null if not included
     * @return the bundle path
     */
    protected String getBundlePath(ResourceTagAttributes attributes, List<String> files) {
        ResourcesRequestBundle bundle = resourcesRequest.getBundle(attributes.name(), attributes.mappingPrefix(), files);
        final String requestBundlePath = bundle == null ? null : bundle.getBundlePath();
        final String bundleResourcePath;

        if (requestBundlePath == null) {
            //lookup the bundle normally
            bundleResourcePath = bundlingService.resolveBundleResourceName(attributes.name(),
                    attributes.mappingPrefix(),
                    files,
                    getBundleAppendText(attributes));

            //save this for any other bundles that may be on the page
            resourcesRequest.saveBundle(attributes.name(), attributes.mappingPrefix(), files, bundleResourcePath);
        } else {
            //this bundle was already requested somewhere in the template
            bundleResourcePath = requestBundlePath;
        }

        return bundleResourcePath;
    }

    /**
     * Performs post processing on an unbundled file list to either grab the file list stored on the request (see
     * {@link ResourcesRequest}) or use the files from the tag attributes and save them so they can be used
     * again later without the files attribute.
     * @param attributeFiles the files that were on the attribute (and any additional files to include)
     * @param tagAttributes the attributes that were on the original resource tag
     * @param context the context of the original resource tag
     * @return list of files to use as resources
     */
    protected List<String> postProcessUnbundledFileList(List<String> attributeFiles, ResourceTagAttributes tagAttributes, BroadleafTemplateContext context) {

        final ResourcesRequestBundle resourcesRequestBundle = resourcesRequest.getBundle(tagAttributes.name(), tagAttributes.mappingPrefix(), attributeFiles);
        final List<String> filesOnSavedBundle = resourcesRequestBundle == null ? null : resourcesRequestBundle.getBundleFilePaths();

        if (filesOnSavedBundle != null) {
            // this bundle has already been requested earlier in the template
            // we can use the stored files and not have to pull the information from the attributes
            return filesOnSavedBundle;
        } else {
            // store these files on the request in case they're requested again
            // so we can pull them without the template having to have the files attribute again
            List<String> fullFileUrls = new ArrayList<>(attributeFiles.size());
            for (String file : attributeFiles) {
                fullFileUrls.add(getFullUnbundledFileName(file, tagAttributes, context));
            }
            resourcesRequest.saveBundle(tagAttributes.name(), tagAttributes.mappingPrefix(), attributeFiles, fullFileUrls);

            return fullFileUrls;
        }
    }

    /**
     * Gets the bundle append text, text that will be appended to the end of a bundle
     * @param attributes the original resource tag attributes
     * @return bundle append text
     */
    protected String getBundleAppendText(ResourceTagAttributes attributes) {
        if (!StringUtils.isEmpty(attributes.bundleCompletedEvent()) && attributes.name().endsWith(".js")) {
            return getBundleCompleteEventJavaScript(attributes);
        }
        return null;
    }

    /**
     * Gets the JavaScript that fires an event when a bundle is completed
     * @param attributes the attributes to build the event off of
     * @return JavaScript that fires an event or null if none requested
     */
    protected String getBundleCompleteEventJavaScript(ResourceTagAttributes attributes) {
        final String event = attributes.bundleCompletedEvent();
        return "var " + event + "Event = new CustomEvent('" + event + "');" +
                "document.dispatchEvent(" + event + "Event);";

    }

    /**
     * Validates the requested tag attributes
     * @param resourceTagAttributes the tag attributes from the original resource tag
     */
    protected void validateTagAttributes(ResourceTagAttributes resourceTagAttributes) {
        if (StringUtils.isEmpty(resourceTagAttributes.name())) {
            throw new IllegalArgumentException("A 'name' attribute is required for " + getPrefix() + ":" + getName() + " tags");
        }

        boolean hasSavedResources = resourcesRequest.getBundle(resourceTagAttributes.name(),
                    resourceTagAttributes.mappingPrefix(),
                    buildBundledFilesList(resourceTagAttributes)) != null;

        if (!hasSavedResources) {
            if (resourceTagAttributes.mappingPrefix() == null) {
                throw new IllegalArgumentException("A 'mapping-prefix' attribute is required for "
                        + getPrefix() + ":" + getName() + " tags when they first appear on the template");
            }
            if (StringUtils.isEmpty(resourceTagAttributes.files())) {
                throw new IllegalArgumentException("A 'files' attribute is required for "
                        + getPrefix() + ":" + getName() + " tags when they first appear on the template");
            }
        }
    }
}
