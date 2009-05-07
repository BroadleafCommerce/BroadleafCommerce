package org.broadleafcommerce.email.service;

import java.io.IOException;

import org.broadleafcommerce.email.domain.EmailAProduct;
import org.broadleafcommerce.email.domain.EmailContactUs;
import org.broadleafcommerce.email.service.exception.EmailException;

public interface EmailWebService {
    String getClickAndPickupSurveyProperties();

    String getEmailAProductProperties();

    String getEmailContactUsProperties();

    String getEmailProperties();

    Long sendClickAndPickupSurveys() throws IOException;

    void sendEmailAProduct(EmailAProduct emailAProduct)
    throws EmailException;

    void sendEmailContactUs(EmailContactUs emailContactUs)
    throws EmailException;

    void setClickAndPickupSurveyProperties(String clickAndPickupSurveyProperties);

    void setEmailAProductProperties(String emailAProductProperties);

    void setEmailContactUsProperties(String emailContactUsProperties);

    void setEmailProperties(String defaultProperties);
}
