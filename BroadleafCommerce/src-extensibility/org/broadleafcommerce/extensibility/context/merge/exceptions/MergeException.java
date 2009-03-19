package org.broadleafcommerce.extensibility.context.merge.exceptions;

/**
 * This exception is thrown when a problem is encountered during
 * the actual merge of the source and patch documents.
 * 
 * @author jfischer
 *
 */
public class MergeException extends Exception {

	private static final long serialVersionUID = 1L;

	public MergeException() {
		super();
	}

	public MergeException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public MergeException(String arg0) {
		super(arg0);
	}

	public MergeException(Throwable arg0) {
		super(arg0);
	}

}
