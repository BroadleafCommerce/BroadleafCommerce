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
package org.broadleafcommerce.common.sandbox.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public interface SandBox extends Serializable {

    Long getId();

    void setId(Long id);

    /**
     * The name of the sandbox.
     * Certain sandbox names are reserved in the system.    User created
     * sandboxes cannot start with "", "approve_", or "deploy_".
     *
     * @return String sandbox name
     */
    String getName();

    void setName(String name);

    SandBoxType getSandBoxType();

    void setSandBoxType(SandBoxType sandBoxType);

    Long getAuthor();

    void setAuthor(Long author);

    SandBox getParentSandBox();

    void setParentSandBox(SandBox parentSandBox);

    String getColor();

    void setColor(String color);

    Date getGoLiveDate();

    void setGoLiveDate(Date goLiveDate);

    List<Long> getSandBoxIdsForUpwardHierarchy(boolean includeInherited);

    List<Long> getSandBoxIdsForUpwardHierarchy(boolean includeInherited, boolean includeCurrent);

    List<SandBox> getChildSandBoxes();

    void setChildSandBoxes(List<SandBox> childSandBoxes);

    /**
     * @return whether or not this sandbox, or any of its parent sandboxes, has type DEFAULT.
     */
    public boolean getIsInDefaultHierarchy();

}


