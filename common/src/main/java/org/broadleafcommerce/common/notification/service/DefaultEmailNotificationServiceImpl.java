/*-
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.notification.service;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.email.service.EmailService;
import org.broadleafcommerce.common.email.service.info.EmailInfo;
import org.broadleafcommerce.common.notification.service.type.EmailNotification;
import org.broadleafcommerce.common.notification.service.type.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;

/**
 * @author Nick Crum ncrum
 */
@Service("blEmailNotificationService")
public class DefaultEmailNotificationServiceImpl implements NotificationService {

    protected final Log LOG = LogFactory.getLog(DefaultEmailNotificationServiceImpl.class);

    @Autowired(required = false)
    protected List<EmailInfo> emailInfos;

    @Autowired
    @Qualifier("blEmailService")
    protected EmailService emailService;

    @Override
    public boolean canHandle(Class<? extends Notification> clazz) {
        return EmailNotification.class.isAssignableFrom(clazz);
    }

    @Override
    public void sendNotification(Notification notification) {
        boolean success = false;
        for (EmailInfo info: ListUtils.emptyIfNull(emailInfos)) {
            if (Objects.equals(info.getEmailType(), notification.getType().getType())) {
                EmailInfo clonedInfo = info.clone();
                clonedInfo.setAttachments(((EmailNotification) notification).getAttachments());
                emailService.sendTemplateEmail(((EmailNotification) notification).getEmailAddress(), clonedInfo, notification.getContext());
                success = true;
            }
        }

        if (!success && LOG.isWarnEnabled()) {
            LOG.warn("Unable to find an EmailInfo that matched a notification of type " + notification.getType().getType()
                    + ". Be sure to specify the \"emailType\" property for any EmailInfo you define.");
        }
    }
}
