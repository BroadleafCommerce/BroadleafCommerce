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

package mx.core
{

import mx.managers.IFocusManager;

/**
 *  IContainer is a interface that indicates a component
 *  extends or mimics mx.core.Container
 *
 *  @see mx.core.Container
 */
public interface IContainer extends IUIComponent
{

include "ISpriteInterface.as"
include "IDisplayObjectContainerInterface.as"
include "IInteractiveObjectInterface.as"

    /**
     *  @copy mx.core.Container#defaultButton
     */
    function get defaultButton():IFlexDisplayObject;
    function set defaultButton(value:IFlexDisplayObject):void;

    /**
     *  @copy mx.core.Container#creatingContentPane
     */
    function get creatingContentPane():Boolean;
    function set creatingContentPane(value:Boolean):void;

    /**
     *  @copy mx.core.Container#viewMetrics
     */
    function get viewMetrics():EdgeMetrics;

    /**
     *  @copy mx.core.Container#horizontalScrollPosition
     */
    function get horizontalScrollPosition():Number;
    function set horizontalScrollPosition(value:Number):void;

    /**
     *  @copy mx.core.Container#verticalScrollPosition
     */
    function get verticalScrollPosition():Number;
    function set verticalScrollPosition(value:Number):void;

    /**
     *  @copy mx.core.UIComponent#focusManager
     */
    function get focusManager():IFocusManager;
}

}
