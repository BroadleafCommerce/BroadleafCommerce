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
package org.broadleafcommerce.common.breadcrumbs.processor;

import org.broadleafcommerce.common.breadcrumbs.dto.BreadcrumbDTO;
import org.broadleafcommerce.common.breadcrumbs.service.BreadcrumbService;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.dialect.AbstractModelVariableModifierProcessor;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * A Thymeleaf processor that will add a list of BreadcrumbDTOs to the model.
 *
 * @author bpolster
 */
public class BreadcrumbProcessor extends AbstractModelVariableModifierProcessor {

    @Resource(name = "blBreadcrumbService")
    protected BreadcrumbService breadcrumbService;

    /**
     * Sets the name of this processor to be used in the Thymeleaf template
     */
    public BreadcrumbProcessor() {
        super("breadcrumbs");
    }

    @Override
    public int getPrecedence() {
        return 1000;
    }

    @Override
    protected void modifyModelAttributes(Arguments arguments, Element element) {
        String baseUrl = getBaseUrl(arguments, element);
        Map<String, String[]> params = getParams(arguments, element);
        List<BreadcrumbDTO> dtos = breadcrumbService.buildBreadcrumbDTOs(baseUrl, params);
        String resultVar = element.getAttributeValue("resultVar");
        
        if (resultVar == null) {
            resultVar = "breadcrumbs";
        }
        
        if (!CollectionUtils.isEmpty(dtos)) {
            addToModel(arguments, resultVar, dtos);
        }
    }

    protected String getBaseUrl(Arguments arguments, Element element) {
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();

        if (brc != null) {
            return brc.getRequest().getRequestURI();
        }
        return "";
    }

    protected Map<String, String[]> getParams(Arguments arguments, Element element) {
        Map<String, String[]> paramMap = new HashMap<String, String[]>();
        BroadleafRequestContext brc = BroadleafRequestContext.getBroadleafRequestContext();

        if (brc != null) {
            paramMap = BroadleafRequestContext.getRequestParameterMap();
            if (paramMap != null) {
                paramMap = new HashMap<String, String[]>(paramMap);
            }
        }
        return paramMap;
    }

}
