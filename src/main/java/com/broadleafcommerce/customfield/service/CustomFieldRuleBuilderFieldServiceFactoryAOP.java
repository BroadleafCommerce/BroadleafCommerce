/*
 * Broadleaf Commerce Confidential
 * _______________________________
 *
 * [2009] - [2013] Broadleaf Commerce, LLC
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 */

package com.broadleafcommerce.customfield.service;

import com.broadleafcommerce.customfield.domain.CustomField;
import com.broadleafcommerce.customfield.service.type.CustomFieldTargetType;
import com.broadleafcommerce.customfield.service.type.CustomFieldType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.server.service.persistence.module.FieldManager;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldData;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.RuleBuilderFieldService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Allows the custom field module to contribute dynamically obtained fields to the field list in the rule
 * builder for the target entity. With this approach, the module architecture does not interfere with any
 * <tt>RuleBuilderServiceFactory</tt> overrides (e.g. a version of the factory that takes into account
 * fields retrieved via inspection, rather than explicit declaration, or both).
 *
 * @author Jeff Fischer
 */
public class CustomFieldRuleBuilderFieldServiceFactoryAOP {

    @Resource(name="blCustomFieldService")
    protected CustomFieldService customFieldService;

    public Object process(ProceedingJoinPoint call) throws Throwable {
        final RuleBuilderFieldService response = (RuleBuilderFieldService) call.proceed();
        if (response != null && CustomFieldInfo.RULE_BUILDER_FIELD_SERVICES.containsKey(response.getName())) {
            final ArrayList<FieldData> tempList = new ArrayList<FieldData>();
            tempList.addAll(response.getFields());
            CustomFieldTargetType[] targetTypes = CustomFieldInfo.RULE_BUILDER_FIELD_SERVICES.get(response.getName());
            for (CustomFieldTargetType targetType : targetTypes) {
                List<CustomField> customFields = customFieldService.findByTargetEntityName(targetType.getType());
                for (CustomField customField : customFields) {
                    if (customField.getShowFieldInRuleBuilder()) {
                        SupportedFieldType type = SupportedFieldType.valueOf(customField.getCustomFieldType());
                        StringBuilder fieldName = new StringBuilder();
                        fieldName.append(CustomFieldInfo.RULE_BUILDER_TYPE_PREFIXES.get(response.getName()).get(targetType));
                        fieldName.append(CustomFieldInfo.CUSTOM_FIELD_FIELD_NAMES.get(targetType.getType()));
                        fieldName.append(FieldManager.MAPFIELDSEPARATOR);
                        fieldName.append(customField.getLabel());

                        tempList.add(new FieldData.Builder()
                            .label(targetType.getFriendlyType() + " - " + customField.getFriendlyName())
                            .name(fieldName.toString())
                            .operators(CustomFieldInfo.CUSTOM_FIELD_RULE_OPERATORS.get(CustomFieldType.getInstance
                                    (customField.getCustomFieldType())))
                            .options("[]")
                            .type(type)
                            .build());
                    }
                }
            }

            Collections.sort(tempList, new Comparator<FieldData>() {
                @Override
                public int compare(FieldData o1, FieldData o2) {
                    return o1.getFieldLabel().compareTo(o2.getFieldLabel());
                }
            });

            response.setFields(tempList);
        }

        return response;
    }
}
