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
package org.broadleafcommerce.common.resource.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.resource.GeneratedResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * @see ResourceMinificationService 
 * @author Andre Azzolini (apazzolini)
 */
@Service("blResourceMinificationService")
public class ResourceMinificationServiceImpl implements ResourceMinificationService {

    protected static final Log LOG = LogFactory.getLog(ResourceMinificationServiceImpl.class);

    protected static final String MINIFICATION_IS_DISABLED_RETURNING_ORIGINAL_RESOURCE = "Minification is disabled, returning original resource";
    protected static final String COULD_NOT_MINIFY_RESOURCES_RETURNED_UNMINIFIED_BYTES = "Could not minify resources, returned unminified bytes";
    public static String CSS_TYPE = "css";
    public static String JS_TYPE = "js";
    public static String JS_MIN = ".min.js";
    public static String CSS_MIN = ".min.css";

    @Autowired
    protected Environment environment;

    @Autowired
    protected JavascriptMinificationService jsMinificationService;

    @Autowired
    protected CssMinificationService cssMinificationService;

    @Override
    public boolean getEnabled() {
        return environment.getProperty("minify.enabled", Boolean.class);
    }

    @Override
    public byte[] minify(String filename, byte[] bytes) {
        if (!getEnabled()) {
            LOG.trace(MINIFICATION_IS_DISABLED_RETURNING_ORIGINAL_RESOURCE);
            return bytes;
        }

        Resource modifiedResource = minify(new ByteArrayResource(bytes), filename);

        if (modifiedResource instanceof GeneratedResource) {
            return ((GeneratedResource) modifiedResource).getBytes();
        } else {
            return bytes;
        }
    }

    @Override
    public Resource minify(Resource originalResource) {
        if (!getEnabled()) {
            LOG.trace(MINIFICATION_IS_DISABLED_RETURNING_ORIGINAL_RESOURCE);
            return originalResource;
        }

        if (originalResource.getFilename() == null) {
            LOG.warn("Attempted to modify resource without a filename, returning non-minified resource");
            return originalResource;
        }

        return minify(originalResource, originalResource.getFilename());
    }

    @Override
    public Resource minify(Resource originalResource, String filename) {
        if (!getEnabled()) {
            LOG.trace(MINIFICATION_IS_DISABLED_RETURNING_ORIGINAL_RESOURCE);
            return originalResource;
        }

        String type = getFileType(originalResource, filename);
        if (type == null) {
            LOG.info("Unsupported minification resource: " + filename);
            return originalResource;
        }

        if (isPreviouslyMinifiedFile(originalResource)) {
            LOG.debug("Minification has already be done for this resource: " + filename);
            return originalResource;
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(originalResource.getInputStream(), "utf-8"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(baos, "utf-8"));) {

            minify(in, out, filename, type);
            out.flush();
            return new GeneratedResource(baos.toByteArray(), filename);
        } catch (Exception e) {
            LOG.warn(COULD_NOT_MINIFY_RESOURCES_RETURNED_UNMINIFIED_BYTES, e);
            return originalResource;
        }
    }
    
    // TODO Change method signature to throw a ResourceMinificationException in 5.4
    protected void minify(BufferedReader in, BufferedWriter out, String filename, String type) throws IOException {
        try {
            if (JS_TYPE.equals(type)) {
                jsMinificationService.minifyJs(filename, in, out);
            } else if (CSS_TYPE.equals(type)) {
                cssMinificationService.minifyCss(filename, in, out);
            } else {
                throw new ResourceMinificationException("Minification is not supported for file: " + filename);
            }
        } catch (ResourceMinificationException e) {
            // We're throwing and catching ResourceMinificationException for backwards compatibility. 
            // In 5.4 this method will throw a ResourceMinificationException
            throw new IOException(e);
        }
    }

    /**
     * Return a SupportedFileType
     * @param originalResource
     * @param filename
     * @return
     */
    protected String getFileType(Resource originalResource, String filename) {
        if (filename.contains(".js")) {
            return JS_TYPE;
        } else if (filename.contains(".css")) {
            return CSS_TYPE;
        }
        return null;
    }

    protected boolean isPreviouslyMinifiedFile(Resource originalResource) {
        String filename = originalResource.getFilename();
        if (StringUtils.endsWith(filename, JS_MIN) || StringUtils.endsWith(filename, CSS_MIN)) {
            return true;
        }
        return false;
    }

}
