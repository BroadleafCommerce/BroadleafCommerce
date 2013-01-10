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

package org.broadleafcommerce.openadmin.server.domain;

import org.broadleafcommerce.openadmin.server.domain.visitor.PersistencePerspectiveItemVisitor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.persistence.Entity;

/**
 * 
 * @author jfischer
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_SNDBX_SMPL_MAP_STRCTR")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
public class SimpleValueMapStructureImpl extends MapStructureImpl implements SimpleValueMapStructure {

    private static final long serialVersionUID = 1L;
    
    @Column(name = "VAL_PROP_NAME")
    protected String valuePropertyName;
    
    @Column(name = "VAL_PROP_FRIENDLY_NAME")
    protected String valuePropertyFriendlyName;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.SimpleValueMapStructure#getValuePropertyName()
     */
    @Override
    public String getValuePropertyName() {
        return valuePropertyName;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.SimpleValueMapStructure#setValuePropertyName(java.lang.String)
     */
    @Override
    public void setValuePropertyName(String valuePropertyName) {
        this.valuePropertyName = valuePropertyName;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.SimpleValueMapStructure#getValuePropertyFriendlyName()
     */
    @Override
    public String getValuePropertyFriendlyName() {
        return valuePropertyFriendlyName;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.SimpleValueMapStructure#setValuePropertyFriendlyName(java.lang.String)
     */
    @Override
    public void setValuePropertyFriendlyName(String valuePropertyFriendlyName) {
        this.valuePropertyFriendlyName = valuePropertyFriendlyName;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime
                * result
                + ((valuePropertyFriendlyName == null) ? 0
                        : valuePropertyFriendlyName.hashCode());
        result = prime
                * result
                + ((valuePropertyName == null) ? 0 : valuePropertyName
                        .hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        SimpleValueMapStructureImpl other = (SimpleValueMapStructureImpl) obj;
        if (valuePropertyFriendlyName == null) {
            if (other.valuePropertyFriendlyName != null)
                return false;
        } else if (!valuePropertyFriendlyName
                .equals(other.valuePropertyFriendlyName))
            return false;
        if (valuePropertyName == null) {
            if (other.valuePropertyName != null)
                return false;
        } else if (!valuePropertyName.equals(other.valuePropertyName))
            return false;
        return true;
    }

    @Override
    public void accept(PersistencePerspectiveItemVisitor visitor) {
        visitor.visit(this);
    }
}
