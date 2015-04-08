/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
import org.broadleafcommerce.common.extension.ExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.site.domain.Site;

/**
 * <p>
 * ExtensionHandler for methods within {@link EntityInformationService}
 * 
 * <p>
 * Rather than implementing this interface directly you should extend your implementation from
 * {@link AbstractEntityInformationServiceExtensionHandler}.
 * 
 * <p>
 * Intended to be used by enterprise and multi-tenant modules to populate a dto so that other 
 * modules can have easy access to this information w/out a formal dependency on the multi-tenant or
 * enterprise modules.
 * 
 * @author bpolster
 * @see {@link AbstractEntityInformationServiceExtensionHandler}
 */
public interface EntityInformationServiceExtensionHandler extends ExtensionHandler {

    /**
     * Handler implementations will override or populate the values on the passed in dto.
     * 
     * @param dto {@link EntityInformationDto} the dto to add values to
     * @param entityInstance the object to be examined
     * @see {@link EntityInformationServiceImpl#buildEntityInformationForObject(Object)}
     */
    ExtensionResultStatusType updateEntityInformationDto(EntityInformationDto dto, Object entityInstance);

    /**
     * Handler implementations will populate the {@link ExtensionResultHolder} with a valid 
     * base profile id if one exists for the site
     * 
     * @param site {@link Site} the Site to check for a base profile
     * @param erh {@link ExtensionResultHolder} a container for the result     
     */
    ExtensionResultStatusType getBaseProfileIdForSite(Site site, ExtensionResultHolder<Long> erh);

    /**
     * Handler implementations will set the value of {@link ExtensionResultHolder} to true if the
     * passed in object supports site discriminator usage.   For example, when running in a Multi-Tenant
     * Broadleaf implementation.
     * 
     * @param o
     * @return
     */
    ExtensionResultStatusType getOkayToUseSiteDiscriminator(Object o, ExtensionResultHolder<Boolean> erh);

}
