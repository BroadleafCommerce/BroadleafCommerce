package org.broadleafcommerce.core.linked.data;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.codehaus.jettison.json.JSONException;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by jacobmitash on 6/28/17.
 */
public interface CategorySearchLinkedDataService {

    /**
     * Adds linked data to the model for given products
     * @param model where to add data
     * @param products the products to get info from
     */
    void addLinkedData(ModelAndView model, List<Product> products) throws JSONException;
}
