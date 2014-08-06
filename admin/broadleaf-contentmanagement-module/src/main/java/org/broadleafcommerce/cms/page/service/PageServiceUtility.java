/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.page.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.field.domain.FieldDefinition;
import org.broadleafcommerce.cms.field.domain.FieldGroup;
import org.broadleafcommerce.cms.page.dao.PageDao;
import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageField;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.file.service.StaticAssetPathService;
import org.broadleafcommerce.common.page.dto.PageDTO;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * This method is able to convert a fieldKey 
 * Created by bpolster.
 */
@Service("blPageServiceUtility")
public class PageServiceUtility {

    protected static final Log LOG = LogFactory.getLog(PageServiceUtility.class);
    
    protected static String AND = " && ";
    protected static final String FOREIGN_LOOKUP = "BLC_FOREIGN_LOOKUP";

    @Resource(name="blPageDao")
    protected PageDao pageDao;
    
    @Resource(name = "blPageServiceExtensionManager")
    protected PageServiceExtensionManager extensionManager;

    @Resource(name="blStaticAssetPathService")
    protected StaticAssetPathService staticAssetPathService;

    public void addPageFieldToDTO(Page page, boolean secure, PageDTO pageDTO, String fieldKey) {
        addPageFieldToDTO(page, secure, pageDTO, fieldKey, null);
    }

    public void addPageFieldToDTO(Page page, boolean secure, PageDTO pageDTO, String fieldKey, String originalValue) {
        String cmsPrefix = staticAssetPathService.getStaticAssetUrlPrefix();
        
        PageField pf = page.getPageFields().get(fieldKey);
        if (originalValue == null) {
            originalValue = pf.getValue();
        }
        
        FieldDefinition fd = getFieldDefinition(page, fieldKey);
        
        if (fd != null && fd.getFieldType() == SupportedFieldType.ADDITIONAL_FOREIGN_KEY) {
            pageDTO.getPageFields().put(fieldKey, FOREIGN_LOOKUP + '|' + fd.getAdditionalForeignKeyClass() + '|' + originalValue);
        } else if (StringUtils.isNotBlank(originalValue) && StringUtils.isNotBlank(cmsPrefix) && originalValue.contains(cmsPrefix)) {
            //This may either be an ASSET_LOOKUP image path or an HTML block (with multiple <img>) or a plain STRING that contains the cmsPrefix.
            //If there is an environment prefix configured (e.g. a CDN), then we must replace the cmsPrefix with this one.
            String fldValue = staticAssetPathService.convertAllAssetPathsInContent(originalValue, secure);
            pageDTO.getPageFields().put(fieldKey, fldValue);
        } else {
            pageDTO.getPageFields().put(fieldKey, originalValue);
        }
    }
    
    protected FieldDefinition getFieldDefinition(Page page, String fieldKey) {
        ExtensionResultHolder<FieldDefinition> erh = new ExtensionResultHolder<FieldDefinition>();
        ExtensionResultStatusType result = extensionManager.getProxy().getFieldDefinition(erh, page, fieldKey);
        
        if (result == ExtensionResultStatusType.HANDLED) {
            return erh.getResult();
        }
        
        if (page.getPageTemplate() != null) {
            for (FieldGroup fg : page.getPageTemplate().getFieldGroups()) {
                for (FieldDefinition fd : fg.getFieldDefinitions()) {
                    if (fd.getName().equals(fieldKey)) {
                        return fd;
                    }
                }
            }
        }
        
        return null;
    }
}
