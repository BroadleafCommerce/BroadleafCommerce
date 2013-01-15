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
import org.broadleafcommerce.openadmin.client.dto.FilterAndSortCriteria;
import org.broadleafcommerce.openadmin.client.dto.Property;

import com.gwtincubator.security.exception.ApplicationSecurityException;

import java.util.Map;

/**
 * @author Andre Azzolini (apazzolini)
 */
public interface AdminEntityService {

    public ClassMetadata getClassMetadata(Class<?> clazz) throws ServiceException, ApplicationSecurityException;

    public Entity[] getRecords(Class<?> clazz, FilterAndSortCriteria... fascs) throws ServiceException, ApplicationSecurityException;

    public Entity getRecord(Class<?> clazz, String id) throws ServiceException, ApplicationSecurityException;

    public Entity updateEntity(Entity entity, Class<?> clazz) throws ServiceException, ApplicationSecurityException;

    public Map<String, Entity[]> getSubRecords(Class<?> clazz, String containingEntityId, ClassMetadata metadata) throws ServiceException, ApplicationSecurityException;

    Property[] getDisplayProperties(ClassMetadata metadata, Entity entity);




}