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

package mx.managers
{

import flash.display.DisplayObject;

import mx.core.IToolTip;
import mx.core.IUIComponent;
import mx.effects.IAbstractEffect;

[ExcludeClass]

/**
 *  @private
 *  This interface is used internally by Flex 3.
 *  Flex 2.0.1 used the IToolTipManager interface.
 *
 *  This interface does not extend IToolTipManager
 *  because IToolTipManager had a dependency on Effect,
 *  which has a dependency on UIComponent.
 *  IToolTipManager2 fixes this by depending on IAbstractEffect,
 *  a new interface in Flex 3.
 *
 *  IToolTipManager2 must be in the cross-versioning
 *  bootstrap set of classes (see bootstrap-defs.as)
 *  and if the bootstrap loader contains implementation
 *  classes such as Effect and UIComponent then any apps
 *  and modules it loads would use the bootstrap's version
 *  of these classes, instead of using their own versions.
 */
public interface IToolTipManager2
{
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  currentTarget
    //----------------------------------

	/**
	 *  @private
	 */
	function get currentTarget():DisplayObject;
	
	/**
	 *  @private
	 */
	function set currentTarget(value:DisplayObject):void;
	
    //----------------------------------
    //  currentToolTip
    //----------------------------------

	/**
	 *  @private
	 */
	function get currentToolTip():IToolTip;
	
	/**
	 *  @private
	 */
	function set currentToolTip(value:IToolTip):void;
	
    //----------------------------------
    //  enabled
    //----------------------------------

	/**
	 *  @private
	 */
	function get enabled():Boolean;
	
	/**
	 *  @private
	 */
	function set enabled(value:Boolean):void;
	
    //----------------------------------
    //  hideDelay
    //----------------------------------

	/**
	 *  @private
	 */
	function get hideDelay():Number;
	
	/**
	 *  @private
	 */
	function set hideDelay(value:Number):void;
	
    //----------------------------------
    //  hideEffect
    //----------------------------------

	/**
	 *  @private
	 */
	function get hideEffect():IAbstractEffect;

	/**
	 *  @private
	 */
	function set hideEffect(value:IAbstractEffect):void;
	
    //----------------------------------
    //  scrubDelay
    //----------------------------------

	/**
	 *  @private
	 */
	function get scrubDelay():Number;
	
	/**
	 *  @private
	 */
	function set scrubDelay(value:Number):void;
	
    //----------------------------------
    //  showDelay
    //----------------------------------

	/**
	 *  @private
	 */
	function get showDelay():Number;
	
	/**
	 *  @private
	 */
	function set showDelay(value:Number):void;
	
    //----------------------------------
    //  showEffect
    //----------------------------------

	/**
	 *  @private
	 */
	function get showEffect():IAbstractEffect;
	
	/**
	 *  @private
	 */
	function set showEffect(value:IAbstractEffect):void;

    //----------------------------------
    //  toolTipClass
    //----------------------------------

	/**
	 *  @private
	 */
	function get toolTipClass():Class;
	
	/**
	 *  @private
	 */
	function set toolTipClass(value:Class):void;

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	function registerToolTip(target:DisplayObject, oldToolTip:String,
							 newToolTip:String):void;
	
	/**
	 *  @private
	 */
	function registerErrorString(target:DisplayObject, oldErrorString:String,
								 newErrorString:String):void;
	
	/**
	 *  @private
	 */
	function sizeTip(toolTip:IToolTip):void;

	/**
	 *  @private
	 */
	function createToolTip(text:String, x:Number, y:Number,
						   errorTipBorderStyle:String = null,
						   context:IUIComponent = null):IToolTip;
	
	/**
	 *  @private
	 */
	function destroyToolTip(toolTip:IToolTip):void;
}

}
