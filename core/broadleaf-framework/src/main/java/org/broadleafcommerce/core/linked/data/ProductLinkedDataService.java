package org.broadleafcommerce.core.linked.data;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.codehaus.jettison.json.JSONException;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by jacobmitash on 6/28/17.
 */
public interface ProductLinkedDataService {

    /**
     * Adds linked data to the model for given product
     * @param model where to add data
     * @param product the product to get info from
     */
    void addLinkedData(ModelAndView model, Product product, String url) throws JSONException;
}
