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

import com.google.common.io.CharStreams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.javascript.jscomp.AbstractCommandLineRunner;
import com.google.javascript.jscomp.ClosureCodingConvention;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.WarningLevel;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

/**
 * Javascript minification service implemented using the Google Closure Compiler
 * This will be used for minification if the Google Closure Compiler dependency is included in the project
 * 
 * This library also supports extra aggressive minification, transpiling, code optimizing, and bundleing
 * 
 * <ul>
 *  <li>minify.closure.compiler.languageIn - Sets ECMAScript version to use for the input.<br/>
 *  Options: ECMASCRIPT3, ECMASCRIPT5, ECMASCRIPT5_STRICT,
 *  ECMASCRIPT6_TYPED (experimental), ECMASCRIPT_2015, ECMASCRIPT_2016,
 *  ECMASCRIPT_2017, ECMASCRIPT_NEXT<br/>
 *  Default: ECMASCRIPT5
 *  </li>
 *
 *  <li>minify.closure.compiler.languageOut - Sets ECMAScript version to use for the output.<br/>
 *  Options: ECMASCRIPT3, ECMASCRIPT5, ECMASCRIPT5_STRICT,
 *  ECMASCRIPT6_TYPED (experimental), ECMASCRIPT_2015, ECMASCRIPT_2016,
 *  ECMASCRIPT_2017, ECMASCRIPT_NEXT, NO_TRANSPILE<br/>
 *  Default: NO_TRANSPILE</li>
 *
 *  <li>minify.closure.compiler.warningLevel - Warnings level. Possible values: QUIET, DEFAULT, VERBOSE<br/>
 *  Default: SILENT</li>
 * </ul>
 * 
 * @author Jay Aisenbrey (cja769)
 *
 */
@Service("blJavascriptMinificationService")
public class GoogleClosureJavascriptMinificationServiceImpl implements JavascriptMinificationService {

    @Value("${minify.closure.compiler.warningLevel:QUIET}")
    protected String warningLevel;
    
    protected CompilerOptions.LanguageMode languageIn;
    protected CompilerOptions.LanguageMode languageOut;
    
    public GoogleClosureJavascriptMinificationServiceImpl(@Value("${minify.closure.compiler.languageIn:ECMASCRIPT5}") String compilerLanguageIn, 
                                                          @Value("${minify.closure.compiler.languageOut:NO_TRANSPILE}") String compilerLanguageOut) {
        this.languageIn = null;
        this.languageOut = null;
        if (StringUtils.isNotBlank(compilerLanguageIn)) {
            this.languageIn = CompilerOptions.LanguageMode.valueOf(compilerLanguageIn);
            this.languageOut = this.languageIn;
        }

        if (StringUtils.isNoneBlank(compilerLanguageOut)) {
            this.languageOut = CompilerOptions.LanguageMode.valueOf(compilerLanguageOut);
            if (this.languageIn == null) {
                this.languageIn = this.languageOut; 
            }
        }

        if (this.languageIn == null) {
            throw new IllegalArgumentException("Please set properties \"minify.closure.compiler.languageIn\" or \"minify.closure.compiler.languageOut\" if you wish to use Google Closure Compiler for Javascript minification");
        }
    }
    
    @Override
    public void minifyJs(String filename, Reader reader, Writer writer) throws ResourceMinificationException {
        try {
            SourceFile input = SourceFile.fromCode(filename, CharStreams.toString(reader));
            String compiled = compileJs(input, filename);
            writer.write(compiled);
        } catch (IOException e) {
            throw new ResourceMinificationException("Error minifying js file " + filename, e);
        }
    }

    protected String compileJs(SourceFile input, String filename) throws ResourceMinificationException, IOException {
        Compiler compiler = getCompiler();
        List<SourceFile> builtinExterns = AbstractCommandLineRunner.getBuiltinExterns(CompilerOptions.Environment.CUSTOM);
        Result result = compiler.compile(builtinExterns, Arrays.asList(input), getCompilerOptions());
        String compiled = compiler.toSource();
        if (!result.success || StringUtils.isBlank(compiled)) {
            StringBuilder errorString = new StringBuilder("\n");
            if (result.errors != null) {
                for (int i = 0; i < result.errors.size(); i++) {
                    errorString.append(result.errors.get(i).getDescription())
                            .append("\n");
                }
            }
            throw new ResourceMinificationException("Error minifying js file " + filename + errorString);
        }
        return compiler.toSource();
    }

    protected Compiler getCompiler() {
        return new Compiler();
    }
    
    protected CompilerOptions getCompilerOptions() {
        CompilerOptions options = new CompilerOptions();
        options.setLanguageIn(this.languageIn);
        options.setLanguageOut(this.languageOut);
        options.setCodingConvention(new ClosureCodingConvention());
        CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
        WarningLevel.valueOf(this.warningLevel).setOptionsForWarningLevel(options);
        options.setClosurePass(true);
        options.skipAllCompilerPasses();
        return options;
    }
    
}
