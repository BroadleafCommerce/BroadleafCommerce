/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2024 Broadleaf Commerce
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
package org.broadleafcommerce.core.pricing.service.tax.provider;

import org.apache.commons.lang.StringUtils;
import org.broadleafcommerce.common.config.domain.ModuleConfiguration;
import org.broadleafcommerce.common.i18n.domain.ISOCountry;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupFee;
import org.broadleafcommerce.core.order.domain.FulfillmentGroupItem;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.domain.TaxDetail;
import org.broadleafcommerce.core.order.domain.TaxType;
import org.broadleafcommerce.core.pricing.service.exception.TaxException;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.State;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * <p>
 * Simple factor-based tax module that can be configured by adding rates for
 * specific postalCodes, city, state, or country.
 *
 * <p>
 * Through configuration, this module can be used to set a specific tax rate for items and shipping for a given postal code,
 * city, state, or country.
 * 
 * <p>
 * Utilizes the fulfillment group's address to determine the tax location.
 * 
 * <p>
 * Useful for those with very simple tax needs that want to configure rates programmatically.
 * 
 * @author jfischer, brian polster
 * @author Phillip Verheyden (phillipuniverse)
 */
public class SimpleTaxProvider implements TaxProvider {

    protected Map<String, Double> itemPostalCodeTaxRateMap;
    protected Map<String, Double> itemCityTaxRateMap;
    protected Map<String, Double> itemStateTaxRateMap;
    protected Map<String, Double> itemCountryTaxRateMap;

    protected Map<String, Double> fulfillmentGroupPostalCodeTaxRateMap;
    protected Map<String, Double> fulfillmentGroupCityTaxRateMap;
    protected Map<String, Double> fulfillmentGroupStateTaxRateMap;
    protected Map<String, Double> fulfillmentGroupCountryTaxRateMap;

    protected Double defaultItemTaxRate;
    protected Double defaultFulfillmentGroupTaxRate;

    protected boolean taxFees;
    
    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfig;
    
    @Override
    public boolean canRespond(ModuleConfiguration config) {
        // this will only be executed with null module configurations
        return config == null;
    }

    @Override
    public Order calculateTaxForOrder(Order order, ModuleConfiguration config) throws TaxException {
        if (!order.getCustomer().isTaxExempt()) {
            for (FulfillmentGroup fulfillmentGroup : order.getFulfillmentGroups()) {
                handleFulfillmentGroupItemTaxes(fulfillmentGroup);
                handleFulfillmentGroupFeeTaxes(fulfillmentGroup);
                handleFulfillmentGroupTaxes(fulfillmentGroup);
            }
        }

        return order;
    }

    protected void handleFulfillmentGroupItemTaxes(FulfillmentGroup fulfillmentGroup) {
        for (FulfillmentGroupItem fgItem : fulfillmentGroup.getFulfillmentGroupItems()) {
            if (isItemTaxable(fgItem)) {
                applyTaxFactor(fgItem.getTaxes(), determineItemTaxRate(fulfillmentGroup.getAddress()), fgItem.getTotalItemTaxableAmount());
            }
        }
    }

    protected void handleFulfillmentGroupFeeTaxes(FulfillmentGroup fulfillmentGroup) {
        for (FulfillmentGroupFee fgFee : fulfillmentGroup.getFulfillmentGroupFees()) {
            if (isFeeTaxable(fgFee)) {
                applyTaxFactor(fgFee.getTaxes(), determineItemTaxRate(fulfillmentGroup.getAddress()), fgFee.getAmount());
            }
        }
    }

    protected void handleFulfillmentGroupTaxes(FulfillmentGroup fulfillmentGroup) {
        applyTaxFactor(fulfillmentGroup.getTaxes(), determineTaxRateForFulfillmentGroup(fulfillmentGroup), fulfillmentGroup.getFulfillmentPrice());
    }

    protected void applyTaxFactor(List<TaxDetail> taxes, BigDecimal taxFactor, Money taxMultiplier) {
        TaxDetail tax = findExistingTaxDetail(taxes);
        boolean shouldUpdateOrCreateTaxRecord = taxFactor != null && taxFactor.compareTo(BigDecimal.ZERO) != 0;
        boolean shouldRemoveTaxRecord = (taxFactor == null || taxFactor.compareTo(BigDecimal.ZERO) == 0) && tax != null;
        if (shouldUpdateOrCreateTaxRecord) {
            if (tax == null) {
                tax = entityConfig.createEntityInstance(TaxDetail.class.getName(), TaxDetail.class);
                tax.setType(TaxType.COMBINED);
                taxes.add(tax);
            }
            tax.setRate(taxFactor);
            tax.setAmount(taxMultiplier.multiply(taxFactor));
        } else if (shouldRemoveTaxRecord) {
            taxes.remove(tax);
        }
    }

    protected TaxDetail findExistingTaxDetail(List<TaxDetail> taxes) {
        for (TaxDetail detail : taxes) {
            if (detail.getType().equals(TaxType.COMBINED)) {
                return detail;
            }
        }
        return null;
    }

    @Override
    public Order commitTaxForOrder(Order order, ModuleConfiguration config) throws TaxException {
        // intentionally left blank; no tax needs to be committed as this already has the tax details on the order
        return order;
    }

    @Override
    public void cancelTax(Order order, ModuleConfiguration config) throws TaxException {
        // intentionally left blank; tax never got committed so it never gets cancelled
    }

    /**
     * Returns the taxAmount for the passed in postal code or
     * null if no match is found.
     *
     * @param postalCode
     * @return
     */
    public Double lookupPostalCodeRate(Map<String,Double> postalCodeTaxRateMap, String postalCode) {
        if (postalCodeTaxRateMap != null && postalCode != null) {
            return postalCodeTaxRateMap.get(postalCode);
        }
        return null;
    }

    /**
     * Changes the city to upper case before checking the
     * configuration.
     *
     * Return null if no match is found.
     *
     * @param cityTaxRateMap, city
     * @return
     */
    public Double lookupCityRate(Map<String,Double> cityTaxRateMap, String city) {
        if (cityTaxRateMap != null && city != null) {
            city = city.toUpperCase();
            return cityTaxRateMap.get(city);
        }
        return null;
    }

    /**
     * Returns the taxAmount for the passed in state or
     * null if no match is found.
     *
     * First checks the abbreviation (uppercase) followed by the name (uppercase).
     *
     * @param stateTaxRateMap, state
     * @return
     */
    public Double lookupStateRate(Map<String,Double> stateTaxRateMap, State state) {
        if (stateTaxRateMap != null && state != null && state.getAbbreviation() != null) {
            String stateAbbr = state.getAbbreviation().toUpperCase();
            Double rate = stateTaxRateMap.get(stateAbbr);
            if (rate == null && state.getName() != null) {
                String stateName = state.getName().toUpperCase();
                return stateTaxRateMap.get(stateName);
            } else {
                return rate;
            }
        }
        return null;
    }

    /**
     * Returns the taxAmount for the passed in stateProvinceRegion or
     * null if no match is found.
     *
     * First checks the abbreviation (uppercase) followed by the name (uppercase).
     *
     * @param stateTaxRateMap, stateProvinceRegion
     * @return
     */
    public Double lookupStateRate(Map<String,Double> stateTaxRateMap, String stateProvinceRegion) {
        if (stateTaxRateMap != null && StringUtils.isNotBlank(stateProvinceRegion)) {
            return stateTaxRateMap.get(stateProvinceRegion);
        }
        return null;
    }

    /**
     * Returns the taxAmount for the passed in country or
     * null if no match is found.
     *
     * First checks the abbreviation (uppercase) followed by the name (uppercase).
     *
     * @param countryTaxRateMap, country
     * @return
     */
    public Double lookupCountryRate(Map<String,Double> countryTaxRateMap, Country country) {
        if (countryTaxRateMap != null && country != null && country.getAbbreviation() != null) {
            String cntryAbbr = country.getAbbreviation().toUpperCase();
            Double rate = countryTaxRateMap.get(cntryAbbr);
            if (rate == null && country.getName() != null) {
                String countryName = country.getName().toUpperCase();
                return countryTaxRateMap.get(countryName);
            } else {
                return rate;
            }
        }
        return null;
    }

    /**
     * Returns the taxAmount for the passed in country or
     * null if no match is found.
     *
     * First checks the alpha2 (uppercase) followed by the name (uppercase).
     *
     * @param countryTaxRateMap, isoCountry
     * @return
     */
    public Double lookupCountryRate(Map<String,Double> countryTaxRateMap, ISOCountry isoCountry) {
        if (countryTaxRateMap != null && isoCountry != null && isoCountry.getAlpha2() != null) {
            String cntryAbbr = isoCountry.getAlpha2().toUpperCase();
            Double rate = countryTaxRateMap.get(cntryAbbr);
            if (rate == null && isoCountry.getName() != null) {
                String countryName = isoCountry.getName().toUpperCase();
                return countryTaxRateMap.get(countryName);
            } else {
                return rate;
            }
        }
        return null;
    }

    protected boolean isItemTaxable(FulfillmentGroupItem item) {
        return item.getOrderItem().isTaxable();
    }

    protected boolean isFeeTaxable(FulfillmentGroupFee fee) {
        return fee.isTaxable();
    }


    /**
     * Uses the passed in address to determine if the item is taxable.
     *
     * Checks the configured maps in order - (postal code, city, state, country)
     *
     * @param address
     * @return
     */
    public BigDecimal determineItemTaxRate(Address address) {
        if (address != null) {
            Double postalCodeRate = lookupPostalCodeRate(itemPostalCodeTaxRateMap, address.getPostalCode());
            if (postalCodeRate != null) {
                return BigDecimal.valueOf(postalCodeRate);
            }
            Double cityCodeRate = lookupCityRate(itemCityTaxRateMap, address.getCity());
            if (cityCodeRate != null) {
                return BigDecimal.valueOf(cityCodeRate);
            }

            Double stateCodeRate;
            if (StringUtils.isNotBlank(address.getStateProvinceRegion())) {
                stateCodeRate = lookupStateRate(itemStateTaxRateMap, address.getStateProvinceRegion());
            } else {
                stateCodeRate = lookupStateRate(itemStateTaxRateMap, address.getState());
            }

            if (stateCodeRate != null) {
                return BigDecimal.valueOf(stateCodeRate);
            }

            Double countryCodeRate;
            if (address.getIsoCountryAlpha2() != null) {
                countryCodeRate = lookupCountryRate(itemCountryTaxRateMap, address.getIsoCountryAlpha2());
            } else {
                countryCodeRate = lookupCountryRate(itemCountryTaxRateMap, address.getCountry());
            }

            if (countryCodeRate != null) {
                return BigDecimal.valueOf(countryCodeRate);
            }
        }

        if (defaultItemTaxRate != null) {
            return BigDecimal.valueOf(defaultItemTaxRate);
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Uses the passed in address to determine if the item is taxable.
     *
     * Checks the configured maps in order - (postal code, city, state, country)
     *
     * @param fulfillmentGroup
     * @return
     */
    public BigDecimal determineTaxRateForFulfillmentGroup(FulfillmentGroup fulfillmentGroup) {
        boolean isTaxable = true;

        if (fulfillmentGroup.isShippingPriceTaxable() != null) {
            isTaxable = fulfillmentGroup.isShippingPriceTaxable();
        }

        if (isTaxable) {
            Address address = fulfillmentGroup.getAddress();
            if (address != null) {
                Double postalCodeRate = lookupPostalCodeRate(fulfillmentGroupPostalCodeTaxRateMap, address.getPostalCode());
                if (postalCodeRate != null) {
                    return BigDecimal.valueOf(postalCodeRate);
                }
                Double cityCodeRate = lookupCityRate(fulfillmentGroupCityTaxRateMap, address.getCity());
                if (cityCodeRate != null) {
                    return BigDecimal.valueOf(cityCodeRate);
                }

                Double stateCodeRate;
                if (StringUtils.isNotBlank(address.getStateProvinceRegion())) {
                    stateCodeRate = lookupStateRate(fulfillmentGroupStateTaxRateMap, address.getStateProvinceRegion());
                } else {
                    stateCodeRate = lookupStateRate(fulfillmentGroupStateTaxRateMap, address.getState());
                }

                if (stateCodeRate != null) {
                    return BigDecimal.valueOf(stateCodeRate);
                }

                Double countryCodeRate;
                if (address.getIsoCountryAlpha2() != null) {
                    countryCodeRate = lookupCountryRate(fulfillmentGroupCountryTaxRateMap, address.getIsoCountryAlpha2());
                } else {
                    countryCodeRate = lookupCountryRate(fulfillmentGroupCountryTaxRateMap, address.getCountry());
                }

                if (countryCodeRate != null) {
                    return BigDecimal.valueOf(countryCodeRate);
                }
            }

            if (defaultFulfillmentGroupTaxRate != null) {
                return BigDecimal.valueOf(defaultFulfillmentGroupTaxRate);
            }
        }
        return BigDecimal.ZERO;
    }

    public Map<String, Double> getItemPostalCodeTaxRateMap() {
        return itemPostalCodeTaxRateMap;
    }

    public void setItemPostalCodeTaxRateMap(Map<String, Double> itemPostalCodeTaxRateMap) {
        this.itemPostalCodeTaxRateMap = itemPostalCodeTaxRateMap;
    }

    public Map<String, Double> getItemCityTaxRateMap() {
        return itemCityTaxRateMap;
    }

    public void setItemCityTaxRateMap(Map<String, Double> itemCityTaxRateMap) {
        this.itemCityTaxRateMap = itemCityTaxRateMap;
    }

    public Map<String, Double> getItemStateTaxRateMap() {
        return itemStateTaxRateMap;
    }

    public void setItemStateTaxRateMap(Map<String, Double> itemStateTaxRateMap) {
        this.itemStateTaxRateMap = itemStateTaxRateMap;
    }

    public Map<String, Double> getItemCountryTaxRateMap() {
        return itemCountryTaxRateMap;
    }

    public void setItemCountryTaxRateMap(Map<String, Double> itemCountryTaxRateMap) {
        this.itemCountryTaxRateMap = itemCountryTaxRateMap;
    }

    public Map<String, Double> getFulfillmentGroupPostalCodeTaxRateMap() {
        return fulfillmentGroupPostalCodeTaxRateMap;
    }

    public void setFulfillmentGroupPostalCodeTaxRateMap(Map<String, Double> fulfillmentGroupPostalCodeTaxRateMap) {
        this.fulfillmentGroupPostalCodeTaxRateMap = fulfillmentGroupPostalCodeTaxRateMap;
    }

    public Map<String, Double> getFulfillmentGroupCityTaxRateMap() {
        return fulfillmentGroupCityTaxRateMap;
    }

    public void setFulfillmentGroupCityTaxRateMap(Map<String, Double> fulfillmentGroupCityTaxRateMap) {
        this.fulfillmentGroupCityTaxRateMap = fulfillmentGroupCityTaxRateMap;
    }

    public Map<String, Double> getFulfillmentGroupStateTaxRateMap() {
        return fulfillmentGroupStateTaxRateMap;
    }

    public void setFulfillmentGroupStateTaxRateMap(Map<String, Double> fulfillmentGroupStateTaxRateMap) {
        this.fulfillmentGroupStateTaxRateMap = fulfillmentGroupStateTaxRateMap;
    }

    public Map<String, Double> getFulfillmentGroupCountryTaxRateMap() {
        return fulfillmentGroupCountryTaxRateMap;
    }

    public void setFulfillmentGroupCountryTaxRateMap(Map<String, Double> fulfillmentGroupCountryTaxRateMap) {
        this.fulfillmentGroupCountryTaxRateMap = fulfillmentGroupCountryTaxRateMap;
    }

    public Double getDefaultItemTaxRate() {
        return defaultItemTaxRate;
    }

    public void setDefaultItemTaxRate(Double defaultItemTaxRate) {
        this.defaultItemTaxRate = defaultItemTaxRate;
    }

    public Double getDefaultFulfillmentGroupTaxRate() {
        return defaultFulfillmentGroupTaxRate;
    }

    public void setDefaultFulfillmentGroupTaxRate(Double defaultFulfillmentGroupTaxRate) {
        this.defaultFulfillmentGroupTaxRate = defaultFulfillmentGroupTaxRate;
    }

}
