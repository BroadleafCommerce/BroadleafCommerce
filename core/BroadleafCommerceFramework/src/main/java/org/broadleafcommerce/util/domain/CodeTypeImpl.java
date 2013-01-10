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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((codeType == null) ? 0 : codeType.hashCode());
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result
                + ((modifiable == null) ? 0 : modifiable.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CodeTypeImpl other = (CodeTypeImpl) obj;
        if (codeType == null) {
            if (other.codeType != null)
                return false;
        } else if (!codeType.equals(other.codeType))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        if (modifiable == null) {
            if (other.modifiable != null)
                return false;
        } else if (!modifiable.equals(other.modifiable))
            return false;
        return true;
    }
    
    
}
