package org.broadleafcommerce.profile.web.model;

import org.broadleafcommerce.profile.domain.Phone;

/**
 * The Form Backing Bean used by the CustomerPhoneController.  This design was chosen instead
 * of placing multiple values on the request.  This is a smaller scenario since there are not many
 * properties for the CustomerPhoneController, but to be consistent, we did not put a large amount of
 * unnecessary parameters on the request.
 * 
 * @author sconlon
 *
 */
public class PhoneNameForm {
    private Phone phone;
    private String phoneName;

    public Phone getPhone() {
        return phone;
    }

    public String getPhoneName() {
        return phoneName;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }
}
