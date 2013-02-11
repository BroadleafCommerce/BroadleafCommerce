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

package org.broadleafcommerce.openadmin.web.service;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetCollectionMetadata;
import org.broadleafcommerce.openadmin.client.dto.AdornedTargetList;
import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.component.RuleBuilder;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;

import com.gwtincubator.security.exception.ApplicationSecurityException;

import java.util.Map;

/**
 * @author Andre Azzolini (apazzolini)
 */
public interface FormBuilderService {

    public ListGrid buildListGrid(ClassMetadata cmd, Entity[] entities);

    public RuleBuilder buildRuleBuilder(ClassMetadata cmd, Entity[] entities, String[] ruleVars, String[] configKeys);

    public EntityForm buildEntityForm(ClassMetadata cmd, Entity e, Map<String, Entity[]> subCollections)
            throws ClassNotFoundException, ServiceException, ApplicationSecurityException;

    public EntityForm buildEntityForm(ClassMetadata cmd);

    public EntityForm buildAdornedListForm(AdornedTargetCollectionMetadata adornedMd, AdornedTargetList adornedList,
            String parentId)
            throws ServiceException, ApplicationSecurityException;

    public ListGrid buildAdornedListGrid(AdornedTargetCollectionMetadata fmd, ClassMetadata cmd, Entity[] entities);

}