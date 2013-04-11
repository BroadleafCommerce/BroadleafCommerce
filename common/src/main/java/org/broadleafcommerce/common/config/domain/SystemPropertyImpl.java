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

package org.broadleafcommerce.common.config.domain;

import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * Allows the storage and retrieval of System Properties in the database
 * <p/>
 * User: Kelly Tisdell
 * Date: 6/20/12
 */
@Entity
@Table(name="BLC_SYSTEM_PROPERTY")
@Inheritance(strategy = InheritanceType.JOINED)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blStandardElements")
@AdminPresentationClass(friendlyName = "SystemPropertyImpl")
public class SystemPropertyImpl implements SystemProperty {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "SystemPropertyId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "SystemPropertyId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "SystemPropertyImpl", allocationSize = 50)
    @Column(name = "BLC_SYSTEM_PROPERTY_ID")
    protected Long id;

    @Column(name= "PROPERTY_NAME", unique = true, nullable = false)
    protected String name;

    @Column(name= "PROPERTY_VALUE", nullable = false)
    protected String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
