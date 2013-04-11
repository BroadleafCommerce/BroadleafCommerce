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

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * Represents a class containing an MVEL rule
 *
 * @author Jeff Fischer
 */
public interface SimpleRule extends Serializable {

    /**
     * The rule in the form of an MVEL expression
     *
     * @return the rule as an MVEL string
     */
    @Nonnull
    public String getMatchRule();

    /**
     * Sets the match rule used to test this item.
     *
     * @param matchRule the rule as an MVEL string
     */
    public void setMatchRule(@Nonnull String matchRule);

}
