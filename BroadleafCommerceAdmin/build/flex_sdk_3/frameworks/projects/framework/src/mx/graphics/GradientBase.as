////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.graphics
{

import flash.events.Event;
import flash.events.EventDispatcher;
import mx.core.mx_internal;
import mx.events.PropertyChangeEvent;

use namespace mx_internal;

[DefaultProperty("entries")]

/**
 *  The GradientBase class is the base class for
 *  LinearGradient, LinearGradientStroke, and RadialGradient.
 */
public class GradientBase extends EventDispatcher
{
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  Constructor.
	 */
	public function GradientBase() 
	{
		super();
	}
	
	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

 	/**
	 *  @private
	 */
	mx_internal var colors:Array /* of uint */ = [];

 	/**
	 *  @private
	 */
	mx_internal var ratios:Array /* of Number */ = [];

 	/**
	 *  @private
	 */
	mx_internal var alphas:Array /* of Number */ = [];
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  entries
	//----------------------------------

 	/**
	 *  @private
	 *  Storage for the entries property.
	 */
	private var _entries:Array = [];
	
	[Bindable("propertyChange")]
    [Inspectable(category="General", arrayType="mx.graphics.GradientEntry")]

	/**
	 *  An Array of GradientEntry objects
	 *  defining the fill patterns for the gradient fill.
	 *
	 *  @default []
	 */
	public function get entries():Array
	{
		return _entries;
	}

 	/**
	 *  @private
	 */
	public function set entries(value:Array):void
	{
		var oldValue:Array = _entries;
		_entries = value;
		
		processEntries();
		
		dispatchGradientChangedEvent("entries", oldValue, value);
	}

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Extract the gradient information in the public <code>entries</code>
	 *  Array into the internal <code>colors</code>, <code>ratios</code>,
	 *  and <code>alphas</code> arrays.
	 */
	private function processEntries():void
	{
		colors = [];
		ratios = [];
		alphas = [];

		if (!_entries || _entries.length == 0)
			return;

		var ratioConvert:Number = 255;

		var i:int;
		
		var n:int = _entries.length;
		for (i = 0; i < n; i++)
		{
			var e:GradientEntry = _entries[i];
			e.addEventListener(PropertyChangeEvent.PROPERTY_CHANGE, 
							   entry_propertyChangeHandler, false, 0, true);
			colors.push(e.color);
			alphas.push(e.alpha);
			ratios.push(e.ratio * ratioConvert);
		}
		
		if (isNaN(ratios[0]))
			ratios[0] = 0;
			
		if (isNaN(ratios[n - 1]))
			ratios[n - 1] = 255;
		
		i = 1;

		while (true)
		{
			while (i < n && !isNaN(ratios[i]))
			{
				i++;
			}

			if (i == n)
				break;
				
			var start:int = i - 1;
			
			while (i < n && isNaN(ratios[i]))
			{
				i++;
			}
			
			var br:Number = ratios[start];
			var tr:Number = ratios[i];
			
			for (var j:int = 1; j < i - start; j++)
			{
				ratios[j] = br + j * (tr - br) / (i - start);
			}
		}
	}

	/**
	 *  Dispatch a gradientChanged event.
	 */
	mx_internal function dispatchGradientChangedEvent(prop:String,
													  oldValue:*, value:*):void
	{
		dispatchEvent(PropertyChangeEvent.createUpdateEvent(this, prop,
															oldValue, value));
	}

	//--------------------------------------------------------------------------
	//
	//  Event handlers
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private function entry_propertyChangeHandler(event:Event):void
	{
		processEntries();

		dispatchGradientChangedEvent("entries", entries, entries);
	}
}

}
