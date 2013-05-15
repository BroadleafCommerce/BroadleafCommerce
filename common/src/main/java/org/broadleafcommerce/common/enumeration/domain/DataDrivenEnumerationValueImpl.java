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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * @author Jeff Fischer
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="BLC_DATA_DRVN_ENUM_VAL")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "DataDrivenEnumerationImpl_friendyName")
public class DataDrivenEnumerationValueImpl implements DataDrivenEnumerationValue {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "DataDrivenEnumerationValueId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "DataDrivenEnumerationValueId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "DataDrivenEnumerationValueId", allocationSize = 50)
    @Column(name = "ENUM_VAL_ID")
    protected Long id;

    @ManyToOne(targetEntity = DataDrivenEnumerationImpl.class)
    @JoinColumn(name = "ENUM_TYPE")
    protected DataDrivenEnumeration type;

    @Column(name = "ENUM_KEY")
    @Index(name = "ENUM_VAL_KEY_INDEX", columnNames = {"ENUM_KEY"})
    protected String key;

    @Column(name = "DISPLAY")
    protected String display;

    @Column(name = "HIDDEN")
    @Index(name = "HIDDEN_INDEX", columnNames = {"HIDDEN"})
    protected Boolean hidden = false;

    @Override
    public String getDisplay() {
        return display;
    }

    @Override
    public void setDisplay(String display) {
        this.display = display;
    }

    @Override
    public Boolean getHidden() {
        return hidden;
    }

    @Override
    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

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
    public DataDrivenEnumeration getType() {
        return type;
    }

    @Override
    public void setType(DataDrivenEnumeration type) {
        this.type = type;
    }
}
