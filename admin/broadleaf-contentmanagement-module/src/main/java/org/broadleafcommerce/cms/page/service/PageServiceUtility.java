/*-
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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
package org.broadleafcommerce.cms.page.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.field.domain.FieldDefinition;
import org.broadleafcommerce.cms.page.dao.PageDao;
import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageAttribute;
import org.broadleafcommerce.cms.page.domain.PageField;
import org.broadleafcommerce.cms.page.domain.PageItemCriteria;
import org.broadleafcommerce.cms.page.domain.PageRule;
import org.broadleafcommerce.cms.page.domain.PageTemplateFieldGroupXref;
import org.broadleafcommerce.common.dao.GenericEntityDao;
import org.broadleafcommerce.common.extension.ExtensionResultHolder;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.file.service.StaticAssetPathService;
import org.broadleafcommerce.common.page.dto.PageDTO;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.sandbox.SandBoxHelper;
import org.broadleafcommerce.common.structure.dto.ItemCriteriaDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
    
    @Resource(name = "blGenericEntityDao")
    protected GenericEntityDao genericDao;

    @Resource(name = "blPageServiceExtensionManager")
    protected PageServiceExtensionManager extensionManager;

    @Resource(name="blStaticAssetPathService")
    protected StaticAssetPathService staticAssetPathService;

    @Resource(name = "blSandBoxHelper")
    protected SandBoxHelper sandBoxHelper;

    public PageDTO buildPageDTO(Page page, boolean secure) {
        PageDTO pageDTO = new PageDTO();
        pageDTO.setId(page.getId());
        pageDTO.setDescription(page.getDescription());
        pageDTO.setUrl(page.getFullUrl());
        pageDTO.setPriority(page.getPriority());

        if (page.getPageTemplate() != null) {
            pageDTO.setTemplatePath(page.getPageTemplate().getTemplatePath());
            if (page.getPageTemplate().getLocale() != null) {
                pageDTO.setLocaleCode(page.getPageTemplate().getLocale().getLocaleCode());
            }
        }

        for (String fieldKey : page.getPageFields().keySet()) {
            addPageFieldToDTO(page, secure, pageDTO, fieldKey);
        }

        pageDTO.setRuleExpression(buildRuleExpression(page));

        if (page.getQualifyingItemCriteria() != null && page.getQualifyingItemCriteria().size() > 0) {
            pageDTO.setItemCriteriaDTOList(buildItemCriteriaDTOList(page));
        }

        for (Entry<String, PageAttribute> entry : page.getAdditionalAttributes().entrySet()) {
            pageDTO.getPageAttributes().put(entry.getKey(), entry.getValue().getValue());
        }
        pageDTO.getPageAttributes().put("title", page.getMetaTitle());
        pageDTO.getPageAttributes().put("metaDescription", page.getMetaDescription());

        return pageDTO;
    }

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
        
        if (fd != null && fd.getFieldType() == SupportedFieldType.ADDITIONAL_FOREIGN_KEY
                && StringUtils.isNotBlank(originalValue)) {
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
            for (PageTemplateFieldGroupXref fgXrefs : page.getPageTemplate().getFieldGroupXrefs()) {
                for (FieldDefinition fd : fgXrefs.getFieldGroup().getFieldDefinitions()) {
                    if (fd.getName().equals(fieldKey)) {
                        return fd;
                    }
                }
            }
        }
        
        return null;
    }

    protected String buildRuleExpression(Page page) {
        StringBuffer ruleExpression = null;
        Map<String, PageRule> ruleMap = page.getPageMatchRules();
        if (ruleMap != null) {
            for (String ruleKey : ruleMap.keySet()) {
                if (ruleExpression == null) {
                    ruleExpression = new StringBuffer(ruleMap.get(ruleKey).getMatchRule());
                } else {
                    ruleExpression.append(AND);
                    ruleExpression.append(ruleMap.get(ruleKey).getMatchRule());
                }
            }
        }
        if (ruleExpression != null) {
            return ruleExpression.toString();
        } else {
            return null;
        }
    }

    protected List<ItemCriteriaDTO> buildItemCriteriaDTOList(Page page) {
        List<ItemCriteriaDTO> itemCriteriaDTOList = new ArrayList<ItemCriteriaDTO>();
        for (PageItemCriteria criteria : page.getQualifyingItemCriteria()) {
            ItemCriteriaDTO criteriaDTO = new ItemCriteriaDTO();
            criteriaDTO.setMatchRule(criteria.getMatchRule());
            criteriaDTO.setQty(criteria.getQuantity());
            itemCriteriaDTOList.add(criteriaDTO);
        }
        return itemCriteriaDTOList;
    }

    public PageDTO hydrateForeignLookups(PageDTO page) {
        for (Entry<String, Object> entry : page.getPageFields().entrySet()) {
            if (entry.getValue() instanceof String && ((String) entry.getValue()).startsWith(FOREIGN_LOOKUP)) {
                page.getForeignPageFields().put(entry.getKey(), entry.getValue());
            }
        }

        if (page.getForeignPageFields().size() > 0) {
            PageDTO clone = new PageDTO();
            clone.copy(page);

            for (Entry<String, Object> entry : page.getForeignPageFields().entrySet()) {
                String clazz = ((String) entry.getValue()).split("\\|")[1];
                String id = ((String) entry.getValue()).split("\\|")[2];
                Object newValue = null;
                if (StringUtils.isNotBlank(clazz) && StringUtils.isNotBlank(id) && !"null".equals(id)) {
                    newValue = genericDao.readGenericEntity(genericDao.getImplClass(clazz), id);
                }
                if (newValue != null) {
                    clone.getPageFields().put(entry.getKey(), newValue);
                } else {
                    clone.getPageFields().remove(entry.getKey());
                }
            }
            return clone;
        } else {
            return page;
        }
    }
}
