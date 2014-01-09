/*
 * #%L
 * BroadleafCommerce Framework Web
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
package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.core.payment.domain.PaymentResponseItem;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is a JAXB wrapper around PaymentResponseItem.
 * <p/>
 * User: Elbert Bautista
 * Date: 4/26/12
 */
@XmlRootElement(name = "paymentResponseItem")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class PaymentResponseItemWrapper extends BaseWrapper implements APIWrapper<PaymentResponseItem> {

    @XmlElement
    protected Boolean transactionSuccess;

    @XmlElement
    protected String processorResponseCode;

    @XmlElement(name = "element")
    @XmlElementWrapper(name = "additionalFields")
    protected List<MapElementWrapper> additionalFields;

    @Override
    public void wrapDetails(PaymentResponseItem model, HttpServletRequest request) {
        this.processorResponseCode = model.getProcessorResponseCode();
        this.transactionSuccess = model.getTransactionSuccess();
        if (model.getAdditionalFields() != null && !model.getAdditionalFields().isEmpty()) {
            List<MapElementWrapper> mapElementWrappers = new ArrayList<MapElementWrapper>();
            for (String key : model.getAdditionalFields().keySet()) {
                MapElementWrapper mapElementWrapper = new MapElementWrapper();
                mapElementWrapper.setKey(key);
                mapElementWrapper.setValue(model.getAdditionalFields().get(key));
                mapElementWrappers.add(mapElementWrapper);
            }
            this.additionalFields = mapElementWrappers;
        }
    }

    @Override
    public void wrapSummary(PaymentResponseItem model, HttpServletRequest request) {
        wrapDetails(model, request);
    }
}
