/*
 * #%L
 * broadleaf-enterprise
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.service.persistence;

import org.broadleafcommerce.openadmin.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.server.service.type.FetchType;

/**
 * Detect the type of fetch and paging being used for a particular admin pipeline fetch request
 * </p>
 * Also, {@link #shouldPromptForSearch(PersistencePackage, CriteriaTransferObject)} designates whether this fetch
 * request should be considered empty and instead prompt the user to enter a search term in the listgrid before
 * retrieving records. This save a wasted default retrieval delay, which could be impactful.
 *
 * @author Jeff Fischer
 */
public interface FetchTypeDetection {

    FetchType getFetchType(PersistencePackage persistencePackage, CriteriaTransferObject cto);

    boolean shouldPromptForSearch(PersistencePackage persistencePackage, CriteriaTransferObject cto);

}
