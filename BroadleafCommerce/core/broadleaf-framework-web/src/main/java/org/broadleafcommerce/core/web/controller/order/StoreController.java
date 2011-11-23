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

package org.broadleafcommerce.core.web.controller.order;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.store.domain.Store;
import org.broadleafcommerce.core.store.service.StoreService;
import org.broadleafcommerce.core.web.store.model.FindAStoreForm;
import org.broadleafcommerce.profile.core.domain.Address;
import org.broadleafcommerce.profile.core.domain.AddressImpl;
import org.broadleafcommerce.profile.core.service.CountryService;
import org.broadleafcommerce.profile.core.service.StateService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller("storeController")
@RequestMapping("/storeLocator")
public class StoreController {

    protected final Log logger = LogFactory.getLog(getClass());

    @Resource(name="blStoreService")
    private StoreService storeService;
    @Resource(name="blStateService")
    protected StateService stateService;
    @Resource(name="blCountryService")
    protected CountryService countryService;

    @RequestMapping(method = RequestMethod.GET)
    public String showStores(ModelMap model) {
        List<Store> storeList = storeService.readAllStores();
        FindAStoreForm findAStoreForm = new FindAStoreForm();
        findAStoreForm.setDistance("30");
        model.addAttribute("stateList", stateService.findStates());
        model.addAttribute("countryList", countryService.findCountries());
        model.addAttribute("stores", storeList);
        model.addAttribute("findAStoreForm", findAStoreForm);
        return "storeLocator/findAStore";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String findStores(ModelMap model, @ModelAttribute FindAStoreForm findAStoreForm, BindingResult errors) {
        Address searchAddress = new AddressImpl();
        searchAddress.setAddressLine1(findAStoreForm.getAddressLine1());
        searchAddress.setAddressLine2(findAStoreForm.getAddressLine2());
        searchAddress.setCity(findAStoreForm.getCity());
        searchAddress.setState(findAStoreForm.getState());
        searchAddress.setPostalCode(findAStoreForm.getPostalCode());
        searchAddress.setCountry(findAStoreForm.getCountry());

        if (findAStoreForm.getPostalCode() == null || "".equals(findAStoreForm.getPostalCode()) ||
                "".equals(findAStoreForm.getDistance()) || findAStoreForm.getPostalCode().length() != 5) {
            model.addAttribute("errorMessage" , "Please enter a valid zip/postal code and distance." );
            return showStores(model);
        }

        findAStoreForm.setStoreDistanceMap(storeService.findStoresByAddress(searchAddress,
                Double.parseDouble(findAStoreForm.getDistance())));
        if (findAStoreForm.getStoreDistanceMap().size() == 0) {
            model.addAttribute("errorMessage" , "No stores found in your area." );
        }
        model.addAttribute("stateList", stateService.findStates());
        model.addAttribute("countryList", countryService.findCountries());
        model.addAttribute("findAStoreForm", findAStoreForm);
        return "storeLocator/findAStore";
    }
}