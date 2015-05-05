package org.broadleafcommerce.common.web.resource.resolver;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ContentVersionStrategy;
import org.springframework.web.servlet.resource.VersionStrategy;

import java.util.HashMap;

import javax.annotation.PostConstruct;

@Component("blVersionResourceResolverStrategyMap")
public class BLVersionResourceResolverDefaultStrategyMap<T, V> extends HashMap<String, VersionStrategy> {

    private static final long serialVersionUID = -3345223635822341852L;

    @PostConstruct
    public void initIt() throws Exception {
        this.put("/**", (VersionStrategy) new ContentVersionStrategy());
    }

}
