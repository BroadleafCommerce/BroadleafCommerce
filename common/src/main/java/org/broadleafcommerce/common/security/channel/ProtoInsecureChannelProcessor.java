package org.broadleafcommerce.common.security.channel;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.channel.InsecureChannelProcessor;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collection;

/**
 * @author Jeff Fischer
 */
public class ProtoInsecureChannelProcessor extends InsecureChannelProcessor {

    @Override
    public void decide(FilterInvocation invocation, Collection<ConfigAttribute> config) throws IOException, ServletException {
        if ((invocation == null) || (config == null)) {
            throw new IllegalArgumentException("Nulls cannot be provided");
        }

        for (ConfigAttribute attribute : config) {
            if (supports(attribute)) {
                if (invocation.getHttpRequest().getHeader("X-Forwarded-Proto").equals("https")) {
                    getEntryPoint().commence(invocation.getRequest(), invocation.getResponse());
                }
            }
        }
    }
}
