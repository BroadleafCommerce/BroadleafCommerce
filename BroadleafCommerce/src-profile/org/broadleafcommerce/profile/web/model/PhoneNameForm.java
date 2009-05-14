package org.broadleafcommerce.profile.web.model;

import org.broadleafcommerce.profile.domain.Phone;

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
