/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.audit;

import java.util.Date;

/**
 * @author Jeff Fischer
 */
public interface AdminAudit {

    Date getDateCreated();

    Date getDateUpdated();

    void setDateCreated(Date dateCreated);

    void setDateUpdated(Date dateUpdated);

    Long getCreatedBy();

    void setCreatedBy(Long createdBy);

    Long getUpdatedBy();

    void setUpdatedBy(Long updatedBy);

}
