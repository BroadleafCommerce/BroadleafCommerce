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
import flash.events.Event;
import flash.events.FocusEvent;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.geom.Point;
import flash.geom.Rectangle;
import flash.ui.Keyboard;
import flash.xml.XMLNode;
import mx.collections.ArrayCollection;
import mx.collections.ICollectionView;
import mx.collections.XMLListCollection;
import mx.collections.errors.ItemPendingError;
import mx.containers.ApplicationControlBar;
import mx.controls.menuClasses.IMenuBarItemRenderer;
import mx.controls.menuClasses.IMenuDataDescriptor;
import mx.controls.menuClasses.MenuBarItem;
import mx.controls.treeClasses.DefaultDataDescriptor;
import mx.core.ClassFactory;
import mx.core.EventPriority;
import mx.core.FlexVersion;
import mx.core.IFactory;
import mx.core.IFlexDisplayObject;
import mx.core.IUIComponent;
import mx.core.mx_internal;
import mx.core.UIComponent;
import mx.core.UIComponentGlobals;
import mx.events.CollectionEvent;
import mx.events.CollectionEventKind;
import mx.events.FlexEvent;
import mx.events.MenuEvent;
import mx.managers.IFocusManagerComponent;
import mx.managers.ISystemManager;
import mx.styles.CSSStyleDeclaration;
import mx.styles.ISimpleStyleClient;
import mx.styles.StyleManager;
import mx.styles.StyleProxy;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when selection changes as a result of user 
 *  interaction.  
 *  This event is also dispatched when the user changes 
 *  the current menu selection in a pop-up submenu. 
 *  When the event occurs on the menu bar, 
 *  the <code>menu</code> property of the MenuEvent object is <code>null</code>.
 *  When it occurs in a pop-up submenu, the <code>menu</code> property 
 *  contains a reference to the Menu object that represents the 
 *  the pop-up submenu.
 *
 *  @eventType mx.events.MenuEvent.CHANGE 
 */
[Event(name="change", type="mx.events.MenuEvent")]

/**
 *  Dispatched when the user selects an item in a pop-up submenu.
 *
 *  @eventType mx.events.MenuEvent.ITEM_CLICK 
 */
[Event(name="itemClick", type="mx.events.MenuEvent")]

/**
 *  Dispatched when a pop-up submenu closes.
 *
 *  @eventType mx.events.MenuEvent.MENU_HIDE 
 */
[Event(name="menuHide", type="mx.events.MenuEvent")]

/**
 *  Dispatched when a pop-up submenu opens, or the 
 *  user selects a menu bar item with no drop-down menu.
 *
 *  @eventType mx.events.MenuEvent.MENU_SHOW 
 */
[Event(name="menuShow", type="mx.events.MenuEvent")]

/**
 *  Dispatched when the mouse pointer rolls out of a menu item.
 *
 *  @eventType mx.events.MenuEvent.ITEM_ROLL_OUT
 */
[Event(name="itemRollOut", type="mx.events.MenuEvent")]

/**
 *  Dispatched when the mouse pointer rolls over a menu item.
 *
 *  @eventType mx.events.MenuEvent.ITEM_ROLL_OVER
 */
[Event(name="itemRollOver", type="mx.events.MenuEvent")]

//--------------------------------------
//  Styles
//--------------------------------------

include "../styles/metadata/FocusStyles.as"
include "../styles/metadata/LeadingStyle.as"
include "../styles/metadata/SkinStyles.as"
include "../styles/metadata/TextStyles.as"

/**
 *  Alpha level of the color defined by the <code>backgroundColor</code>
 *  property.
 *  Valid values range from 0.0 to 1.0.
 *  @default 1.0
 */
[Style(name="backgroundAlpha", type="Number", inherit="no", deprecatedReplacement="menuStyleName", deprecatedSince="3.0")]

/**
 *  Background color of the component.
 *  The default value is <code>undefined</code>, which means it is not 
 *  set and the component has a transparent background.
 *
 *  <p>The default skins of most Flex controls are partially transparent. 
 *  As a result, the background color of a container partially "bleeds through" 
 *  to controls that are in that container. 
 *  You can avoid this by setting the alpha values of the control's 
 *  <code>fillAlphas</code> property to 1, as the following example shows:
 *  </p>
 *  <pre>
 *  &lt;mx:<i>Container</i> backgroundColor="0x66CC66"/&gt;
 *      &lt;mx:<i>ControlName</i> ... fillAlphas="[1,1]"/&gt;
 *  &lt;/mx:<i>Container</i>&gt;
 *  </pre>
 */
[Style(name="backgroundColor", type="uint", format="Color", inherit="no", deprecatedReplacement="menuStyleName", deprecatedSince="3.0")]

/**
 *  The background skin of the MenuBar control. 
 *   
 *  @default mx.skins.halo.MenuBarBackgroundSkin 
 */
[Style(name="backgroundSkin", type="Class", inherit="no")]

/**
 *  The default skin for a MenuBar item
 * 
 *  @default mx.skins.halo.ActivatorSkin 
 */
[Style(name="itemSkin", type="Class", inherit="no", states="up, over, down")]

/**
 *  The skin when a MenuBar item is not selected.
 * 
 *  @default mx.skins.halo.ActivatorSkin
 */
[Style(name="itemUpSkin", type="Class", inherit="no")]

/**
 *  The skin when focus is over a MenuBar item either. 
 * 
 *  @default mx.skins.halo.ActivatorSkin
 */
[Style(name="itemOverSkin", type="Class", inherit="no")]

/**
 *  The skin when a MenuBar item is selected. 
 * 
 *  @default mx.skins.halo.ActivatorSkin 
 */
[Style(name="itemDownSkin", type="Class", inherit="no")]

/**
 *  Name of the CSSStyleDeclaration that specifies the styles for
 *  the Menu controls displayed by this MenuBar control. 
 *  By default, the Menu controls use the MenuBar control's
 *  inheritable styles. 
 *  
 *  <p>You can use this class selector to set the values of all the style properties 
 *  of the Menu class, including <code>backgroundAlpha</code> and <code>backgroundColor</code>.</p>
 * 
 *  @default undefined
 */
[Style(name="menuStyleName", type="String", inherit="no")]


/**
 *  @copy mx.controls.Menu#style:rollOverColor
 */
[Style(name="rollOverColor", type="uint", format="Color", inherit="yes")]

/**
 *  @copy mx.controls.Menu#style:selectionColor
 */
[Style(name="selectionColor", type="uint", format="Color", inherit="yes")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[AccessibilityClass(implementation="mx.accessibility.MenuBarAccImpl")]

[DefaultBindingProperty(destination="dataProvider")]

[DefaultProperty("dataProvider")]

[DefaultTriggerEvent("change")]

[IconFile("MenuBar.png")]

[RequiresDataBinding(true)]

/**
 *  A MenuBar control defines a horizontal, top-level menu bar that contains
 *  one or more menus. Clicking on a top-level menu item opens a pop-up submenu
 *  that is an instance of the Menu control.
 *
 *  <p>The top-level menu bar of the MenuBar control is generally always visible. 
 *  It is not intended for use as a pop-up menu. The individual submenus
 *  pop up as the user selects them with the mouse or keyboard. Open submenus 
 *  disappear when a menu item is selected, or if the menu is dismissed by the
 *  user clicking outside the menu.</p>
 *
 *  <p>For information and an example on the attributes that you can use 
 *  in the data provider for the MenuBar control, see the Menu control.</p>
 *
 *  <p>The MenuBar control has the following sizing characteristics:
 *  </p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>The width is determined from the menu text, with a 
 *               minimum value of 27 pixels for the width. The default 
 *               value for the height is 22 pixels.</td>
 *        </tr>
 *     </table>
 *
 *  @mxml
 *  <p>
 *  The <code>&lt;mx:MenuBar&gt</code> tag inherits all of the tag attributes of its superclass, and
 *  adds the following tag attributes:
 *  </p>
 *  
 *  <pre>
 *  &lt;mx:MenuBar
 *    <b>Properties</b>
 *    dataDescriptor="<i>mx.controls.treeClasses.DefaultDataDescriptor</i>"
 *    dataProvider="<i>undefined</i>"
 *    iconField="icon"
 *    labelField="label"
 *    labelFunction="<i>undefined</i>"
 *    menuBarItemRenderer="<i>mx.controls.menuClasses.MenuBarItem</i>"
 *    menuBarItems="[]"
 *    menus="[]"
 *    selectedIndex="-1"
 *    showRoot="true"
 *  
 *    <b>Styles</b>
 *    backgroundSkin="mx.skins.halo.MenuBarBackgroundSkin"
 *    borderColor="0xAAB3B3"
 *    color="0x0B333C"
 *    cornerRadius="0"
 *    disabledColor="0xAAB3B3"
 *    fillAlphas="[0.6,0.4]"
 *    fillColors="[0xFFFFFF, 0xCCCCCC]"
 *    focusAlpha="0.5"
 *    focusRoundedCorners="tl tr bl br"
 *    fontAntiAliasType="advanced|normal"
 *    fontFamily="Verdana"
 *    fontGridFitType="pixel|none|subpixel"
 *    fontSharpness="0"
 *    fontSize="10"
 *    fontStyle="normal|italic"
 *    fontThickness="0"
 *    fontWeight="normal|bold"
 *    highlightAlphas="[0.3,0.0]"
 *    itemDownSkin="mx.skins.halo.ActivatorSkin"
 *    itemOverSkin="mx.skins.halo.ActivatorSkin"
 *    itemUpSkin="mx.skins.halo.ActivatorSkin"
 *    leading="2"
 *    menuStyleName="<i>No default</i>"
 *    rollOverColor="0xB2E1FF"
 *    selectionColor="0x7FCEFF"
 *    textAlign="left"
 *    textDecoration="none"
 *    textIndent="0"
 *  
 *    <b>Events</b>
 *    itemClick="<i>No default"</i>
 *    itemRollOut="<i>No default"</i>
 *    itemRollOver="<i>No default"</i>
 *    menuHide="<i>No default"</i>
 *    menuShow="<i>No default"</i>
 *  /&gt;
 *  </pre>
 *  </p>
 *
 *  @see mx.controls.Menu
 *  @see mx.controls.PopUpMenuButton
 *  @see mx.controls.menuClasses.IMenuBarItemRenderer
 *  @see mx.controls.menuClasses.MenuBarItem
 *  @see mx.controls.menuClasses.IMenuDataDescriptor
 *  @see mx.controls.treeClasses.DefaultDataDescriptor
 *
 *  @includeExample examples/MenuBarExample.mxml
 *
 */
public class MenuBar extends UIComponent implements IFocusManagerComponent
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private static const MARGIN_WIDTH:int = 10;

    //--------------------------------------------------------------------------
    //
    //  Class mixins
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Placeholder for mixin by MenuBarAccImpl.
     */
    mx_internal static var createAccessibilityImplementation:Function;

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function MenuBar()
    {
        super();
        menuBarItemRenderer = new ClassFactory(MenuBarItem);
        tabChildren = false;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Storage variable for the original dataProvider
     */
    mx_internal var _rootModel:ICollectionView;

    /**
     *  @private
     */
    private var isDown:Boolean;

    /**
     *  @private
     */
    private var inKeyDown:Boolean = false;

    /**
     *  @private
     */
    private var background:IFlexDisplayObject;

    /**
     *  @private
     *  This menu bar could be inside an ApplicationControlBar (ACB).
     */
    private var isInsideACB:Boolean = false;

    /**
     *  @private
     */
    private var supposedToLoseFocus:Boolean = false;

    /**
     *  @private
     */
    private var dataProviderChanged:Boolean = false;
    
    /**
     *  @private
     */
    private var iconFieldChanged:Boolean = false;
    
        /**
     *  @private
     */
    private var menuBarItemRendererChanged:Boolean = false;

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
     *  The baselinePosition of a MenuBar is calculated
     *  for its first MenuBarItem.
     */
    override public function get baselinePosition():Number
    {
        if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
            return super.baselinePosition;
        
        if (!validateBaselinePosition())
            return NaN;

        if (menuBarItems.length == 0)
            return super.baselinePosition;
        
        var menuBarItem0:IUIComponent = menuBarItems[0] as IUIComponent;
        if (!menuBarItem0)
            return super.baselinePosition;
            
        validateNow();
        
        return menuBarItem0.y + menuBarItem0.baselinePosition;      
    }

    //--------------------------------------------------------------------------
    // enabled
    //--------------------------------------------------------------------------

    [Inspectable(category="General", enumeration="true,false", defaultValue="true")]

    /**
     *  @private
     */
    override public function set enabled(value:Boolean):void
    {
        super.enabled = value;

        if (menuBarItems)
        {
            var n:int = menuBarItems.length;
            for (var i:int = 0; i < n; i++)
            {
                menuBarItems[i].enabled = value;
            }
        }
    }

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
    //  dataDescriptor
    //----------------------------------

    /**
     *  @private
     */
    mx_internal var _dataDescriptor:IMenuDataDescriptor =
        new DefaultDataDescriptor();

    [Inspectable(category="Data")]

    /**
     *  The object that accesses and manipulates data in the data provider. 
     *  The MenuBar control delegates to the data descriptor for information 
     *  about its data. This data is then used to parse and move about the 
     *  data source. The data descriptor defined for the MenuBar is used for
     *  all child menus and submenus. 
     * 
     *  <p>When you specify this property as an attribute in MXML, you must
     *  use a reference to the data descriptor, not the string name of the
     *  descriptor. Use the following format for setting the property:</p>
     *
     * <pre>&lt;mx:MenuBar id="menubar" dataDescriptor="{new MyCustomDataDescriptor()}"/&gt;</pre>
     *
     *  <p>Alternatively, you can specify the property in MXML as a nested
     *  subtag, as the following example shows:</p>
     *
     *  <pre>&lt;mx:MenuBar&gt;
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
        
        //force all the menus to be re-created with the new dataDescriptor
        menus = [];
    }
    
    //----------------------------------
    //  dataProvider
    //----------------------------------

    [Bindable("collectionChange")]
    [Inspectable(category="Data")]

    /**
     *  The hierarchy of objects that are displayed as MenuBar items and menus. 
     *  The top-level children all become MenuBar items, and their children 
     *  become the items in the menus and submenus. 
     * 
     *  The MenuBar control handles the source data object as follows:
     *  <p>
     *  <ul>
     *  <li>A String containing valid XML text is converted to an XML object.</li>
     *  <li>An XMLNode is converted to an XML object.</li>
     *  <li>An XMLList is converted to an XMLListCollection.</li>
     *  <li>Any object that implements the ICollectionView interface is cast to
     *  an ICollectionView.</li>
     *  <li>An Array is converted to an ArrayCollection.</li>
     *  <li>Any other type object is wrapped in an Array with the object as its sole
     *  entry.</li>
     *  </ul>
     *  </p>
     * 
     *  @default "undefined"
     */
    public function get dataProvider():Object
    {
        if (_rootModel)
        {   
            return _rootModel;
        }
        else return null;
    }

    /**
     *  @private
     */
    public function set dataProvider(value:Object):void
    {
        if (_rootModel)
        {
            _rootModel.removeEventListener(CollectionEvent.COLLECTION_CHANGE, 
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
            _rootModel = new XMLListCollection(xl);
        }
        //if already a collection dont make new one
        else if (value is ICollectionView)
        {
            _rootModel = ICollectionView(value);
            if (_rootModel.length == 1)
                _hasRoot = true;
        }
        else if (value is Array)
        {
            _rootModel = new ArrayCollection(value as Array);
        }
        //all other types get wrapped in an ArrayCollection
        else if (value is Object)
        {
            _hasRoot = true;
            // convert to an array containing this one item
            var tmp:Array = [];
            tmp.push(value);
            _rootModel = new ArrayCollection(tmp);
        }
        else
        {
            _rootModel = new ArrayCollection();
        }
        //add listeners as weak references
        _rootModel.addEventListener(CollectionEvent.COLLECTION_CHANGE,
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
    mx_internal var _hasRoot:Boolean = false;

    /**
     *  @copy mx.controls.Menu#hasRoot
     */
    public function get hasRoot():Boolean
    {
        return _hasRoot;
    }

    //----------------------------------
    //  iconField
    //----------------------------------

    /**
     *  @private
     *  Storage for iconField property.
     */
    private var _iconField:String = "icon";

    [Bindable("iconFieldChanged")]
    [Inspectable(category="Other", defaultValue="icon")]

    /**
     *  The name of the field in the data provider that determines the 
     *  icon to display for each menu item. By default, the MenuBar does not 
     *  try to display icons along with the text in a menu item. By specifying 
     *  an icon field, you can define a graphic that is created 
     *  and displayed as an icon for a menu item. 
     *
     *  <p>The MenuItemRenderer examines 
     *  the data provider for a property of the name defined 
     *  by the <code>iconField</code> property.  If the value of the property is a Class, it 
     *  instantiates that class and expects it to be an instance of 
     *  IFlexDisplayObject. If the value of the property is a String, it 
     *  looks to see if a Class exists with that name in the application, and if 
     *  it cannot find one, it looks for a property on the document 
     *  with that name and expects that property to map to a Class.</p>
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
        if (_iconField != value)
        {
            iconFieldChanged = true;
            _iconField = value;
            invalidateProperties();
            dispatchEvent(new Event("iconFieldChanged"));
        }
    }

    //----------------------------------
    //  labelField
    //----------------------------------

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
     *  For example, use @label to specify the label attribute in an E4X XML Object 
     *  as the text to display for each menu item. 
     * 
     *  Setting the <code>labelFunction</code> property overrides this property.
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
            dispatchEvent(new Event("labelFieldChanged"));
        }
    }

    //----------------------------------
    //  labelFunction
    //----------------------------------

    [Inspectable(category="Data")]

    /**
     *  The function that determines the text to display for each menu item.
     *  The label function must find the appropriate field or fields in the 
     *  data provider and return a displayable string.
     * 
     *  If you omit this property, Flex uses the contents of the field or
     *  attribute specified by the <code>labelField</code> property.
     *  If you specify this property, Flex ignores any <code>labelField</code>
     *  property value.
     *
     *  The <code>labelFunction</code> property is good for handling formatting
     *  and localization.
     *
     *  <p>The label function must take a single argument which is the item
     *  in the data provider and return a String.</p>
     *  <pre>
     *  <code>myLabelFunction(item:Object):String</code> </pre>
     *
     *  @default "undefined"
     */
    public var labelFunction:Function;

    //----------------------------------
    //  menuBarItemRenderer
    //----------------------------------

    /**
     *  @private
     *  Storage for the menuBarItemRenderer property.
     */
    private var _menuBarItemRenderer:IFactory;

    [Bindable("menuBarItemRendererChanged")]
    [Inspectable(category="Data")]

     /**
     *  The item renderer used by the MenuBar control for 
     *  the top-level menu bar of the MenuBar control. 
     * 
     *  <p>You can define an item renderer for the pop-up submenus 
     *  of the MenuBar control. 
     *  Because each pop-up submenu is an instance of the Menu control, 
     *  you use the class MenuItemRenderer to define an item renderer 
     *  for the pop-up submenus. 
     *  To set the item renderer for a pop-up submenu, access the Menu object using 
     *  the <code>menus</code> property. </p>
     *
     *  @default "mx.controls.menuClasses.MenuBarItem"
     * 
     *  @see mx.controls.menuClasses.MenuBarItem
     */
    public function get menuBarItemRenderer():IFactory
    {
        return _menuBarItemRenderer;
    }

    /**
     * @private 
     */
    public function set menuBarItemRenderer(value:IFactory):void
    {
        if (_menuBarItemRenderer != value)
        {
            _menuBarItemRenderer = value;
        
            menuBarItemRendererChanged = true;
            invalidateProperties();
            dispatchEvent(new Event("menuBarItemRendererChanged"));
        }
    }

    //----------------------------------
    //  menuBarItems
    //----------------------------------

    /**
     *  An Array that contains the MenuBarItem objects that render 
     *  each item in the top-level menu bar of a MenuBar control. By default, 
     *  this property contains instances of the MenuBarItem class. 
     * 
     *  Items should not be added directly to the <code>menuBarItems</code> array. To 
     *  add new menubar items, add them directly to the MenuBar control's 
     *  data provider. 
     * 
     *  @default [ ]
     * 
     *  @see mx.controls.menuClasses.MenuBarItem
     */
    public var menuBarItems:Array = [];

    //----------------------------------
    //  menuBarItemStyleFilters
    //----------------------------------

    /**
     *  The set of styles to pass from the MenuBar to the menuBar items.
     *  @see mx.styles.StyleProxy
     *  @review
     */
    protected function get menuBarItemStyleFilters():Object
    {
        return _menuBarItemStyleFilters;
    }
    
    private static var _menuBarItemStyleFilters:Object = null;

    //----------------------------------
    //  menus
    //----------------------------------

    /**
     *  An Array containing the Menu objects corresponding to the 
     *  pop-up submenus of this MenuBar control.
     *  Each MenuBar item can have a corresponding Menu object in the Array,
     *  even if the item does not have a pop-up submenu.
     *  Flex does not initially populate the <code>menus</code> array;
     *  instead, it creates the menus dynamically, as needed. 
     * 
     *  Items should not be added directly to the <code>menus</code> Array. To 
     *  add new drop-down menus, add directly to the MenuBar 
     *  control's data provider.
     * 
     *  @default [ ] 
     */
    public var menus:Array = [];

    //----------------------------------
    //  selectedIndex
    //----------------------------------

    /**
     *  @private
     *  The index of the currently open menu item, or -1 if none is open.
     */
    private var openMenuIndex:int = -1;
    
    [Bindable("valueCommit")]
    [Inspectable(category="General", defaultValue="-1")]

    /**
     *  The index in the MenuBar control of the currently open Menu 
     *  or the last opened Menu if none are currently open.    
     *  
     *  @default -1
     */
    public function get selectedIndex():int
    {
        return openMenuIndex;
    }
    
    /**
     *  @private
     */
    public function set selectedIndex(value:int):void
    {
        openMenuIndex = value;
        dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));
    }

    //----------------------------------
    //  showRoot
    //----------------------------------

    /**
     *  @private
     *  Storage variable for showRoot flag.
     */
    mx_internal var _showRoot:Boolean = true;

    /**
     *  @private
     */
    mx_internal var showRootChanged:Boolean = false;
    
    [Inspectable(category="Data", enumeration="true,false", defaultValue="false")]

    /**
     *  A Boolean flag that specifies whether to display the data provider's 
     *  root node.
     *
     *  If the data provider has a root node, and the <code>showRoot</code> property 
     *  is set to <code>false</code>, the items on the MenuBar control correspond to
     *  the immediate descendants of the root node.  
     * 
     *  This flag has no effect on data providers without root nodes, 
     *  like Lists and Arrays. 
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
    
    //------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function initializeAccessibility():void
    {
        if (MenuBar.createAccessibilityImplementation != null)
            MenuBar.createAccessibilityImplementation(this);
    }


    /**
     *  @private
     */
    override protected function createChildren():void
    {
        super.createChildren();

        // Check if this MenuBar is inside an ACB.
        for (var p:Object = parent; p; p = p.parent)
        {
            if (p is ApplicationControlBar)
            {
                isInsideACB = true;
                break;
            }
        }

        updateBackground();
    }

    /**
     *  Updates the MenuBar control's background skin. 
     * 
     *  This method is called when MenuBar children are created or when 
     *  any styles on the MenuBar changes. 
     */
    protected function updateBackground():void
    {
        if (isInsideACB)
        {
            // draw translucent menubar
            setStyle("translucent", true);
        }
        else
        {
            // Remove existing background
            if (background)
            {
                removeChild(DisplayObject(background));
                background = null;
            }
            
            var backgroundSkinClass:Class = getStyle("backgroundSkin");
            background = new backgroundSkinClass();
            if (background is ISimpleStyleClient)
                ISimpleStyleClient(background).styleName = this;
            addChildAt(DisplayObject(background), 0);
        }
    }

    /**
     *  @private
     */
    override protected function commitProperties():void
    {
        var i:int;
        
        if (showRootChanged)
        {
            if (!_hasRoot)
                showRootChanged = false;            
        }

        if (dataProviderChanged || showRootChanged)
        {
            var tmpCollection:ICollectionView;
            
            //reset flags 
            dataProviderChanged = false;
            showRootChanged = false;
        
            // are we swallowing the root?
            if (_rootModel && !_showRoot && _hasRoot)
            {
                var rootItem:* = _rootModel.createCursor().current;
                if (rootItem != null &&
                    _dataDescriptor.isBranch(rootItem, _rootModel) &&
                    _dataDescriptor.hasChildren(rootItem, _rootModel))
                {
                    // then get rootItem children
                    tmpCollection = 
                        _dataDescriptor.getChildren(rootItem, _rootModel);
                }
            }
            //make top level items
            removeAll();
            if (_rootModel)
            {
                if (!tmpCollection)
                    tmpCollection = _rootModel;
                // not really a default handler, but we need to 
                // be later than the wrapper
                tmpCollection.addEventListener(CollectionEvent.COLLECTION_CHANGE,
                                                  collectionChangeHandler,
                                                  false,
                                                  EventPriority.DEFAULT_HANDLER, true);
                                          
                var collectionLength:int = tmpCollection.length;
                for (i = 0; i < collectionLength; i++)
                {
                    try
                    {
                        insertMenuBarItem(i, tmpCollection[i]);
                    }
                    catch(e:ItemPendingError)
                    {
                        //we probably dont need to actively recover from here
                    }
                }
            }
        }
        
        if (iconFieldChanged || menuBarItemRendererChanged)
        {
            //reset flag
            iconFieldChanged = false;
            menuBarItemRendererChanged = false;
            
            removeAll();
            if (_rootModel)
            {
                if (!tmpCollection)
                    tmpCollection = _rootModel;
                
                for (i = 0; i < tmpCollection.length; i++)
                {
                    try
                    {
                        insertMenuBarItem(i, tmpCollection[i]);
                    }
                    catch(e:ItemPendingError)
                    {
                        //we probably dont need to actively recover from here
                    }
                }
            }
        }
        
        super.commitProperties();
    }

    /**
     *  Calculates the preferred width and height of the MenuBar based on the
     *  default widths of the MenuBar items. 
     */
    override protected function measure():void
    {
        super.measure();

        var len:int = menuBarItems.length;

        measuredWidth = 0;

        // measured height is at least 22
        measuredHeight = DEFAULT_MEASURED_MIN_HEIGHT; 
        for (var i:int = 0; i < len; i++)
        {
            measuredWidth += menuBarItems[i].getExplicitOrMeasuredWidth();
            measuredHeight = Math.max(
                    measuredHeight, menuBarItems[i].getExplicitOrMeasuredHeight());
        }

        if (len > 0)
            measuredWidth += 2 * MARGIN_WIDTH;
        else // else give it a default width, MARGIN_WIDTH = 10.
            measuredWidth = DEFAULT_MEASURED_MIN_WIDTH; // setting it slightly more than the width

        measuredMinWidth = measuredWidth;
        measuredMinHeight = measuredHeight;
    }

    /**
     *  @private
     *  Sizes and positions the items on the MenuBar.
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        var lastX:Number = MARGIN_WIDTH;
        var lastW:Number = 0;
        var len:int = menuBarItems.length;

        var clipContent:Boolean = false;
        var hideItems:Boolean = (unscaledWidth == 0 || unscaledHeight == 0);

        for (var i:int = 0; i < len; i++)
        {
            var item:IMenuBarItemRenderer = menuBarItems[i];

            item.setActualSize(item.getExplicitOrMeasuredWidth(), unscaledHeight);
            item.visible = !hideItems;

            lastX = item.x = lastX+lastW;
            lastW = item.width;

            if (!hideItems &&
                (item.getExplicitOrMeasuredHeight() > unscaledHeight ||
                 (lastX + lastW) > unscaledWidth))
            {
                clipContent = true;
            }
        }

        if (background)
        {
            background.setActualSize(unscaledWidth, unscaledHeight);
            background.visible = !hideItems;
        }

        // Set a scroll rect to handle clipping.
        scrollRect = clipContent ? new Rectangle(0, 0,
                unscaledWidth, unscaledHeight) : null;
    }

    //------------------------------------------------------------------------
    //  Focus handling
    //------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function focusInHandler(event:FocusEvent):void
    {
        super.focusInHandler(event);
    }

    /**
     *  @private
     */
    override protected function focusOutHandler(event:FocusEvent):void
    {
        super.focusOutHandler(event);
        
        if (supposedToLoseFocus)
            getMenuAt(openMenuIndex).hide();
        
        supposedToLoseFocus = false;
    }

    //------------------------------------------------------------------------
    //  Support for setStyle
    //------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function styleChanged(styleProp:String):void
    {
        var i:int;
        super.styleChanged(styleProp);

        var n:int = menuBarItems.length;
        for (i = 0; i < n; i++)
        {
            getMenuAt(i).styleChanged(styleProp);
        }

        if (!styleProp || styleProp == "" || styleProp == "backgroundSkin")
        {
            updateBackground();
        }
        
        if (styleProp == null ||
            styleProp == "styleName" ||
            styleProp == "menuStyleName")
        {
            var menuStyleName:String = getStyle("menuStyleName");
            var m:Menu;
            if (menuStyleName)
            {
                if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
                {
                    var styleDecl:CSSStyleDeclaration =
                        StyleManager.getStyleDeclaration("." + menuStyleName);
         
                    if (styleDecl)
                    {
                        for (i = 0; i < menus.length; i++)
                        {
                            m = menus[i];
                            m.styleDeclaration = styleDecl;
                            m.regenerateStyleCache(true);
                        }
                    }
                 }
                 else
                 {
                    for (i = 0; i < menus.length; i++)
                    {
                        m = menus[i];
                        m.styleName = menuStyleName;
                    }
                 }
            }
        }
    }

    /**
     *  @private
     */
    override public function notifyStyleChangeInChildren(
                                styleProp:String,
                                recursive:Boolean):void
    {
        super.notifyStyleChangeInChildren(styleProp, recursive);

        var n:int = menuBarItems.length;
        for (var i:int = 0; i < n; i++)
        {
            getMenuAt(i).notifyStyleChangeInChildren(styleProp, recursive);
        }
    }

    //------------------------------------------------------------------------
    //
    //  Methods
    //
    //------------------------------------------------------------------------

    /**
     *  @private
     */
    private function collectionChangeHandler(event:Event):void
    {
        //trace("[MenuBar] caught Model changed");
        if (event is CollectionEvent)
        {
            var ce:CollectionEvent = CollectionEvent(event);
            if (ce.kind == CollectionEventKind.ADD)
            {
                dataProviderChanged = true;
                invalidateProperties();
                //trace("[MenuBar] add event");
            }
            else if (ce.kind == CollectionEventKind.REMOVE)
            {
                dataProviderChanged = true;
                invalidateProperties();
                //trace("[MenuBar] remove event at:", ce.location);
            }
            else if (ce.kind == CollectionEventKind.REFRESH)
            {
                dataProviderChanged = true;
                dataProvider = dataProvider; //start over
                invalidateProperties();
                invalidateSize();
                //trace("[MenuBar] refresh event");
            }
            else if (ce.kind == CollectionEventKind.RESET)
            {
                dataProviderChanged = true;
                invalidateProperties();
                invalidateSize();
                //trace("[MenuBar] reset event");
            }
            else if (ce.kind == CollectionEventKind.UPDATE)
            {
                //As long as there are no open menus, we can
                //handle the update. Otherwise we create new 
                //menubar items and submenus in commitProperties
                //and run the risk of orphaning the already open
                //menu. 
                if (openMenuIndex == -1)
                {
                    dataProviderChanged = true;
                    invalidateProperties();
                }
            }
        }

        //bItemsSizeChanged = true;
        invalidateDisplayList();
    }

    /**
     *  @private
     */
    private function eventHandler(event:Event):void
    {
        //these events come from the menu's themselves. 
        //we'll redispatch all of them. 
        if (event is MenuEvent) 
        {
            var t:String = event.type;
    
            if (event.type == MenuEvent.MENU_HIDE && 
                MenuEvent(event).menu == menus[openMenuIndex])
            {
                menuBarItems[openMenuIndex].menuBarItemState = "itemUpSkin";
                openMenuIndex = -1;
                dispatchEvent(event as MenuEvent);
            }
            else
                dispatchEvent(event);
        }
    }

    /**
     *  @private
     *
     *  Adds a menu to the MenuBar control at the specified location.
     *  An index of 0 inserts the menu at the leftmost spot in the MenuBar.
     *
     *  @param index Index where the menu should be inserted.
     *  @param arg1 May be either:a String, which is the item's label; or an xmlNode.
     *  @param arg2 May be: undefined; a menu; or an xml/xmlNode.
     */
    private function addMenuAt(index:int, arg1:Object, arg2:Object = null):void
    {
        if (!dataProvider)
            dataProvider = {};

        var newMenu:Menu;
        var mdp:Object;
        var newItem:Object = arg1;

        insertMenuBarItem(index, mdp);
    }

    /**
     *  @copy mx.controls.listClasses.ListBase#itemToLabel()
     */
    public function itemToLabel(data:Object):String
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
     *  Returns the class for an icon, if any, for a data item,  
     *  based on the <code>iconField</code> property.
     *  The field in the item can return a string as long as that
     *  string represents the name of a class in the application.
     *  The field in the item can also be a string that is the name
     *  of a variable in the document that holds the class for
     *  the icon.
     *  
     *  @param data The item from which to extract the icon class
     *  @return The icon for the item, as a class reference or 
     *  <code>null</code> if none.
     */
    public function itemToIcon(data:Object):Class
    {
        if (data == null)
        {
            return null;
        }
        var iconClass:Class;
        var icon:*;

        if (data is XML)
        {
            try
            {
                if (data[iconField].length() != 0)
                {
                   icon = String(data[iconField]);
                   if (icon != null)
                   {
                       iconClass =
                            Class(systemManager.getDefinitionByName(icon));
                       if (iconClass)
                           return iconClass;

                       return document[icon];
                   }
                }
            }
            catch(e:Error)
            {
            }
        }

        else if (data is Object)
        {
            try
            {
                if (data[iconField] != null)
                {
                    if (data[iconField] is Class)
                    {
                        return data[iconField];
                    }
                    if (data[iconField] is String)
                    {
                        iconClass = Class(systemManager.getDefinitionByName(
                                                data[iconField]));
                        if (iconClass)
                        {
                            return iconClass;
                        }
                        return document[data[iconField]];
                    }
                }
            }
            catch(e:Error)
            {
            }
        }

        return null;
    }

    //--------------------------------------------------------------------------
    // Activator list management
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function insertMenuBarItem(index:int, mdp:Object):void
    {
        if (dataProviderChanged)
        {
            commitProperties();
            return;
        }

        var item:IMenuBarItemRenderer = menuBarItemRenderer.newInstance();
        item.styleName = new StyleProxy(this, menuBarItemStyleFilters);
        item.visible = false;
        item.enabled = enabled && _dataDescriptor.isEnabled(mdp) != false;
        item.data = mdp;
        item.menuBar = this;
        item.menuBarItemIndex = index;
        addChild(DisplayObject(item));
        menuBarItems.splice(index, 0, item);

        invalidateSize();
        invalidateDisplayList();
        
        item.addEventListener(MouseEvent.MOUSE_OVER, mouseOverHandler);
        item.addEventListener(MouseEvent.MOUSE_DOWN, mouseDownHandler);
        item.addEventListener(MouseEvent.MOUSE_UP, mouseUpHandler);
        item.addEventListener(MouseEvent.MOUSE_OUT, mouseOutHandler);
    }

    /**
     *  Returns a reference to the Menu object at the specified MenuBar item index,  
     *  where 0 is the Menu contained at the leftmost MenuBar item index. 
     *
     *  @param index Index of the Menu instance to return.
     *
     *  @return Reference to the Menu contained at the specified index.
     */
    public function getMenuAt(index:int):Menu
    {
        if (dataProviderChanged)
            commitProperties();

        var item:IMenuBarItemRenderer = menuBarItems[index];
      
        var mdp:Object = item.data;
        var menu:Menu = menus[index];

        if (menu == null)
        {
            menu = new Menu();
            menu.showRoot = false;
            
            if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
                menu.styleName = this;
            
            var menuStyleName:Object = getStyle("menuStyleName");
            if (menuStyleName)
            {
                if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
                {
                    var styleDecl:CSSStyleDeclaration =
                        StyleManager.getStyleDeclaration("." + menuStyleName);
                    if (styleDecl)
                        menu.styleDeclaration = styleDecl;
                }
                else
                {
                    menu.styleName = menuStyleName;
                }
            }
            
            menu.sourceMenuBar = this;
            menu.owner = this;
            menu.addEventListener("menuHide", eventHandler);
            menu.addEventListener("itemRollOver", eventHandler);
            menu.addEventListener("itemRollOut", eventHandler);
            menu.addEventListener("menuShow", eventHandler);
            menu.addEventListener("itemClick", eventHandler);
            menu.addEventListener("change", eventHandler);
            menu.iconField = _iconField;
            menu.labelField = _labelField;
            menu.labelFunction = labelFunction;
            menu.dataDescriptor = _dataDescriptor;
            menu.invalidateSize();

            menus[index] = menu;
            menu.sourceMenuBarItem = item; // menu needs this for a hitTest when clicking outside menu area
            Menu.popUpMenu(menu, this, mdp);
        }

        return menu;
    }

    /**
     *  @private
     *  Show a menuBarItem's menu
     */
    private function showMenu(index:Number):void
    {
        selectedIndex = index;
        var item:IMenuBarItemRenderer = menuBarItems[index];

        // The getMenuAt function will create the Menu if it doesn't
        // already exist.
        var menu:Menu = getMenuAt(index);
        var sm:ISystemManager = systemManager;
        var screen:Rectangle = sm.screen;

        UIComponentGlobals.layoutManager.validateClient(menu, true);

        // popups go on the root of the swf which if loaded, is not
        // necessarily at 0,0 in global coordinates
        var pt:Point = new Point(0, 0);
        pt = DisplayObject(item).localToGlobal(pt);
        // check to see if we'll go offscreen
        if (pt.y + item.height + 1 + menu.getExplicitOrMeasuredHeight() > screen.height + screen.y)
            pt.y -= menu.getExplicitOrMeasuredHeight();
        else
            pt.y += item.height + 1;
        if (pt.x + menu.getExplicitOrMeasuredWidth() > screen.width + screen.x)
            pt.x = screen.x + screen.width - menu.getExplicitOrMeasuredWidth();
        pt = DisplayObject(sm.topLevelSystemManager).globalToLocal(pt);

        // If inside an ACB, slight offset looks much better.
        if (isInsideACB)
            pt.y += 2;

        menu.show(pt.x, pt.y);
    }

    /**
     *  @private
     */
    private function removeMenuBarItemAt(index:int):void
    {
        if (dataProviderChanged)
            commitProperties();

        var item:IMenuBarItemRenderer = menuBarItems[index];

        if (item)
        {
            removeChild(DisplayObject(item));
            menuBarItems.splice(index, 1);
            invalidateSize();
            invalidateDisplayList();
        }
    }


    /**
     *  @private
     */
    private function removeAll():void
    {
        if (dataProviderChanged)
            commitProperties();

        while (menuBarItems.length > 0)
        {
            var item:IMenuBarItemRenderer = menuBarItems[0];
            removeChild(DisplayObject(item));
            menuBarItems.splice(0, 1);
        }

        menus = [];

        invalidateSize();
        invalidateDisplayList();
    }

    //--------------------------------------------------------------------------
    // Mouse Event Handlers
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function mouseOverHandler(event:MouseEvent):void
    {
        var item:IMenuBarItemRenderer = IMenuBarItemRenderer(event.target);
        var index:int = item.menuBarItemIndex;
        var requiresEvent:Boolean = false;
        var m:Menu = getMenuAt(index);
        var evt:MenuEvent;
        if (item.enabled)
        {
            if (openMenuIndex != -1 || inKeyDown)
            {
                var oldIndex:Number = openMenuIndex;
                if (oldIndex != index)
                {
                    // Deactivate the old
                    isDown = false;
                    if (oldIndex != -1)
                    {
                        var oldItem:IMenuBarItemRenderer = menuBarItems[oldIndex];
                        oldItem.dispatchEvent(new MouseEvent(MouseEvent.MOUSE_UP));
                        oldItem.menuBarItemState = "itemUpSkin";
                        
                        //we've effectively rolled out of this item, dispatch the 
                        //correct itemRollOut event
                        evt = new MenuEvent(MenuEvent.ITEM_ROLL_OUT);
                        evt.menuBar = this;
                        evt.index = oldIndex;
                        evt.label = itemToLabel(oldItem.data);
                        evt.item = oldItem.data;
                        evt.itemRenderer = oldItem;
                        dispatchEvent(evt);
                    }

                    // Activate the new
                    item.menuBarItemState = "itemDownSkin";
                    var dp:ICollectionView = ICollectionView(m.dataProvider);
                    
                    //Show only those menus that have children to show
                    if (m.dataDescriptor.isBranch(item.data, item.data) &&
                        m.dataDescriptor.hasChildren(item.data, item.data))
                    {
                        showMenu(index);
                    }   
                    // we still want to dispatch a menuShow event for top-level menuitems with 
                    // no children and update the selectedIndex property
                    else if (m)
                    {
                        selectedIndex = index;
                        
                        evt = new MenuEvent(MenuEvent.MENU_SHOW);
                        evt.menuBar = this;
                        evt.menu = m;
                        dispatchEvent(evt);
                        
                        item.menuBarItemState = "itemOverSkin";
                    }
                    isDown = true;
                    
                    if (m.dataDescriptor.getType(item.data) != "separator")
                    {
                        requiresEvent = true;
                        //fire the change event 
                        evt = new MenuEvent(MenuEvent.CHANGE);
                        evt.index = index;
                        evt.menuBar = this;
                        evt.label = itemToLabel(item.data);
                        evt.item = item.data;
                        evt.itemRenderer = item;
                        dispatchEvent(evt);
                    }
                    
                }
                else
                {
                    var mm:Menu = getMenuAt(index);
                    mm.deleteDependentSubMenus();
                    mm.setFocus();
                }
            }
            else
            {
                item.menuBarItemState = "itemOverSkin";
                isDown = false;
                if (m.dataDescriptor.getType(item.data) != "separator")
                    requiresEvent = true;
            }
            inKeyDown = false;
            
            if (requiresEvent)
            {
                // Fire the appropriate rollover event
                evt = new MenuEvent(MenuEvent.ITEM_ROLL_OVER);
                evt.index = index;
                evt.menuBar = this;
                evt.label = itemToLabel(item.data);
                evt.item = item.data;
                evt.itemRenderer = item;
                dispatchEvent(evt);
            }
        }
    }

    /**
     *  @private
     */
    private function mouseDownHandler(event:MouseEvent):void
    {
        var item:IMenuBarItemRenderer = IMenuBarItemRenderer(event.target);
        var index:int = item.menuBarItemIndex;
        var m:Menu = getMenuAt(index);
        
        if (item.enabled)
        {
            item.menuBarItemState = "itemDownSkin";

            if (!isDown)
            {
                m.supposedToLoseFocus = true;
            
                var dp:ICollectionView = ICollectionView(m.dataProvider)
                //Show only those menus that have children to show
                if (m.dataDescriptor.isBranch(item.data, item.data) &&
                    m.dataDescriptor.hasChildren(item.data, item.data))
                {
                    showMenu(index);
                }
                    
                // we still want to dispatch a menuShow event for top-level menuitems with no children
                // and update the selectedIndex property
                else if (m)
                {
                    selectedIndex = index;
                    
                    var evt:MenuEvent = new MenuEvent(MenuEvent.MENU_SHOW);
                    evt.menuBar = this;
                    evt.menu = m;
                    dispatchEvent(evt);
                }
                    
                isDown = true;
            }
            else
            {
                isDown = false;
            }
            
            if (m.dataDescriptor.getType(item.data) != "separator")
            {
                //fire the change event 
                var menuEvent:MenuEvent = new MenuEvent(MenuEvent.CHANGE);
                menuEvent.index = index;
                menuEvent.menuBar = this;
                menuEvent.label = itemToLabel(item.data);
                menuEvent.item = item.data;
                menuEvent.itemRenderer = item;
                dispatchEvent(menuEvent);
            }
        }
    }

    /**
     *  @private
     */
    private function mouseUpHandler(event:MouseEvent):void
    {
        var item:IMenuBarItemRenderer = IMenuBarItemRenderer(event.target);
        var index:int = item.menuBarItemIndex;

        if (item.enabled && !isDown)
        {
            getMenuAt(index).hideAllMenus();
            item.menuBarItemState = "itemOverSkin";
        }
    }

    /**
     *  @private
     */
    private function mouseOutHandler(event:MouseEvent):void
    {
        var item:IMenuBarItemRenderer = IMenuBarItemRenderer(event.target);
        var index:int = item.menuBarItemIndex;
        var m:Menu = getMenuAt(index);

        if (item.enabled && openMenuIndex != index)
        {
            menuBarItems[index].menuBarItemState = "itemUpSkin";
        }
        // Fire the appropriate rollout event
        if (item.data && 
            (m.dataDescriptor.getType(item.data) != "separator"))
        {
            var menuEvent:MenuEvent = new MenuEvent(MenuEvent.ITEM_ROLL_OUT);
            menuEvent.index = index;
            menuEvent.menuBar = this;
            menuEvent.label = itemToLabel(item.data);
            menuEvent.item = item.data;
            menuEvent.itemRenderer = item;
            dispatchEvent(menuEvent);
        }
    }

    //--------------------------------------------------------------------------
    // Keyboard Navigation Handlers
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
        var barLen:int = menuBarItems.length;

        if (event.keyCode == Keyboard.RIGHT || event.keyCode == Keyboard.LEFT)
        {
            inKeyDown = true;
            var nextIndex:int = openMenuIndex;
            var found:Boolean = false;
            var count:int = 0;
            while (!found && count < barLen)
            {
                count++;
                nextIndex = (event.keyCode == Keyboard.RIGHT) ? nextIndex + 1 : nextIndex - 1;

                if (nextIndex>=barLen)
                    nextIndex = 0;
                else if (nextIndex < 0)
                    nextIndex = barLen - 1;

                if (menuBarItems[nextIndex].enabled)
                    found = true;
            }

            // trigger next item in the bar
            if (count <= barLen && found)
                menuBarItems[nextIndex].dispatchEvent(new MouseEvent(MouseEvent.MOUSE_OVER));

            event.stopPropagation();
        }

        // Handle Keyboard.DOWN Navigation
        if (event.keyCode == Keyboard.DOWN)
        {
            if (openMenuIndex != -1)
            {
                var menu:Menu = getMenuAt(openMenuIndex);
                menu.selectedIndex = 0;
                supposedToLoseFocus = true;
                var dp:ICollectionView = ICollectionView(menu.dataProvider);
                var item:IMenuBarItemRenderer = menu.sourceMenuBarItem;
                
                //focus only those menus that have children to show
                if (menu.dataDescriptor.isBranch(item.data, item.data) && 
                    menu.dataDescriptor.hasChildren(item.data, item.data)) 
                {
                    menu.setFocus();
                }
            }
            event.stopPropagation();
        }
  
        // Handle Keyboard.ENTER/ESCAPE Commands
        if ((event.keyCode == Keyboard.ENTER) || (event.keyCode == Keyboard.ESCAPE))
        {
            if (openMenuIndex != -1)
                getMenuAt(openMenuIndex).hide();
            event.stopPropagation();
        }
    }

}

}
