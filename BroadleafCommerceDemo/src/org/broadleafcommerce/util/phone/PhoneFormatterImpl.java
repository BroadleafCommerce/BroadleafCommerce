package org.broadleafcommerce.util.phone;

import org.broadleafcommerce.profile.domain.Phone;

public class PhoneFormatterImpl implements PhoneFormatter{
public void formatPhoneNumber(Phone phone) {
phone.setPhoneNumber(formatTelephoneNumber(phone.getPhoneNumber()));
}

private String formatTelephoneNumber(String pNumber) {
if (pNumber == null) {
return null;
}
String number = pNumber.replaceAll("\\D", "");
if (number.length() == 0) {
return null;
}

if (number.length() > 10) {
number = number.substring(0, 10);
}

StringBuffer newNumber = new StringBuffer(number);
if (newNumber.length() == 10) {
newNumber.insert(6, "-");
newNumber.insert(3, "-");
}
return newNumber.toString();
}
}