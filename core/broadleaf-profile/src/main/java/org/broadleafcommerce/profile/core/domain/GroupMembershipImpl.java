package org.broadleafcommerce.profile.core.domain;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jerry Ocanas (jocanas)
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_GROUP_MEMBERSHIP")
public class GroupMembershipImpl implements GroupMembership {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "GROUP_MEMBERSHIP_ID")
    @AdminPresentation(friendlyName = "GroupMembershipImpl_Group_Membership_Id", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @ManyToOne(targetEntity = CustomerGroupImpl.class)
    @JoinColumn(name = "CUSTOMER_GROUP_ID")
    @AdminPresentation(friendlyName = "GroupMembershipImpl_CustomerGroup")
    protected CustomerGroup customerGroup;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, targetEntity = CustomerImpl.class, optional=false)
    @JoinColumn(name = "CUSTOMER_ID")
    @AdminPresentation(friendlyName = "GroupMembershipImpl_Customer", excluded = true, visibility = VisibilityEnum.HIDDEN_ALL)
    protected Customer customer;

    @OneToMany(mappedBy = "groupMembership", targetEntity = RoleImpl.class)
    @AdminPresentation(friendlyName = "GroupMembershipImpl_Roles")
    protected List<Role> roles = new ArrayList<Role>();

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public CustomerGroup getCustomerGroup() {
        return customerGroup;
    }

    @Override
    public void setCustomerGroup(CustomerGroup customerGroup) {
        this.customerGroup = customerGroup;
    }

    @Override
    public Customer getCustomer() {
        return customer;
    }

    @Override
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public List<Role> getRoles() {
        return roles;
    }

    @Override
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
