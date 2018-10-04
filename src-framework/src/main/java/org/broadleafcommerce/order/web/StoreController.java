package org.broadleafcommerce.order.web;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.AddressImpl;
import org.broadleafcommerce.profile.service.CountryService;
import org.broadleafcommerce.profile.service.StateService;
import org.broadleafcommerce.store.domain.Store;
import org.broadleafcommerce.store.service.StoreService;
import org.broadleafcommerce.store.web.model.FindAStoreForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller("storeController")
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
            model.addAttribute("errorMessage" , "Please enter a valid postal code and distance." );
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