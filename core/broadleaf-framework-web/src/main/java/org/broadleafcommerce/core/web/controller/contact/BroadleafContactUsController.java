/*-
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.web.controller.contact;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.notification.service.NotificationDispatcher;
import org.broadleafcommerce.common.notification.service.type.EmailNotification;
import org.broadleafcommerce.common.notification.service.type.NotificationEventType;
import org.broadleafcommerce.common.web.controller.BroadleafAbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;

/**
 * @author Nick Crum ncrum
 */
public class BroadleafContactUsController extends BroadleafAbstractController {

    protected static final Log LOG = LogFactory.getLog(BroadleafContactUsController.class);

    @Value("${site.emailAddress}")
    protected String targetEmailAddress;

    @Autowired
    @Qualifier("blNotificationDispatcher")
    protected NotificationDispatcher notificationDispatcher;

    public String sendConfirmationEmail(String name, String emailAddress, String comments) {
        HashMap<String, Object> context = new HashMap<>();
        context.put("name", name);
        context.put("comments", comments);
        context.put("emailAddress", emailAddress);
        try {
            notificationDispatcher.dispatchNotification(
                    new EmailNotification(targetEmailAddress, NotificationEventType.CONTACT_US, context)
            );
        } catch (ServiceException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unable to send contact us email", e);
            }
            return "redirect:" + getPath();
        }

        return getSuccessView();
    }

    public String index() {
        return getView();
    }

    protected String getPath() {
        return "/contactus";
    }

    protected String getView() {
        return "contactus/contactus";
    }

    protected String getSuccessView() {
        return "contactus/success";
    }

}
