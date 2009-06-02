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
package org.broadleafcommerce.profile.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_COUNTRY")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class CountryImpl implements Country, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ABBREVIATION")
    private String abbreviation;

    @Column(name = "NAME")
    private String name;

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String Abbreviation) {
        this.abbreviation = Abbreviation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || !(other instanceof CountryImpl)) return false;

        CountryImpl item = (CountryImpl) other;

        if (name != null && item.name != null ? !name.equals(item.name) : name != item.name) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result *= 31;

        return result;
    }
}
