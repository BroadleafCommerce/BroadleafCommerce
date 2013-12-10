/*
 * #%L
 * BroadleafCommerce Common Libraries
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

package org.broadleafcommerce.common.web.payment.expression;

import org.broadleafcommerce.common.extension.AbstractExtensionHandler;
import org.broadleafcommerce.common.extension.ExtensionResultStatusType;

import java.util.Map;

/**
 * @author Elbert Bautista (elbertbautista)
 *
 */
public abstract class AbstractPaymentGatewayFieldExtensionHandler extends AbstractExtensionHandler
        implements PaymentGatewayFieldExtensionHandler {

    public abstract String getCreditCardHolderName();
    public abstract String getCreditCardType();
    public abstract String getCreditCardNum();
    public abstract String getCreditCardExpDate();
    public abstract String getCreditCardExpMonth();
    public abstract String getCreditCardExpYear();
    public abstract String getCreditCardCvv();

    public abstract String getBillToAddressFirstName();
    public abstract String getBillToAddressLastName();
    public abstract String getBillToAddressCompanyName();
    public abstract String getBillToAddressLine1();
    public abstract String getBillToAddressLine2();
    public abstract String getBillToAddressCityLocality();
    public abstract String getBillToAddressStateRegion();
    public abstract String getBillToAddressPostalCode();
    public abstract String getBillToAddressCountryCode();
    public abstract String getBillToAddressPhone();
    public abstract String getBillToAddressEmail();

    public abstract String getShipToAddressFirstName();
    public abstract String getShipToAddressLastName();
    public abstract String getShipToAddressCompanyName();
    public abstract String getShipToAddressLine1();
    public abstract String getShipToAddressLine2();
    public abstract String getShipToAddressCityLocality();
    public abstract String getShipToAddressStateRegion();
    public abstract String getShipToAddressPostalCode();
    public abstract String getShipToAddressCountryCode();
    public abstract String getShipToAddressPhone();
    public abstract String getShipToAddressEmail();

    @Override
    public ExtensionResultStatusType mapFieldName(String fieldNameKey, Map<String, String> fieldNameMap) {

        //-------------------------
        // Credit Card Fields
        //-------------------------

        if ("creditCard.creditCardHolderName".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getCreditCardHolderName() != null ? getCreditCardHolderName() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("creditCard.creditCardType".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getCreditCardType() != null ? getCreditCardType() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("creditCard.creditCardNum".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getCreditCardNum() != null ? getCreditCardNum() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("creditCard.creditCardExpDate".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getCreditCardExpDate() != null ? getCreditCardExpDate() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("creditCard.creditCardExpMonth".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getCreditCardExpMonth() != null ? getCreditCardExpMonth() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("creditCard.creditCardExpYear".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getCreditCardExpYear() != null ? getCreditCardExpYear() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("creditCard.creditCardCvv".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getCreditCardCvv() != null ? getCreditCardCvv() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        //-------------------------
        // BillTo Fields
        //-------------------------

        if ("billTo.addressFirstName".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getBillToAddressFirstName() != null ? getBillToAddressFirstName() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("billTo.addressLastName".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getBillToAddressLastName() != null ? getBillToAddressLastName() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("billTo.addressCompanyName".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getBillToAddressCompanyName() != null ? getBillToAddressCompanyName() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("billTo.addressLine1".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getBillToAddressLine1() != null ? getBillToAddressLine1() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("billTo.addressLine2".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getBillToAddressLine2() != null ? getBillToAddressLine2() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("billTo.addressCityLocality".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getBillToAddressCityLocality() != null ? getBillToAddressCityLocality() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("billTo.addressStateRegion".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getBillToAddressStateRegion() != null ? getBillToAddressStateRegion() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("billTo.addressPostalCode".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getBillToAddressPostalCode() != null ? getBillToAddressPostalCode() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("billTo.addressCountryCode".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getBillToAddressCountryCode() != null ? getBillToAddressCountryCode() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("billTo.addressPhone".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getBillToAddressPhone() != null ? getBillToAddressPhone() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("billTo.addressEmail".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getBillToAddressEmail() != null ? getBillToAddressEmail() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        //-------------------------
        // ShipTo Fields
        //-------------------------

        if ("shipTo.addressFirstName".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getShipToAddressFirstName() != null ? getShipToAddressFirstName() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("shipTo.addressLastName".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getShipToAddressLastName() != null ? getShipToAddressLastName() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("shipTo.addressCompanyName".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getShipToAddressCompanyName() != null ? getShipToAddressCompanyName() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("shipTo.addressLine1".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getShipToAddressLine1() != null ? getShipToAddressLine1() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("shipTo.addressLine2".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getShipToAddressLine2() != null ? getShipToAddressLine2() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("shipTo.addressCityLocality".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getShipToAddressCityLocality() != null ? getShipToAddressCityLocality() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("shipTo.addressStateRegion".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getShipToAddressStateRegion() != null ? getShipToAddressStateRegion() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("shipTo.addressPostalCode".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getShipToAddressPostalCode() != null ? getShipToAddressPostalCode() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("shipTo.addressCountryCode".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getShipToAddressCountryCode() != null ? getShipToAddressCountryCode() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("shipTo.addressPhone".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getShipToAddressPhone() != null ? getShipToAddressPhone() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        if ("shipTo.addressEmail".equals(fieldNameKey)){
            fieldNameMap.put( fieldNameKey,
                    getShipToAddressEmail() != null ? getShipToAddressEmail() : fieldNameKey);
            return ExtensionResultStatusType.HANDLED_CONTINUE;
        }

        return ExtensionResultStatusType.HANDLED_CONTINUE;
    }
}
