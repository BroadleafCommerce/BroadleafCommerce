/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.common.config.dao;

import org.broadleafcommerce.common.config.domain.MessageSource;
import org.broadleafcommerce.common.locale.domain.Locale;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * This DAO enables access to manage locale specific Message Sources that can be stored in the database.
 * <p/>
 * @author Elbert Bautista (elbertbautista)
 */
public interface MessageSourceDao {

    public MessageSource saveMessageSource(MessageSource messageSource);

    public void deleteMessageSource(MessageSource messageSource);

    public List<MessageSource> readAllMessageSources();

    public MessageSource readMessageSourceByNameAndLocale(String name, @Nonnull Locale locale);

    public MessageSource createNewMessageSource();

    public MessageSource readById(Long id);

    /**
     * Removes the MessageSource from the null-capable cache.
     *
     * @param messageSource the message source instance
     */
    public void removeFromCache(MessageSource messageSource);
}
