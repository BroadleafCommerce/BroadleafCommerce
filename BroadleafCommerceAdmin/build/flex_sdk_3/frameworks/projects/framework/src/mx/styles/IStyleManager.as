////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.styles
{

import flash.events.IEventDispatcher;

[ExcludeClass]

/**
 *  @private
 *  This interface was used by Flex 2.0.1.
 *  Flex 3 now uses IStyleManager2 instead.
 */
public interface IStyleManager
{
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
	//  inheritingStyles
    //----------------------------------

	function get inheritingStyles():Object;
	function set inheritingStyles(value:Object):void;

    //----------------------------------
	//  stylesRoot
    //----------------------------------

	function get stylesRoot():Object;
	function set stylesRoot(value:Object):void;

    //----------------------------------
	//  typeSelectorCache
    //----------------------------------

	function get typeSelectorCache():Object;
	function set typeSelectorCache(value:Object):void;

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

	function getStyleDeclaration(selector:String):CSSStyleDeclaration;
	
	function setStyleDeclaration(selector:String,
								styleDeclaration:CSSStyleDeclaration,
								update:Boolean):void;
	
	function clearStyleDeclaration(selector:String, update:Boolean):void;
	
	function registerInheritingStyle(styleName:String):void;
	
	function isInheritingStyle(styleName:String):Boolean;
	
	function isInheritingTextFormatStyle(styleName:String):Boolean;
	
	function registerSizeInvalidatingStyle(styleName:String):void;
	
	function isSizeInvalidatingStyle(styleName:String):Boolean;
	
	function registerParentSizeInvalidatingStyle(styleName:String):void;
	
	function isParentSizeInvalidatingStyle(styleName:String):Boolean;
	
	function registerParentDisplayListInvalidatingStyle(styleName:String):void;
	
	function isParentDisplayListInvalidatingStyle(styleName:String):Boolean;
	
	function registerColorName(colorName:String, colorValue:uint):void;
	
	function isColorName(colorName:String):Boolean;
	
	function getColorName(colorName:Object):uint;
	
	function getColorNames(colors:Array /* of Number or String */):void;
	
	function isValidStyleValue(value:*):Boolean;
    
	function loadStyleDeclarations(
					url:String, update:Boolean = true,
					trustContent:Boolean = false):IEventDispatcher;
    
	function unloadStyleDeclarations(
					url:String, update:Boolean = true):void;

	function initProtoChainRoots():void;
	
	function styleDeclarationsChanged():void;
}

}
