package org.broadleafcommerce.profile.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.CustomerPhone;
import org.broadleafcommerce.profile.service.CustomerPhoneService;
import org.broadleafcommerce.profile.test.dataprovider.CustomerPhoneControllerTestDataProvider;
import org.broadleafcommerce.profile.web.controller.CustomerPhoneController;
import org.broadleafcommerce.profile.web.model.PhoneNameForm;
import org.broadleafcommerce.test.integration.BaseTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.testng.annotations.Test;

public class CustomerPhoneControllerTest extends BaseTest {
    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
    @Resource
    private CustomerPhoneController customerPhoneController;
    @Resource
    private CustomerPhoneService customerPhoneService;
    private List<Long> createdCustomerPhoneIds = new ArrayList<Long>();
    private Long userId = 1L;
    private MockHttpServletRequest request;

    @Test(groups = "createCustomerPhoneFromController", dataProvider = "setupCustomerPhoneControllerData", dataProviderClass = CustomerPhoneControllerTestDataProvider.class)
    @Rollback(false)
    public void createCustomerPhoneFromController(PhoneNameForm phoneNameForm) {
        BindingResult errors = new BeanPropertyBindingResult(phoneNameForm, "phoneNameForm");

        request = this.getNewServletInstance();
        request.getSession().setAttribute("customer_session", userId); //set customer on session

        String view = customerPhoneController.savePhone(phoneNameForm, errors, request);
        assert (view.indexOf("success") >= 0);

        List<CustomerPhone> phones = customerPhoneService.readAllCustomerPhonesByCustomerId(1L);

        boolean inPhoneList = false;

        Long id = (Long) request.getAttribute("customerPhoneId");
        assert (id != null);

        for (CustomerPhone p : phones) {
            if ((p.getPhoneName() != null) && p.getPhoneName().equals(phoneNameForm.getPhoneName())) {
                inPhoneList = true;
            }
        }
        assert (inPhoneList == true);

        createdCustomerPhoneIds.add(id);
    }

    @Test(groups = "makePhoneDefaultOnCustomerPhoneController", dependsOnGroups = "createCustomerPhoneFromController")
    public void makePhoneDefaultOnCustomerPhoneController() {
        Long nonDefaultPhoneId = null;
        List<CustomerPhone> phones_1 = customerPhoneService.readAllCustomerPhonesByCustomerId(1L);

        for (CustomerPhone p : phones_1) {
            if (!p.getPhone().isDefault()) {
                nonDefaultPhoneId = p.getId();

                break;
            }
        }

        request = this.getNewServletInstance();

        String view = customerPhoneController.makePhoneDefault(nonDefaultPhoneId, request);
        assert (view.indexOf("success") >= 0);

        List<CustomerPhone> phones = customerPhoneService.readAllCustomerPhonesByCustomerId(1L);

        for (CustomerPhone p : phones) {
            if (p.getId() == nonDefaultPhoneId) {
                assert (p.getPhone().isDefault());

                break;
            }
        }
    }

    @Test(groups = "readCustomerPhoneFromController", dependsOnGroups = "createCustomerPhoneFromController")
    public void readCustomerPhoneFromController() {
        List<CustomerPhone> phones_1 = customerPhoneService.readAllCustomerPhonesByCustomerId(1L);
        int phones_1_size = phones_1.size();

        request = this.getNewServletInstance();

        String view = customerPhoneController.deletePhone(createdCustomerPhoneIds.get(0), request);
        assert (view.indexOf("success") >= 0);

        List<CustomerPhone> phones_2 = customerPhoneService.readAllCustomerPhonesByCustomerId(1L);
        assert ((phones_1_size - phones_2.size()) == 1);
    }

    @Test(groups = "viewCustomerPhoneFromController")
    public void viewCustomerPhoneFromController() {
        PhoneNameForm pnf = new PhoneNameForm();

        BindingResult errors = new BeanPropertyBindingResult(pnf, "phoneNameForm");

        request = this.getNewServletInstance();

        String view = customerPhoneController.viewPhone(null, request, pnf, errors);
        assert (view.indexOf("success") >= 0);
        assert (request.getAttribute("customerPhoneId") == null);
    }

    @Test(groups = "viewExistingCustomerPhoneFromController", dependsOnGroups = "createCustomerPhoneFromController")
    public void viewExistingCustomerPhoneFromController() {
        List<CustomerPhone> phones_1 = customerPhoneService.readAllCustomerPhonesByCustomerId(1L);
        PhoneNameForm pnf = new PhoneNameForm();

        BindingResult errors = new BeanPropertyBindingResult(pnf, "phoneNameForm");

        request = this.getNewServletInstance();

        String view = customerPhoneController.viewPhone(phones_1.get(0).getId(), request, pnf, errors);
        assert (view.indexOf("success") >= 0);
        assert (request.getAttribute("customerPhoneId").equals(phones_1.get(0).getId()));
    }

    private MockHttpServletRequest getNewServletInstance() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("customer_session", userId); //set customer on session

        return request;
    }
}
