/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.structure.message.jms;

import org.broadleafcommerce.cms.structure.domain.StructuredContent;
import org.broadleafcommerce.cms.structure.message.ArchivedStructuredContentPublisher;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.HashMap;

/**
 * JMS implementation of ArchivedPagePublisher.
 * Intended usage is to notify other VMs that a pageDTO needs to be
 * evicted from cache.   This occurs when the page is marked as
 * archived - typically because a replacemet page has been
 * promoted to production.
 *
 * Utilizes Spring JMS template pattern where template and destination
 * are configured via Spring.
 *
 * Created by bpolster.
 */
public class JMSArchivedStructuredContentPublisher implements ArchivedStructuredContentPublisher {

    private JmsTemplate archiveStructuredContentTemplate;

    private Destination archiveStructuredContentDestination;

    @Override
    public void processStructuredContentArchive(final StructuredContent sc, final String baseNameKey, final String baseTypeKey) {
        archiveStructuredContentTemplate.send(archiveStructuredContentDestination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                HashMap<String, String> objectMap = new HashMap<String,String>(2);
                objectMap.put("nameKey", baseNameKey);
                objectMap.put("typeKey", baseTypeKey);
                return session.createObjectMessage(objectMap);
            }
        });
    }

    public JmsTemplate getArchiveStructuredContentTemplate() {
        return archiveStructuredContentTemplate;
    }

    public void setArchiveStructuredContentTemplate(JmsTemplate archiveStructuredContentTemplate) {
        this.archiveStructuredContentTemplate = archiveStructuredContentTemplate;
    }

    public Destination getArchiveStructuredContentDestination() {
        return archiveStructuredContentDestination;
    }

    public void setArchiveStructuredContentDestination(Destination archiveStructuredContentDestination) {
        this.archiveStructuredContentDestination = archiveStructuredContentDestination;
    }
}
