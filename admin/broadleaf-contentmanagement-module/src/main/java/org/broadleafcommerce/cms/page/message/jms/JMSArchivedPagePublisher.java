/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.cms.page.message.jms;

import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.message.ArchivedPagePublisher;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

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
public class JMSArchivedPagePublisher implements ArchivedPagePublisher {

    private JmsTemplate archivePageTemplate;

    private Destination archivePageDestination;

    @Override
    public void processPageArchive(final Page page, final String basePageKey) {
        archivePageTemplate.send(archivePageDestination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(basePageKey);
            }
        });
    }

    public JmsTemplate getArchivePageTemplate() {
        return archivePageTemplate;
    }

    public void setArchivePageTemplate(JmsTemplate archivePageTemplate) {
        this.archivePageTemplate = archivePageTemplate;
    }

    public Destination getArchivePageDestination() {
        return archivePageDestination;
    }

    public void setArchivePageDestination(Destination archivePageDestination) {
        this.archivePageDestination = archivePageDestination;
    }
}
