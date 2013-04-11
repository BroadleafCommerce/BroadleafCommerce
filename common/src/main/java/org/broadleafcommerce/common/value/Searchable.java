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

package org.broadleafcommerce.common.value;

import java.io.Serializable;

/**
 * Describes a class that contains searchable information. Can be used by the framework search engine to create
 * search indexes and indicate that information in this class should be searched for search terms during actual
 * searches.
 *
 * @author Jeff Fischer
 */
public interface Searchable<T extends Serializable> extends ValueAssignable<T> {

    /**
     * Whether or not this class contains searchable information
     *
     * @return Whether or not this class contains searchable information
     */
    Boolean getSearchable();

    /**
     * Whether or not this class contains searchable information
     *
     * @param searchable Whether or not this class contains searchable information
     */
    void setSearchable(Boolean searchable);

}
