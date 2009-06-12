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

package mx.core
{

/**
 *  The IRepeater interface defines the APIs for Repeater
 *  public APIs of the Repeater object.
 */
public interface IRepeater
{
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  container
	//----------------------------------

    /**
     *  The container that contains this Repeater,
	 *  and in which it will create its children.
     */
    function get container():IContainer;

	//----------------------------------
	//  count
	//----------------------------------

    /**
     *  The number of times this Repeater should execute.
	 *
     *  <p>If the Repeater reaches the end of the data provider while
	 *  executing, the number of times it actually executes will be less
	 *  that the requested count.</p>
     */
    function get count():int;
    
	/**
	 *  @private
	 */
    function set count(value:int):void;

	//----------------------------------
	//  currentIndex
	//----------------------------------

    /**
     *  The index of the item in the <code>dataProvider</code> currently
	 *  being processed while this Repeater is executing.
	 *
	 *  <p>After the Repeater has finished executing,
	 *  this property is <code>-1</code>.
	 *  However, the <code>repeaterIndex</code> property of a repeated
	 *  component instance remembers the index of the
	 *  <code>dataProvider</code> item from which it was created.
	 *  In the case of nested Repeaters, you can use the
	 *  <code>repeaterIndices</code> array.</p>
	 *
	 *  @see mx.core.UIComponent#repeaterIndex
	 *  @see mx.core.UIComponent#repeaterIndices
	 *  @see mx.core.UIComponent#instanceIndex
	 *  @see mx.core.UIComponent#instanceIndices
     */
    function get currentIndex():int;

	//----------------------------------
	//  currentItem
	//----------------------------------

    /**
     *  The item in the <code>dataProvider</code> currently being processed
	 *  while this Repeater is executing.
	 *
	 *  <p>After the Repeater has finished executing,
	 *  this property is <code>null</code>.
	 *  However, in this case you can call the <code>getRepeaterItem()</code>
	 *  method of the repeated component instance to get the
	 *  <code>dataProvider</code> item from which it was created.</p>
	 *
	 *  @see mx.core.UIComponent#getRepeaterItem()
     */
    function get currentItem():Object;

	//----------------------------------
	//  dataProvider
	//----------------------------------

    /**
     *  The data provider used by this Repeater to create repeated instances
	 *  of its children.
	 *  
	 *  <p>If you read the <code>dataProvider</code> property, you always get
	 *  an ICollectionView object or <code>null</code>.
	 *  If you set the <code>dataProvider</code> property to anything other than
	 *  <code>null</code>, it is converted into an ICollectionView object,
	 *  according the following rules:</p>
	 *
	 *  <ul>
	 *    <li>If you set it to an Array, it is converted into an ArrayCollection.</li>
	 *    <li>If you set it to an ICollectionView, no conversion is performed.</li>
	 *    <li>If you set it to an IList, it is converted into a ListCollectionView.</li>
	 *    <li>If you set it to an XML or XMLList, it is converted
	 *      into an XMLListCollection.</li>
	 *    <li>Otherwise, it is converted to a single-element ArrayCollection.</li>
	 *  </ul>
     *
     *  <p>You must specify a value for the <code>dataProvider</code> property 
     *  or the Repeater component will not execute.</p>
     */
    function get dataProvider():Object;
    
	/**
	 *  @private
	 */
    function set dataProvider(value:Object):void;

	//----------------------------------
	//  recycleChildren
	//----------------------------------

    /**
     *  A Boolean flag indicating whether this Repeater should re-use
	 *  previously created children, or create new ones.
	 *
	 *  <p>If <code>true</code>, when this Repeater's
	 *  <code>dataProvider</code>, <code>startingIndex</code>,
	 *  or <code>count</code> changes, it will recycle the existing
	 *  children by binding the new data into them.
	 *  If more children are required, they are created and added.
	 *  If fewer children are required, the extra ones are removed
	 *  and garbage collected.</p>
     *
     *  <p>If <code>false</code>, when this Repeater's 
	 *  <code>dataProvider</code>, <code>startingIndex</code>,
	 *  or <code>count</code> changes, it will remove any previous
	 *  children that it created and then create and 
     *  add new children from the new data items.</p>
	 *
	 *  <p>This property is <code>false</code> by default.
	 *  Setting it to <code>true</code> can increase performance,
	 *  but is not appropriate in all situations.
	 *  For example, if the previously created children have state
	 *  information such as text typed in by a user, then this
	 *  state will not get reset when the children are recycled.</p>
     */
    function get recycleChildren():Boolean;
    
	/**
	 *  @private
	 */
	function set recycleChildren(value:Boolean):void;

	//----------------------------------
	//  startingIndex
	//----------------------------------

    /**
     *  The index into the <code>dataProvider</code> at which this Repeater
     *  starts creating children.
     */
    function get startingIndex():int;
    
	/**
	 *  @private
	 */
    function set startingIndex(value:int):void;

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

    /**
     *  Initializes a new Repeater object.
	 *
	 *  <p>This method is called by the Flex framework.
	 *  Developers should not need to call it.</p>
	 *
     *  @param container The Container that contains this Repeater,
	 *  and in which this Repeater will create its children.
	 *
     *  @param recurse A Boolean flag indicating whether this Repeater
	 *  should create all descendants of its children.
     */
    function initializeRepeater(container:IContainer, recurse:Boolean):void;
        
    /**
     *  Executes the bindings into all the UIComponents created
	 *  by this Repeater.
	 *
	 *  <p>This method is called by the Flex framework.
	 *  Developers should not need to call it.</p>
     */
    function executeChildBindings():void;
}

}
