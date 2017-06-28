package org.broadleafcommerce.core.linked.data;

import org.codehaus.jettison.json.JSONException;

/**
 * Created by jacobmitash on 6/28/17.
 */
public interface HomepageLinkedDataService {

    /**
     * Adds linked data to the homepage
     * @param url the URL of the home page
     * @return JSON string of linked data
     */
    String getLinkedData(String url) throws JSONException;
}
