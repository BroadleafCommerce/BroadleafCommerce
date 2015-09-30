/*
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
/**
 * 
 */
package org.broadleafcommerce.cms.structure.domain;

import org.broadleafcommerce.cms.field.domain.FieldGroup;
import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.io.Serializable;



/**
 * <p>
 * XREF entity between a {@link StructuredContentFieldTemplate} and a {@link FieldGroup}
 * 
 * <p>
 * This was created to facilitate specifying ordering for the {@link FieldGroup}s within a {@link StructuredContentFieldTemplate}
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
public interface StructuredContentFieldGroupXref extends Serializable, MultiTenantCloneable<StructuredContentFieldGroupXref> {

    /**
     * The order that this field group should have within this template
     */
    Integer getGroupOrder();

    void setGroupOrder(Integer groupOrder);

    StructuredContentFieldTemplate getTemplate();

    void setTemplate(StructuredContentFieldTemplate template);

    FieldGroup getFieldGroup();
    
    void setFieldGroup(FieldGroup fieldGroup);
    
}
