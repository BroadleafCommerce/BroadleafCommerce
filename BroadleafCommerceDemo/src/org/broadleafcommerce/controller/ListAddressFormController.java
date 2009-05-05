package org.broadleafcommerce.controller;

import org.springframework.web.servlet.mvc.SimpleFormController;

public class ListAddressFormController extends SimpleFormController {
	/*
    protected final Log logger = LogFactory.getLog(getClass());
    private AddressService addressService;
    private CustomerService customerService;

    public void setAddressService(AddressService addressService) {
        this.addressService = addressService;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    protected Object formBackingObject(HttpServletRequest request) throws ServletException {
        CreateAddress createAddress = new CreateAddress();
        List<Address> addressList = getAddressListForUser();
        createAddress.setAddressList(addressList);
        return createAddress;
    }

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Address> addressList = getAddressListForUser();
        Map<String, List<Address>> model = new HashMap<String, List<Address>>();
        model.put("addressList", addressList);

        return new ModelAndView("listAddress", model);
    }

    private List<Address> getAddressListForUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Customer customer = customerService.readCustomerByUsername(auth.getName());
        List<Address> addressList = addressService.readAddressByUserId(customer.getId());
        return addressList;
    }*/
}
