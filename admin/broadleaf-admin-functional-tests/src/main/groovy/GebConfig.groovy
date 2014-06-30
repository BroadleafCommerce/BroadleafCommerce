/*
 * #%L
 * BroadleafCommerce Common Libraries
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
import org.apache.commons.lang.SystemUtils
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeDriverService
import org.openqa.selenium.firefox.FirefoxDriver


println 'Loading default Broadleaf GebConfig'
// Use the FirefoxDriver by default
driver = { new FirefoxDriver() }
baseUrl = 'http://demo75ip6w.broadleafcommerce.org/admin/'
reportsDir = 'target/gebreports'
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