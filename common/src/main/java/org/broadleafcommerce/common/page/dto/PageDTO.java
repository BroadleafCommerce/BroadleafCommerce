/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.page.dto;

import org.broadleafcommerce.common.structure.dto.ItemCriteriaDTO;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Page fields must be pre-processed (for example to fix image paths).
 * This DTO allows us to process the PageFields once and then cache
 * the results.
 *
 * Created by bpolster.
 */
public class PageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    protected Long id;
    protected String description;
    protected String localeCode;
    protected String templatePath;
    protected String url;
    protected Long sandboxId;
    protected Integer priority;
    protected Map<String, String> pageFields = new HashMap<String,String>();
    protected String ruleExpression;
    protected List<ItemCriteriaDTO> itemCriteriaDTOList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getSandboxId() {
        return sandboxId;
    }

    public void setSandboxId(Long sandboxId) {
        this.sandboxId = sandboxId;
    }

    public Map<String, String> getPageFields() {
        return pageFields;
    }

    public void setPageFields(Map<String, String> pageFields) {
        this.pageFields = pageFields;
    }
    
    public String getRuleExpression() {
        return ruleExpression;
    }

    public void setRuleExpression(String ruleExpression) {
        this.ruleExpression = ruleExpression;
    }

    public List<ItemCriteriaDTO> getItemCriteriaDTOList() {
        return itemCriteriaDTOList;
    }

    public void setItemCriteriaDTOList(List<ItemCriteriaDTO> itemCriteriaDTOList) {
        this.itemCriteriaDTOList = itemCriteriaDTOList;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }   
    
}

