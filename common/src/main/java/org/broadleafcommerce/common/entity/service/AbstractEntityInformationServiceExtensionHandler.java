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
import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.site.domain.Catalog;
import org.broadleafcommerce.common.site.domain.Site;

import java.util.List;

public class AbstractEntityInformationServiceExtensionHandler extends AbstractExtensionHandler
        implements EntityInformationServiceExtensionHandler {

    @Override
    public ExtensionResultStatusType updateEntityInformationDto(EntityInformationDto dto, Object entityInstance) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType getBaseProfileIdForSite(Site site, ExtensionResultHolder<Long> erh) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType getOkayToUseSiteDiscriminator(Object o, ExtensionResultHolder<Boolean> erh) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }

    @Override
    public ExtensionResultStatusType findAllCatalogs(ExtensionResultHolder<List<Catalog>> erh) {
        return ExtensionResultStatusType.NOT_HANDLED;
    }
}