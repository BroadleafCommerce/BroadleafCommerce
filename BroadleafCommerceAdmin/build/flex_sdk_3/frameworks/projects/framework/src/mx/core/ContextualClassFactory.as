////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.core
{

import flash.utils.getQualifiedClassName;

/**
 *  A class factory that provides a system manager
 *  as a context of where the class should be created.
 */
public class ContextualClassFactory extends ClassFactory
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
	 *  @param generator The Class that the <code>newInstance()</code> method
	 *  uses to generate objects from this factory object.
	 *
	 *  @param systemManager The system manager context in which the object
	 *  should be created.
	 */
	public function ContextualClassFactory(
							generator:Class = null,
							moduleFactory:IFlexModuleFactory = null)
	{
		super(generator);

		this.moduleFactory = moduleFactory;
	}
 
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	/**
	 *  The context in which an object should be created.
	 *
	 *  <p>This is used to solve using the embedded fonts in an application SWF
	 *  when the framework is loaded as an RSL
	 *  (the RSL has its own SWF context).
	 *  Embedded fonts may only be accessed from the SWF file context
	 *  in which they were created.
	 *  By using the <code>systemManager</code> of the application SWF,
	 *  the RSL can create objects in the application SWF context
	 *  that will have access to the application's embedded fonts.
	 *  <code>moduleFactory</code> will call <code>create()</code> to create
	 *  an object in the context of the <code>moduleFactory</code>.</p>
	 *
	 *  @default null
	 */
	public var moduleFactory:IFlexModuleFactory;

	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  Creates a new instance of the <code>generator</code> class,
	 *  with the properties specified by <code>properties</code>.
	 *
	 *  <p>This method implements the <code>newInstance()</code> method
	 *  of the IFactory interface.</p>
	 *
	 *  @return The new instance that was created.
	 */
	override public function newInstance():*
	{
		var instance:Object = null;
		
		if (moduleFactory)
			instance = moduleFactory.create(getQualifiedClassName(generator));
		
		if (!instance)
			instance = super.newInstance();			

		if (properties)
		{
			for (var p:String in properties)
			{
        		instance[p] = properties[p];
			}
		}

		return instance;
	}

}

}
