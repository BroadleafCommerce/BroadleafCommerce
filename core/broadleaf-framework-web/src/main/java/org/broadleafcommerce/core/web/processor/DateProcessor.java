/*-
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.processor;

import org.broadleafcommerce.common.util.FormatUtil;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

/**
 * A Thymeleaf processor that formats the date
 */
@Component("blDateProcessor")
@ConditionalOnTemplating
public class DateProcessor implements DateExpression {

    @Override
    public String getName() {
        return "date";
    }

    @Override
    public int getPrecedence() {
        return 10000;
    }

    @Override
    public String renderWithContextTimeZone(final Date date) {
        String dateString = "";
        if (Objects.nonNull(date)) {
            BroadleafRequestContext broadleafRequestContext = BroadleafRequestContext.getBroadleafRequestContext();
            TimeZone timeZone = broadleafRequestContext.getTimeZone();
            dateString = FormatUtil.dateToSting(date, timeZone);
        }
        return dateString;
    }

}
