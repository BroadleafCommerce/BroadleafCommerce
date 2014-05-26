package org.broadleafcommerce.common.util.dao;

/**
 * Specify the attributes of a ORDER BY that should appear in the TypedQuery. Generally takes the form of:
 * </p>
 * <pre>
 * {@code
 * TypedQueryBuilder builder = new TypedQueryBuilder(com.MyClass, "item")
     .addOrder("i.name", true);
 * }
 * </pre>
 * </p>
 *
 * @author Jeff Fischer
 */
public class TQOrder {

    protected String expression;
    protected Boolean ascending = true;

    public TQOrder(String expression, Boolean ascending) {
        this.expression = expression;
        this.ascending = ascending;
    }

    public String toQl() {
        StringBuilder sb = new StringBuilder();
        sb.append(expression);
        sb.append(" ");
        sb.append(ascending != null && ascending?"ASC":"DESC");

        return sb.toString();
    }
}
