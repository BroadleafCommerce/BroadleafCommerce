package org.broadleafcommerce.core.workflow;

import org.broadleafcommerce.core.order.service.call.ActivityMessageDTO;

import java.util.List;


public interface ActivityMessages {

    List<ActivityMessageDTO> getActivityMessages();

    void setActivityMessages(List<ActivityMessageDTO> activityMessages);
}
