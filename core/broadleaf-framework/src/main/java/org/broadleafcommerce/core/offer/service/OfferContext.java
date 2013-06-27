/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.core.offer.service;


public class OfferContext {
    
    private static final ThreadLocal<OfferContext> OFFERCONTEXT = new ThreadLocal<OfferContext>();
    
    public static OfferContext getOfferContext() {
        return OFFERCONTEXT.get();
    }
    
    public static void setOfferContext(OfferContext offerContext) {
        OFFERCONTEXT.set(offerContext);
    }

    protected Boolean executePromotionCalculation = true;

    public Boolean getExecutePromotionCalculation() {
        return executePromotionCalculation;
    }

    public void setExecutePromotionCalculation(Boolean executePromotionCalculation) {
        this.executePromotionCalculation = executePromotionCalculation;
    }
}
