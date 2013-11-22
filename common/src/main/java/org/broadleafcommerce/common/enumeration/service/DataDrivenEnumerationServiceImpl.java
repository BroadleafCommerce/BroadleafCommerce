/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.enumeration.service;

import javax.annotation.Resource;

import org.broadleafcommerce.common.enumeration.dao.DataDrivenEnumerationDao;
import org.broadleafcommerce.common.enumeration.domain.DataDrivenEnumeration;
import org.broadleafcommerce.common.enumeration.domain.DataDrivenEnumerationValue;
import org.springframework.stereotype.Service;


@Service("blDataDrivenEnumerationService")
public class DataDrivenEnumerationServiceImpl implements DataDrivenEnumerationService {

    @Resource(name = "blDataDrivenEnumerationDao")
    protected DataDrivenEnumerationDao dao;

    @Override
    public DataDrivenEnumeration findEnumByKey(String enumKey) {
        return dao.readEnumByKey(enumKey);
    }
    
    @Override
    public DataDrivenEnumerationValue findEnumValueByKey(String enumKey, String enumValueKey) {
        return dao.readEnumValueByKey(enumKey, enumValueKey);
    }

}
