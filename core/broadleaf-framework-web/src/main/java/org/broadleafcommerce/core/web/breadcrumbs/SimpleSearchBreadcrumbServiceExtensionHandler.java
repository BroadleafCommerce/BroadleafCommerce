/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.web.breadcrumbs;

import org.broadleafcommerce.common.breadcrumbs.dto.BreadcrumbDTO;
import org.broadleafcommerce.common.breadcrumbs.dto.BreadcrumbDTOType;
import org.broadleafcommerce.common.breadcrumbs.service.BreadcrumbHandlerDefaultPriorities;
import org.broadleafcommerce.common.breadcrumbs.service.BreadcrumbServiceExtensionManager;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Provides a simple breadcrumb or search, based solely on whether the "q" parameter is present.
 * Relies on the 
 * 
 * @author bpolster
 *
 */
@Service("blSearchBreadcrumbServiceExtensionHandler")
public class SimpleSearchBreadcrumbServiceExtensionHandler extends AbstractBreadcrumbServiceExtensionHandler {

    @Value("${breadcrumb.removeAllParamsExceptCategoryId:true}")
    protected boolean removeAllParamsExceptCategoryId;

    @Resource(name = "blBreadcrumbServiceExtensionManager")
    protected BreadcrumbServiceExtensionManager extensionManager;

    @PostConstruct
    public void init() {
        if (isEnabled()) {
            extensionManager.registerHandler(this);
        }
    }

    @Override
    public ExtensionResultStatusType modifyBreadcrumbList(String url, Map<String, String[]> params,
            ExtensionResultHolder<List<BreadcrumbDTO>> holder) {

        String keyword = getSearchKeyword(url, params);
        
        
        url = getBreadcrumbUrl(url, holder);
        params = getBreadcrumbParams(params, holder);

        if (!StringUtils.isEmpty(keyword)) {
            BreadcrumbDTO searchDto = new BreadcrumbDTO();
            searchDto.setText(keyword);
            searchDto.setLink(buildLink(url, params));
            searchDto.setType(BreadcrumbDTOType.SEARCH);
            holder.getResult().add(0, searchDto);

            updateContextMap(params, holder);
        }

        return ExtensionResultStatusType.HANDLED_CONTINUE;
    }

    protected String getBreadcrumbUrl(String url, ExtensionResultHolder<List<BreadcrumbDTO>> holder) {
        Map<String, Object> contextMap = holder.getContextMap();
        if (contextMap.containsKey(BreadcrumbServiceExtensionManager.CONTEXT_PARAM_STRIPPED_URL)) {
            return (String) contextMap.get(BreadcrumbServiceExtensionManager.CONTEXT_PARAM_STRIPPED_URL);
        } else {
            return url;
        }
    }

    @SuppressWarnings("unchecked")
    protected Map<String, String[]> getBreadcrumbParams(Map<String, String[]> params, ExtensionResultHolder<List<BreadcrumbDTO>> holder) {
        Map<String, Object> contextMap = holder.getContextMap();
        if (contextMap.containsKey(BreadcrumbServiceExtensionManager.CONTEXT_PARAM_STRIPPED_PARAMS)) {
            return (Map<String, String[]>) contextMap.get(BreadcrumbServiceExtensionManager.CONTEXT_PARAM_STRIPPED_PARAMS);
        } else {
            return params;
        }
    }

    /**
     * This handler only manages keyword.   In a typical usage, we also want to get rid of 
     * any facet parameters that may be on the URL.     
     * 
     * @param params
     * @param holder
     */
    protected void updateContextMap(Map<String, String[]> params, ExtensionResultHolder<List<BreadcrumbDTO>> holder) {
        if (params != null && params.containsKey(getKeywordParam())) {
            params.remove(getKeywordParam());
            
            if (removeAllParamsExceptCategoryId) {
                Iterator<Entry<String, String[]>> it = params.entrySet().iterator();
                
                while (it.hasNext()) {
                    Entry<String, String[]> entry = it.next();
                    if (!"categoryId".equals(entry.getKey())) {
                        it.remove();
                    }
                }
            }
            
            Map<String, Object> contextMap = holder.getContextMap();
            contextMap.put(BreadcrumbServiceExtensionManager.CONTEXT_PARAM_STRIPPED_PARAMS, params);
        }
    }

    public String getSearchKeyword(String url, Map<String, String[]> params) {
        if (params != null && params.containsKey(getKeywordParam())) {
            String[] keywords = params.get(getKeywordParam());
            if (keywords != null && keywords.length > 0) {
                return keywords[0];
            }
        }
        return null;
    }

    protected String getKeywordParam() {
        return "q";
    }

    @Override
    public int getDefaultPriority() {
        return BreadcrumbHandlerDefaultPriorities.SEARCH_CRUMB;
    }

}
