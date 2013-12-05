/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package org.broadleafcommerce.common.payment.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author Elbert Bautista (elbertbautista)
 *
 * This default implementation produces the Request Attributes and Request Paremeters
 * in JSON notation.
 *
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
