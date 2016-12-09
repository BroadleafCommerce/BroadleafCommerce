package org.broadleafcommerce.core.web.geolocation;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.util.BLCRequestUtils;
import org.broadleafcommerce.common.web.AbstractBroadleafWebRequestProcessor;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.core.geolocation.GeolocationDTO;
import org.broadleafcommerce.core.geolocation.GeolocationService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.Resource;

@Component("blGeolocationRequestProcessor")
public class GeolocationRequestProcessor extends AbstractBroadleafWebRequestProcessor {

    public static final String FORWARD_HEADER = "X-FORWARDED-FOR";
    public static final String GEOLOCATON_ATTRIBUTE_NAME = "_blGeolocationAttribute";

    @Resource(name="blGeolocationService")
    protected GeolocationService geolocationService;

    @Override
    public void process(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            ServletWebRequest servletWebRequest = (ServletWebRequest) request;

            GeolocationDTO location = (GeolocationDTO) BLCRequestUtils.getSessionAttributeIfOk(request, GEOLOCATON_ATTRIBUTE_NAME);
            if (location == null) {
                location = geolocationService.getLocationData(getIPAddress(servletWebRequest));
                BLCRequestUtils.setSessionAttributeIfOk(request, GEOLOCATON_ATTRIBUTE_NAME, location);
            }
            BroadleafRequestContext.getBroadleafRequestContext().getAdditionalProperties().put(GEOLOCATON_ATTRIBUTE_NAME, location);
        }
    }

    protected String getIPAddress(ServletWebRequest request) {
        String ipAddress = request.getHeader(FORWARD_HEADER);
        if (StringUtils.isEmpty(ipAddress)) {
            ipAddress = request.getRequest().getRemoteAddr();
        }
        return ipAddress;
    }
}