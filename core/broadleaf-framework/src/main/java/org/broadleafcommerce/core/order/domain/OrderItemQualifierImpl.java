/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.core.order.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.persistence.DefaultPostLoaderDao;
import org.broadleafcommerce.common.persistence.PostLoaderDao;
import org.broadleafcommerce.common.util.HibernateUtils;
import org.broadleafcommerce.core.offer.domain.Offer;
import org.broadleafcommerce.core.offer.domain.OfferImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.proxy.HibernateProxy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "BLC_ITEM_OFFER_QUALIFIER")
@Inheritance(strategy=InheritanceType.JOINED)
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blOrderElements")
public class OrderItemQualifierImpl implements OrderItemQualifier {

    public static final Log LOG = LogFactory.getLog(OrderItemQualifierImpl.class);
    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "OrderItemQualifierId")
    @GenericGenerator(
        name = "OrderItemQualifierId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name = "segment_value", value = "OrderItemQualifierImpl"),
            @Parameter(name = "entity_name", value = "org.broadleafcommerce.core.order.domain.OrderItemQualifierImpl")
        }
    )
    @Column(name = "ITEM_OFFER_QUALIFIER_ID")
    protected Long id;

    @ManyToOne(targetEntity = OrderItemImpl.class)
    @JoinColumn(name = "ORDER_ITEM_ID")
    protected OrderItem orderItem;

    @ManyToOne(targetEntity = OfferImpl.class, optional=false)
    @JoinColumn(name = "OFFER_ID")
    protected Offer offer;

    @Column(name = "QUANTITY")
    protected Long quantity;

    @Transient
    protected Offer deproxiedOffer;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public OrderItem getOrderItem() {
        return orderItem;
    }

    @Override
    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    @Override
    public void setOffer(Offer offer) {
        this.offer = offer;
        deproxiedOffer = null;
    }

    @Override
    public Offer getOffer() {
        if (deproxiedOffer == null) {
            PostLoaderDao postLoaderDao = DefaultPostLoaderDao.getPostLoaderDao();

            if (postLoaderDao != null) {
                Long id = offer.getId();
                deproxiedOffer = postLoaderDao.find(OfferImpl.class, id);
            } else if (offer instanceof HibernateProxy) {
                deproxiedOffer = HibernateUtils.deproxy(offer);
            } else {
                deproxiedOffer = offer;
            }
        }

        return deproxiedOffer;
    }

    @Override
    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    @Override
    public Long getQuantity() {
        return quantity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((offer == null) ? 0 : offer.hashCode());
        result = prime * result + ((orderItem == null) ? 0 : orderItem.hashCode());
        result = prime * result + ((quantity == null) ? 0 : quantity.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!getClass().isAssignableFrom(obj.getClass())) return false;
        OrderItemQualifierImpl other = (OrderItemQualifierImpl) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        if (offer == null) {
            if (other.offer != null) return false;
        } else if (!offer.equals(other.offer)) return false;
        if (orderItem == null) {
            if (other.orderItem != null) return false;
        } else if (!orderItem.equals(other.orderItem)) return false;
        if (quantity == null) {
            if (other.quantity != null) return false;
        } else if (!quantity.equals(other.quantity)) return false;
        return true;
    }

}
