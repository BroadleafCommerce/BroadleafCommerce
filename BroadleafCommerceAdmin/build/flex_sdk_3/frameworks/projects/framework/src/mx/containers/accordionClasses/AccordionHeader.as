////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.containers.accordionClasses
{

import flash.display.DisplayObject;
import flash.events.Event;
import flash.events.MouseEvent;
import mx.containers.Accordion;
import mx.controls.Button;
import mx.core.Container;
import mx.core.FlexVersion;
import mx.core.EdgeMetrics;
import mx.core.IDataRenderer;
import mx.core.IFlexDisplayObject;
import mx.core.mx_internal;
import mx.styles.CSSStyleDeclaration;
import mx.styles.ISimpleStyleClient;
import mx.styles.StyleManager;

use namespace mx_internal;

[AccessibilityClass(implementation="mx.accessibility.AccordionHeaderAccImpl")]

/**
 *  The AccordionHeader class defines the appearance of the navigation buttons
 *  of an Accordion.
 *  You use the <code>getHeaderAt()</code> method of the Accordion class to get a reference
 *  to an individual AccordionHeader object.
 *
 *  @see mx.containers.Accordion
 */
public class AccordionHeader extends Button implements IDataRenderer
{
	include "../../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class mixins
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Placeholder for mixin by AccordionHeaderAccImpl.
	 */
	mx_internal static var createAccessibilityImplementation:Function;

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 */
	public function AccordionHeader()
	{
		super();

		// Since we play games with allowing selected to be set without
		// toggle being set, we need to clear the default toggleChanged
		// flag here otherwise the initially selected header isn't
		// drawn in a selected state.
		toggleChanged = false;
		mouseFocusEnabled = false;
		tabEnabled = false;
	}

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private var focusObj:DisplayObject;

	/**
	 *  @private
	 */
	private var focusSkin:IFlexDisplayObject;

	//--------------------------------------------------------------------------
	//
	//  Overridden properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  data
	//----------------------------------

	/**
	 *  @private
	 *  Storage for the _data property.
	 */
	private var _data:Object;

	/**
	 *  Stores a reference to the content associated with the header.
	 */
	override public function get data():Object
	{
		return _data;
	}
	
	/**
	 *  @private
	 */
	override public function set data(value:Object):void
	{
		_data = value;
	}
	
	//----------------------------------
	//  selected
	//----------------------------------

	/**
	 *  @private
	 */
	override public function set selected(value:Boolean):void
	{
		_selected = value;

		invalidateDisplayList();
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: UIComponent
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	override protected function createChildren():void
	{
		super.createChildren();
		
		if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
		{		
			// AccordionHeader has a bit of a conflict here. Our styleName points to
			// our parent Accordion, which has padding values defined. We also have
			// padding values defined on our type selector, but since class selectors
			// take precedence over type selectors, the type selector padding values
			// are ignored. Force them in here.
			var styleDecl:CSSStyleDeclaration = StyleManager.getStyleDeclaration(className);
			
			if (styleDecl)
			{
				var value:Number = styleDecl.getStyle("paddingLeft");
				if (!isNaN(value))
					setStyle("paddingLeft", value);
				value = styleDecl.getStyle("paddingRight");
				if (!isNaN(value))
					setStyle("paddingRight", value);
			}
		}
	}
	
	/**
	 *  @private
	 */
	override protected function initializeAccessibility():void
	{
		if (AccordionHeader.createAccessibilityImplementation != null)
			AccordionHeader.createAccessibilityImplementation(this);
	}
	
	/**
	 *  @private
	 */
	override public function drawFocus(isFocused:Boolean):void
	{
		// Accordion header focus is drawn inside the control.
		if (isFocused && !isEffectStarted)
		{
			if (!focusObj)
			{
				var focusClass:Class = getStyle("focusSkin");

				focusObj = new focusClass();

				var focusStyleable:ISimpleStyleClient = focusObj as ISimpleStyleClient;
				if (focusStyleable)
					focusStyleable.styleName = this;

				addChild(focusObj);

				// Call the draw method if it has one
				focusSkin = focusObj as IFlexDisplayObject;
			}

			if (focusSkin)
			{
				focusSkin.move(0, 0);
				focusSkin.setActualSize(unscaledWidth, unscaledHeight);
			}
			focusObj.visible = true;

			dispatchEvent(new Event("focusDraw"));
		}
		else if (focusObj)
		{
			focusObj.visible = false;
		}
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: Button
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	override mx_internal function layoutContents(unscaledWidth:Number,
											     unscaledHeight:Number,
											     offset:Boolean):void
	{
		super.layoutContents(unscaledWidth, unscaledHeight, offset);

		// Move the focus object to front.
		// AccordionHeader needs special treatment because it doesn't
		// show focus by having the standard focus ring display outside.
		if (focusObj)
			setChildIndex(focusObj, numChildren - 1);
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden event handlers: Button
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	override protected function rollOverHandler(event:MouseEvent):void
	{
		super.rollOverHandler(event);

		// The halo design specifies that accordion headers overlap
		// by a pixel when layed out. In order for the border to be
		// completely drawn on rollover, we need to set our index
		// here to bring this header to the front.
		var accordion:Accordion = Accordion(parent);
		if (accordion.enabled)
		{
			accordion.rawChildren.setChildIndex(this,
				accordion.rawChildren.numChildren - 1);
		}
	}
}

}
