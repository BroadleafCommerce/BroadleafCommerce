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

import flash.display.DisplayObject;
import flash.geom.Rectangle;
import flash.text.StyleSheet;
import flash.text.TextFormat;
import flash.text.TextLineMetrics;
import mx.automation.IAutomationObject;
import mx.managers.IToolTipManagerClient;
import mx.styles.ISimpleStyleClient;

/**
 *  The IUITextField interface defines the basic set of APIs
 *  for UITextField instances.
 */
public interface IUITextField extends IIMESupport,
                         IFlexModule,
                         IInvalidating, ISimpleStyleClient,
                         IToolTipManagerClient, IUIComponent
{

	include "ITextFieldInterface.as"
	include "IInteractiveObjectInterface.as"

    /**
     *  @copy mx.core.UITextField#ignorePadding
     */
    function get ignorePadding():Boolean;
    function set ignorePadding(value:Boolean):void;

    /**
     *  @copy mx.core.UITextField#inheritingStyles
     */
    function get inheritingStyles():Object;
    function set inheritingStyles(value:Object):void;

    /**
     *  @copy mx.core.UITextField#nestLevel
     */
    function get nestLevel():int;
    function set nestLevel(value:int):void;

    /**
     *  @copy mx.core.UITextField#nonInheritingStyles
     */
    function get nonInheritingStyles():Object;
    function set nonInheritingStyles(value:Object):void;

    /**
     *  @copy mx.core.UITextField#nonZeroTextHeight
     */
    function get nonZeroTextHeight():Number;

    /**
     *  @copy mx.core.UITextField#getStyle()
     */
    function getStyle(styleProp:String):*;

    /**
     *  @copy mx.core.UITextField#getUITextFormat()
     */
    function getUITextFormat():UITextFormat

    /**
     *  @copy mx.core.UITextField#setColor()
     */
    function setColor(color:uint):void;

    /**
     *  @copy mx.core.UITextField#setFocus()
     */
    function setFocus():void;

    /**
     *  @copy mx.core.UITextField#truncateToFit()
     */
    function truncateToFit(truncationIndicator:String = null):Boolean;

}

}
