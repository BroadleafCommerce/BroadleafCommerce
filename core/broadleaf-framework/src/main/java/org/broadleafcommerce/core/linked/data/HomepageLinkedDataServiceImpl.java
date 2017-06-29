package org.broadleafcommerce.core.linked.data;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * Created by jacobmitash on 6/28/17.
 */
@Service("blHomepageLinkedDataService")
public class HomepageLinkedDataServiceImpl extends AbstractLinkedDataService implements HomepageLinkedDataService {

    @Autowired
    protected Environment environment;

    @Override
    public String getLinkedData(String url) throws JSONException {
        JSONArray schemaObjects = new JSONArray();

        JSONObject webSite = getDefaultWebSite(url);

        JSONObject potentialAction = new JSONObject();
        potentialAction.put("@type", "SearchAction");
        potentialAction.put("target", url.concat(environment.getProperty("site.search")));
        potentialAction.put("query-input", "required name=query");

        webSite.put("potentialAction", potentialAction);

        schemaObjects.put(webSite);
        schemaObjects.put(getDefaultBreadcrumbList());
        schemaObjects.put(getDefaultOrganization(url));

        return schemaObjects.toString();
    }
}
