/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.enumeration.domain;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.AddMethodType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_DATA_DRVN_ENUM")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "DataDrivenEnumerationImpl_friendyName")
public class DataDrivenEnumerationImpl implements DataDrivenEnumeration {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "DataDrivenEnumerationId")
    @GenericGenerator(
        name="DataDrivenEnumerationId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="DataDrivenEnumerationImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.common.enumeration.domain.DataDrivenEnumerationImpl")
        }
    )
    @Column(name = "ENUM_ID")
    protected Long id;
    
    @Column(name = "ENUM_KEY")
    @Index(name = "ENUM_KEY_INDEX", columnNames = {"KEY"})
    @AdminPresentation(friendlyName = "DataDrivenEnumerationImpl_Key", order = 1, gridOrder = 1, prominent = true)
    protected String key;
    
    @Column(name = "MODIFIABLE")
    @AdminPresentation(friendlyName = "DataDrivenEnumerationImpl_Modifiable", order = 2, gridOrder = 2, prominent = true)
    protected Boolean modifiable = false;

    @OneToMany(mappedBy = "type", targetEntity = DataDrivenEnumerationValueImpl.class, cascade = {CascadeType.ALL})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    @AdminPresentationCollection(addType = AddMethodType.PERSIST, friendlyName = "DataDrivenEnumerationImpl_Enum_Values", order = 3)
    protected List<DataDrivenEnumerationValue> enumValues = new ArrayList<DataDrivenEnumerationValue>();
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public Boolean getModifiable() {
        if (modifiable == null) {
            return Boolean.FALSE;
        } else {
            return modifiable;
        }
    }

    @Override
    public void setModifiable(Boolean modifiable) {
        this.modifiable = modifiable;
    }

    @Override
    public List<DataDrivenEnumerationValue> getEnumValues() {
        return enumValues;
    }

    @Override
    public void setEnumValues(List<DataDrivenEnumerationValue> enumValues) {
        this.enumValues = enumValues;
    }

    @Override
    @Deprecated
    public List<DataDrivenEnumerationValue> getOrderItems() {
        return enumValues;
    }

    @Override
    @Deprecated
    public void setOrderItems(List<DataDrivenEnumerationValue> orderItems) {
        this.enumValues = orderItems;
    }
}
