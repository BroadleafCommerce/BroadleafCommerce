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

import org.springframework.stereotype.Service;

import com.yahoo.platform.yui.compressor.CssCompressor;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * CSS minification service implemented using the YUICompressor library
 *
 * @author Jay Aisenbrey (cja769)
 */
@Service("blCssMinificationService")
public class YUICssMinificationServiceImpl implements CssMinificationService {

    @Override
    public void minifyCss(String filename, Reader reader, Writer writer) throws ResourceMinificationException {
        try {
            CssCompressor cssc = new CssCompressor(reader);
            cssc.compress(writer, 100);
        } catch (IOException e) {
            throw new ResourceMinificationException("Error minifiying css file " + filename, e);
        }
    }

}
