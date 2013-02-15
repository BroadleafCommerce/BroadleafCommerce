/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.service;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;

import com.gwtincubator.security.exception.ApplicationSecurityException;

import java.util.Map;

/**
 * @author Andre Azzolini (apazzolini)
 */
public interface AdminEntityService {

    public ClassMetadata getClassMetadata(String className)
            throws ServiceException, ApplicationSecurityException;

    public ClassMetadata getClassMetadata(PersistencePackageRequest request)
            throws ServiceException, ApplicationSecurityException;

    public Entity[] getRecords(PersistencePackageRequest request)
            throws ServiceException, ApplicationSecurityException;

    public Entity getRecord(String className, String id)
            throws ServiceException, ApplicationSecurityException;

    public Entity[] getRecordsForCollection(final ClassMetadata containingClassMetadata, final String containingEntityId,
            final Property collectionProperty)
            throws ServiceException, ApplicationSecurityException;

    public Map<String, Entity[]> getRecordsForAllSubCollections(final String containingClassName,
            final String containingEntityId)
            throws ServiceException, ApplicationSecurityException;

    public Entity addSubCollectionEntity(EntityForm entityForm, String className, String fieldName, String parentId)
            throws ServiceException, ApplicationSecurityException, ClassNotFoundException;

    public Entity updateEntity(EntityForm entityForm, String className)
            throws ServiceException, ApplicationSecurityException;

}