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

import org.springframework.core.io.Resource;

/**
 * Service responsible for interacting with YUI-compressor or Google Closure Compiler to minify JS/CSS resources.
 * <p>
 * Property to enable minification:
 * minify.enabled - whether or not to actually perform minification
 *
 * @author Andre Azzolini (apazzolini)
 */
public interface ResourceMinificationService {

    /**
     * Given the source byte[], will return a byte[] that represents the minified version of the byte[]
     *
     * @param filename
     * @param bytes
     * @return the minified bytes
     */
    byte[] minify(String filename, byte[] bytes);

    /**
     * Indicates whether or not the system is allowed to minify bundled resources.
     *
     * @return the value of the system property "minify.enabled"
     */
    boolean getEnabled();

    /**
     * Delegates to {@link #minify(Resource, String)} where the filename argument is originalResource.getFilename().
     *
     * @param originalResource
     * @return
     */
    Resource minify(Resource originalResource);

    /**
     * Given a {@link Resource}, will return a resource that represents the minified version.
     *
     * @param orginalResource
     * @param the             name of resource
     * @return The minified resource
     */
    Resource minify(Resource originalResource, String filename);

}
