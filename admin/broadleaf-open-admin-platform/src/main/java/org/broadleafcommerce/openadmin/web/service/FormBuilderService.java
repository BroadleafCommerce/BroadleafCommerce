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
import org.broadleafcommerce.openadmin.client.dto.MapMetadata;
import org.broadleafcommerce.openadmin.client.dto.MapStructure;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.component.RuleBuilder;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.FieldGroup;

import com.gwtincubator.security.exception.ApplicationSecurityException;

import java.util.Map;

/**
 * @author Andre Azzolini (apazzolini)
 */
public interface FormBuilderService {

    public ListGrid buildListGrid(ClassMetadata cmd, Entity[] entities);

    public RuleBuilder buildRuleBuilder(ClassMetadata cmd, Entity[] entities, String[] ruleVars, String[] configKeys);

    /**
     * Fills out all of the fields in the given {@link EntityForm} with actual values from <b>e</b>. This will also
     * fill out the {@link EntityForm#getCollectionListGrids()} based on the given <b>subCollections</b>
     * 
     * @param ef the form DTO to fill out
     * @param cmd the metadata associated with the form
     * @param e the entity for which the fields from <b>ef</b> should derive their values
     * @param subCollections the additional collections that are associated with <b>ef</b> keyed by their property name
     * from the represented entity (for instance, the list of OrderItems for an Order would be keyed by the 'orderItems'
     * field)
     * @return the same <b>ef</b> that was passed in, but filled out with values from <b>e</b> and <b>subCollections<b>
     */
    public EntityForm buildEntityForm(EntityForm ef, ClassMetadata cmd, Entity e, Map<String, Entity[]> subCollections)
            throws ClassNotFoundException, ServiceException, ApplicationSecurityException;

    /**
     * Instantiates a new {@link EntityForm} based on the information gleaned from <b>cmd</b>. Delegates to
     * {@link #buildFormMetadata(ClassMetadata, EntityForm)} for instantiating the field groups for display.
     * 
     * @param cmd
     * @return a new {@link EntityForm} with its {@link FieldGroup}s properly initialized and ready for Display
     * @see {@link #buildFormMetadata(ClassMetadata, EntityForm)}
     */
    public EntityForm createEntityForm(ClassMetadata cmd, Entity e, Map<String, Entity[]> subCollections)
            throws ClassNotFoundException, ServiceException, ApplicationSecurityException;

    public EntityForm buildAdornedListForm(AdornedTargetCollectionMetadata adornedMd, AdornedTargetList adornedList,
            String parentId)
            throws ServiceException, ApplicationSecurityException;

    public ListGrid buildAdornedListGrid(AdornedTargetCollectionMetadata fmd, ClassMetadata cmd, Entity[] entities);

    /**
     * Convenience method for creating an {@link EntityForm} and building its metadata.
     * @param cmd
     * @return a newly created {@link EntityForm} with its display properties instantiated
     * @see {@link #buildFormMetadata(ClassMetadata, EntityForm)}
     */
    public EntityForm createEntityForm(ClassMetadata cmd);

    /**
     * Initial building of the display metadata associated with displaying an {@link EntityForm}. For instance, this method
     * will initialize the {@link FieldGroup} display properties for the given <b>ef</b>.
     * 
     * @see {@link EntityForm#getGroups()}
     * @see {@link FieldGroup}
     */
    public void buildFormMetadata(ClassMetadata cmd, final EntityForm ef);

    public ListGrid buildMapListGrid(MapMetadata fmd, ClassMetadata cmd, Entity[] entities);

    public EntityForm buildMapForm(MapMetadata mapMd, MapStructure mapStructure, ClassMetadata cmd, String parentId)
            throws ServiceException, ApplicationSecurityException;

}
