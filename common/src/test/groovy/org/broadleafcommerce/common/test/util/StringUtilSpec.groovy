/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
/**
 * 
 */
package org.broadleafcommerce.common.test.util

import org.broadleafcommerce.common.util.StringUtil

import spock.lang.Specification


/**
 * 
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
class StringUtilSpec extends Specification {

    def "Exact roperty segments"() {
        when: 
            def result = StringUtil.segmentInclusion("fulfillmentLocation.address.phoneFax", "fulfillmentLocation.address.phone")
        then:
            !result
        
        when:
            result = StringUtil.segmentInclusion("fulfillmentLocation.address.phoneFax.phoneNumber", "fulfillmentLocation.address.phoneFax")
        then:
            result
    }
    
}
