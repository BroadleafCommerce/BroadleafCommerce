package org.broadleafcommerce.order.dao;

import java.util.List;

import org.broadleafcommerce.order.domain.FullfillmentGroup;
import org.broadleafcommerce.order.domain.FullfillmentGroupItem;

public interface FullfillmentGroupItemDao {
	
	public FullfillmentGroupItem readFullfillmentGroupItemById(Long fullfillmentGroupItemId);
	
	public FullfillmentGroupItem maintainFullfillmentGroupItem(FullfillmentGroupItem fullfillmentGroupItem);
	
	public List<FullfillmentGroupItem> readFullfillmentGroupItemsForFullfillmentGroup(FullfillmentGroup fullfillmentGroup);
	
	public void deleteFullfillmentGroupItem(FullfillmentGroupItem fullfillmentGroupItem);
	
	public FullfillmentGroupItem create();
	
}
