/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.weave;

/**
 * 
 * @author Kelly Tisdell
 *
 */
public interface ConditionalFieldAnnotationCopyTransformersManager {

    /**
     * Based on the entity name, this method will determine if the associated conditionalProperty from the Spring configuration is true.
     *
     * @param entityName
     * @return
     */
    Boolean isEntityEnabled(String entityName);

    /**
     * Retrieve the direct copy transform config info
     *
     * @param entityName
     * @return
     */
    ConditionalFieldAnnotationCopyTransformMemberDTO getTransformMember(String entityName);

}
