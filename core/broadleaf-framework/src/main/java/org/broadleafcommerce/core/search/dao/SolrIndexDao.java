/*
 * Broadleaf Commerce Confidential
 * _______________________________
 *
 * [2009] - [2013] Broadleaf Commerce, LLC
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
 */
package org.broadleafcommerce.core.search.dao;

import java.util.List;

/**
 * Provides some specialized catalog retrieval methods for {@link org.broadleafcommerce.core.search.service.solr.SolrIndexService} for maximum
 * efficiency of solr document creation during indexing.
 *
 * @author Jeff Fischer
 */
public interface SolrIndexDao {

    /**
     * Retrieve the product ids that belong to this category. The products will be sorted
     * based on their display order.
     *
     * @param categoryId the category
     * @return the list of sorted product ids
     */
    List<Long> readProductIdsByCategory(Long categoryId);

}
