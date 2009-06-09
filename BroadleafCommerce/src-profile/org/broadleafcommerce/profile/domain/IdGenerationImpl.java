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
package org.broadleafcommerce.profile.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_ID_GENERATION")
public class IdGenerationImpl implements IdGeneration {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID_TYPE")
    protected String type;

    @Column(name = "BATCH_START")
    protected Long batchStart;

    @Column(name = "BATCH_SIZE")
    protected Long batchSize;

    @Version
    protected Integer version;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getBatchStart() {
        return batchStart;
    }

    public void setBatchStart(Long batchStart) {
        this.batchStart = batchStart;
    }

    public Long getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Long batchSize) {
        this.batchSize = batchSize;
    }

    public Integer getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((batchSize == null) ? 0 : batchSize.hashCode());
        result = prime * result + ((batchStart == null) ? 0 : batchStart.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((version == null) ? 0 : version.hashCode());
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
        IdGenerationImpl other = (IdGenerationImpl) obj;
        if (batchSize == null) {
            if (other.batchSize != null)
                return false;
        } else if (!batchSize.equals(other.batchSize))
            return false;
        if (batchStart == null) {
            if (other.batchStart != null)
                return false;
        } else if (!batchStart.equals(other.batchStart))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (version == null) {
            if (other.version != null)
                return false;
        } else if (!version.equals(other.version))
            return false;
        return true;
    }
}
