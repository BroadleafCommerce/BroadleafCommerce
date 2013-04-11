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

package org.broadleafcommerce.cms.structure.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bpolster.
 */
public class StructuredContentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Long id;
    protected String contentName;
    protected String contentType;
    protected String localeCode;
    protected Integer priority;
    protected Long sandboxId;    
    protected Map values = new HashMap<String,String>();
    protected String ruleExpression;
    protected List<ItemCriteriaDTO> itemCriteriaDTOList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        values.put("contentName", contentName);
        this.contentName = contentName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        values.put("contentType", contentType);
        this.contentType = contentType;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public void setLocaleCode(String localeCode) {
        values.put("localeCode", localeCode);
        this.localeCode = localeCode;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        values.put("priority", priority);
        this.priority = priority;
    }

    public Long getSandboxId() {
        return sandboxId;
    }

    public void setSandboxId(Long sandboxId) {
        this.sandboxId = sandboxId;
    }

    public Map getValues() {
        return values;
    }

    public void setValues(Map values) {
        this.values = values;
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
}
