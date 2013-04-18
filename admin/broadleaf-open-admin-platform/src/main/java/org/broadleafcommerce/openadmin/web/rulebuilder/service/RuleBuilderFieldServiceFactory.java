package org.broadleafcommerce.openadmin.web.rulebuilder.service;

import java.util.List;

/**
 * @author Jeff Fischer
 */
public interface RuleBuilderFieldServiceFactory {

    RuleBuilderFieldService createInstance(String name);

    List<RuleBuilderFieldService> getFieldServices();

    void setFieldServices(List<RuleBuilderFieldService> fieldServices);
}
