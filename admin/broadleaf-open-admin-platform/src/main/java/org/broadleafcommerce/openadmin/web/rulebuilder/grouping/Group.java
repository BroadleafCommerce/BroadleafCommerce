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
package org.broadleafcommerce.openadmin.web.rulebuilder.grouping;

import org.broadleafcommerce.openadmin.web.rulebuilder.BLCOperator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jfischer
 * @author Elbert Bautista (elbertbautista)
 */
public class Group {

    private List<String> phrases = new ArrayList<String>();
    private List<Group> subGroups = new ArrayList<Group>();
    private BLCOperator operatorType;
    private Boolean isTopGroup = false;

    public List<String> getPhrases() {
        return phrases;
    }

    public BLCOperator getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(BLCOperator operatorType) {
        this.operatorType = operatorType;
    }

    public List<Group> getSubGroups() {
        return subGroups;
    }

    public Boolean getIsTopGroup() {
        return isTopGroup;
    }

    public void setIsTopGroup(Boolean isTopGroup) {
        this.isTopGroup = isTopGroup;
    }
}
