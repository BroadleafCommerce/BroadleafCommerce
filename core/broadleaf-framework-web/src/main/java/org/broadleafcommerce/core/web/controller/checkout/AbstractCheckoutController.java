/*
 * Copyright 2008-2009 the original author or authors.
 *
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
 */

package org.broadleafcommerce.core.web.controller.checkout;

import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.broadleafcommerce.core.checkout.service.CheckoutService;
import org.broadleafcommerce.core.order.service.FulfillmentGroupService;
import org.broadleafcommerce.core.order.service.FulfillmentOptionService;
import org.broadleafcommerce.core.order.service.OrderMultishipOptionService;
import org.broadleafcommerce.core.order.service.OrderService;
import org.broadleafcommerce.core.payment.service.PaymentInfoFactory;
import org.broadleafcommerce.core.payment.service.SecurePaymentInfoService;
import org.broadleafcommerce.core.web.checkout.validator.BillingInfoFormValidator;
import org.broadleafcommerce.core.web.checkout.validator.ShippingInfoFormValidator;
import org.broadleafcommerce.profile.core.service.AddressService;
import org.broadleafcommerce.profile.core.service.CountryService;
import org.broadleafcommerce.profile.core.service.CustomerAddressService;
import org.broadleafcommerce.profile.core.service.StateService;

import javax.annotation.Resource;

/**
 * An abstract controller that provides convenience methods and resource declarations for its
 * children. Operations that are shared between controllers that deal with checkout belong here.
 *
 * @author elbertbautista
 */
public class AbstractCheckoutController extends BroadleafAbstractController {

    @Resource(name = "blOrderService")
    protected OrderService orderService;

    @Resource(name = "blFulfillmentOptionService")
    protected FulfillmentOptionService fulfillmentOptionService;

    @Resource(name = "blFulfillmentGroupService")
    protected FulfillmentGroupService fulfillmentGroupService;

    @Resource(name = "blCheckoutService")
    protected CheckoutService checkoutService;

    @Resource(name = "blStateService")
    protected StateService stateService;

    @Resource(name = "blCountryService")
    protected CountryService countryService;

    @Resource(name = "blCustomerAddressService")
    protected CustomerAddressService customerAddressService;

    @Resource(name = "blAddressService")
    protected AddressService addressService;

    @Resource(name = "blOrderMultishipOptionService")
    protected OrderMultishipOptionService orderMultishipOptionService;

    @Resource(name = "blShippingInfoFormValidator")
    protected ShippingInfoFormValidator shippingInfoFormValidator;

    @Resource(name = "blBillingInfoFormValidator")
    protected BillingInfoFormValidator billingInfoFormValidator;

    @Resource(name = "blCreditCardPaymentInfoFactory")
    protected PaymentInfoFactory creditCardPaymentInfoFactory;

    @Resource(name = "blSecurePaymentInfoService")
    protected SecurePaymentInfoService securePaymentInfoService;

}
