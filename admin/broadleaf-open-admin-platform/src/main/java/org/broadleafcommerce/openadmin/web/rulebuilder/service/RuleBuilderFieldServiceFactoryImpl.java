/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.web.rulebuilder.service;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Factory class that returns the appropriate RuleBuilderFieldService
 * given the service name. The services are injected into the factory defined in applicationContext-servlet-open-admin.xml
 * @see org.broadleafcommerce.openadmin.web.rulebuilder.service.RuleBuilderFieldService
 *
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blRuleBuilderFieldServiceFactory")
public class RuleBuilderFieldServiceFactoryImpl implements RuleBuilderFieldServiceFactory {

    @Resource(name="blRuleBuilderFieldServices")
    protected List<RuleBuilderFieldService> fieldServices;

    @Override
    public RuleBuilderFieldService createInstance(String name) {

        for (RuleBuilderFieldService service : fieldServices) {
            if (service.getName().equals(name)){
                try {
                    return service.clone();
                } catch (CloneNotSupportedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return null;
    }

    @Override
    public List<RuleBuilderFieldService> getFieldServices() {
        return fieldServices;
    }

    @Override
    public void setFieldServices(List<RuleBuilderFieldService> fieldServices) {
        this.fieldServices = fieldServices;
    }
}
