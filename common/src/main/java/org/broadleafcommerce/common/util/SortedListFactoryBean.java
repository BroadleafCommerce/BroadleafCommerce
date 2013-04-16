package org.broadleafcommerce.common.util;

import org.springframework.beans.factory.config.ListFactoryBean;
import org.springframework.core.Ordered;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Jeff Fischer
 */
public class SortedListFactoryBean extends ListFactoryBean {

    @Override
    protected List createInstance() {
        List response = super.createInstance();
        Collections.sort(response, new Comparator<Ordered>() {
            @Override
            public int compare(Ordered o1, Ordered o2) {
                return new Integer(o1.getOrder()).compareTo(o2.getOrder());
            }
        });

        return response;
    }
}
