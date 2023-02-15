/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2023 Broadleaf Commerce
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.currency.util.BroadleafCurrencyUtils;
import org.broadleafcommerce.common.money.Money;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeEntry;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverride;
import org.broadleafcommerce.common.presentation.override.AdminPresentationMergeOverrides;
import org.broadleafcommerce.common.presentation.override.PropertyType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.lang.reflect.Method;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_BUND_ITEM_FEE_PRICE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "blOrderElements")
@AdminPresentationMergeOverrides(
    {
        @AdminPresentationMergeOverride(name = "", mergeEntries =
            @AdminPresentationMergeEntry(propertyType = PropertyType.AdminPresentation.READONLY,
                                            booleanOverrideValue = true))
    }
)
public class BundleOrderItemFeePriceImpl implements BundleOrderItemFeePrice  {

    public static final Log LOG = LogFactory.getLog(BundleOrderItemFeePriceImpl.class);
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "BundleOrderItemFeePriceId")
    @GenericGenerator(
        name="BundleOrderItemFeePriceId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="BundleOrderItemFeePriceImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.order.domain.BundleOrderItemFeePriceImpl")
        }
    )
    @Column(name = "BUND_ITEM_FEE_PRICE_ID")
    protected Long id;

    @ManyToOne(targetEntity = BundleOrderItemImpl.class, optional = false)
    @JoinColumn(name = "BUND_ORDER_ITEM_ID")
    protected BundleOrderItem bundleOrderItem;

    @Column(name = "AMOUNT", precision=19, scale=5)
    @AdminPresentation(friendlyName = "BundleOrderItemFeePriceImpl_Amount", order=2, prominent=true)
    protected BigDecimal amount;

    @Column(name = "NAME")
    @AdminPresentation(friendlyName = "BundleOrderItemFeePriceImpl_Name", order=1, prominent=true)
    private String name;

    @Column(name = "REPORTING_CODE")
    @AdminPresentation(friendlyName = "BundleOrderItemFeePriceImpl_Reporting_Code", order=3, prominent=true)
    private String reportingCode;

    @Column(name = "IS_TAXABLE")
    @AdminPresentation(friendlyName = "BundleOrderItemFeePriceImpl_Taxable", order=4)
    private Boolean isTaxable = Boolean.FALSE;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public BundleOrderItem getBundleOrderItem() {
        return bundleOrderItem;
    }

    @Override
    public void setBundleOrderItem(BundleOrderItem bundleOrderItem) {
        this.bundleOrderItem = bundleOrderItem;
    }

    @Override
    public Money getAmount() {
        return convertToMoney(amount);
    }

    @Override
    public void setAmount(Money amount) {
        this.amount = Money.toAmount(amount);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Boolean isTaxable() {
        return isTaxable;
    }

    @Override
    public void setTaxable(Boolean isTaxable) {
        this.isTaxable = isTaxable;
    }

    @Override
    public String getReportingCode() {
        return reportingCode;
    }

    @Override
    public void setReportingCode(String reportingCode) {
        this.reportingCode = reportingCode;
    }

    public void checkCloneable(BundleOrderItemFeePrice bundleFeePrice) throws CloneNotSupportedException, SecurityException, NoSuchMethodException {
        Method cloneMethod = bundleFeePrice.getClass().getMethod("clone", new Class[]{});
        if (cloneMethod.getDeclaringClass().getName().startsWith("org.broadleafcommerce") && !bundleFeePrice.getClass().getName().startsWith("org.broadleafcommerce")) {
            //subclass is not implementing the clone method
            throw new CloneNotSupportedException("Custom extensions and implementations should implement clone in order to guarantee split and merge operations are performed accurately");
        }
    }

    protected Money convertToMoney(BigDecimal amount) {
        return amount == null ? null : BroadleafCurrencyUtils.getMoney(amount, bundleOrderItem.getOrder().getCurrency());
    }

    @Override
    public BundleOrderItemFeePrice clone() {
        //instantiate from the fully qualified name via reflection
        BundleOrderItemFeePrice clone;
        try {
            clone = (BundleOrderItemFeePrice) Class.forName(this.getClass().getName()).newInstance();
            try {
                checkCloneable(clone);
            } catch (CloneNotSupportedException e) {
                LOG.warn("Clone implementation missing in inheritance hierarchy outside of Broadleaf: " + clone.getClass().getName(), e);
            }
            clone.setAmount(convertToMoney(amount));
            clone.setName(name);
            clone.setReportingCode(reportingCode);
            clone.setBundleOrderItem(bundleOrderItem);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return clone;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        result = prime * result + ((bundleOrderItem == null) ? 0 : bundleOrderItem.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + (isTaxable ? 1231 : 1237);
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((reportingCode == null) ? 0 : reportingCode.hashCode());
        return result;
    }
    
    @Override
    public CreateResponse<BundleOrderItemFeePrice> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<BundleOrderItemFeePrice> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        BundleOrderItemFeePrice cloned = createResponse.getClone();
        cloned.setBundleOrderItem((BundleOrderItem)bundleOrderItem.createOrRetrieveCopyInstance(context).getClone());
        cloned.setAmount(amount == null ? null : new Money(amount));
        cloned.setName(name);
        cloned.setReportingCode(reportingCode);
        cloned.setTaxable(isTaxable == null ? null : isTaxable);
        return  createResponse;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        BundleOrderItemFeePriceImpl other = (BundleOrderItemFeePriceImpl) obj;
        if (amount == null) {
            if (other.amount != null) {
                return false;
            }
        } else if (!amount.equals(other.amount)) {
            return false;
        }
        if (bundleOrderItem == null) {
            if (other.bundleOrderItem != null) {
                return false;
            }
        } else if (!bundleOrderItem.equals(other.bundleOrderItem)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (isTaxable != other.isTaxable) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (reportingCode == null) {
            if (other.reportingCode != null) {
                return false;
            }
        } else if (!reportingCode.equals(other.reportingCode)) {
            return false;
        }
        return true;
    }
}
