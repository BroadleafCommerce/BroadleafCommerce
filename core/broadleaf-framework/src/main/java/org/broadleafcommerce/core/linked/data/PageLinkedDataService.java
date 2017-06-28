package org.broadleafcommerce.core.linked.data;

import org.codehaus.jettison.json.JSONException;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by jacobmitash on 6/28/17.
 */
public interface PageLinkedDataService {

    /**
     * Adds linked data to the homepage
     * @param model where to add data
     * @param url the URL of the home page
     */
    void addLinkedData(ModelAndView model, String url) throws JSONException;
}
