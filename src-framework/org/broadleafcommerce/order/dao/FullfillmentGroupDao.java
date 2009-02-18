package org.broadleafcommerce.order.dao;

import java.util.List;

import org.broadleafcommerce.order.domain.DefaultFullfillmentGroup;
import org.broadleafcommerce.order.domain.FullfillmentGroup;
import org.broadleafcommerce.order.domain.Order;

public interface FullfillmentGroupDao {

	public FullfillmentGroup readFullfillmentGroupById(Long fullfillmentGroupId);
	
	public FullfillmentGroup maintainFullfillmentGroup(FullfillmentGroup fullfillmentGroup);
	
	public List<FullfillmentGroup> readFullfillmentGroupsForOrder(Order order);
	
	public DefaultFullfillmentGroup maintainDefaultFullfillmentGroup(DefaultFullfillmentGroup defaultFullfillmentGroup);
	
	public DefaultFullfillmentGroup readDefaultFullfillmentGroupById(Long fullfillmentGroupId);
	
	public DefaultFullfillmentGroup readDefaultFullfillmentGroupForOrder(Order order);
	
	public DefaultFullfillmentGroup createDefault();
	
	public FullfillmentGroup create();
}
