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
package org.broadleafcommerce.common.extensibility.jpa.copy;

import org.broadleafcommerce.common.extensibility.jpa.convert.BroadleafClassTransformer;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Properties;

/**
 * This class transformer will do nothing. The main use case for this transformer is when you would prefer to not
 * have a module's template classes copied over, and would rather do it yourself. 
 * 
 * This transformer should not typically be used.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class NullClassTransformer implements BroadleafClassTransformer {
    
    @Override
    public void compileJPAProperties(Properties props, Object key) throws Exception {
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, 
            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        return null;
    }

}

