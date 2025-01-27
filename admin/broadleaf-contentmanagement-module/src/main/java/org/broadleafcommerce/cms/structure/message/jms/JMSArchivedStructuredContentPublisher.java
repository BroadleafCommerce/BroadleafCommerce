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
package org.broadleafcommerce.cms.structure.message.jms;

import org.broadleafcommerce.cms.structure.domain.StructuredContent;
import org.broadleafcommerce.cms.structure.message.ArchivedStructuredContentPublisher;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import java.util.HashMap;

import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;

/**
 * JMS implementation of ArchivedPagePublisher.
 * Intended usage is to notify other VMs that a pageDTO needs to be
 * evicted from cache.   This occurs when the page is marked as
 * archived - typically because a replacemet page has been
 * promoted to production.
 * <p>
 * Utilizes Spring JMS template pattern where template and destination
 * are configured via Spring.
 * <p>
 * Created by bpolster.
 */
public class JMSArchivedStructuredContentPublisher implements ArchivedStructuredContentPublisher {

    private JmsTemplate archiveStructuredContentTemplate;

    private Destination archiveStructuredContentDestination;

    @Override
    public void processStructuredContentArchive(
            final StructuredContent sc,
            final String baseNameKey,
            final String baseTypeKey
    ) {
        archiveStructuredContentTemplate.send(archiveStructuredContentDestination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                HashMap<String, String> objectMap = new HashMap<>(2);
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
