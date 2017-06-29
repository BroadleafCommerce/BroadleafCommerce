package org.broadleafcommerce.core.linked.data;

import org.codehaus.jettison.json.JSONException;

/**
 * This linked data service provides metadata relevant to every
 * page of the site that does not fall under the other services.
 * Implementation at {@link DefaultLinkedDataServiceImpl}
 *
 * @author Jacob Mitash
 */
public interface DefaultLinkedDataService {

    /**
     * Gets the linked data for default pages
     *
     * @param url the url of the page visited
     * @return string JSON representation of linked data
     */
    String getLinkedData(String url) throws JSONException;

}
