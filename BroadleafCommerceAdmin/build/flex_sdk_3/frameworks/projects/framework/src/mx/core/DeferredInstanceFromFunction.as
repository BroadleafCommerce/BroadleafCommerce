////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.core
{

/**
 *  A deferred instance factory that uses a generator function
 *  to create an instance of the required object.
 *  An application uses the <code>getInstance()</code> method to
 *  create an instance of an object when it is first needed and get
 *  a reference to the object thereafter.
 *
 *  @see DeferredInstanceFromClass
 */
public class DeferredInstanceFromFunction implements IDeferredInstance
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

    /**
     *  Constructor.
	 *
	 *  @param generator A function that creates and returns an instance
	 *  of the required object.
     */
    public function DeferredInstanceFromFunction(generator:Function)
    {
		super();

    	this.generator = generator;
    }

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

    /**
	 * 	@private
     *	The generator function.
     */
    private var generator:Function;

	/**
	 * 	@private
	 * 	The generated value.
	 */
	private var instance:Object = null;

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *	Returns a reference to an instance of the desired object.
	 *  If no instance of the required object exists, calls the function
	 *  specified in this class' <code>generator</code> constructor parameter.
	 * 
	 *  @return An instance of the object.
	 */
	public function getInstance():Object
	{
		if (!instance)
			instance = generator();

		return instance;
	}
}

}
