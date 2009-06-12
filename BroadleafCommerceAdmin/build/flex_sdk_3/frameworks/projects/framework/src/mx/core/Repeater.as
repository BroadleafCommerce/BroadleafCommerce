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

package mx.core
{

import flash.display.DisplayObject;
import flash.events.Event;
import mx.collections.ArrayCollection;
import mx.collections.CursorBookmark;
import mx.collections.ICollectionView;
import mx.collections.IList;
import mx.collections.IViewCursor;
import mx.collections.ItemResponder;
import mx.collections.ListCollectionView;
import mx.collections.XMLListCollection;
import mx.collections.errors.ItemPendingError;
import mx.events.CollectionEvent;
import mx.events.CollectionEventKind;
import mx.events.FlexEvent;
import mx.events.PropertyChangeEvent;
import mx.managers.ISystemManager;
import mx.managers.SystemManager;
import mx.automation.IAutomationObject;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched each time an item is processed and the 
 *  <code>currentIndex</code> and <code>currentItem</code> 
 *  properties are updated.
 *
 *  @eventType mx.events.FlexEvent.REPEAT
 */
[Event(name="repeat", type="mx.events.FlexEvent")]

/**
 *  Dispatched after all the subcomponents of a repeater are created.
 *  This event is triggered even if the <code>dataProvider</code>
 *  property is empty or <code>null</code>.
 *
 *  @eventType mx.events.FlexEvent.REPEAT_END
 */
[Event(name="repeatEnd", type="mx.events.FlexEvent")]

/**
 *  Dispatched when Flex begins processing the <code>dataProvider</code>
 *  property and begins creating the specified subcomponents.
 *  This event is triggered even if the <code>dataProvider</code>
 *  property is empty or <code>null</code>.
 *
 *  @eventType mx.events.FlexEvent.REPEAT_START
 */
[Event(name="repeatStart", type="mx.events.FlexEvent")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[IconFile("Repeater.png")]

[ResourceBundle("core")]

/**
 *  The Repeater class is the runtime object that corresponds
 *  to the <code>&lt;mx:Repeater&gt;</code> tag.
 *  It creates multiple instances of its subcomponents
 *  based on its dataProvider.
 *  The repeated components can be any standard or custom
 *  controls or containers.
 *
 *  <p>You can use the <code>&lt;mx:Repeater&gt;</code> tag
 *  anywhere a control or container tag is allowed, with the exception
 *  of the <code>&lt;mx:Application&gt;</code> container tag.
 *  To repeat a user interface component, you place its tag
 *  in the <code>&lt;mx:Repeater&gt;</code> tag.
 *  You can use more than one <code>&lt;mx:Repeater&gt;</code> tag
 *  in an MXML document.
 *  You can also nest <code>&lt;mx:Repeater&gt;</code> tags.</p>
 *
 *  <p>You cannot use the <code>&lt;mx:Repeater&gt;</code> tag
 *  for objects that do not extend the UIComponent class.</p>
 *
 *  @mxml
 *
 *  <p>The &lt;Repeater&gt; class has the following properties:</p>
 *
 *  <pre>
 *  &lt;mx:Repeater
 *    <strong>Properties</strong>
 *    id="<i>No default</i>"
 *    childDescriptors="<i>No default</i>"
 *    count="<i>No default</i>"
 *    dataProvider="<i>No default</i>"
 *    recycleChildren="false|true"
 *    startingIndex="0"
 *
 *    <strong>Events</strong>
 *    repeat="<i>No default</i>"
 *    repeatEnd="<i>No default</i>"
 *    repeatStart="<i>No default</i>"
 *  &gt;
 *  </pre>
 *
 *  @includeExample examples/RepeaterExample.mxml
 *
 *  @see mx.core.Container
 *  @see mx.core.UIComponent
 *  @see mx.core.UIComponentDescriptor
 *  @see flash.events.EventDispatcher
 */
public class Repeater extends UIComponent implements IRepeater
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function Repeater()
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
    private var iterator:IViewCursor;

    /**
     *  @private
     *  Flag indicating whether this Repeater has been fully created.
     *  Used to avoid createComponentFromDescriptor() calling execute().
     */
    private var created:Boolean = false;

    /**
     *  @private
     *  The index of this Repeater's UIComponentDescriptor in the
     *  container's child descriptors.
     */
    private var descriptorIndex:int;

    /**
     *  @private
     *  The Array of components created during the previous execution.
     *  This array is used during recycle().
     * 
     *  mx_internal for automation delegate access
     */
    mx_internal var createdComponents:Array;

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  showInAutomationHierarchy
    //----------------------------------

  	/**
     *  @private
     */
    override public function set showInAutomationHierarchy(value:Boolean):void
    {
    	//do not allow value changes
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  childDescriptors
    //----------------------------------

    /**
     *  An Array of UIComponentDescriptor objects for this Repeater's children.
     */
    public var childDescriptors:Array /* of UIComponentDescriptor */;

    //----------------------------------
    //  container
    //----------------------------------

    /**
     *  @private
     *  Storage for the 'container' read-only property.
     *  Initialized by the constructor.
     */
    private var _container:Container;

    /**
     *  The container that contains this Repeater.
     */
    public function get container():IContainer
    {
        return _container as IContainer;
    }

    //----------------------------------
    //  count
    //----------------------------------

    /**
     *  @private
     *  Storage for the 'count' property.
     */
    private var _count:int = -1;

    [Bindable("countChanged")]
    [Inspectable(category="General", defaultValue="-1")]

    /**
     * @inheritDoc
     */
    public function get count():int
    {
        return _count;
    }

    /**
     *  @private
     */
    public function set count(value:int):void
    {
        _count = value;

        execute();

        dispatchEvent(new Event("countChanged"));
    }

    //----------------------------------
    //  currentIndex
    //----------------------------------

    /**
     *  @private
     *  Storage for the 'currentIndex' property.
     */
    private var _currentIndex:int;

    [Bindable("nextRepeaterItem")]
    [Inspectable(category="General", defaultValue="-1")]

    /**
     * @inheritDoc
     */
    public function get currentIndex():int
    {
        if (_currentIndex == -1)
        {
			var message:String = resourceManager.getString(
				"core", "notExecuting");
            throw new Error(message);
        }

        return _currentIndex;
    }

    //----------------------------------
    //  currentItem
    //----------------------------------

    [Bindable("nextRepeaterItem")]
    [Inspectable(category="General", defaultValue="null")]

    /**
     * @inheritDoc
     */
    public function get currentItem():Object
    {
        if (_currentIndex == -1)
        {
			var message:String = resourceManager.getString(
				"core", "notExecuting");
            throw new Error(message);
        }

        var result:Object;

        if (iterator)
        {
            try
            {
                iterator.seek(CursorBookmark.FIRST, _currentIndex);
                result = iterator.current;
            }
            catch(itemPendingError:ItemPendingError)
            {
                itemPendingError.addResponder(new ItemResponder(responderResultHandler, responderFaultHandler));
            }
        }

        return result;
    }

    //----------------------------------
    //  dataProvider
    //----------------------------------

    /**
     *  @private
     *  Storage for the 'dataProvider' property.
     */
    private var collection:ICollectionView;

    [Bindable("collectionChange")]
    [Inspectable(category="General", defaultValue="null")]

    /**
     * @inheritDoc
     */
    public function get dataProvider():Object
    {
        return collection;
    }

    /**
     *  @private
     */
    public function set dataProvider(value:Object):void
    {
        var hadValue:Boolean = false;

        if (collection)
        {
            hadValue = true;
            collection.removeEventListener(CollectionEvent.COLLECTION_CHANGE, collectionChangedHandler);
            collection = null;
            iterator = null;
        }

        if (value is Array)
        {
            collection = new ArrayCollection(value as Array);
        }
        else if (value is ICollectionView)
        {
            collection = ICollectionView(value);
        }
        else if (value is IList)
        {
            collection = new ListCollectionView(IList(value));
        }
        else if (value is XMLList)
        {
            collection = new XMLListCollection(value as XMLList);
        }
        else if (value is XML)
        {
            var xl:XMLList = new XMLList();
            xl += value;
            collection = new XMLListCollection(xl);
        }
        else if (value != null)
        {
            // convert it to an array containing this one item
            var tmp:Array = [value];
            collection = new ArrayCollection(tmp);
        }

        if (collection)
        {
            // weak reference
            collection.addEventListener(CollectionEvent.COLLECTION_CHANGE, collectionChangedHandler, false, 0, true);
            iterator = collection.createCursor();
        }

        dispatchEvent(new Event("collectionChange"));

        if (collection || hadValue)
        {
            execute();
        }
    }

    //----------------------------------
    //  numCreatedChildren
    //----------------------------------

    /**
     *  @private
     */
    private function get numCreatedChildren():int
    {
        var result:int = 0;

        for (var i:int = 0; i < createdComponents.length; i++)
        {
            var component:IFlexDisplayObject = createdComponents[i];
            if (component is Repeater)
            {
                var repeater:Repeater = Repeater(component);
                result += repeater.numCreatedChildren;
            }
            else
            {
                result += 1;
            }
        }

        return result;
    }

    //----------------------------------
    //  recycleChildren
    //----------------------------------

    /**
     *  @private
     *  Storage for the recycleChildren property.
     */
    private var _recycleChildren:Boolean = false;

    [Inspectable(category="Other", defaultValue="false")]

    /**
     * @inheritDoc
     */
    public function get recycleChildren():Boolean
    {
        return _recycleChildren;
    }

    /**
     *  @private
     */
    public function set recycleChildren(value:Boolean):void
    {
        _recycleChildren = value;
    }

    //----------------------------------
    //  startingIndex
    //----------------------------------

    /**
     *  @private
     *  Storage for the 'startingIndex' property.
     */
    private var _startingIndex:int = 0;

    [Bindable("startingIndexChanged")]
    [Inspectable(category="General", defaultValue="0")]

    /**
     * @inheritDoc
     */
    public function get startingIndex():int
    {
        return _startingIndex;
    }

    /**
     *  @private
     */
    public function set startingIndex(value:int):void
    {
        _startingIndex = value;

        execute();

        dispatchEvent(new Event("startingIndexChanged"));
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function toString():String
    {
        return Object(container).toString() + "." + super.toString();
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     * @inheritDoc
     */
    public function initializeRepeater(container:IContainer,
                                       recurse:Boolean):void
    {
        _container = Container(container);
        descriptorIndex = container.numChildren;
        created = true;

        // Create instances of the components contained within
        // this Repeater
        if (collection)
        {
            createComponentsFromDescriptors(recurse);
        }
        
        if (owner == null)
        	owner = Container(container);
    }

    /**
     * @inheritDoc
     */
    public function executeChildBindings():void
    {
        var n:int = container.numChildren;
        for (var i:int = 0; i < n; i++)
        {
            var child:IRepeaterClient = IRepeaterClient(container.getChildAt(i));

            if (hasDescendant(child) && child is IDeferredInstantiationUIComponent)
                IDeferredInstantiationUIComponent(child).executeBindings();
        }
    }

    /**
     *  @private
     *  Used by data binding code generation.
     */
    mx_internal function getItemAt(index:int):Object
    {
        var result:Object;

        if (iterator)
        {
            try
            {
                iterator.seek(CursorBookmark.FIRST, index);
                result = iterator.current;
            }
            catch(itemPendingError:ItemPendingError)
            {
                itemPendingError.addResponder(
                    new ItemResponder(responderResultHandler, responderFaultHandler));
            }
        }

        return result;
    }

    /**
     *  @private
     */
    private function responderResultHandler(data:Object, info:Object):void
    {
        execute();
    }

    /**
     *  @private
     */
    private function responderFaultHandler(data:Object, info:Object):void
    {
    }

    /**
     *  @private
     *  Determines whether the specified UIComponent or Repeater
     *  is inside this Repeater, by searching its 'repeaters' array.
     *
     *  The methods removeAllChildren() and removeAllChildRepeaters()
     *  use this to determine which child UIComponents and child Repeaters
     *  of this Repeater's container should be removed, since some of
     *  the container's children may be outside this Repeater.
     */
    private function hasDescendant(o:Object):Boolean
    {
        var repeaters:Array = o.repeaters;
        if (repeaters == null)
            return false;

        var n:int = repeaters.length;
        for (var i:int = 0; i < n; i++)
        {
            if (repeaters[i] == this)
                return true;
        }
        return false;
    }

    /**
     *  @private
     *  Removes all the child UIComponents in this Repeater's container
     *  which belong to this Repeater. Returns the lowest index of the
     *  removed children, or null if none were removed.
     *
     *  The method execute() uses this to clean up previously created
     *  child UIComponents when this Repeater's dataProvider, startingIndex,
     *  or count changes.
     */
    private function removeAllChildren(container:IContainer):void
    {
        // It's simpler and more efficient
        // to loop backwards when removing children.
        var n:int = container.numChildren;
        for (var i:int = n - 1; i >= 0; i--)
        {
            var child:IRepeaterClient = IRepeaterClient(container.getChildAt(i));

            if (hasDescendant(child))
            {
                if (child is Container)
                {
                    removeAllChildren(Container(child));
                    removeAllChildRepeaters(Container(child));
                }

                container.removeChildAt(i);

                if (child is IDeferredInstantiationUIComponent)
                {
                    IDeferredInstantiationUIComponent(child).
                        deleteReferenceOnParentDocument(
                            IFlexDisplayObject(parentDocument));
                }
            }
        }
    }

    /**
     *  @private
     *  Removes all the child Repeaters in this Repeater's container
     *  which belong to this Repeater.
     *
     *  The method execute() uses this to clean up previously created
     *  child Repeaters when this Repeater's dataProvider, startingIndex,
     *  or count changes.
     */
    private function removeAllChildRepeaters(container:Container):void
    {
        // It's simpler and more efficient
        // to loop backwards when removing repeaters.
        if (container.childRepeaters)
        {
            var n:int = container.childRepeaters.length;
            for (var i:int = n - 1; i >= 0; i--)
            {
                var repeater:Repeater = container.childRepeaters[i];
                if (hasDescendant(repeater))
                {
                    removeRepeater(repeater);
                }
            }
        }
    }

    /**
     *  @private
     */
    private function removeChildRepeater(container:Container, repeater:Repeater):void
    {
        var i:int = 0;
        var n:int = container.childRepeaters.length;

        while (i < n)
        {
            if (container.repeaters[i] == repeater)
            {
                container.repeaters.splice(i, 1);
                break;
            }
            i++;
        }
    }

    /**
     *  @private
     */
    private function createComponentFromDescriptor(
                                    instanceIndex:int,
                                    descriptorIndex:int,
                                    recurse:Boolean):IFlexDisplayObject
    {
        var descriptor:UIComponentDescriptor = childDescriptors[descriptorIndex];

        if (!descriptor.document)
        {
            descriptor.document = document;
        }

        // Ensure that the newly created component gets the proper
        // 'instanceIndices', 'repeaters' and 'repeaterIndices'
        // properties.
        descriptor.instanceIndices = instanceIndices ? instanceIndices : [];
        descriptor.instanceIndices.push(instanceIndex)
        descriptor.repeaters = repeaters;
        descriptor.repeaters.push(this);
        descriptor.repeaterIndices = repeaterIndices;
        descriptor.repeaterIndices.push(startingIndex + instanceIndex);

        // Do not reuse the descriptor's properties, because we don't want repeated items
        // to share non scalar properties.  Otherwise they will have references to the
        // same objects as opposed to having their own instances.
        descriptor.invalidateProperties();

        // Create object described by the descriptor (which might be
        // either a UIComponent or a nested Repeater).
        var child:IFlexDisplayObject = Container(container).createComponentFromDescriptor(
                                                descriptor, recurse);

        // get rid of the repeater-specific properties on the
        // descriptor now that createComponent is done
        descriptor.instanceIndices = null;
        descriptor.repeaters = null;
        descriptor.repeaterIndices = null;

        // Execute any binding that is watching the current item.
        dispatchEvent(new Event("nextRepeaterItem"));

        return child;
    }

    /**
     *  @private
     *  Create repeated instances of the UIComponents and Repeaters
     *  that are owned by this Repeater, based on its dataProvider.
     *
     *  This method is the heart of the Repeater class. It is called
     *  when this repeater is created by createComponentFromDescriptor(),
     *  and when this Repeater gets re-executed when its dataProvider,
     *  startingIndex, or count changes.
     */
    private function createComponentsFromDescriptors(recurse:Boolean):void
    {
        dispatchEvent(new FlexEvent(FlexEvent.REPEAT_START));

        createdComponents = [];

        if (collection && (collection.length > 0) && (collection.length - startingIndex > 0))
        {
            var n:int = positiveMin(collection.length - startingIndex, count);

            // Loop over the items of the dataProvider.
            for (var i:int = 0; i < n; i++)
            {
                // When a repeater is repeating, 'currentIndex' and 'currentItem'
                // can be read.
                _currentIndex = startingIndex + i;

                // Dispatch a "repeat" event.
                dispatchEvent(new FlexEvent(FlexEvent.REPEAT));

                if (childDescriptors && (childDescriptors.length > 0))
                {
                    var m:int = childDescriptors.length;

                    // Loop over the child descriptors of the repeater.
                    for (var j:int = 0; j < m; j++)
                    {
                        var component:IFlexDisplayObject =
                            createComponentFromDescriptor(i, j, recurse);

                        createdComponents.push(component);

                        if (component is IUIComponent)
                        	 IUIComponent(component).owner = this;

                        if (component is IAutomationObject)
                        	 IAutomationObject(component).showInAutomationHierarchy = true;
                    }
                }
            }

            // When a repeater isn't repeating, 'currentIndex' is -1
            // and 'currentItem' is null.
            _currentIndex = -1;
        }

        dispatchEvent(new FlexEvent(FlexEvent.REPEAT_END));
    }

    /**
     *  @private
     *  This method is used by execute() to determine where the newly-created
     *  UIComponents should be inserted in the container.
     */
    private function getIndexForFirstChild():int
    {
        var locationInfo:LocationInfo = new LocationInfo();
        var i:int = 0;
        var cc:Array = Container(container).createdComponents;
        var length:int = cc ? cc.length : 0;

        while (i < length)
        {
            var component:IFlexDisplayObject = Container(container).createdComponents[i];

            if (component == this)
            {
                locationInfo.found = true;
                break;
            }
            else if (component is Repeater)
            {
                var repeater:Repeater = Repeater(component);
                repeater.getIndexForRepeater(this, locationInfo);

                if (locationInfo.found)
                {
                    break;
                }
            }
            else
            {
                locationInfo.index += 1;
            }

            i++;
        }

        return locationInfo.found ? locationInfo.index : container.numChildren;
    }

    /**
     *  @private
     */
    private function getIndexForRepeater(target:Repeater,
                                         locationInfo:LocationInfo):void
    {
        var i:int = 0;
        var length:int = createdComponents.length;

        while (i < length)
        {
            var component:IFlexDisplayObject = createdComponents[i];

            if (component == target)
            {
                locationInfo.found = true;
                break;
            }
            else if (component is Repeater)
            {
                var repeater:Repeater = Repeater(component);
                repeater.getIndexForRepeater(target, locationInfo);

                if (locationInfo.found)
                    break;
            }
            else
            {
                locationInfo.index += 1;
            }

            i++;
        }
    }

    /**
     *  @private
     *  This method is used by execute() to move the newly-created UIComponents,
     *  which get created at the end of the container, to their proper indexes
     *  in the container.
     */
    private function reindexDescendants(from:int, to:int):void
    {
        // Determine how many children were created by
        // createComponentsFromDescriptors(), which includes the
        // children created by nested Repeaters in this container.
        var n:int = container.numChildren - from;

        // Set the child index of each one.
        for (var i:int = 0; i < n; i++)
        {
            var child:IRepeaterClient =
                IRepeaterClient(container.getChildAt(from + i));

            container.setChildIndex(DisplayObject(child), to + i);
        }
    }

    /**
     *  @private
     */
    private function resetRepeaterIndices(o:IRepeaterClient, index:int):void
    {
        var repeaterIndices:Array = o.repeaterIndices;
        repeaterIndices[repeaterIndices.length - 1] = index;
        o.repeaterIndices = repeaterIndices;

        if (o is Container)
        {
            var fc:Container = Container(o);

            var n:int = fc.numChildren;
            for (var i:int = 0; i < n; i++)
            {
                var child:IRepeaterClient = IRepeaterClient(fc.getChildAt(i));

                resetRepeaterIndices(child, index);
            }
        }
    }

    /**
     *  @private
     */
    private function recycle():void
    {
        dispatchEvent(new FlexEvent(FlexEvent.REPEAT_START));

        var n:int = 0;
        var i:int;
        var m:int;
        var j:int;

        var numComponents:int = 0;

        if (collection && (collection.length > 0) && (collection.length - startingIndex > 0))
        {
            n = positiveMin(collection.length - startingIndex, count);

            var previousChildIndex:int = 0;

            // Loop over the items of the dataProvider.
            for (i = 0; i < n; i++)
            {
                // When a repeater is repeating, 'currentIndex' and 'currentItem'
                // can be read.
                _currentIndex = startingIndex + i;

                // Dispatch a "repeat" event.
                dispatchEvent(new FlexEvent(FlexEvent.REPEAT));

                if (childDescriptors)
                {
                    m = childDescriptors.length;
                    var repeater:Repeater;

                    if (createdComponents.length >= ((i + 1) * m))
                    {
                        // Loop over the components previously created by this Repeater
                        for (j = 0; j < m; j++)
                        {
                            var createdComponent:IRepeaterClient = createdComponents[(i * m) + j];

                            if (createdComponent is Repeater)
                            {
                                repeater = Repeater(createdComponent);
                                resetRepeaterIndices(repeater, _currentIndex);
				                repeater.owner = this;
                                repeater.execute();
                            }
                            else
                            {
                                resetRepeaterIndices(createdComponent, _currentIndex);
                                if (createdComponent is IDeferredInstantiationUIComponent)
                                    IDeferredInstantiationUIComponent(createdComponent).executeBindings(true);
                            }
                            numComponents++;
                        }
                    }
                    else
                    {
                        for (j = 0; j < m; j++)
                        {
                            var from:int = container.numChildren;
                            var to:int = getIndexForFirstChild() + numCreatedChildren;

                            var component:IRepeaterClient =
                                IRepeaterClient(createComponentFromDescriptor(i, j, true));

                            createdComponents.push(component);

                            if (component is IUIComponent)
                                IUIComponent(component).owner = this;
                            
                            if (component is IAutomationObject)
                                IAutomationObject(component).showInAutomationHierarchy = true;

                            if (component is Repeater)
                            {
                                repeater = Repeater(component);
                                repeater.reindexDescendants(from, to);
                            }
                            else
                            {
                                container.setChildIndex(DisplayObject(component), to);
                            }
                            numComponents++;
                        }
                    }
                }
            }
        }

        // When a repeater isn't repeating, 'currentIndex' is -1 and
        // 'currentItem' is null.
        _currentIndex = -1;

        // Remove any extra children created the last time that we no
        // longer need.
        for (i = createdComponents.length - 1; i >= numComponents; i--)
        {
            var extra:IRepeaterClient = createdComponents.pop();

            if (extra is Repeater)
            {
                removeRepeater(Repeater(extra));
            }
            else if (extra)
            {
                if (extra is Container)
                {
                    removeAllChildren(Container(extra));
                    removeAllChildRepeaters(Container(extra));
                }

                if (container.contains(DisplayObject(extra)))
                    container.removeChild(DisplayObject(extra));

                if (extra is IDeferredInstantiationUIComponent)
                {
                    IDeferredInstantiationUIComponent(extra).
                        deleteReferenceOnParentDocument(
                            IFlexDisplayObject(parentDocument));
                }
            }
        }

        dispatchEvent(new FlexEvent(FlexEvent.REPEAT_END));
    }

    /**
     *  @private
     */
    private function recreate():void
    {
        // Clean out what this Repeater created last time.
        removeAllChildren(container);
        removeAllChildRepeaters(Container(container));

        // Determine how to re-index the new children.
        var from:int = container.numChildren;
        var to:int = getIndexForFirstChild();

        // Create new child UIComponents and child Repeaters.
        // The child UIComponents get added at the "end"
        // of the container by addChild().
        createComponentsFromDescriptors(true);

        // Re-index them to where they belong.
        if (from != to)
            reindexDescendants(from, to);
    }

    /**
     *  @private
     *  Execute this Repeater a second time, after its dataProvider,
     *  startingIndex, or count changes.
     */
    private function execute():void
    {
        if (!created)
            return;

        if (recycleChildren && createdComponents && (createdComponents.length > 0))
            recycle();
        else
            recreate();
    }

    /**
     *  @private
     *  Handles "Change" event sent by calls to Collection APIs
     *  on this Repeater's dataProvider.
     */
    private function collectionChangedHandler(collectionEvent:CollectionEvent):void
    {
        switch (collectionEvent.kind)
        {
            case CollectionEventKind.UPDATE:
            {
                break;
            }

            default:
            {
                execute();
            }
        }
    }

    /**
     *  @private
     */
    private function addItems(firstIndex:int, lastIndex:int):void
    {
        if (startingIndex > lastIndex)
            return;

        var i:int;

        // Determine the child index at which the newly created
        // components will be inserted.
        var index:int = -1;
        var n:int = container.numChildren;
        var child:IRepeaterClient;
        var repeaterIndex:int;

        // If the data items have just been appended to the dataProvider,
        // loop over the children of the container in reverse order,
        // looking for the first child that was associated with this
        // repeater. We'll insert the newly created components after
        // that child.
        if (lastIndex == (collection.length - 1))
        {
            for (i = n - 1; i >= 0; i--)
            {
                child = IRepeaterClient(container.getChildAt(i));
                repeaterIndex = getRepeaterIndex(child);

                if (repeaterIndex != -1)
                {
                    index = i + 1;
                    break;
                }
            }
        }

        // If the data items have just been inserted before the end
        // of the dataProvider, loop over the the children of the container,
        // looking for the first one in the specified range.
        // For example, if items 3 through 5 were just inserted,
        // look for the first child that previously was
        // associated with item 3, 4, or 5.
        else
        {
            var numItemsAdded:int = (lastIndex - firstIndex) + 1;
            for (i = 0; i < n; i++)
            {
                child = IRepeaterClient(container.getChildAt(i));
                repeaterIndex = getRepeaterIndex(child);

                if (repeaterIndex != -1)
                {
                    if (firstIndex <= repeaterIndex &&
                        repeaterIndex <= lastIndex &&
                        index == -1)
                    {
                        index = i;
                    }

                    if (repeaterIndex >= firstIndex)
                        adjustIndices(child, numItemsAdded);
                }
            }
        }

        // Create components for the new dataProvider items
        if (count == -1)
        {
            n = lastIndex;
        }
        else
        {
            n = positiveMin(startingIndex + count - 1, lastIndex);
        }

        for (i = Math.max(startingIndex, firstIndex); i <= n; i++)
        {
            var m:int = childDescriptors.length;

            _currentIndex = i;
            dispatchEvent(new FlexEvent(FlexEvent.REPEAT));

            for (var j:int = 0; j < m; j++)
            {
                var from:int = container.numChildren;
                var to:int = getIndexForFirstChild() + numCreatedChildren;
                var component:IFlexDisplayObject =
                    createComponentFromDescriptor(i - startingIndex, j, true);

                createdComponents.push(component);

                if (component is IUIComponent)
                    IUIComponent(component).owner = this;

                if (component is IAutomationObject)
                	 IAutomationObject(component).showInAutomationHierarchy = true;
                
                // The newly created components are at the end of the container.
                // Move them to the indexes where they belong.
                if (component is Repeater)
                {
                    var r:Repeater = Repeater(component);
					r.owner = this;
                    r.reindexDescendants(from, to);
                }
                else
                {
                    container.setChildIndex(DisplayObject(component), to);
                }
            }
        }

        _currentIndex = -1;
    }

    /**
     *  @private
     */
    private function removeItems(firstIndex:int, lastIndex:int):void
    {
        if (createdComponents && (createdComponents.length > 0))
        {
            for (var i:int = createdComponents.length - 1; i >= 0; i--)
            {
                var component:IRepeaterClient = createdComponents[i];

                var repeaterIndex:int = getRepeaterIndex(component);

                if (((firstIndex <= repeaterIndex) &&
                    ((repeaterIndex < lastIndex) || (lastIndex == -1))) ||
                    (repeaterIndex >= dataProvider.length))
                {
                    if (component is Repeater)
                    {
                        var repeater:Repeater = Repeater(component);
                        removeRepeater(repeater);
                    }
                    else if (container.contains(DisplayObject(component)))
                    {
                        container.removeChild(DisplayObject(component));
                    }

                    if (component is IDeferredInstantiationUIComponent)
                    {
                        IDeferredInstantiationUIComponent(component).
                            deleteReferenceOnParentDocument(
                                IFlexDisplayObject(parentDocument));
                    }

                    createdComponents.splice(i, 1);
                }
                else if ((firstIndex <= repeaterIndex) && (lastIndex != -1) &&
                         (repeaterIndex >= lastIndex))
                {
                    adjustIndices(component, (lastIndex - firstIndex) + 1);

                    if (component is IDeferredInstantiationUIComponent)
                    {
                        IDeferredInstantiationUIComponent(component).
                            executeBindings(true);
                    }
                }
            }
        }
    }

    /**
     *  @private
     */
    private function removeRepeater(repeater:Repeater):void
    {
        repeater.removeAllChildren(repeater.container);

        repeater.removeAllChildRepeaters(Container(repeater.container));

        removeChildRepeater(Container(container), repeater);

        repeater.deleteReferenceOnParentDocument(
            IFlexDisplayObject(parentDocument));

        // Null out the dataProvider, so that listeners will be removed.
        repeater.dataProvider = null;
    }

    /**
     *  @private
     */
    private function updateItems(firstIndex:int, lastIndex:int):void
    {
        if (recycleChildren)
        {
            var n:int = container.numChildren;
            for (var i:int = 0; i < n; i++)
            {
                var child:IRepeaterClient = IRepeaterClient(container.getChildAt(i));
                var repeaterIndex:int = getRepeaterIndex(child);

                if (repeaterIndex != -1 &&
                    firstIndex <= repeaterIndex &&
                    repeaterIndex <= lastIndex)
                {
                    if (child is IDeferredInstantiationUIComponent)
                        IDeferredInstantiationUIComponent(child).executeBindings(true);
                }
            }
        }
        else
        {
            removeItems(firstIndex, lastIndex);
            addItems(firstIndex, lastIndex);
        }
    }

    /**
     *  @private
     */
    private function sort():void
    {
        execute();
    }

    /**
     *  @private
     */
    private function getRepeaterIndex(o:IRepeaterClient):int
    {
        var repeaters:Array = o.repeaters;
        if (repeaters == null)
            return -1;

        var n:int = repeaters.length;
        for (var i:int = 0; i < n; i++)
        {
            if (repeaters[i] == this)
                return o.repeaterIndices[i];
        }

        return -1;
    }

    /**
     *  @private
     */
    private function adjustIndices(o:IRepeaterClient, adjustment:int):void
    {
        var repeaters:Array = o.repeaters;
        if (repeaters == null)
            return;

        var n:int = repeaters.length;
        for (var i:int = 0; i < n; i++)
        {
            if (repeaters[i] == this)
            {
                o.repeaterIndices[i] += adjustment;
                o.instanceIndices[i] += adjustment;
                break;
            }
        }
    }

    /**
     *  @private
     *  This function is like Math.min(),
     *  but if x is negative, it is ignored.
     *  If y is negative, it is ignored.
     *  If both are negative, zero is returned.
     */
    private function positiveMin(x:int, y:int):int
    {
        var result:int = 0;

        if (x >= 0)
        {
            if (y >= 0)
            {
                if (x < y)
                    result = x;
                else
                    result = y;
            }
            else
            {
                result = x;
            }
        }
        else
        {
            result = y;
        }

        return result;
    }

}

}

////////////////////////////////////////////////////////////////////////////////

/**
 *  @private
 */
class LocationInfo
{
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    public function LocationInfo()
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
    public var found:Boolean = false;

    /**
     *  @private
     */
    public var index:int = 0;
}
