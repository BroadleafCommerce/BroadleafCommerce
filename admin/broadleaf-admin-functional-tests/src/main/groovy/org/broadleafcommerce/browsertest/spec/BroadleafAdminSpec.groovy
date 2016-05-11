/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
