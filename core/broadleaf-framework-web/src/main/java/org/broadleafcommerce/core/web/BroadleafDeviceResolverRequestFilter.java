package org.broadleafcommerce.core.web;

import org.broadleafcommerce.common.admin.condition.ConditionalOnNotAdmin;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.common.web.filter.FilterOrdered;
import org.springframework.core.Ordered;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceResolver;
import org.springframework.mobile.device.DeviceResolverRequestFilter;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This filter adds the device type that made the request to the request context.
 * 
 * @author Nathan Moore (nathanmoore).
 */
@Component("blDeviceResolverRequestFilter")
@ConditionalOnNotAdmin
public class BroadleafDeviceResolverRequestFilter extends DeviceResolverRequestFilter implements Ordered {

    @Resource(name = "blDeviceResolver")
    private DeviceResolver deviceResolver;

    /**
     * Called early in the post security chain to ensure that the device type can be used with greatest flexibility.
     * 
     * @return
     */
    @Override
    public int getOrder() {
        return FilterOrdered.POST_SECURITY_HIGH + 10;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        resolveDeviceType(request);
        filterChain.doFilter(request, response);
    }
    
    protected void resolveDeviceType(HttpServletRequest request) {
        final Device device = deviceResolver.resolveDevice(request);
        String deviceType = "unknown";
        
        if (device != null) {
            if (device.isMobile()) {
                deviceType = "mobile";
            } else if (device.isTablet()) {
                deviceType = "tablet";
            } else if (device.isNormal()) {
                deviceType = "normal";
            }
        }
        
        BroadleafRequestContext.getBroadleafRequestContext().getAdditionalProperties().put(DeviceUtils.CURRENT_DEVICE_ATTRIBUTE, deviceType);
    }
    
}
