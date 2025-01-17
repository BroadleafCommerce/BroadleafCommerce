/*-
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.common.security.service;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.Policy;
import org.springframework.core.io.ClassPathResource;

public class AntisamyServiceImpl implements AntisamyService {

    private static final String DEFAULT_ANTI_SAMY_POLICY_FILE_LOCATION = "antisamy-myspace.xml";
    private static AntisamyServiceImpl instance = new AntisamyServiceImpl();
    //this is thread safe for the usage of scan()
    private final AntiSamy as = new AntiSamy();
    protected String antiSamyPolicyFileLocation = DEFAULT_ANTI_SAMY_POLICY_FILE_LOCATION;
    //this is thread safe
    private Policy antiSamyPolicy = getAntiSamyPolicy(antiSamyPolicyFileLocation);

    public static AntisamyService getInstance() {
        return instance;
    }

    private static Policy getAntiSamyPolicy(final String policyFileLocation) {
        try {
            return Policy.getInstance(new ClassPathResource(policyFileLocation).getInputStream());
        } catch (Exception e) {
            throw new RuntimeException("Unable to create URL", e);
        }
    }

    @Override
    public String getAntiSamyPolicyFileLocation() {
        return antiSamyPolicyFileLocation;
    }

    @Override
    public void setAntiSamyPolicyFileLocation(String antiSamyPolicyFileLocation) {
        this.antiSamyPolicyFileLocation = antiSamyPolicyFileLocation;
        this.antiSamyPolicy = getAntiSamyPolicy(antiSamyPolicyFileLocation);
    }

    @Override
    public AntiSamy getAntiSamy() {
        return as;
    }

    @Override
    public Policy getAntiSamyPolicy() {
        return antiSamyPolicy;
    }

}
