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

package org.broadleafcommerce.core.order.domain;

import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
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
import java.math.BigDecimal;

@Entity
@DiscriminatorColumn(name = "TYPE")
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_DISC_ITEM_FEE_PRICE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
@AdminPresentationClass(friendlyName = "DiscreteOrderItemFeePriceImpl_baseDiscreteOrderItemFreePrice")
public class DiscreteOrderItemFeePriceImpl implements DiscreteOrderItemFeePrice  {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "DiscreteOrderItemFeePriceId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "DiscreteOrderItemFeePriceId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "DiscreteOrderItemFeePriceImpl", allocationSize = 50)
    @Column(name = "DISC_ITEM_FEE_PRICE_ID")
    protected Long id;

    @ManyToOne(targetEntity = DiscreteOrderItemImpl.class, optional = false)
    @JoinColumn(name = "ORDER_ITEM_ID")
    protected DiscreteOrderItem discreteOrderItem;

    @Column(name = "AMOUNT", precision=19, scale=5)
    @AdminPresentation(friendlyName = "DiscreteOrderItemFeePriceImpl_Amount", order=2, group = "DiscreteOrderItemFeePriceImpl_Description", prominent=true)
    protected BigDecimal amount;

    @Column(name = "NAME")
    @AdminPresentation(friendlyName = "DiscreteOrderItemFeePriceImpl_Name", order=1, group = "DiscreteOrderItemFeePriceImpl_Description", prominent=true)
    private String name;

    @Column(name = "REPORTING_CODE")
    @AdminPresentation(friendlyName = "DiscreteOrderItemFeePriceImpl_Reporting_Code", order=3, group = "DiscreteOrderItemFeePriceImpl_Description", prominent=true)
    private String reportingCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public DiscreteOrderItem getDiscreteOrderItem() {
		return discreteOrderItem;
	}

	public void setDiscreteOrderItem(DiscreteOrderItem discreteOrderItem) {
		this.discreteOrderItem = discreteOrderItem;
	}

	public Money getAmount() {
        return amount == null ? null : new Money(amount);
    }

    public void setAmount(Money amount) {
        this.amount = Money.toAmount(amount);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReportingCode() {
        return reportingCode;
    }

    public void setReportingCode(String reportingCode) {
        this.reportingCode = reportingCode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        result = prime * result + ((discreteOrderItem == null) ? 0 : discreteOrderItem.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((reportingCode == null) ? 0 : reportingCode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DiscreteOrderItemFeePriceImpl other = (DiscreteOrderItemFeePriceImpl) obj;
        if (amount == null) {
            if (other.amount != null)
                return false;
        } else if (!amount.equals(other.amount))
            return false;
        if (discreteOrderItem == null) {
            if (other.discreteOrderItem != null)
                return false;
        } else if (!discreteOrderItem.equals(other.discreteOrderItem))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (reportingCode == null) {
            if (other.reportingCode != null)
                return false;
        } else if (!reportingCode.equals(other.reportingCode))
            return false;
        return true;
    }
}
