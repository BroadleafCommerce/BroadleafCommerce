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
package org.broadleafcommerce.core.catalog.domain;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.broadleafcommerce.core.catalog.domain.common.SkuAttributeMappedSuperclass;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;

/**
 * The Class SkuAttributeImpl is the default implementation of {@link SkuAttribute}.
 * A SKU Attribute is a designator on a SKU that differentiates it from other similar SKUs
 * (for example: Blue attribute for hat).
 * If you want to add fields specific to your implementation of BroadLeafCommerce you should extend
 * this class and add your fields.  If you need to make significant changes to the SkuImpl then you
 * should implement your own version of {@link Sku}.
 * <br>
 * <br>
 * This implementation uses a Hibernate implementation of JPA configured through annotations.
 * The Entity references the following tables:
 * BLC_SKU_ATTRIBUTES,
 * 
 * 
 *   @see {@link SkuAttribute}, {@link SkuImpl}
 *   @author btaylor
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(appliesTo="BLC_SKU_ATTRIBUTE", indexes={
		@Index(name="SKUATTR_NAME_INDEX", columnNames={"NAME"}),
		@Index(name="SKUATTR_SKU_INDEX", columnNames={"SKU_ID"})
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
public class SkuAttributeImpl extends SkuAttributeMappedSuperclass implements SkuAttribute {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The sku. */
    @ManyToOne(targetEntity = SkuImpl.class, optional=false)
    @JoinColumn(name = "SKU_ID")
    protected Sku sku;

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.SkuAttribute#getSku()
     */
    public Sku getSku() {
        return sku;
    }

    /* (non-Javadoc)
     * @see org.broadleafcommerce.core.catalog.domain.SkuAttribute#setSku(org.broadleafcommerce.core.catalog.domain.Sku)
     */
    public void setSku(Sku sku) {
        this.sku = sku;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((sku == null) ? 0 : sku.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        SkuAttributeImpl other = (SkuAttributeImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (sku == null) {
            if (other.sku != null)
                return false;
        } else if (!sku.equals(other.sku))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

}
