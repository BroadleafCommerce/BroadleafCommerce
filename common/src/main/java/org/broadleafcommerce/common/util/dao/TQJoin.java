/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.common.util.dao;

/**
 * Specify the attributes of a JOIN that should appear in the TypedQuery. Generally takes the form of:
 * </p>
 * <pre>
 * {@code
 * TypedQueryBuilder builder = new TypedQueryBuilder(com.MyClass, "item")
     .addJoin(new TQJoin("item.collection", "collection"))
     .addRestriction("collection.id", "=", 1L);
 * }
 * </pre>
 * </p>
 * The alias value can be used in subsequent restriction expressions.
 *
 * @author Jeff Fischer
 */
public class TQJoin {

    protected String expression;
    protected String alias;

    public TQJoin(String expression, String alias) {
        this.expression = expression;
        this.alias = alias;
    }

    public String toQl() {
        StringBuilder sb = new StringBuilder();
        sb.append(expression);
        sb.append(" ");
        sb.append(alias);

        return sb.toString();
    }
}
