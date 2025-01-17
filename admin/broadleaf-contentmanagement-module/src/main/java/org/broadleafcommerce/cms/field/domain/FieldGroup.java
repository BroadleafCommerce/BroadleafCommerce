/*-
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.cms.field.domain;

import org.broadleafcommerce.cms.structure.domain.StructuredContentFieldGroupXref;
import org.broadleafcommerce.common.copy.MultiTenantCloneable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bpolster.
 */
public interface FieldGroup extends Serializable, MultiTenantCloneable<FieldGroup> {

    List<StructuredContentFieldGroupXref> getFieldGroupXrefs();

    void setFieldGroupXrefs(List<StructuredContentFieldGroupXref> fieldGroupXrefs);

    Long getId();

    void setId(Long id);

    String getName();

    void setName(String name);

    Boolean getInitCollapsedFlag();

    void setInitCollapsedFlag(Boolean initCollapsedFlag);

    List<FieldDefinition> getFieldDefinitions();

    void setFieldDefinitions(List<FieldDefinition> fieldDefinitions);

    Boolean isMasterFieldGroup();

    void setIsMasterFieldGroup(Boolean isMasterFieldGroup);

}
