package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Jeff Fischer
 */
@Service("blRestrictionFactory")
public class RestrictionFactoryImpl implements RestrictionFactory {

    @Resource(name="blRestrictionFactoryMap")
    protected Map<String, Restriction> restrictions = new LinkedHashMap<String, Restriction>();

    public Map<String, Restriction> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(Map<String, Restriction> restrictions) {
        this.restrictions = restrictions;
    }

    @Override
    public Restriction getRestriction(String type, String propertyId) {
        Restriction restriction = restrictions.get(type).clone();
        restriction.setFieldPathBuilder(new FieldPathBuilder());

        return restriction;
    }
}
