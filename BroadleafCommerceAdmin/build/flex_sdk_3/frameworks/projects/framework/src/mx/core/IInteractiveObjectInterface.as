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

/**
 *  The methods here would normally just be in IInteractiveObject,
 *  but for backward compatibility, their ancestor methods have to be included
 *  directly into IFlexDisplayObject, so these also have to be kept in 
 *  this separate include file so it can be used in ITextField
 */

    /**
     *  @copy flash.display.InteractiveObject#tabEnabled
     */
    function get tabEnabled():Boolean;
    function set tabEnabled(enabled:Boolean):void;
    
    /** 
     *  @copy flash.display.InteractiveObject#tabIndex
     */
    function get tabIndex():int;
    function set tabIndex(index:int):void;
    
    /** 
     *  @copy flash.display.InteractiveObject#focusRect
     */
    function get focusRect():Object; 
    function set focusRect(focusRect:Object):void;
    
    /** 
     *  @copy flash.display.InteractiveObject#mouseEnabled
     */
    function get mouseEnabled():Boolean;
    function set mouseEnabled(enabled:Boolean):void;
    
    /** 
     *  @copy flash.display.InteractiveObject#doubleClickEnabled
     */
    function get doubleClickEnabled():Boolean;
    function set doubleClickEnabled(enabled:Boolean):void;
    
    /** 
     *  @private
    function get accessibilityImplementation() : AccessibilityImplementation;
    function set accessibilityImplementation( value : AccessibilityImplementation ) : void;
     */
    
