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
import java.util.Map;

/**
 * @author Nick Crum ncrum
 */
@Component("blNotificationRegisterCustomerEventListener")
public class NotificationRegisterCustomerEventListener extends AbstractBroadleafApplicationEventListener<RegisterCustomerEvent> {

    protected static final String CUSTOMER_CONTEXT_KEY = "customer";
    protected final Log LOG = LogFactory.getLog(NotificationRegisterCustomerEventListener.class);

    @Autowired
    @Qualifier("blCustomerService")
    protected CustomerService customerService;

    @Autowired
    @Qualifier("blEmailNotificationService")
    protected NotificationService notificationService;

    @Override
    protected void handleApplicationEvent(RegisterCustomerEvent event) {
        Customer customer = customerService.readCustomerById(event.getCustomerId());
        if (customer == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unable to send registration email for customer with id " + event.getCustomerId() + ". No such customer found.");
            }
            return;
        }

        try {
            Map<String, Object> context = new HashMap<>();
            context.put(CUSTOMER_CONTEXT_KEY, customer);
            notificationService.sendNotification(new Notification(customer.getEmailAddress(), NotificationType.REGISTER_CUSTOMER, context));
        } catch (ServiceException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unable to send registration email for customer with email " + customer.getEmailAddress(), e);
            }
        }
    }

    @Override
    public boolean isAsynchronous() {
        return true;
    }
}
