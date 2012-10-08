/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.pricelist.domain;

import org.broadleafcommerce.common.currency.domain.BroadleafCurrency;
import org.broadleafcommerce.common.currency.domain.BroadleafCurrencyImpl;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * 
 */
    @Entity
    @Inheritance(strategy = InheritanceType.JOINED)
    @Table(name = "BLC_PRICE_LIST")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
    @AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "PriceListImpl_friendyName")
    public class PriceListImpl implements java.io.Serializable,PriceList {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "PriceListID", strategy = GenerationType.TABLE)
    @TableGenerator(name = "PriceListID", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "PriceListID", allocationSize = 50)
    @Column(name = "PRICE_LIST_ID")
    @AdminPresentation(friendlyName = "PriceListImpl_ID", group = "PriceListImpl_Primary_Key", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;



    @Column (name = "FRIENDLY_NAME")
    @AdminPresentation(friendlyName = "PriceListImpl_Friendly_Name", order=2, group = "PriceListImpl_Details", prominent=true)
    protected String friendlyName;

    @Column (name = "PRICE_KEY",nullable=false)
    @AdminPresentation(friendlyName = "PriceListImpl_Key", order=2, group = "PriceListImpl_Details", prominent=true)
    protected String priceKey;

    @Column (name = "USE_DEFAULT_FLAG")
    @AdminPresentation(friendlyName = "PriceListImpl_Is_Default", order=4, group = "PriceListImpl_Details", prominent=false)
    protected Boolean useDefaultIfNotFound;
    
    @ManyToOne(targetEntity = BroadleafCurrencyImpl.class)
    @JoinColumn(name = "CURRENCY_CODE")
    @AdminPresentation(friendlyName = "PriceListImpl_Currency", order=2,group = "PriceListImpl_Details", excluded = true, visibility = VisibilityEnum.GRID_HIDDEN)
    @AdminPresentationToOneLookup(lookupDisplayProperty="friendlyName")
    protected BroadleafCurrency currencyCode;
    
    
    @Override
    public String getFriendlyName() {
        return friendlyName;
    }

    @Override
    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    @Override
    public void setDefaultFlag(Boolean defaultFlag) {
        this.useDefaultIfNotFound = defaultFlag;
    }

    @Override
    public Boolean getDefaultFlag() {
        return useDefaultIfNotFound;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PriceListImpl)) {
            return false;
        }

        PriceListImpl priceList = (PriceListImpl) o;

        if (currencyCode != null ? !currencyCode.equals(priceList.currencyCode) : priceList.currencyCode != null) {
            return false;
        }
        if (priceKey != null ? !priceKey.equals(priceList.priceKey) : priceList.priceKey != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = currencyCode != null ? currencyCode.hashCode() : 0;
        result = 31 * result + (priceKey != null ? priceKey.hashCode() : 0);
        return result;
    }
    @Override
    public Long getId() {
        return id;
    }
    @Override
    public void setId(Long id) {
        this.id = id;
    }
    @Override
    public String getPriceKey() {
        return priceKey;
    }

    @Override
    public void setPriceKey(String priceKey) {
        this.priceKey = priceKey;
    }

    public Boolean getUseDefaultIfNotFound() {
        return useDefaultIfNotFound;
    }

    public void setUseDefaultIfNotFound(Boolean useDefaultIfNotFound) {
        this.useDefaultIfNotFound = useDefaultIfNotFound;
    }

    @Override
    public BroadleafCurrency getCurrencyCode() {
        return currencyCode;
    }

    @Override
    public void setCurrencyCode(BroadleafCurrency currencyCode) {
        this.currencyCode = currencyCode;
    }
}
