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

package org.broadleafcommerce.common.config.domain;

import java.io.Serializable;

/**
 * This interface represents a System Property (name/value pair) stored in the database.  It can be used to override
 * Spring-injected properties that are injected using the @Value annotation.
 * <p/>
 * User: Kelly Tisdell
 * Date: 6/20/12
 */
public interface SystemProperty extends Serializable {

    public Long getId();

    public void setId(Long id);

    public String getName();

    public void setName(String name);

    public String getValue();

    public void setValue(String value);

}
