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
/**
 * 
 */
package org.broadleafcommerce.common.config;

import com.google.javascript.jscomp.ClosureCodingConvention;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.WarningLevel;
import com.google.javascript.jscomp.parsing.parser.FeatureSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

/**
 * Main configuration class for the broadleaf-common module
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Configuration
public class BroadleafCommonConfig {

    @Value("${minify.closure.compiler.languageIn:ECMASCRIPT_2017}")
    protected String compilerLanguageIn;
    @Value("${minify.closure.compiler.languageOut:ECMASCRIPT5}")
    protected String compilerLanguageOut;
    @Value("${minify.closure.compiler.warningLevel:QUIET}")
    protected String warningLevel;

    /**
     * Other enterprise/mulititenant modules override this adapter to provide one that supports dynamic filtration
     */
    @Bean
    @ConditionalOnMissingBean(name = "blJpaVendorAdapter")
    public JpaVendorAdapter blJpaVendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        //TODO see https://jira.spring.io/browse/SPR-13269. Since we're still on Hibernate 4.1, we want to revert to the previous
        // Spring behavior, which was not to prepare the connection. This avoids some warnings and extra connection acquisitions
        // for read only transactions. When we advance Hibernate, we should look at not blocking Spring's connection preparation.
        vendorAdapter.setPrepareConnection(false);
        return vendorAdapter;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Compiler blClosureCompiler() {
        return new Compiler();
    }

    @Bean
    public CompilerOptions blClosureCompilerOptions() {
        CompilerOptions options = new CompilerOptions();

        options.setCodingConvention(new ClosureCodingConvention());

        if (StringUtils.isNoneBlank(compilerLanguageIn)) {
            CompilerOptions.LanguageMode languageIn = CompilerOptions.LanguageMode.valueOf(compilerLanguageIn);
            if (StringUtils.isBlank(compilerLanguageOut)) {
                options.setLanguage(languageIn);
            } else {
                options.setLanguageIn(languageIn);
            }
        }

        if(StringUtils.isNoneBlank(compilerLanguageOut)) {
            options.setLanguageOut(CompilerOptions.LanguageMode.valueOf(compilerLanguageOut));
        }

        CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);

        WarningLevel.valueOf(warningLevel).setOptionsForWarningLevel(options);

        options.setClosurePass(true);
        options.setRewritePolyfills(toFeatureSet(options.getLanguageIn()).contains(FeatureSet.ES6));
        options.setStrictModeInput(true);

        return options;
    }

    protected FeatureSet toFeatureSet(CompilerOptions.LanguageMode languageMode) {
        switch (languageMode) {
            case ECMASCRIPT3:
                return FeatureSet.ES3;
            case ECMASCRIPT5:
            case ECMASCRIPT5_STRICT:
                return FeatureSet.ES5;
            case ECMASCRIPT_2015:
                return FeatureSet.ES6_MODULES;
            case ECMASCRIPT_2016:
                return FeatureSet.ES7_MODULES;
            case ECMASCRIPT_2017:
                return FeatureSet.ES8_MODULES;
            case ECMASCRIPT_NEXT:
                return FeatureSet.ES_NEXT;
            case ECMASCRIPT6_TYPED:
                return FeatureSet.TYPESCRIPT;
            case NO_TRANSPILE:
                throw new IllegalStateException();
        }

        throw new IllegalStateException();
    }

}
