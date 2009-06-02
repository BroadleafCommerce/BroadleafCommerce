/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.profile.service.addressValidation;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.UnhandledException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;

public class AddressStandardAbbreviations {
	protected final Log logger = LogFactory.getLog(getClass());
    private Map<Object,Object> abbreviationMap;

    public Map<Object,Object> getAbbreviationsMap() {
        return abbreviationMap;
    }

    public void setAbbreviationPropertyFile(Resource abbreviationPropertyFile) {
        try {
            Properties props = new Properties();
            props.load(abbreviationPropertyFile.getInputStream());
            abbreviationMap = Collections.unmodifiableMap(props);
        } catch (IOException e) {
            logger.error("Error loading AddressStandardAbbreviations properties file using Resource: " + abbreviationPropertyFile, e);
            throw new UnhandledException(e);
        }
    }
}
