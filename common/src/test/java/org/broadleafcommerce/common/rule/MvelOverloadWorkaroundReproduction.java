package org.broadleafcommerce.common.rule;

import org.apache.commons.collections.map.MultiValueMap;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Run the test case as a java main application in a new JVM. This seems to be required to cause the variability in the ordering
 * of the call to Class#getMethods on SelectizeCollectionUtils. In this case, we should be using the refactor of
 * {@link SelectizeCollectionUtils} that no longer has method overloading for #intersection, which should avoid the mvel
 * issue altogether.
 * </p>
 * See {@link Class#getMethods()} for mention of the undetermined ordering behavior.
 *
 * @author Jeff Fischer
 */
public class MvelOverloadWorkaroundReproduction {

    public static void main(String[] items) {
        MvelTestUtils.exerciseWorkaround();
    }

}
