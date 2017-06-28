package org.broadleafcommerce.core.linked.data;

import org.broadleafcommerce.core.catalog.domain.Product;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by jacobmitash on 6/28/17.
 */
@Service("blCategorySearchLinkedDataService")
public class CategorySearchLinkedDataServiceImpl implements CategorySearchLinkedDataService {

    @Override
    public void addLinkedData(ModelAndView model, List<Product> products) throws JSONException {
        JSONObject linkedData = new JSONObject();
        linkedData.put("@context", "http://schema.org");
        linkedData.put("@type", "ItemList");
        JSONArray itemList = new JSONArray();
        for(int i = 0; i < products.size(); i++) {
            JSONObject item = new JSONObject();
            item.put("@type", "ListItem");
            item.put("position", i + 1);
            item.put("url", products.get(i).getUrl());
            itemList.put(item);
        }
        linkedData.put("itemListElement", itemList);

        model.addObject("linkedData", linkedData.toString(2));
    }
}
