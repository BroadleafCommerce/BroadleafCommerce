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
package org.broadleafcommerce.common.structure.dto;

import java.io.Serializable;

/**
 * StructuredContent data is converted into a DTO since it requires
 * pre-processing.   The data is fairly static so the desire is
 * to cache the value after it has been processed.
 *
 * This DTO represents a compact version of StructuredContentItemCriteria
 *
 * Created by bpolster.
 */
public class ItemCriteriaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Integer qty;
    protected String matchRule;

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getMatchRule() {
        return matchRule;
    }

    public void setMatchRule(String matchRule) {
        this.matchRule = matchRule;
    }
}
