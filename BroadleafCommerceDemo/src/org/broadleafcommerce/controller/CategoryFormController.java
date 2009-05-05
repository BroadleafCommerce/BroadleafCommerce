package org.broadleafcommerce.controller;

import org.springframework.web.servlet.mvc.SimpleFormController;

public class CategoryFormController extends SimpleFormController {
	/*
    protected final Log logger = LogFactory.getLog(getClass());
    private CatalogService catalogService;

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    protected Object formBackingObject(HttpServletRequest request)
    throws ServletException {
        return new CreateCategory();
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
    throws Exception {
        CreateCategory createCategory = (CreateCategory) command;
        Category category = new BroadleafCategory();
        category.setName(createCategory.getName());
        category.setUrlKey(createCategory.getUrlKey());
        category.setUrl(createCategory.getUrl());

        if (StringUtils.isNotBlank(createCategory.getParentId())){
            Category parentCategory = catalogService.findCategoryById(new Long(createCategory.getParentId()));
            if (parentCategory != null){
                category.setParentCategory(parentCategory);
            }
        }

        ModelAndView mav = new ModelAndView(getSuccessView(), errors.getModel());

        if (errors.hasErrors()) {
            logger.debug("Error returning back to the form");

            return showForm(request, response, errors);
        }

        catalogService.saveCategory(category);
        mav.addObject("saved", true);

        return mav;
    }
    */
}
