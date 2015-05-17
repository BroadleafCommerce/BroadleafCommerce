/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.web.service;

import org.broadleafcommerce.common.media.domain.Media;

/**
 * @author Chad Harchar (charchar)
 */
public interface MediaBuilderService {

    /**
     * Converts the given json {@link String} to {@link org.broadleafcommerce.common.media.domain.Media} given the
     * {@link java.lang.Class} that has been passed in.
     *
     * @param json the {@link String} to be converted to {@link Media}
     * @param type the {@link Class} that the {@link Media} should be
     */
    public Media convertJsonToMedia(String json, Class<?> type);
}
