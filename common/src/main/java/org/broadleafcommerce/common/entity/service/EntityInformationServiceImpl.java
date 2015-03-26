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
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.site.domain.Site;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 
 * @author bpolster
 *
 */
@Service("blEntityInformationService")
public class EntityInformationServiceImpl implements EntityInformationService {

    @Resource(name = "blEntityInformationServiceExtensionManager")
    protected EntityInformationServiceExtensionManager extensionManager;

    public EntityInformationDto buildEntityInformationForObject(Object o) {
        EntityInformationDto dto = createEntityInformationDto(o);
        extensionManager.getProxy().updateEntityInformationDto(dto, o);
        return dto;
    }

    @Override
    public Long getBaseProfileIdForSite(Site site) {
        ExtensionResultHolder<Long> erh = new ExtensionResultHolder<Long>();
        extensionManager.getProxy().getBaseProfileIdForSite(site, erh);
        return erh.getResult();
    }

    @Override
    public boolean getOkayToUseSiteDiscriminator(Object o) {
        ExtensionResultHolder<Boolean> erh = new ExtensionResultHolder<Boolean>();
        erh.setResult(Boolean.FALSE);
        extensionManager.getProxy().getOkayToUseSiteDiscriminator(o, erh);
        return erh.getResult();
    }

    /**
     * Factory method for instantiating the {@link EntityInformationDto}
     * @return
     */
    protected EntityInformationDto createEntityInformationDto(Object o) {
        return new EntityInformationDto();
    }

}
