/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.common.resource.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.resource.GeneratedResource;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * @see ResourceMinificationService 
 * @author Andre Azzolini (apazzolini)
 */
@Service("blResourceMinificationService")
public class ResourceMinificationServiceImpl implements ResourceMinificationService {
    protected static final Log LOG = LogFactory.getLog(ResourceMinificationServiceImpl.class);

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
    public boolean getEnabled() {
        return BLCSystemProperty.resolveBooleanSystemProperty("minify.enabled");
    }

    @Override
    public boolean getAllowSingleMinification() {
        return BLCSystemProperty.resolveBooleanSystemProperty("minify.allowSingleMinification");
    }
    
    @Override
    public byte[] minify(String filename, byte[] bytes) {
        Resource modifiedResource = minify(new ByteArrayResource(bytes), filename);
         
        if (modifiedResource instanceof GeneratedResource) {
            return ((GeneratedResource) modifiedResource).getBytes();
        } else {
            return bytes;
        }
    }

    @Override
    public Resource minify(Resource originalResource) {
        if (originalResource.getFilename() == null) {
            LOG.warn("Attempted to modify resource without a filename, returning non-minified resource");
            return originalResource;
        }
        return minify(originalResource, originalResource.getFilename());
    }

    @Override
    public Resource minify(Resource originalResource, String filename) {
        if (true) {
            return originalResource;
        }
        String type = null;
        if (filename.contains(".js")) {
            type = "js";
        } else if (filename.contains(".css")) {
            type = "css";
        } else {
            // Unsupported minification type
            return originalResource;
        }
        
        byte[] minifiedBytes = null;
        try (BufferedReader in =
                new BufferedReader(new InputStreamReader(originalResource.getInputStream(), "utf-8"));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedWriter out =
                        new BufferedWriter(new OutputStreamWriter(baos, "utf-8"));) {

            if ("js".equals(type)) {
                JavaScriptCompressor jsc = new JavaScriptCompressor(in, getLogBasedErrorReporter());
                jsc.compress(out, linebreak, munge, verbose, preserveAllSemiColons, disableOptimizations);
            } else if ("css".equals(type)) {
                CssCompressor cssc = new CssCompressor(in);
                cssc.compress(out, 100);
            }
            out.flush();
            minifiedBytes = baos.toByteArray();
        } catch (Exception e) {
            LOG.warn("Could not minify resources, returned unminified bytes", e);
            return originalResource;
        }
        
        return new GeneratedResource(minifiedBytes, filename);
    }
    
    protected ErrorReporter getLogBasedErrorReporter() {
        return new ErrorReporter() {
            @Override
            public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
                if (line < 0) {
                    LOG.warn(message);
                } else {
                    LOG.warn(line + ':' + lineOffset + ':' + message);
                }
            }

            @Override
            public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
                if (line < 0) {
                    LOG.error(message);
                } else {
                    LOG.error(line + ':' + lineOffset + ':' + message);
                }
            }

            @Override
            public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, 
                    int lineOffset) {
                error(message, sourceName, line, lineSource, lineOffset);
                return new EvaluatorException(message);
            }
            
        };
    }
}
