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
package org.broadleafcommerce.common.util.dao;

import org.broadleafcommerce.common.persistence.ArchiveStatus;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;


/**
 * Helper class for criteria queries. 
 * @author gdiaz
 *
 */
public class QueryUtils {
    
    /**
     * given a list of AND-ed query restrictions, adds to the list the  restrictions necessary for effective-dating
     * The rules used are:
     * <ul>
     *   <li>The "activeStart" effective date has to exist (otherwise, inactive)</li>
     *   <li>The "activeStart" effective date has be prior or equal to today</li>
     *   <li>For the record to remain active, the "activeEnd" date can be either left unspecified, or be after today</li>
     * <ul>
     * @param builder            the Criteria builder
     * @param restrictions       the list of AND-ed restrictions
     * @param datesPath          the Path expression that can be used to retrieve the date fields
     * @param startDateFieldName the name of the start date field, for example, "activeStartDate"
     * @param endDateFieldName   the name of the end date field, for example, "activeEndDate"
     */
    public static void effectiveDate(CriteriaBuilder builder, List<Predicate> restrictions, Path datesPath, String startDateFieldName, String endDateFieldName) {
        Date now = new Date();
        Path<Date> activeStartDate =  datesPath.<Date>get(startDateFieldName);
        Path<Date> activeEndDate =  datesPath.<Date>get(endDateFieldName);
        restrictions.add(builder.isNotNull(activeStartDate));
        restrictions.add(builder.lessThanOrEqualTo(activeStartDate, now));
        Predicate notExpired = builder.or(builder.isNull(activeEndDate), builder.greaterThan(activeEndDate, now));
        restrictions.add(notExpired);       
    }

    /**
     * a contant ArchiveStatus that is 'N' (the default)
     */
    private static final ArchiveStatus statusNotArchived = new ArchiveStatus();

    /**
     * given a list of AND-ed restrictions, it determines wether or not the record is "archived", and adds the necessary restrictions.
     *  A record is considered  "archived" when the ARCHIVED field is explicitly "Y", so both null and "N" will do as non-archived
     * @param builder
     * @param restrictions
     * @param archivedPath
     * @param archivedFieldName
     */
    public static void notArchived(CriteriaBuilder builder, List<Predicate> restrictions, Path archivedPath, String archivedFieldName) {
        Path<ArchiveStatus> archiveValue = archivedPath.<ArchiveStatus> get(archivedFieldName);
        Predicate archivedNull = builder.isNull(archiveValue);
        Predicate notArchived = builder.equal(archiveValue, statusNotArchived);
        restrictions.add(builder.or(archivedNull, notArchived));
    }

}
