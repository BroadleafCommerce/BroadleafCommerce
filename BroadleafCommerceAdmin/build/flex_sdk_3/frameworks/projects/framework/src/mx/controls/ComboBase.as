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

import flash.display.DisplayObject;
import flash.events.Event;
import flash.events.FocusEvent;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.events.TextEvent;
import mx.collections.ArrayCollection;
import mx.collections.CursorBookmark;
import mx.collections.ICollectionView;
import mx.collections.IList;
import mx.collections.IViewCursor;
import mx.collections.ListCollectionView;
import mx.collections.XMLListCollection;
import mx.core.EdgeMetrics;
import mx.core.FlexVersion;
import mx.core.IFlexDisplayObject;
import mx.core.IIMESupport;
import mx.core.IRectangularBorder;
import mx.core.IUITextField;
import mx.core.UIComponent;
import mx.core.UITextField;
import mx.core.mx_internal;
import mx.events.CollectionEvent;
import mx.events.CollectionEventKind;
import mx.events.FlexEvent;
import mx.managers.IFocusManager;
import mx.managers.IFocusManagerComponent;
import mx.styles.ISimpleStyleClient;
import mx.styles.StyleProxy;
import mx.utils.UIDUtil;


use namespace mx_internal;

/**
 *  Name of the class to use as the default skin for the background and border. 
 *  For the ComboBase class, there is no default value.
 *  For the ComboBox class, the default value is the ComboBoxArrowSkin class.
 */
[Style(name="skin", type="Class", inherit="no", states=" up, over, down, disabled,  editableUp, editableOver, editableDown, editableDisabled")]

/**
 *  Name of the class to use as the skin for the background and border
 *  when the mouse is not over the control.
 *  For the ComboBase class, there is no default value.
 *  For the ComboBox class, the default value is the ComboBoxArrowSkin class.
 */
[Style(name="upSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the background and border
 *  when the mouse is over the control.
 *  For the ComboBase class, there is no default value.
 *  For the ComboBox class, the default value is the ComboBoxArrowSkin class.
 *  For the ColorPicker class, the default value is the ColorPickerSkin class.
 *  For the DateField class, the default value is the ScrollArrowDownSkin class.
 */
[Style(name="overSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the background and border
 *  when the user holds down the mouse button.
 *  For the ComboBase class, there is no default value.
 *  For the ComboBox class, the default value is the ComboBoxArrowSkin class.
 *  For the ColorPicker class, the default value is the ColorPickerSkin class.
 *  For the DateField class, the default value is the ScrollArrowDownSkin class.
 */
[Style(name="downSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the background and border
 *  when the control is disabled.
 *  For the ComboBase class, there is no default value.
 *  For the ComboBox class, the default value is the ComboBoxArrowSkin class.
 *  For the ColorPicker class, the default value is the ColorPickerSkin class.
 *  For the DateField class, the default value is the ScrollArrowDownSkin class.
 */
[Style(name="disabledSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the background and border
 *  when the mouse is not over the control, and the <code>editable</code>
 *  property is <code>true</code>. This skin is only used by the ComboBox class.
 *  For the ComboBase class, there is no default value.
 *  For the ComboBox class, the default value is the ComboBoxArrowSkin class.
 */
[Style(name="editableUpSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the background and border
 *  when the mouse is over the control, and the <code>editable</code>
 *  property is <code>true</code>. This skin is only used by the ComboBox class.
 *  For the ComboBase class, there is no default value.
 *  For the ComboBox class, the default value is the ComboBoxArrowSkin class.
 */
[Style(name="editableOverSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the background and border
 *  when the user holds down the mouse button, and the <code>editable</code>
 *  property is <code>true</code>. This skin is only used by the ComboBox class.
 *  For the ComboBase class, there is no default value.
 *  For the ComboBox class, the default value is the ComboBoxArrowSkin class.
 */
[Style(name="editableDownSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the background and border
 *  when the control is disabled, and the <code>editable</code>
 *  property is <code>true</code>. This skin is only used by the ComboBox class.
 *  For the ComboBase class, there is no default value.
 *  For the ComboBox class, the default value is the ComboBoxArrowSkin class.
 */
[Style(name="editableDisabledSkin", type="Class", inherit="no")]

/**
 *  The style declaration for the internal TextInput subcomponent 
 *  that displays the current selection. 
 *  If no value is specified, then the TextInput subcomponent uses 
 *  the default text styles defined by the ComboBase class.
 *
 *  @default ""
 */
[Style(name="textInputStyleName", type="String", inherit="no")]
//--------------------------------------
//  Other metadata
//--------------------------------------

[AccessibilityClass(implementation="mx.accessibility.ComboBaseAccImpl")]

/**
 *  The ComboBase class is the base class for controls that display text in a 
 *  text field and have a button that causes a drop-down list to appear where 
 *  the user can choose which text to display.
 *  The ComboBase class is not used directly as an MXML tag.
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:ComboBase&gt;</code> tag inherits all the tag attributes
 *  of its superclass, and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;<i>mx:tagname</i>
 *    <b>Properties</b>
 *    dataProvider="null"
 *    editable="false|true"
 *    imeMode="null"
 *    restrict="null"
 *    selectedIndex="-1"
 *    selectedItem="null"
 *    text=""
 *    &nbsp;
 *    <b>Styles</b>
 *    disabledSkin="<i>Depends on class</i>"
 *    downSkin="<i>Depends on class</i>"
 *    editableDisabledSkin="<i>Depends on class</i>"
 *    editableDownSkin="<i>Depends on class</i>"
 *    editableOverSkin="<i>Depends on class</i>"
 *    editableUpSkin="<i>Depends on class</i>"
 *    overSkin="<i>Depends on class</i>"
 *    textInputStyleName="" 
 *    upSkin="<i>Depends on class</i>"
 *
 *  /&gt;
 *  </pre>
 *
 *  @see mx.controls.Button
 *  @see mx.controls.TextInput
 *  @see mx.collections.ICollectionView
 */
public class ComboBase extends UIComponent implements IIMESupport, IFocusManagerComponent
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class mixins
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Placeholder for mixin by ComboBaseAccImpl.
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
    public function ComboBase()
    {
        super();

        tabEnabled = true;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  The ICollectionView of items this component displays.  
     */
    protected var collection:ICollectionView;

    /**
     *  The main IViewCursor used to fetch items from the
     *  dataProvider and pass the items to the renderers.
     *  At the end of any sequence of code, it must always be positioned
     *  at the topmost visible item on screen.
     */
    protected var iterator:IViewCursor;

    /**
     *  @private
     *  A separate IViewCursor used to find indices of items and other things.
     *  The collectionIterator can be at any place within the set of items.
     */
    mx_internal var collectionIterator:IViewCursor;

    /**
     *  @private
     *  The internal object that draws the border.
     */
    mx_internal var border:IFlexDisplayObject;

    /**
     *  @private
     *  The internal Button property that causes the drop-down list to appear.
     */
    mx_internal var downArrowButton:Button;

    /**
     *  @private
     */
    mx_internal var wrapDownArrowButton:Boolean = true;

    /**
     *  @private
     */
    mx_internal var useFullDropdownSkin:Boolean = false;

    /**
     *  @private
     */
    private var selectedUID:String;

    /**
     *  @private
     *  A flag indicating that selection has changed
     */
    mx_internal var selectionChanged:Boolean = false;

    /**
     *  @private
     *  A flag indicating that selectedIndex has changed
     */
    mx_internal var selectedIndexChanged:Boolean = false;

    /**
     *  @private
     *  A flag indicating that selectedItem has changed
     */
    mx_internal var selectedItemChanged:Boolean = false;

    /**
     *  @private
     *  Stores the old value of the borderStyle style
     */
    mx_internal var oldBorderStyle:String;

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
     *  The baselinePosition of a ComboBase is calculated for its TextInput.
     */
    override public function get baselinePosition():Number
    {
        if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
            return textInput.y  + textInput.baselinePosition;
            
        if (!validateBaselinePosition())
            return NaN;

        return textInput.y  + textInput.baselinePosition;
    }

    //----------------------------------
    //  enabled
    //----------------------------------

    /**
     *  @private
     *  Storage for enabled property.
     */
    private var _enabled:Boolean = false;

    /**
     *  @private
     */
    private var enabledChanged:Boolean = false;

    [Inspectable(category="General", enumeration="true,false", defaultValue="true")]

    /**
     *  @private
     */
    override public function set enabled(value:Boolean):void
    {
        super.enabled = value;
        _enabled = value;

        enabledChanged = true;
        invalidateProperties();
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

     //----------------------------------
    //  arrowButtonStyleFilters
    //----------------------------------
    
    /**
     *  Set of styles to pass from the ComboBase to the down arrow button
     *  @see mx.styles.StyleProxy
     */ 
    protected function get arrowButtonStyleFilters():Object
    {
        return null;
    }
    //----------------------------------
    //  borderMetrics
    //----------------------------------

    /**
     *  Returns an EdgeMetrics object that has four properties:
     *  <code>left</code>, <code>top</code>, <code>right</code>,
     *  and <code>bottom</code>.
     *  The value of each property is equal to the thickness of the
     *  corresponding side of the border, expressed in pixels.
     *
     *  @return EdgeMetrics object with the left, right, top,
     *  and bottom properties.
     */
    protected function get borderMetrics():EdgeMetrics
    {
        if (border && border is IRectangularBorder)
            return IRectangularBorder(border).borderMetrics;

        return EdgeMetrics.EMPTY;
    }

    //----------------------------------
    //  dataProvider
    //----------------------------------

    [Bindable("collectionChange")]
    [Inspectable(category="Data")]

    /**
     *  The set of items this component displays. This property is of type
     *  Object because the derived classes can handle a variety of data
     *  types such as Arrays, XML, ICollectionViews, and other classes.  All
     *  are converted into an ICollectionView and that ICollectionView is
     *  returned if you get the value of this property; you will not get the
     *  value you set if it was not an ICollectionView.
     *
     *  <p>Setting this property will adjust the <code>selectedIndex</code>
     *  property (and therefore the <code>selectedItem</code> property) if 
     *  the <code>selectedIndex</code> property has not otherwise been set. 
     *  If there is no <code>prompt</code> property, the <code>selectedIndex</code>
     *  property will be set to 0; otherwise it will remain at -1,
     *  the index used for the prompt string.  
     *  If the <code>selectedIndex</code> property has been set and
     *  it is out of range of the new data provider, unexpected behavior is
     *  likely to occur.</p>
     * 
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
        else
        {
            // convert it to an array containing this one item
            var tmp:Array = [value];
            collection = new ArrayCollection(tmp);
        }
        // get an iterator for the displaying rows.  The CollectionView's
        // main iterator is left unchanged so folks can use old DataSelector
        // methods if they want to
        iterator = collection.createCursor();
        collectionIterator = collection.createCursor(); //IViewCursor(collection);

        // trace("ListBase added change listener");
        // weak listeners to collections and dataproviders
        collection.addEventListener(CollectionEvent.COLLECTION_CHANGE, collectionChangeHandler, false, 0, true);

        var event:CollectionEvent =
            new CollectionEvent(CollectionEvent.COLLECTION_CHANGE);
        event.kind = CollectionEventKind.RESET;
        collectionChangeHandler(event);
        dispatchEvent(event);

        invalidateSize();
        invalidateDisplayList();
    }

    //----------------------------------
    //  editable
    //----------------------------------

    /**
     *  @private
     *  Storage for editable property.
     */
    private var _editable:Boolean = false;

    /**
     *  @private
     */
    mx_internal var editableChanged:Boolean = true;

    [Bindable("editableChanged")]
    [Inspectable(category="General", defaultValue="false")]

    /**
     *  A flag that indicates whether the control is editable, 
     *  which lets the user directly type entries that are not specified 
     *  in the dataProvider, or not editable, which requires the user select
     *  from the items in the dataProvider.
     *
     *  <p>If <code>true</code> keyboard input will be entered in the
     *  editable text field; otherwise it will be used as shortcuts to
     *  select items in the dataProvider.</p>
     *
     *  @default false.
     *  This property is ignored by the DateField control.
     *
     */
    public function get editable():Boolean
    {
        return _editable;
    }

    /**
     *  @private
     */
    public function set editable(value:Boolean):void
    {
        _editable = value;
        editableChanged = true;

        invalidateProperties();

        dispatchEvent(new Event("editableChanged"));
    }

    //----------------------------------
    //  imeMode
    //----------------------------------

    /**
     *  @private
     */
    private var _imeMode:String = null;

    /**
     *  @copy mx.controls.TextInput#imeMode
     * 
     *  @default null
     **/
    public function get imeMode():String
    {
        return _imeMode;
    }

    /**
     *  @private
     */
    public function set imeMode(value:String):void
    {
        _imeMode = value;

        if (textInput)
            textInput.imeMode = _imeMode;
    }

    //----------------------------------
    //  restrict
    //----------------------------------

    /**
     *  @private
     *  Storage for restrict property.
     */
    private var _restrict:String;

    [Bindable("restrictChanged")]
    [Inspectable(category="Other")]

    /**
     *  Set of characters that a user can or cannot enter into the text field.
     * 
     *  @default null
     *
     *  @see flash.text.TextField#restrict
     *
     */
    public function get restrict():String
    {
        return _restrict;
    }

    /**
     *  @private
     */
    public function set restrict(value:String):void
    {
        _restrict = value;

        invalidateProperties();

        dispatchEvent(new Event("restrictChanged"));
    }

    //----------------------------------
    //  selectedIndex
    //----------------------------------

    private var _selectedIndex:int = -1;

    [Bindable("change")]
    [Bindable("valueCommit")]
    [Inspectable(category="General", defaultValue="-1")]

    /**
     *  The index in the data provider of the selected item.
     *  If there is a <code>prompt</code> property, the <code>selectedIndex</code>
     *  value can be set to -1 to show the prompt.
     *  If there is no <code>prompt</code>, property then <code>selectedIndex</code>
     *  will be set to 0 once a <code>dataProvider</code> is set.
     *
     *  <p>If the ComboBox control is editable, the <code>selectedIndex</code>
     *  property is -1 if the user types any text
     *  into the text field.</p>
     *
     *  <p>Unlike many other Flex properties that are invalidating (setting
     *  them does not have an immediate effect), the <code>selectedIndex</code> and
     *  <code>selectedItem</code> properties are synchronous; setting one immediately 
     *  affects the other.</p>
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
        if (value == -1)
        {
            _selectedItem = null;
            selectedUID = null;
        }

        //2 code paths: one for before collection, one after
        if (!collection || collection.length == 0)
        {
            selectedIndexChanged = true;
        }
        else
        {
            if (value != -1)
            {
                value = Math.min(value, collection.length - 1);
                var bookmark:CursorBookmark = iterator.bookmark;
                var len:int = value;
                iterator.seek(CursorBookmark.FIRST, len);
                var data:Object = iterator.current;
                var uid:String = itemToUID(data);
                iterator.seek(bookmark, 0);
                _selectedIndex = value;
                _selectedItem = data;
                selectedUID = uid;
            }
        }

        selectionChanged = true;

        invalidateDisplayList();

        dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));
    }


    //----------------------------------
    //  selectedItem
    //----------------------------------

    /**
     *  @private
     *  Storage for the selectedItem property.
     */
    private var _selectedItem:Object;

    [Bindable("change")]
    [Bindable("valueCommit")]
    [Inspectable(environment="none")]

    /**
     *  The item in the data provider at the selectedIndex.
     *
     *  <p>If the data is an object or class instance, modifying
     *  properties in the object or instance modifies the 
     *  <code>dataProvider</code> object but may not update the views  
     *  unless the instance is Bindable or implements IPropertyChangeNotifier
     *  or a call to dataProvider.itemUpdated() occurs.</p>
     *
     *  Setting the <code>selectedItem</code> property causes the
     *  ComboBox control to select that item (display it in the text field and
     *  set the <code>selectedIndex</code>) if it exists in the data provider.
     *  If the ComboBox control is editable, the <code>selectedItem</code>
     *  property is <code>null</code> if the user types any text
     *  into the text field.
     *
     *  <p>Unlike many other Flex properties that are invalidating (setting
     *  them does not have an immediate effect), <code>selectedIndex</code> and
     *  <code>selectedItem</code> are synchronous; setting one immediately 
     *  affects the other.</p>
     *
     *  @default null;
     */
    public function get selectedItem():Object
    {
        return _selectedItem;
    }

    /**
     *  @private
     */
    public function set selectedItem(data:Object):void
    {
        setSelectedItem(data);
    }

    /**
     *  @private
     */
    private function setSelectedItem(data:Object, clearFirst:Boolean = true):void
    {
        //2 code paths: one for before collection, one after
        if (!collection || collection.length == 0)
        {
            _selectedItem = value;
            selectedItemChanged = true;
            invalidateDisplayList();
            return;
        }

        var found:Boolean = false;
        var listCursor:IViewCursor = collection.createCursor();
        var i:int = 0;
        do
        {
            if (data == listCursor.current)
            {
                _selectedIndex = i;
                _selectedItem = data;
                selectedUID = itemToUID(data);
                selectionChanged = true;
                found = true;
                break;
            }
            i++;
        }
        while (listCursor.moveNext());

        if (!found)
        {
            selectedIndex = -1;
            _selectedItem = null;
            selectedUID = null;
        }

        invalidateDisplayList();
    }

    //----------------------------------
    //  text
    //----------------------------------

    /**
     *  @private
     *  Storage for the text property.
     */
    private var _text:String = "";

    /**
     *  @private
     */
    mx_internal var textChanged:Boolean;

    [Bindable("collectionChange")]
    [Bindable("valueCommit")]
    [Inspectable(category="General", defaultValue="")]
    [NonCommittingChangeEvent("change")]

    /**
     *  Contents of the text field.  If the control is non-editable
     *  setting this property has no effect. If the control is editable, 
     *  setting this property sets the contents of the text field.
     *
     *  @default ""
     */
    public function get text():String
    {
        return _text;
    }

    /**
     *  @private
     */
    public function set text(value:String):void
    {
        _text = value;
        textChanged = true;

        invalidateProperties();

        dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));
    }

    //----------------------------------
    //  textInput
    //----------------------------------

    /**
     *  The internal TextInput subcomponent that displays
     *  the current selection.
     */
    protected var textInput:TextInput;


    //----------------------------------
    //  textInputStyleFilters
    //----------------------------------

    /**
     *  The set of styles to pass from the ComboBase to the text input. 
     *  These styles are ignored if you set 
     *  the <code>textInputStyleName</code> style property.
     *  @see mx.styles.StyleProxy
     */
    protected function get textInputStyleFilters():Object 
    {
        return _textInputStyleFilters;
    }
    
    private static var _textInputStyleFilters:Object =
    {
        "backgroundAlpha" : "backgroundAlpha",
        "backgroundColor" : "backgroundColor",
        "backgroundImage" : "backgroundImage",
        "backgroundDisabledColor" : "backgroundDisabledColor",
        "backgroundSize" : "backgroundSize",
        "borderAlpha" : "borderAlpha", 
        "borderColor" : "borderColor",
        "borderSides" : "borderSides", 
        "borderSkin" : "borderSkin",
        "borderStyle" : "borderStyle",
        "borderThickness" : "borderThickness",
        "dropShadowColor" : "dropShadowColor",
        "dropShadowEnabled" : "dropShadowEnabled",
        "embedFonts" : "embedFonts",
        "focusAlpha" : "focusAlpha",
        "focusBlendMode" : "focusBlendMode",
        "focusRoundedCorners" : "focusRoundedCorners", 
        "focusThickness" : "focusThickness",
        "leading" : "leading",
        "paddingLeft" : "paddingLeft", 
        "paddingRight" : "paddingRight",
        "shadowDirection" : "shadowDirection",
        "shadowDistance" : "shadowDistance",
        "textDecoration" : "textDecoration"
     };

    //----------------------------------
    //  value
    //----------------------------------

    [Bindable("change")]
    [Bindable("valueCommit")]

    /**
     *  The value of the selected item. If the item is a Number or String,
     *  the value is the item. If the item is an object, the value is
     *  the <code>data</code> property, if it exists, or the <code>label</code>
     *  property, if it exists.
     *
     *  <p><strong>Note:</strong> Using the <code>selectedItem</code> property 
     *  is often preferable to using this property. The <code>value</code>
     *  property exists for backward compatibility with older applications.</p>
     *
     */
    public function get value():Object
    {
        if (_editable)
            return text;

        var item:Object = selectedItem;

        if (item == null || typeof(item) != "object")
            return item;

        // Note: the explicit comparison with null is important, because otherwise when
        // the data is zero, the label will be returned.  See bug 183294 for an example.
        return item.data != null ? item.data : item.label;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function initializeAccessibility():void
    {
        if (ComboBase.createAccessibilityImplementation != null)
            ComboBase.createAccessibilityImplementation(this);
    }

    /**
     *  @private
     */
    override protected function createChildren():void
    {
        super.createChildren();

        // Create the border first, in the back.
        if (!border)
        {
            var borderClass:Class = getStyle("borderSkin");

            if (borderClass)
            {
                border = new borderClass();

                if (border is IFocusManagerComponent)
                    IFocusManagerComponent(border).focusEnabled = false;

                if (border is ISimpleStyleClient)
                    ISimpleStyleClient(border).styleName = this;

                addChild(DisplayObject(border));
            }
        }

        // Next, create the downArrowButton before creating the textInput,
        // because it can be as large as the entire control.
        if (!downArrowButton)
        {
            downArrowButton = new Button();
            downArrowButton.styleName = new StyleProxy(this, arrowButtonStyleFilters);
            downArrowButton.focusEnabled = false;

            addChild(downArrowButton);

            downArrowButton.addEventListener(FlexEvent.BUTTON_DOWN,
                                             downArrowButton_buttonDownHandler);

        }

        // Create the textInput on top.
        if (!textInput)
        {
            var textInputStyleName:Object = getStyle("textInputStyleName");
            if (!textInputStyleName)
                textInputStyleName = new StyleProxy(this, textInputStyleFilters);
            
            textInput = new TextInput();

            textInput.editable = _editable;
            editableChanged = true;
            // Don't show ESC characters in the text field.
            textInput.restrict = "^\u001b";
            textInput.focusEnabled = false;
            textInput.imeMode = _imeMode;
            textInput.styleName = textInputStyleName;

            textInput.addEventListener(Event.CHANGE, textInput_changeHandler);
            textInput.addEventListener(FlexEvent.ENTER, textInput_enterHandler);
            textInput.addEventListener(FocusEvent.FOCUS_IN, focusInHandler);
            textInput.addEventListener(FocusEvent.FOCUS_OUT, focusOutHandler);
            textInput.addEventListener(FlexEvent.VALUE_COMMIT,
                                       textInput_valueCommitHandler);

            addChild(textInput);

            textInput.move(0, 0);

            textInput.parentDrawsFocus = true;
        }
    }

    /**
     *  @private
     */
    override public function styleChanged(styleProp:String):void
    {
        if (downArrowButton)
            downArrowButton.styleChanged(styleProp);

        if (textInput)
            textInput.styleChanged(styleProp);

        if (border && border is ISimpleStyleClient)
            ISimpleStyleClient(border).styleChanged(styleProp);

        super.styleChanged(styleProp);
    }

    /**
     *  @private
     */
    override protected function commitProperties():void
    {
        super.commitProperties();

        // A TextField interprets restrict="" as meaning
        // don't allow any characters, and restrict = null
        // as meaning allow all characters.
        // The former is useless (the TextField should be disabled
        // instead) and the latter is useful, but MXML doesn't
        // have a way to specify restrict = null. So Flex changes
        // the semantics of restrict="" to mean allow all characters.
        textInput.restrict = _restrict;

        if (textChanged)
        {
            textInput.text = _text;
            textChanged = false;
        }

        if (enabledChanged)
        {
            textInput.enabled = _enabled;
            editableChanged = true;
            downArrowButton.enabled = _enabled;
            enabledChanged = false;
        }

        if (editableChanged)
        {
            editableChanged = false;
            var e:Boolean = _editable;
            
            if (wrapDownArrowButton == false)
            {
                if (e)
                {
                    if (oldBorderStyle)
                        setStyle("borderStyle", oldBorderStyle);
                }
                else
                {
                    oldBorderStyle = getStyle("borderStyle");
                    setStyle("borderStyle", "comboNonEdit");
                }
            }
            
            // Swap the button skins if we have different skins for editable and
            // non-editable states.
            if (useFullDropdownSkin)
            {
                downArrowButton.upSkinName = e ? "editableUpSkin" : "upSkin";
                downArrowButton.overSkinName = e ? "editableOverSkin" : "overSkin";
                downArrowButton.downSkinName = e ? "editableDownSkin" : "downSkin";
                downArrowButton.disabledSkinName = e ? "editableDisabledSkin" : "disabledSkin";
                downArrowButton.changeSkins();
                downArrowButton.invalidateDisplayList();
            }
            
            if (textInput)
            {
                textInput.editable = e;
                textInput.selectable = e;
                if (e)
                {
                    textInput.removeEventListener(MouseEvent.MOUSE_DOWN,
                                                  textInput_mouseEventHandler);
                    textInput.removeEventListener(MouseEvent.MOUSE_UP,
                                                  textInput_mouseEventHandler);
                    textInput.removeEventListener(MouseEvent.ROLL_OVER,
                                                  textInput_mouseEventHandler);
                    textInput.removeEventListener(MouseEvent.ROLL_OUT,
                                                  textInput_mouseEventHandler);
                    textInput.addEventListener(KeyboardEvent.KEY_DOWN,
                                               keyDownHandler);
                }
                else
                {
                    // Trap all mouse actions on the textfield and feed them to the button
                    textInput.addEventListener(MouseEvent.MOUSE_DOWN,
                                               textInput_mouseEventHandler);
                    textInput.addEventListener(MouseEvent.MOUSE_UP,
                                              textInput_mouseEventHandler);
                    textInput.addEventListener(MouseEvent.ROLL_OVER,
                                               textInput_mouseEventHandler);
                    textInput.addEventListener(MouseEvent.ROLL_OUT,
                                               textInput_mouseEventHandler);
                    textInput.removeEventListener(KeyboardEvent.KEY_DOWN,
                                                  keyDownHandler);
                }
            }
        }
    }

    /**
     *  Determines the <code>measuredWidth</code> and
     *  <code>measuredHeight</code> properties of the control.
     *  The measured width is the width of the widest text
     *  in the <code>dataProvider</code>
     *  plus the width of the drop-down button.
     *  The measured height is the larger of either the button or the text.
     *  If no data provider has been set or there are no items
     *  in the data provider, the <code>measuredWidth</code> property is set to
     *  <code>UIComponent.DEFAULT_MEASURED_WIDTH</code> and the 
     *  <code>measuredHeight</code> property is set
     *  to <code>UIComponent.DEFAULT_MEASURED_HEIGHT</code>.
     * 
     *  @see mx.core.UIComponent#measure()
     */
    override protected function measure():void
    {
        super.measure();

        var buttonWidth:Number = getStyle("arrowButtonWidth");
        var buttonHeight:Number = downArrowButton.getExplicitOrMeasuredHeight();

        // Text fields have 4 pixels of white space added to each side
        // by the player, so fudge this amount.
        // If we don't have any data, measure a single space char for defaults
        if (collection && collection.length > 0)
        {
            var prefSize:Object = calculatePreferredSizeFromData(collection.length);

            var bm:EdgeMetrics = borderMetrics;

            var textWidth:Number = prefSize.width + bm.left + bm.right + 8;
            var textHeight:Number = prefSize.height + bm.top + bm.bottom 
                        + UITextField.TEXT_HEIGHT_PADDING;

            measuredMinWidth = measuredWidth = textWidth + buttonWidth;
            measuredMinHeight = measuredHeight = Math.max(textHeight, buttonHeight);
        }
        else
        {
            measuredMinWidth = DEFAULT_MEASURED_MIN_WIDTH;
            measuredWidth = DEFAULT_MEASURED_WIDTH;
            measuredMinHeight = DEFAULT_MEASURED_MIN_HEIGHT;
            measuredHeight = DEFAULT_MEASURED_HEIGHT;
        }
        
        if (FlexVersion.compatibilityVersion >= FlexVersion.VERSION_3_0)
        {
            // Add in the paddingTop and paddingBottom values
            var padding:Number = getStyle("paddingTop") + getStyle("paddingBottom");
            measuredMinHeight += padding;
            measuredHeight += padding;
        }
    }

    /**
     *  Sizes and positions the internal components in the given width
     *  and height.  The drop-down button is placed all the way to the right
     *  and the text field fills the remaining area.
     * 
     *  @param unscaledWidth Specifies the width of the component, in pixels,
     *  in the component's coordinates, regardless of the value of the
     *  <code>scaleX</code> property of the component.
     *
     *  @param unscaledHeight Specifies the height of the component, in pixels,
     *  in the component's coordinates, regardless of the value of the
     *  <code>scaleY</code> property of the component.
     * 
     *  @see mx.core.UIComponent#updateDisplayList()
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        var w:Number = unscaledWidth;
        var h:Number = unscaledHeight;

        var arrowWidth:Number = getStyle("arrowButtonWidth");
        var textInputHeight:Number = textInput.getExplicitOrMeasuredHeight();

        if (isNaN(arrowWidth))
            arrowWidth = 0;

        if (wrapDownArrowButton)
        {
            var vm:EdgeMetrics = borderMetrics;
            var wh:Number = h - vm.top - vm.bottom;
            downArrowButton.setActualSize(wh, wh);
            downArrowButton.move(w - arrowWidth - vm.right, vm.top);
            border.setActualSize(w, h);
            textInput.setActualSize(w - arrowWidth, textInputHeight);
        }
        else
        {
            if (!_editable && useFullDropdownSkin)
            {
                var paddingTop:Number = getStyle("paddingTop");
                var paddingBottom:Number = getStyle("paddingBottom");
                
                downArrowButton.move(0, 0);
                border.setActualSize(w, h);
                textInput.setActualSize(w - arrowWidth, textInputHeight);
                textInput.border.visible = false;
                if (FlexVersion.compatibilityVersion >= FlexVersion.VERSION_3_0)
                    textInput.move(textInput.x, ((h - textInputHeight - paddingTop - paddingBottom) / 2) + paddingTop);
                downArrowButton.setActualSize(unscaledWidth, unscaledHeight);
            }
            else
            {
                downArrowButton.move(w - arrowWidth, 0);
                border.setActualSize(w - arrowWidth, h);
                textInput.setActualSize(w - arrowWidth, h);
                downArrowButton.setActualSize(arrowWidth, unscaledHeight);
                textInput.border.visible = true;
            }
        }
        

        if (selectedIndexChanged)
        {
            selectedIndex = selectedIndex;
            selectedIndexChanged = false;
        }

        if (selectedItemChanged)
        {
            selectedItem = selectedItem;
            selectedItemChanged = false;
        }
    }

    /**
     *  @private
     */
    override public function setFocus():void
    {
        if (textInput && _editable)
            textInput.setFocus();
        else
            super.setFocus();
    }

    /**
     *  @private
     */
    override protected function isOurFocus(target:DisplayObject):Boolean
    {
        return target == textInput || super.isOurFocus(target);
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Determines default values of the height and width to use for the 
     *  entries in the drop-down list. 
     *  Each subclass of ComboBase must implement this method and return 
     *  an Object containing two properties: <code>width</code> and 
     *  <code>height</code>.
     *
     *  @param numItems The number of items to check to determine the size.
     *
     *  @return An Object with <code>width</code> and <code>height</code> 
     *  properties.
     */
    protected function calculatePreferredSizeFromData(numItems:int):Object
    {
        return null;
    }

    /**
     *  Determines the UID for a dataProvider item.
     *  Every dataProvider item must have or will be assigned a unique
     *  identifier (UID).
     *
     *  @param data A dataProvider item.
     *
     *  @return A unique identifier.
     */
    protected function itemToUID(data:Object):String
    {
        if (!data)
            return "null";

        return UIDUtil.getUID(data);
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function focusInHandler(event:FocusEvent):void
    {
        super.focusInHandler(event);

        var fm:IFocusManager = focusManager;
        if (fm)
            fm.defaultButtonEnabled = false;
    }

    /**
     *  @private
     */
    override protected function focusOutHandler(event:FocusEvent):void
    {
        super.focusOutHandler(event);

        var fm:IFocusManager = focusManager;

        if (fm)
            fm.defaultButtonEnabled = true;

        if (_editable)
            dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  Responds to changes to the data provider.  The component will adjust
     *  the <code>selectedIndex</code> property if items are added or removed 
     *  before the component's selected item.
     *
     *  @param event The CollectionEvent dispatched from the collection.
     *
     *  @see mx.events.CollectionEvent
     */
    protected function collectionChangeHandler(event:Event):void
    {
        if (event is CollectionEvent)
        {
            var requiresValueCommit:Boolean = false;

            var len:Number;

            var ind:Object;

            var ce:CollectionEvent = CollectionEvent(event);
            if (ce.kind == CollectionEventKind.ADD)
            {
                if (selectedIndex >= ce.location)
                    _selectedIndex++;
            }
            if (ce.kind == CollectionEventKind.REMOVE)
            {
                for (var i:int = 0; i < ce.items.length; i++)
                {
                    var uid:String = itemToUID(ce.items[i]);
                    if (selectedUID == uid)
                    {
                        selectionChanged = true;
                    }
                }
                if (selectionChanged)
                {
                    if (_selectedIndex >= collection.length)
                        _selectedIndex = collection.length - 1;

                    selectedIndexChanged = true;
                    requiresValueCommit = true;
                    invalidateDisplayList();
                }
                else if (selectedIndex >= ce.location)
                {
                    _selectedIndex--;
                    selectedIndexChanged = true;
                    requiresValueCommit = true;
                    invalidateDisplayList();
                }
            
            }
            if (ce.kind == CollectionEventKind.REFRESH)
            {
                selectedItemChanged = true;
                // Sorting always changes the selection array
                requiresValueCommit = true;
            }

            invalidateDisplayList();

            if (requiresValueCommit)
                dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));
        }
    }

    /**
     *  @private
     *  Forward an event to the down arrow button.
     */
    private function textInput_mouseEventHandler(event:Event):void
    {
        downArrowButton.dispatchEvent(event);
    }

    /**
     *  Handles changes to the TextInput that serves as the editable
     *  text field in the component.  The method sets 
     *  <code>selectedIndex</code> to -1 (and therefore 
     *  <code>selectedItem</code> to <code>null</code>).
     * 
     *  @param event The event that is triggered each time the text in the control changes.     
     */
    protected function textInput_changeHandler(event:Event):void
    {
        // update _text as the user types
        _text = textInput.text;
        if (_selectedIndex != -1)
        {
            _selectedIndex = -1;
            _selectedItem = null;
            selectedUID = null;
        }
    }

    /**
     *  @private
     *  valueCommit handler for the textInput
     */
    private function textInput_valueCommitHandler(event:FlexEvent):void
    {
        // update _text if textInput.text is changed programatically
        _text = textInput.text;
        dispatchEvent(event);
    }

    /**
     *  @private
     */
    private function textInput_enterHandler(event:FlexEvent):void
    {
        dispatchEvent(event);
        dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));
    }

    /**
     *  Performs some action when the drop-down button is pressed.  This is
     *  an abstract base class implementation, so it has no effect and is
     *  overridden by the subclasses.
     * 
     *  @param event The event that is triggered when the drop-down button is pressed.     
     */
    protected function downArrowButton_buttonDownHandler(event:FlexEvent):void
    {
        // overridden by subclasses
    }

    /**
     *  @private
     */
    mx_internal function getTextInput():TextInput
    {
        return textInput;
    }

    /**
     *  @private
     */
    mx_internal function get ComboDownArrowButton():Button
    {
        return downArrowButton;
    }


}

}
