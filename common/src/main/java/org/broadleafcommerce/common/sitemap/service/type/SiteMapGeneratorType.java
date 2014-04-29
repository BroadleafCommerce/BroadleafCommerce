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

package org.broadleafcommerce.common.sitemap.service.type;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An extendible enumeration of site map generator types.
 * 
 * <ul>
 *  <li><b>CATEGORY</b> - Generator that understands how to generate category based sitemap entries.</li>
 *  <li><b>PRODUCT</b> - Generator that understands how to generate product based sitemap entries.</li>
 *  <li><b>PAGE</b> - Generator that understands how to generate page based sitemap entries.</li>
 *  <li><b>CUSTOM</b> - Generator that understands how to generate static based sitemap entries.</li>  
 * </ul>
 * 
 * @author bpolster
 */
public class SiteMapGeneratorType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, SiteMapGeneratorType> TYPES = new LinkedHashMap<String, SiteMapGeneratorType>();

    public static final SiteMapGeneratorType CATEGORY = new SiteMapGeneratorType("CATEGORY", "Category");
    public static final SiteMapGeneratorType PRODUCT = new SiteMapGeneratorType("PRODUCT", "Product");
    public static final SiteMapGeneratorType SKU = new SiteMapGeneratorType("SKU", "Sku");
    public static final SiteMapGeneratorType PAGE = new SiteMapGeneratorType("PAGE", "Page");
    public static final SiteMapGeneratorType CUSTOM = new SiteMapGeneratorType("CUSTOM", "Custom");

    public static SiteMapGeneratorType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public SiteMapGeneratorType() {
        //do nothing
    }

    public SiteMapGeneratorType(final String type, final String friendlyType) {
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
        SiteMapGeneratorType other = (SiteMapGeneratorType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
