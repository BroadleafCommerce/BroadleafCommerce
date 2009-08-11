/*
 * Copyright 2008-2009 the original author or authors.
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
package org.broadleafcommerce.util.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "BLC_CODE_TYPES")
@Inheritance(strategy=InheritanceType.JOINED)
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class CodeTypeImpl implements CodeType {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "CodeTypeId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "CodeTypeId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "CodeTypeId", allocationSize = 50)
    @Column(name = "CODE_ID")
    protected Long id;

    @Column(name = "CODE_TYPE", nullable=false)
    protected String codeType;

    @Column(name = "CODE_KEY", nullable=false)
    protected String key;

    @Column(name = "CODE_DESC")
    protected String description;

    @Column(name = "MODIFIABLE")
    protected Character modifiable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isModifiable() {
        if(modifiable == null)
            return null;
        return modifiable == 'Y' ? Boolean.TRUE : Boolean.FALSE;
    }

    public Boolean getModifiable() {
        return isModifiable();
    }

    public void setModifiable(Boolean modifiable) {
        if(modifiable == null) {
            this.modifiable = null;
        } else {
            this.modifiable = modifiable ? 'Y' : 'N';
        }
    }
}
