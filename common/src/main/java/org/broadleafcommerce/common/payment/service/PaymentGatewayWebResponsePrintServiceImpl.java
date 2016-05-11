/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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

package org.broadleafcommerce.common.payment.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * <p>This default implementation produces the Request Attributes and Request Paremeters
 * in JSON notation.</p>
 *
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blPaymentGatewayWebResponsePrintService")
public class PaymentGatewayWebResponsePrintServiceImpl implements PaymentGatewayWebResponsePrintService {

    public static final String REQUEST_ATTRIBUTES = "attributes";
    public static final String REQUEST_PARAMETERS = "parameters";

    @Override
    public String printRequest(HttpServletRequest request) {
        StringBuilder builder = new StringBuilder();
        Enumeration enAttr = request.getAttributeNames();
        builder.append("{");
            builder.append("\""+REQUEST_ATTRIBUTES+"\"" + ": {");
            while(enAttr.hasMoreElements()){
                String attributeName = (String)enAttr.nextElement();
                if (request.getAttribute(attributeName) instanceof String) {
                    builder.append("\"");
                    builder.append(attributeName);
                    builder.append("\"");
                    builder.append(":");
                    builder.append("\"");
                    builder.append(request.getAttribute(attributeName).toString());
                    builder.append("\"");
                    builder.append(",");
                }
            }
            builder.deleteCharAt(builder.lastIndexOf(","));
            builder.append("},");
            builder.append("\""+REQUEST_PARAMETERS+"\"" + ": {");
            Enumeration enParams = request.getParameterNames();
            while(enParams.hasMoreElements()){
                String paramName = (String)enParams.nextElement();
                builder.append("\"");
                builder.append(paramName);
                builder.append("\"");
                builder.append(":");
                builder.append("\"");
                builder.append(request.getParameter(paramName));
                builder.append("\"");
                builder.append(",");
            }
            builder.deleteCharAt(builder.lastIndexOf(","));
        builder.append("}");
        builder.append("}");
        return builder.toString();
    }

}
