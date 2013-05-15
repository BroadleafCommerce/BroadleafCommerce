/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.common.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.RequestDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

/**
 * Responsible for returning the RequestDTO to use for the current request.
 *
 * @author Priyesh Patel
 */
@Component("blRequestDTOResolver")
public class BroadleafRequestDTOResolverImpl implements BroadleafRequestDTOResolver {
    private final Log LOG = LogFactory.getLog(BroadleafRequestDTOResolverImpl.class);

    public static String REQUEST_DTO = "blRequestDTO";




    @Override
    public RequestDTO resolveRequestDTO(WebRequest request) {
        RequestDTO requestDTO = null;

        // First check for request attribute
        requestDTO = (RequestDTO) request.getAttribute(REQUEST_DTO, WebRequest.SCOPE_REQUEST);


        // Second, check the session if there a saved requestDTO
        if (requestDTO == null || requestDTO.getRequestDTOAttributes().isEmpty()) {
            RequestDTO globalRequestDTO = (RequestDTO) request.getAttribute(REQUEST_DTO, WebRequest.SCOPE_GLOBAL_SESSION);
            if (globalRequestDTO != null) {
                requestDTO = globalRequestDTO;
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Attempt to find timezone from session resulted in " + requestDTO);
                }
            }

        }

        // Finally, save to session if the custom fields have been set, 
        if (requestDTO != null && !requestDTO.getRequestDTOAttributes().isEmpty()) {
            request.setAttribute(REQUEST_DTO, requestDTO, WebRequest.SCOPE_GLOBAL_SESSION);
        }


        return requestDTO;
    }

}
