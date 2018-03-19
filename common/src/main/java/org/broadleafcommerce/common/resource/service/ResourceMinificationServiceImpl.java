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

import com.google.common.collect.ImmutableList;
import com.google.javascript.jscomp.AbstractCommandLineRunner;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.config.service.SystemPropertiesService;
import org.broadleafcommerce.common.resource.GeneratedResource;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

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

    @Value("${minify.closure.compiler.enabled}")
    protected boolean closureCompilerEnabled;

    protected CompilerOptions compilerOptions;

    protected SystemPropertiesService systemPropertiesService;

    @Autowired
    @Qualifier("blClosureCompilerOptions")
    public void setCompilerOptions(CompilerOptions compilerOptions) {
        this.compilerOptions = compilerOptions;
    }

    @Autowired
    @Qualifier("blSystemPropertiesService")
    public void setSystemPropertiesService(SystemPropertiesService systemPropertiesService) {
        this.systemPropertiesService = systemPropertiesService;
    }

    @Lookup
    protected Compiler getClosureCompiler() {
        return null;
    }

    @Override
    public boolean getEnabled() {
        return systemPropertiesService.resolveBooleanSystemProperty("minify.enabled");
    }

    @Override
    @Deprecated
    public boolean getAllowSingleMinification() {
        return systemPropertiesService.resolveBooleanSystemProperty("minify.allowSingleMinification");
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
        
        try {
            byte[] minifiedBytes = minifyResource(originalResource, filename, type);

            return new GeneratedResource(minifiedBytes, filename);
        } catch (Exception e) {
            LOG.warn(COULD_NOT_MINIFY_RESOURCES_RETURNED_UNMINIFIED_BYTES, e);

            return originalResource;
        }
    }

    @SneakyThrows
    protected byte[] minifyResource(Resource originalResource, String filename, String type) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(originalResource.getInputStream(), StandardCharsets.UTF_8));
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(baos, StandardCharsets.UTF_8))) {

            if (JS_TYPE.equals(type)) {
                return minifyJs(filename, reader, writer, baos);
            } else if (CSS_TYPE.equals(type)) {
                return minifyCss(reader, writer, baos);
            } else {
                throw new Exception("Unsupported minification resource: " + filename);
            }
        }
    }

    @SneakyThrows
    protected byte[] minifyJs(String filename, Reader reader, Writer writer, ByteArrayOutputStream baos) {
        if (closureCompilerEnabled) {
            SourceFile input = SourceFile.fromReader(filename, reader);

            String compiled = compileJs(input);

            if (StringUtils.isBlank(compiled)) {
                throw new Exception(COULD_NOT_MINIFY_RESOURCES_RETURNED_UNMINIFIED_BYTES);
            }

            writer.write(compiled);
        } else {
            JavaScriptCompressor jsc = new JavaScriptCompressor(reader, getLogBasedErrorReporter(filename));
            jsc.compress(writer, linebreak, munge, verbose, preserveAllSemiColons, disableOptimizations);
        }

        writer.flush();

        return baos.toByteArray();
    }

    @SneakyThrows
    protected String compileJs(SourceFile input) {
        Compiler compiler = getClosureCompiler();

        List<SourceFile> builtinExterns = AbstractCommandLineRunner.getBuiltinExterns(CompilerOptions.Environment.BROWSER);

        Result result = compiler.compile(builtinExterns, ImmutableList.of(input), compilerOptions);

        if(result.success) {
            return compiler.toSource();
        }

        throw new Exception(COULD_NOT_MINIFY_RESOURCES_RETURNED_UNMINIFIED_BYTES);
    }

    @SneakyThrows
    protected byte[] minifyCss(Reader reader, Writer writer, ByteArrayOutputStream baos) {
        CssCompressor cssc = new CssCompressor(reader);
        cssc.compress(writer, 100);

        writer.flush();
        return baos.toByteArray();
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
