package org.broadleafcommerce.core.web.api.wrapper;

import org.broadleafcommerce.core.order.service.call.ActivityMessageDTO;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "message")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class CartMessageWrapper extends BaseWrapper implements APIWrapper<ActivityMessageDTO> {

    @XmlElement
    protected String message;
    @XmlElement
    protected String messageType;
    @XmlElement
    protected Integer priority;
    @Override
    public void wrapDetails(ActivityMessageDTO model, HttpServletRequest request) {
        this.message = model.getMessage();
        this.priority = model.getPriority();
        this.messageType = model.getType();
    }

    @Override
    public void wrapSummary(ActivityMessageDTO model, HttpServletRequest request) {
        wrapDetails(model, request);
    }

}
