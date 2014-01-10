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
package org.broadleafcommerce.common.dialect;

import org.hibernate.dialect.MySQL5InnoDBDialect;

/**
 * Intended to allow installations migrating from BLC version 2.0 to not be forced to make a schema
 * change for boolean fields when migrating to BLC version 3.0, and above.
 *
 * @deprecated use org.hibernate.dialect.MySQL5InnoDBDialect instead
 * @author Jeff Fischer
 */
@Deprecated
public class Broadleaf2CompatibilityMySQL5InnoDBDialect extends MySQL5InnoDBDialect {

    public Broadleaf2CompatibilityMySQL5InnoDBDialect() {
        super();
        registerColumnType( java.sql.Types.BOOLEAN, "bit" );
    }

}
