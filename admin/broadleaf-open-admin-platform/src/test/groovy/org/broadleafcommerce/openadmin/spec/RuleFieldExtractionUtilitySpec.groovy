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

import org.broadleafcommerce.openadmin.server.service.persistence.module.provider.RuleFieldExtractionUtility
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.DataWrapper
import spock.lang.Specification

/**
 * Unit test for {@link RuleFieldExtractionUtility} focusing on exception handling during JSON deserialization.
 */
class RuleFieldExtractionUtilitySpec extends Specification {

    RuleFieldExtractionUtility utility

    def setup() {
        utility = new RuleFieldExtractionUtility()
    }

    def "null or empty JSON array returns null DataWrapper"() {
        expect:
        utility.convertJsonToDataWrapper(null) == null
        utility.convertJsonToDataWrapper("[]") == null
    }

    def "valid JSON returns populated DataWrapper"() {
        setup:
        String validJson = '{"data": [], "error": null, "rawMvel": null}'

        when:
        DataWrapper dw = utility.convertJsonToDataWrapper(validJson)

        then:
        dw != null
        dw.getError() == null
        dw.getData() != null
    }

    def "invalid JSON does not throw JacksonException but returns error wrapper"() {
        setup:
        String invalidJson = '{"data": [ {invalid_json} ], "error": null}'

        when:
        DataWrapper dw = utility.convertJsonToDataWrapper(invalidJson)

        then:
        noExceptionThrown()
        dw != null
        dw.getError() != null
        dw.getError().contains("Could not deserialize JSON")
    }
}
