package org.broadleafcommerce.profile.aop.test;

import javax.annotation.Resource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.broadleafcommerce.profile.service.CustomerService;

public class CustomerStateAOP {

	@Resource(name = "blCustomerService")
	private CustomerService customerService;

    public Object processCustomerRetrieval(ProceedingJoinPoint call) throws Throwable {
        return customerService.readCustomerById(1L);
    }

}
