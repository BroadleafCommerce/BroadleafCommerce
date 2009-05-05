package org.broadleafcommerce.controller;

import org.springframework.web.servlet.mvc.SimpleFormController;

public class SkuFormController extends SimpleFormController {
    /*protected final Log logger = LogFactory.getLog(getClass());
    private CatalogService catalogService;

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Override
    protected Object formBackingObject(HttpServletRequest request)
    throws ServletException {
        Product createProduct = new BroadleafProduct();
        Sku sku = new BroadleafSku();

        if (request.getParameter("productId") != null) {
            createProduct = catalogService.findProductById(Long.valueOf(request.getParameter("productId")));
            sku.setProduct(createProduct);
        }

        if (request.getParameter("skuId") != null){
            sku = catalogService.readSkuById(new Long(request.getParameter("skuId")));
            Map<String, ItemAttribute> attribs = sku.getItemAttributes();
            if (attribs == null) {
                attribs = new HashMap<String, ItemAttribute>();
            }
            attribs.put("", new BroadleafItemAttribute());
        }

        return sku;
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
    throws Exception {
        Sku sku = (Sku) command;

        ModelAndView mav = new ModelAndView(getSuccessView(), errors.getModel());

        if (errors.hasErrors()) {
            logger.debug("Error returning back to the form");

            return showForm(request, response, errors);
        }


        catalogService.saveSku(sku);
        mav.addObject("saved", true);

        return mav;
    }
    */
}
