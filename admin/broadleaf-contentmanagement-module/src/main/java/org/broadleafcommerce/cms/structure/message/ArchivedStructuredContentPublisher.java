package org.broadleafcommerce.cms.structure.message;

import org.broadleafcommerce.cms.structure.domain.StructuredContent;

/**
 * The ArchivedStructuredContentPublisher will be notified when a StructuredContent item has
 * been marked as archived.    This provides a convenient cache-eviction
 * point for items in production.
 *
 * Implementers of this service could send a JMS or AMQP message so
 * that other VMs can evict the item.
 *
 * Created by bpolster.
 */
public interface ArchivedStructuredContentPublisher {
    void processStructuredContentArchive(StructuredContent sc, String baseTypeKey, String baseNameKey);
}