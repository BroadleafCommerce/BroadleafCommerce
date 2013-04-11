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

package org.broadleafcommerce.openadmin.client.datasource.dynamic;

import com.gwtincubator.security.exception.ApplicationSecurityException;
import org.broadleafcommerce.openadmin.client.dto.BatchDynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.BatchPersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.service.AbstractCallback;
import org.broadleafcommerce.openadmin.client.service.AppServices;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
public class BatchManager {

    private static BatchManager manager = null;

    public static BatchManager getInstance() {
        if (manager == null) {
            manager = new BatchManager();
        }

        return manager;
    }

    protected Map<Integer, BatchPackage> batchPackages = new LinkedHashMap<Integer, BatchPackage>();
    protected Map<Integer, BatchPackage> sentPackages = new LinkedHashMap<Integer, BatchPackage>();

    public void addBatchPackage(BatchPackage batchPackage) {
        this.batchPackages.put(batchPackage.getBatchPackageId(), batchPackage);
    }

    public Map<Integer, BatchPackage> getBatchPackages() {
        return batchPackages;
    }

    public void setBatchPackages(Map<Integer, BatchPackage> batchPackages) {
        this.batchPackages = batchPackages;
    }

    public void executeBatchRPC(final BatchSuccessHandler successHandler) {
        BatchOperationType batchOperationType = null;
        List<PersistencePackage> persistencePackages = new ArrayList<PersistencePackage>();
        Iterator<Map.Entry<Integer, BatchPackage>> itr = batchPackages.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<Integer, BatchPackage> entry = itr.next();
            itr.remove();
            switch (entry.getValue().getBatchOperationType()) {
                case INSPECT:
                    if (batchOperationType == null) {
                        batchOperationType = BatchOperationType.INSPECT;
                    } else if (batchOperationType != BatchOperationType.INSPECT) {
                        throw new IllegalArgumentException("Differing operation types detected in a single batch");
                    }
                    PersistencePackage persistencePackage = entry.getValue().getPersistencePackage();
                    persistencePackage.setBatchId(entry.getValue().getBatchPackageId());
                    persistencePackages.add(persistencePackage);
                    break;
                default:
                    throw new IllegalArgumentException("Operation Type not supported: " + entry.getValue().getBatchOperationType());
            }
            sentPackages.put(entry.getKey(), entry.getValue());
        }
        BatchPersistencePackage batchPersistencePackage = new BatchPersistencePackage();
        batchPersistencePackage.setPersistencePackages(persistencePackages.toArray(new PersistencePackage[persistencePackages.size()]));
        AppServices.DYNAMIC_ENTITY.batchInspect(batchPersistencePackage, new AbstractCallback<BatchDynamicResultSet>() {

            @Override
            protected void onOtherException(Throwable exception) {
                try {
                    super.onOtherException(exception);
                    for (Map.Entry<Integer, BatchPackage> entry : batchPackages.entrySet()) {
                        if (entry.getValue().getAsyncCallback() != null) {
                            entry.getValue().getAsyncCallback().onFailure(exception);
                            break;
                        }
                    }
                } finally {
                    batchPackages.clear();
                    sentPackages.clear();
                }
            }

            @Override
            protected void onSecurityException(ApplicationSecurityException exception) {
                try {
                    super.onSecurityException(exception);
                    for (Map.Entry<Integer, BatchPackage> entry : batchPackages.entrySet()) {
                        if (entry.getValue().getAsyncCallback() != null) {
                            entry.getValue().getAsyncCallback().onFailure(exception);
                            break;
                        }
                    }
                } finally {
                    batchPackages.clear();
                    sentPackages.clear();
                }
            }

            @Override
            public void onSuccess(BatchDynamicResultSet result) {
                super.onSuccess(result);
                for (DynamicResultSet dynamicResultSet : result.getDynamicResultSets()) {
                    if (!sentPackages.containsKey(dynamicResultSet.getBatchId())) {
                        throw new RuntimeException("Unable to find a BatchPackage that matches the DynamicResultSet at batchId: " + dynamicResultSet.getBatchId());
                    }
                    BatchPackage batchPackage = sentPackages.remove(dynamicResultSet.getBatchId());
                    if (batchPackage.getAsyncCallback() != null) {
                        batchPackage.getAsyncCallback().onSuccess(dynamicResultSet);
                    }
                }
                if (successHandler != null) {
                    successHandler.onSuccess();
                }
            }

        });
    }
}
