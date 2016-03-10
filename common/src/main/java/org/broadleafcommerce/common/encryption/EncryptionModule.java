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
package org.broadleafcommerce.common.encryption;

/**
 * Basic extension point for modules handling encryption.
 */
public interface EncryptionModule {

    /**
     * Encrypt a text string
     * @param plainText
     * @return
     */
    public String encrypt(String plainText);

    /**
     * Decrypt a encrypted string
     * @param cipherText
     * @return
     */
    public String decrypt(String cipherText);

    /**
     * Check to see if a text string matches a generated encrypted token.
     * This is useful for encoders that always generate a unique hash.
     * @param raw
     * @param encrypted
     * @return
     */
    public Boolean matches(String raw, String encrypted);

}
