/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.web.service;

import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.server.security.domain.AdminSection;
import org.broadleafcommerce.openadmin.web.form.component.ListGrid;
import org.broadleafcommerce.openadmin.web.form.component.ListGridRecord;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;

/**
 * Extension handler for various methods from {@link FormBuilderService}
 * 
 *
 * @author Phillip Verheyden (phillipuniverse)
 * @see {@link AbstractFormBuilderExtensionHandler}
 * @see {@link FormBuilderService}
 * @see {@link FormBuilderServiceImpl}
 */
public interface FormBuilderExtensionHandler extends ExtensionHandler {

    /**
     * Modifies an {@link EntityForm} <i>before</i> it is populated with an {@link Entity}. This method is invoked after all
     * of the {@link EntityForm} fields have been created with the appropriate metadata, but the values have not been set.
     * An example invocation occurs when creating the 'add' form from a collection list grid. Note that this is invoked
     * every time that an {@link EntityForm} is created, whether it is from a "main" entity (when clicking to view the details
     * screen for an entity from from an admin section) or when building an 'add' or 'edit' form for a related list grid
     * from the detail view for an entity.
     * 
     * <p>Example usages of this method would be to modify the display for certain fields for a particular {@link EntityForm}.</p>
     * 
     * <p>This method is invoked on <i>every</i> {@link EntityForm} that is built from the admin (both initial creation
     * with empty field values and populating with real values from an {@link Entity}).</p>
     * 
     * <p>Also, it's important to note that in most cases you do not need to implement both this method and 
     * {@link #modifyPopulatedEntityForm(EntityForm, Entity)}. It is usually sufficient to only modify one or the other.
     * In fact, in some cases (like on a validation failure or when viewing the details for an entity) both this method <i>and</i>
     * {@link #modifyPopulatedEntityForm(EntityForm, Entity)} are invoked (this method is invoked first)</p>
     * 
     * <p>This methods is always invoked <i><b>before</b></i> {@link #modifyPopulatedEntityForm(EntityForm, Entity)}.
     * 
     * @param ef the {@link EntityForm} that has not yet been populated with values from an entity
     * @see {@link FormBuilderService#populateEntityForm(org.broadleafcommerce.openadmin.dto.ClassMetadata, EntityForm)}
     * @return
     */
    public ExtensionResultStatusType modifyUnpopulatedEntityForm(EntityForm ef);
    
    /**
     * Modifies an {@link EntityForm} after it has been populated with an {@link Entity}. This is invoked after not only
     * all of the {@link EntityForm} fields have been created but the {@link EntityForm} field values have been actually
     * populated with the real values from the given {@link Entity}. An example of when this method is invoked is after
     * validation has failed (on any {@link EntityForm} from the admin) or when viewing the details for an entity.
     * 
     * <p>This method is not invoked on the creation of every single {@link EntityForm} but rather <i>only</i> on the cases
     * presented above. If you need functionality for every case that a particular {@link EntityForm} could be built,
     * you should probably implement the {@link #modifyUnpopulatedEntityForm(EntityForm)} method instead.</p>
     * 
     * <p>This method is very similar to {@link #modifyUnpopulatedEntityForm(EntityForm)} and usually implementors will only
     * override one or the other.</p>
     * 
     * <p>This method is always invoked <i><b>after<b></i> {@link #modifyUnpopulatedEntityForm(EntityForm)}.</p>
     * 
     * @param ef the {@link EntityForm} being populated
     * @param entity the {@link Entity} that the {@link EntityForm} has used to populate all of the values for its fields
     * @return whether or not it was handled
     * @see {@link FormBuilderService#populateEntityForm(org.broadleafcommerce.openadmin.dto.ClassMetadata, Entity, EntityForm)}
     */
    public ExtensionResultStatusType modifyPopulatedEntityForm(EntityForm ef, Entity entity);
    
    /**
     * Invoked whenever a detailed {@link EntityForm} is built, <i>after</i> the initial list grids have been created on
     * the given {@link EntityForm}. This allows for further display modifications to the related {@link ListGrids} that
     * could occur on an {@link EntityForm}, or to only modify an {@link EntityForm} when it is showing an entity in
     * a details view
     * 
     * <p>A <i>detailed</i> {@link EntityForm} is built when clicking on a row from the main {@link ListGrid} in an {@link AdminSection}
     *  or when viewing the details for an entity in a read-only.</p>
     *  
     * <p>As far as order of operations are concerned, this is always invoked <i>after</i> {@link #modifyPopulatedEntityForm(EntityForm, Entity)},
     * which is invoked <i>after</i> {@link #modifyUnpopulatedEntityForm(EntityForm)}. <b>This means that this method is
     * invoked last and can override values from the previous methods</b></p>
     *  
     * @param ef the {@link EntityForm} that has been built with all 
     * @return whether or not it was handled
     * @see {@link FormBuilderService#populateEntityForm(org.broadleafcommerce.openadmin.dto.ClassMetadata, org.broadleafcommerce.openadmin.dto.Entity, java.util.Map, EntityForm)
     */
    public ExtensionResultStatusType modifyDetailEntityForm(EntityForm ef);
    
    /**
     * Provides a hook to modify the ListGridRecord for the given Entity while building the list grid record.
     * 
     * @param className
     * @param record
     * @param entity
     * @return whether or not it was handled
     */
    public ExtensionResultStatusType modifyListGridRecord(String className, ListGridRecord record, Entity entity);
    
    /**
     * Provides a hook to add additional actions to all entity forms.
     * 
     * @param entityForm
     * @return whether or not it was handled
     */
    public ExtensionResultStatusType addAdditionalFormActions(EntityForm entityForm);

}
