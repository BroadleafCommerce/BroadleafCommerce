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
package org.broadleafcommerce.common.resource.service;

import org.springframework.core.io.Resource;

/**
 * Service responsible for interacting with YUI-compressor or Google Closure Compiler to minify JS/CSS resources.
 *
 * YUI-compressor is controlled via the following properties:
 *
 * <ul>
 *  <li>minify.enabled - whether or not to actually perform minification</li>
 *  <li>minify.linebreak - if set to a value other than -1, will enforce a linebreak at that value</li>
 *  <li>minify.munge - if true, will replace variable names with shorter versions</li>
 *  <li>minify.verbose - if true, will display extra logging information to the console</li>
 *  <li>minify.preserveAllSemiColons - if true, will never remove semi-colons, even if two in a row exist</li>
 *  <li>minify.disableOptimizations - if true, will disable some micro-optimizations that are performed</li>
 * </ul>
 *
 * The Google Closure Compiler uses to minify only JS resources.
 * To use it instead of the YUI-compressor, add the property: minify.closure.compiler.enabled=true
 *
 * Compiler is controlled via the following properties:
 * <ul>
 *  <li>minify.closure.compiler.languageIn - Sets ECMAScript version to use for the input.
 *  Options: ECMASCRIPT3, ECMASCRIPT5, ECMASCRIPT5_STRICT, "
 * "ECMASCRIPT6_TYPED (experimental), ECMASCRIPT_2015, ECMASCRIPT_2016, "
 * "ECMASCRIPT_2017, ECMASCRIPT_NEXT</li>
 *
 *  <li>minify.closure.compiler.languageOut - Sets ECMAScript version to use for the output.
 *  Options: ECMASCRIPT3, ECMASCRIPT5, ECMASCRIPT5_STRICT, "
 * "ECMASCRIPT6_TYPED (experimental), ECMASCRIPT_2015, ECMASCRIPT_2016, "
 * "ECMASCRIPT_2017, ECMASCRIPT_NEXT, NO_TRANSPILE</li>
 *
 *  <li>minify.closure.compiler.warningLevel - Warnings level. Possible values: QUIET, DEFAULT, VERBOSE</li>
 * </ul>
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
    public byte[] minify(String filename, byte[] bytes);

    /**
     * Indicates whether or not the system is allowed to minify bundled resources.
     * 
     * @return the value of the system property "minify.enabled"
     */
    public boolean getEnabled();

    /**
     * Indicates whether or not the system is allowed to attempt to minify individual files. This can be useful if
     * the YUI compressor is failing to minify JavaScript/CSS due to syntax errors and you are attempting to track
     * down which file is problematic. It should not be enabled in a production environment.
     *
     * @deprecated
     * @return the value of the system property "minify.allowSingleMinification"
     */
    @Deprecated
    boolean getAllowSingleMinification();

    /**
     * Delegates to {@link #minify(Resource, String)} where the filename argument is originalResource.getFilename().
     * @param originalResource
     * @return
     */
    Resource minify(Resource originalResource);

    /**
     * Given a {@link Resource}, will return a resource that represents the minified version.
     * 
     * @param orginalResource
     * @param the name of resource
     * 
     * @return The minified resource
     */
    Resource minify(Resource originalResource, String filename);

}
