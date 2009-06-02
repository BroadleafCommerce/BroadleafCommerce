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
package org.broadleafcommerce.offer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.domain.CustomerImpl;

//TODO: Should rename to CustomerOfferImpl
@Entity
@Table(name = "OFFER_CUSTOMER")
public class OfferCustomerImpl implements Serializable,OfferCustomer {
	public static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "OFFER_CUSTOMER_ID")
	private Long id;

	@ManyToOne(targetEntity = OfferCodeImpl.class)
	@JoinColumn(name = "OFFER_CODE_ID")
	private OfferCode offerCode;

	@ManyToOne(targetEntity = CustomerImpl.class)
	@JoinColumn(name = "CUSTOMER_ID")
	private Customer customer;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public OfferCode getOfferCode() {
		return offerCode;
	}

	public void setOfferCode(OfferCode offerCode) {
		this.offerCode = offerCode;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}


}
