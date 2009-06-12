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
 *  The IRepeaterClient interface defines the APIs for components
 *  that can have multiple instances created by a Repeater.
 *  The IRepeaterClient interface is implemented by the UIComponent class
 *  and so is inherited by all Flex framework controls and containers.
 */
public interface IRepeaterClient
{
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  instanceIndices
    //----------------------------------

    /**
     *  An Array that contains the indices required
     *  to reference the repeated component instance from its document. 
     *  This Array is empty unless the component
     *  is within one or more Repeaters.
     *  The first element corresponds to the outermost Repeater.
     *  For example, if the <code>id</code> is <code>"b"</code>
     *  and <code>instanceIndices</code> is <code>[ 2, 4 ]</code>,
     *  you would reference it on the document as <code>b[2][4]</code>.
     */
    function get instanceIndices():Array;
    
    /**
     *  @private
     */
    function set instanceIndices(value:Array):void;
    
    //----------------------------------
    //  isDocument
    //----------------------------------

    /**
     *  @copy mx.core.UIComponent#isDocument
     */
    function get isDocument():Boolean;

    //----------------------------------
    //  repeaterIndices
    //----------------------------------

    /**
     *  An Array that contains the indices of the items in the data
     *  providers of the Repeaters that produced the component.
     *  The Array is empty unless the component is within one or more
     *  Repeaters.
     *  The first element corresponds to the outermost Repeater component.
     *  For example, if <code>repeaterIndices</code> is <code>[ 2, 4 ]</code>,
     *  the outer Repeater component used its <code>dataProvider[2]</code>
     *  data item and the inner Repeater component used its
     *  <code>dataProvider[4]</code> data item.
     *
     *  <p>This property differs from <code>instanceIndices</code>
     *  if the <code>startingIndex</code> of any of the Repeater components
     *  is non-zero.
     *  For example, even if a Repeater component starts at
     *  <code>dataProvider</code> item 4, the document reference of the first
     *  repeated component is <code>b[0]</code>, not <code>b[4]</code>.</p>
     */
    function get repeaterIndices():Array;
    
    /**
     *  @private
     */
    function set repeaterIndices(value:Array):void;

    //----------------------------------
    //  repeaters
    //----------------------------------

    /**
     *  An Array that contains any enclosing Repeaters of the component.
     *  The Array is empty unless the component is within one or more Repeaters.
     *  The first element corresponds to the outermost Repeater. 
     */
    function get repeaters():Array;
    
    /**
     *  @private
     */
    function set repeaters(value:Array):void;

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Initializes the <code>instanceIndices</code>,
     *  <code>repeaterIndices</code>, and <code>repeaters</code> properties.
     *
     *  <p>This method is called by the Flex framework.
     *  Developers should not need to call it.</p>
     *  
     *  @param parent The parent Repeater that created this component.
     */
    function initializeRepeaterArrays(parent:IRepeaterClient):void;
}

}
