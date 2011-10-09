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

package org.broadleafcommerce.cms.locale.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * Created by jfischer
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_LOCALE")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
public class LocaleImpl implements Locale {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "LocaleId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "LocaleId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "LocaleImpl", allocationSize = 10)
    @Column(name = "LOCALE_ID")
    protected Long id;

    @Column (name = "LOCALE_CODE")
    protected String localeCode;

    @Column (name = "FRIENDLY_NAME")
    protected String friendlyName;

    @Column (name = "DEFAULT_FLAG")
    protected Boolean defaultFlag;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getLocaleCode() {
        return localeCode;
    }

    @Override
    public void setLocaleCode(String localeCode) {
        this.localeCode = localeCode;
    }

    @Override
    public String getFriendlyName() {
        return friendlyName;
    }

    @Override
    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    @Override
    public void setDefaultFlag(Boolean defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    @Override
    public Boolean getDefaultFlag() {
        return defaultFlag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Locale)) return false;

        LocaleImpl locale = (LocaleImpl) o;

        if (id != null && locale.id != null) {
            return id.equals(locale.id);
        }

        if (localeCode != null ? !localeCode.equals(locale.localeCode) : locale.localeCode != null) return false;
        if (friendlyName != null ? !friendlyName.equals(locale.friendlyName) : locale.friendlyName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (localeCode != null ? localeCode.hashCode() : 0);
        result = 31 * result + (friendlyName != null ? friendlyName.hashCode() : 0);
        return result;
    }
}
