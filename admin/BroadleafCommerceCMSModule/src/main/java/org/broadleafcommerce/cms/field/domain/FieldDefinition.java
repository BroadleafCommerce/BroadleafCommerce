/*
 * Copyright 2008-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.cms.field.domain;

/**
 * Created by bpolster.
 */
public interface FieldDefinition {
    public Long getId();

    public void setId(Long id);

    public String getName();

    public void setName(String name);

    public String getFieldType();

    public void setFieldType(String fieldType);

    public String getSecurityLevel();

    public void setSecurityLevel(String securityLevel);

    public Boolean getHiddenFlag();

    public void setHiddenFlag(Boolean hiddenFlag);

    public String getValidationRegEx();

    public void setValidationRegEx(String validationRegEx);

    public Integer getMaxLength();

    public void setMaxLength(Integer maxLength);

    public Integer getColumnWidth();

    public void setColumnWidth(Integer columnWidth);

    public Boolean getTextAreaFlag();

    public void setTextAreaFlag(Boolean textAreaFlag);

    public String getEnumerationName();

    public void setEnumerationName(String enumerationName);

    public Boolean getAllowMultiples();

    public void setAllowMultiples(Boolean allowMultiples);
}
