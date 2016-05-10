/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.broadleafcommerce.common.util.BLCMapUtils;
import org.broadleafcommerce.common.util.BLCMessageUtils;
import org.broadleafcommerce.common.util.TypedClosure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * 
 * @author jfischer
 *
 */
@JsonAutoDetect
public class ClassMetadata implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @JsonProperty
    private String ceilingType;

    @JsonProperty
    private String securityCeilingType;

    @JsonIgnore
    private ClassTree polymorphicEntities;

    @JsonProperty(value = "classProperties")
    private Property[] properties;

    @JsonIgnore
    private Map<String, TabMetadata> tabAndGroupMetadata;

    @JsonProperty
    private String currencyCode = "USD";
    
    private Map<String, Property> pMap = null;

    public Map<String, Property> getPMap() {
        if (pMap == null) {
            pMap = BLCMapUtils.keyedMap(properties, new TypedClosure<String, Property>() {

                @Override
                public String getKey(Property value) {
                    return value.getName();
                }
            });
        }
        return pMap;
    }

    public String getCeilingType() {
        return ceilingType;
    }
    
    public void setCeilingType(String type) {
        this.ceilingType = type;
    }

    /**
     * For dynamic forms, the type to check security permissions will be different than the type used to generate the 
     * forms.   For example, a user with "ADD" or "UPDATE" permissions on STRUCTURE_CONTENT does not need 
     * to have the same level of access to StructuredContentTemplate.   
     * 
     * @param type
     */
    public String getSecurityCeilingType() {
        return securityCeilingType;
    }

    public void setSecurityCeilingType(String securityCeilingType) {
        this.securityCeilingType = securityCeilingType;
    }

    public ClassTree getPolymorphicEntities() {
        return polymorphicEntities;
    }

    public void setPolymorphicEntities(ClassTree polymorphicEntities) {
        this.polymorphicEntities = polymorphicEntities;
    }

    public Property[] getProperties() {
        return properties;
    }
    
    public void setProperties(Property[] properties) {
        this.properties = properties;
    }

    public Map<String, TabMetadata> getTabAndGroupMetadata() {
        return tabAndGroupMetadata;
    }

    public void setTabAndGroupMetadata(Map<String, TabMetadata> tabAndGroupMetadata) {
        this.tabAndGroupMetadata = tabAndGroupMetadata;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public TabMetadata getTabMetadataUsingTabKey(String tabKey) {
        return tabAndGroupMetadata.get(tabKey);
    }

    public TabMetadata getTabMetadataUsingGroupKey(String groupKey) {
        for (TabMetadata tab : tabAndGroupMetadata.values()) {
            if (tab.getGroupMetadata() != null) {
                for (GroupMetadata group : tab.getGroupMetadata().values()) {
                    if (group.getGroupName() != null && group.getGroupName().equals(groupKey)) {
                        return tab;
                    }
                }
            }
        }
        return null;
    }

    public TabMetadata getFirstTab() {
        Iterator<TabMetadata> tabMetadataIterator = tabAndGroupMetadata.values().iterator();
        TabMetadata result = tabMetadataIterator.hasNext() ? tabMetadataIterator.next() : null;

        while(tabMetadataIterator.hasNext()) {
            TabMetadata next = tabMetadataIterator.next();
            if (result.getTabOrder() == null) {
                result = next;
            } else if (next.getTabOrder() != null && next.getTabOrder() < result.getTabOrder()) {
                result = next;
            }
        }

        return result;
    }

    public String[][] getGroupOptionsFromTabAndGroupMetadata() {
        List<String[]> result = new ArrayList<>();

        for (TabMetadata tab : tabAndGroupMetadata.values()) {
            for (GroupMetadata group : tab.getGroupMetadata().values()) {
                String key = group.getGroupName();
                String displayValue = BLCMessageUtils.getMessage(tab.getTabName()) + " : " + BLCMessageUtils.getMessage(group.getGroupName());
                result.add(new String[]{key, displayValue});
            }
        }

        return result.toArray(new String[result.size()][2]);
    }
}
