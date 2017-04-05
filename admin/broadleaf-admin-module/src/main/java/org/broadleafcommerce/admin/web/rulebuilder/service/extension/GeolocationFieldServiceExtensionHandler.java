/*
 * #%L
 * BroadleafCommerce Admin Module
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package org.broadleafcommerce.admin.web.rulebuilder.service.extension;

import org.broadleafcommerce.common.extension.ExtensionResultStatusType;
import org.broadleafcommerce.common.presentation.RuleIdentifier;
import org.broadleafcommerce.common.presentation.RuleOperatorType;
import org.broadleafcommerce.common.presentation.RuleOptionType;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.openadmin.web.rulebuilder.dto.FieldData;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.AbstractRuleBuilderFieldServiceExtensionHandler;
import org.broadleafcommerce.openadmin.web.rulebuilder.service.RuleBuilderFieldServiceExtensionManager;
import org.springframework.stereotype.Component;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author Jon Fleschler (jfleschler)
 */
@Component("blGeolocationFieldServiceExtensionHandler")
public class GeolocationFieldServiceExtensionHandler extends AbstractRuleBuilderFieldServiceExtensionHandler {

    public static final String GEOLOCATON_ATTRIBUTE_NAME = "_blGeolocationAttribute";

    @Resource(name = "blRuleBuilderFieldServiceExtensionManager")
    protected RuleBuilderFieldServiceExtensionManager extensionManager;

    @PostConstruct
    public void init() {
        if (isEnabled()) {
            extensionManager.registerHandler(this);
        }
    }

    @Override
    public ExtensionResultStatusType addFields(List<FieldData> fields, String ruleFieldName, String dtoClassName) {

        if (isGeolocationEnabled() && RuleIdentifier.REQUEST.equals(ruleFieldName)) {
            fields.add(new FieldData.Builder()
                    .label("rule_geolocationCountryCode")
                    .name("countryCode")
                    .operators(RuleOperatorType.TEXT_LIST)
                    .options(RuleOptionType.EMPTY_COLLECTION)
                    .type(SupportedFieldType.STRING)
                    .overrideDtoClassName("org.broadleafcommerce.core.geolocation.GeolocationDTO")
                    .overrideEntityKey(GEOLOCATON_ATTRIBUTE_NAME)
                    .build());

            fields.add(new FieldData.Builder()
                    .label("rule_geolocationCountryName")
                    .name("countryName")
                    .operators(RuleOperatorType.TEXT_LIST)
                    .options(RuleOptionType.EMPTY_COLLECTION)
                    .type(SupportedFieldType.STRING)
                    .overrideDtoClassName("org.broadleafcommerce.core.geolocation.GeolocationDTO")
                    .overrideEntityKey(GEOLOCATON_ATTRIBUTE_NAME)
                    .build());

            fields.add(new FieldData.Builder()
                    .label("rule_geolocationRegionCode")
                    .name("regionCode")
                    .operators(RuleOperatorType.TEXT_LIST)
                    .options(RuleOptionType.EMPTY_COLLECTION)
                    .type(SupportedFieldType.STRING)
                    .overrideDtoClassName("org.broadleafcommerce.core.geolocation.GeolocationDTO")
                    .overrideEntityKey(GEOLOCATON_ATTRIBUTE_NAME)
                    .build());

            fields.add(new FieldData.Builder()
                    .label("rule_geolocationRegionName")
                    .name("regionName")
                    .operators(RuleOperatorType.TEXT_LIST)
                    .options(RuleOptionType.EMPTY_COLLECTION)
                    .type(SupportedFieldType.STRING)
                    .overrideDtoClassName("org.broadleafcommerce.core.geolocation.GeolocationDTO")
                    .overrideEntityKey(GEOLOCATON_ATTRIBUTE_NAME)
                    .build());

            fields.add(new FieldData.Builder()
                    .label("rule_geolocationCity")
                    .name("city")
                    .operators(RuleOperatorType.TEXT_LIST)
                    .options(RuleOptionType.EMPTY_COLLECTION)
                    .type(SupportedFieldType.STRING)
                    .overrideDtoClassName("org.broadleafcommerce.core.geolocation.GeolocationDTO")
                    .overrideEntityKey(GEOLOCATON_ATTRIBUTE_NAME)
                    .build());

            fields.add(new FieldData.Builder()
                    .label("rule_geolocationPostalCode")
                    .name("postalCode")
                    .operators(RuleOperatorType.TEXT_LIST)
                    .options(RuleOptionType.EMPTY_COLLECTION)
                    .type(SupportedFieldType.STRING)
                    .overrideDtoClassName("org.broadleafcommerce.core.geolocation.GeolocationDTO")
                    .overrideEntityKey(GEOLOCATON_ATTRIBUTE_NAME)
                    .build());

            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        return ExtensionResultStatusType.NOT_HANDLED;
    }

    protected boolean isGeolocationEnabled() {
        return BLCSystemProperty.resolveBooleanSystemProperty("geolocation.api.enabled", false);
    }
}
