package org.broadleafcommerce.common.rule;

import org.apache.commons.collections.map.MultiValueMap;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Run the test case as a java main application in a new JVM. This seems to be required to cause the variability in the ordering
 * of the call to Class#getMethods on SelectizeCollectionUtils. For our case to cause the compiled expression corruption, we
 * must end up with an invocation of Class#getMethods that returns the #intersection(String, Iterable) Method positioned
 * in the methods array before the #intersection(Iterable, Iterable) version of the method. Once you have a JVM in place, the
 * ordering seems consistent, so you have to start a new JVM to hope to see the variable ordering phenomenon.
 * See {@link MvelTestUtils} for the examples of the overloaded static intersection method implementations.
 * </p>
 * See {@link Class#getMethods()} for mention of the undetermined ordering behavior.
 *
 * @author Jeff Fischer
 */
public class MvelOverloadFailureReproduction {

    public static void main(String[] items) {
        MvelTestUtils.exerciseFailure();
    }

}
