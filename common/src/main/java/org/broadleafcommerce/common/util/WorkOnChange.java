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
package org.broadleafcommerce.common.util;

import java.util.Collection;

/**
 * Encapsulate some amount of work to perform whenever a change aware collection is modified.
 *
 * @see org.broadleafcommerce.common.util.BLCCollectionUtils#createChangeAwareCollection(WorkOnChange, java.util.Collection)
 * @author Jeff Fischer
 */
public interface WorkOnChange {

    /**
     * An implementation of this method will be called whenever a change is detected on a change aware collection. The implementation
     * should contain whatever code is necessary to respond to the collection change.
     *
     * @param changed the un-proxied collection that was modified
     */
    void doWork(Collection changed);

}
