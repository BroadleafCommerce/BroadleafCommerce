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
package org.broadleafcommerce.common.encryption;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.config.RuntimeEnvironmentKeyResolver;
import org.broadleafcommerce.common.config.SystemPropertyRuntimeEnvironmentKeyResolver;

/**
 * The default encryption module simply passes through the decrypt and encrypt text.
 * A real implementation should adhere to PCI compliance, which requires robust key
 * management, including regular key rotation. An excellent solution would be a module
 * for interacting with the StrongKey solution. Refer to this discussion:
 *
 * http://www.strongauth.com/forum/index.php?topic=44.0
 *
 * @author jfischer
 *
 */
public class PassthroughEncryptionModule implements EncryptionModule {

    private static final Log LOG = LogFactory.getLog(PassthroughEncryptionModule.class);

    protected RuntimeEnvironmentKeyResolver keyResolver = new SystemPropertyRuntimeEnvironmentKeyResolver();

    public PassthroughEncryptionModule() {
        if ("production".equals(keyResolver.resolveRuntimeEnvironmentKey())) {
            LOG.warn("This passthrough encryption module provides NO ENCRYPTION and should NOT be used in production.");
        }
    }

    @Override
    public String decrypt(String cipherText) {
        return cipherText;
    }

    @Override
    public String encrypt(String plainText) {
        return plainText;
    }

    @Override
    public Boolean matches(String raw, String encrypted) {
        return encrypted.equals(encrypt(raw));
    }
}
