package org.broadleafcommerce.profile.core.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.event.AbstractBroadleafApplicationEventListener;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.notification.service.type.Notification;
import org.broadleafcommerce.common.notification.service.NotificationService;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.broadleafcommerce.common.notification.service.type.NotificationType;
import java.util.HashMap;

/**
 * @author Nick Crum ncrum
 */
@Component("blNotificationForgotPasswordEventListener")
public class NotificationForgotPasswordEventListener extends AbstractBroadleafApplicationEventListener<ForgotPasswordEvent> {

    protected static final String TOKEN_CONTEXT_KEY = "token";
    protected static final String RESET_PASSWORD_URL_CONTEXT_KEY = "resetPasswordUrl";
    protected static final String CUSTOMER_CONTEXT_KEY = "customer";
    protected final Log LOG = LogFactory.getLog(NotificationForgotPasswordEventListener.class);

    @Autowired
    @Qualifier("blCustomerService")
    protected CustomerService customerService;

    @Autowired
    @Qualifier("blEmailNotificationService")
    protected NotificationService notificationService;

    @Override
    protected void handleApplicationEvent(ForgotPasswordEvent event) {
        Customer customer = customerService.readCustomerById(event.getCustomerId());
        String resetPasswordUrl = event.getResetPasswordUrl();
        String token = event.getToken();

        try {
            HashMap<String, Object> context = new HashMap<>();
            context.put(TOKEN_CONTEXT_KEY, token);
            context.put(RESET_PASSWORD_URL_CONTEXT_KEY, resetPasswordUrl);
            context.put(CUSTOMER_CONTEXT_KEY, customer);
            notificationService.sendNotification(new Notification(customer.getEmailAddress(), NotificationType.FORGOT_PASSWORD, context));
        } catch (ServiceException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unable to send a forgot password email for " + customer.getEmailAddress(), e);
            }
        }
    }

    @Override
    public boolean isAsynchronous() {
        return true;
    }
}
