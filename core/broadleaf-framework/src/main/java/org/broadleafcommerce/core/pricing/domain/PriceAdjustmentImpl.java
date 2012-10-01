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
    @Table(name = "BLC_PRICE_ADJUSTMENT")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
    @AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "PriceAdjustmentImpl_friendyName")
    public class PriceAdjustmentImpl implements java.io.Serializable,PriceAdjustment {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "PriceAdjustmentID", strategy = GenerationType.TABLE)
    @TableGenerator(name = "PriceAdjustmentID", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "PriceAdjustmentID", allocationSize = 50)
    @Column(name = "PRICE_ADJUSTMENT_ID")
    @AdminPresentation(friendlyName = "PriceAdjustmentImpl_Sku_ID", group = "PriceAdjustmentImpl_Primary_Key", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;
    
    /** The sale price. */
    @Column(name = "PRICE_ADJUSTMENT", precision=19, scale=5)
    @AdminPresentation(friendlyName = "Adjustment", order=2, prominent=true, fieldType=SupportedFieldType.MONEY, groupOrder=3)
    protected BigDecimal priceAdjustment;

  
    @Override
    public BigDecimal getPriceAdjustment() {
        return priceAdjustment;
    }

    @Override
    public void setPriceAdjustment(BigDecimal priceAdjustment) {
        this.priceAdjustment = priceAdjustment;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

 


}
