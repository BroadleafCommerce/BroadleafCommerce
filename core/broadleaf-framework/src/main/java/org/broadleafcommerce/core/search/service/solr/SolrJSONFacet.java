/*
 * #%L
 * BroadleafCommerce Pricelist
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.search.service.solr;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Nick Crum ncrum
 */
public class SolrJSONFacet {
    protected Map<String, Object> map = new HashMap<>();

    public Map<String, Object> getMap() {
        return map;
    }

    public Object get(String key) {
        if (!map.containsKey(key)) {
            return null;
        }
        return map.get(key);
    }

    public SolrJSONFacet getSubFacet(String key) {
        Object get = get(key);
        return get != null && SolrJSONFacet.class.isAssignableFrom(get.getClass()) ? (SolrJSONFacet) get : null;
    }

    @SuppressWarnings("unchecked")
    public List<SolrJSONFacet> getBuckets() {
        return (List<SolrJSONFacet>) get("buckets");
    }

    public Double getDouble(String key) {
        return Double.valueOf((String) get(key));
    }

    public Integer getInteger(String key) {
        return Integer.valueOf((String) get(key));
    }

    public String getString(String key) {
        return (String) get(key);
    }

    @Override
    public String toString() {
        return toString(0);
    }

    protected String toString(int tabs) {
        StringBuilder sb = new StringBuilder();
        if (MapUtils.isNotEmpty(map)) {
            sb.append("{\n");
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                sb.append(getString(entry.getKey(), entry.getValue(), tabs+1));
            }
            sb.append(StringUtils.repeat(" ", (tabs-1)*2)).append("}");
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    protected String getString(String key, Object object, int tabs) {
        StringBuilder sb = new StringBuilder();
        if (List.class.isAssignableFrom(object.getClass())) {
            sb.append(StringUtils.repeat(" ", tabs * 2)).append(key).append(" : ").append("[\n");
            for (SolrJSONFacet subFacet : (List<SolrJSONFacet>) object) {
                sb.append(StringUtils.repeat(" ", (tabs+1) * 2)).append(subFacet.toString(tabs + 2)).append(",\n");
            }
            sb.append(StringUtils.repeat(" ", tabs * 2)).append("]").append(",\n");
        } else if (SolrJSONFacet.class.isAssignableFrom(object.getClass())) {
            sb.append(StringUtils.repeat(" ", tabs * 2)).append(key).append(" : ").append(((SolrJSONFacet) object).toString(tabs + 1)).append(",\n");
        } else {
            sb.append(StringUtils.repeat(" ", tabs * 2)).append(key).append(" : ").append(object.toString()).append(",\n");
        }

        return sb.toString();
    }
}
