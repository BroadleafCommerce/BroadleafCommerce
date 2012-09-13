/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.cms.page.domain;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * Implementations of this interface contain item rule data that is used for targeting
 * <code>Page</code>s.
 * <br>
 * <br>
 * For example, a <code>Page</code>  could be setup to only show to user's
 * who have a particular product in their cart.
 *
 * @see org.broadleafcommerce.core.order.service.PageCartRuleProcessor
 * @author bpolster
 */
public interface PageItemCriteria extends Serializable {

    /**
     * Gets the primary key.
     *
     * @return the primary key
     */
    @Nullable
    public Long getId();

    /**
     * Sets the primary key.
     *
     * @param id the new primary key
     */
    public void setId(@Nullable Long id);

    /**
     *
     * @return
     */
	public Integer getQuantity();

    /**
     *
     * @param quantity
     */
	public void setQuantity(Integer quantity);

    /**
     * Returns the MVEL rule to apply against an order to determine if
     * this content should be displayed.
     *
     * @return the rule as an MVEL string
     */
	public String getOrderItemMatchRule();

    /**
     * An valid MVEL syntax rule to be used to process the
     *
     * @param orderItemMatchRule
     */
	public void setOrderItemMatchRule(String orderItemMatchRule);

    /**
     * Returns the parent <code>Page</code> to which this
     * field belongs.
     *
     * @return
     */
    @Nonnull
    public Page getPage();

    /**
     * Sets the parent <code>Page</code>.
     * @param page
     */
    public void setPage(@Nonnull Page page);

    /**
     * Builds a copy of this item.   Used by the content management system when an
     * item is edited.
     *
     * @return a copy of this item
     */
    @Nonnull
    public PageItemCriteria cloneEntity();
	
}