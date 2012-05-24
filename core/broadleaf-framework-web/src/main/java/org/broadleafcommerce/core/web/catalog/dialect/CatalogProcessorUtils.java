package org.broadleafcommerce.core.web.catalog.dialect;

import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.springframework.context.ApplicationContext;
import org.thymeleaf.Arguments;
import org.thymeleaf.spring3.context.SpringWebContext;

public class CatalogProcessorUtils {

	public  static CatalogService getCatalogService(Arguments arguments) {
		final ApplicationContext appCtx = ((SpringWebContext) arguments.getContext()).getApplicationContext(); 
		return (CatalogService) appCtx.getBean("blCatalogService");
	}

}
