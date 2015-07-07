/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.common.entity.service;

import org.broadleafcommerce.common.entity.dto.EntityInformationDto;
import org.broadleafcommerce.common.site.domain.Site;

/**
 * Class that provides guidance to modules that need to react when running in a multi-tenant mode.   Especially, 
 * when an entity is associated with a Profile or Catalog.
 * 
 * @author bpolster
 *
 */
public interface EntityInformationService {

    /**
     * Given an object, populates {@link EntityInformationDto} with the associated site, profile, and catalog ids.
     * 
     * This method does nothing by default and instead relies on the {@link EntityInformationServiceExtensionManager} to
     * populate the correct values.     Designed for use with the commercial "workflow" and "multi-tenant" modules.
     * 
     * @param o
     * @return
     */
    EntityInformationDto buildEntityInformationForObject(Object o);

    /**
     * Given a Site object, returns the Base Profile Id if one exists.    This method provides a hook
     * for Broadleaf MultiTenant functionality
     * 
     * @param o
     * @return
     */
    Long getBaseProfileIdForSite(Site site);

    /**
     * Given an entity instance, returns true if the object has access to a Site Discriminator.
     * 
     * @param o
     * @return
     */
    boolean getOkayToUseSiteDiscriminator(Object o);

}
