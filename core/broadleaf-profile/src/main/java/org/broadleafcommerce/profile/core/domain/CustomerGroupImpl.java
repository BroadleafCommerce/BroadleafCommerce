package org.broadleafcommerce.profile.core.domain;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;

import javax.persistence.*;

/**
 * @author Jerry Ocanas (jocanas)
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_CUSTOMER_GROUP")
public class CustomerGroupImpl implements CustomerGroup {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CUSTOMER_GROUP_ID")
    @AdminPresentation(friendlyName = "CustomerGroupImpl_Customer_Group_Id", visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @Column(name = "NAME")
    @AdminPresentation(friendlyName = "CustomerGroupImpl_Name")
    protected String name;
    
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
