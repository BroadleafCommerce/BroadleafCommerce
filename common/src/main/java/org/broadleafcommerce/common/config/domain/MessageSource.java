/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.common.config.domain;

import org.broadleafcommerce.common.locale.domain.Locale;
import java.io.Serializable;

/**
 * This interface represents a Message Source stored in the database.
 * @author Elbert Bautista (elbertbautista)
 */
public interface MessageSource extends Serializable {

    /**
     * Unique id of the DB record
     * @return the id
     */
    public Long getId();

    /**
     * Sets the id of the DB record
     * @param id
     */
    public void setId(Long id);

    /**
     * The name of the property as it exists in property files (for example cart.title)
     * @return the name
     */
    public String getName();

    /**
     * Sets the property name.
     * @param name
     */
    public void setName(String name);

    /**
     * Returns the property value.
     * @return the property value
     */
    public String getValue();

    /**
     * Sets the property value.
     * @param value
     */
    public void setValue(String value);

    /**
     * Returns a Broadleaf Locale.
     * @return the locale
     */
    public Locale getLocale();

    /**
     * Sets the the Broadleaf Locale value.
     * @param locale
     */
    public void setLocale(Locale locale);


}
