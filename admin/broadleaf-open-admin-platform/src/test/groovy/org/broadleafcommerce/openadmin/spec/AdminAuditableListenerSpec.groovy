/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2026 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.spec

import org.broadleafcommerce.common.web.BroadleafRequestContext
import org.broadleafcommerce.openadmin.audit.AdminAuditable
import org.broadleafcommerce.openadmin.audit.AdminAuditableListener

import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import spock.lang.Specification

class AdminAuditableListenerSpec extends Specification {

    static final Long ADMIN_USER_ID = 100L

    AdminAuditableListener listener = new AdminAuditableListener()

    def setup() {
        BroadleafRequestContext context = new BroadleafRequestContext()
        context.setAdmin(true)
        context.setAdminUserId(ADMIN_USER_ID)
        BroadleafRequestContext.setBroadleafRequestContext(context)
    }

    def cleanup() {
        BroadleafRequestContext.setBroadleafRequestContext(null)
    }

    def "Creation data is set on persist for an entity with an embedded auditable"() {
        setup: "A new entity whose auditable holds no creation data"
        EmbeddedAuditableEntity entity = new EmbeddedAuditableEntity()

        when: "The entity is persisted"
        listener.setAuditCreationAndUpdateData(entity)

        then: "Creation and update data are populated from the admin context"
        entity.auditable.createdBy == ADMIN_USER_ID
        entity.auditable.dateCreated != null
        entity.auditable.updatedBy == ADMIN_USER_ID
        entity.auditable.dateUpdated != null
    }

    def "Existing creation data is not overwritten on persist"() {
        setup: "An entity (e.g. a clone) that already carries creation data"
        Date originalDate = new Date(0L)
        EmbeddedAuditableEntity entity = new EmbeddedAuditableEntity()
        entity.auditable.createdBy = 5L
        entity.auditable.dateCreated = originalDate

        when: "The entity is persisted"
        listener.setAuditCreationAndUpdateData(entity)

        then: "The original creation data is kept and only the update data changes"
        entity.auditable.createdBy == 5L
        entity.auditable.dateCreated == originalDate
        entity.auditable.updatedBy == ADMIN_USER_ID
    }

    @Entity
    static class EmbeddedAuditableEntity {

        @Embedded
        protected AdminAuditable auditable = new AdminAuditable()

    }

}
