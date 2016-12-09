package org.broadleafcommerce.common.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Convenience annotation that behaves exactly like {@link FrameworkController} plus {@link ResponseBody}.
 *
 * @author Philip Baggett (pbaggett)
 * @see FrameworkController
 * @see ResponseBody
 * @since 5.2
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ResponseBody
public @interface FrameworkRestController {

    /**
     * @see FrameworkController#value()
     */
    RequestMapping[] value() default {};
}
