/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.core.payment.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.i18n.domain.ISOCountry;
import org.broadleafcommerce.common.i18n.service.ISOService;
import org.broadleafcommerce.common.payment.PaymentAdditionalFieldType;
import org.broadleafcommerce.common.payment.dto.AddressDTO;
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO;
import org.broadleafcommerce.core.order.domain.FulfillmentGroup;
import org.broadleafcommerce.core.order.domain.Order;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.payment.domain.OrderPayment;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.Country;
import org.broadleafcommerce.profile.core.domain.CustomerPayment;
import org.broadleafcommerce.profile.core.domain.Phone;
import org.broadleafcommerce.profile.core.domain.State;
import org.broadleafcommerce.profile.core.service.AddressService;
import org.broadleafcommerce.profile.core.service.CountryService;
import org.broadleafcommerce.profile.core.service.PhoneService;
import org.broadleafcommerce.profile.core.service.StateService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blPaymentResponseDTOToEntityService")
public class PaymentResponseDTOToEntityServiceImpl implements PaymentResponseDTOToEntityService {

    private static final Log LOG = LogFactory.getLog(PaymentResponseDTOToEntityServiceImpl.class);

    @Resource(name = "blAddressService")
    protected AddressService addressService;

    @Resource(name = "blStateService")
    protected StateService stateService;

    @Resource(name = "blCountryService")
    protected CountryService countryService;

    @Resource(name = "blISOService")
    protected ISOService isoService;

    @Resource(name = "blPhoneService")
    protected PhoneService phoneService;

    @Resource(name = "blFulfillmentGroupService")
    protected FulfillmentGroupService fulfillmentGroupService;

    @Override
    public void populateBillingInfo(PaymentResponseDTO responseDTO, OrderPayment payment, Address tempBillingAddress, boolean isUseBillingAddressFromGateway) {
        Address billingAddress = tempBillingAddress;
        if (responseDTO.getBillTo() != null && isUseBillingAddressFromGateway) {
            billingAddress = addressService.create();
            AddressDTO<PaymentResponseDTO> billToDTO = responseDTO.getBillTo();
            populateAddressInfo(billToDTO, billingAddress);
        }

        payment.setBillingAddress(billingAddress);
    }

    @Override
    public void populateShippingInfo(PaymentResponseDTO responseDTO, Order order) {
        FulfillmentGroup shippableFulfillmentGroup = fulfillmentGroupService.getFirstShippableFulfillmentGroup(order);
        Address shippingAddress = null;
        if (responseDTO.getShipTo() != null && shippableFulfillmentGroup != null) {
            shippingAddress = addressService.create();
            AddressDTO<PaymentResponseDTO> shipToDTO = responseDTO.getShipTo();
            populateAddressInfo(shipToDTO, shippingAddress);

            shippableFulfillmentGroup = fulfillmentGroupService.findFulfillmentGroupById(shippableFulfillmentGroup.getId());
            if (shippableFulfillmentGroup != null) {
                shippableFulfillmentGroup.setAddress(shippingAddress);
                fulfillmentGroupService.save(shippableFulfillmentGroup);
            }
        }
    }

    @Override
    public void populateAddressInfo(AddressDTO<PaymentResponseDTO> dto, Address address) {
        address.setFirstName(dto.getAddressFirstName());
        address.setLastName(dto.getAddressLastName());
        address.setFullName(dto.getAddressFirstName() + " " + dto.getAddressLastName());
        address.setAddressLine1(dto.getAddressLine1());
        address.setAddressLine2(dto.getAddressLine2());
        address.setCity(dto.getAddressCityLocality());

        State state = null;
        if(dto.getAddressStateRegion() != null) {
            state = stateService.findStateByAbbreviation(dto.getAddressStateRegion());
        }
        if (state == null) {
            LOG.warn("The given state from the response: " + dto.getAddressStateRegion() + " could not be found"
                    + " as a state abbreviation in BLC_STATE");
        }
        address.setState(state);
        address.setStateProvinceRegion(dto.getAddressStateRegion());

        address.setPostalCode(dto.getAddressPostalCode());

        Country country = null;
        ISOCountry isoCountry = null;
        if (dto.getAddressCountryCode() != null) {
            country = countryService.findCountryByAbbreviation(dto.getAddressCountryCode());
            isoCountry = isoService.findISOCountryByAlpha2Code(dto.getAddressCountryCode());
        }
        if (country == null) {
            LOG.warn("The given country from the response: " + dto.getAddressCountryCode() + " could not be found"
                    + " as a country abbreviation in BLC_COUNTRY");
        } else if (isoCountry == null) {
            LOG.error("The given country from the response: " + dto.getAddressCountryCode() + " could not be found"
                    + " as a country alpha-2 code in BLC_ISO_COUNTRY");
        }

        address.setCountry(country);
        address.setIsoCountryAlpha2(isoCountry);

        if (dto.getAddressPhone() != null) {
            Phone billingPhone = phoneService.create();
            billingPhone.setPhoneNumber(dto.getAddressPhone());
            address.setPhonePrimary(billingPhone);
        }

        addressService.populateAddressISOCountrySub(address);
    }

    @Override
    public void populateCustomerPaymentToken(PaymentResponseDTO responseDTO, CustomerPayment customerPayment) {
        if (responseDTO.getPaymentToken() != null) {
            customerPayment.setPaymentToken(responseDTO.getPaymentToken());
        } else if (responseDTO.getResponseMap().containsKey(PaymentAdditionalFieldType.TOKEN.getType())) {
            //handle legacy additional fields map
            customerPayment.setPaymentToken(responseDTO.getResponseMap().get(PaymentAdditionalFieldType.TOKEN.getType()));
        } else if (responseDTO.getCreditCard() != null) {
            //handle higher PCI level compliance scenarios
            customerPayment.setPaymentToken(responseDTO.getCreditCard().getCreditCardNum());
        }
    }

}
