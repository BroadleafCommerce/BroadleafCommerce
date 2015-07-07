/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.server.util;

import org.broadleafcommerce.openadmin.dto.ClassTree;

import java.util.LinkedHashMap;

/**
 * @author Elbert Bautista (elbertbautista)
 *
 * Utility class to convert the Polymorphic ClassTree into a Map
 */
public class PolymorphicEntityMapUtil {

    public LinkedHashMap<String, String> convertClassTreeToMap(ClassTree polymorphicEntityTree) {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(polymorphicEntityTree.getRight()/2);
        buildPolymorphicEntityMap(polymorphicEntityTree, map);
        return map;
    }

    protected void buildPolymorphicEntityMap(ClassTree entity, LinkedHashMap<String, String> map) {
        String friendlyName = entity.getFriendlyName();
        if (friendlyName != null && !friendlyName.equals("")) {

            //TODO: fix this for i18N
            //check if the friendly name is an i18N key
            //String val = BLCMain.getMessageManager().getString(friendlyName);
            //if (val != null) {
            //    friendlyName = val;
            //}

        }
        map.put(entity.getFullyQualifiedClassname(), friendlyName!=null?friendlyName:entity.getName());
        for (ClassTree child : entity.getChildren()) {
            buildPolymorphicEntityMap(child, map);
        }
    }

}
