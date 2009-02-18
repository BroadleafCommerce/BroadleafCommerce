package org.broadleafcommerce.test.integration;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.order.dao.FullfillmentGroupDao;
import org.broadleafcommerce.order.dao.OrderDaoJpa;
import org.broadleafcommerce.order.domain.DefaultFullfillmentGroup;
import org.broadleafcommerce.order.domain.FullfillmentGroup;
import org.broadleafcommerce.order.domain.Order;
import org.broadleafcommerce.profile.dao.AddressDaoJpa;
import org.broadleafcommerce.profile.domain.Address;
import org.broadleafcommerce.profile.domain.Customer;
import org.broadleafcommerce.profile.service.CustomerService;
import org.broadleafcommerce.test.dataprovider.FullfillmentGroupDataProvider;
import org.springframework.test.annotation.Rollback;
import org.testng.annotations.Test;

public class FullfillmentGroupDaoTest extends BaseTest {

    private Order order;
    private Long defaultFulfillmentGroupId;
    private Long fulfillmentGroupId;

    @Resource
    private FullfillmentGroupDao fullfillmentGroupDao;
    
    @Resource
    private CustomerService customerService;

    @Resource
    private AddressDaoJpa addressDao;

    @Resource
    private OrderDaoJpa orderDao;

    @Test(groups={"createDefaultFullfillmentGroup"}, dataProvider="basicFullfillmentGroup", dataProviderClass=FullfillmentGroupDataProvider.class,dependsOnGroups={"createOrder","createAddress"})
    @Rollback(false)
    public void createDefaultFullfillmentGroup(FullfillmentGroup fullfillmentGroup){
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        Address address = (addressDao.readAddressByUserId(customer.getId())).get(0);
        Order salesOrder= (orderDao.readOrdersForCustomer(customer)).get(0);

        DefaultFullfillmentGroup newFG = fullfillmentGroupDao.createDefault();
        newFG.setAddress(address);
        newFG.setCost(fullfillmentGroup.getCost());
        newFG.setMethod(fullfillmentGroup.getMethod());
        newFG.setOrderId(salesOrder.getId());
        newFG.setReferenceNumber(fullfillmentGroup.getReferenceNumber());        
        
        assert newFG.getId() == null;
        fullfillmentGroupDao.maintainDefaultFullfillmentGroup(newFG);
        assert fullfillmentGroup.getId() != null;
        order = salesOrder;
        defaultFulfillmentGroupId = fullfillmentGroup.getId();
    }

    @Test (groups = {"readDefaultFullfillmentGroupForOrder"}, dependsOnGroups={"createFullfillmentGroup"})
    public void readDefaultFullfillmentGroupForOrder(){
        DefaultFullfillmentGroup dfg = fullfillmentGroupDao.readDefaultFullfillmentGroupForOrder(order);
        assert dfg.getId() != null;
        assert dfg.getType().equals("DEFAULT");
    }

    @Test (groups = {"readDefaultFullfillmentGroupForId"}, dependsOnGroups={"createFullfillmentGroup"})
    public void readDefaultFullfillmentGroupForId(){
        DefaultFullfillmentGroup dfg = fullfillmentGroupDao.readDefaultFullfillmentGroupById(defaultFulfillmentGroupId);
        assert dfg != null;
        assert dfg.getId() != null;
        assert dfg.getType().equals("DEFAULT");
    }

    @Test(groups={"createFullfillmentGroup"}, dataProvider="basicFullfillmentGroup", dataProviderClass=FullfillmentGroupDataProvider.class,dependsOnGroups={"createOrder","createAddress"})
    @Rollback(false)
    public void createFullfillmentGroup(FullfillmentGroup fullfillmentGroup){
        String userName = "customer1";
        Customer customer = customerService.readCustomerByUsername(userName);
        Address address = (addressDao.readAddressByUserId(customer.getId())).get(0);
        Order salesOrder= (orderDao.readOrdersForCustomer(customer)).get(0);

        FullfillmentGroup newFG = fullfillmentGroupDao.create();
        newFG.setAddress(address);
        newFG.setCost(fullfillmentGroup.getCost());
        newFG.setMethod(fullfillmentGroup.getMethod());
        newFG.setOrderId(salesOrder.getId());
        newFG.setReferenceNumber(fullfillmentGroup.getReferenceNumber());        
        
        assert newFG.getId() == null;
        fullfillmentGroupDao.maintainFullfillmentGroup(newFG);
        assert fullfillmentGroup.getId() != null;
        order = salesOrder;
        fulfillmentGroupId = fullfillmentGroup.getId();
    }

    @Test (groups = {"readFullfillmentGroupsForId"}, dependsOnGroups={"createFullfillmentGroup"})
    public void readFullfillmentGroupsForId(){
        FullfillmentGroup fg = fullfillmentGroupDao.readFullfillmentGroupById(fulfillmentGroupId);
        assert fg != null;
        assert fg.getId() != null;
    }

    @Test (groups = {"readFullfillmentGroupsForOrder"}, dependsOnGroups={"createFullfillmentGroup"})
    public void readFullfillmentGroupsForOrder(){
        List<FullfillmentGroup> fgs = fullfillmentGroupDao.readFullfillmentGroupsForOrder(order);
        assert fgs != null;
        assert fgs.size() > 0;
        boolean defaultFGReturned = false;
        for (FullfillmentGroup fullfillmentGroup : fgs) {
			if(fullfillmentGroup.getType() == "DEFAULT"){
				defaultFGReturned = true;
			}
		}
        assert defaultFGReturned;
    }

}
