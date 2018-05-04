package org.broadleafcommerce.admin.web.controller.entity;

import org.apache.commons.lang3.StringUtils;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.openadmin.web.controller.entity.AdminBasicEntityController;
import org.broadleafcommerce.openadmin.web.form.entity.EntityForm;
import org.broadleafcommerce.openadmin.web.form.entity.Field;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller("blAdminBaseProductController")
@RequestMapping("/product:" + AdminBaseProductController.SECTION_KEY)
public class AdminBaseProductController extends AdminBasicEntityController {

    public static final String SECTION_KEY = "product";

    @Resource(name = "blCatalogService")
    protected CatalogService catalogService;

    @Override
    protected void modifyAddEntityForm(EntityForm ef, Map<String, String> pathVars) {
        String defaultCategoryUrlPrefix = null;
        Field defaultCategory = ef.findField("defaultCategory");
        if (defaultCategory != null && StringUtils.isNotBlank(defaultCategory.getValue())) {
            Category cat = catalogService.findCategoryById(Long.parseLong(defaultCategory.getValue()));
            defaultCategoryUrlPrefix = cat.getUrl();
        }

        Field overrideGeneratedUrl = ef.findField("overrideGeneratedUrl");
        if (overrideGeneratedUrl != null) {
            overrideGeneratedUrl.setFieldType(SupportedFieldType.HIDDEN.toString().toLowerCase());
            boolean overriddenUrl = Boolean.parseBoolean(overrideGeneratedUrl.getValue());
            Field fullUrl = ef.findField("url");
            if (fullUrl != null) {
                fullUrl.withAttribute("overriddenUrl", overriddenUrl)
                        .withAttribute("sourceField", "defaultSku--name")
                        .withAttribute("toggleField", "overrideGeneratedUrl")
                        .withAttribute("prefix-selector", "#field-defaultCategory")
                        .withAttribute("prefix", defaultCategoryUrlPrefix)
                        .withFieldType(SupportedFieldType.GENERATED_URL.toString().toLowerCase());
            }
        }
    }

    @Override
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String viewEntityForm(HttpServletRequest request, HttpServletResponse response, Model model,
                                 @PathVariable Map<String, String> pathVars,
                                 @PathVariable(value = "id") String id) throws Exception {
        String view = super.viewEntityForm(request, response, model, pathVars, id);

        return view;
    }
}
