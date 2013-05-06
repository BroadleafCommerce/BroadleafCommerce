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

package org.broadleafcommerce.core.offer.service.processor;

import org.broadleafcommerce.core.offer.domain.Offer;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Priyesh Patel
 */
public class AbstractBaseProcessorExtensionManager implements AbstractBaseProcessorExtensionListener {
    
    protected List<AbstractBaseProcessorExtensionListener> listeners = new ArrayList<AbstractBaseProcessorExtensionListener>();

    
    public List<AbstractBaseProcessorExtensionListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<AbstractBaseProcessorExtensionListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public List<Offer> removeAdditionalOffers(List<Offer> offers, AbstractBaseProcessor processor) {
        for (AbstractBaseProcessorExtensionListener listener : listeners) {
            offers = listener.removeAdditionalOffers(offers, processor);
        }
        return offers;
    }

    @Override
    public List<Offer> removeOutOfDateOffers(List<Offer> offers, AbstractBaseProcessor processor) {
        for (AbstractBaseProcessorExtensionListener listener : listeners) {
            offers = listener.removeOutOfDateOffers(offers, processor);
        }
        return offers;
    }

}
