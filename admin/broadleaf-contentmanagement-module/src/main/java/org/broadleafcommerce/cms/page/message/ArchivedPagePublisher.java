package org.broadleafcommerce.cms.page.message;

import org.broadleafcommerce.cms.page.domain.Page;

/**
 * The ArchivedPagePublisher will be notified when a page has
 * been marked as archived.    This provides a convenient cache-eviction
 * point for pages in production.
 *
 * Implementers of this service could send a JMS or AMQP message so
 * that other VMs can evict the item.
 *
 * Created by bpolster.
 */
public interface ArchivedPagePublisher {
    void processPageArchive(Page page, String basePageKey);
}