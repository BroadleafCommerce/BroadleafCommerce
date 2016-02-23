package org.broadleafcommerce.core.search.dao;

import org.broadleafcommerce.core.search.domain.CategorySearchFacet;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * DAO used to interact with the database {@link org.broadleafcommerce.core.search.domain.CategorySearchFacet}
 *
 * @author Nathan Moore (nathandmoore)
 */
public interface CategorySearchFacetDao {

    /**
     * Retrieves a list of {@code CategorySearchFacet} with foreign keys to a specified {@code SearchFacet}
     *
     * @param id    ID of a {@code SearchFacet}
     * @return      List of {@code CategorySearchFacets} referencing the specified {@code SearchFacet}
     */
    @Nonnull
    public List<CategorySearchFacet> readCategorySearchFacetsBySearchFacet(@Nonnull Long id);

}
