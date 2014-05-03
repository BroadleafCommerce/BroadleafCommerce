/*
 * #%L
 * broadleaf-enterprise
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
package org.broadleafcommerce.openadmin.server.service.module;

import org.broadleafcommerce.openadmin.dto.PersistencePackage;

import java.util.Map;

/**
 * Useful in concert with the enterprise module. Keeps track of references for ADD operations across sandboxes.
 *
 * @author Jeff Fischer
 */
public interface ReconstituteForAddHandler {

    /**
     * Must rematch up any items that are dependent on a previous add. For example,
     * if you add a product, and then add a cross sale product, the cross sale will
     * have an incorrect reference to the product state before the replay unless
     * you update the cross sale to the new product value.
     *
     * @param library holds any previous adds
     * @param replayPackage the change request
     */
    void reconstitutePreviousAddForReplay(Map<Class<?>, Map<String,String>> library, PersistencePackage replayPackage);

    /**
     * Must rematch up any items that are dependent on a previous add. For example,
     * if you add an offer and promote, and then another user edits a property on this
     * add, the edit will have an incorrect reference to the offer state before the
     * replay unless you update the edit to the new offer value.
     *
     * @param library holds any previous adds
     * @param replayPackage the change request
     */
    void reconstitutePreviousAddForUpdateReplay(Map<Class<?>, Map<String, String>> library, PersistencePackage replayPackage);

}
