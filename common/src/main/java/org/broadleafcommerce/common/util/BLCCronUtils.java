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
package org.broadleafcommerce.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Date;

/**
 * Convenience class to facilitate cron interpretation.
 * 
 * @author Chris Kittrell (ckittrell)
 */
public class BLCCronUtils {

    private static final Log LOG = LogFactory.getLog(BLCCronUtils.class);
    
    /**
     * Gathers the next time that the cron expression will be valid
     * 
     * @param cron
     * @return the next valid date
     */
    public static Date getNextValidTime(String cron) {
        Date nextValidTime = null;
        try {
            if (StringUtils.isNotBlank(cron)) {
                nextValidTime = new CronExpression(cron).getNextValidTimeAfter(new Date());
            }
        } catch (ParseException e) {
            LOG.warn("Unable to parse the given cron expression: " + cron, e);
        }

        return nextValidTime;
    }

    /**
     * Determines whether or not the cron expression is valid
     *
     * @param cron
     * @return whether or not the cron is valid
     */
    public static boolean isValidExpression(String cron) {
        return StringUtils.isNotBlank(cron) && CronExpression.isValidExpression(cron);
    }
}
