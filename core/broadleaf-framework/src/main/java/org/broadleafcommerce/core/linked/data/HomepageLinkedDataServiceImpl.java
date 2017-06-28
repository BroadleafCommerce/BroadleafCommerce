package org.broadleafcommerce.core.linked.data;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 * Created by jacobmitash on 6/28/17.
 */
@Service("blHomepageLinkedDataService")
public class HomepageLinkedDataServiceImpl implements HomepageLinkedDataService {

    @Override
    public String getLinkedData(String url) throws JSONException {

        JSONObject linkedData = new JSONObject();
        linkedData.put("@context", "http://schema.org");
        linkedData.put("@type", "WebSite");
        linkedData.put("name", "The Heat Clinic"); //TODO: website name
        linkedData.put("url", url);
        linkedData.put("image", "https://example.com/img.png"); //TODO: logo/image?

        JSONObject potentialAction = new JSONObject();
        potentialAction.put("@type", "SearchAction");
        potentialAction.put("target", url.concat("search?q={query}")); //TODO: probably not a good way to do that
        potentialAction.put("query-input", "required name=query");

        linkedData.put("potentialAction", potentialAction);

        return linkedData.toString();
    }
}
