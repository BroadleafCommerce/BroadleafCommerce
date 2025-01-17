/*-
 * #%L
 * BroadleafCommerce Common Enterprise
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
package org.broadleafcommerce.common.extensibility.jpa.hibernate;

import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyIgnorePattern;
import org.hibernate.bytecode.enhance.spi.EnhancementContext;
import org.hibernate.jpa.internal.enhance.EnhancingClassTransformerImpl;

import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.spi.TransformerException;

/**
 * This is the override of Hibernate transformer that adds filtration based on class/package name to prevernt
 * parsing unwanted classes
 */
public class BroadleafHibernateEnhancingClassTransformerImpl extends EnhancingClassTransformerImpl {

    private List<DirectCopyIgnorePattern> ignorePatterns;

    public BroadleafHibernateEnhancingClassTransformerImpl(EnhancementContext enhancementContext) {
        super(enhancementContext);
    }

    @Override
    public byte[] transform(
            ClassLoader loader,
            String className,
            Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain,
            byte[] classfileBuffer
    ) throws TransformerException {
        String convertedClassName = className.replace('/', '.');
        boolean isValidPattern = true;
        List<DirectCopyIgnorePattern> matchedPatterns = new ArrayList<DirectCopyIgnorePattern>();
        for (DirectCopyIgnorePattern pattern : ignorePatterns) {
            boolean isPatternMatch = false;
            for (String patternString : pattern.getPatterns()) {
                isPatternMatch = convertedClassName.matches(patternString);
                if (isPatternMatch) {
                    break;
                }
            }
            if (isPatternMatch) {
                matchedPatterns.add(pattern);
            }
            isValidPattern = !(isPatternMatch && pattern.getTemplateTokenPatterns() == null);
            if (!isValidPattern) {
                break;
            }
        }

        if (isValidPattern) {
            return super.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
        }
        return null;
    }

    public void setIgnorePatterns(List<DirectCopyIgnorePattern> ignorePatterns) {
        this.ignorePatterns = ignorePatterns;
    }

}
