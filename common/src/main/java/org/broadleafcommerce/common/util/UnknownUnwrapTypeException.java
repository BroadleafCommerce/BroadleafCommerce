package org.broadleafcommerce.common.util;

/**
 * @author Jeff Fischer
 */
public class UnknownUnwrapTypeException extends RuntimeException {

    public UnknownUnwrapTypeException(Class unwrapType) {
        super( "Cannot unwrap to requested type [" + unwrapType.getName() + "]" );
    }

    public UnknownUnwrapTypeException(Class unwrapType, Throwable root) {
        this( unwrapType );
        super.initCause( root );
    }

}
