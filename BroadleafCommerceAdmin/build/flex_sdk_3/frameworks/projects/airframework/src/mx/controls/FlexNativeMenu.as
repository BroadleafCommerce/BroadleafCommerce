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

package mx.controls
{
	import flash.display.InteractiveObject;
	import flash.display.NativeMenu;
	import flash.display.NativeMenuItem;
	import flash.display.Stage;
	import flash.events.Event;
	import flash.events.EventDispatcher;
	import flash.events.TimerEvent;
	import flash.ui.Keyboard;
	import flash.utils.Timer;
	import flash.xml.XMLNode;
	
	import mx.collections.ArrayCollection;
	import mx.collections.ICollectionView;
	import mx.collections.XMLListCollection;
	import mx.collections.errors.ItemPendingError;
	import mx.controls.menuClasses.IMenuDataDescriptor;
	import mx.controls.treeClasses.DefaultDataDescriptor;
	import mx.core.Application;
	import mx.core.EventPriority;
	import mx.core.UIComponent;
	import mx.core.UIComponentGlobals;
	import mx.core.mx_internal;
	import mx.events.CollectionEvent;
	import mx.events.CollectionEventKind;
	import mx.events.FlexNativeMenuEvent;
	import mx.managers.ILayoutManagerClient;
	import mx.managers.ISystemManager;
	
//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched before a menu or submenu is displayed.
 *
 *  @eventType mx.events.FlexNativeMenuEvent.MENU_SHOW
 */
[Event(name="menuShow", type="mx.events.FlexNativeMenuEvent")]

/**
 *  Dispatched when a menu item is selected.
 *
 *  @eventType mx.events.FlexNativeMenuEvent.ITEM_CLICK
 */
[Event(name="itemClick", type="mx.events.FlexNativeMenuEvent")]

/**
 *  The FlexNativeMenu component provides a wrapper for AIR's NativeMenu class. The FlexNativeMenu
 *  provides a way to define native operating system menus (such as window, application, and
 *  context menus) using techniques that are familiar to Flex developers and consistent with
 *  other Flex menu components, such as using MXML and data providers to specify menu structure.
 *  However, unlike Flex menu components, the menus that are defined by a FlexNativeMenu
 *  component are rendered by the host operating system as part of an AIR application, rather
 *  than being created as visual components by Flex.
 *
 *  <p>Like other Flex menu components, to define the structure of a menu represented by a
 *  FlexNativeMenu component, you create a data provider such as an XML hierarchy or an array
 *  of objects containing data to be used to define the menu. Several properties can be set to
 *  define how the data provider data is interpreted, such as the <code>labelField</code> property
 *  to specify the data field that is used for the menu item label, the <code>keyEquivalentField</code>
 *  property to specify the field that defines a keyboard equivalent shortcut for the menu item,
 *  and the <code>mnemonicIndexField</code> property to specify the field that defines the index
 *  position of the character in the label that is used as the menu item's mnemonic.</p>
 *
 *  <p>The data provider for FlexNativeMenu items can specify several attributes that determine how
 *  the item is displayed and behaves, as the following XML data provider shows:</p>
 *  <pre>
 *   &lt;mx:XML format=&quot;e4x&quot; id=&quot;myMenuData&quot;&gt;
 *     &lt;root&gt;
 *        &lt;menuitem label=&quot;MenuItem A&quot;&gt;
 *            &lt;menuitem label=&quot;SubMenuItem A-1&quot; enabled=&quot;False&quot;/&gt;
 *            &lt;menuitem label=&quot;SubMenuItem A-2&quot;/&gt;
 *        &lt;/menuitem&gt;
 *        &lt;menuitem label=&quot;MenuItem B&quot; type=&quot;check&quot; toggled=&quot;true&quot;/&gt;
 *        &lt;menuitem label=&quot;MenuItem C&quot; type=&quot;check&quot; toggled=&quot;false&quot;/&gt;
 *        &lt;menuitem type=&quot;separator&quot;/&gt;
 *        &lt;menuitem label=&quot;MenuItem D&quot;&gt;
 *            &lt;menuitem label=&quot;SubMenuItem D-1&quot;/&gt;
 *            &lt;menuitem label=&quot;SubMenuItem D-2&quot;/&gt;
 *            &lt;menuitem label=&quot;SubMenuItem D-3&quot;/&gt;
 *        &lt;/menuitem&gt;
 *    &lt;/root&gt;
 * &lt;/mx:XML&gt;</pre>
 *
 *  <p>The following table lists the attributes you can specify,
 *  their data types, their purposes, and how the data provider must represent
 *  them if the menu uses the DefaultDataDescriptor class to parse the data provider:</p>
 *
 *  <table class="innertable">
 *  <tr>
 *    <th>Attribute</th>
 *    <th>Type</th>
 *    <th>Description</th>
 *  </tr>
 *  <tr>
 *    <td><code>altKey</code></td>
 *    <td>Boolean</td>
 *    <td>Specifies whether the Alt key is required as part of the key equivalent for the item.</td>
 *  </tr>
 *  <tr>
 *    <td><code>cmdKey</code></td>
 *    <td>Boolean</td>
 *    <td>Specifies whether the Command key is required as part of the key equivalent for the item.</td>
 *  </tr>
 *  <tr>
 *    <td><code>ctrlKey</code></td>
 *    <td>Boolean</td>
 *    <td>Specifies whether the Control key is required as part of the key equivalent for the item.</td>
 *  </tr>
 *  <tr>
 *    <td><code>enabled</code></td>
 *    <td>Boolean</td>
 *    <td>Specifies whether the user can select the menu item (<code>true</code>),
 *      or not (<code>false</code>). If not specified, Flex treats the item as if
 *      the value were <code>true</code>.
 *      If you use the default data descriptor, data providers must use an <code>enabled</code>
 *      XML attribute or object field to specify this characteristic.</td>
 *  </tr>
 *  <tr>
 *    <td><code>keyEquivalent</code></td>
 *    <td>String</td>
 *    <td>Specifies a keyboard character which, when pressed, triggers an event as though
 *        the menu item was selected. The menu's <code>keyEquivalentField</code> or
 *        <code>keyEquivalentFunction</code> property determines the name of the field
 *        in the data that specifies the key equivalent, or a function for determining
 *        the key equivalents. (If the data provider is in E4X XML format, you must specify
 *        one of these properties to assign a key equivalent.)</td>
 *  </tr>
 *  <tr>
 *    <td><code>label</code></td>
 *    <td>String</td>
 *    <td>Specifies the text that appears in the control. This item is used for all
 *      menu item types except <code>separator</code>.
 *      The menu's <code>labelField</code> or <code>labelFunction</code> property
 *      determines the name of the field in the data that specifies the label,
 *      or a function for determining the labels. (If the data provider is in E4X XML format,
 *      you must specify one of these properties to display a label.)
 *      If the data provider is an Array of Strings, Flex uses the String value as the label.</td>
 *  </tr>
 *  <tr>
 *    <td><code>mnemonicIndex</code></td>
 *    <td>Integer</td>
 *    <td>Specifies the index position of the character in the label that is used as the
 *        mnemonic for the menu item. The menu's <code>mnemonicIndexField</code> or
 *        <code>mnemonicIndexFunction</code> property determines the name of the field
 *        in the data that specifies the mnemonic index, or a function for determining
 *        mnemonic index. (If the data provider is in E4X XML format, you must specify
 *        one of these properties to specify a mnemonic index in the data.) Alternatively,
 *        you can indicate that a character in the label is the menu item's mnemonic by
 *        including an underscore immediately to the left of that character.</td>
 *  </tr>
 *  <tr>
 *    <td><code>shiftKey</code></td>
 *    <td>Boolean</td>
 *    <td>Specifies whether the Shift key is required as part of the key equivalent for the item.</td>
 *  </tr>
 *  <tr>
 *    <td><code>toggled</code></td>
 *    <td>Boolean</td>
 *    <td>Specifies whether a check item is selected.
 *      If not specified, Flex treats the item as if the value were <code>false</code>
 *      and the item is not selected.
 *      If you use the default data descriptor, data providers must use a <code>toggled</code>
 *      XML attribute or object field to specify this characteristic.</td>
 *  </tr>
 *  <tr>
 *    <td><code>type</code></td>
 *    <td>String</td>
 *    <td>Specifies the type of menu item. Meaningful values are <code>separator</code> and
 *      <code>check</code>. Flex treats all other values,
 *      or nodes with no type entry, as normal menu entries.
 *      If you use the default data descriptor, data providers must use a <code>type</code>
 *      XML attribute or object field to specify this characteristic.</td>
 *  </tr>
 * </table>
 *
 *  <p>To create a window menu, set the FlexNativeMenu as the <code>menu</code> property of the
 *  Window or WindowedApplication instance on which the menu should appear. To create an application
 *  menu, assign the FlexNativeMenu as the <code>menu</code> property of the application's
 *  WindowedApplication. To assign a FlexNativeMenu as the context menu for a portion of the user interface,
 *  call the FlexNativeMenu instance's <code>setContextMenu()</code> method, passing the UI object
 *  as an argument. Call the FlexNativeMenu component's <code>display()</code> method to display the
 *  menu as a pop-up menu anywhere on one of the application's windows.</p>
 *
 *  <p>To detect when menu items commands are triggered, register a listener for the <code>itemClick</code>
 *  event. You can also register a listener for the <code>menuShow</code> event to determine when
 *  any menu or submenu is opened.</p>
 *
 *  @mxml
 *  <p>The <code>&lt;mx:FlexNativeMenu&gt</code> tag supports the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:FlexNativeMenu
 *    <b>Properties</b>
 *    dataDescriptor="<i>mx.controls.treeClasses.DefaultDataDescriptor</i>"
 *    dataProvider="<i>undefined</i>"
 *    keyEquivalentField="keyEquivalent"
 *    keyEquivalentFunction="<i>undefined</i>"
 *    keyEquivalentModifiersFunction="<i>undefined</i>"
 *    labelField="label"
 *    labelFunction="<i>undefined</i>"
 *    mnemonicIndexField="mnemonicIndex"
 *    mnemonicIndexFunction="<i>undefined</i>"
 *    showRoot="true"
 * 
 *    <b>Events</b>
 *    itemClick="<i>No default</i>"
 *    menuShow="<i>No default</i>"
 *  /&gt;
 *  </pre>
 *
 *  @see flash.display.NativeMenu
 *  @see mx.events.FlexNativeMenuEvent
 * 
 *  @playerversion AIR 1.1
 */
public class FlexNativeMenu extends EventDispatcher implements ILayoutManagerClient, IFlexContextMenu
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class variables
    //
    //--------------------------------------------------------------------------

    /**
     *  The character to use to indicate the mnemonic index in a label.  By
     *  default, it is the underscore character, so in "C_ut", u would become
     *  the character for the mnemonic index.
     */
    private static var MNEMONIC_INDEX_CHARACTER:String = "_";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function FlexNativeMenu()
    {
        super();

        _nativeMenu.addEventListener(Event.DISPLAYING, menuDisplayHandler, false, 0, true);
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    //--------------------------------------------------------------------------
	//
	//  Properties: ILayoutManagerClient
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  initialized
	//----------------------------------

    /**
	 *  @private
	 *  Storage for the initialized property.
	 */
	private var _initialized:Boolean = false;

    /**
	 *  @copy mx.core.UIComponent#initialized
     */
    public function get initialized():Boolean
	{
		return _initialized;
	}

    /**
     *  @private
     */
    public function set initialized(value:Boolean):void
	{
		_initialized = value;
	}

    //----------------------------------
    //  nestLevel
    //----------------------------------

    /**
	 *  @private
	 *  Storage for the nestLevel property.
	 */
	private var _nestLevel:int = 1;
	
	// no one will likely set nestLevel (but there's a setter in case
	// someone wants to.  We default nestLevel to 1 as it's a top-level
	// component that goes in the chrome.

	/**
     *  @copy mx.core.UIComponent#nestLevel
     */
	public function get nestLevel():int
	{
		return _nestLevel;
	}
	
	/**
     *  @private
     */
	public function set nestLevel(value:int):void
	{
		_nestLevel = value;
		
		// After nestLevel is initialized, add this object to the
		// LayoutManager's queue, so that it is drawn at least once
		invalidateProperties();
	}
	
	//----------------------------------
	//  processedDescriptors
	//----------------------------------

    /**
     *  @private
	 *  Storage for the processedDescriptors property.
     */
	private var _processedDescriptors:Boolean = false;

    /**
     *  @copy mx.core.UIComponent#processedDescriptors
     */
    public function get processedDescriptors():Boolean
	{
		return _processedDescriptors;
	}

    /**
     *  @private
     */
    public function set processedDescriptors(value:Boolean):void
	{
		_processedDescriptors = value;
	}

	//----------------------------------
	//  updateCompletePendingFlag
	//----------------------------------

    /**
     *  @private
	 *  Storage for the updateCompletePendingFlag property.
     */
	private var _updateCompletePendingFlag:Boolean = false;

    /**
	 *  A flag that determines if an object has been through all three phases
	 *  of layout validation (provided that any were required).
     */
    public function get updateCompletePendingFlag():Boolean
	{
		return _updateCompletePendingFlag;
	}

    /**
     *  @private
     */
    public function set updateCompletePendingFlag(value:Boolean):void
	{
		_updateCompletePendingFlag = value;
	}

    //--------------------------------------------------------------------------
    //
    //  Variables: Invalidation
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Whether this component needs to have its
     *  commitProperties() method called.
     */
    private var invalidatePropertiesFlag:Boolean = false;

    /**
     * @private
     */
    private var _nativeMenu:NativeMenu = new NativeMenu();

    [Bindable("nativeMenuUpdate")]

    //----------------------------------
    //  nativeMenu
    //----------------------------------

    /**
      *  Returns the flash.display.NativeMenu managed by this object,
      *  or null if there is not one.
      *
      *  Any changes made directly to the underlying NativeMenu instance
	  *  may be lost when changes are made to the menu or the underlying
	  *  data provider.
      */
	public function get nativeMenu() : NativeMenu
	{
		return _nativeMenu;
	}
	
	//----------------------------------
    //  dataDescriptor
    //----------------------------------

	/**
     *  @private
     */
    private var dataDescriptorChanged:Boolean = false;

    /**
     *  @private
     */
    private var _dataDescriptor:IMenuDataDescriptor =
        new DefaultDataDescriptor();

    [Inspectable(category="Data")]

    /**
     *  The object that accesses and manipulates data in the data provider.
     *  The FlexNativeMenu control delegates to the data descriptor for information
     *  about its data. This data is then used to parse and move about the
     *  data source. The data descriptor defined for the FlexNativeMenu is used for
     *  all child menus and submenus.
     *
     *  <p>When you specify this property as an attribute in MXML, you must
     *  use a reference to the data descriptor, not the string name of the
     *  descriptor. Use the following format for setting the property:</p>
     *
     * <pre>&lt;mx:FlexNativeMenu id="flexNativeMenu" dataDescriptor="{new MyCustomDataDescriptor()}"/&gt;</pre>
     *
     *  <p>Alternatively, you can specify the property in MXML as a nested
     *  subtag, as the following example shows:</p>
     *
     *  <pre>&lt;mx:FlexNativeMenu&gt;
     *  &lt;mx:dataDescriptor&gt;
     *     &lt;myCustomDataDescriptor&gt;
     *  &lt;/mx:dataDescriptor&gt;
     *  ...</pre>
     *
     *  <p>The default value is an internal instance of the
     *  DefaultDataDescriptor class.</p>
     */
    public function get dataDescriptor():IMenuDataDescriptor
    {
        return IMenuDataDescriptor(_dataDescriptor);
    }

    /**
     *  @private
     */
    public function set dataDescriptor(value:IMenuDataDescriptor):void
    {
        _dataDescriptor = value;

        dataDescriptorChanged = true;
    }
	
	//----------------------------------
    //  dataProvider
    //----------------------------------

	/**
     *  @private
     */
    private var dataProviderChanged:Boolean = false;

    /**
     *  @private
     *  Storage variable for the original dataProvider
     */
    mx_internal var _rootModel:ICollectionView;

    [Bindable("collectionChange")]
    [Inspectable(category="Data")]

    /**
     *  The hierarchy of objects that are used to define the structure
	 *  of menu items in the NativeMenu. Individual data objects define
	 *  menu items, and items with child items become menus and submenus.
     *
     *  <p>The FlexNativeMenu control handles the source data object as follows:</p>
	 *
     *  <ul>
     *    <li>A String containing valid XML text is converted to an XML object.</li>
     *    <li>An XMLNode is converted to an XML object.</li>
     *    <li>An XMLList is converted to an XMLListCollection.</li>
     *    <li>Any object that implements the ICollectionView interface is cast to
     *        an ICollectionView.</li>
     *    <li>An Array is converted to an ArrayCollection.</li>
     *    <li>Any other type object is wrapped in an Array with the object as its sole
     *        entry.</li>
     *  </ul>
     *
     *  @default "undefined"
     */
    public function get dataProvider():Object
    {
        if (mx_internal::_rootModel)
        {
            return mx_internal::_rootModel;
        }
        else return null;
    }

    /**
     *  @private
     */
    public function set dataProvider(value:Object):void
    {
        if (mx_internal::_rootModel)
        {
            mx_internal::_rootModel.removeEventListener(CollectionEvent.COLLECTION_CHANGE,
                                           collectionChangeHandler);
        }

        // handle strings and xml
        if (typeof(value)=="string")
            value = new XML(value);
        else if (value is XMLNode)
            value = new XML(XMLNode(value).toString());
        else if (value is XMLList)
            value = new XMLListCollection(value as XMLList);

        if (value is XML)
        {
            _hasRoot = true;
            var xl:XMLList = new XMLList();
            xl += value;
            mx_internal::_rootModel = new XMLListCollection(xl);
        }
        //if already a collection dont make new one
        else if (value is ICollectionView)
        {
            mx_internal::_rootModel = ICollectionView(value);
            if (mx_internal::_rootModel.length == 1)
                _hasRoot = true;
        }
        else if (value is Array)
        {
            mx_internal::_rootModel = new ArrayCollection(value as Array);
        }
        //all other types get wrapped in an ArrayCollection
        else if (value is Object)
        {
            _hasRoot = true;
            // convert to an array containing this one item
            var tmp:Array = [];
            tmp.push(value);
            mx_internal::_rootModel = new ArrayCollection(tmp);
        }
        else
        {
            mx_internal::_rootModel = new ArrayCollection();
        }
        //add listeners as weak references
        mx_internal::_rootModel.addEventListener(CollectionEvent.COLLECTION_CHANGE,
                                    collectionChangeHandler, false, 0, true);
        //flag for processing in commitProps
        dataProviderChanged = true;
        invalidateProperties();

        var event:CollectionEvent = new CollectionEvent(CollectionEvent.COLLECTION_CHANGE);
        event.kind = CollectionEventKind.RESET;
        collectionChangeHandler(event);
        dispatchEvent(event);
    }

    //----------------------------------
    //  hasRoot
    //----------------------------------

    /**
     *  @private
     *  Flag to indicate if the model has a root
     */
    private var _hasRoot:Boolean = false;

    /**
     *  @copy mx.controls.Menu#hasRoot
     */
    public function get hasRoot():Boolean
    {
        return _hasRoot;
    }

    //----------------------------------
    //  keyEquivalentField
    //----------------------------------

	/**
     *  @private
     */
    private var keyEquivalentFieldChanged:Boolean = false;

    /**
     *  @private
     */
    private var _keyEquivalentField:String = "keyEquivalent";

    [Bindable("keyEquivalentChanged")]
    [Inspectable(category="Data", defaultValue="keyEquivalent")]

    /**
     *  The name of the field in the data provider that determines the
     *  key equivalent for each menu item.  The set of values is defined
	 *  in the Keyboard class, in the <code>KEYNAME_XXXX</code> constants. For example,
	 *  consult that list for the value for a control character such as Home, Insert, etc.
     *
     *  <p>Setting the <code>keyEquivalentFunction</code> property causes this property to be ignored.</p>
     *
     *  @default "keyEquivalent"
     *  @see flash.ui.Keyboard
     */
    public function get keyEquivalentField():String
    {
        return _keyEquivalentField;
    }

    /**
     *  @private
     */
    public function set keyEquivalentField(value:String):void
    {
        if (_keyEquivalentField != value)
        {
            _keyEquivalentField = value;
            keyEquivalentFieldChanged = true;

            invalidateProperties();

            dispatchEvent(new Event("keyEquivalentFieldChanged"));
        }
    }

    //----------------------------------
    //  keyEquivalentFunction
    //----------------------------------

    /**
     *  @private
     */
    private var _keyEquivalentFunction:Function;

    [Bindable("keyEquivalentFunctionChanged")]
    [Inspectable(category="Data")]

    /**
     *  The function that determines the key equivalent for each menu item.
     *  If you omit this property, Flex uses the contents of the field or
     *  attribute specified by the <code>keyEquivalentField</code> property.
     *  If you specify this property, Flex ignores any <code>keyEquivalentField</code>
     *  property value.
     *
     *  <p>The <code>keyEquivalentFunction</code> property is good for handling formatting,
     *  localization, and platform independence.</p>
     *
     *  <p>The key equivalent function must take a single argument, which is the item
     *  in the data provider, and must return a String.</p>
	 *
     *  <pre><code>myKeyEquivalentFunction(item:Object):String</code></pre>
     *
     *  @default "undefined"
     *  @see flash.ui.Keyboard
     */
    public function get keyEquivalentFunction():Function
    {
        return _keyEquivalentFunction;
    }

    /**
     *  @private
     */
    public function set keyEquivalentFunction(value:Function):void
    {
        if (_keyEquivalentFunction != value)
        {
            _keyEquivalentFunction = value;
            keyEquivalentFieldChanged = true;

            invalidateProperties();

            dispatchEvent(new Event("keyEquivalentFunctionChanged"));
        }
    }

    //----------------------------------
    //  keyEquivalentModifiersFunction
    //----------------------------------

	/**
     *  @private
     */
    private var keyEquivalentModifiersFunctionChanged:Boolean = false;

    /**
     *  @private
     */
    private var _keyEquivalentModifiersFunction:Function = keyEquivalentModifiersDefaultFunction;
    	
    private function keyEquivalentModifiersDefaultFunction(data:Object):Array
	{
		var modifiers:Array = [];
		var xmlModifiers:Array = ["@altKey", "@cmdKey", "@ctrlKey", "@shiftKey"];
		var objectModifiers:Array = ["altKey", "cmdKey", "ctrlKey", "shiftKey"];
        var keyboardModifiers:Array = [Keyboard.ALTERNATE, Keyboard.COMMAND, Keyboard.CONTROL, Keyboard.SHIFT];
		
		if (data is XML)
        {
        	for (var i:int = 0; i < xmlModifiers.length; i++)
        	{
        		try
	            {
	            	var modifier:* = data[xmlModifiers[i]];
		            if (modifier[0] == true)
		                modifiers.push(keyboardModifiers[i]);
	            }
	            catch(e:Error)
	            {
	            }
        	}
        }
        else if (data is Object)
        {
            for (i = 0; i < objectModifiers.length; i++)
        	{
        		try
	            {
	            	modifier = data[objectModifiers[i]];
		            if (String(modifier).toLowerCase() == "true")
		                modifiers.push(keyboardModifiers[i]);
	            }
	            catch(e:Error)
	            {
	            }
        	}
        }

        return modifiers;
    }

    [Bindable("keyEquivalentModifiersFunctionChanged")]
    [Inspectable(category="Data")]

    /**
     *  The function that determines the key equivalent modifiers for each menu item.
     *
     *  If you omit this property, Flex uses its own default function to determine the
     *  Array of modifiers by looking in the data provider data for the presence of
	 *  the following (boolean) fields: <code>altKey</code>, <code>cmdKey</code>,
	 *  <code>ctrlKey</code>, and <code>shiftKey</code>.
     *
     *  <p>The <code>keyEquivalentModifiersFunction</code> property is good for handling
     *  formatting, localization, and platform independence.</p>
     *
     *  <p>The key equivalent modifiers function must take a single argument, which
     *  is the item in the data provider, and must return an array of modifier key names.</p>
	 *
     *  <pre><code>myKeyEquivalentModifiersFunction(item:Object):Array</code></pre>
     *
     *  @default "undefined"
     */
    public function get keyEquivalentModifiersFunction():Function
    {
        return _keyEquivalentModifiersFunction;
    }

    /**
     *  @private
     */
    public function set keyEquivalentModifiersFunction(value:Function):void
    {
        if (_keyEquivalentModifiersFunction != value)
        {
            _keyEquivalentModifiersFunction = value;
            keyEquivalentModifiersFunctionChanged = true;

            invalidateProperties();

            dispatchEvent(new Event("keyEquivalentModifiersFunctionChanged"));
        }
    }

    //----------------------------------
    //  labelField
    //----------------------------------

	/**
     *  @private
     */
    private var labelFieldChanged:Boolean = false;

    /**
     *  @private
     */
    private var _labelField:String = "label";

    [Bindable("labelFieldChanged")]
    [Inspectable(category="Data", defaultValue="label")]

    /**
     *  The name of the field in the data provider that determines the
     *  text to display for each menu item. If the data provider is an Array of
     *  Strings, Flex uses each string value as the label. If the data
     *  provider is an E4X XML object, you must set this property explicitly.
     *  For example, if each XML elementin an E4X XML Object includes a "label"
	 *  attribute containing the text to display for each menu item, set
	 *  the labelField to <code>"&#064;label"</code>.
     *
     *  <p>In a label, you can specify the character to be used as the mnemonic index
     *  by preceding it with an underscore. For example, a label value of <code>"C_ut"</code>
	 *  sets the mnemonic index to 1. Only the first underscore present is used for this
	 *  purpose.  To display a literal underscore character in the label, you can escape it
	 *  using a double underscore. For example, a label value of <code>"C__u_t"</code> would
	 *  result in a menu item with the label "C_ut" and a mnemonic index of 3 (the "t"
	 *  character). If the field defined in the <code>mnemonicIndexField</code> property
	 *  is present and set to a value greater than zero, that value takes precedence over
	 *  any underscore-specified mnemonic index value.</p>
     *
     *  <p>Setting the <code>labelFunction</code> property causes this property to be ignored.</p>
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
        if (_labelField != value)
        {
            _labelField = value;
            labelFieldChanged = true;

            invalidateProperties();

            dispatchEvent(new Event("labelFieldChanged"));
        }
    }

    //----------------------------------
    //  labelFunction
    //----------------------------------

    /**
     *  @private
     */
    private var _labelFunction:Function;

    [Bindable("labelFunctionChanged")]
    [Inspectable(category="Data")]

    /**
     *  The function that determines the text to display for each menu item.
     *  The label function must find the appropriate field or fields in the
     *  data provider and return a displayable string.
     *
     *  <p>If you omit this property, Flex uses the contents of the field or
     *  attribute specified by the <code>labelField</code> property.
     *  If you specify this property, Flex ignores any <code>labelField</code>
     *  property value.</p>
     *
     *  <p>The <code>labelFunction</code> property can be helpful for handling formatting,
     *  localization, and platform-independence.</p>
     *
     *  <p>The label function must take a single argument, which is the item
     *  in the data provider, and must return a String.</p>
	 *
     *  <pre><code>myLabelFunction(item:Object):String</code></pre>
     *
     *  @default "undefined"
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
        if (_labelFunction != value)
        {
            _labelFunction = value;
            labelFieldChanged = true;

            invalidateProperties();

            dispatchEvent(new Event("labelFunctionChanged"));
        }
    }

    //----------------------------------
    //  mnemonicIndexField
    //----------------------------------

	/**
     *  @private
     */
    private var mnemonicIndexFieldChanged:Boolean = false;

    /**
     *  @private
     */
    private var _mnemonicIndexField:String = "mnemonicIndex";

    [Bindable("mnemonicIndexChanged")]
    [Inspectable(category="Data", defaultValue="mnemonicIndex")]

    /**
     *  The name of the field in the data provider that determines the
     *  mnemonic index for each menu item.
     *
     *  <p>If the field specified by this property contains a number greater
	 *  than zero, that mnemonic index
     *  takes precedence over one specified by an underscore in the label.</p>
     *
     *  <p>Setting the <code>mnemonicIndexFunction</code> property causes
	 *  this property to be ignored.</p>
     *
     *  @default "mnemonicIndex"
	 *
	 *  @see #labelField
     */
    public function get mnemonicIndexField():String
    {
        return _mnemonicIndexField;
    }

    /**
     *  @private
     */
    public function set mnemonicIndexField(value:String):void
    {
        if (_mnemonicIndexField != value)
        {
            _mnemonicIndexField = value;
            mnemonicIndexFieldChanged = true;

            invalidateProperties();

            dispatchEvent(new Event("mnemonicIndexFieldChanged"));
        }
    }

    //----------------------------------
    //  mnemonicIndexFunction
    //----------------------------------

    /**
     *  @private
     */
    private var _mnemonicIndexFunction:Function;

    [Bindable("mnemonicIndexFunctionChanged")]
    [Inspectable(category="Data")]

    /**
     *  The function that determines the mnemonic index for each menu item.
     *
     *  <p>If you omit this property, Flex uses the contents of the field or
     *  attribute specified by the <code>mnemonicIndexField</code> property.
     *  If you specify this property, Flex ignores any <code>mnemonicIndexField</code>
     *  property value.</p>
     *
     *  <p>If this property is defined and the function returns a number greater than
	 *  zero for a data item, the returned mnemonic index
     *  takes precedence over one specified by an underscore in the label.</p>
     *
     *  <p>The <code>mnemonicIndexFunction</code> property is good for handling formatting,
     *  localization, and platform independence.</p>
     *
     *  <p>The mnemonic index function must take a single argument which is the item
     *  in the data provider and return an int.</p>
	 *
     *  <pre><code>myMnemonicIndexFunction(item:Object):int</code></pre>
     *
     *  @default "undefined"
     */
    public function get mnemonicIndexFunction():Function
    {
        return _mnemonicIndexFunction;
    }

    /**
     *  @private
     */
    public function set mnemonicIndexFunction(value:Function):void
    {
        if (_mnemonicIndexFunction != value)
        {
            _mnemonicIndexFunction = value;
            mnemonicIndexFieldChanged = true;

            invalidateProperties();

            dispatchEvent(new Event("mnemonicIndexFunctionChanged"));
        }
    }

    //----------------------------------
    //  showRoot
    //----------------------------------

    /**
     *  @private
     *  Storage variable for showRoot flag.
     */
    private var _showRoot:Boolean = true;

    /**
     *  @private
     */
    private var showRootChanged:Boolean = false;

    [Inspectable(category="Data", enumeration="true,false", defaultValue="false")]

    /**
     *  A Boolean flag that specifies whether to display the data provider's
     *  root node.
     *
     *  <p>If the data provider has a root node, and the <code>showRoot</code> property
     *  is set to <code>false</code>, the top-level menu items displayed by the
	 *  FlexNativeMenu control correspond to the immediate descendants of the root node.</p>
     *
     *  <p>This flag has no effect when using a data provider without a root nodes,
     *  such as a List or Array.</p>
     *
     *  @default true
     *  @see #hasRoot
     */
    public function get showRoot():Boolean
    {
        return _showRoot;
    }

    /**
     *  @private
     */
    public function set showRoot(value:Boolean):void
    {
        if (_showRoot != value)
        {
            showRootChanged = true;
            _showRoot = value;
            invalidateProperties();
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------
   	
   	/**
	 *  @copy mx.core.UIComponent#invalidateProperties()
	 */
    public function invalidateProperties():void
    {
        // Don't try to add the object to the display list queue until we've
		// been assigned a nestLevel, or we'll get added at the wrong place in
		// the LayoutManager's priority queue.
		if (!invalidatePropertiesFlag && nestLevel > 0)
		{
			invalidatePropertiesFlag = true;
			if (UIComponentGlobals.mx_internal::layoutManager)
				UIComponentGlobals.mx_internal::layoutManager.invalidateProperties(this);
			else
			{
				var myTimer:Timer = new Timer(100, 1);
				myTimer.addEventListener(TimerEvent.TIMER, validatePropertiesTimerHandler);
				myTimer.start();
			}
		}
    }

    /**
     *  @private
     */
    public function validatePropertiesTimerHandler(event:TimerEvent):void
    {
        validateProperties();
    }

    /**
     *  @inheritDoc
     */
    public function validateProperties():void
    {
        if (invalidatePropertiesFlag)
        {
            commitProperties();

            invalidatePropertiesFlag = false;
        }
    }

    /**
     *  @inheritDoc
     */
    public function validateSize(recursive:Boolean = false):void
    {
    }

    /**
     *  @inheritDoc
     */
    public function validateDisplayList():void
    {
    }

    /**
	 *  Validates and updates the properties and layout of this object
	 *  and redraws it, if necessary.
	 */
	public function validateNow():void
	{
		// Since we don't have commit/measure/layout phases,
		// all we need to do here is the commit phase
		if (invalidatePropertiesFlag)
			validateProperties();
	}

	/**
	 *  Sets the context menu of the InteractiveObject to the underlying native menu.
	 */
	public function setContextMenu(component:InteractiveObject):void
	{
		component.contextMenu = nativeMenu;
		
		if (component is Application)
		{
			var systemManager:ISystemManager = Application(component).systemManager;
			
			if (systemManager is InteractiveObject)
        		InteractiveObject(systemManager).contextMenu = nativeMenu;
		}
	}
	
	/**
	 *  Unsets the context menu of the InteractiveObject that has been set to
	 *  the underlying native menu.
	 */
	public function unsetContextMenu(component:InteractiveObject):void
	{
		component.contextMenu = null;
	}

    /**
     *  Processes the properties set on the component.
	 *
	 *  @see mx.core.UIComponent#commitProperties()
     */
    protected function commitProperties():void
    {
        if (showRootChanged)
        {
            if (!_hasRoot)
                showRootChanged = false;
        }

        if (dataProviderChanged ||showRootChanged ||
        	labelFieldChanged || dataDescriptorChanged)
        {
            var tmpCollection:ICollectionView;

            //reset flags
            dataProviderChanged = false;
            showRootChanged = false;
            labelFieldChanged = false;
            dataDescriptorChanged = false;

            // are we swallowing the root?
            if (mx_internal::_rootModel && !_showRoot && _hasRoot)
            {
                var rootItem:* = mx_internal::_rootModel.createCursor().current;
                if (rootItem != null &&
                    _dataDescriptor.isBranch(rootItem, mx_internal::_rootModel) &&
                    _dataDescriptor.hasChildren(rootItem, mx_internal::_rootModel))
                {
                    // then get rootItem children
                    tmpCollection =
                        _dataDescriptor.getChildren(rootItem, mx_internal::_rootModel);
                }
            }

            // remove all items first.  This is better than creating a new NativeMenu
            // as the root since we have the same reference
            clearMenu(_nativeMenu);

            // make top level items
            if (mx_internal::_rootModel)
            {
                if (!tmpCollection)
                    tmpCollection = mx_internal::_rootModel;
                // not really a default handler, but we need to
                // be later than the wrapper
                tmpCollection.addEventListener(CollectionEvent.COLLECTION_CHANGE,
                                               collectionChangeHandler,
                                               false,
                                               EventPriority.DEFAULT_HANDLER, true);

             	populateMenu(_nativeMenu, tmpCollection);
            }

            dispatchEvent(new Event("nativeMenuChange"));
        }
    }

    /**
     *  Creates a menu and adds appropriate listeners
     *
     *  @private
     */
    private function createMenu():NativeMenu
    {
        var menu:NativeMenu = new NativeMenu();
        // need to do this in the constructor for the root nativeMenu
        menu.addEventListener(Event.DISPLAYING, menuDisplayHandler, false, 0, true);

        return menu;
    }

    /**
     *  Clears out all items in a given menu
     *
     *  @private
     */
    private function clearMenu(menu:NativeMenu):void
    {
        var numItems:int = menu.numItems;
    	for (var i:int = 0; i < numItems; i++)
    	{
    		menu.removeItemAt(0);
    	}
    }

    /**
     *  Populates a menu and the related submenus given a collection
     *
     *  @private
     */
    private function populateMenu(menu:NativeMenu, collection:ICollectionView):NativeMenu
    {
        var collectionLength:int = collection.length;
        for (var i:int = 0; i < collectionLength; i++)
        {
            try
            {
                insertMenuItem(menu, i, collection[i]);
            }
            catch(e:ItemPendingError)
            {
                //we probably dont need to actively recover from here
            }
        }

        return menu;
    }

    /**
     *  Adds the NativeMenuItem to the NativeMenu.  This methods looks at the
     *  properties of the data sent in and sets them properly on the NativeMenuItem.
     *
     *  @private
     */
    private function insertMenuItem(menu:NativeMenu, index:int, data:Object):void
    {
        if (dataProviderChanged)
        {
            commitProperties();
            return;
        }

		var type:String = dataDescriptor.getType(data).toLowerCase();
		var isSeparator:Boolean = (type == "separator");
		
		// label changes later, but separator is read-only so need to know here
		var nativeMenuItem:NativeMenuItem = new NativeMenuItem("", isSeparator);
		
		if (!isSeparator)
		{
			// enabled
			nativeMenuItem.enabled = dataDescriptor.isEnabled(data);
			
			// checked
			nativeMenuItem.checked = type == "check" && dataDescriptor.isToggled(data);
			
			// data
			nativeMenuItem.data = dataDescriptor.getData(data, mx_internal::_rootModel);
			
			// key equivalent
			nativeMenuItem.keyEquivalent = itemToKeyEquivalent(data);
			
			// key equivalent modifiers
			nativeMenuItem.keyEquivalentModifiers = itemToKeyEquivalentModifiers(data);
			
			// label and mnemonic index
			var labelData:String = itemToLabel(data);
			var mnemonicIndex:int = itemToMnemonicIndex(data);
			
			if (mnemonicIndex >= 0)
			{
				nativeMenuItem.label = parseLabelToString(labelData);
				nativeMenuItem.mnemonicIndex = mnemonicIndex;
			}
			else
			{
				nativeMenuItem.label = parseLabelToString(labelData);
				nativeMenuItem.mnemonicIndex = parseLabelToMnemonicIndex(labelData);
			}
			
			// event listeners
			nativeMenuItem.addEventListener(flash.events.Event.SELECT, itemSelectHandler, false, 0, true);
			
			// recursive
			if (dataDescriptor.isBranch(data, mx_internal::_rootModel) &&
				dataDescriptor.hasChildren(data, mx_internal::_rootModel))
			{
				nativeMenuItem.submenu = createMenu();
				populateMenu(nativeMenuItem.submenu,
					dataDescriptor.getChildren(data, mx_internal::_rootModel));
			}
		}
		
		// done!
		menu.addItem(nativeMenuItem);
    }

    /**
     *  @copy flash.display.NativeMenu#display()
     */
     public function display(stage:Stage, x:int, y:int):void
     {
     	nativeMenu.display(stage, x, y);     	
     }

    /**
     *  Returns the key equivalent for the given data object
     *  based on the <code>keyEquivalentField</code> and <code>keyEquivalentFunction</code>
	 *  properties. If the method cannot convert the parameter to a String, it returns an
     *  empty string.
     *
     *  @param data Object to be displayed.
     *
     *  @return The key equivalent based on the data.
     */
    protected function itemToKeyEquivalent(data:Object):String
    {
        if (data == null)
            return "";

        if (keyEquivalentFunction != null)
            return keyEquivalentFunction(data);

        if (data is XML)
        {
            try
            {
                if (data[keyEquivalentField].length() != 0)
                {
                    data = data[keyEquivalentField];
                    return data.toString();
                }

                //if (XMLList(data.@keyEquivalent).length() != 0)
                //{
                //  data = data.@keyEquivalent;
                //}
            }
            catch(e:Error)
            {
            }
        }
        else if (data is Object)
        {
            try
            {
                if (data[keyEquivalentField] != null)
                {
                    data = data[keyEquivalentField];
                    return data.toString();
                }
            }
            catch(e:Error)
            {
            }
        }

        return "";
    }

    /**
     *  Returns the key equivalent modifiers for the given data object
     *  based on the <code>keyEquivalentModifiersFunction</code> property.
     *  If the method cannot convert the parameter to an Array of modifiers,
     *  it returns an empty Array.
     *
     *  @param data Object to be displayed.
     *
     *  @return The array of key equivalent modifiers based on the data
     */
    protected function itemToKeyEquivalentModifiers(data:Object):Array
    {
        if (data == null)
            return [];

        if (keyEquivalentModifiersFunction != null)
            return keyEquivalentModifiersFunction(data);

        return [];
    }

    /**
     *  Returns the String to use as the menu item label for the given data
	 *  object, based on the <code>labelField</code> and <code>labelFunction</code>
	 *  properties.
     *  If the method cannot convert the parameter to a String, it returns a
     *  single space.
     *
     *  @param data Object to be displayed.
     *
     *  @return The string to be displayed based on the data.
     */
    protected function itemToLabel(data:Object):String
    {
        if (data == null)
            return " ";

        if (labelFunction != null)
            return labelFunction(data);

        if (data is XML)
        {
            try
            {
                if (data[labelField].length() != 0)
                    data = data[labelField];

                //if (XMLList(data.@label).length() != 0)
                //{
                //  data = data.@label;
                //}
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
        else if (data is String)
            return String(data);

        try
        {
            return data.toString();
        }
        catch(e:Error)
        {
        }

        return " ";
    }

    /**
     *  Returns the mnemonic index for the given data object
     *  based on the <code>mnemonicIndexField</code> and <code>mnemonicIndexFunction</code>
	 *  properties. If the method cannot convert the parameter to an integer, it returns -1.
     *
     *  @param data Object to be displayed.
     *
     *  @return The mnemonic index based on the data.
     */
    protected function itemToMnemonicIndex(data:Object):int
    {
        if (data == null)
            return -1;

        var mnemonicIndex:int;

        if (mnemonicIndexFunction != null)
            return mnemonicIndexFunction(data);

        if (data is XML)
        {
            try
            {
                if (data[mnemonicIndexField].length() != 0)
                {
                    mnemonicIndex = data[mnemonicIndexField]; // no need for parseInt??
                    return mnemonicIndex;
                }

                //if (XMLList(data.@mnemonicIndex).length() != 0)
                //{
                //  data = data.@mnemonicIndex;
                //}
            }
            catch(e:Error)
            {
            }
        }
        else if (data is Object)
        {
            try
            {
                if (data[mnemonicIndexField] != null)
                {
                    mnemonicIndex = data[mnemonicIndexField];
                    return mnemonicIndex;
                }
            }
            catch(e:Error)
            {
            }
        }

        return -1;
    }

    /**
     *  Determines the actual label to be used for the NativeMenuItem
     *  by removing underscore characters and converting escaped underscore
	 *  characters, if there are any.
     */
    protected function parseLabelToString(data:String):String
    {
    	const singleCharacter:RegExp = new RegExp(MNEMONIC_INDEX_CHARACTER, "g");
    	const doubleCharacter:RegExp = new RegExp(MNEMONIC_INDEX_CHARACTER + MNEMONIC_INDEX_CHARACTER, "g");
    	var dataWithoutEscapedUnderscores:Array = data.split(doubleCharacter);
    	
    	// now need to find lone underscores and remove it
    	var len:int = dataWithoutEscapedUnderscores.length;
    	for(var i:int = 0; i < len; i++)
    	{
    		var str:String = String(dataWithoutEscapedUnderscores[i]);
    		dataWithoutEscapedUnderscores[i] = str.replace(singleCharacter, "");
    	}
    	
    	return dataWithoutEscapedUnderscores.join(MNEMONIC_INDEX_CHARACTER);
    }

    /**
     *  Extracts the mnemonic index from a label based on the presence of
	 *  an underscore character. It finds the leading underscore character if
     *  there is one and uses that as the index.
     */
    protected function parseLabelToMnemonicIndex(data:String):int
    {
    	const doubleCharacter:RegExp = new RegExp(MNEMONIC_INDEX_CHARACTER + MNEMONIC_INDEX_CHARACTER, "g");
        var dataWithoutEscapedUnderscores:Array = data.split(doubleCharacter);
    	
    	// now need to find first underscore
    	var len:int = dataWithoutEscapedUnderscores.length;
    	var strLengthUpTo:int = 0; // length of string accumulator
    	for(var i:int = 0; i < len; i++)
    	{
    		var str:String = String(dataWithoutEscapedUnderscores[i]);
    		var index:int = str.indexOf(MNEMONIC_INDEX_CHARACTER);
    		
    		if (index >= 0)
    			return index + strLengthUpTo;
    		
    		strLengthUpTo += str.length + MNEMONIC_INDEX_CHARACTER.length;
    	}
    	
    	return -1;
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------
 	
 	/**
     *  @private
     */
    private function itemSelectHandler(event:Event):void
    {
        var nativeMenuItem:NativeMenuItem = event.target as NativeMenuItem;

        var type:String = dataDescriptor.getType(nativeMenuItem.data).toLowerCase();
        if (type == "check")
        {
        	var checked:Boolean = !dataDescriptor.isToggled(nativeMenuItem.data);
        	nativeMenuItem.checked = checked;
        	dataDescriptor.setToggled(nativeMenuItem.data, checked);
        	// this causes an update event which ends up re-creating
        	// the whole menu... (SDK-13109)
        }

        var menuEvent:FlexNativeMenuEvent = new FlexNativeMenuEvent(FlexNativeMenuEvent.ITEM_CLICK);
        menuEvent.nativeMenu = nativeMenuItem.menu;
        menuEvent.index = nativeMenuItem.menu.getItemIndex(nativeMenuItem);
        menuEvent.nativeMenuItem = nativeMenuItem;
        menuEvent.label = nativeMenuItem.label;
        menuEvent.item = nativeMenuItem.data;
        dispatchEvent(menuEvent);
    }

    /**
     *  @private
     */
    private function menuDisplayHandler(event:Event):void
    {
        var nativeMenu:NativeMenu = event.target as NativeMenu;

        var menuEvent:FlexNativeMenuEvent = new FlexNativeMenuEvent(FlexNativeMenuEvent.MENU_SHOW);
        menuEvent.nativeMenu = nativeMenu;
        dispatchEvent(menuEvent);
    }
 	
 	/**
     *  @private
     */
    private function collectionChangeHandler(ce:CollectionEvent):void
    {
        //trace("[FlexNativeMenu] caught Model changed");
        if (ce.kind == CollectionEventKind.ADD)
        {
            dataProviderChanged = true;
            invalidateProperties();
            // should handle elegantly with better performance
            //trace("[FlexNativeMenu] add event");
        }
        else if (ce.kind == CollectionEventKind.REMOVE)
        {
            dataProviderChanged = true;
            invalidateProperties();
            // should handle elegantly with better performance
            //trace("[FlexNativeMenu] remove event at:", ce.location);
        }
        else if (ce.kind == CollectionEventKind.REFRESH)
        {
            dataProviderChanged = true;
            dataProvider = dataProvider; //start over
            invalidateProperties();
            //trace("[FlexNativeMenu] refresh event");
        }
        else if (ce.kind == CollectionEventKind.RESET)
        {
            dataProviderChanged = true;
            invalidateProperties();
            //trace("[FlexNativeMenu] reset event");
        }
        else if (ce.kind == CollectionEventKind.UPDATE)
        {
         	dataProviderChanged = true;
            invalidateProperties();
            // should handle elegantly with better performance
            // but can't right now
            //trace("[FlexNativeMenu] update event");
        }
    }
}

}
