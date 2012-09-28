package org.broadleafcommerce.core.checkout.service.workflow;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.catalog.domain.Sku;
import org.broadleafcommerce.core.inventory.exception.ConcurrentInventoryModificationException;
import org.broadleafcommerce.core.inventory.service.InventoryService;
import org.broadleafcommerce.core.workflow.ErrorHandler;
import org.broadleafcommerce.core.workflow.ProcessContext;
import org.broadleafcommerce.core.workflow.WorkflowException;
import org.springframework.stereotype.Component;

/**
 * This error handler essentially does exactly what the {@link DefaultErrorHandler} does, 
 * but it also attempts to detect whether inventory was decremented during the workflow. If 
 * so, it attampts to compensate for that inventory.
 * 
 * @author Kelly Tisdell
 *
 */
@Component("blInventoryCompensatingCheckoutErrorHandler")
public class InventoryCompensatingCheckoutErrorHandler implements ErrorHandler {

	private static final Log LOG = LogFactory.getLog(InventoryCompensatingCheckoutErrorHandler.class);
	
	@Resource(name="blInventoryService")
	protected InventoryService inventoryService;
	
    @SuppressWarnings("unused")
    private String name;
    
    private int maxRetries = 5;
	
	@Override
	public void setBeanName(String name) {
		this.name = name;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void handleError(ProcessContext context, Throwable th)
			throws WorkflowException {
		context.stopProcess();
		
		CheckoutSeed seed = ((CheckoutContext) context).getSeedData();
		
		// The DecrementInventoryActivity, if successful, adds the inventory that was decremented to the context.  If we 
		// find that, then we need to attempt to compensate for that inventory.
		if (seed.getUserDefinedFields() != null && seed.getUserDefinedFields().get("BLC_INVENTORY_DECREMENTED") != null) {
			Map<Sku, Integer> inventoryToIncrement = (Map<Sku, Integer>)seed.getUserDefinedFields().get("BLC_INVENTORY_DECREMENTED");
			if (! inventoryToIncrement.isEmpty()) {
				int retryCount = 0;
	
		        while (retryCount < maxRetries) {
		            try {
		            	inventoryService.incrementInventory(inventoryToIncrement);
		            	break;
		            } catch (ConcurrentInventoryModificationException ex) {
		                retryCount++;
		                if (retryCount == maxRetries) {
		                    LOG.error("After an exception was encountered during checkout, where inventory was decremented. " + maxRetries + " attempts were made to compensate, " +
		                    		"but were unsuccessful for order ID: " + seed.getOrder().getId() + ". This should be corrected manually!", ex);
		                }
		            } catch (RuntimeException ex) {
		            	LOG.error("An unexpected error occured in the error handler of the checkout workflow trying to compensate for inventory. This happend for order ID: " +
		            			seed.getOrder().getId() + ". This should be corrected manually!", ex);
		            	break;
		            }
		        }
			}
		}
		
        LOG.error("An error occurred during the workflow", th);
        throw new WorkflowException(th);
	}

}
