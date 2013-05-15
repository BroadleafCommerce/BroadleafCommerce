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

package org.broadleafcommerce.common.enumeration.domain;

import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_DATA_DRVN_ENUM")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "DataDrivenEnumerationImpl_friendyName")
public class DataDrivenEnumerationImpl implements DataDrivenEnumeration {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "DataDrivenEnumerationId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "DataDrivenEnumerationId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "DataDrivenEnumerationId", allocationSize = 50)
    @Column(name = "ENUM_ID")
    protected Long id;
    
    @Column(name = "ENUM_KEY")
    @Index(name = "ENUM_KEY_INDEX", columnNames = {"KEY"})
    protected String key;
    
    @Column(name = "MODIFIABLE")
    protected Boolean modifiable = false;

    @OneToMany(mappedBy = "type", targetEntity = DataDrivenEnumerationValueImpl.class, cascade = {CascadeType.ALL})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
    protected List<DataDrivenEnumerationValue> orderItems = new ArrayList<DataDrivenEnumerationValue>();
    
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
        return modifiable;
    }

    @Override
    public void setModifiable(Boolean modifiable) {
        this.modifiable = modifiable;
    }

    @Override
    public List<DataDrivenEnumerationValue> getOrderItems() {
        return orderItems;
    }

    @Override
    public void setOrderItems(List<DataDrivenEnumerationValue> orderItems) {
        this.orderItems = orderItems;
    }
}
