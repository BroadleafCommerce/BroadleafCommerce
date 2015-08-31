package org.broadleafcommerce.core.search.dao;

import org.broadleafcommerce.core.search.domain.Field;
import org.broadleafcommerce.core.search.domain.SearchField;

/**
 * DAO used to interact with the database search fields
 *
 * @author Nick Crum (ncrum)
 */
public interface SearchFieldDao {

    /**
     * Returns the SearchField instance associated with the given field parameter, or null if non exists.
     *
     * @param field the Field we are looking for the SearchField for
     * @return a SearchField instance for the given field
     */
    public SearchField readSearchFieldForField(Field field);
}
