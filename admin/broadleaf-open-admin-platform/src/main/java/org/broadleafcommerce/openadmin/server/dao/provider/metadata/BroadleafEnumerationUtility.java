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
package org.broadleafcommerce.openadmin.server.dao.provider.metadata;

import org.broadleafcommerce.common.BroadleafEnumerationType;
import org.broadleafcommerce.common.util.Tuple;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blBroadleafEnumerationUtility")
public class BroadleafEnumerationUtility {

    @SuppressWarnings("rawtypes")
    public List<Tuple<String, String>> getEnumerationValues(String broadleafEnumerationClass, DynamicEntityDao dynamicEntityDao) {
        try {
            Map<String, String> enumVals;
            Class<?> broadleafEnumeration = Class.forName(broadleafEnumerationClass);  
    
            Method typeMethod = broadleafEnumeration.getMethod("getType");
            Method friendlyTypeMethod = broadleafEnumeration.getMethod("getFriendlyType");
            Field types = dynamicEntityDao.getFieldManager().getField(broadleafEnumeration, "TYPES");
            
            if (Comparable.class.isAssignableFrom(broadleafEnumeration)) {
                enumVals = new LinkedHashMap<String, String>();
                Set<BroadleafEnumerationType> blcEnumSet = new TreeSet<BroadleafEnumerationType>();
                if (types != null) {
                    Map typesMap = (Map) types.get(null);
                    for (Object value : typesMap.values()) {
                        blcEnumSet.add((BroadleafEnumerationType) value);
                    }
    
                    for (Object value : typesMap.values()) {
                        enumVals.put((String) friendlyTypeMethod.invoke(value), (String) typeMethod.invoke(value));
                    }
                }
            } else {
                enumVals = new TreeMap<String, String>();
                if (types != null) {
                    Map typesMap = (Map) types.get(null);
                    for (Object value : typesMap.values()) {
                        enumVals.put((String) friendlyTypeMethod.invoke(value), (String) typeMethod.invoke(value));
                    }
                } else {
                    Field[] fields = dynamicEntityDao.getAllFields(broadleafEnumeration);
                    for (Field field : fields) {
                        boolean isStatic = Modifier.isStatic(field.getModifiers());
                        if (isStatic && field.getType().isAssignableFrom(broadleafEnumeration)){
                            enumVals.put((String) friendlyTypeMethod.invoke(field.get(null)), (String) typeMethod.invoke(field.get(null)));
                        }
                    }
                }
            }
            
            List<Tuple<String, String>> enumerationValues = new ArrayList<Tuple<String, String>>();
            for (String key : enumVals.keySet()) {
                Tuple<String, String> t = new Tuple<String, String>(enumVals.get(key), key);
                enumerationValues.add(t);
            }
            return enumerationValues;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
