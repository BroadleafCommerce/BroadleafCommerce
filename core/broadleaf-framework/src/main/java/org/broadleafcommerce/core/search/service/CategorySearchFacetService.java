package org.broadleafcommerce.core.search.service;

import org.broadleafcommerce.core.search.domain.CategorySearchFacet;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Service used to interact with {@link org.broadleafcommerce.core.search.dao.CategorySearchFacetDao}
 *
 * @author Nathan Moore (nathandmoore)
 */
public interface CategorySearchFacetService {

    /**
     * Retrieves a list of {@code CategorySearchFacet} with foreign keys to a specified {@code SearchFacet}
     *
     * @param id    ID of a {@code SearchFacet}
     * @return      List of {@code CategorySearchFacets} referencing the specified {@code SearchFacet}
     */
    @Nonnull
    public List<CategorySearchFacet> readCategorySearchFacetsBySearchFacet(@Nonnull Long id);

}
