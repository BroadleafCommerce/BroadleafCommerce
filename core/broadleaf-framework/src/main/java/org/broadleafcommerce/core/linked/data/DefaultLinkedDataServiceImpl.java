package org.broadleafcommerce.core.linked.data;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.springframework.stereotype.Service;

/**
 * @author Jacob Mitash
 */
@Service("blDefaultLinkedDataService")
public class DefaultLinkedDataServiceImpl extends AbstractLinkedDataService implements DefaultLinkedDataService {

    @Override
    public String getLinkedData(String url) throws JSONException {
        JSONArray schemaObjects = new JSONArray();

        schemaObjects.put(getDefaultOrganization(url));
        schemaObjects.put(getDefaultWebSite(url));
        schemaObjects.put(getDefaultBreadcrumbList());

        return schemaObjects.toString();
    }
}
