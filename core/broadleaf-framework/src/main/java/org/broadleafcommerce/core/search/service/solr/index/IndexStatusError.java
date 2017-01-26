/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.core.search.service.solr.index;

import java.util.Date;

/**
 * Error status information about for a (embedded) Solr instance's index
 *
 * @author Daniel Colgrove
 */
public interface IndexStatusError {
    /**
     * The ID of the {@Link SystemEvent{
     * @param eventId
     */
    void setEventId(Long eventId);

    Long getEventId();

    /**
     * The current retry counter
     * @param retryCount
     */
    void setRetryCount(Integer retryCount);

    Integer getRetryCount();

    /**
     * The date of the event error
     * @param errorDate
     */
    void setErrorDate(Date errorDate);
    Date getErrorDate();
}
