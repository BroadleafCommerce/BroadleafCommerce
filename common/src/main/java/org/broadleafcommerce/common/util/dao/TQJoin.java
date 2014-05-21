package org.broadleafcommerce.common.util.dao;

/**
 * Specify the attributes of a JOIN that should appear in the TypedQuery. Generally takes the form of:
 * </p>
 * <pre>
 * {@code
 * TypedQueryBuilder builder = new TypedQueryBuilder(com.MyClass, "item")
     .addJoin(new TQJoin("item.collection", "collection"))
     .addRestriction("collection.id", "=", 1L);
 * }
 * </pre>
 * </p>
 * The alias value can be used in subsequent restriction expressions.
 *
 * @author Jeff Fischer
 */
public class TQJoin {

    protected String expression;
    protected String alias;

    public TQJoin(String expression, String alias) {
        this.expression = expression;
        this.alias = alias;
    }

    public String toQl() {
        StringBuilder sb = new StringBuilder();
        sb.append(expression);
        sb.append(" ");
        sb.append(alias);

        return sb.toString();
    }
}
