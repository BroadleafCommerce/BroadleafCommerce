/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.broadleafcommerce.openadmin.client.dto.BatchDynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.BatchPersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.CriteriaTransferObject;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.springframework.security.access.annotation.Secured;

/**
 * Asynchronous version of {@link EntityService}.
 * 
 * @author jfischer
 */
public interface DynamicEntityServiceAsync {
    
    void inspect(PersistencePackage persistencePackage, AsyncCallback<DynamicResultSet> cb);
    
    void fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto, AsyncCallback<DynamicResultSet> cb);
    
    void add(PersistencePackage persistencePackage, AsyncCallback<Entity> cb);
    
    void update(PersistencePackage persistencePackage, AsyncCallback<Entity> cb);
    
    void remove(PersistencePackage persistencePackage, AsyncCallback<Void> cb);

    void batchInspect(BatchPersistencePackage persistencePackage, AsyncCallback<BatchDynamicResultSet> async);
}
