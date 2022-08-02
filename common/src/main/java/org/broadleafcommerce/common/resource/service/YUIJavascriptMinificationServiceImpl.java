/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2022 Broadleaf Commerce
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.stereotype.Service;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/** 
 * Javascript minification service implemented using the YUICompressor library
 * This will be used for minification if the Google Closure Compiler dependency is not included in the project
 * 
 * Properties used by YUICompressor for JS minification
 * 
 * <ul>
 *  <li>minify.linebreak - if set to a value other than -1, will enforce a linebreak at that value<br/>
 *      Default: -1
 *  </li>
 *  <li>minify.munge - if true, will replace variable names with shorter versions<br/>
 *      Default: true
 *  </li>
 *  <li>minify.verbose - if true, will display extra logging information to the console<br/>
 *      Default: false
 *  </li>
 *  <li>minify.preserveAllSemiColons - if true, will never remove semi-colons, even if two in a row exist<br/>
 *      Default: true
 *  </li>
 *  <li>minify.disableOptimizations - if true, will disable some micro-optimizations that are performed<br/>
 *      Default: false
 *  </li>
 * </ul>
 * 
 * @author Jay Aisenbrey (cja769)
 * 
 **/
@Service("blJavascriptMinificationService")
@ConditionalOnMissingClass(value = "com.google.javascript.jscomp.Compiler")
public class YUIJavascriptMinificationServiceImpl implements JavascriptMinificationService {

    protected static final Log LOG = LogFactory.getLog(YUIJavascriptMinificationServiceImpl.class);

    @Value("${minify.linebreak}")
    protected int linebreak;

    @Value("${minify.munge}")
    protected boolean munge;

    @Value("${minify.verbose}")
    protected boolean verbose;

    @Value("${minify.preserveAllSemiColons}")
    protected boolean preserveAllSemiColons;

    @Value("${minify.disableOptimizations}")
    protected boolean disableOptimizations;

    @Override
    public void minifyJs(String filename, Reader reader, Writer writer) throws ResourceMinificationException {
        try {
            JavaScriptCompressor jsc = new JavaScriptCompressor(reader, getLogBasedErrorReporter(filename));
            jsc.compress(writer, linebreak, munge, verbose, preserveAllSemiColons, disableOptimizations);
        } catch (IOException e) {
            throw new ResourceMinificationException("Error minifying js file " + filename, e);
        }
    }

    protected ErrorReporter getLogBasedErrorReporter(final String filename) {
        return new ErrorReporter() {

            @Override
            public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
                if (line < 0) {
                    LOG.warn(message);
                } else {
                    if (sourceName == null) {
                        sourceName = filename;
                    }
                    LOG.warn(sourceName + " - " + lineSource + " - " + line + ':' + lineOffset + " - " + message);
                }
            }

            @Override
            public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
                if (line < 0) {
                    LOG.error(message);
                } else {
                    if (sourceName == null) {
                        sourceName = filename;
                    }
                    LOG.error(sourceName + " - " + lineSource + " - " + line + ':' + lineOffset + " - " + message);
                }
            }

            @Override
            public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource,
                                                   int lineOffset) {
                if (sourceName == null) {
                    sourceName = filename;
                }
                error(message, sourceName, line, lineSource, lineOffset);
                return new EvaluatorException(message);
            }

        };
    }
}
