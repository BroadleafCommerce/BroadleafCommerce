/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * Deferred import loader that, when used in an {@link @Configuration} class, executes _after_ the set
 * of Spring {@link EnableAutoConfiguration} classes brought in by the {@Link AutoConfigurationImportSelector}.
 * 
 * <p>
 * Particularly useful when you want to do something in absence of autoconfiguration where Broadleaf is still
 * required to have a particular component. This allows you to confidently use annotations like {@code @ConditionalOnMissingBean}
 * to only create things if Spring autoconfiguration didn't already.
 * 
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link PostAutoConfigurationImport}
 * @see {@link org.springframework.context.annotation.ConfigurationClassParser}
 */
public class PostAutoConfigurationDefferedImportSelector implements DeferredImportSelector {

    @Override
    @SuppressWarnings("unchecked")
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        MultiValueMap<String, Object> attributes = 
            importingClassMetadata.getAllAnnotationAttributes(PostAutoConfigurationImport.class.getName(), true);
        Object importAttributes = attributes.get("value");
        List<String> imports = new ArrayList<>();
        for (String[] classes : (List<String[]>) importAttributes) {
            imports.addAll(Arrays.asList(classes));
        }
        return imports.toArray(new String[imports.size()]);
    }
}
