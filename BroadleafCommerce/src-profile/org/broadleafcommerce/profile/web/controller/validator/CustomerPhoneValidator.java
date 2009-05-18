package org.broadleafcommerce.profile.web.controller.validator;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.profile.domain.CustomerPhone;
import org.broadleafcommerce.profile.domain.Phone;
import org.broadleafcommerce.profile.service.CustomerPhoneService;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Service("customerPhoneValidator")
public class CustomerPhoneValidator implements Validator {
    /** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());

    @Resource
    private final CustomerPhoneService customerPhoneService;

    public CustomerPhoneValidator(){
        this.customerPhoneService = null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean supports(Class clazz) {
        return clazz.equals(Phone.class);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        //use regular phone
        CustomerPhone cPhone = (CustomerPhone) obj;

        if (!errors.hasErrors()) {
            //check for duplicate phone number
            List<CustomerPhone> phones = customerPhoneService.readAllCustomerPhonesByCustomerId(cPhone.getCustomerId());

            String phoneNum = cPhone.getPhone().getPhoneNumber();
            String phoneName = cPhone.getPhoneName();

            for (CustomerPhone existingPhone : phones) {
                if(phoneNum.equals(existingPhone.getPhone().getPhoneNumber())){
                    errors.pushNestedPath("phone");
                    errors.rejectValue("phoneNumber", "phoneNumber.duplicate", null);
                    errors.popNestedPath();
                }

                if(phoneName.equalsIgnoreCase(existingPhone.getPhoneName())){
                    errors.rejectValue("phoneName", "phoneName.duplicate", null);
                }
            }
        }
    }
}
