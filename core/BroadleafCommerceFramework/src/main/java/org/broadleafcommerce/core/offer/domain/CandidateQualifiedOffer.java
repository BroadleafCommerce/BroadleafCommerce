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
package org.broadleafcommerce.core.offer.domain;

import java.util.HashMap;
import java.util.List;

import org.broadleafcommerce.core.order.domain.OrderItem;
import org.broadleafcommerce.money.Money;

/**
 * 
 * @author jfischer
 *
 */
public interface CandidateQualifiedOffer extends CandidateOffer {

	public HashMap<OfferItemCriteria, List<OrderItem>> getCandidateQualifiersMap();

	public void setCandidateQualifiersMap(HashMap<OfferItemCriteria, List<OrderItem>> candidateItemsMap);
	
	public List<OrderItem> getCandidateTargets();

	public void setCandidateTargets(List<OrderItem> candidateTargets);
	
	public Money calculateSavingsForOrderItem(OrderItem chgItem, int qtyToReceiveSavings);
	
}