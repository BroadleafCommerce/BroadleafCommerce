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
        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put("name", name);
        context.put("comments", comments);
        context.put("emailAddress", emailAddress);
        try {
            notificationDispatcher.dispatchNotification(new EmailNotification(targetEmailAddress, NotificationEventType.CONTACT_US, context));
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
