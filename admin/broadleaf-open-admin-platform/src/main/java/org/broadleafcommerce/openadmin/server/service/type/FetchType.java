/*
 * #%L
 * broadleaf-enterprise
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
package org.broadleafcommerce.openadmin.server.service.type;

import org.broadleafcommerce.openadmin.web.form.component.ListGrid;

/**
 * Defines the type of fetch and paging technique to be used: {@link #LARGERESULTSET} denotes a lastid approach, rather than an offset, while
 * {@link #DEFAULT} denotes a standard offset and page size technique.
 * </p>
 * This is used primarily to inform the type of UI paging component. See {@link ListGrid#fetchType}.
 *
 * @author Jeff Fischer
 */
public enum FetchType {
    LARGERESULTSET,DEFAULT
}
