package org.broadleafcommerce.core.web.catalog.dialect;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

public class CatalogDialect extends AbstractDialect {

	@Override
	public String getPrefix() {
		return "blc-catalog";
	}

	@Override
	public boolean isLenient() {
		return true;
	}
	
	@Override 
    public Set<IProcessor> getProcessors() { 
        final Set<IProcessor> processors = new HashSet<IProcessor>(); 
        processors.add(new CategoriesProcessor()); 
        processors.add(new TestProcessor()); 
        return processors; 
    } 

}
