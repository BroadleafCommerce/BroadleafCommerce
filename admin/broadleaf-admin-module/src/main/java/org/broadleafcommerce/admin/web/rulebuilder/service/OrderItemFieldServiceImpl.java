/*
 * #%L
 * BroadleafCommerce Admin Module
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
package org.broadleafcommerce.admin.web.rulebuilder.service;

import org.broadleafcommerce.common.presentation.RuleIdentifier;
import org.broadleafcommerce.common.presentation.RuleOperatorType;
import org.broadleafcommerce.common.presentation.RuleOptionType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldData;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.AbstractRuleBuilderFieldService;
import org.springframework.stereotype.Service;

/**
 * An implementation of a RuleBuilderFieldService
 * that constructs metadata necessary
 * to build the supported fields for an Order Item entity
 *
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blOrderItemFieldService")
public class OrderItemFieldServiceImpl extends AbstractRuleBuilderFieldService {

    //TODO: extensibility mechanism, support i18N
    @Override
    public void init() {
        fields.add(new FieldData.Builder()
                .label("rule_orderItemName")
                .name("name")
                .operators(RuleOperatorType.TEXT)
                .options(RuleOptionType.EMPTY_COLLECTION)
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemPrice")
                .name("price")
                .operators(RuleOperatorType.NUMERIC)
                .options(RuleOptionType.EMPTY_COLLECTION)
                .type(SupportedFieldType.MONEY)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemQuantity")
                .name("quantity")
                .operators(RuleOperatorType.NUMERIC)
                .options(RuleOptionType.EMPTY_COLLECTION)
                .type(SupportedFieldType.INTEGER)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemCategoryName")
                .name("category.name")
                .operators(RuleOperatorType.TEXT)
                .options(RuleOptionType.EMPTY_COLLECTION)
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemCategoryUrl")
                .name("category.url")
                .operators(RuleOperatorType.TEXT)
                .options(RuleOptionType.EMPTY_COLLECTION)
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemProductManufacturer")
                .name("product.manufacturer")
                .operators(RuleOperatorType.TEXT)
                .options(RuleOptionType.EMPTY_COLLECTION)
                .type(SupportedFieldType.STRING)
                .build());
        fields.add(new FieldData.Builder()
                .label("rule_orderItemSkuLongDescription")
                .name("sku.longDescription")
                .operators(RuleOperatorType.TEXT)
                .options(RuleOptionType.EMPTY_COLLECTION)
                .type(SupportedFieldType.STRING)
                .build());
    }

    @Override
    public String getName() {
        return RuleIdentifier.ORDERITEM;
    }

    @Override
    public String getDtoClassName() {
        return "org.broadleafcommerce.core.order.domain.OrderItemImpl";
    }
}
