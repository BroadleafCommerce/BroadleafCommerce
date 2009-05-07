package org.broadleafcommerce.email.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.broadleafcommerce.catalog.domain.Product;
import org.broadleafcommerce.catalog.service.CatalogService;
import org.broadleafcommerce.email.dao.EmailWebTaskDao;
import org.broadleafcommerce.email.domain.AbstractEmailTarget;
import org.broadleafcommerce.email.domain.EmailAProduct;
import org.broadleafcommerce.email.domain.EmailContactUs;
import org.broadleafcommerce.email.domain.EmailStore;
import org.broadleafcommerce.email.domain.SurveyTargetUser;
import org.broadleafcommerce.email.service.exception.EmailException;
import org.broadleafcommerce.email.service.info.EmailInfo;
import org.springframework.transaction.annotation.Transactional;

public class EmailWebServiceImpl implements EmailWebService {
    private final EmailWebTaskDao emailWebTaskDao;
    private final EmailService emailService;
    private final CatalogService catalogService;

    private String emailProperties;
    private String clickAndPickupSurveyProperties;
    private String emailAProductProperties;
    private String emailContactUsProperties;
    private AbstractEmailTarget contactUsRecipient;

    public EmailWebServiceImpl(EmailWebTaskDao emailWebTaskDao, EmailService emailService, CatalogService catalogService) {
        this.emailWebTaskDao = emailWebTaskDao;
        this.emailService = emailService;
        this.catalogService = catalogService;
    }

    public String getEmailProperties() {
        return emailProperties;
    }

    public void setEmailProperties(String emailProperties) {
        this.emailProperties = emailProperties;
    }

    public String getClickAndPickupSurveyProperties() {
        return clickAndPickupSurveyProperties;
    }

    public void setClickAndPickupSurveyProperties(String clickAndPickupSurveyProperties) {
        this.clickAndPickupSurveyProperties = clickAndPickupSurveyProperties;
    }

    public String getEmailAProductProperties() {
        return emailAProductProperties;
    }

    public void setEmailAProductProperties(String emailAProductProperties) {
        this.emailAProductProperties = emailAProductProperties;
    }

    public String getEmailContactUsProperties() {
        return emailContactUsProperties;
    }

    public void setEmailContactUsProperties(String emailContactUsProperties) {
        this.emailContactUsProperties = emailContactUsProperties;
    }

    public AbstractEmailTarget getContactUsRecipient() {
        return contactUsRecipient;
    }

    public void setContactUsRecipient(AbstractEmailTarget contactUsRecipient) {
        this.contactUsRecipient = contactUsRecipient;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public Long sendClickAndPickupSurveys() throws IOException {
        EmailInfo info = new EmailInfo(new String[]{emailProperties, clickAndPickupSurveyProperties});
        List<SurveyTargetUser> users = emailWebTaskDao.retrieveClickAndPickSurveyTargets();
        Iterator<SurveyTargetUser> itr = users.iterator();
        long count = 0;
        while(itr.hasNext()) {
            SurveyTargetUser user = itr.next();
            EmailStore store = emailWebTaskDao.retrieveStoreById(user.getStoreId());

            HashMap map = new HashMap();
            map.put("user", user);
            map.put("info", info);
            map.put("store", store);

            emailService.sendTemplateEmail(map);
            count++;
        }

        return count;
    }

    @Override
    public void sendEmailAProduct(EmailAProduct emailAProduct) throws EmailException {
        AbstractEmailTarget emailUser = new AbstractEmailTarget(){};
        emailUser.setEmailAddress(emailAProduct.getRecipientEmail());
        EmailInfo info;
        try {
            info = new EmailInfo(new String[]{emailProperties, emailAProductProperties});
        } catch (IOException e) {
            throw new EmailException(e);
        }
        info.setFromAddress(emailAProduct.getSenderEmail());
        Product product = catalogService.findProductById(emailAProduct.getProductId());
        //        TCSProduct product = (TCSProduct) catalogService.findProductById(emailAProduct.getProductId());
        product.getProductImages().size();

        //TODO scc: TCS specific
        //product.getProductBullets().size();
        product.getSkus().size();

        //TODO scc: TCS specific
        //product.getAccessorySkus().size();

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("user", emailUser);
        map.put("info", info);
        map.put("product", product);
        map.put("categoryId", emailAProduct.getCategoryId());
        map.put("emailMessage", emailAProduct.getEmailMessage());
        map.put("messageFrom", emailAProduct.getSenderEmail());
        emailService.sendTemplateEmail(map);

        if (emailAProduct.isCopySender()) {
            AbstractEmailTarget copyEmailUser = new AbstractEmailTarget(){};
            copyEmailUser.setEmailAddress(emailAProduct.getSenderEmail());
            HashMap<String, Object> copyMap = new HashMap<String, Object>();
            copyMap.put("user", copyEmailUser);
            copyMap.put("info", info);
            copyMap.put("product", product);
            copyMap.put("categoryId", emailAProduct.getCategoryId());
            copyMap.put("emailMessage", emailAProduct.getEmailMessage());
            copyMap.put("messageFrom", emailAProduct.getSenderEmail());
            emailService.sendTemplateEmail(copyMap);
        }
    }

    public void sendEmailContactUs(EmailContactUs emailContactUs)
    throws EmailException {
        AbstractEmailTarget emailUser = new AbstractEmailTarget(){};
        emailUser.setEmailAddress(contactUsRecipient.getEmailAddress());
        EmailInfo info;
        try {
            info = new EmailInfo(new String[]{emailProperties, emailContactUsProperties});
        } catch (IOException e) {
            throw new EmailException(e);
        }
        info.setFromAddress(emailContactUs.getSenderEmail());
        info.setSubject(emailContactUs.getEmailSubject());
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("user", emailUser);
        map.put("info", info);
        map.put("comments", emailContactUs.getComments());
        map.put("referer", emailContactUs.getReferer());
        map.put("sessionId", emailContactUs.getSessionId());
        map.put("phoneNumber", emailContactUs.getPhoneNumber());
        emailService.sendTemplateEmail(map);

    }



}
