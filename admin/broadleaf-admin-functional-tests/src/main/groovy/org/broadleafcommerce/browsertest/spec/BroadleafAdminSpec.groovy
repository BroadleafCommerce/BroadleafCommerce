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

import spock.lang.Shared
import spock.lang.Stepwise
import geb.Browser
import geb.spock.GebReportingSpec
import groovy.json.JsonSlurper


/**
 * Root spec that other specs should derive from. This gives all of the tests login functionality
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
abstract class BroadleafAdminSpec extends GebReportingSpec {

    /**
     * The file location on the server where a dump of the database is saved
     */
    @Shared
    String snapshotFile
    
    def setupSpec() {
        if (isSpecStepwise()) {
            loginAsDefaultUser()
        }
        
        String snapshotUrl = System.getProperty("snapshot.url")
        
        if (snapshotUrl) {
            println 'Snapshotting current database state'
            def snapshotJson = new JsonSlurper().parseText(new URL(snapshotUrl).text)
            println snapshotJson.message
            snapshotFile = snapshotJson.filepath
        }
    }
    
    def setup() {
        if (!isSpecStepwise()) {
            loginAsDefaultUser()
        }
    }
    
    def cleanup() {
        if (!isSpecStepwise()) {
            String reloadUrl = System.getProperty("reload.url")
            if (reloadUrl) {
                reloadUrl += "&filepath=$snapshotFile"
                println 'Reloading database from snapshot'
                def reloadJson = new JsonSlurper().parseText(new URL(reloadUrl).text)
                println reloadJson.message
            }
        }
    }
    
    def loginAsDefaultUser() {
        Browser.drive(getBrowser()) {
            go ""
            $('form').j_username = 'admin'
            $('form').j_password = 'admin'
            $('input[type=submit]').click()
        }
    }
    
    def isSpecStepwise() {
        this.class.getAnnotation(Stepwise) != null
    }
    
}
