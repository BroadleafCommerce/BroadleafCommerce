package org.broadleafcommerce.controller;

import org.springframework.web.servlet.mvc.SimpleFormController;

public class ListCategoryFormController extends SimpleFormController {
    /*protected final Log logger = LogFactory.getLog(getClass());
    private CatalogService catalogService;

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    protected Object formBackingObject(HttpServletRequest request)throws ServletException {
        return new BroadleafCategory();
    }

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Category> categoryList = catalogService.findAllCategories();
        Map<Object, Object> model = new HashMap<Object, Object>();
        model.put("categoryList", categoryList);

        return new ModelAndView(getSuccessView(), model);
    }*/
}
