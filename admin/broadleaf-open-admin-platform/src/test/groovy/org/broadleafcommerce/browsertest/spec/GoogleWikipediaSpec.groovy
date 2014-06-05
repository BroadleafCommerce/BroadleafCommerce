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

import org.broadleafcommerce.browsertest.page.GoogleHomePage

import geb.spock.GebSpec

class GoogleWikipediaSpec extends GebSpec {

   def "first result for wikipedia search should be wikipedia"() {
       given:
           to GoogleHomePage
       
       expect:
           at GoogleHomePage
//       when:
//       search.field.value("wikipedia")
//
//       then:
//       waitFor { at GoogleResultsPage }
//
//       and:
//       firstResultLink.text() == "Wikipedia"
//
//       when:
//       firstResultLink.click()
//
//       then:
//       waitFor { at WikipediaPage }
   }
}