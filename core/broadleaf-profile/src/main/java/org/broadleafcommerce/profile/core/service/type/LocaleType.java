/*
 * #%L
 * BroadleafCommerce Profile
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
package org.broadleafcommerce.profile.core.service.type;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;


/**
 * An extendible enumeration of locale types.
 * 
 * @author jfischer
 */
public class LocaleType implements Serializable, BroadleafEnumerationType {

    public static void main(String[] items) {
        System.out.println(Locale.TAIWAN.toString());
    }
    
    private static final long serialVersionUID = 1L;

    private static final Map<String, LocaleType> TYPES = new LinkedHashMap<String, LocaleType>();

    public static final LocaleType US_ENGLISH  = new LocaleType("en_US", "US English", Locale.US);
    public static final LocaleType CANADA  = new LocaleType("en_CA", "Canada English", Locale.CANADA);
    public static final LocaleType CANADA_FRENCH  = new LocaleType("fr_CA", "Canada French", Locale.CANADA_FRENCH);
    public static final LocaleType CHINA  = new LocaleType("zh_CN", "China", Locale.CHINA);
    public static final LocaleType CHINA_ENGLISH  = new LocaleType("en_CN", "China English", new Locale("CN", "en"));
    public static final LocaleType FRANCE  = new LocaleType("fr_FR", "France", Locale.FRANCE);
    public static final LocaleType FRANCE_ENGLISH  = new LocaleType("en_FR", "France English", new Locale("FR", "en"));
    public static final LocaleType GERMANY  = new LocaleType("de_DE", "Germany", Locale.GERMANY);
    public static final LocaleType GERMANY_ENGLISH  = new LocaleType("en_DE", "Germany English", new Locale("DE", "en"));
    public static final LocaleType ITALY  = new LocaleType("it_IT", "Italy", Locale.ITALY);
    public static final LocaleType ITALY_ENGLISH  = new LocaleType("en_IT", "Italy English", new Locale("IT", "en"));
    public static final LocaleType JAPAN  = new LocaleType("ja_JP", "Japan", Locale.JAPAN);
    public static final LocaleType JAPAN_ENGLISH  = new LocaleType("en_JP", "Japan English", new Locale("JP", "en"));
    public static final LocaleType KOREA  = new LocaleType("ko_KR", "Korea", Locale.KOREA);
    public static final LocaleType KOREA_ENGLISH  = new LocaleType("en_KR", "Korea English", new Locale("KR", "en"));
    public static final LocaleType INDIA_HINDI  = new LocaleType("hi_IN", "India Hindi", new Locale("IN", "hi"));
    public static final LocaleType INDIA_ENGLISH  = new LocaleType("en_IN", "India English", new Locale("IN", "en"));
    public static final LocaleType UK_ENGLISH  = new LocaleType("en_UK", "UK English", Locale.UK);
    public static final LocaleType TAIWAN = new LocaleType("zh_TW", "Taiwan", Locale.TAIWAN);
    public static final LocaleType TAIWAN_ENGLISH = new LocaleType("en_TW", "Taiwan English", new Locale("TW", "en"));
    
    public static LocaleType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;
    private Locale locale;

    public LocaleType() {
        //do nothing
    }

    public LocaleType(final String type, final String friendlyType, final Locale locale) {
        this.friendlyType = friendlyType;
        this.locale = locale;
        setType(type);
    }

    public String getType() {
        return type;
    }

    public String getFriendlyType() {
        return friendlyType;
    }
    
    public Locale getLocale() {
        return locale;
    }

    private void setType(final String type) {
        this.type = type;
        if (!TYPES.containsKey(type)) {
            TYPES.put(type, this);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        LocaleType other = (LocaleType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
