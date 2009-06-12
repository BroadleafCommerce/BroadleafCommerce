////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2002-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.controls
{

import flash.events.Event;
import flash.events.FocusEvent;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.events.TextEvent;
import flash.geom.Point;
import flash.geom.Rectangle;
import flash.text.TextLineMetrics;
import flash.ui.Keyboard;
import flash.utils.getTimer;
import mx.collections.ArrayCollection;
import mx.collections.CursorBookmark;
import mx.controls.dataGridClasses.DataGridListData;
import mx.controls.listClasses.BaseListData;
import mx.controls.listClasses.IDropInListItemRenderer;
import mx.controls.listClasses.IListItemRenderer;
import mx.controls.listClasses.ListBase;
import mx.controls.listClasses.ListData;
import mx.core.ClassFactory;
import mx.core.FlexVersion;
import mx.core.EdgeMetrics;
import mx.core.IDataRenderer;
import mx.core.IFactory;
import mx.core.ScrollPolicy;
import mx.core.UIComponent;
import mx.core.UIComponentGlobals;
import mx.core.mx_internal;
import mx.effects.Tween;
import mx.events.CollectionEvent;
import mx.events.CollectionEventKind;
import mx.events.DropdownEvent;
import mx.events.FlexEvent;
import mx.events.FlexMouseEvent;
import mx.events.ListEvent;
import mx.events.ScrollEvent;
import mx.events.ScrollEventDetail;
import mx.managers.PopUpManager;
import mx.styles.CSSStyleDeclaration;
import mx.styles.StyleManager;
import mx.styles.StyleProxy;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when the ComboBox contents changes as a result of user
 *  interaction, when the <code>selectedIndex</code> or
 *  <code>selectedItem</code> property changes, and, if the ComboBox control
 *  is editable, each time a keystroke is entered in the box.
 *
 *  @eventType mx.events.ListEvent.CHANGE
 */
[Event(name="change", type="mx.events.ListEvent")]

/**
 *  Dispatched when the drop-down list is dismissed for any reason such when 
 *  the user:
 *  <ul>
 *      <li>selects an item in the drop-down list</li>
 *      <li>clicks outside of the drop-down list</li>
 *      <li>clicks the drop-down button while the drop-down list is 
 *  displayed</li>
 *      <li>presses the ESC key while the drop-down list is displayed</li>
 *  </ul>
 *
 *  @eventType mx.events.DropdownEvent.CLOSE
 */
[Event(name="close", type="mx.events.DropdownEvent")]

/**
 *  Dispatched when the <code>data</code> property changes.
 *
 *  <p>When you use a component as an item renderer,
 *  the <code>data</code> property contains an item from the
 *  dataProvider.
 *  You can listen for this event and update the component
 *  when the <code>data</code> property changes.</p>
 * 
 *  @eventType mx.events.FlexEvent.DATA_CHANGE
 */
[Event(name="dataChange", type="mx.events.FlexEvent")]

/**
 *  Dispatched if the <code>editable</code> property
 *  is set to <code>true</code> and the user presses the Enter key
 *  while typing in the editable text field.
 *
 *  @eventType mx.events.FlexEvent.ENTER
 */
[Event(name="enter", type="mx.events.FlexEvent")]

/**
 *  Dispatched when user rolls the mouse out of a drop-down list item.
 *  The event object's <code>target</code> property contains a reference
 *  to the ComboBox and not the drop-down list.
 *
 *  @eventType mx.events.ListEvent.ITEM_ROLL_OUT
 */
[Event(name="itemRollOut", type="mx.events.ListEvent")]

/**
 *  Dispatched when the user rolls the mouse over a drop-down list item.
 *  The event object's <code>target</code> property contains a reference
 *  to the ComboBox and not the drop-down list.
 *
 *  @eventType mx.events.ListEvent.ITEM_ROLL_OVER
 */
[Event(name="itemRollOver", type="mx.events.ListEvent")]

/**
 *  Dispatched when the user clicks the drop-down button
 *  to display the drop-down list.  It is also dispatched if the user
 *  uses the keyboard and types Ctrl-Down to open the drop-down.
 *
 *  @eventType mx.events.DropdownEvent.OPEN
 */
[Event(name="open", type="mx.events.DropdownEvent")]

/**
 *  Dispatched when the user scrolls the ComboBox control's drop-down list.
 *
 *  @eventType mx.events.ScrollEvent.SCROLL
 */
[Event(name="scroll", type="mx.events.ScrollEvent")]

//--------------------------------------
//  Styles
//--------------------------------------

include "../styles/metadata/FocusStyles.as"
include "../styles/metadata/IconColorStyles.as"
include "../styles/metadata/LeadingStyle.as"
include "../styles/metadata/PaddingStyles.as"
include "../styles/metadata/SkinStyles.as"
include "../styles/metadata/TextStyles.as"

/**
 *  The set of BackgroundColors for drop-down list rows in an alternating
 *  pattern.
 *  Value can be an Array of two of more colors.
 *  If <code>undefined</code> then the rows will use the drop-down list's 
 *  backgroundColor style.
 *
 *  @default undefined
 */
[Style(name="alternatingItemColors", type="Array", arrayType="uint", format="Color", inherit="yes")]

/**
 *  Width of the arrow button in pixels.
 *  @default 22
 */
[Style(name="arrowButtonWidth", type="Number", format="Length", inherit="no")]

/**
 *  The thickness of the border of the drop-down list, in pixels. 
 *  This value is overridden if you define 
 *  <code>borderThickness</code> when setting the 
 *  <code>dropdownStyleName</code> CSSStyleDeclaration. 
 *
 *  @default 1
 */
[Style(name="borderThickness", type="Number", format="Length", inherit="no")]

/**
 *  The length of the transition when the drop-down list closes, in milliseconds.
 *  The default transition has the drop-down slide up into the ComboBox.
 *
 *  @default 250
 */
[Style(name="closeDuration", type="Number", format="Time", inherit="no")]

/**
 *  An easing function to control the close transition.  Easing functions can
 *  be used to control the acceleration and deceleration of the transition.
 *
 *  @default undefined
 */
[Style(name="closeEasingFunction", type="Function", inherit="no")]

/**
 *  The color of the border of the ComboBox.  If <code>undefined</code>
 *  the drop-down list will use its normal borderColor style.  This style
 *  is used by the validators to show the ComboBox in an error state.
 * 
 *  @default undefined
 */
[Style(name="dropdownBorderColor", type="uint", format="Color", inherit="yes")]

/**
 *  The name of a CSSStyleDeclaration to be used by the drop-down list.  This
 *  allows you to control the appearance of the drop-down list or its item
 *  renderers.
 * 
 * [deprecated]
 *
 *  @default "comboDropDown"
 */
[Style(name="dropDownStyleName", type="String", inherit="no", deprecatedReplacement="dropdownStyleName")]

/**
 *  The name of a CSSStyleDeclaration to be used by the drop-down list.  This
 *  allows you to control the appearance of the drop-down list or its item
 *  renderers.
 *
 *  @default "comboDropdown"
 */
[Style(name="dropdownStyleName", type="String", inherit="no")]

/**
 *  Length of the transition when the drop-down list opens, in milliseconds.
 *  The default transition has the drop-down slide down from the ComboBox.
 *
 *  @default 250
 */
[Style(name="openDuration", type="Number", format="Time", inherit="no")]

/**
 *  An easing function to control the open transition.  Easing functions can
 *  be used to control the acceleration and deceleration of the transition.
 *
 *  @default undefined
 */
[Style(name="openEasingFunction", type="Function", inherit="no")]

/**
 *  Number of pixels between the control's bottom border
 *  and the bottom of its content area.
 *  When the <code>editable</code> property is <code>true</code>, 
 *  <code>paddingTop</code> and <code>paddingBottom</code> affect the size 
 *  of the ComboBox control, but do not affect the position of the editable text field.
 *  
 *  @default 0 
 */
[Style(name="paddingBottom", type="Number", format="Length", inherit="no")]

/**
 *  Number of pixels between the control's top border
 *  and the top of its content area.
 *  When the <code>editable</code> property is <code>true</code>, 
 *  <code>paddingTop</code> and <code>paddingBottom</code> affect the size 
 *  of the ComboBox control, but do not affect the position of the editable text field.
 *  
 *  @default 0
 */
[Style(name="paddingTop", type="Number", format="Length", inherit="no")]

/**
 *  The rollOverColor of the drop-down list.

 *  @see mx.controls.List
 */
[Style(name="rollOverColor", type="uint", format="Color", inherit="yes")]

/**
 *  The selectionColor of the drop-down list.

 *  @see mx.controls.List
 */
[Style(name="selectionColor", type="uint", format="Color", inherit="yes")]

/**
 *  The selectionDuration of the drop-down list.
 * 
 *  @default 250
 * 
 *  @see mx.controls.List
 */
[Style(name="selectionDuration", type="uint", format="Time", inherit="no")]

/**
 *  The selectionEasingFunction of the drop-down list.
 * 
 *  @default undefined
 * 
 *  @see mx.controls.List
 */
[Style(name="selectionEasingFunction", type="Function", inherit="no")]

/**
 *  The textRollOverColor of the drop-down list.
 * 
 *  @default #2B333C
 *  
 *  @see mx.controls.List
 */
[Style(name="textRollOverColor", type="uint", format="Color", inherit="yes")]

/**
 *  The textSelectedColor of the drop-down list.
 * 
 *  @default #2B333C
 *  @see mx.controls.List
 */
[Style(name="textSelectedColor", type="uint", format="Color", inherit="yes")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[AccessibilityClass(implementation="mx.accessibility.ComboBoxAccImpl")]

[DataBindingInfo("acceptedTypes", "{ dataProvider: { label: &quot;String&quot; } }")]

[DefaultBindingProperty(source="selectedItem", destination="dataProvider")]

[DefaultProperty("dataProvider")]

[DefaultTriggerEvent("change")]

[IconFile("ComboBox.png")]

/**
 *  The ComboBox control contains a drop-down list
 *  from which the user can select a single value.
 *  Its functionality is very similar to that of the
 *  SELECT form element in HTML.
 *  The ComboBox can be editable, in which case
 *  the user can type entries into the TextInput portion
 *  of the ComboBox that are not in the list.
 *
 *  <p>The ComboBox control has the following default sizing 
 *     characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>Wide enough to accommodate the longest entry in the 
 *               drop-down list in the display area of the main
 *               control, plus the drop-down button. When the 
 *               drop-down list is not visible, the default height 
 *               is based on the label text size. 
 *
 *               <p>The default drop-down list height is five rows, or 
 *               the number of entries in the drop-down list, whichever 
 *               is smaller. The default height of each entry in the 
 *               drop-down list is 22 pixels.</p></td>
 *        </tr>
 *        <tr>
 *           <td>Minimum size</td>
 *           <td>0 pixels.</td>
 *        </tr>
 *        <tr>
 *           <td>Maximum size</td>
 *           <td>5000 by 5000.</td>
 *        </tr>
 *        <tr>
 *           <td>dropdownWidth</td>
 *           <td>The width of the ComboBox control.</td>
 *        </tr>
 *        <tr>
 *           <td>rowCount</td>
 *           <td>5 rows.</td>
 *        </tr>
 *     </table>
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:ComboBox&gt;</code> tag inherits all the tag attributes
 *  of its superclass, and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:ComboBox
 *    <b>Properties</b>
 *    dataProvider="null"
 *    dropdownFactory="<i>ClassFactory that creates an mx.controls.List</i>"
 *    dropdownWidth="<i>100 or width of the longest text in the dataProvider</i>"
 *    itemRenderer="null"
 *    labelField="label"
 *    labelFunction="null"
 *    prompt="null"
 *    rowCount="5"
 *    selectedIndex="-1"
 *    selectedItem="null"
 *    
 *    <b>Styles</b>
 *    alternatingItemColors="undefined"
 *    arrowButtonWidth="22"
 *    borderColor="0xB7BABC"
 *    borderThickness="1"
 *    closeDuration="250"
 *    closeEasingFunction="undefined"
 *    color="0x0B333C"
 *    cornerRadius="0"
 *    disabledColor="0xAAB3B3"
 *    disabledIconColor="0x919999"
 *    dropdownBorderColor="undefined"
 *    dropdownStyleName="comboDropdown"
 *    fillAlphas="[0.6,0.4]"
 *    fillColors="[0xFFFFFF, 0xCCCCCC]"
 *    focusAlpha="0.4"
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
 *    iconColor="0x111111"
 *    leading="0"
 *    openDuration="250"
 *    openEasingFunction="undefined"
 *    paddingTop="0"
 *    paddingBottom="0"
 *    paddingLeft="5"
 *    paddingRight="5"
 *    rollOverColor="<i>Depends on theme color"</i>
 *    selectionColor="<i>Depends on theme color"</i>
 *    selectionDuration="250"
 *    selectionEasingFunction="undefined"
 *    textAlign="left|center|right"
 *    textDecoration="none|underline"
 *    textIndent="0"
 *    textRollOverColor="0x2B333C"
 *    textSelectedColor="0x2B333C"
 *    
 *    <b>Events</b>
 *    change="<i>No default</i>"
 *    close="<i>No default</i>"
 *    dataChange="<i>No default</i>"
 *    enter="<i>No default</i>"
 *    itemRollOut="<i>No default</i>"
 *    itemRollOver="<i>No default</i>"
 *    open="<i>No default</i>"
 *    scroll="<i>No default</i>"
 *  /&gt;
 *  </pre>
 *
 *  @includeExample examples/SimpleComboBox.mxml
 *
 *  @see mx.controls.List
 *  @see mx.effects.Tween
 *  @see mx.managers.PopUpManager
 *
 */
public class ComboBox extends ComboBase
                      implements IDataRenderer, IDropInListItemRenderer,
                      IListItemRenderer
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class mixins
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Placeholder for mixin by ComboBoxAccImpl.
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
    public function ComboBox()
    {
        super();

        // It it better to start out with an empty data provider rather than
        // an undefined one. Otherwise, code in getDropdown() sets it to []
        // later, but via setDataProvider(). This API has side effects like
        // setting selectionChanged, which causes the text in an editable
        // ComboBox to be lost.
        dataProvider = new ArrayCollection();

        useFullDropdownSkin = true;
        wrapDownArrowButton = false;
        addEventListener("unload", unloadHandler);
        addEventListener(Event.REMOVED_FROM_STAGE, removedFromStageHandler);
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  A reference to the internal List that pops up to display a row
     *  for each dataProvider item.
     */
    private var _dropdown:ListBase;

    /**
     *  @private
     *  A int to track the oldIndex, used when the dropdown is dismissed using the ESC key.
     */
    private var _oldIndex:int;

    /**
     *  @private
     *  The tween used for showing and hiding the drop-down list.
     */
    private var tween:Tween = null;
    
    /**
     *  @private
     *  A flag to track whether the dropDown tweened up or down. 
     */
    private var tweenUp:Boolean = false;
    

    /**
     *  @private
     */
    private var preferredDropdownWidth:Number;

    /**
     *  @private
     */
    private var dropdownBorderStyle:String = "solid";

    /**
     *  @private
     *  Is the dropdown list currently shown?
     */
    private var _showingDropdown:Boolean = false;

    /**
     *  @private
     */
    private var _selectedIndexOnDropdown:int = -1;

    /**
     *  @private
     */
    private var bRemoveDropdown:Boolean = false;

    /**
     *  @private
     */
    private var inTween:Boolean = false;

    /**
     *  @private
     */
    private var bInKeyDown:Boolean = false;

    /**
     *  @private
     *  Flag that will block default data/listData behavior
     */
    private var selectedItemSet:Boolean;

    /**
     *  @private
     *  Event that is causing the dropDown to open or close.
     */
    private var triggerEvent:Event;

    /**
     *  @private
     *  Whether the text property was explicitly set or not
     */
    private var explicitText:Boolean;

    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  data
    //----------------------------------

    /**
     *  @private
     *  Storage for the data property.
     */
    private var _data:Object;

    [Bindable("dataChange")]
    [Inspectable(environment="none")]

    /**
     *  The <code>data</code> property lets you pass a value
     *  to the component when you use it in an item renderer or item editor.
     *  You typically use data binding to bind a field of the <code>data</code>
     *  property to a property of this component.
     *
     *  <p>The ComboBox control uses the <code>listData</code> property and the
     *  <code>data</code> property as follows. If the ComboBox is in a 
     *  DataGrid control, it expects the <code>dataField</code> property of the 
     *  column to map to a property in the data and sets 
     *  <code>selectedItem</code> to that property. If the ComboBox control is 
     *  in a List control, it expects the <code>labelField</code> of the list 
     *  to map to a property in the data and sets <code>selectedItem</code> to 
     *  that property. 
     *  Otherwise, it sets <code>selectedItem</code> to the data itself.</p>
     *
     *  <p>You do not set this property in MXML.</p>
     *
     *  @see mx.core.IDataRenderer
     */
    public function get data():Object
    {
        return _data;
    }

    /**
     *  @private
     */
    public function set data(value:Object):void
    {
        var newSelectedItem:*;

        _data = value;

        if (_listData && _listData is DataGridListData)
            newSelectedItem = _data[DataGridListData(_listData).dataField];
        else if (_listData is ListData && ListData(_listData).labelField in _data)
            newSelectedItem = _data[ListData(_listData).labelField];
        else
            newSelectedItem = _data;

        if (newSelectedItem !== undefined && !selectedItemSet)
        {
            selectedItem = newSelectedItem;
            selectedItemSet = false;
        }

        dispatchEvent(new FlexEvent(FlexEvent.DATA_CHANGE));
    }

    //----------------------------------
    //  listData
    //----------------------------------

    /**
     *  @private
     *  Storage for the listData property.
     */
    private var _listData:BaseListData;

    [Bindable("dataChange")]
    [Inspectable(environment="none")]

    /**
     *  When a component is used as a drop-in item renderer or drop-in item 
     *  editor, Flex initializes the <code>listData</code> property of the 
     *  component with the appropriate data from the List control. The 
     *  component can then use the <code>listData</code> property and the 
     *  <code>data</code> property to display the appropriate information 
     *  as a drop-in item renderer or drop-in item editor.
     *
     *  <p>You do not set this property in MXML or ActionScript; Flex sets it 
     *  when the component
     *  is used as a drop-in item renderer or drop-in item editor.</p>
     *
     *  @see mx.controls.listClasses.IDropInListItemRenderer
     */
    public function get listData():BaseListData
    {
        return _listData;
    }

    /**
     *  @private
     */
    public function set listData(value:BaseListData):void
    {
        _listData = value;
    }

    //----------------------------------
    //  dataProvider
    //----------------------------------

    /**
     *  @private
     */
    private var collectionChanged:Boolean = false;

    [Bindable("collectionChange")]
    [Inspectable(category="Data", arrayType="Object")]

    /**
     *  @inheritDoc
     */
    override public function set dataProvider(value:Object):void
    {
        selectionChanged = true;

        super.dataProvider = value;

        destroyDropdown();

        _showingDropdown = false;

        invalidateProperties();
        invalidateSize();
    }

    //----------------------------------
    //  itemRenderer
    //----------------------------------

    /**
     *  @private
     *  Storage for itemRenderer property.
     */
    private var _itemRenderer:IFactory;

    [Inspectable(category="Data")]

    /**
     *  IFactory that generates the instances that displays the data for the
     *  drop-down list of the control.  You can use this property to specify 
     *  a custom item renderer for the drop-down list.
     *
     *  <p>The control uses a List control internally to create the drop-down
     *  list.
     *  The default item renderer for the List control is the ListItemRenderer
     *  class, which draws the text associated with each item in the list, 
     *  and an optional icon. </p>
     *
     *  @see mx.controls.List
     *  @see mx.controls.listClasses.ListItemRenderer
     */
    public function get itemRenderer():IFactory
    {
        return _itemRenderer;
    }

    /**
     *  @private
     */
    public function set itemRenderer(value:IFactory):void
    {
        _itemRenderer = value;

        if (_dropdown)
            _dropdown.itemRenderer = value;

        invalidateSize();
        invalidateDisplayList();

        dispatchEvent(new Event("itemRendererChanged"));
    }

    //----------------------------------
    //  selectedIndex
    //----------------------------------

    [Bindable("change")]
    [Bindable("collectionChange")]
    [Bindable("valueCommit")]
    [Inspectable(category="General", defaultValue="0")]

    /**
     *  Index of the selected item in the drop-down list.
     *  Setting this property sets the current index and displays
     *  the associated label in the TextInput portion.
     *  <p>The default value is -1, but it set to 0
     *  when a <code>dataProvider</code> is assigned, unless there is a prompt.
     *  If the control is editable, and the user types in the TextInput portion,
     *  the value of the <code>selectedIndex</code> property becomes 
     *  -1. If the value of the <code>selectedIndex</code> 
     *  property is out of range, the <code>selectedIndex</code> property is set to the last
     *  item in the <code>dataProvider</code>.</p>
     */
    override public function set selectedIndex(value:int):void
    {
        super.selectedIndex = value;

        if (value >= 0)
            selectionChanged = true;
            
        implicitSelectedIndex = false;
        invalidateDisplayList();

        // value committed event needs the text to be set
        if (textInput && !textChanged && value >= 0)
            textInput.text = selectedLabel;
        else if (textInput && prompt)
            textInput.text = prompt;

        // [Matt] setting the text of the textInput should take care of this now
        // Send a valueCommit event, which is used by the data model
        //dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));
    }

    //----------------------------------
    //  selectedItem
    //----------------------------------

    [Bindable("change")]
    [Bindable("collectionChange")]
    [Bindable("valueCommit")]
    [Inspectable(environment="none")]

    /**
     *  Contains a reference to the selected item in the
     *  <code>dataProvider</code>.
     *  If the data is an object or class instance, modifying
     *  properties in the object or instance modifies the <code>dataProvider</code>
     *  and thus its views.  Setting the selectedItem itself causes the
     *  ComboBox to select that item (display it in the TextInput portion and set
     *  the selectedIndex) if it exists in the dataProvider.
     *  <p>If the ComboBox control is editable, the <code>selectedItem</code>
     *  property is <code>null</code> if the user types any text
     *  into the TextInput.
     *  It has a value only if the user selects an item from the drop-down
     *  list, or if it is set programmatically.</p>
     */
    override public function set selectedItem(value:Object):void
    {
        selectedItemSet = true;

        super.selectedItem = value
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
    //  dropdown
    //----------------------------------

    /**
     *  A reference to the List control that acts as the drop-down in the ComboBox.
     */
    public function get dropdown():ListBase
    {
        return getDropdown();
    }

    //----------------------------------
    //  dropdownFactory
    //----------------------------------

    /**
     *  @private
     *  Storage for the dropdownFactory property.
     */
    private var _dropdownFactory:IFactory = new ClassFactory(List);

    [Bindable("dropdownFactoryChanged")]

    /**
     *  The IFactory that creates a ListBase-derived instance to use
     *  as the drop-down.
     *  The default value is an IFactory for List
     *
     */
    public function get dropdownFactory():IFactory
    {
        return _dropdownFactory;
    }

    /**
     *  @private
     */
    public function set dropdownFactory(value:IFactory):void
    {
        _dropdownFactory = value;

        dispatchEvent(new Event("dropdownFactoryChanged"));
    }

    //----------------------------------
    //  dropDownStyleFilters
    //----------------------------------

    /**
     *  The set of styles to pass from the ComboBox to the dropDown.
     *  Styles in the dropDownStyleName style will override these styles.
     *  @see mx.styles.StyleProxy
     *  @review
     */
    protected function get dropDownStyleFilters():Object
    {
        return null;
    }

    //----------------------------------
    //  dropdownWidth
    //----------------------------------

    /**
     *  @private
     *  Storage for the dropdownWidth property.
     */
    private var _dropdownWidth:Number = 100;

    [Bindable("dropdownWidthChanged")]
    [Inspectable(category="Size", defaultValue="100")]

    /**
     *  Width of the drop-down list, in pixels.
     *  <p>The default value is 100 or the width of the longest text
     *  in the <code>dataProvider</code>, whichever is greater.</p>
     *
     */
    public function get dropdownWidth():Number
    {
        return _dropdownWidth;
    }

    /**
     *  @private
     */
    public function set dropdownWidth(value:Number):void
    {
        _dropdownWidth = value;

        preferredDropdownWidth = value;

        if (_dropdown)
            _dropdown.setActualSize(value, _dropdown.height);

        dispatchEvent(new Event("dropdownWidthChanged"));
    }

    //----------------------------------
    //  labelField
    //----------------------------------

    /**
     *  @private
     *  Storage for the labelField property.
     */
    private var _labelField:String = "label";

    /**
     *  @private
     */
    private var labelFieldChanged:Boolean;

    [Bindable("labelFieldChanged")]
    [Inspectable(category="Data", defaultValue="label")]

    /**
     *  Name of the field in the items in the <code>dataProvider</code>
     *  Array to display as the label in the TextInput portion and drop-down list.
     *  By default, the control uses a property named <code>label</code>
     *  on each Array object and displays it.
     *  <p>However, if the <code>dataProvider</code> items do not contain
     *  a <code>label</code> property, you can set the <code>labelField</code>
     *  property to use a different property.</p>
     *
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
        labelFieldChanged = true;

        invalidateDisplayList();

        dispatchEvent(new Event("labelFieldChanged"));
    }

    //----------------------------------
    //  labelFunction
    //----------------------------------

    /**
     *  @private
     *  Storage for the labelFunction property.
     */
    private var _labelFunction:Function;

    /**
     *  @private
     */
    private var labelFunctionChanged:Boolean;

    [Bindable("labelFunctionChanged")]
    [Inspectable(category="Data")]

    /**
     *  User-supplied function to run on each item to determine its label.
     *  By default the control uses a property named <code>label</code>
     *  on each <code>dataProvider</code> item to determine its label.
     *  However, some data sets do not have a <code>label</code> property,
     *  or do not have another property that can be used for displaying
     *  as a label.
     *  <p>An example is a data set that has <code>lastName</code> and
     *  <code>firstName</code> fields but you want to display full names.
     *  You use <code>labelFunction</code> to specify a callback function
     *  that uses the appropriate fields and return a displayable String.</p>
     *
     *  <p>The labelFunction takes a single argument which is the item
     *  in the dataProvider and returns a String:</p>
     *  <pre>
     *  myLabelFunction(item:Object):String
     *  </pre>
     *
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
        labelFunctionChanged = true;

        invalidateDisplayList();

        dispatchEvent(new Event("labelFunctionChanged"));
    }

    //----------------------------------
    //  prompt
    //----------------------------------

    private var promptChanged:Boolean = false;

    /**
     *  @private
     *  Storage for the prompt property.
     */
    private var _prompt:String;

    [Inspectable(category="General")]

    /**
     *  The prompt for the ComboBox control. A prompt is
     *  a String that is displayed in the TextInput portion of the
     *  ComboBox when <code>selectedIndex</code> = -1.  It is usually
     *  a String like "Select one...".  If there is no
     *  prompt, the ComboBox control sets <code>selectedIndex</code> to 0
     *  and displays the first item in the <code>dataProvider</code>.
     */
    public function get prompt():String
    {
        return _prompt;
    }

    /**
     *  @private
     */
    public function set prompt(value:String):void
    {
        _prompt = value;
        promptChanged = true;
        invalidateProperties();
    }

    //----------------------------------
    //  rowCount
    //----------------------------------

    /**
     *  @private
     *  Storage for the rowCount property.
     */
    private var _rowCount:int = 5;

    [Bindable("resize")]
    [Inspectable(category="General", defaultValue="5")]

    /**
     *  Maximum number of rows visible in the ComboBox control list.
     *  If there are fewer items in the
     *  dataProvider, the ComboBox shows only as many items as
     *  there are in the dataProvider.
     *  
     *  @default 5
     */
    public function get rowCount():int
    {
        return Math.max(1, Math.min(collection.length, _rowCount));
    }

    /**
     *  @private
     */
    public function set rowCount(value:int):void
    {
        _rowCount = value;

        if (_dropdown)
            _dropdown.rowCount = value;
    }

    //----------------------------------
    //  selectedLabel
    //----------------------------------

    /**
     *  The String displayed in the TextInput portion of the ComboBox. It
     *  is calculated from the data by using the <code>labelField</code> 
     *  or <code>labelFunction</code>.
     */
    public function get selectedLabel():String
    {
        var item:Object = selectedItem;

        return itemToLabel(item);
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: UIComponent
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function initializeAccessibility():void
    {
        if (ComboBox.createAccessibilityImplementation != null)
            ComboBox.createAccessibilityImplementation(this);
    }

    /**
     *  @private
     */
    override public function styleChanged(styleProp:String):void
    {
        destroyDropdown();

        super.styleChanged(styleProp);
    }

    /**
     *  Makes sure the control is at least 40 pixels wide,
     *  and tall enough to fit one line of text
     *  in the TextInput portion of the control but at least
     *  22 pixels high.
     */
    override protected function measure():void
    {
        super.measure();

        // Make sure we're not too small
        measuredMinWidth = Math.max(measuredWidth, DEFAULT_MEASURED_MIN_WIDTH);

        // Make sure we're tall enough to hold our text.
        // Text field height is text height + 4 pixels top/bottom
        var textHeight:Number = measureText("M").height + 6;
        var bm:EdgeMetrics = borderMetrics;
        measuredMinHeight = measuredHeight =
            Math.max(textHeight + bm.top + bm.bottom, DEFAULT_MEASURED_MIN_HEIGHT);
        if (FlexVersion.compatibilityVersion >= FlexVersion.VERSION_3_0)    
            measuredMinHeight = measuredHeight += getStyle("paddingTop") + getStyle("paddingBottom");
    }

    /**
     *  @private
     *  Make sure the drop-down width is the same as the rest of the ComboBox
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        // we toss the dropdown when resized
        // except if we're opening the dropdown
        // then we assume the updateDisplayList() call is spurious
        // and will not affect the dropdown size
        if (_dropdown && !inTween)
        {
            destroyDropdown();
        }
        else if (!_showingDropdown && inTween)
        {
            bRemoveDropdown = true;
        }

        var ddw:Number = preferredDropdownWidth;
        if (isNaN(ddw))
            ddw = _dropdownWidth = unscaledWidth;

        if (labelFieldChanged)
        {
            if (_dropdown)
                _dropdown.labelField = _labelField;

            selectionChanged = true;
            if (!explicitText) 
                textInput.text = selectedLabel;
            labelFieldChanged = false;
        }

        if (labelFunctionChanged)
        {
            if (_dropdown)
                _dropdown.labelFunction = _labelFunction;

            selectionChanged = true;
            if (!explicitText)
                textInput.text = selectedLabel;
            labelFunctionChanged = false;
        }

        if (selectionChanged)
        {
            if (!textChanged)
            {
                if (selectedIndex == -1 && prompt)
                    textInput.text = prompt;
                else if (!explicitText)
                    textInput.text = selectedLabel;
            }

            textInput.invalidateDisplayList();
            textInput.validateNow();

            if (editable)
            {
                textInput.getTextField().setSelection(0, textInput.text.length);
                textInput.getTextField().scrollH = 0;
            }

            if (_dropdown)
                _dropdown.selectedIndex = selectedIndex;

            selectionChanged = false;
        }

        // We might need to decrease the number of rows.
        if (_dropdown && _dropdown.rowCount != rowCount)
            _dropdown.rowCount = rowCount;
    }

    /**
     *  @private
     */
    override protected function commitProperties():void
    {
        explicitText = textChanged;

        super.commitProperties();

        if (collectionChanged)
        {
            if (selectedIndex == -1 && implicitSelectedIndex && _prompt == null)
                selectedIndex = 0;
            selectedIndexChanged = true;
            collectionChanged = false;
        }
        if (promptChanged && prompt != null && selectedIndex == -1)
        {
            promptChanged = false;
            textInput.text = prompt;
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Returns a string representing the <code>item</code> parameter.
     *  
     *  <p>This method checks in the following order to find a value to return:</p>
     *  
     *  <ol>
     *    <li>If you have specified a <code>labelFunction</code> property,
     *  returns the result of passing the item to the function.</li>
     *    <li>If the item is a String, Number, Boolean, or Function, returns
     *  the item.</li>
     *    <li>If the item has a property with the name specified by the control's
     *  <code>labelField</code> property, returns the contents of the property.</li>
     *    <li>If the item has a label property, returns its value.</li>
     *  </ol>
     * 
     *  @param item The object that contains the value to convert to a label. If the item is null, this method returns the empty string.
     */
    public function itemToLabel(item:Object):String
    {
        // we need to check for null explicitly otherwise
        // a numeric zero will not get properly converted to a string.
        // (do not use !item)
        if (item == null)
            return "";

        if (labelFunction != null)
            return labelFunction(item);

        if (typeof(item) == "object")
        {
            try
            {
                if (item[labelField] != null)
                    item = item[labelField];
            }
            catch(e:Error)
            {
            }
        }
        else if (typeof(item) == "xml")
        {
            try
            {
                if (item[labelField].length() != 0)
                    item = item[labelField];
            }
            catch(e:Error)
            {
            }
        }

        if (typeof(item) == "string")
            return String(item);

        try
        {
            return item.toString();
        }
        catch(e:Error)
        {
        }

        return " ";
    }

    /**
     *  Displays the drop-down list.
     */
    public function open():void
    {
        displayDropdown(true);
    }

    /**
     *  Hides the drop-down list.
     */
    public function close(trigger:Event = null):void
    {
        if (_showingDropdown)
        {
            if (_dropdown && selectedIndex != _dropdown.selectedIndex)
                selectedIndex = _dropdown.selectedIndex;

            displayDropdown(false, trigger);

            dispatchChangeEvent(new Event("dummy"),
                    _selectedIndexOnDropdown,
                    selectedIndex);
        }
    }

    /**
     *  @private
     */
    mx_internal function hasDropdown():Boolean
    {
        return _dropdown != null;
    }

    /**
     *  @private
     */
    private function getDropdown():ListBase
    {
        if (!initialized)
            return null;

        if (!hasDropdown())
        {
            var dropDownStyleName:String = getStyle("dropDownStyleName");
            if (dropDownStyleName == null ) 
                dropDownStyleName = getStyle("dropdownStyleName");
            

            _dropdown = dropdownFactory.newInstance();
            _dropdown.visible = false;
            _dropdown.focusEnabled = false;
            _dropdown.owner = this;

            if (itemRenderer)
                _dropdown.itemRenderer = itemRenderer;

            if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
            {
                _dropdown.styleName = this;
            }

            if (dropDownStyleName)
            {
                if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
                {
                    var styleDecl:CSSStyleDeclaration =
                        StyleManager.getStyleDeclaration("." + dropDownStyleName);
    
                    if (styleDecl)
                        _dropdown.styleDeclaration = styleDecl;
                }
                else
                {
                    _dropdown.styleName = dropDownStyleName;
                }
            }
            else if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
            {
                _dropdown.setStyle("cornerRadius", 0);
            }

            PopUpManager.addPopUp(_dropdown, this);

            // Don't display a tween when the selection changes.
            // The dropdown menu is about to appear anyway,
            // and other processing can make the tween look choppy.
            _dropdown.setStyle("selectionDuration", 0);

            if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0 && dropdownBorderStyle && dropdownBorderStyle != "")
                _dropdown.setStyle("borderStyle", dropdownBorderStyle);

            // Set up a data provider in case one doesn't yet exist,
            // so we can share it with the dropdown listbox.
            if (!dataProvider)
                dataProvider = new ArrayCollection();

            _dropdown.dataProvider = dataProvider;
            _dropdown.rowCount = rowCount;
            _dropdown.width = _dropdownWidth;
            _dropdown.selectedIndex = selectedIndex;
            _oldIndex = selectedIndex;
            _dropdown.verticalScrollPolicy = ScrollPolicy.AUTO;
            _dropdown.labelField = _labelField;
            _dropdown.labelFunction = _labelFunction;
            _dropdown.allowDragSelection = true;

            _dropdown.addEventListener("change", dropdown_changeHandler);
            _dropdown.addEventListener(ScrollEvent.SCROLL, dropdown_scrollHandler);
            _dropdown.addEventListener(ListEvent.ITEM_ROLL_OVER, dropdown_itemRollOverHandler);
            _dropdown.addEventListener(ListEvent.ITEM_ROLL_OUT, dropdown_itemRollOutHandler);
            _dropdown.addEventListener(FlexMouseEvent.MOUSE_DOWN_OUTSIDE, dropdown_mouseDownOutsideHandler);
            _dropdown.addEventListener(FlexMouseEvent.MOUSE_WHEEL_OUTSIDE, dropdown_mouseWheelOutsideHandler);

            // the drop down should close if the user clicks on any item.
            // add a handler to detect a click in the list
            _dropdown.addEventListener(ListEvent.ITEM_CLICK, dropdown_itemClickHandler);

            UIComponentGlobals.layoutManager.validateClient(_dropdown, true);
            _dropdown.setActualSize(_dropdownWidth, _dropdown.getExplicitOrMeasuredHeight());
            _dropdown.validateDisplayList();

            _dropdown.cacheAsBitmap = true;

            // weak reference to stage
            systemManager.addEventListener(Event.RESIZE, stage_resizeHandler, false, 0, true);
        }

        _dropdown.scaleX = scaleX;
        _dropdown.scaleY = scaleY;

        return _dropdown;
    }

    /**
     *  @private
     */
    private function displayDropdown(show:Boolean, trigger:Event = null):void
    {
        if (!initialized || show == _showingDropdown)
            return;

        // Subclasses may extend to do pre-processing
        // before the dropdown is displayed
        // or override to implement special display behavior

        // Show or hide the dropdown
        var initY:Number;
        var endY:Number;
        var duration:Number;
        var easingFunction:Function;

        var point:Point = new Point(0, unscaledHeight);
        point = localToGlobal(point);
        
        //opening the dropdown 
        if (show)
        {
            // Store the selectedIndex temporarily so we can tell
            // if the value changed when the dropdown is closed
            _selectedIndexOnDropdown = selectedIndex;

            getDropdown();


            if (_dropdown.parent == null)  // was popped up then closed
                PopUpManager.addPopUp(_dropdown, this);
            else
                PopUpManager.bringToFront(_dropdown);

            point = _dropdown.parent.globalToLocal(point);

            // if we donot have enough space in the bottom display the dropdown
            // at the top. But if the space there is also less than required
            // display it below.
            if (point.y + _dropdown.height > screen.height &&
                point.y > _dropdown.height)
            {
                // Dropdown will go below the bottom of the stage
                // and be clipped. Instead, have it grow up.
                point.y -= (unscaledHeight + _dropdown.height);
                initY = -_dropdown.height;
                tweenUp = true;
            }
            else
            {
                initY = _dropdown.height;
                tweenUp = false;
            }
        
            var sel:int = _dropdown.selectedIndex;
            if (sel == -1)
                sel = 0;
            var pos:Number = _dropdown.verticalScrollPosition;

            // try to set the verticalScrollPosition one above the selected index so
            // it looks better when the dropdown is displayed
            pos = sel - 1;
            pos = Math.min(Math.max(pos, 0), _dropdown.maxVerticalScrollPosition);
            _dropdown.verticalScrollPosition = pos;

            if (_dropdown.x != point.x || _dropdown.y != point.y)
                _dropdown.move(point.x, point.y);

            _dropdown.scrollRect = new Rectangle(0, initY,
                    _dropdown.width, _dropdown.height);

            if (!_dropdown.visible)
                _dropdown.visible = true;

            // Make sure we don't remove the dropdown at the end of the tween
            bRemoveDropdown = false;
            
            // Set up the tween and relevant variables. 
            _showingDropdown = show;
            duration = getStyle("openDuration");
            endY = 0;
            easingFunction = getStyle("openEasingFunction") as Function;
        }
        
        // closing the dropdown 
        else if (_dropdown)
        {
            point = _dropdown.parent.globalToLocal(point);
            // Set up the tween and relevant variables. 
            endY = (point.y + _dropdown.height > screen.height || tweenUp
                               ? -_dropdown.height
                               : _dropdown.height);
            _showingDropdown = show;
            initY = 0;
            duration = getStyle("closeDuration");
            easingFunction = getStyle("closeEasingFunction") as Function;
            
            _dropdown.resetDragScrolling();
        }
        
        inTween = true;
        UIComponentGlobals.layoutManager.validateNow();
        
        // Block all layout, responses from web service, and other background
        // processing until the tween finishes executing.
        UIComponent.suspendBackgroundProcessing();
        
        // Disable the dropdown during the tween.
        if (_dropdown)
            _dropdown.enabled = false;
        
        duration = Math.max(1, duration);
        tween = new Tween(this, initY, endY, duration);
        
        if (easingFunction != null && tween)
            tween.easingFunction = easingFunction;
            
        triggerEvent = trigger;
    }

    /**
     *  @private
     */
    private function dispatchChangeEvent(oldEvent:Event, prevValue:int,
                                         newValue:int):void
    {
        if (prevValue != newValue)
        {
            var newEvent:Event = oldEvent is ListEvent ?
                                 oldEvent :
                                 new ListEvent("change");

            dispatchEvent(newEvent);
        }
    }

    /**
     *  @private
     */
    private function destroyDropdown():void
    {
        if (_dropdown && !_showingDropdown)
        {
            if (inTween)
            {
                tween.endTween();
            }
            else
            {
                PopUpManager.removePopUp(_dropdown);
                _dropdown = null;
            }
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden event handlers
    //
    //--------------------------------------------------------------------------
    private var implicitSelectedIndex:Boolean = false;
    /**
     *  @private
     */
    override protected function collectionChangeHandler(event:Event):void
    {
        // Save a copy of the selectedIndex
        var curSelectedIndex:int = selectedIndex;

        super.collectionChangeHandler(event);

        if (event is CollectionEvent)
        {
            // trace("ListBase collectionEvent");
            var ce:CollectionEvent = CollectionEvent(event);

            if (collection.length == 0)
            {
                // Special case: Empty dataProvider.
                if (!selectedIndexChanged && !selectedItemChanged)
                {
                    super.selectedIndex = -1;
                    implicitSelectedIndex = true;
                    invalidateDisplayList();
                }
                // if the combobox is non-editable remove the text
                // we don't want to remove the text if it is editable as user might
                // have typed something.
                if (textInput && !editable)
                    textInput.text = "";
            }

            else if (ce.kind == CollectionEventKind.ADD)
            {
                if (collection.length == ce.items.length)
                {
                    // Special case: Adding the first item(s). Select item 0
                    // if there is no prompt
                    if (selectedIndex == -1 && _prompt == null)
                        selectedIndex = 0;
                }
                else
                {
                    // we dont want to destroy the dropdown just
                    // because data got added.  Especially true
                    // for paged data.
                    return;
                }
            }

            else if (ce.kind == CollectionEventKind.UPDATE)
            {
                if (ce.location == selectedIndex ||
                    ce.items[0].source == selectedItem)
                    // unsorted lists don't have a valid location
                    // Force an update of the text input
                    selectionChanged = true;
            }

            else if (ce.kind == CollectionEventKind.REPLACE)
            {
                // bail on a replace, no need to change anything,
                // especially for paged data
                return;
            }

            else if (ce.kind == CollectionEventKind.RESET)
            {
                collectionChanged = true;
                if (!selectedIndexChanged && !selectedItemChanged)
                    selectedIndex = prompt ? -1 : 0;
                invalidateProperties();
            }

            invalidateDisplayList();

            destroyDropdown();

            _showingDropdown = false;
        }
    }

    private function popup_moveHandler(event:Event):void
    {
        destroyDropdown();
    }

    /**
     *  @private
     */
    override protected function textInput_changeHandler(event:Event):void
    {
        super.textInput_changeHandler(event);

        // Force a change event to be dispatched
        dispatchChangeEvent(event, -1, -2);
    }

    /**
     *  @private
     */
    override protected function downArrowButton_buttonDownHandler(
                                    event:FlexEvent):void
    {
        // The down arrow should always toggle the visibility of the dropdown.
        if (_showingDropdown)
        {
            close(event);
        }
        else
        {
            displayDropdown(true, event);
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
    private function dropdown_mouseDownOutsideHandler(event:MouseEvent):void
    {
        if (event.target != _dropdown)
            // the dropdown's items can dispatch a mouseDownOutside
            // event which then bubbles up to us
            return;

        if (!hitTestPoint(event.stageX, event.stageY, true))
        {
            close(event);
        }
    }

    /**
     *  @private
     */
    private function dropdown_mouseWheelOutsideHandler(event:MouseEvent):void
    {
        dropdown_mouseDownOutsideHandler(event);
    }

    /**
     *  @private
     */
    private function dropdown_itemClickHandler(event:ListEvent):void
    {
        if (_showingDropdown)
        {
            close();
        }
    }

    /**
     *  @private
     */
    override protected function focusOutHandler(event:FocusEvent):void
    {
        // Note: event.relatedObject is the object getting focus.
        // It can be null in some cases, such as when you open
        // the dropdown and then click outside the application.

        // If the dropdown is open...
        if (_showingDropdown && _dropdown)
        {
            // If focus is moving outside the dropdown...
            if (!event.relatedObject ||
                !_dropdown.contains(event.relatedObject))
            {
                // Close the dropdown.
                close();
            }
        }

        super.focusOutHandler(event);
    }

    private function stage_resizeHandler(event:Event):void
    {
        if (_dropdown)
        {
            _dropdown.$visible = false;
            _showingDropdown = false;
        }
    }

    /**
     *  @private
     */
    private function dropdown_scrollHandler(event:Event):void
    {
        // TextField.scroll bubbles so you might see it here
        if (event is ScrollEvent)
        {
            var se:ScrollEvent = ScrollEvent(event);
            if (se.detail == ScrollEventDetail.THUMB_TRACK ||
                    se.detail == ScrollEventDetail.THUMB_POSITION ||
                    se.detail == ScrollEventDetail.LINE_UP ||
                    se.detail == ScrollEventDetail.LINE_DOWN)
                dispatchEvent(se);
        }
    }

    /**
     *  @private
     */
    private function dropdown_itemRollOverHandler(event:Event):void
    {
        dispatchEvent(event);
    }

    /**
     *  @private
     */
    private function dropdown_itemRollOutHandler(event:Event):void
    {
        dispatchEvent(event);
    }

    /**
     *  @private
     */
    private function dropdown_changeHandler(event:Event):void
    {
        var prevValue:int = selectedIndex;

        // This assignment will also assign the label to the text field.
        // See setSelectedIndex().
        if (_dropdown)
            selectedIndex = _dropdown.selectedIndex;

        // If this was generated by the dropdown as a result of a keystroke, it is
        // likely a Page-Up or Page-Down, or Arrow-Up or Arrow-Down.
        // If the selection changes due to a keystroke,
        // we leave the dropdown displayed.
        // If it changes as a result of a mouse selection,
        // we close the dropdown.
        if (!_showingDropdown)
            dispatchChangeEvent(event, prevValue, selectedIndex);
        else if (!bInKeyDown)
        {
            // this will also send a change event if needed
            close();
        }
    }

    /**
     *  @private
     */
    override protected function keyDownHandler(event:KeyboardEvent):void
    {
        // If a the editable field currently has focus, it is handling
        // all arrow keys. We shouldn't also scroll this selection.
        if (event.target == textInput)
            return;

        if (event.ctrlKey && event.keyCode == Keyboard.DOWN)
        {
            displayDropdown(true, event);
            event.stopPropagation();
        }
        else if (event.ctrlKey && event.keyCode == Keyboard.UP)
        {
            close(event);
            event.stopPropagation();
        }
        else if (event.keyCode == Keyboard.ESCAPE)
        {
            if (_showingDropdown)
            {
                if (_oldIndex != _dropdown.selectedIndex)
                    selectedIndex = _oldIndex;

                displayDropdown(false);
                event.stopPropagation();
            }
        }

        else if (event.keyCode == Keyboard.ENTER)
        {
            if (_showingDropdown)
            {
                close();
                event.stopPropagation();
            }
        }
        else
        {
            if (!editable ||
                event.keyCode == Keyboard.UP ||
                event.keyCode == Keyboard.DOWN ||
                event.keyCode == Keyboard.PAGE_UP ||
                event.keyCode == Keyboard.PAGE_DOWN)
            {
                var oldIndex:int = selectedIndex;

                // Make sure we know we are handling a keyDown,
                // so if the dropdown sends out a "change" event
                // (like when an up-arrow or down-arrow changes
                // the selection) we know not to close the dropdown.
                bInKeyDown = _showingDropdown;
                // Redispatch the event to the dropdown
                // and let its keyDownHandler() handle it.

                dropdown.dispatchEvent(event.clone());
                event.stopPropagation();
                bInKeyDown = false;

            }
        }
    }

    /**
     *  @private
     *  This acts as the destructor.
     */
    private function unloadHandler(event:Event):void
    {
        if (inTween)
        {
            UIComponent.resumeBackgroundProcessing();
            inTween = false;
        }

        if (_dropdown)
            _dropdown.parent.removeChild(_dropdown);
    }
    
    /**
     *  @private
     */
    private function removedFromStageHandler(event:Event):void
    {
        // Ensure we've unregistered ourselves from PopupManager, else
        // we'll be leaked.
        destroyDropdown();
    }

    //--------------------------------------------------------------------------
    //
    // Tween handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    mx_internal function onTweenUpdate(value:Number):void
    {
        if (_dropdown)
        {
            _dropdown.scrollRect = new Rectangle(0, value,
                _dropdown.width, _dropdown.height);
        }
    }

    /**
     *  @private
     */
    mx_internal function onTweenEnd(value:Number):void
    {
        if (_dropdown)
        {
            // Clear the scrollRect here. This way if drop shadows are
            // assigned to the dropdown they show up correctly
            _dropdown.scrollRect = null;

            inTween = false;
            _dropdown.enabled = true;
            _dropdown.visible = _showingDropdown;
        }

        if (bRemoveDropdown)
        {
            PopUpManager.removePopUp(_dropdown);
            _dropdown = null;
            bRemoveDropdown = false;
        }

        UIComponent.resumeBackgroundProcessing();
        var cbdEvent:DropdownEvent =
            new DropdownEvent(_showingDropdown ? DropdownEvent.OPEN : DropdownEvent.CLOSE);
        cbdEvent.triggerEvent = triggerEvent;
        dispatchEvent(cbdEvent);
    }

    /**
     *  Determines default values of the height and width to use for each 
     *  entry in the drop-down list, based on the maximum size of the label 
     *  text in the first <code>numItems</code> items in the data provider. 
     *
     *  @param count The number of items to check to determine the value.
     *  
     *  @return An Object containing two properties: width and height.
     */
    override protected function calculatePreferredSizeFromData(count:int):Object
    {
        var maxW:Number = 0;
        var maxH:Number = 0;

        var bookmark:CursorBookmark = iterator ? iterator.bookmark : null;
        
        iterator.seek(CursorBookmark.FIRST, 0);
        
        var more:Boolean = iterator != null;
        
        var lineMetrics:TextLineMetrics;

        for (var i:int = 0; i < count; i++)
        {
            var data:Object;
            if (more)
                data = iterator ? iterator.current : null;
            else
                data = null;

            var txt:String = itemToLabel(data);

            lineMetrics = measureText(txt);

            maxW = Math.max(maxW, lineMetrics.width);
            maxH = Math.max(maxH, lineMetrics.height);
            
            if (iterator)
                iterator.moveNext();
        }

        if (prompt)
        {
            lineMetrics = measureText(prompt);

            maxW = Math.max(maxW, lineMetrics.width);
            maxH = Math.max(maxH, lineMetrics.height);
        }

        maxW += getStyle("paddingLeft") + getStyle("paddingRight");

        if (iterator)
            iterator.seek(bookmark, 0);

        return { width: maxW, height: maxH };
    }

    /**
     *  @private
     */
    mx_internal function get isShowingDropdown():Boolean
    {
        return _showingDropdown;
    }


}

}
