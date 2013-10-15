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

package org.broadleafcommerce.common.sitemap.service.type;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An extendible enumeration of sitemap generation types.   
 * 
 * This enum is intended to be use by the BasicSiteMapGenerationConfiguration.   
 * 
 * Any entries to this type must also exist in the SiteMapGenerationConfigurationType
 *  
 * 
 * <ul>
 *  <li><b>PRODUCT</b> - Generator that understands how to generate product based sitemap entries.</li>
 *  <li><b>PAGE</b> - Generator that understands how to generate page based sitemap entries.</li>  
 * </ul>
 * 
 * @author bpolster
 */
public class BasicSiteMapGeneratorType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, BasicSiteMapGeneratorType> TYPES = new LinkedHashMap<String, BasicSiteMapGeneratorType>();

    public static final BasicSiteMapGeneratorType PRODUCT = new BasicSiteMapGeneratorType("PRODUCT", "Product");
    public static final BasicSiteMapGeneratorType PAGE = new BasicSiteMapGeneratorType("PAGE", "Page");

    public static BasicSiteMapGeneratorType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public BasicSiteMapGeneratorType() {
        //do nothing
    }

    public BasicSiteMapGeneratorType(final String type, final String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
    }

    public String getType() {
        return type;
    }

    public String getFriendlyType() {
        return friendlyType;
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
        BasicSiteMapGeneratorType other = (BasicSiteMapGeneratorType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
