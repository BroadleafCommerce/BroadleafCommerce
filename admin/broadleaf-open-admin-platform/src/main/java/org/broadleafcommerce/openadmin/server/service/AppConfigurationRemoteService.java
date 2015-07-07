/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.openadmin.server.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jfischer
 */
@Service("blAppConfigurationRemoteService")
public class AppConfigurationRemoteService implements AppConfigurationService {

    private static final Log LOG = LogFactory.getLog(AppConfigurationRemoteService.class);

    @Resource(name = "blAppConfigurationMap")
    protected Map<String, String> propertyConfigurations = new HashMap<String, String>();

    @Override
    public Boolean getBooleanPropertyValue(String propertyName) {
        if (propertyConfigurations.get(propertyName) == null) {
            return null;
        } else {
            return Boolean.valueOf(propertyConfigurations.get(propertyName));
        }
    }

}
