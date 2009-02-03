package org.broadleafcommerce.test.integration;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.order.dao.OrderDaoJpa;
import org.broadleafcommerce.order.dao.OrderShippingDao;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.order.domain.OrderShipping;
import org.broadleafcommerce.profile.dao.AddressDaoJpa;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.User;
import org.broadleafcommerce.profile.service.UserService;
import org.broadleafcommerce.test.dataprovider.OrderShippingDataProvider;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class OrderShippingDaoTest extends BaseTest {

	private Order order;
	
	@Resource
	private OrderShippingDao orderShippingDao;
	
	@Resource
	private UserService userService;
	
	@Resource
	private AddressDaoJpa addressDao;

	@Resource
	private OrderDaoJpa orderDao;
	
	@Test(groups={"createOrderShipping"}, dataProvider="basicOrderShipping", dataProviderClass=OrderShippingDataProvider.class,dependsOnGroups={"createOrder","createAddress"})
	@Rollback(false)
	public void createOrderShipping(OrderShipping orderShipping){
		String userName = "user1";
		User user = userService.readUserByUsername(userName);
		Address address = (addressDao.readAddressByUserId(user.getId())).get(0);
		Order salesOrder= (orderDao.readOrdersForUser(user)).get(0);
		
		orderShipping.setAddress(address);
		orderShipping.setOrder(salesOrder);
		
		assert orderShipping.getId() == null;
		orderShippingDao.maintainOrderShipping(orderShipping);
		assert orderShipping.getId() != null;
		order = salesOrder;
	}
	
	@Test (groups = {"readOrderShippingForOrder"}, dependsOnGroups={"createOrderShipping"})
	public void readOrderShippingForOrder(){
		List<OrderShipping> shippings = orderShippingDao.readOrderShippingForOrder(order);
		assert shippings != null;
		assert shippings.size() > 0;
	}
	
}
