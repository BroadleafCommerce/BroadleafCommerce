/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.service.persistence.module;

import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.server.service.handler.CustomPersistenceHandler;

import java.io.Serializable;
import java.util.List;

/**
 * Wrap calls to {@link BasicPersistenceModule#getPersistentRecords(String, List, Integer, Integer)},
 * {@link BasicPersistenceModule#getTotalRecords(String, List)}, and {@link BasicPersistenceModule#getRecords(FetchExtractionRequest)},
 * presumably to provide additional configuration or state modification before calling those methods on BasicPersistenceModule.
 *
 * @author Jeff Fischer
 */
public interface FetchWrapper {

    /**
     * Retrieve the records from the persistent store given the params in the {@link FetchRequest}. Presumably, modifications
     * or state manipulation may be performed to affect an alteration of the resulting SQL select syntax.
     *
     * @param fetchRequest
     * @return
     */
    List<Serializable> getPersistentRecords(FetchRequest fetchRequest);

    /**
     * Retrieve the total record count given the params in the {@link FetchRequest}. Presumably, modifications or
     * state manipulation may be performed to affect an alteration of the resulting SQL select count syntax.
     *
     * @param fetchRequest
     * @return
     */
    Integer getTotalRecords(FetchRequest fetchRequest);

    /**
     * Retrieve the dto representation of persistent records based on the record contents and entity metadata.
     *
     * @param fetchExtractionRequest
     * @return
     */
    Entity[] getRecords(FetchExtractionRequest fetchExtractionRequest);

}
