/*
 * Copyright 2008-20011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.cms.message.service;

import org.broadleafcommerce.cms.message.domain.ContentMessage;
import org.broadleafcommerce.openadmin.server.domain.SandBox;

/**
 * Created by bpolster.
 */
public interface ContentMessageService {


    /**
     * Returns the message with the passed in id.
     *
     * @param id - The id of the message.
     * @return The associated message.
     */
    public ContentMessage findMessageById(Long id);



    /**
     * Returns the message with the passed in id.
     *
     * @param messageKey - The key of the message.
     * @return The associated message.
     */
    public String findMessageValueByKeyAndLanguage(SandBox sandBox, String messageKey, String localeName);


    /**
     * This method is intended to be called from within the CMS
     * admin only.
     *
     * Adds the passed in message to the DB.
     *
     * Creates a sandbox/site if one doesn't already exist.
     */
    public ContentMessage addContentMessage(ContentMessage cm, SandBox destinationSandbox);

    /**
     * This method is intended to be called from within the CMS
     * admin only.
     *
     * Updates the message according to the following rules:
     *
     * 1.  If sandbox has changed from null to a value
     * This means that the user is editing an item in production and
     * the edit is taking place in a sandbox.
     *
     * 2.  If the sandbox has changed from one value to another
     * This means that the user is moving the item from one sandbox
     * to another.
     *
     * Update the siteId for the message to the one associated with the
     * new sandbox
     *
     * 3.  If the sandbox has changed from a value to null
     * This means that the item is moving from the sandbox to production.
     *
     * If an existing production item is found with a matching key, update that item by
     * setting it's archived flag to true.
     *
     * Then, update the sandboxId of the contentMessage being updated to be the
     * sandboxId of the passed in sandbox.
     *
     * 4.  If the sandbox is the same then just update the page.
     */
    public ContentMessage updateContentMessage(ContentMessage message, SandBox sandbox);


    /**
     * If the destinationSandbox is production, this archives the item in production.
     * Otherwise, it sets the deletedFlag of the item in the passed in sandbox.
     *
     * @param message
     * @param destinationSandbox
     * @return
     */
    public void deleteContentMessage(ContentMessage message, SandBox destinationSandbox);

}
