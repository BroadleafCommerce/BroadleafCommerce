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
import flash.events.TextEvent;
import flash.text.TextField;
import flash.text.TextLineMetrics;
import flash.ui.Keyboard;
import mx.controls.listClasses.BaseListData;
import mx.controls.listClasses.IDropInListItemRenderer;
import mx.controls.listClasses.IListItemRenderer;
import mx.core.FlexVersion;
import mx.core.IDataRenderer;
import mx.core.IFlexDisplayObject;
import mx.core.IIMESupport;
import mx.core.UIComponent;
import mx.core.UITextField;
import mx.core.mx_internal;
import mx.events.FlexEvent;
import mx.events.NumericStepperEvent;
import mx.managers.IFocusManager;
import mx.managers.IFocusManagerComponent;
import mx.styles.StyleProxy;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when the value of the NumericStepper control changes
 *  as a result of user interaction.
 *
 *  @eventType mx.events.NumericStepperEvent.CHANGE
 */
[Event(name="change", type="mx.events.NumericStepperEvent")]

/**
 *  Dispatched when the <code>data</code> property changes.
 *
 *  <p>When you use a component as an item renderer,
 *  the <code>data</code> property contains the data to display.
 *  You can listen for this event and update the component
 *  when the <code>data</code> property changes.</p>
 * 
 *  @eventType mx.events.FlexEvent.DATA_CHANGE
 */
[Event(name="dataChange", type="mx.events.FlexEvent")]

//--------------------------------------
//  Styles
//--------------------------------------

include "../styles/metadata/BorderStyles.as"
include "../styles/metadata/FocusStyles.as"
include "../styles/metadata/IconColorStyles.as"
include "../styles/metadata/LeadingStyle.as"
include "../styles/metadata/PaddingStyles.as"
include "../styles/metadata/TextStyles.as"

/**
 *  Name of the class to use as the default skin for the down arrow.
 * 
 *  @default mx.skins.halo.NumericStepperDownSkin
 */
[Style(name="downArrowSkin", type="Class", inherit="no", states="up, over, down, disabled")]

/**
 *  Name of the class to use as the skin for the Down arrow
 *  when the arrow is disabled.
 *
 *  @default mx.skins.halo.NumericStepperDownSkin
 */
[Style(name="downArrowDisabledSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the Down arrow
 *  when the arrow is enabled and a user presses the mouse button over the arrow.
 *
 *  @default mx.skins.halo.NumericStepperDownSkin
 */
[Style(name="downArrowDownSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the Down arrow
 *  when the arrow is enabled and the mouse pointer is over the arrow.
 *  
 *  @default mx.skins.halo.NumericStepperDownSkin
 */
[Style(name="downArrowOverSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the Down arrow
 *  when the arrow is enabled and the mouse pointer is not on the arrow.
 *  There is no default.
 */
[Style(name="downArrowUpSkin", type="Class", inherit="no")]

/**
 *  Alphas used for the highlight fill of controls.
 *
 *  @default [ 0.3, 0.0 ]
 */
[Style(name="highlightAlphas", type="Array", arrayType="Number", inherit="no")]

/**
 *  Name of the class to use as the default skin for the up arrow.
 *  
 *  @default mx.skins.halo.NumericStepperUpSkin
 */
[Style(name="upArrowSkin", type="Class", inherit="no", states="up, over, down, disabled")]

/**
 *  Name of the class to use as the skin for the Up arrow
 *  when the arrow is disabled.
 *
 *  @default mx.skins.halo.NumericStepperUpSkin
 */
[Style(name="upArrowDisabledSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the Up arrow
 *  when the arrow is enabled and a user presses the mouse button over the arrow.
 *
 *  @default mx.skins.halo.NumericStepperUpSkin
 */
[Style(name="upArrowDownSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the Up arrow
 *  when the arrow is enabled and the mouse pointer is over the arrow.
 *
 *  @default mx.skins.halo.NumericStepperUpSkin
 */
[Style(name="upArrowOverSkin", type="Class", inherit="no")]

/**
 *  Name of the class to use as the skin for the Up arrow
 *  when the arrow is enabled and the mouse pointer is not on the arrow.
 *
 *  @default mx.skins.halo.NumericStepperUpSkin
 */
[Style(name="upArrowUpSkin", type="Class", inherit="no")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[IconFile("NumericStepper.png")]

[DefaultBindingProperty(source="value", destination="value")]

[DefaultTriggerEvent("change")]

/**
 *  The NumericStepper control lets the user select
 *  a number from an ordered set.
 *  The NumericStepper control consists of a single-line
 *  input text field and a pair of arrow buttons
 *  for stepping through the possible values.
 *  The Up Arrow and Down Arrow keys also cycle through the values.
 *
 *  <p>The NumericStepper control has the following default characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>Wide enough to display the maximum number of digits used by the control</td>
 *        </tr>
 *        <tr>
 *           <td>Minimum size</td>
 *           <td>Based on the size of the text.</td>
 *        </tr>
 *        <tr>
 *           <td>Maximum size</td>
 *           <td>Undefined</td>
 *        </tr>
 *     </table>
 *
 *  @mxml
 *
 *  The <code>&lt;mx:NumericStepper&gt;</code> tag inherits all of the tag
 *  attributes of its superclass, and adds the following tag attributes:
 *
 *  <pre>
 *  &lt;mx:NumericStepper
 *    <strong>Properties</strong>
 *    imeMode="null"
 *    maxChars="10"
 *    maximum="10"
 *    minimum="0"
 *    stepSize="1"
 *    value="0"
 *  
 *    <strong>Styles</strong>
 *    backgroundAlpha="1.0"
 *    backgroundColor="undefined"
 *    backgroundImage="undefined"
 *    backgroundSize="auto"
 *    borderColor="0xAAB3B3"
 *    borderSides="left top right bottom"
 *    borderSkin="HaloBorder"
 *    borderStyle="inset"
 *    borderThickness="1"
 *    color="0x0B333C"
 *    cornerRadius="0"
 *    disabledColor="0xAAB3B3"
 *    disabledIconColor="0x999999"
 *    downArrowDisabledSkin="NumericStepperDownSkin"
 *    downArrowDownSkin="NumericStepperDownSkin"
 *    downArrowOverSkin="NumericStepperDownSkin"
 *    downArrowUpSkin="NumericStepperDownSkin"
 *    dropShadowEnabled="false"
 *    dropShadowColor="0x000000"
 *    focusAlpha="0.5"
 *    focusRoundedCorners="tl tr bl br"
 *    fontAntiAliasType="advanced"
 *    fontFamily="Verdana"
 *    fontGridFitType="pixel"
 *    fontSharpness="0"
 *    fontSize="10"
 *    fontStyle="normal|italic"
 *    fontThickness="0"
 *    fontWeight="normal|bold"
 *    highlightAlphas="[0.3,0.0]"
 *    iconColor="0x111111"
 *    leading="2"
 *    paddingLeft="0"
 *    paddingRight="0"
 *    shadowDirection="center"
 *    shadowDistance="2"
 *    textAlign="left|center|right"
 *    textDecoration="none|underline"
 *    textIndent="0"
 *    upArrowDisabledSkin="NumericStepperUpSkin"
 *    upArrowDownSkin="NumericStepperUpSkin"
 *    upArrowOverSkin="NumericStepperUpSkin"
 *    upArrowUpSkin="NumericStepperUpSkin"
 *  
 *    <strong>Events</strong>
 *    change="<i>No default</i>"
 *    dataChange="<i>No default</i>"
 *  /&gt;
 *  </pre>
 *
 *  @includeExample examples/NumericStepperExample.mxml
 *
 */
public class NumericStepper extends UIComponent
                            implements IDataRenderer, IDropInListItemRenderer,
                            IFocusManagerComponent, IIMESupport,
                            IListItemRenderer
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
    public function NumericStepper()
    {
        super();
        tabChildren = true;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    mx_internal var inputField:TextInput;

    /**
     *  @private
     */
    mx_internal var nextButton:Button;

    /**
     *  @private
     */
    mx_internal var prevButton:Button;

    /**
     *  @private
     *  Flag that will block default data/listData behavior
     */
    private var valueSet:Boolean;

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
     *  The baselinePosition of a NumericStepper is calculated
     *  for its inputField.
     */
    override public function get baselinePosition():Number
    {
        if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
            return inputField ? inputField.baselinePosition : NaN;

        if (!validateBaselinePosition())
            return NaN;

        return inputField.y + inputField.baselinePosition;
    }

    //----------------------------------
    //  enabled
    //----------------------------------

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
        enabledChanged = true;

        invalidateProperties();
    }

    /**
     *  @private
     */
    override public function get enabled():Boolean
    {
        return super.enabled;
    }

    //----------------------------------
    //  tabIndex
    //----------------------------------

    /**
     *  @private
     *  Storage for the tabIndex property.
     */
    private var _tabIndex:int = -1;

    /**
     *  @private
     */
    private var tabIndexChanged:Boolean = false;

    /**
     *  @private
     *  Tab order in which the control receives the focus when navigating
     *  with the Tab key.
     *
     *  @default -1
     *  @tiptext tabIndex of the component
     *  @helpid 3198
     */
    override public function get tabIndex():int
    {
        return _tabIndex;
    }

    /**
     *  @private
     */
    override public function set tabIndex(value:int):void
    {
        if (value == _tabIndex)
            return;

        _tabIndex = value;
        tabIndexChanged = true;

        invalidateProperties();
    }


    //--------------------------------------------------------------------------
    //
    //  Properties
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
     *  The <code>data</code> property lets you pass a value to the component
     *  when you use it in an item renderer or item editor.
     *  You typically use data binding to bind a field of the <code>data</code>
     *  property to a property of this component.
     *
     *  <p>When you use the control as a drop-in item renderer or drop-in
     *  item editor, Flex automatically writes the current value of the item
     *  to the <code>value</code> property of this control.</p>
     *
     *  @default null
     *  @see mx.core.IDataRenderer
     */
    public function get data():Object
    {
        if (!_listData)
            data = this.value;

        return _data;
    }

    /**
     *  @private
     */
    public function set data(value:Object):void
    {
        _data = value;

        if (!valueSet)
        {
            this.value = _listData ? parseFloat(_listData.label) : Number(_data);
            valueSet = false;
        }

        dispatchEvent(new FlexEvent(FlexEvent.DATA_CHANGE));
    }

    //----------------------------------
    //  downArrowStyleFilters
    //----------------------------------

    /**
     *  Set of styles to pass from the NumericStepper to the down arrow.
     *  @see mx.styles.StyleProxy
     */
    protected function get downArrowStyleFilters():Object
    {
        return _downArrowStyleFilters;
    }
    
    private static var _downArrowStyleFilters:Object = 
    {    
        "cornerRadius" : "cornerRadius",        
        "highlightAlphas" : "highlightAlphas",
        "downArrowUpSkin" : "downArrowUpSkin",
        "downArrowOverSkin" : "downArrowOverSkin",
        "downArrowDownSkin" : "downArrowDownSkin",
        "downArrowDisabledSkin" : "downArrowDisabledSkin",
        "downArrowSkin" : "downArrowSkin",
        "repeatDelay" : "repeatDelay",
        "repeatInterval" : "repeatInterval"
    };

    //----------------------------------
    //  imeMode
    //----------------------------------

    /**
     *  @private
     */
    private var _imeMode:String = null;

    [Inspectable(defaultValue="")]

    /**
     *  Specifies the IME (Input Method Editor) mode.
     *  The IME enables users to enter text in Chinese, Japanese, and Korean.
     *  Flex sets the specified IME mode when the control gets the focus
     *  and sets it back to previous value when the control loses the focus.
     *
     * <p>The flash.system.IMEConversionMode class defines constants for the
     *  valid values for this property.
     *  You can also specify <code>null</code> to specify no IME.</p>
     *
     *  @see flash.system.IMEConversionMode
     *
     *  @default null
     */
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

        if (inputField)
            inputField.imeMode = _imeMode;
    }

    //----------------------------------
    //  inputFieldStyleFilters
    //----------------------------------

    /**
     *  Set of styles to pass from the NumericStepper to the input field.
     *  @see mx.styles.StyleProxy
     */
    protected function get inputFieldStyleFilters():Object
    {
        return _inputFieldStyleFilters;
    }
    
    private static var _inputFieldStyleFilters:Object = 
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
        "paddingLeft" : "paddingLeft", 
        "paddingRight" : "paddingRight",
        "shadowDirection" : "shadowDirection",
        "shadowDistance" : "shadowDistance",
        "textDecoration" : "textDecoration"
    }; 

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
     *  When a component is used as a drop-in item renderer or drop-in
     *  item editor, Flex initializes the <code>listData</code> property
     *  of the component with the appropriate data from the List control.
     *  The component can then use the <code>listData</code> property
     *  to initialize the <code>data</code> property of the drop-in
     *  item renderer or drop-in item editor.
     *
     *  <p>You do not set this property in MXML or ActionScript;
     *  Flex sets it when the component is used as a drop-in item renderer
     *  or drop-in item editor.</p>
     *
     *  @default null
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
    //  maxChars
    //----------------------------------

    /**
     *  @private
     *  Storage for the maxChars property.
     */
    private var _maxChars:int = 0;

    /**
     *  @private
     */
    private var maxCharsChanged:Boolean = false;

    [Bindable("maxCharsChanged")]

    /**
     *  The maximum number of characters that can be entered in the field.
     *  A value of 0 means that any number of characters can be entered.
     *
     *  @default 0
     */
    public function get maxChars():int
    {
        return _maxChars;
    }

    public function set maxChars(value:int):void
    {
        if (value == _maxChars)
            return;
            
        _maxChars = value;
        maxCharsChanged = true;
        
        invalidateProperties();

        dispatchEvent(new Event("maxCharsChanged"));
    }

    //----------------------------------
    //  maximum
    //----------------------------------

    /**
     *  @private
     *  Storage for maximum property.
     */
    private var _maximum:Number = 10;

    [Bindable("maximumChanged")]
    [Inspectable(category="General", defaultValue="10")]

    /**
     *  Maximum value of the NumericStepper.
     *  The maximum can be any number, including a fractional value.
     *
     *  @default 10
     */
    public function get maximum():Number
    {
        return _maximum;
    }

    public function set maximum(value:Number):void
    {
        _maximum = value;
        
        // To validate the value as min/max/stepsize has changed.
        if (!valueChanged)
        {
            this.value = this.value;
            valueSet = false;
        }
        
        dispatchEvent(new Event("maximumChanged"));
    }

    //----------------------------------
    //  minimum
    //----------------------------------

    /**
     *  @private
     *  Storage for minimum property.
     */
    private var _minimum:Number = 0;

    [Bindable("minimumChanged")]
    [Inspectable(category="General", defaultValue="0")]

    /**
     *  Minimum value of the NumericStepper.
     *  The minimum can be any number, including a fractional value.
     *
     *  @default 0
     */
    public function get minimum():Number
    {
        return _minimum;
    }

    public function set minimum(value:Number):void
    {
        _minimum = value;
        
        // To validate the value as min/max/stepsize has changed.
        if (!valueChanged)
        {
            this.value = this.value;
            valueSet = false;
        }
        
        dispatchEvent(new Event("minimumChanged"));
    }

    //----------------------------------
    //  nextValue
    //----------------------------------

    /**
     *  @private
     *  Storage for the nextValue property.
     */
    private var _nextValue:Number = 0;

    /**
     *  The value that is one step larger than the current <code>value</code>
     *  property and not greater than the <code>maximum</code> property value.
     */
    public function get nextValue():Number
    {
        if (checkRange(value + stepSize))
            _nextValue = value + stepSize;

        return _nextValue;
    }

    //----------------------------------
    //  previousValue
    //----------------------------------

    /**
     *  @private
     *  Storage for the previousValue property.
     */
    private var _previousValue:Number = 0;

    /**
     *  The value that is one step smaller than the current <code>value</code>
     *  property and not smaller than the <code>maximum</code> property value.
     */
    public function get previousValue():Number
    {
        if (checkRange(_value - stepSize))
            _previousValue = value - stepSize;

        return _previousValue;
    }

    //----------------------------------
    //  stepSize
    //----------------------------------

    /**
     *  @private
     *  Storage for the stepSize property.
     */
    private var _stepSize:Number = 1;

    [Bindable("stepSizeChanged")]
    [Inspectable(category="General", defaultValue="1")]

    /**
     *  Non-zero unit of change between values.
     *  The <code>value</code> property must be a multiple of this number.
     *
     *  @default 1
     */
    public function get stepSize():Number
    {
        return _stepSize;
    }

    /**
     *  @private
     */
    public function set stepSize(value:Number):void
    {
        _stepSize = value;
        
        // To validate the value as min/max/stepsize has changed.
        if (!valueChanged)
        {
            this.value = this.value;
            valueSet = false;
        }
        
        dispatchEvent(new Event("stepSizeChanged"));
    }

    //----------------------------------
    //  upArrowStyleFilters
    //----------------------------------

    /**
     *  Set of styles to pass from the NumericStepper to the up arrow.
     *  @see mx.styles.StyleProxy
     */
    protected function get upArrowStyleFilters():Object 
    {
        return _upArrowStyleFilters;
    }
    
    private static var _upArrowStyleFilters:Object = 
    {
        "cornerRadius" : "cornerRadius",        
        "highlightAlphas" : "highlightAlphas",
        "upArrowUpSkin" : "upArrowUpSkin",
        "upArrowOverSkin" : "upArrowOverSkin",
        "upArrowDownSkin" : "upArrowDownSkin",
        "upArrowDisabledSkin" : "upArrowDisabledSkin",
        "upArrowSkin" : "upArrowSkin",
        "repeatDelay" : "repeatDelay",
        "repeatInterval" : "repeatInterval"
    };

    //----------------------------------
    //  value
    //----------------------------------

    /**
     *  @private
     *  Storage for the value property.
     */
    private var _value:Number = 0;

    /**
     *  @private
     *  last value we send CHANGE for.
     *  _value will hold uncommitted values as well
     */
    private var lastValue:Number = 0;

    /**
     *  @private
     *  Holds the value of the value property
     *  until it is committed in commitProperties().
     */
    private var proposedValue:Number = 0;

    /**
     *  @private
     *  Keeps track of whether we need to update
     *  the value in commitProperties().
     */
    private var valueChanged:Boolean = false;

    [Bindable("change")]
    [Bindable("valueCommit")]
    [Inspectable(category="General", defaultValue="0")]

    /**
     *  Current value displayed in the text area of the NumericStepper control.
     *  If a user enters number that is not a multiple of the
     *  <code>stepSize</code> property or is not in the range
     *  between the <code>maximum</code> and <code>minimum</code> properties,
     *  this property is set to the closest valid value.
     *
     *  @default 0
     */
    public function get value():Number
    {
        return valueChanged ? proposedValue : _value;
    }

    /**
     *  @private
     */
    public function set value(value:Number):void
    {
        valueSet = true;

        proposedValue = value;
        valueChanged = true;

        invalidateProperties();
        invalidateSize();
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function createChildren():void
    {
        super.createChildren();

        if (!inputField)
        {
            inputField = new TextInput();

            inputField.styleName = new StyleProxy(this, inputFieldStyleFilters);
            inputField.focusEnabled = false;

            // restrict to numbers - dashes - commas - decimals
            inputField.restrict = "0-9\\-\\.\\,";

            inputField.maxChars = _maxChars;
            inputField.text = String(_value);
            inputField.parentDrawsFocus = true;
            inputField.imeMode = _imeMode;

            inputField.addEventListener(FocusEvent.FOCUS_IN, inputField_focusInHandler);
            inputField.addEventListener(FocusEvent.FOCUS_OUT, inputField_focusOutHandler);
            inputField.addEventListener(KeyboardEvent.KEY_DOWN, inputField_keyDownHandler);
            inputField.addEventListener(Event.CHANGE, inputField_changeHandler);

            addChild(inputField);
        }

        if (!nextButton)
        {       
            nextButton = new Button();
            nextButton.styleName = new StyleProxy(this, upArrowStyleFilters);
            nextButton.upSkinName = "upArrowUpSkin";
            nextButton.overSkinName = "upArrowOverSkin";
            nextButton.downSkinName = "upArrowDownSkin";
            nextButton.disabledSkinName = "upArrowDisabledSkin";
            nextButton.skinName = "upArrowSkin";
            nextButton.upIconName = "";
            nextButton.overIconName = "";
            nextButton.downIconName = "";
            nextButton.disabledIconName = "";

            nextButton.focusEnabled = false;
            nextButton.autoRepeat = true;

            nextButton.addEventListener(MouseEvent.CLICK, buttonClickHandler);
            nextButton.addEventListener(FlexEvent.BUTTON_DOWN, buttonDownHandler);

            addChild(nextButton);
        }

        if (!prevButton)
        {
            prevButton = new Button();
            prevButton.styleName = new StyleProxy(this, downArrowStyleFilters);
            prevButton.upSkinName = "downArrowUpSkin";
            prevButton.overSkinName = "downArrowOverSkin";
            prevButton.downSkinName = "downArrowDownSkin";
            prevButton.disabledSkinName = "downArrowDisabledSkin";
            prevButton.skinName = "downArrowSkin";
            prevButton.upIconName = "";
            prevButton.overIconName = "";
            prevButton.downIconName = "";
            prevButton.disabledIconName = "";

            prevButton.focusEnabled = false;
            prevButton.autoRepeat = true;

            prevButton.addEventListener(MouseEvent.CLICK, buttonClickHandler);
            prevButton.addEventListener(FlexEvent.BUTTON_DOWN, buttonDownHandler);

            addChild(prevButton);
        }
    }

    /**
     *  @private
     */
    override protected function commitProperties():void
    {
        super.commitProperties();
        
        if (maxCharsChanged)
        {
            maxCharsChanged = false;
            inputField.maxChars = _maxChars;
        }
        
        if (valueChanged)
        {
            valueChanged = false;

            setValue(isNaN(proposedValue) ? 0 : proposedValue, false);
        }

        if (enabledChanged)
        {
            enabledChanged = false;

            prevButton.enabled = enabled;
            nextButton.enabled = enabled;
            inputField.enabled = enabled;
        }

        if (tabIndexChanged)
        {
            inputField.tabIndex = _tabIndex;

            tabIndexChanged = false;
        }

    }

    /**
     *  @private
     *  Return the preferred sizes of the stepper.
     */
    override protected function measure():void
    {
        super.measure();

        var widestNumber:Number = minimum.toString().length >
                                  maximum.toString().length ?
                                  minimum :
                                  maximum;
        widestNumber += stepSize;

        var lineMetrics:TextLineMetrics = measureText(widestNumber.toString());
        
        var textHeight:Number = inputField.getExplicitOrMeasuredHeight();
        var buttonHeight:Number = prevButton.getExplicitOrMeasuredHeight() +
                                  nextButton.getExplicitOrMeasuredHeight();

        var h:Number = Math.max(textHeight, buttonHeight);
        h = Math.max(DEFAULT_MEASURED_MIN_HEIGHT, h);

        var textWidth:Number = lineMetrics.width + UITextField.TEXT_WIDTH_PADDING;
        var buttonWidth:Number = Math.max(prevButton.getExplicitOrMeasuredWidth(),
                                          nextButton.getExplicitOrMeasuredWidth());

        var w:Number = textWidth + buttonWidth + 20;
        w = Math.max(DEFAULT_MEASURED_MIN_WIDTH, w);

        measuredMinWidth = DEFAULT_MEASURED_MIN_WIDTH;
        measuredMinHeight = DEFAULT_MEASURED_MIN_HEIGHT;

        measuredWidth = w;
        measuredHeight = h;
    }

    /**
     *  @private
     *  Place the buttons to the right of the text field.
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        var w:Number = nextButton.getExplicitOrMeasuredWidth();
        var h:Number = Math.round(unscaledHeight / 2);
        var h2:Number = unscaledHeight - h;

        nextButton.x = unscaledWidth - w;
        nextButton.y = 0;
        nextButton.setActualSize(w, h2);
        
        prevButton.x = unscaledWidth - w;
        prevButton.y = unscaledHeight - h;
        prevButton.setActualSize(w, h);

        inputField.setActualSize(unscaledWidth - w, unscaledHeight);
    }

    /**
     *  @private
     *  Update the text field. 
     */
    override public function setFocus():void
    {
        if (stage)
            stage.focus = TextField(inputField.getTextField());
    }

    /**
     *  @private
     */
    override protected function isOurFocus(target:DisplayObject):Boolean
    {
        return target == inputField || super.isOurFocus(target);
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Verify that the value is within range.
     */
    private function checkRange(v:Number):Boolean
    {
        return v >= minimum && v <= maximum;
    }

    /**
     *  @private
     */
    private function checkValidValue(value:Number):Number
    {
        if (isNaN(value))
            return this.value;

        var closest:Number = stepSize * Math.round(value / stepSize);

        // The following logic has been implemented to fix bug 135045.
        // It assumes that the above line of code which rounds off the
        // value is not removed ! (That is, the precision of the value is
        // never expected to be greater than the step size.
        // ex : value = 1.11111 stepSize = 0.01)

        // Use precision of the step to round of the value.
        // When the stepSize is very small the system tends to put it in
        // exponential format.(ex : 1E-7) The following string split logic
        // cannot work with exponential notation. Hence we add 1 to the stepSize
        // to make it get represented in the decimal format.
        // We are only interested in the number of digits towards the right
        // of the decimal place so it doesnot affect anything else.
        var parts:Array = (new String(1 + stepSize)).split(".");

        // we need to do the round of (to remove the floating point error)
        // if the stepSize had a fractional value
        if (parts.length == 2)
        {
            var scale:Number = Math.pow(10, parts[1].length);
            closest = Math.round(closest * scale) / scale;
        }

        if (closest > maximum)
            return maximum;
        else if (closest < minimum)
            return minimum;
        else
            return closest;
    }

    /**
     *  @private
     */
    private function setValue(value:Number,
                              sendEvent:Boolean = true,
                              trigger:Event = null):void
    {

        var v:Number = checkValidValue(value);
        if (v == lastValue)
            return;

        lastValue = _value = v;
        inputField.text = v.toString();

        if (sendEvent)
        {
            var event:NumericStepperEvent =
                new NumericStepperEvent(NumericStepperEvent.CHANGE);
            event.value = _value;
            event.triggerEvent = trigger;

            dispatchEvent(event);
        }

        dispatchEvent(new FlexEvent(FlexEvent.VALUE_COMMIT));
    }

    /**
     *  @private
     *  Checks the value in the text field. If it is valid value
     *  and different from the current value, it is taken as new value.
     */
    private function takeValueFromTextField(trigger:Event = null):void
    {
        var inputValue:Number = Number(inputField.text);
        if ((inputValue != lastValue &&
            (Math.abs(inputValue - lastValue) >= 0.000001 || isNaN(inputValue))) || 
            inputField.text == "")
        {
            var newValue:Number = checkValidValue(Number(inputField.text));
            inputField.text = newValue.toString();
            setValue(newValue, trigger != null, trigger);
        }
    }

    /**
     *  @private
     *  Increase/decrease the current value.
     */
    private function buttonPress(button:Button, trigger:Event = null):void
    {
        if (enabled)
        {
            // we may get a buttonPress message before focusOut event for
            // the text field. Hence we need to check the value in
            // inputField.
            takeValueFromTextField();

            var oldValue:Number = lastValue;
            setValue(button == nextButton ?
                     lastValue + stepSize :
                     lastValue - stepSize, true, trigger);

            if (oldValue != lastValue)
                inputField.getTextField().setSelection(0,0);
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden event handlers: UIComponent
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Remove the focus from the text field.
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
     *  Remove the focus from the text field.
     */
    override protected function focusOutHandler(event:FocusEvent):void
    {
        var fm:IFocusManager = focusManager;
        if (fm)
            fm.defaultButtonEnabled = true;

        super.focusOutHandler(event);

        takeValueFromTextField(event);
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function buttonDownHandler(event:FlexEvent):void
    {
        buttonPress(Button(event.currentTarget), event);
    }

    /**
     *  @private
     */
    private function buttonClickHandler(event:MouseEvent):void
    {
        inputField.setFocus();
        inputField.getTextField().setSelection(0, 0);
    }

    /**
     *  @private
     */
    private function inputField_focusInHandler(event:FocusEvent):void
    {
        focusInHandler(event);
        
        // Send out a new FocusEvent because the TextInput eats the event
        // Make sure that it does not bubble.
        dispatchEvent(new FocusEvent(event.type, false, false,
                                     event.relatedObject,
                                     event.shiftKey, event.keyCode));
    }

    /**
     *  @private
     */
    private function inputField_focusOutHandler(event:FocusEvent):void
    {
        focusOutHandler(event);
        
        // Send out a new FocusEvent because the TextInput eats the event
        // Make sure that it does not bubble
        dispatchEvent(new FocusEvent(event.type, false, false,
                                     event.relatedObject,
                                     event.shiftKey,event.keyCode));
    }

    /**
     *  @private
     */
    private function inputField_keyDownHandler(event:KeyboardEvent):void
    {
        var tmpV:Number;

        switch (event.keyCode)
        {
            case Keyboard.DOWN:
            {
                tmpV = value - stepSize;
                setValue(tmpV, true);
                break;
            }

            case Keyboard.UP:
            {
                tmpV = stepSize + value;
                setValue(tmpV, true);
                break;
            }

            case Keyboard.HOME:
            {
                inputField.text = minimum.toString();
                setValue(minimum, true);
                break;
            }

            case Keyboard.END:
            {
                inputField.text = maximum.toString();
                setValue(maximum, true);
                break;
            }

            case Keyboard.ENTER:
            case Keyboard.TAB:
            {
                var inputValue:Number = Number(inputField.text);
                if (inputValue != lastValue &&
                    (Math.abs(inputValue - lastValue) >= 0.000001 ||
                     isNaN(inputValue)))
                {
                    var newValue:Number = checkValidValue(Number(inputField.text));
                    inputField.text = newValue.toString();
                    setValue(newValue, true);
                }

                // Prevent the defaultButton from firing
                event.stopImmediatePropagation();
                break;
            }
        }

        // Act as a proxy because the TextInput stops propogation
        dispatchEvent(event);
    }

    /**
     *  @private
     */
    private function inputField_changeHandler(event:Event):void
    {
        // Stop the event from bubbling up.
        event.stopImmediatePropagation();

        var inputValue:Number = Number(inputField.text);
        if ((inputValue != value &&
            (Math.abs(inputValue - value) >= 0.000001 || isNaN(inputValue))) || 
            inputField.text == "")
        {
            _value = checkValidValue(inputValue);
        }
    }

}

}
