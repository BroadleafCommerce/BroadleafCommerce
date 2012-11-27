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

package org.broadleafcommerce.profile.core.domain;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;

import javax.persistence.*;

/**
 * @author Jerry Ocanas (jocanas)
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CUSTOMER_GROUP")
public class CustomerGroupImpl implements CustomerGroup {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CUSTOMER_GROUP_ID")
    @AdminPresentation(friendlyName = "CustomerGroupImpl_Customer_Group_Id", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @Column(name = "NAME")
    @AdminPresentation(friendlyName = "CustomerGroupImpl_Name")
    protected String name;
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
