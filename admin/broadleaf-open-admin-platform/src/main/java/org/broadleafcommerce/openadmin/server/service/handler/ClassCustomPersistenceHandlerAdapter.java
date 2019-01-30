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
package org.broadleafcommerce.openadmin.server.service.handler;

import org.broadleafcommerce.openadmin.dto.PersistencePackage;

import java.util.ArrayList;
import java.util.List;

/**
 * Convenience passthrough for {@link CustomPersistenceHandlerAdapter} that provides a method for class detection
 * based on the provided constructor.
 * 
 * @author Andre Azzolini (apazzolini)
 */
public class ClassCustomPersistenceHandlerAdapter extends CustomPersistenceHandlerAdapter {
    
    List<Class<?>> handledClasses = new ArrayList<Class<?>>();
    
    public ClassCustomPersistenceHandlerAdapter(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            handledClasses.add(clazz);
        }
    }
    
    protected boolean classMatches(PersistencePackage pkg) {
        String ceilingEntityFullyQualifiedClassname = pkg.getCeilingEntityFullyQualifiedClassname();
        for (Class<?> clazz : handledClasses) {
            if (clazz.getName().equals(ceilingEntityFullyQualifiedClassname)) {
                return true;
            }
        }

        return false;
    }

    protected boolean classIsAssignableFrom(PersistencePackage pkg) {
        try {
            String ceilingEntityFullyQualifiedClassname = pkg.getCeilingEntityFullyQualifiedClassname();
            Class ceilingEntityClass = Class.forName(ceilingEntityFullyQualifiedClassname);
            for (Class<?> clazz : handledClasses) {
                if (clazz.isAssignableFrom(ceilingEntityClass)) {
                    return true;
                }
            }
        } catch (ClassNotFoundException e) {
            // do nothing
        }

        return false;
    }

}
