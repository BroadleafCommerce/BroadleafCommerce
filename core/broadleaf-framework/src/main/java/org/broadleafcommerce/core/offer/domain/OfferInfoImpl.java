/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.offer.domain;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_OFFER_INFO")
public class OfferInfoImpl implements OfferInfo {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator= "OfferInfoId")
    @GenericGenerator(
        name="OfferInfoId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="OfferInfoImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.offer.domain.OfferInfoImpl")
        }
    )
    @Column(name = "OFFER_INFO_ID")
    protected Long id;

    @ElementCollection
    @MapKeyColumn(name="FIELD_NAME")
    @Column(name="FIELD_VALUE")
    @CollectionTable(name="BLC_OFFER_INFO_FIELDS", joinColumns=@JoinColumn(name="OFFER_INFO_FIELDS_ID"))
    @BatchSize(size = 50)
    protected Map<String, String> fieldValues = new HashMap<String, String>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Map<String, String> getFieldValues() {
        return fieldValues;
    }

    @Override
    public void setFieldValues(Map<String, String> fieldValues) {
        this.fieldValues = fieldValues;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fieldValues == null) ? 0 : fieldValues.hashCode());
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
        OfferInfoImpl other = (OfferInfoImpl) obj;

        if (id != null && other.id != null) {
            return id.equals(other.id);
        }

        if (fieldValues == null) {
            if (other.fieldValues != null)
                return false;
        } else if (!fieldValues.equals(other.fieldValues))
            return false;
        return true;
    }
}
