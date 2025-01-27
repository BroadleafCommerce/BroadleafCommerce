/*-
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.config.domain.ModuleConfiguration;
import org.broadleafcommerce.common.copy.MultiTenantCloneable;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.money.Money;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * The Interface TaxDetail. A TaxDetail object stores relevant tax information
 * including a tax type, amount, and rate.
 */
public interface TaxDetail extends Serializable, MultiTenantCloneable<TaxDetail> {

    /**
     * Gets the id.
     *
     * @return the id
     */
    Long getId();

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    void setId(Long id);

    /**
     * Gets the tax type
     *
     * @return the tax type
     */
    TaxType getType();

    /**
     * Sets the tax type
     *
     * @param type the tax type
     */
    void setType(TaxType type);

    /**
     * Gets the tax amount
     *
     * @return the tax amount
     */
    Money getAmount();

    /**
     * Sets the tax amount
     *
     * @param amount the tax amount
     */
    void setAmount(Money amount);

    /**
     * Gets the tax rate
     *
     * @return the rate
     */
    BigDecimal getRate();

    /**
     * Sets the tax rate.
     *
     * @param rate name the tax rate
     */
    void setRate(BigDecimal rate);

    BroadleafCurrency getCurrency();

    void setCurrency(BroadleafCurrency currency);

    /**
     * Returns the configuration of the module that was used to calculate taxes. Allows
     * for tracking, especially when more than one module may be used by the system.
     *
     * @return
     */
    ModuleConfiguration getModuleConfiguration();

    /**
     * Sets the module configuration that was used to calculate taxes.  Allows for tracking
     * of which module was used, especially in cases where more than one module is available
     * over time.
     *
     * @param config
     */
    void setModuleConfiguration(ModuleConfiguration config);

    /**
     * Returns the name of the tax jurisdiction. May return null.
     *
     * @return
     */
    String getJurisdictionName();

    /**
     * Optionally sets the name of the tax jurisdiction.
     *
     * @param jurisdiction
     */
    void setJurisdictionName(String jurisdiction);

    /**
     * Gets the name of the tax. May return null.
     *
     * @return
     */
    String getTaxName();

    /**
     * Sets the name of the tax, if applicable.
     *
     * @param taxName
     */
    void setTaxName(String taxName);

    /**
     * Returns the name of the region used for tax calculation. May
     * return null.
     *
     * @return
     */
    String getRegion();

    /**
     * Sets the region, as a string. Typically this will be a
     * State, Province, or County.
     *
     * @param region
     */
    void setRegion(String region);

    /**
     * Returns the country, as a string, used for tax calculation.
     * May return null.
     *
     * @return
     */
    String getCountry();

    /**
     * Sets the country used for tax calculation.
     *
     * @param country
     */
    void setCountry(String country);

}
