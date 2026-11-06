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

import org.broadleafcommerce.common.exception.SecurityServiceException
import org.broadleafcommerce.openadmin.dto.PersistencePackage
import org.broadleafcommerce.openadmin.dto.SectionCrumb
import org.broadleafcommerce.openadmin.server.security.domain.AdminUserImpl
import org.broadleafcommerce.openadmin.server.security.extension.AdminSecurityCheckExtensionManager
import org.broadleafcommerce.openadmin.server.security.remote.AdminSecurityServiceRemote
import org.broadleafcommerce.openadmin.server.security.remote.EntityOperationType
import org.broadleafcommerce.openadmin.server.security.service.AdminSecurityService

import spock.lang.Specification

/**
 * Regression coverage for BroadleafCommerce/QA#5506: a client-supplied sectionCrumb used to be able to authorize
 * an operation on a completely unrelated ceiling entity, as long as the user had permission on whatever entity the
 * crumb named (an OR across the target entity and every crumb). See
 * {@link AdminSecurityServiceRemote#securityCheck(PersistencePackage, EntityOperationType)}.
 *
 * @author Claude Opus 5.5
 */
class AdminSecurityServiceRemoteSpec extends Specification {

    static final String PERMITTED_ENTITY = "org.broadleafcommerce.core.offer.domain.Offer"
    static final String UNRELATED_ENTITY = "org.broadleafcommerce.openadmin.server.security.domain.AdminUser"

    AdminSecurityServiceRemote remote
    AdminSecurityService securityService

    def setup() {
        // A Spy lets the real securityCheck() logic run while only getPersistentAdminUser() is stubbed, since
        // the real implementation reads from the (unset, in this test) Spring SecurityContext.
        remote = Spy(AdminSecurityServiceRemote)
        remote.getPersistentAdminUser() >> new AdminUserImpl()

        securityService = Mock(AdminSecurityService)
        securityService.isUserQualifiedForOperationOnCeilingEntity(_, _, PERMITTED_ENTITY) >> true
        securityService.isUserQualifiedForOperationOnCeilingEntity(_, _, { it != PERMITTED_ENTITY }) >> false
        securityService.doesOperationExistForCeilingEntity(_, _) >> true
        remote.securityService = securityService

        remote.securityCheckExtensionManager = new AdminSecurityCheckExtensionManager()
    }

    def "a crumb for an unrelated, permitted entity cannot authorize a top-level operation on an entity the user lacks permission for"() {
        given: "a top-level request (no sectionEntityField) targeting an entity the user cannot access"
        PersistencePackage pkg = new PersistencePackage()
        pkg.securityCeilingEntityFullyQualifiedClassname = UNRELATED_ENTITY
        pkg.sectionCrumbs = [crumb(PERMITTED_ENTITY)] as SectionCrumb[]

        when: "the attacker supplies a crumb naming an entity they ARE permitted on"
        remote.securityCheck(pkg, EntityOperationType.FETCH)

        then: "the crumb must not leak permission across onto the unrelated target entity"
        thrown(SecurityServiceException)
    }

    def "a crumb still authorizes a genuine, server-identified sub-collection request"() {
        given: "a request flagged by the server as a real sub-collection fetch (sectionEntityField is only ever " +
                "populated after resolving a real field on the ceiling entity's own class metadata)"
        PersistencePackage pkg = new PersistencePackage()
        pkg.securityCeilingEntityFullyQualifiedClassname = UNRELATED_ENTITY
        pkg.sectionEntityField = "someRealCollectionField"
        pkg.sectionCrumbs = [crumb(PERMITTED_ENTITY)] as SectionCrumb[]

        when: "the user navigated here from a section they do have permission on"
        remote.securityCheck(pkg, EntityOperationType.FETCH)

        then: "the pre-existing collection-editing behavior is unaffected by the fix"
        noExceptionThrown()
    }

    def "direct permission on the target entity still works with no crumbs present"() {
        given:
        PersistencePackage pkg = new PersistencePackage()
        pkg.securityCeilingEntityFullyQualifiedClassname = PERMITTED_ENTITY

        when:
        remote.securityCheck(pkg, EntityOperationType.FETCH)

        then:
        noExceptionThrown()
    }

    SectionCrumb crumb(String sectionIdentifier) {
        SectionCrumb crumb = new SectionCrumb()
        crumb.sectionIdentifier = sectionIdentifier
        crumb.sectionId = "1"
        return crumb
    }

}
