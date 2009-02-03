package org.broadleafcommerce.test.integration;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.order.dao.OrderDaoJpa;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.profile.dao.ContactInfoDaoJpa;
import org.broadleafcommerce.profile.domain.ContactInfo;
import org.broadleafcommerce.profile.domain.User;
import org.broadleafcommerce.profile.service.UserService;
import org.broadleafcommerce.test.dataprovider.OrderDataProvider;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class OrderDaoTest extends BaseTest {

	String userName = new String();
	Long orderId;
	
	@Resource
	private OrderDaoJpa orderDao;
	
	@Resource(name = "userService")
	private UserService userService;

	@Resource
	private ContactInfoDaoJpa contactInfoDao;
	
	@Test(groups = {"createOrder"}, dataProvider="basicOrder",dataProviderClass=OrderDataProvider.class, dependsOnGroups={"readUser1","createContactInfo"})
	@Rollback(false)
	public void createOrder(Order order){
		userName = "user1";
		User user = userService.readUserByUsername(userName);
		ContactInfo ci = (contactInfoDao.readContactInfoByUserId(user.getId())).get(0);
		assert order.getId() == null;
		order.setUser(user);
		order.setContactInfo(ci);
		order = orderDao.maintianOrder(order);
		assert order.getId() != null;
		orderId = order.getId();
	}
	
	@Test(groups = {"readOrder"},dependsOnGroups={"createOrder"})
	public void readOrderById(){				
		Order result = orderDao.readOrderById(orderId);
		assert result != null;
	}

	@Test(groups = {"readOrdersForUser"},dependsOnGroups={"readUser1","createOrder"} )
	public void readOrdersForUser(){
		userName = "user1";
		User user = userService.readUserByUsername(userName);		
		List<Order> orders = orderDao.readOrdersForUser(user);
		assert orders.size() > 0;
	}
	
}
