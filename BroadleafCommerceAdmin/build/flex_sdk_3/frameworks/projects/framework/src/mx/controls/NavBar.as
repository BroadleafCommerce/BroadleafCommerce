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

package mx.controls
{

import flash.display.DisplayObject;
import flash.display.InteractiveObject;
import flash.events.Event;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import mx.collections.ArrayCollection;
import mx.collections.IList;
import mx.containers.Box;
import mx.containers.BoxDirection;
import mx.containers.ViewStack;
import mx.core.ClassFactory;
import mx.core.Container;
import mx.core.FlexVersion;
import mx.core.IFactory;
import mx.core.IFlexDisplayObject;
import mx.core.ScrollPolicy;
import mx.core.UIComponent;
import mx.core.mx_internal;
import mx.events.ChildExistenceChangedEvent;
import mx.events.CollectionEvent;
import mx.events.FlexEvent;
import mx.events.IndexChangedEvent;
import mx.events.ItemClickEvent;
import mx.managers.ISystemManager;
import mx.managers.SystemManager;
import mx.logging.AbstractTarget;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when a navigation item is selected.
 *
 *  @eventType mx.events.ItemClickEvent.ITEM_CLICK
 */
[Event(name="itemClick", type="mx.events.ItemClickEvent")]

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="defaultButton", kind="property")]
[Exclude(name="horizontalLineScrollSize", kind="property")]
[Exclude(name="horizontalPageScrollSize", kind="property")]
[Exclude(name="horizontalScrollBar", kind="property")]
[Exclude(name="horizontalScrollPolicy", kind="property")]
[Exclude(name="horizontalScrollPosition", kind="property")]
[Exclude(name="icon", kind="property")]
[Exclude(name="label", kind="property")]
[Exclude(name="maxHorizontalScrollPosition", kind="property")]
[Exclude(name="maxVerticalScrollPosition", kind="property")]
[Exclude(name="verticalLineScrollSize", kind="property")]
[Exclude(name="verticalPageScrollSize", kind="property")]
[Exclude(name="verticalScrollBar", kind="property")]
[Exclude(name="verticalScrollPolicy", kind="property")]
[Exclude(name="verticalScrollPosition", kind="property")]

[Exclude(name="scroll", kind="event")]
[Exclude(name="click", kind="event")]

[Exclude(name="horizontalScrollBarStyleName", kind="style")]
[Exclude(name="verticalScrollBarStyleName", kind="style")]

[ResourceBundle("controls")]

/**
 *  The NavBar control is the superclass for navigator controls, such as the
 *  LinkBar and TabBar controls, and cannot be instantiated directly.
 *
 *  @mxml
 *
 *  <p>The <code><mx:NavBar></code> tag inherits all of the tag attributes
 *  of its superclass, with the exception of scrolling-related
 *  attributes, and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:<i>tagname</i>
 *    <strong>Properties</strong>
 *    dataProvider="<i>No default</i>"
 *    iconField="icon"
 *    labeField="label"
 *    selectedIndex="-1"
 *    toolTipField="toolTip"
 *     
 *    <strong>Events</strong>
 *    itemClick="<i>No default</i>"
 *    &gt;
 *     ...
 *       <i>child tags</i>
 *     ...
 *    &lt;/mx:<i>tagname</i>&gt;
 *  </pre>
 *
 *  @see mx.collections.IList
 *  @see mx.containers.ViewStack
 *  @see mx.controls.LinkBar
 *  @see mx.controls.TabBar
 *  @see mx.controls.ButtonBar
 *  
 */
public class NavBar extends Box
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
    public function NavBar()
    {
        super();

        direction = BoxDirection.HORIZONTAL;
		showInAutomationHierarchy = true;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  The target ViewStack.
     *  If this is null, the dataProvider is an Array.
     */
    mx_internal var targetStack:ViewStack;

    /**
     *  @private
     *  This variable is set when the NavBar is still initializing and the
     *  dataProvider is set to a ViewStack (or a String that might identify
     *  a ViewStack in the parent document).
     */
    private var pendingTargetStack:Object;

    /**
     *  @private
     *  The factory that generates the instances of the navigation items.
     *  It generates instances of ButtonBarButton for ButtonBar and
     *  ToggleButtonBar, of LinkButton for LinkBar, and of Tab for TabBar.
     *  This var is expected to be set before the navigation items are created;
     *  setting it does not re-create existing navigation items based on the
     *  new factory.
     */
    mx_internal var navItemFactory:IFactory = new ClassFactory(Button);

 	/**
     *  @private
     *  allows us to null out child containers' toolTips without descending
     *  into recursive madness.
     */
	private var lastToolTip:String = null;

    /**
     *  @private
     */
    private var measurementHasBeenCalled:Boolean = false;

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  baselinePosition
    //----------------------------------

    /**
     *  @private
     *  The baselinePosition of a NavBar is calculated
	 *  for the label of the first nav item.
 	 *  If there are no nav items, the NavBar doesn't appear
	 *  and the baselinePosition is irrelevant.
     */
    override public function get baselinePosition():Number
    {
		if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
			return super.baselinePosition;
				    
		if (!validateBaselinePosition())
			return NaN;

		if (numChildren == 0)
			return super.baselinePosition;

		var child:Button = Button(getChildAt(0));
		
		validateNow();

		return child.y + child.baselinePosition;
    }

    //----------------------------------
    //  enabled
    //----------------------------------

    [Inspectable(category="General", enumeration="true,false", defaultValue="true")]

    /**
     *  @private
     */
    override public function set enabled(value:Boolean):void
    {
        if (value != enabled)
        {
            super.enabled = value;

            var n:int = numChildren;
            for (var i:int = 0; i < n; i++)
            {
                if (targetStack)
                {
                    Button(getChildAt(i)).enabled = value &&
                            Container(targetStack.getChildAt(i)).enabled;
                }
                else
                {
                    Button(getChildAt(i)).enabled = value;
                }
            }
        }
    }

    //----------------------------------
    //  horizontalScrollPolicy
    //----------------------------------

    [Inspectable(environment="none")]

    /**
     *  @private
     *  The NavBar control's <code>horizontalScrollPolicy</code> is always
     *  <code>ScrollPolicy.OFF</code>.
     *  It cannot be set to any other value.
     */
    override public function get horizontalScrollPolicy():String
    {
        return ScrollPolicy.OFF;
    }

    /**
     *  @private
     */
    override public function set horizontalScrollPolicy(value:String):void
    {
        // A NavBar control never scrolls.
        // Its horizontalScrollPolicy</code> property is initialized to
        // ScrollPolicy.OFF and can't be changed.
    }

    //----------------------------------
    //  verticalScrollPolicy
    //----------------------------------

    [Inspectable(environment="none")]

    /**
     *  @private
     *  The NavBar control's <code>verticalScrollPolicy</code> is always
     *  <code>ScrollPolicy.OFF</code>.
     *  It cannot be set to any other value.
     */
    override public function get verticalScrollPolicy():String
    {
        return ScrollPolicy.OFF;
    }

    /**
     *  @private
     */
    override public function set verticalScrollPolicy(value:String):void
    {
        // A NavBar control never scrolls.
        // Its <code>verticalScrollPolicy</code> property is initialized
        // to <code>ScrollPolicy.OFF</code> and can't be changed.
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  dataProvider
    //----------------------------------

    /**
     *  @private
     *  Storage for the dataProvider property.
     */
    private var _dataProvider:IList;

    /**
     *  @private
     */
    private var dataProviderChanged:Boolean = false;

    [Bindable("collectionChange")]
    [Inspectable(category="Data")]

    /**
     *  Data used to populate the navigator control.
     *  The type of data can either be a ViewStack container or an Array.
     *
     *  <p>When you use a ViewStack container, the <code>label</code>
     *  and <code>icon</code> properties of the ViewStack container's children
     *  are used to populate the navigation items,
     *  as in the following example:</p>
     * 
     *  <pre>
     *  &lt;mx:LinkBar dataProvider="{vs}"/&gt;
     *  &lt;mx:ViewStack id="vs"&gt;
     *    &lt;mx:VBox label="Accounts" icon="account_icon"/&gt;
     *    &lt;mx:VBox label="Leads" icon="leads_icon"/&gt;
     *  &lt;/mx:ViewStack&gt; </pre>
     *  
     *  <p>The LinkBar control contains two links: "Accounts" and "Leads,"
     *  each with its own icon as specified on the VBox tags.
     *  When you click a link, the ViewStack container navigates
     *  to the corresponding view.</p>
     *
     *  <p>When you use an Array, the <code>labelField</code> property
     *  determines the name of the <code>dataProvider</code> field
     *  to use as the label for each navigation item; the <code>iconField</code>
     *  property determines the name of the <code>dataProvider</code> field
     *  to use as the icon for each navigation item. 
     *  If you use an Array of Strings, the <code>labelField</code>
     *  property is ignored.</p>
     *
     *  @default "undefined"
     */
    public function get dataProvider():Object
    {
        return targetStack ? targetStack : _dataProvider;
    }

    /**
     *  @private
     */
    public function set dataProvider(value:Object /* can be either String, ViewStack, Array or IList */):void
    {
        var message:String;
		
		if (value &&
            !(value is String) &&
            !(value is ViewStack) &&
            !(value is Array) &&
            !(value is IList))
        {
			message = resourceManager.getString(
				"controls", "errWrongContainer", [ id ]);
            throw new Error(message);
        }

        // If value is a string, try to resolve here.
        // If value is a ViewStack name, document[value] may not be defined yet.
        // In this case, fall through to the code below
        // which will setup a pending target view stack.
        if ((value is String) && (document && document[value]))
            value = document[value];

        if ((value is String) || (value is ViewStack))
        {
            setTargetViewStack(value);
            return;
        }
        else if ((value is IList && IList(value).length > 0 &&
                 IList(value).getItemAt(0) is DisplayObject) ||
                 (value is Array && (value as Array).length > 0 &&
                 value[0] is DisplayObject))
        {
            var name:String = id ?
                              className + " '" + id + "'" :
                              "a " + className;
            message = resourceManager.getString(
				"controls", "errWrongType", [ name ]);
			throw new Error(message);
        }

        // Clear any existing target stack.
        setTargetViewStack(null);
        removeAllChildren();

        if (value is IList)
            _dataProvider = IList(value);
        else if (value is Array)
            _dataProvider = new ArrayCollection(value as Array);
        else
            _dataProvider = null;

        dataProviderChanged = true;
        invalidateProperties();

        if (_dataProvider)
        {
            // use weak reference
            _dataProvider.addEventListener(CollectionEvent.COLLECTION_CHANGE,
                                           collectionChangeHandler, false, 0, true);
        }

        // Styles might not have been set yet, so short circuit and let
        // createChildren() handle the rest.
        if (inheritingStyles == UIComponent.STYLE_UNINITIALIZED)
            return;

        dispatchEvent(new Event("collectionChange"));
    }

    //----------------------------------
    //  iconField
    //----------------------------------

    /**
     *  @private
     *  Storage for the iconField property.
     */
    private var _iconField:String = "icon";

    [Bindable("iconFieldChanged")]
    [Inspectable(category="Data")]

    /**
     *  Name of the field in the <code>dataProvider</code> object
     *  to display as the icon for each navigation item. 
     *
     *  @default "icon"
     */
    public function get iconField():String
    {
        return _iconField;
    }

    /**
     *  @private
     */
    public function set iconField(value:String):void
    {
        _iconField = value;

        // If we have a data provider, update here to
        // reflect new icon field.
        if (_dataProvider)
            dataProvider = _dataProvider;

        dispatchEvent(new Event("iconFieldChanged"));
    }

    //----------------------------------
    //  labelField
    //----------------------------------

    /**
     *  @private
     *  Storage for the <code>labelField</code> property.
     */
    private var _labelField:String = "label";

    [Bindable("labelFieldChanged")]
    [Inspectable(category="Data")]

    /**
     *  Name of the field in the <code>dataProvider</code> object
     *  to display as the label for each navigation item. 
     *
     *  @default "label"
     */
    public function get labelField():String
    {
        return _labelField;
    }

    /**
     *  @private
     */
    public function set labelField(value:String):void
    {
        _labelField = value;

        // If we have a data provider, update here to
        // reflect new label field.
        if (_dataProvider)
            dataProvider = _dataProvider;

        dispatchEvent(new Event("labelFieldChanged"));
    }
    
 	//----------------------------------
    //  labelFunction
    //----------------------------------

    /**
     *  @private
     *  Storage for labelFunction property.
     */
    private var _labelFunction:Function;

    [Bindable("labelFunctionChanged")]
    [Inspectable(category="Data")]

    /**
     *  A user-supplied function to run on each item to determine its label.  
     *  By default, the component looks for a property named <code>label</code> 
     *  on each data provider item and displays it.
     *  However, some data sets do not have a <code>label</code> property
     *  nor do they have another property that can be used for displaying.
     *  An example is a data set that has lastName and firstName fields
     *  but you want to display full names.
     *
     *  <p>You can supply a <code>labelFunction</code> that finds the 
     *  appropriate fields and returns a displayable string. The 
     *  <code>labelFunction</code> is also good for handling formatting and 
     *  localization. </p>
     *
     *  <p>For most components, the label function takes a single argument
     *  which is the item in the data provider and returns a String.</p>
     *  <pre>
     *  myLabelFunction(item:Object):String</pre>
     *
     *
     *  @default null
     */
    public function get labelFunction():Function
    {
        return _labelFunction;
    }

    /**
     *  @private
     */
    public function set labelFunction(value:Function):void
    {
        _labelFunction = value;

    //    itemsSizeChanged = true;
        invalidateDisplayList();

        dispatchEvent(new Event("labelFunctionChanged"));
    }
    
    //----------------------------------
    //  selectedIndex
    //----------------------------------

    /**
     *  @private
     *  Storage for the selectedIndex property.
     */
    private var _selectedIndex:int = -1;

    [Bindable("itemClick")]
    [Bindable("valueCommit")]
    [Inspectable(category="General", defaultValue="-1")]

    /**
     *  Index of the active navigation item,
     *  where the first item is at an index of 0.
     *
     *  @default -1
     */
    public function get selectedIndex():int
    {
        return _selectedIndex;
    }

    /**
     *  @private
     */
    public function set selectedIndex(value:int):void
    {
        _selectedIndex = value;

        if (targetStack)
            targetStack.selectedIndex = value;

        dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));
    }

    //----------------------------------
    //  toolTipField
    //----------------------------------

    /**
     *  @private
     *  Storage for the toolTipField property.
     */
    private var _toolTipField:String = "toolTip";

    [Bindable("toolTipFieldChanged")]
    [Inspectable(category="Data")]

    /**
     *  Name of the the field in the <code>dataProvider</code> object
     *  to display as the tooltip label.
     *  
     *  @default "toolTip"
     */
    public function get toolTipField():String
    {
        return _toolTipField;
    }

    /**
     *  @private
     */
    public function set toolTipField(value:String):void
    {
        _toolTipField = value;

        // If we have a data provider, update here to
        // reflect new toolTip field.
        if (_dataProvider)
            dataProvider = _dataProvider;

        dispatchEvent(new Event("toolTipFieldChanged"));
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

        if (dataProviderChanged)
        {
            createNavChildren();
            dataProviderChanged = false;
        }
    }

    /**
     *  @private
     */
    override protected function commitProperties():void
    {
        super.commitProperties();

        if (!measurementHasBeenCalled)
        {
            checkPendingTargetStack();
            measurementHasBeenCalled = true;
        }

        if (dataProviderChanged)
        {
            dataProviderChanged = false;
            createNavChildren();
        }

        if (blocker)
            blocker.visible = false;
    }

    /**
     *  @private
     *  Always do a recursive notification since our content children may have us as
     *  the styleName.
     */
    override public function notifyStyleChangeInChildren(
                                styleProp:String, recursive:Boolean):void
    {
        super.notifyStyleChangeInChildren(styleProp, true);
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

  	//--------------------------------------------------------------------------
    //
    //  Item fields
    //
    //--------------------------------------------------------------------------

   /**
     *  Returns the string the renderer would display for the given data object
     *  based on the labelField and labelFunction properties.
     *  If the method cannot convert the parameter to a string, it returns a
     *  single space.
     *
     *  @param data Object to be rendered.
     *
     *  @return The string to be displayed based on the data.
     */
    public function itemToLabel(data:Object):String
    {
        if (data == null)
            return "";

        if (labelFunction != null)
            return labelFunction(data);

        if (data is XML)
        {
            try
            {
                if (data[labelField].length() != 0)
                    data = data[labelField];
                //by popular demand, this is a default XML labelField
                //else if (data.@label.length() != 0)
                //  data = data.@label;
            }
            catch(e:Error)
            {
            }
        }
        else if (data is Object)
        {
            try
            {
                if (data[labelField] != null)
                    data = data[labelField];
            }
            catch(e:Error)
            {
            }
        }

        if (data is String)
            return String(data);
		
		if (data is Number)
			return data.toString();
			
        return "";
    }

    /**
     *  Creates the individual navigator items.  
     * 
     *  By default, this method performs no action. 
     *  You can override this method in a 
     *  subclass to create a navigator item based on the type of 
     *  navigator items in your subclass.
     *
     *  @param label The label for the created navigator item. 
     *
     *  @param icon The icon for the created navigator item. 
     *  Typically, this is an icon that you have embedded in the application.
     *
     *  @return The created navigator item.
     */
    protected function createNavItem(label:String,
                                     icon:Class = null):IFlexDisplayObject
    {
        // Override to create a nav item.
        return null;
    }

    /**
     *  Highlights the selected navigator item. 
     * 
     *  By default, this method performs no action. 
     *  You can override this method in a subclass to 
     *  highlight the selected navigator item.
     *
     *  @param index The index of the selected item in the NavBar control.
     *  The first item is at an index of 0.
     */
    protected function hiliteSelectedNavItem(index:int):void
    {
        // Override to hilite selected item.
    }

    /**
     *  Resets the navigator bar to its default state.
     * 
     *  By default, this method performs no action. 
     *  You can override this method in a subclass to 
     *  reset the navigator bar to a default state.
     */
    protected function resetNavItems():void
    {
        // Override to reset nav items.
    }

    /**
     *  Sets the label property of a navigator item in the 
     *  NavBar control. 
     * 
     *  You can override this method in a subclass to 
     *  set the label of a navigator item based on the type of 
     *  navigator items in your subclass.
     *
     *  @param index The index of the navigator item in the NavBar control.
     *  The first navigator item is at an index of 0.
     *
     *  @param label The new label text for the navigator item. 
     */
    protected function updateNavItemLabel(index:int, label:String):void
    {
        // Override if your nav item doesn't derive from Button.
        var item:Button = Button(getChildAt(index));

        item.label = label;
    }

    /**
     *  Resets the icon of a navigator item in the 
     *  NavBar control. 
     * 
     *  You can override this method in a subclass to 
     *  set the icon of a navigator item based on the type of 
     *  navigator items in your subclass.
     *
     *  @param index The index of the navigator item in the NavBar control.
     *  The first navigator item is at an index of 0.
     *
     *  @param icon The new icon for the navigator item. 
     *  Typically, this is an icon that you have embedded in the application.
     */
    protected function updateNavItemIcon(index:int, icon:Class):void
    {
        // Override if your nav item doesn't derive from Button.
        var item:Button = Button(getChildAt(index));

        item.setStyle("icon", icon);
    }

    /**
     *  @private
     */
    private function createNavChildren():void
    {
        if (!_dataProvider)
            return;

        var n:int = _dataProvider.length;
        for (var i:int = 0; i < n; i++)
        {
            var item:Object = _dataProvider.getItemAt(i);
            var navItem:Button;
            if (item is String)
            {
                navItem = Button(createNavItem(String(item)));
            }
            else
            {
                var label:String = itemToLabel(item);
                // if labelField doesn't exist in item, label will be null;

                if (iconField)
                {
                    var iconValue:Object = null;

                    try
                    {
                        iconValue = item[iconField];
                    }
                    catch(e:Error)
                    {
                    }

                    // Workaround for bug 123651. Currently
                    // the MXML compiler passes the class name
                    // as a string instead of a Class reference.
                    var icon:Class =
                        iconValue is String ?
                        Class(systemManager.getDefinitionByName(
                                                String(iconValue))) :
                        Class(iconValue);

                    navItem = Button(createNavItem(label, icon));
                }
                else
                {
                    navItem = Button(createNavItem(label, null));
                }

                // Assigning undefined to a String coerces to "",
                // which cancels an ancestor's tooltip.
                // So we have to change undefined to null.
                if (toolTipField)
                {
                    try
                    {
                        navItem.toolTip = (item[toolTipField] === undefined
                                           ? null
                                           : item[toolTipField]);
                    }
                    catch(e:Error)
                    {
                    }
                }
            }

            navItem.enabled = enabled;
        }
    }



    /**
     *  @private
     */
    private function setTargetViewStack(
                        newStack:Object /* can be String or ViewStack */):void
    {
        // If this property is set at creation time, the target view stack
        // may not exist yet. In this case, we just save off the requested
        // target stack and set it later.
        if (!measurementHasBeenCalled && newStack)
        {
            pendingTargetStack = newStack;
            invalidateProperties();
        }
        else
        {
            _setTargetViewStack(newStack);
        }
    }

    /**
     *  @private
     */
    private function _setTargetViewStack(
                        newStack:Object /* can be String or ViewStack */):void
    {
        var newTargetStack:ViewStack;

        if (newStack is ViewStack)
        {
            newTargetStack = ViewStack(newStack);
        }
        else if (newStack)
        {
            // newStack is not a ViewStack.
            // First, look for it on the current document
            newTargetStack = parentDocument[newStack];
        }
        else
        {
            newTargetStack = null;
        }

        // Remove any existing event listeners
        if (targetStack)
        {
            targetStack.removeEventListener(
                ChildExistenceChangedEvent.CHILD_ADD,
                childAddHandler);

            targetStack.removeEventListener(
                ChildExistenceChangedEvent.CHILD_REMOVE,
                childRemoveHandler);

            targetStack.removeEventListener(Event.CHANGE, changeHandler);

            targetStack.removeEventListener(
                IndexChangedEvent.CHILD_INDEX_CHANGE, childIndexChangeHandler);
        }

        // Clear out the current links
        removeAllChildren();
        _selectedIndex = -1;

        targetStack = newTargetStack;

        if (!targetStack)
            return;

        targetStack.addEventListener(ChildExistenceChangedEvent.CHILD_ADD,
                                     childAddHandler);

        targetStack.addEventListener(ChildExistenceChangedEvent.CHILD_REMOVE,
                                     childRemoveHandler);

        targetStack.addEventListener(Event.CHANGE, changeHandler);

        targetStack.addEventListener(IndexChangedEvent.CHILD_INDEX_CHANGE,
                                     childIndexChangeHandler);

        var numViews:int = targetStack.numChildren;

        for (var i:int = 0; i < numViews; i++)
        {
            var child:Container = Container(targetStack.getChildAt(i));
            var item:Button = Button(createNavItem(itemToLabel(child), child.icon));

                 

            // Make the nav item inherit the tooltip from the child
            if (child.toolTip)
            {
                item.toolTip = child.toolTip;
                child.toolTip = null;
            }
            
            child.addEventListener("labelChanged", labelChangedHandler);
            child.addEventListener("iconChanged", iconChangedHandler);
            child.addEventListener("enabledChanged", enabledChangedHandler);
        	child.addEventListener("toolTipChanged", toolTipChangedHandler);      

            item.enabled = enabled && child.enabled;
        }

        var index:int = targetStack.selectedIndex;
        if (index == -1 && targetStack.numChildren > 0)
            index = 0;

        if (index != -1)
            hiliteSelectedNavItem(index);

        invalidateDisplayList();
    }

    /**
     *  @private
     */
    private function checkPendingTargetStack():void
    {
        // Check for pending target view stacks.
        if (pendingTargetStack)
        {
            _setTargetViewStack(pendingTargetStack);
            pendingTargetStack = null;
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function collectionChangeHandler(event:Event):void
    {
        // Brute force -- rebuild everything...
        dataProvider = dataProvider;
    }

    /**
     *  @private
     */
    private function childAddHandler(event:ChildExistenceChangedEvent):void
    {
        // Prevent infinite recursion; the call to createNavItem() below
        // can cause this handler to get invoked due to event bubbling,
        // such as when a TabBar is inside a TabNavigator.
        if (event.target == this)
            return;

        // Bail if this isn't a child of the target stack.
        if (event.relatedObject.parent != targetStack)
            return;

        var newChild:Container = Container(event.relatedObject);
        var item:Button = Button(createNavItem(itemToLabel(newChild), newChild.icon));
        var index:int = newChild.parent.getChildIndex(DisplayObject(newChild));
        setChildIndex(item, index);

        if (newChild.toolTip)
        {
            item.toolTip = newChild.toolTip;
            newChild.toolTip = null;
        }

        newChild.addEventListener("labelChanged", labelChangedHandler);
        newChild.addEventListener("iconChanged", iconChangedHandler);
        newChild.addEventListener("enabledChanged", enabledChangedHandler);
        newChild.addEventListener("toolTipChanged", toolTipChangedHandler);
	
        item.enabled = enabled && newChild.enabled;
    }

    /**
     *  @private
     */
    private function childRemoveHandler(event:ChildExistenceChangedEvent):void
    {
        // Prevent infinite recursion; the call to removeChildAt() below
        // can cause this handler to get invoked due to event bubbling,
        // such as when a TabBar is inside a TabNavigator.
        if (event.target == this)
            return;

        var viewStack:ViewStack = ViewStack(event.target);
        removeChildAt(viewStack.getChildIndex(event.relatedObject));

        callLater(resetNavItems);
    }

    /**
     *  @private
     */
    private function changeHandler(event:Event):void
    {
        // Change events from text fields propagate, so we need to make sure
        // this event is coming from our dataProvider
        if (event.target == dataProvider)
            hiliteSelectedNavItem(Object(event.target).selectedIndex);
    }

    /**
     *  @private
     */
    private function childIndexChangeHandler(event:IndexChangedEvent):void
    {
        // Prevent infinite recursion; the call to setChildIndex() below
        // can cause this handler to get invoked due to event bubbling,
        // such as when a TabBar is inside a TabNavigator.
        if (event.target == this)
            return;

        setChildIndex(getChildAt(event.oldIndex), event.newIndex);
        resetNavItems();
    }

    /**
     *  @private
     */
    private function labelChangedHandler(event:Event):void
    {
        // Event.target is a child of the target view stack. Need to
        // convert to index
        var itemIndex:int =
            targetStack.getChildIndex(DisplayObject(event.target));

        updateNavItemLabel(itemIndex, Container(event.target).label);
    }

    /**
     *  @private
     */
    private function iconChangedHandler(event:Event):void
    {
        // Event.target is a child of the target view stack. Need to
        // convert to index
        var itemIndex:int =
            targetStack.getChildIndex(DisplayObject(event.target));

        updateNavItemIcon(itemIndex, Container(event.target).icon);
    }
    
    /**
     * @private
     */
    private function toolTipChangedHandler(event:Event):void
    {
    	// Event.target is a child of the target view stack. Need to
        // convert to index
        var itemIndex:int =
            targetStack.getChildIndex(DisplayObject(event.target));
        var item:UIComponent = UIComponent(getChildAt(itemIndex));
		
		if (UIComponent(event.target).toolTip)
		{
			item.toolTip = UIComponent(event.target).toolTip;
			lastToolTip = UIComponent(event.target).toolTip;
			UIComponent(event.target).toolTip = null;
		}
		else if (!lastToolTip)
		{
       		item.toolTip = UIComponent(event.target).toolTip;
       		lastToolTip = "placeholder";
        	UIComponent(event.target).toolTip = null;
  		}
  		else
  		 	lastToolTip = null;
    } 

    /**
     *  @private
     */
    private function enabledChangedHandler(event:Event):void
    {
        // Event.target is a child of the target view stack. Need to
        // convert to index
        var itemIndex:int =
            targetStack.getChildIndex(DisplayObject(event.target));

        Button(getChildAt(itemIndex)).enabled = enabled && event.target.enabled;
    }

    /**
     *  Handles the <code>MouseEvent.CLICK</code> event 
     *  for the items in the NavBar control. This handler
     *  dispatches the <code>itemClick</code> event for the NavBar control.
     *
     *  @param event The event object.
     */
    protected function clickHandler(event:MouseEvent):void
    {
        var index:int = getChildIndex(DisplayObject(event.currentTarget));
        if (targetStack)
            targetStack.selectedIndex = index;

        _selectedIndex = index;

        var newEvent:ItemClickEvent =
            new ItemClickEvent(ItemClickEvent.ITEM_CLICK);
        newEvent.label = Button(event.currentTarget).label;
        newEvent.index = index;
        newEvent.relatedObject = InteractiveObject(event.currentTarget);
        newEvent.item = _dataProvider ?
                        _dataProvider.getItemAt(index) :
                        null;
        dispatchEvent(newEvent);
    
        event.stopImmediatePropagation();
    }

}

}
