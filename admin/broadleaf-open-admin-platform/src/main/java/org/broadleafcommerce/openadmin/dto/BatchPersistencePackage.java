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

package org.broadleafcommerce.openadmin.dto;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Jeff Fischer
 */
public class BatchPersistencePackage implements Serializable {

    protected PersistencePackage[] persistencePackages;

    public PersistencePackage[] getPersistencePackages() {
        return persistencePackages;
    }

    public void setPersistencePackages(PersistencePackage[] persistencePackages) {
        this.persistencePackages = persistencePackages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BatchPersistencePackage)) return false;

        BatchPersistencePackage that = (BatchPersistencePackage) o;

        if (!Arrays.equals(persistencePackages, that.persistencePackages)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return persistencePackages != null ? Arrays.hashCode(persistencePackages) : 0;
    }
}
