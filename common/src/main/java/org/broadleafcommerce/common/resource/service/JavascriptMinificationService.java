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

import java.io.Reader;
import java.io.Writer;

/**
 * Service that's used to minify Javascript
 * 
 * @see YUIJavascriptMinificationServiceImpl
 * @see GoogleClosureJavascriptMinificationServiceImpl
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
public interface JavascriptMinificationService {

    /**
     * Minifies the file in "reader" and writes the minified version to "writer"
     * 
     * @param filename Name of the file to be minified
     * @param reader The original file
     * @param writer The writer that the minified version of the file in "reader" will be written to
     * @throws ResourceMinificationException Failure to successfully minify the bytes in "reader"
     */
    public void minifyJs(String filename, Reader reader, Writer writer) throws ResourceMinificationException;
}
