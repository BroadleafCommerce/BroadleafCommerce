/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.catalog.taglib.tei;

import org.broadleafcommerce.core.web.catalog.taglib.GetProductsByCategoryIdTag;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;
import java.util.ArrayList;
import java.util.List;

public class GetProductsByCategoryIdTei extends TagExtraInfo {

    @Override
    public VariableInfo[] getVariableInfo(TagData tagData) {
        List<VariableInfo> infos = new ArrayList<VariableInfo>(2);

        String variableName = tagData.getAttributeString("var");
        infos.add(new VariableInfo(variableName, String.class.getName(), true, VariableInfo.NESTED));

        variableName = tagData.getAttributeString("categoryId");

        if (variableName != null) {
            variableName = GetProductsByCategoryIdTag.toVariableName(variableName);
            infos.add(new VariableInfo(variableName, String.class.getName(), true, VariableInfo.NESTED));
        }

        return infos.toArray(new VariableInfo[infos.size()]);
    }
}
