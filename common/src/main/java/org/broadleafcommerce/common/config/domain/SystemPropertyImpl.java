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
package org.broadleafcommerce.common.config.domain;

import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

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
    @GeneratedValue(generator = "SystemPropertyId")
    @GenericGenerator(
        name="SystemPropertyId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="SystemPropertyImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.common.config.domain.SystemPropertyImpl")
        }
    )
    @Column(name = "BLC_SYSTEM_PROPERTY_ID")
    protected Long id;

    @Column(name= "PROPERTY_NAME", unique = true, nullable = false)
    protected String name;

    @Column(name= "PROPERTY_VALUE", nullable = false)
    protected String value;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }
}
