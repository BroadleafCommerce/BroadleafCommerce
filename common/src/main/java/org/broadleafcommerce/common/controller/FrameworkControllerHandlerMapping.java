package org.broadleafcommerce.common.controller;

import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import java.lang.reflect.Method;

/**
 * HandlerMapping to find and map {@link org.springframework.web.bind.annotation.RequestMapping}s inside {@link
 * FrameworkController} and {@link FrameworkRestController} classes.
 * <p>
 * When framework controllers are enabled with {@link EnableFrameworkControllers} and a class is annotated with {@link
 * FrameworkController} or {@link FrameworkRestController} then this class will add {@link
 * org.springframework.web.bind.annotation.RequestMapping}s found within the class to handler mappings. This class has a
 * lower priority than the default {@link org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping}
 * so when a request comes in, {@link org.springframework.web.bind.annotation.RequestMapping}s located inside a class
 * annotated with {@link org.springframework.stereotype.Controller} with have a higher priority and be found before
 * {@link org.springframework.web.bind.annotation.RequestMapping}s found within a {@link FrameworkController} or {@link
 * FrameworkRestController}.
 *
 * @author Philip Baggett (pbaggett)
 * @see EnableFrameworkControllers
 * @see FrameworkController
 * @see FrameworkRestController
 * @since 5.2
 */
@Component
public class FrameworkControllerHandlerMapping extends RequestMappingHandlerMapping {

    public FrameworkControllerHandlerMapping() {
        setOrder(Ordered.LOWEST_PRECEDENCE - 2);
    }

    @Override
    protected boolean isHandler(Class<?> beanType) {
        return (beanType.getAnnotation(FrameworkController.class) != null) || (beanType.getAnnotation(FrameworkRestController.class) != null);
    }

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo requestMappingInfo = super.getMappingForMethod(method, handlerType);

        FrameworkController frameworkControllerAnnotation = handlerType.getAnnotation(FrameworkController.class);
        if (frameworkControllerAnnotation != null && frameworkControllerAnnotation.value().length > 0) {
            RequestMappingInfo parentRequestMappingInfo = createRequestMappingInfo(frameworkControllerAnnotation.value()[0], null);
            requestMappingInfo = parentRequestMappingInfo.combine(requestMappingInfo);
            return requestMappingInfo;
        }

        FrameworkRestController frameworkRestControllerAnnotation = handlerType.getAnnotation(FrameworkRestController.class);
        if (frameworkRestControllerAnnotation != null && frameworkRestControllerAnnotation.value().length > 0) {
            RequestMappingInfo parentRequestMappingInfo = createRequestMappingInfo(frameworkRestControllerAnnotation.value()[0], null);
            requestMappingInfo = parentRequestMappingInfo.combine(requestMappingInfo);
            return requestMappingInfo;
        }

        return requestMappingInfo;
    }
}
