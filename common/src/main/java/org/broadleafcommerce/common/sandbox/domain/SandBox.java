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

package org.broadleafcommerce.common.sandbox.domain;

import org.broadleafcommerce.common.site.domain.Site;

import java.io.Serializable;

public interface SandBox extends Serializable {

    public Long getId();

    public void setId(Long id);

    /**
     * The name of the sandbox.
     * Certain sandbox names are reserved in the system.    User created
     * sandboxes cannot start with "", "approve_", or "deploy_".
     *
     * @return String sandbox name
     */
    public String getName();

    public void setName(String name);

    public SandBoxType getSandBoxType();

    public void setSandBoxType(SandBoxType sandBoxType);

    public Long getAuthor();

    public void setAuthor(Long author);

    public SandBox clone();
}


