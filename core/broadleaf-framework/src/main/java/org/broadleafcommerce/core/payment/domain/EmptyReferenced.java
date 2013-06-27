/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.payment.domain;

import org.broadleafcommerce.common.encryption.EncryptionModule;

/**
 * @author Jeff Fischer
 */
public class EmptyReferenced implements Referenced {
    
    private String referenceNumber;
    
    @Override
    public EncryptionModule getEncryptionModule() {
        return null;
    }

    @Override
    public String getReferenceNumber() {
        return referenceNumber;
    }

    @Override
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    @Override
    public void setEncryptionModule(EncryptionModule encryptionModule) {
        //do nothing
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long id) {
        //do nothing
    }
}
