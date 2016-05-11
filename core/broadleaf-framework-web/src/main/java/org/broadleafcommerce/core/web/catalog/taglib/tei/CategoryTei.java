/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.catalog.taglib.tei;

import org.broadleafcommerce.core.web.catalog.taglib.CategoryTag;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;
import java.util.ArrayList;
import java.util.List;

public class CategoryTei extends TagExtraInfo {

    @Override
    public VariableInfo[] getVariableInfo(TagData tagData) {
        List<VariableInfo> infos = new ArrayList<VariableInfo>(2);

        String variableName = tagData.getAttributeString("var");
        infos.add(new VariableInfo(variableName, String.class.getName(), true, VariableInfo.NESTED));

        variableName = tagData.getAttributeString("categoryId");

        if (variableName != null) {
            variableName = CategoryTag.toVariableName(variableName);
            infos.add(new VariableInfo(variableName, String.class.getName(), true, VariableInfo.NESTED));
        }

        return infos.toArray(new VariableInfo[infos.size()]);
    }
}
