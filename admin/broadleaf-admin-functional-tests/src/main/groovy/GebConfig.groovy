/*
 * #%L
 * BroadleafCommerce Common Libraries
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
import org.apache.commons.lang3.SystemUtils
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.firefox.FirefoxDriver

import geb.buildadapter.SystemPropertiesBuildAdapter


println 'Loading default Broadleaf GebConfig'
// Use the FirefoxDriver by default
driver = { new FirefoxDriver() }
if (!System.getProperty(SystemPropertiesBuildAdapter.BASE_URL_PROPERTY_NAME)) {
    baseUrl = 'http://demo75ip2w.blcqa.com/admin/'
}

if (!System.getProperty(SystemPropertiesBuildAdapter.REPORTS_DIR_PROPERTY_NAME)) {
    reportsDir = 'target/gebreports'
}
waiting {
    timeout = 60
}
environments {

    // See: http://code.google.com/p/selenium/wiki/ChromeDriver
    chrome {
        def chromeDriver = new File(System.getProperty('java.io.tmpdir') + '/chromedriver')
        def system = ''
        if (SystemUtils.IS_OS_MAC) {
            system = 'mac32'
        } else if (SystemUtils.IS_OS_WINDOWS) {
            system = 'win32'
        } else if (SystemUtils.IS_OS_LINUX) {
            system = 'linux64'
        }
        
        downloadDriver(chromeDriver, "http://chromedriver.storage.googleapis.com/2.10/chromedriver_${system}.zip")
        System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, chromeDriver.absolutePath)
        
        driver = { new ChromeDriver() }
    }

    // See: http://code.google.com/p/selenium/wiki/FirefoxDriver
    firefox {
        driver = { new FirefoxDriver() }
    }
    
}

private void downloadDriver(File file, String path) {
    if (!file.exists()) {
        println 'Downloading Chrome driver to ' + file.absolutePath + ' from ' + path
        def ant = new AntBuilder()
        ant.get(src: path, dest: 'driver.zip')
        ant.unzip(src: 'driver.zip', dest: file.parent)
        ant.delete(file: 'driver.zip')
        ant.chmod(file: file, perm: '700')
    }
}
