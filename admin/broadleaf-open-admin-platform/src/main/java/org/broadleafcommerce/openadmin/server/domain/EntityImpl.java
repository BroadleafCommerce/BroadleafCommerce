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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.*;

/**
 * 
 * @author jfischer
 *
 */
@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_SNDBX_ENTITY")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
public class EntityImpl implements Entity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "EntityId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "EntityId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "EntityImpl", allocationSize = 50)
    @Column(name = "ENTITY_ID")
    protected Long id;
    
    @Column(name = "TYPE")
    @Index(name="SNDBX_ENTITY_TYPE", columnNames={"TYPE"})
    protected String type;
    
    @OneToMany(mappedBy = "entity", targetEntity = PropertyImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})   
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blSandBoxElements")
    @BatchSize(size = 50)
    protected List<Property> properties = new ArrayList<Property>();
    
    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.Entity#getProperties()
     */
    @Override
    public List<Property> getProperties() {
        return properties;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.Entity#setProperties(java.util.List)
     */
    @Override
    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }
    
    /* (non-Javadoc)
     * @see org.broadleafcommerce.openadmin.domain.Entity#addProperty(org.broadleafcommerce.openadmin.client.datasource.results.Property)
     */
    @Override
    public void addProperty(Property property) {
        properties.add(property);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((properties == null) ? 0 : properties.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EntityImpl other = (EntityImpl) obj;
        
        if (id != null && other.id != null) {
            return id.equals(other.id);
        }
        
        if (properties == null) {
            if (other.properties != null)
                return false;
        } else if (!properties.equals(other.properties))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
