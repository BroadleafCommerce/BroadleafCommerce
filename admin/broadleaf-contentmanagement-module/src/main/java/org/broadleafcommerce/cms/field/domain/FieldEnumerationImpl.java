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

package org.broadleafcommerce.cms.field.domain;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * Created by jfischer
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_FLD_ENUM")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
public class FieldEnumerationImpl implements FieldEnumeration {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "FieldEnumerationId")
    @GenericGenerator(
        name="FieldEnumerationId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="FieldEnumerationImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.cms.field.domain.FieldEnumerationImpl")
        }
    )
    @Column(name = "FLD_ENUM_ID")
    protected Long id;

    @Column (name = "NAME")
    protected String name;

    @OneToMany(mappedBy = "fieldEnumeration", targetEntity = FieldEnumerationItemImpl.class, cascade = {CascadeType.ALL})
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCMSElements")
    @OrderBy("fieldOrder")
    @BatchSize(size = 20)
    protected List<FieldEnumerationItem> enumerationItems;

    @Override
    public List<FieldEnumerationItem> getEnumerationItems() {
        return enumerationItems;
    }

    @Override
    public void setEnumerationItems(List<FieldEnumerationItem> enumerationItems) {
        this.enumerationItems = enumerationItems;
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
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
