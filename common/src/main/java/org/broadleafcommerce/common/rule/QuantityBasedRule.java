/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.common.rule;

import java.io.Serializable;

/**
 * Represents a class containing an MVEL rule and an associated quantity.
 *
 * @author Jeff Fischer
 */
public interface QuantityBasedRule extends Serializable {

    /**
     * The quantity for which a match must be found using the rule. This generally
     * equates to order item quantity (e.g. 2 shirts matching the rule are required in order to receive a discount)
     *
     * @return the quantity of matches required
     */
    public Integer getQuantity();

    /**
     * The quantity for which a match must be found using the rule. This generally
     * equates to order item quantity (e.g. 2 shirts matching the rule are required in order to receive a discount)
     *
     * @param quantity the quantity of matches required
     */
    public void setQuantity(Integer quantity);

    /**
     * The rule in the form of an MVEL expression
     *
     * @return the rule as an MVEL string
     */
    public String getMatchRule();

    /**
     * Sets the match rule used to test this item.
     *
     * @param matchRule the rule as an MVEL string
     */
    public void setMatchRule(String matchRule);

    /**
     * The primary key value for this rule object
     *
     * @return the primary key value
     */
    public Long getId();

    /**
     * The primary key value for this rule object
     *
     * @param id the primary key value
     */
    public void setId(Long id);

}
