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

package org.broadleafcommerce.openadmin.client.datasource.dynamic;

import com.gwtincubator.security.exception.ApplicationSecurityException;
import org.broadleafcommerce.openadmin.client.dto.BatchDynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.BatchPersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.service.AbstractCallback;
import org.broadleafcommerce.openadmin.client.service.AppServices;

import java.util.ArrayList;
import java.util.List;

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

    protected List<BatchPackage> batchPackages = new ArrayList<BatchPackage>();

    public void addBatchPackage(BatchPackage batchPackage) {
        this.batchPackages.add(batchPackage);
    }

    public List<BatchPackage> getBatchPackages() {
        return batchPackages;
    }

    public void setBatchPackages(List<BatchPackage> batchPackages) {
        this.batchPackages = batchPackages;
    }

    public void executeBatchRPC(final BatchSuccessHandler successHandler) {
        BatchOperationType batchOperationType = null;
        List<PersistencePackage> persistencePackages = new ArrayList<PersistencePackage>();
        for (BatchPackage batchPackage : batchPackages) {
            switch (batchPackage.getBatchOperationType()) {
                case INSPECT:
                    if (batchOperationType == null) {
                        batchOperationType = BatchOperationType.INSPECT;
                    } else if (batchOperationType != BatchOperationType.INSPECT) {
                        throw new IllegalArgumentException("Differing operation types detected in a single batch");
                    }
                    persistencePackages.add(batchPackage.getPersistencePackage());
                    break;
                default:
                    throw new IllegalArgumentException("Operation Type not supported: " + batchPackage.getBatchOperationType());
            }
        }
        BatchPersistencePackage batchPersistencePackage = new BatchPersistencePackage();
        batchPersistencePackage.setPersistencePackages(persistencePackages.toArray(new PersistencePackage[persistencePackages.size()]));
        AppServices.DYNAMIC_ENTITY.batchInspect(batchPersistencePackage, new AbstractCallback<BatchDynamicResultSet>() {

            @Override
            protected void onOtherException(Throwable exception) {
                try {
                    super.onOtherException(exception);
                    for (BatchPackage batchPackage : batchPackages) {
                        if (batchPackage.getAsyncCallback() != null) {
                            batchPackage.getAsyncCallback().onFailure(exception);
                            break;
                        }
                    }
                } finally {
                    batchPackages.clear();
                }
            }

            @Override
            protected void onSecurityException(ApplicationSecurityException exception) {
                try {
                    super.onSecurityException(exception);
                    for (BatchPackage batchPackage : batchPackages) {
                        if (batchPackage.getAsyncCallback() != null) {
                            batchPackage.getAsyncCallback().onFailure(exception);
                            break;
                        }
                    }
                } finally {
                    batchPackages.clear();
                }
            }

            @Override
            public void onSuccess(BatchDynamicResultSet result) {
                super.onSuccess(result);
                for (int j=0;j<batchPackages.size();j++) {
                    BatchPackage batchPackage = batchPackages.get(j);
                    if (batchPackage.getAsyncCallback() != null) {
                        batchPackage.getAsyncCallback().onSuccess(result.getDynamicResultSets()[j]);
                    }
                }
                batchPackages.clear();
                if (successHandler != null) {
                    successHandler.onSuccess();
                }
            }

        });
    }
}
