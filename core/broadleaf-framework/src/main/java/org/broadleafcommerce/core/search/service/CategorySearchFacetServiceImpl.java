package org.broadleafcommerce.core.search.service;

import org.broadleafcommerce.core.search.dao.CategorySearchFacetDao;
import org.broadleafcommerce.core.search.domain.CategorySearchFacet;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Nathan Moore (nathandmoore)
 */
@Service("blCategorySearchFacetService")
public class CategorySearchFacetServiceImpl implements CategorySearchFacetService {

    @Resource(name = "blCategorySearchFacetDao")
    private CategorySearchFacetDao csfDao;

    public List<CategorySearchFacet> readCategorySearchFacetsBySearchFacet(Long id) { return csfDao.readCategorySearchFacetsBySearchFacet(id); }

}
