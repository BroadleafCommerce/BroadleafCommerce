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

package org.broadleafcommerce.core.pricing.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Created by jfischer
 */
    @Entity
    @Inheritance(strategy = InheritanceType.JOINED)
    @Table(name = "BLC_PRICE_DATA")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
    @AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "PriceDataImpl_friendyName")
    public class PriceDataImpl implements java.io.Serializable,PriceData {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "PriceDateID", strategy = GenerationType.TABLE)
    @TableGenerator(name = "PriceDateID", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "PriceDateID", allocationSize = 50)
    @Column(name = "PRICE_DATA_ID")
    @AdminPresentation(friendlyName = "PriceDataImpl_Sku_ID", group = "PriceDataImpl_Primary_Key", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;
    
    /** The sale price. */
    @Column(name = "SALE_PRICE", precision=19, scale=5)
    @AdminPresentation(friendlyName = "SkuImpl_Sku_Sale_Price", order=2, group = "SkuImpl_Price", prominent=true, fieldType=SupportedFieldType.MONEY, groupOrder=3)
    protected BigDecimal salePrice;

    /** The retail price. */
    @Column(name = "RETAIL_PRICE", precision=19, scale=5)
    @AdminPresentation(friendlyName = "SkuImpl_Sku_Retail_Price", order=1, group = "SkuImpl_Price", prominent=true, fieldType= SupportedFieldType.MONEY, groupOrder=3)
    protected BigDecimal retailPrice;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public BigDecimal getSalePrice() {
        return salePrice;
    }

    @Override
    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    @Override
    public BigDecimal getRetailPrice() {
        return retailPrice;
    }

    @Override
    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }



}
