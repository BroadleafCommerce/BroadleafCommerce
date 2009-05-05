package org.broadleafcommerce.controller;

import org.springframework.web.servlet.mvc.SimpleFormController;

public class ListOrdersFormController extends SimpleFormController {
    /*protected final Log logger = LogFactory.getLog(getClass());
    private OrderService orderService;

    private CustomerService customerService;

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    protected Object formBackingObject(HttpServletRequest request)throws ServletException {
        return new BroadleafOrder();
    }

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Customer customer = customerService.readCustomerByUsername(auth.getName());
        List<BroadleafOrder> orderList = orderService.getOrdersForCustomer(customer.getId());
        Map<Object, Object> model = new HashMap<Object, Object>();
        model.put("orderList", orderList);
        return new ModelAndView("listOrders", model);
    }*/
}
