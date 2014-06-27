/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.browsertest.spec

import geb.Browser
import geb.spock.GebReportingSpec


/**
 * Root spec that other specs should derive from. This gives all of the tests login functionality
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
abstract class BroadleafAdminSpec extends GebReportingSpec {

    def setupSpec() {
        Browser.drive(getBrowser()) {
            go ""
            $('form').j_username = 'admin'
            $('form').j_password = 'admin'
            $('input[type=submit]').click()
        }
    }
    
}
